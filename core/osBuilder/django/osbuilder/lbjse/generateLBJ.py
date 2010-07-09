import sys
import os
import re

from django.template import Context, loader, TemplateDoesNotExist
from django.conf import settings
from models import DomainInfo, IndexInfo, FieldInfo, CandidateFeatureInfo, FeatureInfo

INCLUDE_PATTERNS = re.compile(".*(lbj)$")

gOptions = {}
gContext = None

def initDB():
    settings.DATABASE_NAME = "lbjsearch"
    
def main(argv):
    global gOptions
    if (len(argv) < 1 ):
        print "Usage : generateLBJ.py -compile [domainId] [output folder]"
        sys.exit(1)
    
    initDB();
    option = argv[0]
    
    if option == "-compile" :        
        gOptions["domainId"] = argv[1]
        gOptions["outputPath"] = argv[2]
        compileBuilder()

def compileBuilder():
    outputFolder = gOptions["outputPath"]    
    print " --- Generating lbjse code into '%s'" % outputFolder
    print " --- Django template folders : %s" % settings.TEMPLATE_DIRS
    
    getContext()
        
    if (len(settings.TEMPLATE_DIRS) != 1 ):
        print "Template folder is not identified"
        sys.exit(1)
    else:
        generateBuilderFromTemplates("lbj" , outputFolder,"%s.lbj" % gContext["domain"].name)    

def generateBuilderFromTemplates(baseTemplateDir,outputFolder,templateFile):          
        templateRelPath = baseTemplateDir + os.sep + templateFile
        fullPath = settings.TEMPLATE_DIRS[0] + os.sep + templateRelPath
        context = getContext()
        outputFile = outputFolder + os.sep + templateFile
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
            
        gContext.update({"domain": domain })
#                            {"id":domain.id,
#                             "name":domain.name,
#                             "description":domain.description,
#                             }})
        fields = FieldInfo.objects.filter(domainid = gOptions["domainId"])
        fieldNames = []
        fieldIds = []
        fieldTypes = []
        fieldInfos = []
        allFeatures = []
        for field in fields:
            name = field.name
            id = field.fieldid
            type = field.type
            fieldNames.append(name)
            fieldIds.append(id)
            fieldTypes.append(type)
            
                
            fieldFeatures = []
            for featureInfo in FeatureInfo.objects.filter(fieldid=id, isdeleted=0):
               lbjFeature = getLBJFeatureFor(field, featureInfo.template)
               fieldFeatures.append(featureInfo.template)
               print "Feature " , featureInfo, lbjFeature
               
               allFeatures.append(lbjFeature)
               
            
            if not featureList.has_key(type):
                raise "Field %s(id=%d) has invalid type" % (name, id)
            print "-----" , len(fieldFeatures)
            fieldInfos.append({"name" : name, 
                               "id" : id, 
                               "type" : type, 
                               "featureList" : featureList[type],
                               "features" : fieldFeatures})
                
        gContext.update({"field":
                            {"ids" : fieldIds,
                             "names" : fieldNames,
                             "types" : fieldTypes,
                             "fieldInfos" : fieldInfos
                             }
                         })
        gContext.update({"otherFeatures" : featureList["other"]})
        gContext.update({"allLBJFeatures" : allFeatures})
    return gContext

def getLBJFeatureFor(fieldInfo, featureName):
    context = Context()
    context.update({"fieldInfo" : fieldInfo})
    templateRelPath = "lbj" + os.sep + "features" + os.sep + featureName + ".lbj"
    try:
        return loader.get_template(templateRelPath).render(context)
    except TemplateDoesNotExist:
        print "TemplateDoesNotExist ", templateRelPath
        return "[feature definition for (%s) missing]" % featureName
    
if __name__ == "__main__":
	main(sys.argv)
