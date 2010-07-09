import sys
import os
import re

from django.template import Context, loader
from django.conf import settings
from models import DomainInfo, IndexInfo, FieldInfo, CandidateFeatureInfo



INCLUDE_PATTERNS = re.compile(".*(js|html|css|yui)$")

gOptions = {}
gContext = None

def initDB():
    settings.DATABASE_NAME = "lbjsearch"
    
def main(argv):
    global gOptions
    if (len(argv) < 1 ):
        print "Usage : generateWeb.py -compile [domainId] [output folder]"
        print "Usage : generateWeb.py -domain"
        sys.exit(1)
    
    initDB()
    
    option = argv[0]
    
    if option == "-compile" :        
        gOptions["domainId"] = argv[1]
        gOptions["outputPath"] = argv[2]
        compileBuilder()
    elif option == "-domain":
        listDomains()

def compileBuilder():
    outputFolder = gOptions["outputPath"]    
    print " --- Generating lbjse code into '%s'" % outputFolder
    print " --- Django template folders : %s" % settings.TEMPLATE_DIRS
    
    getContext()
    print "Got context : ", gContext
    
    if (len(settings.TEMPLATE_DIRS) != 1 ):
        print "Template folder is not identified"
        sys.exit(1)
    else:
        generateBuilderFromTemplates(settings.TEMPLATE_DIRS[0] , outputFolder,"lbjse")    

def generateBuilderFromTemplates(baseTemplateDir,outputFolder,templateDir):
    templatePath = baseTemplateDir + os.sep + templateDir
    dirList=os.listdir(templatePath)
    for fname in dirList:
        if not INCLUDE_PATTERNS.match(fname):
            print "Skip ", fname
            continue        
        fullPath = templatePath + os.sep + fname
        templateRelPath = templateDir + os.sep + fname
        if os.path.isdir(fullPath):
            newOutputFolder = outputFolder + os.sep + fname            
            if not os.path.exists(newOutputFolder):
                os.mkdir(newOutputFolder)
            else:
                print "%s already existed" % fname
            generateBuilderFromTemplates(baseTemplateDir,newOutputFolder, templateRelPath )
        else:
            context = getContext()
            outputFile = outputFolder + os.sep + fname
            file = open(outputFile, "w");
            file.write(loader.get_template(templateRelPath).render(context))
            file.close()
            print "File %s is created " % outputFile
        
def getContext():
    global gContext
    if gContext is None:        
        gContext = Context()
        domain = DomainInfo.objects.get(pk=gOptions["domainId"])
        if domain is None:
            raise "No domain found %s" % str(gOptions)
        
        featureList = {}
        for candidate in CandidateFeatureInfo.objects.all():
            if not featureList.has_key(candidate.datatype):
                featureList[candidate.datatype] = []
            featureList[candidate.datatype].append(candidate.feature)
            
        gContext.update({"domain": domain})
        fields = FieldInfo.objects.filter(domainid = gOptions["domainId"])
        fieldNames = []
        fieldIds = []
        fieldTypes = []
        fieldInfos = []
        
        for field in fields:
            name = field.name
            id = field.fieldid
            type = field.type
            fieldNames.append(name)
            fieldIds.append(id)
            fieldTypes.append(type)
            if name != "other":
                if not featureList.has_key(type):
                    raise "Field %s(id=%d) has invalid type" % (name, id)
                fieldInfos.append({"name" : name, "id" : id, "type" : type, 'featureList' : featureList[type]})
        gContext.update({"field":
                            {"ids" : fieldIds,
                             "names" : fieldNames,
                             "types" : fieldTypes,
                             "fieldInfos" : fieldInfos
                             }
                         })
        gContext.update({"otherFeatures" : featureList["other"]})
        gContext.update({"annieWeb" : {
                                       "path" : "/annieWeb/%s" % domain.name
                                       } 
        } )
    return gContext

def listDomains():
    allDomains = DomainInfo.objects.all();
    for domain in allDomains:
        print "-- ", row
    print "Number of rows returned: %d" % len(allDomains)


if __name__ == "__main__":
	main(sys.argv)
