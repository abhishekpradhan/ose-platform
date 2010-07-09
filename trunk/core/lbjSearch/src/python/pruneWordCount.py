import sys

def main(argv):
    inputFile = None
    pruneLevel = None
    for i in range(len(argv)):
        if argv[i] == "-input":
            inputFile = argv[i+1]
        elif argv[i] == "-level":
            pruneLevel = int(argv[i+1])
        
    if inputFile is None:
        print "Bad parameters"
        return
    
    file = open(inputFile)
    
    for line in file.readlines():
        if len(line.split()) < 2 : continue
        count, word = line.split()
        count = int(count)
        if count > pruneLevel:
            print word
        
    file.close() 
    
    
if __name__ == "__main__":
    main(sys.argv)