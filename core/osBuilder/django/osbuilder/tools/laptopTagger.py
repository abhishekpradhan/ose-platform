from models import *

def floatingPoint(x):
    return int(x * 10) / 10.0

def main(argv):
    
    if (len(argv) < 2):
        print "Usage : laptopTagger.py url_file indexId"
        return
    url_file = argv[0]
    indexId = int(argv[1])
    print "reading urls from ", url_file
    
    laptops = Laptop.objects.all()
    laptopMap = {}
    
    for laptop in laptops:
        if laptop.url is not None:
            laptopMap[laptop.url.strip()] = laptop
    print "done reading "
    LAPTOP_MODELS = "16g,1720,19,2230s,2510p,2530p,2710p,2730p,2g,30,4g,6510b,6515b,6530b,6535b,6710b,6715b,6720s,6730b,6730s,6735b,6820s,6830s,6910p,6930p,8510p,8510w,8530p,8530w,8710p,8710w,8730w,c7m,cf52,e8410,m1330,m50vm,n200,r400,r500,r61,r61e,r61i,sl300,sl400,sl500,t400,t4220,t500,t60,t61,t61p,t61u,t7,u810,w500,w7,w700,x200,x300,x301,x61,x61s,y510,y7".split(",")
    
    indexFile = open(url_file)
    count = 0;
    while True:
        line = indexFile.readline()
        if line == "": break    
        (id, url) = line.split()[:2]
        id = int(id)
        url = url.strip()
        if laptopMap.has_key(url):
            laptop = laptopMap[url]
            tags = {}
            if laptop.brand is not None:
                tags[31] = laptop.brand
            
            if laptop.model in LAPTOP_MODELS and laptop.family is not None:
                tags[32] = laptop.family + " " + laptop.model
            
            if laptop.montior is not None:
                tags[33] = floatingPoint(laptop.montior) 
    
            if laptop.harddrive is not None:
                tags[34] = floatingPoint(laptop.harddrive)
            
            if laptop.prospeed is not None:
                tags[35] = floatingPoint(laptop.prospeed)
            
            for (fieldId, value) in tags.items():
                for term in str(value).split():   
                    dt = DocTag(indexid=indexId, docid = id, fieldid = fieldId, value=term)
                    dt.save()
                
            count += 1
            if count % 100 == 0:
                #break
                print "progress...", count