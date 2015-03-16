# Introduction #

This module contains code related to all the learning tasks in Object Search.

  1. Generate training data from annotated web pages
  1. Generate lbj code from field specification
  1. Feature selection
  1. Generate classifiers

# Compilation #

```
ant deps
ant "LBJ compile" -Dlbjsrc=common
ant dist
```

# Details #

## How to train a field ##

  1. Create a new session for the field
    * Open the web interface at http://.../annieWeb/alearner/
  1. Annotate some docs with that field
  1. Add query values
> > Go to alearner and add the tags found in datavis/explorer
  1. Generate training data
```
ant "export tagtrec" -Dindex=30002 -Ddomain=4 -Doutput=c:\working\lbjSearch\grad_30002.tagtrec
```
  1. Generate lbj code for the field
  1. Compile lbjcode
```
ant "LBJ compile" -Dlbjsrc=grad_s42
```
  1. Create a training session
    * Take a look at this TrainingSessionData for examples
  1. Rank features for selection
```
ant "rank features" -Dsession=42 -Ddata=9
```
  1. Select search feature.
  1. Train it.
```
ant "train concrete" -Dsession=42 -Dtrec=c:\working\lbjSearch\grad_30002.tagtrec -Dnrounds=2
```

## More commands ##

  1. google to rresult
```
D:\Users\KimCuong\Documents\PhD\Research\ObjectSearch\core_trunk\lbjSearch>
ant "google to rresult" -DtopK=1000 -Dindex=20004 -DinputPrefix=C:\working\lbjSearch\google\ranking\professor\r -DoutputPrefix=C:\working\lbjSearch\google\ranking\professor\rresult.long -Dfrom=1005 -Dto=1005
```
  1. rresult to html
```
D:\Users\KimCuong\Documents\PhD\Research\ObjectSearch\core_trunk\lbjSearch>
ant "rresult to html" -Ddomain=professor -Dindex=20004 -Drresult=C:\working\lbjSearch\google\ranking\professor\rresult.1005
```
  1. export tagtrec
```
D:\Users\KimCuong\Documents\PhD\Research\ObjectSearch\core_trunk\lbjSearch>
ant "export tagtrec" -Dindex=20004 -Ddomain=2 -Doutput=C:\working\lbjSearch\professor_20004.tagtrec
```
  1. update rresult
```
D:\Users\KimCuong\Documents\PhD\Research\ObjectSearch\core_trunk\lbjSearch>
ant "update rresult" -Dtagtrec=C:\working\lbjSearch\professor_20004.tagtrec -Dquery=1005 -Drresult=C:\working\lbjSearch\google\ranking\professor\rresult.1005
```
  1. evaluate rresult
```
D:\Users\KimCuong\Documents\PhD\Research\ObjectSearch\core_trunk\lbjSearch>
ant "evaluate rresult" -Dmode=map -Drresult=C:\working\lbjSearch\google\ranking\professor\rresult.1005.update
```
  1. lbjsearch experiment
```
D:\Users\KimCuong\Documents\PhD\Research\ObjectSearch\core_trunk\lbjSearch>
ant "lbjsearch experiment" -Dtagtrec=C:\working\lbjSearch\professor_20004.tagtrec -Dindex=20004 -Doutput=C:\working\lbjSearch\lbjsearchexperiment\rresult -Dfrom=1004 -Dto=1005
```