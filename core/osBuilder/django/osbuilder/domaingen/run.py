import sys
import os
import re
import codecs

from django.template import Context, loader
from django.conf import settings

INCLUDE_PATTERNS = re.compile(".*(js|html|css|yui)$")
SKIP_FOLDER_PATTERNS = re.compile(".*(svn)$")

gOptions = {}
gContext = None

def main(argv):
    global gOptions
    if (len(argv) < 1 ):
        print "Usage : run.py -compile [domainId] [output folder]"
        print "Usage : run.py -domain"
        sys.exit(1)
    
    option = argv[0]
    
    if option == "-compile" :        
        gOptions["domainId"] = argv[1]
        gOptions["outputPath"] = argv[2]
        compileBuilder()
    elif option == "-domain":
        listDomains()

def compileBuilder():
    outputFolder = gOptions["outputPath"]    
    print " --- Generating object builder into '%s'" % outputFolder
    print " --- Django template folders : %s" % settings.TEMPLATE_DIRS
    
    getContext()
        
    if (len(settings.TEMPLATE_DIRS) != 1 ):
        print "Template folder is not identified"
        sys.exit(1)
    else:
        generateBuilderFromTemplates(settings.TEMPLATE_DIRS[0] , outputFolder,"web")    

def generateBuilderFromTemplates(baseTemplateDir,outputFolder,templateDir):
    templatePath = baseTemplateDir + os.sep + templateDir
    dirList=os.listdir(templatePath)
    for fname in dirList:
        if SKIP_FOLDER_PATTERNS.match(fname):
            print "Skip ",fname
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
            if not INCLUDE_PATTERNS.match(fname):
                print "Skip ", fname
                continue 
            context = getContext()
            outputFile = outputFolder + os.sep + fname
            file = codecs.open(outputFile, encoding='utf-8', mode='w')
            file.write(loader.get_template(templateRelPath).render(context))
            file.close()
            print "File %s is created " % outputFile
        
def getContext():
    global gContext
    if gContext is None:
        gContext = Context()
        domain = getDomain(gOptions["domainId"])
        if domain is None:
            raise "No domain found %s" % str(gOptions)        
        gContext.update({"domain":
                            {"id":domain[0],
                             "name":domain[1],
                             "description":domain[2],
                             }})
        fields = getAllFieldsForDomainId(gOptions["domainId"])
        fieldNames = []
        fieldIds = []
        fieldTypes = []
        fieldInfos = []
        for field in fields:
            name = field[2]
            id = field[0]
            type = field[3]
            fieldNames.append(name)
            fieldIds.append(id)
            fieldTypes.append(type)
            if name != "other":
                fieldInfos.append({"name" : name, "id" : id, "type" : type})
        gContext.update({"field":
                            {"ids" : fieldIds,
                             "names" : fieldNames,
                             "types" : fieldTypes,
                             "fieldInfos" : fieldInfos
                             }
                         })
    return gContext

def getDomain(domainId):
    from django.db import connection
    cursor = connection.cursor()
    cursor.execute("SELECT * FROM DomainInfo where domainId = " + domainId)
    rows = cursor.fetchall()
    for row in rows:
        return row

def getAllFieldsForDomainId(domainId):
    from django.db import connection
    cursor = connection.cursor()
    cursor.execute("SELECT * FROM FieldInfo where domainId = " + domainId)
    rows = cursor.fetchall ()
    return rows
    
def listDomains():
    from django.db import connection
    cursor = connection.cursor()
    cursor.execute("SELECT * FROM DomainInfo ")
    rows = cursor.fetchall ()
    for row in rows:
        print "-- ", row
    print "Number of rows returned: %d" % cursor.rowcount


if __name__ == "__main__":
	main(sys.argv)
