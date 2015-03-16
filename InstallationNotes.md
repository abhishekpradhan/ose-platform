# Installation node #
### Requirements ###

  1. JDK1.6 in c:\jdk1.6.0\_xx
  1. svn client
  1. Ant
  1. MySQL
  1. Tomcat
  1. Python2.x

### Setup ###
  1. Environment variables
    * Set JAVA\_HOME
    * Make sure javac in PATH (for lbj to work)
    * Set TOMCAT\_HOME (for annieWeb to compile)
  1. Check-out code
    * svn co svn://warbler.cs.uiuc.edu/ObjectSearch/core/trunk/annieDB
    * svn co svn://warbler.cs.uiuc.edu/ObjectSearch/core/trunk/annieIndex
    * svn co svn://warbler.cs.uiuc.edu/ObjectSearch/core/trunk/annieSearch
    * svn co svn://warbler.cs.uiuc.edu/ObjectSearch/lbjse/lbjSearch
    * svn co svn://warbler.cs.uiuc.edu/ObjectSearch/core/trunk/annieWeb
  1. Compile
    * Go to each of the above directory '''in that order''', compile with ant
```
ant updateJar
ant dist
```
    * Sync the annieWeb/WebContent with the one in Tomcat.
  1. Restart Tomcat
  1. If everything is going well, you can open the url : http://localhost:8080/annieWeb/professor/pageviewer.html on your machine