"""
Read urls from a txt file , download them and write to a TREC file 
"""

import os
import re
import sys
import  urllib2, urllib
import socket
import traceback

socket.setdefaulttimeout(5)



class GoogleURLopener(urllib.FancyURLopener):
    version = "Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.9.0.10) Gecko/2009042316 Firefox/3.0.10"


def appendWebpageToTrecFile(url, content, fileName):
    file = open(fileName, "a")
    file.write("<OS_DOC>\n");
    file.write("<OS_DOC_URL>\n");
    file.write("%s\n" % url);
    file.write("</OS_DOC_URL>\n");
    file.write("<OS_DOC_CONTENT>\n");
    for line in content.split(os.linesep):
        if line != "</OS_DOC_CONTENT>": #make sure not contain our delimiter
            file.write(line)
            file.write(os.linesep)
    file.write("</OS_DOC_CONTENT>\n");
    file.write("</OS_DOC>\n");
    file.close()

def main(argv):
    if len(argv) < 3 :
        print "Usage : pyListCrawler.py urls_file trec_output_file [-append]"
        sys.exit(1)

    urllib2._urlopener = GoogleURLopener()

    urlListFile = argv[1]
    trecOutputFile = argv[2]

    fileMode = "w"
    for arg in argv:
        if arg == "-append":
            print "...Appending mode"
            fileMode = "a"

    print "Input : ", urlListFile
    print "Output : " , trecOutputFile

    open(trecOutputFile,fileMode).close()
    file = open(urlListFile)

    count = 0
    while True:
        url = file.readline()
        if url == "": break
        url = url.strip()
        print "Downloading ", url, "..."
       
        try:
            page = urllib2.urlopen(url)
	    if page.info().get("Content-Type").startswith("text"):
            	content = page.read()
		appendWebpageToTrecFile(url, content, trecOutputFile)
	    else:
		print "Skip content-type", page.info().get("Content-Type")
            page.close()
        except :
            print "error !"
            traceback.print_exc(file=sys.stdout)
            continue
        
        count += 1
    print "%d webpages downloaded" % count    


if __name__=="__main__":
    main(sys.argv);
