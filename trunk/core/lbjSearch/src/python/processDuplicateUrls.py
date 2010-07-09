import sys

"""
process a sorted url file, and output two different urls but refer to the same document. 
"""
def main(argv):
    prevLine = "----"
    while True:
        line = sys.stdin.readline().strip()
        if line == "":
            break
        if line != prevLine and (line.startswith(prevLine) or prevLine.startswith(line)) \
            and abs(len(line) - len(prevLine)) == 1 :
            print line
        prevLine = line
        
if __name__ == "__main__":
    main(sys.argv)
