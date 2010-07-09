import sys

def main(argv):
    """processHistogram.py histo_file #bins output_file"""
    inputFile = argv[1] 
    numBins = int(argv[2])
    outputFile = argv[3]
    
    file = open(inputFile)
    data = []
    
    totalCount = 0
    while True:
        line = file.readline()
        if line == "":
            break
        items = line.split()
        if len(items) < 2:
            break
        (item, count) = (items[0], float(items[1]))
        totalCount += count
        data.append((item, count))
    file.close()
    
    output = open(outputFile, "w") 
    binSize = int(totalCount/numBins)
    
    print "Total count ", totalCount
    print "Bin size ", binSize
    print "Number of items ", len(data)
    
    sum = 0
    start = None
    for (item, count) in data:
        if start is None:
            start = item
        sum += count
        #print item
        if sum >= binSize:
            output.write("_range(%s,%s) \t %f\n" % (start, item, sum))
            start = None
            sum = binSize - sum
                    
    if sum > 0:
        output.write("_range(%s,%s) \t %f\n"  % (start, data[-1][0], sum))
    output.close()

if __name__ == "__main__":
    main(sys.argv)
