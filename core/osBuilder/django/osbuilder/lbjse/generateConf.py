import sys
import os
import random

from django.template import Context, loader, TemplateDoesNotExist
from django.conf import settings
from models import DomainInfo, IndexInfo, FieldInfo, LbjseQuery, DocTag


gOptions = {}
gContext = None

def initDB():
    settings.DATABASE_NAME = "lbjsearch"
    
def main(argv):
    global gOptions
    if (len(argv) < 1 ):
        print "Usage : generateConf.py [domainId] [index] [output folder]"
        sys.exit(1)
    
    initDB();
    gOptions["domainId"] = int(argv[0])
    gOptions["indexId"] = int(argv[1])
    gOptions["outputPath"] = argv[2]
    compileBuilder()

def compileBuilder():
    outputFolder = gOptions["outputPath"]    
    print " --- Generating conf files into '%s'" % outputFolder
    print " --- Django template folders : %s" % settings.TEMPLATE_DIRS
    print " --- Using database : %s" % settings.DATABASE_NAME
    
    getContext()
        
    if (len(settings.TEMPLATE_DIRS) != 1 ):
        print "Template folder is not identified"
        sys.exit(1)
    else:
        generateConfigFromTemplates("lbj/learning_conf.xml" , outputFolder)    

def generateConfigFromTemplates(templateFile,outputFolder):          
    fullPath = settings.TEMPLATE_DIRS[0] + os.sep + templateFile
    domainId = gOptions["domainId"]
    indexId = gOptions["indexId"]
    trainTestRatio = 2
    for fieldInfo in FieldInfo.objects.filter(domainid=domainId):
        #generate training config        
        context = getContext()
        queryIds = getTrainingQueryForField(fieldInfo.fieldid)
        context.update({"queryIds" : queryIds})
        if fieldInfo.name == "other":
            docIds = getAllTaggedDocsForDomain(indexId, domainId)
        else:
            docIds = getAllDocTaggedWithField(indexId, fieldInfo.fieldid)
        random.shuffle(docIds)
        
        numTrainDocs = int(1.0 * len(docIds) * trainTestRatio / (trainTestRatio + 1))
        context.update({"docIds" : docIds[:numTrainDocs]})
        outputFile = "%s/training_%s.xml" % (outputFolder, fieldInfo.name)
        createFileFromTemplate(outputFile, fullPath, context) 
        
        context.update({"docIds" : docIds[numTrainDocs:]})
        outputFile = "%s/testing_%s.xml" % (outputFolder, fieldInfo.name)
        createFileFromTemplate(outputFile, fullPath, context)

def createFileFromTemplate(outputFile, fullTemplatePath, context):
    file = open(outputFile, "w")
    file.write(loader.get_template(fullTemplatePath).render(context))
    file.close()
    print "File %s is created " % outputFile

def getTrainingQueryForField(fieldId):
    queryIds = {}
    for query in LbjseQuery.objects.filter(fieldid = fieldId):
        queryIds[query.queryid] = 1
    return list(queryIds.keys())

def getAllDocTaggedWithField(indexId, fieldId):
    docIds = {}
    for tag in DocTag.objects.filter(indexid = indexId, fieldid = fieldId):
        docIds[tag.docid] = 1
    return list(docIds.keys()) 
 
def getAllTaggedDocsForDomain(indexId, domainId):
    docIds = {}
    fieldIds = []

    for field in FieldInfo.objects.filter(domainid=domainId):
        fieldIds.append(field.fieldid)
        
    for tag in DocTag.objects.filter(indexid = indexId):
        if tag.field.id in fieldIds:
            docIds[tag.docid] = 1
            
    return list(docIds.keys())
   
def getContext():
    global gContext
    if gContext is None:
        gContext = Context()
        domain = DomainInfo.objects.get(pk=gOptions["domainId"])
        if domain is None:
            raise "No domain found %s" % str(gOptions)
        
        gContext.update({"domain": domain})
        
        index = IndexInfo.objects.get(pk=gOptions["indexId"])
        if index is None:
            raise "No index found %s" % str(gOptions)
        
        gContext.update({"index":index})
        
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
            fieldInfos.append({"name" : name, 
                               "id" : id, 
                               "type" : type 
                               })
                
        gContext.update({"field":
                            {"ids" : fieldIds,
                             "names" : fieldNames,
                             "types" : fieldTypes,
                             "fieldInfos" : fieldInfos
                             }
                         })
    return gContext

if __name__ == "__main__":
	main(sys.argv)
