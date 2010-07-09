from models import DocInfo, FieldInfo, DocTag
import sys

def floatingPoint(x):
    return int(x * 10) / 10.0

def main(argv):
    indexId = None
    domainId = None
    outputFile = None
    for i in range(len(argv)):
        if argv[i] == "-index":
            indexId = int(argv[i+1])
        elif argv[i] == "-domain":
            domainId = int(argv[i+1])
        elif argv[i] == "-output":
            outputFile = argv[i+1]
    if domainId is None :
        print "Usage : dbToText.py [index id] [domain id] [output file]"
        sys.exit(1)
    
    if indexId is None:
        exportDomainInfoToFile(domainId, outputFile)
    else:
        dbToText(indexId, domainId, outputFile)
    print "Done"
    
def dbToText(indexId, domainId, outputFile):
    fieldIds = []
    fieldIdNameMap = {}
    for fieldInfo in FieldInfo.objects.filter(domainid=domainId):
        fieldIds.append(fieldInfo.fieldid)
        fieldIdNameMap[fieldInfo.fieldid] = fieldInfo.name
    
    allTags = DocTag.objects.filter(indexid=indexId)
    count = 0
    docTagsMap = {}
    for tag in allTags:
        if tag.fieldid not in fieldIds:
            continue
        docId = tag.docid
        if not docTagsMap.has_key(docId):
            docTagsMap[docId] = []
        docTagsMap[docId].append(tag)
    
    output = open(outputFile, "wb")
    n = 0
    for docInfo in DocInfo.objects.filter(indexid=indexId):
        output.write("<DOC>\n")
        output.write("<DOCID>\n")
        output.write("%s\n" % docInfo.docid)
        output.write("</DOCID>\n")
        output.write("<URL>\n")
        output.write("%s\n" % docInfo.url)
        output.write("</URL>\n")
        output.write("<TITLE>\n")
        output.write("%s\n" % docInfo.title.encode("utf-8"))
        output.write("</TITLE>\n")
        output.write("<TEXT>\n")
        output.write(docInfo.bodytext.encode("utf-8"))
        output.write("\n")
        output.write("</TEXT>\n")
        output.write("<TAGS>\n")
        for tag in docTagsMap.get(docInfo.docid,[]):
            output.write("%s:%s\n" % (fieldIdNameMap[tag.fieldid],tag.value))
        output.write("</TAGS>\n")
        output.write("</DOC>\n")
        n += 1
    output.close()
    print "Done exporting ", n, " docs"


def exportDomainInfoToFile(domainId, outputFileName):
    output = open(outputFileName, "wb")
    output.write("%d\n" % domainId)
    for fieldInfo in FieldInfo.objects.filter(domainid=domainId):
        output.write("%d %s\n" % (fieldInfo.fieldid,fieldInfo.name) )
    output.close()
    