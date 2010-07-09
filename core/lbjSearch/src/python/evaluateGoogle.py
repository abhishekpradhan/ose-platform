#!/usr/bin/python
#
# Copyright 2007 Google Inc.
#
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:

# The above copyright notice and this permission notice shall be included in
# all copies or substantial portions of the Software.

# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
# THE SOFTWARE.
#

import getopt
import os
import sys
import urllib
import urllib2
import xml.dom.minidom
import traceback

ID = "objectsearch-cs.uiuc.edu"

BASE_URL = 'http://research.google.com/university/search/service'

DEFAULT_SIZE = 'large'

DEFAULT_START = '0'

NAMESPACE = 'http://research.google.com/university/search'

USAGE = '''Usage: search-example.py [options] --id [project id] terms

  This script iterates over University Research Program for Google
  Search results and prints the response to stdout.

  Use of this service is governed by the terms made available at:

    http://research.google.com/university/search/terms.html

  Options:

    -i --id : the assigned project id [required]
    -s --start : the starting search index [optional, default 0]
    -z --size : the result size ('small' or 'large') [optional, default 'small']
    -h --help : print this help [optional]

  Example:

    search-example.py --id project-stanford.edu "google code"
'''


class Response(object):
  '''A wrapper around the XML of a search response'''

  def __init__(self, node):
    '''Construct a wrapper around an XML search response.

    Exposes the following properties:

      terms : the requested search terms
      size: the requested number of search results
      start: the requested start index (offset 0)
      first: the index (offset 1) of the first result in the response
      last: the index (offset 1) of the last result in the response
      total: the total number of search results
      results: a sequence of Result instances

    Args:
      node: An xml.dom.Node instance containing the search response
    '''
    # Parse the response for information about the request
    self.terms = GetText(node.getElementsByTagName('Q')[0])
    params = node.getElementsByTagName('PARAM')
    for param in params:
      name = param.getAttribute('name')
      if name == 'num':
        self.size = param.getAttribute('value')
      elif name == 'start':
        self.start = param.getAttribute('value')
    # Parse the response for metadata about the results
    res = node.getElementsByTagName('RES')[0]
    self.first = res.getAttribute('SN')
    self.last = res.getAttribute('EN')
    self.total = GetText(res.getElementsByTagName('M')[0])
    self.results = []
    # Parse the individual results
    [self.results.append(Result(r)) for r in res.getElementsByTagName('R')]

  def __str__(self):
    '''Return a representation of this instance as a unicode string'''
    s = 'terms: %s\n' % self.terms
    s += 'size: %s\n' % self.size
    s += 'start: %s\n' % self.start
    s += 'first: %s\n' % self.first
    s += 'last: %s\n' % self.last
    s += 'total: %s\n' % self.total
    s += 'results: \n'
    for result in self.results:
      s += unicode(result)
    return s

class Result(object):
  '''A wrapper around the XML of an individual result'''

  def __init__(self, node):
    '''Construct a wrapper around an XML search result.

    Exposes the following properties:

      index: the index of the result (offset 1)
      url: the address of the page matching the request
      encoded_url: the url-encoded address of the page matching the request
      title: the title of the page matching the request, includes <b> tags
      title_no_bold: the title of the page matching the request, no <b> tags

    Args:
      node: An xml.dom.Node instance containing a search result
    '''
    self.index = node.getAttribute('N')
    self.url = GetText(node.getElementsByTagName('U')[0])
    self.encoded_url = GetText(node.getElementsByTagName('UE')[0])
    self.title = GetText(node.getElementsByTagName('T')[0])
    self.title_no_bold = GetText(node.getElementsByTagName('TNB')[0])

  def __str__(self):
    '''Return a representation of this instance as a unicode string'''
    s = '  index: %s\n' % self.index
    s += '    url: %s\n' % self.url
    s += '    encoded_url: %s\n' % self.encoded_url
    s += '    title: %s\n' % self.title
    s += '    title_no_bold: %s\n' % self.title_no_bold
    return s


def GetText(node):
  '''Extract the contents of a xml.dom.Nodelist as a string.

  Args:
    nodelist: An xml.dom.Node instance
  Returns:
    a string containing the contents of all node.TEXT_NODE instances
  '''
  text = []
  for child in node.childNodes:
    if child.nodeType == xml.dom.Node.TEXT_NODE:
      text.append(child.data)
  return ''.join(text)


def PrintUsageAndExit(message=None):
  '''Print the usage message and exit the program.

  Args:
    message: An error message to print before the usage string.
  '''
  if message:
    print "Error: %s" % message
  print USAGE
  sys.exit(2)


def Search(id, size, start, terms):
  '''Perform a search and print the results to standard out.

  Args:
    id: the assigned service id
    size: the desired size of the search response ('small' or 'large')
    start: the index of the first search result
    terms: the terms to search for

  Returns:
    A Response instance representing the search results
  '''
  values = {'clid': id, 'rsz': size, 'start': start, 'q': terms}
  url = '?'.join([BASE_URL, urllib.urlencode(values)])
  request = urllib2.Request(url)
  print "url ", url
  response = urllib2.urlopen(request)
  document = xml.dom.minidom.parse(response)
  return Response(document)


def ParseArgs(args):
  '''Parse the command line for the required and optional arguments.
  '''
  try:
    shortflags = 'i:o:k:'
    longflags = ['qfile=', 'outputPrefix=', 'topK=' ]
    opts, args = getopt.gnu_getopt(args, shortflags, longflags)
  except getopt.GetoptError:
    PrintUsageAndExit(getopt.GetoptError.msg)
  for o, a in opts:
    if o in ("-i", "--gfile"):
      inputFile = a
    if o in ("-o", "--outputPrefix"):
      outputFile = a
    if o in ("-k", "--topK"):
      topK = int(a)
  return (inputFile, outputFile, topK)

def getSearchResult(numberOfResults, terms):
    results = []
    start = 0
    try:
        while len(results) < numberOfResults:
            response = Search(ID, DEFAULT_SIZE, start, terms)
            if int(response.first) != start + 1:
                print "First != start + 1", response.first, start
                break
            for result in response.results:
                results.append(result.url)
            start += len(response.results)
    except:
        print "Got exception, returning %d items" % len(results)
        traceback.print_exc(file=sys.stdout)
    return results[:numberOfResults]

def getResultAndWriteToUrlFile(terms, topK, outputFile):
    output = open(outputFile, "w")
    for url in getSearchResult(topK, terms):
        output.write("%s\n" % url)
    output.close()
    print "Finish ", outputFile
            
def main():
  (termTagFile, outputPrefix, topK) = ParseArgs(sys.argv[1:])
  input = open(termTagFile)
  while True:
      line = input.readline()
      print "query ", line
      if line == "":
         break
      if line.startswith("#"):
         print "Skip", line
         continue
      t = line.strip().split("\t")
      if len(t) < 2:
          break
      (qid, terms) = t[:2]
      outputFile = outputPrefix + "." + qid 
      getResultAndWriteToUrlFile(terms, topK, outputFile)
  input.close()
  print "Done!"

if __name__ == "__main__":
  main()
