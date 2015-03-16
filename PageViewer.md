![http://warbler.cs.uiuc.edu:8080/trac/attachment/wiki/DataAnnotation/pageviewer.png](http://warbler.cs.uiuc.edu:8080/trac/attachment/wiki/DataAnnotation/pageviewer.png)

If you setup correctly, you should be able to open PageViewer tool at the address http://warbler.cs.uiuc.edu/annieWeb/professor/pageviewer.html. Note that this is under "professor" folder in AnnieWeb. If you want to annotate data for other domain (e.g : real estate), make sure you generate the code for that domain first.

PageViewer lets you go though a list of URLs in the left panel, view the content of a url in the bottom center frame, add/edit tags in the top center frame, it also shows tag suggestion on the right panel.


Note the following features:
  1. "Previous" & "Next" button on the top is faster than clicking on the result panel
  1. "Last Tags" saves time typing the last entered tag
  1. In the annotation frame,  clicking on the title of the url  will take you to the original page/url. Clicking on "Cache" will open whatever we store in our cache. (Note : the bottom center frame show the clean version of the cache (no javascript) ).

'''How to generate the result list?'''
The missing piece is how to generate the result list shown on the left? The quick answer is, first, you generate a list of url in a text file, then generate a html file from the url list. Put it in the same folder as pageviewer.html (e.g : annieWeb/professor/), then tell PageViewer to use it in the url. Note the url in the screenshot say http://localhost:8080/annieWeb/professor/pageviewer.html?result=output.html&indexId=30000 . This means that the url list is stored in output.html and the cache is stored in indexId=30000 (IndexInfo table in the db).

Here are the details
  1. An example of generating output.html is in the CommandLineSearch.java (under /trunk/core/lbjSearch/src/lbjse/tools).
  1. Put the file into /professor folder in tomcat webapps folder. (If Tomcat is run under Eclipse, simply republish the file by refreshing annieWeb).
  1. Open the url http://localhost:8080/annieWeb/professor/pageviewer.html?result=output.html&indexId=30000  and start annotating.