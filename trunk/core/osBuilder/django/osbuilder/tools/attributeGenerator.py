from models import *

def floatingPoint(x):
    return int(x * 10) / 10.0

def main(argv):
    INDEX_ID = 100
    DOMAIN_ID = 1
    OUTPUT_FILE = "C:\Users\KimCuong\Documents\PhD\Research\ObjectSearch\core_trunk\osBuilder\django\\attributes.txt"
    
    outputFile = open(OUTPUT_FILE, "w")
    
    for fieldInfo in FieldInfo.objects.filter(domainid=DOMAIN_ID):
        if fieldInfo.name == "other": continue
        outputFile.write("%d %s %s\n" % (fieldInfo.fieldid , fieldInfo.name,fieldInfo.type) )        
        docTags = DocTag.objects.filter(indexid=INDEX_ID ,fieldid=fieldInfo.fieldid)
        attrSet = {}
        for dt in docTags:
            t = attrSet.get(dt.value,0)
            if fieldInfo.type == "number":
                attrSet[float(dt.value.replace(",",""))] = t + 1
            else:
                attrSet[dt.value] = t + 1
        
        print "field ", fieldInfo.name, fieldInfo
        for (k,v) in attrSet.items():
            print "\t",k,"\t",v
        if fieldInfo.type == "number":
            print attrSet
            for v in attrSet.keys():
                attrSet[v + 0.05] = 1;
                attrSet[v - 0.05] = 1;
        
        l = attrSet.keys();
        l.sort();
        outputFile.write(",".join(map(str,l)))
        outputFile.write("\n")
    
    outputFile.close()
    print "Done"