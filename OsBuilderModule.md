The OSB is a tool to train a new vertical search engine. Typical usages include:

  * Data Annotation : User would interact with a light weight search engine, pull interesting documents out and annotate them with values used to latter train the classifiers/rankers.
  * Feature Design : User can add/remove features that helps ranking different fields of the object. In AnnieSearch, features are crafted from a sophisticated feature language. In LbjSearch, features are selected from a list.
  * Export data : User can export data (e.g : document and annotations) from the database into text file, for other studies.

# Access to the tools #
  * LbjSearch : [camera domain](http://warbler.cs.uiuc.edu/lbjseWeb/camera/osBuilder.html) [other domains](http://warbler.cs.uiuc.edu/lbjseWeb/)
  * AnnieSearch : [all domains](http://warbler.cs.uiuc.edu/annieWeb/)

# How to guide #

## Build a new vertical search engine? ##

Here are the steps required to build a new vertical. Even though these steps are listed sequentially, they can be done after any other step in a spiral manner. For example : 1-->2-->3-->2-->3--> 4-->2-->3-->4....

  1. DomainSpecification     : specify object fields and its data type.
  1. DataAnnotation          : label web pages with object information.
  1. RankingFunctionLearning : use labeled data to learn a ranking function.
  1. TestingAndValidation    : try them in the new object search engine.

## Export documents with annotations? ##

  1. Open the tool [for camera](http://warbler.cs.uiuc.edu/lbjseWeb/camera/osBuilder.html)
  1. Click "Load Indices", then select appropriate index to export.
  1. Click "Download Data" on the left panel, near the bottom.
  1. Save the file onto your computer.

## Explore annotations? ##

  1. Open the tool [for camera](http://warbler.cs.uiuc.edu/lbjseWeb/camera/osBuilder.html)
  1. Click "Load Indices", then select appropriate index to export.
  1. Click the button "Annotation Search". The result panel will display all the document  with annotation.
  1. User can narrow the search by providing criteria for each field of annotation. For example, put "canon" to the "Brand" textbox and click "Annotation Search" will result only those tagged with "canon". Put "range(4,5)" in the "mpix" textbox will result only those tagged with mpix from 4 to 5.

## How to annotate? ##

  1. Open the tool [for camera](http://warbler.cs.uiuc.edu/lbjseWeb/camera/osBuilder.html)
  1. Click "Load Indices", then select appropriate index to export.
  1. Use "Search by Feature" feature to find documents to annotate (this is currently only work for AnnieSearch builder)
  1. Click on the result
  1. Either add annotation manually in the tagging panel (in the middle ) or
  1. Select the text in the bottom frame, hit "Ctrl + Right Click", a menu will pop up, select the appropriate field for the tag value. After filling all the tags, click "Add" in the tagging panel