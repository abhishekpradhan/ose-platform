from models import *

def floatingPoint(x):
    return int(x * 10) / 10.0

def main(argv):
    
    if (len(argv) < 3):
        print "Usage : tagMigrator.py [index translation file] [old indexId] [new index id]"
        return
    
    translationFile = argv[0]    
    indexId = int(argv[1])
    newIndexId = int(argv[2])
    
    print "reading translation file ", translationFile
    transFile = open(translationFile)
    
    idTranslationMap = {}
    
    while True:
        line = transFile.readline()
        if line == "":
            break;
        try:
            (idFrom, idTo, url) = line.split()[:3]
        except ValueError:
            print "problematic line ", line
            continue            
        idTranslationMap[int(idFrom)] = int(idTo)
    
    transFile.close()
    print "done reading , got ", len(idTranslationMap)
    
    allTags = DocTag.objects.filter(indexid=indexId)
    count = 0
    for tag in allTags:
        if idTranslationMap.has_key(tag.docid):
            count += 1
            newTag = DocTag(indexid=newIndexId, docid = idTranslationMap[tag.docid], fieldid = tag.fieldid, value=tag.value)
            print "Converting ", tag, " into ", newTag
            newTag.save()
#        else:
#            print "unknown tag ", tag
    print "Done converting ", len(allTags)
    print "Tags Converted ", count