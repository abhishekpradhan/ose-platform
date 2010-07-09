from models import *

def floatingPoint(x):
    return int(x * 10) / 10.0

def main(argv):
    
    if (len(argv) < 2):
        print "Usage : laptopPrice.py url_file indexId"
        return
    
    url_file = argv[0]    
    indexId = int(argv[1])
    
    laptopPrices = LaptopPrice.objects.all()
    
    print "reading urls from ", url_file
    laptopMap = {}
    
    for laptop in laptopPrices:
        if laptop.url is not None and laptop.price is not None:
            laptopMap[laptop.url.strip()] = laptop.price
    
    indexFile = open(url_file)
    count = 0;
    while True:
        line = indexFile.readline()
        if line == "": break    
        (id, url) = line.split()[:2]
        id = int(id)
        url = url.strip()
        if laptopMap.has_key(url):
            price = laptopMap[url]
            
            dt = DocTag(indexid=indexId, docid = id, fieldid = 36, value=str(price))
            dt.save()
                
            count += 1
            if count % 100 == 0:
                #break
                print "haha", count