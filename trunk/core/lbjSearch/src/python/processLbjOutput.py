import sys

def main(argv):
    outputFile = argv[1]
    debug = False
    if len(argv) >= 3:
        debug = True
    file = open(outputFile)
    print "First line"
    print file.readline(),
    M = float(file.readline().strip())
    print "Got M = ", M
    fVector = []
    while True:
        line = file.readline().strip()
        if debug:
            print " ----------" , line
        if line == "": 
            break
        tokens = line.strip().split(") ")
        try:
            while True: tokens.remove('')
        except:
            pass
        (feature,w1) = tokens[:2]
        w1 = float(w1)
        line = file.readline()
        if line == "": 
            print "w2 missing"
            break
        w2 = float(line.strip())
        fVector.append( ( (M * w1-w2)/M , feature) )

    fVector.sort()
    for (w,f) in fVector:
        print "%.2f\t%s)" % (w,f)
    file.close()

if __name__ == "__main__":
    main(sys.argv)
