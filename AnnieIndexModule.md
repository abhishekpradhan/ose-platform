## Indexer ##
```
ant "trec indexer" -Dtrec=c:\working\lbjSearch\30000_dedup.htmltrec -Dpath=c:\working\annieIndex\combine_dedup_index -Doptions=--create
```

## Index Mergers ##

  1. ose.index.tool.IndexMerger
  1. ose.index.tool.IndexUniqueMerger
This will merge sourceIndex into destIndex, and sourceIndex\_cache into destIndex\_cache as well
```
java ose.index.tool.IndexMerger --sourceIndex C:\working\annieIndex\combine_training_index --destIndex C:\working\annieIndex\combine_index --withCache
```

## Index Viewer ##
  1. ose.index.tool.ShowIndex

## Configure Index for AnnieWeb ##
  1. Put an entry in IndexInfo table in the database. (id, path and cache path)
  1. Note the IndexId, it can be used to refer to the index, and its cache throughout the whole system.

## Remove duplicate in index ##
  1. export raw html trec
```
ant "export allhtml" -Dindex=30000 -Doutput=c:\working\lbjSearch\30000.htmltrec
```
  1. remove duplication
```
ant "filter trec dedup" -Dinput=c:\working\lbjSearch\30000.htmltrec -Doutput=c:\working\lbjSearch\30000_dedup.htmltrec
```
  1. re-index.
```
ant "trec indexer" -Dtrec=c:\working\lbjSearch\30000_dedup.htmltrec -Dpath=c:\working\annieIndex\combine_dedup_index -Doptions=--create
```
  1. export tagurl for each domain
```
ant "export tagonly" -Dindex=30000 -Ddomain=4 -Doutput=c:\working\lbjSearch\grad_30000.tagurl
```
  1. add the tags back to the index by index tagger
```
ant "index tagger" -Dindex=30002 -Ddomain=4 -Dtagurl=c:\working\lbjSearch\grad_30000.tagurl
```

## Moving tags from one index to another ##
```
D:\Users\KimCuong\Documents\PhD\Research\ObjectSearch\core_trunk\lbjSearch>ant "export tagonly" -Dindex=30000 -Ddomain=2 -Doutput=working\professor_30000.tagurl
D:\Users\KimCuong\Documents\PhD\Research\ObjectSearch\core_trunk\lbjSearch>ant "index tagger" -Dindex=30002 -Ddomain=2 -Dtagurl=working\professor_30000.tagurl
```