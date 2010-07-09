from models import DocInfo, FieldInfo, DocTag

def main(argv):
    
    if (len(argv) < 2):
        print "Usage : mergeTextTag.py [index id] [field id]"
        return
    
    indexId = int(argv[0])
    fieldId = int(argv[1])
    
    mergeTag(indexId, fieldId)
    
def mergeTag(indexId, fieldId):
    fieldIds = []
    fieldIdNameMap = {}
        
    allTags = DocTag.objects.filter(indexid=indexId, fieldid = fieldId)
    
    
    for tag in allTags:
        print tag
        
    print "Done "

