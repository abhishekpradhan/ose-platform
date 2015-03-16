# Introduction #

The code in the top folder is
```
/
    /annieDB
    /annieIndex
    /annieSearch
    /annieWeb
    /lbjSearch
    /osBuilder
```

Each folder contains a module of AnnieSearch. The module dependency is as following :

annieDB <-- annieIndex <-- annieSearch  <-- annieWeb, lbjSearch

osBuilder is a standalone python django program that handles domain specific search engine code generation, plus a couple of tools.

Each java module has been provided with ant build file. To build everything, go to lbjSearch folder and type
```
ant deps               # --> build all required modules
ant updateJar          # --> put updated jars into /lib folder
ant dist               # --> build jar files into /dist folder
```

# Setting Things Up #

> ObjectSearch code is using several open source packages. Please get familiar and install them

  * Lucene : to store inverted indices
  * Apache Tomcat : for web demo
  * [MySQL](http://dev.mysql.com/downloads/) : to store feedback info.
  * Eclipse : for development. This is best suited for Java, but I also use it for Python, Javascript, HTML...
    * need to install JavaEE plugins for web development.
  * Subversion client : (e.g : [TortoiseSVN](http://tortoisesvn.tigris.org/) for Windows)
    * [A quick tutorial](http://aymanh.com/subversion-a-quick-tutorial#CheckoutModifyCommit)


#### Checking out and set up dev environment ####


  1. Check out the code using command

> svn co svn://warbler.cs.uiuc.edu/ObjectSearch/core

  1. Import annieSearch into Eclipse

> you need to resolve some missing library path. More details to come

  1. Import annieWeb into Eclipse.

> Make it depend on annieSearch.
> You'll need to resolve some missing configuration for Tomcat

#### Testing it works ####

  * If everything is fine. You can start running Tomcat inside Eclipse to test

  * After starting Tomcat inside Eclipse, which starts a server at port 8080, point your browser to
> > http://localhost:8080/annieWeb/TestFeatureQuery.html
> > And hit "Search"

#### More installation nodes ####
  * InstallationNotes
  * OseBackend

# Details #

For specific module, see

  * AnnieIndexModule    : index html web pages to make lucene feature index.
  * AnnieSearchModule   : process feature query .
  * AnnieWebModule      : back end for  object search web interface .
  * LbjSearchModule     : learning module.
  * OsBuilderModule     : tools to generate code for individual object search engine.