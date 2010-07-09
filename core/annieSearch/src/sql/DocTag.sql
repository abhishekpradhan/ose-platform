/* show tag value for each index*/
SELECT D.IndexId, IndexInfo.name, COUNT(*) , IndexInfo.IndexPath
FROM DocTag D LEFT JOIN IndexInfo ON D.IndexId = IndexInfo.IndexId
GROUP BY IndexId ;

/* show tag value for each index, drill down to field */
SELECT FieldInfo.FieldId, FieldInfo.Name , COUNT(VALUE)  
FROM DocTag, FieldInfo
WHERE IndexId = 1301 AND DocTag.FieldId  = FieldInfo.FieldId
GROUP BY FieldId 
ORDER BY FieldId;



/* show tag statistics for each value from field 1 for Index 1000 */
SELECT VALUE, /* IndexId, */ COUNT(DISTINCT DocId) AS doc FROM DocTag
WHERE FieldId = 8
  AND IndexId = 30000
GROUP BY VALUE
ORDER BY doc DESC;

/* count distinct docId tagged for a field, for a index id */
SELECT VALUE, COUNT(DISTINCT DocId) AS doc FROM DocTag
WHERE FieldId = 36
 AND indexId = 1301
GROUP BY VALUE
ORDER BY CONVERT(VALUE, DECIMAL) DESC;

/* show tag value for domain  */
SELECT * FROM DocTag
WHERE FieldId IN (7,8,9,10,11)
GROUP BY VALUE
ORDER BY FieldId;

/* show tag value and count for each field */
SELECT FieldId, VALUE, COUNT(*) AS C FROM DocTag
WHERE FieldId BETWEEN 7 AND 11
  AND IndexId = 30000
GROUP BY VALUElbjseData
ORDER BY FieldId, C DESC, VALUE;

/* show number of docs for each field within a domain, and an index */
SELECT Name,N.FieldId, NDoc FROM 
	(SELECT FieldId, COUNT( DocId) AS NDoc FROM DocTag
	WHERE IndexId = 30000
	GROUP BY FieldId) AS C,
	(SELECT Name, FieldId
	FROM FieldInfo
	WHERE DomainId = 2) AS N
WHERE C.FieldID = N.FieldID;

/* show number of docs for each field within a domain, and an index */
SELECT IndexId, COUNT(DISTINCT DocId) FROM 
	DocTag AS D,
	(SELECT Name, FieldId
	FROM FieldInfo
	WHERE DomainId = 2) AS N
WHERE D.FieldID = N.FieldID
GROUP BY IndexId;

/* show all tags for a domain */
SELECT DocTag.*
FROM DocTag, FieldInfo
WHERE 
  DomainId = 4
  AND DocTag.FieldId = FieldInfo.FieldId ;

/* show all docid tagged for a domain and an index */
SELECT DISTINCT DocId
FROM DocTag, FieldInfo
WHERE IndexId = 30000
  AND DomainId = 2
  AND DocTag.FieldId = FieldInfo.FieldId ;

/*********** How many docs have been tagged **********/

SELECT DISTINCT DocId 
FROM DocTag
WHERE IndexId = 1301
  AND FieldId IN (31,32,33,34,35,36)
;

SELECT DocId FROM (SELECT * FROM DocTag WHERE IndexId = 301) AS C
GROUP BY DocId
HAVING COUNT(*) >= 7
ORDER BY COUNT(*) 

;

SELECT * FROM DocTag
WHERE IndexId = 1301
LIMIT 10
;

SELECT DISTINCT(DocId) FROM DocTag
WHERE IndexId = 301
  AND VALUE = 'non';


INSERT INTO DocTag(IndexId, DocId, FieldId, VALUE)
SELECT IndexId,DocId,FieldId,VALUE FROM tmp;


SELECT COUNT(*) FROM DocTag  LIMIT 10;

/* check integrity (duplication) */
INSERT IGNORE INTO tmp_copy(IndexId, DocId, FieldId, VALUE , TagId)
SELECT IndexId, DocId, FieldId, VALUE , TagId
FROM DocTag;

SELECT * FROM DocTag L LEFT JOIN tmp_copy R
ON L.IndexId = R.IndexId
 AND L.DocId = R.DocId
 AND L.FieldId = R.FieldId
WHERE R.TagId IS NULL;


INSERT INTO DocTag_1000
SELECT * FROM DocTag
WHERE IndexId = 1000
;

INSERT IGNORE INTO DocTag(IndexId,DocId, FieldId,VALUE)
SELECT IndexId,DocId, FieldId,VALUE FROM tmp_copy
WHERE IndexId = 100;

SELECT COUNT(*) FROM DocTag_warbler;

/******** Data migration-merging-updating ************/
INSERT INTO tmp
SELECT * FROM DocTag 
;

INSERT INTO tmp_copy
SELECT * FROM DocTag_warbler 
;

SELECT IndexId, Name FROM IndexInfo;

SELECT IndexId, COUNT(*) FROM tmp
GROUP BY IndexId
;

INSERT INTO tmp_diff
SELECT New.* FROM tmp_copy NEW LEFT JOIN tmp OLD  
ON 
   Old.IndexId = New.IndexId
  AND Old.DocId = New.DocId
  AND Old.FieldId = New.FieldId
WHERE Old.TagId IS NULL
;

INSERT INTO DocTag (IndexId, DocId, FieldId, VALUE)
SELECT IndexId, DocId, FieldId, VALUE FROM tmp ;

INSERT INTO DocTag(IndexId, DocId, FieldId, VALUE)
SELECT 401, DocId, FieldId, VALUE FROM DocTag
WHERE IndexId = 101
  AND FieldId IN (40,41,42,43,44,45);

/* see the similarity between two set of DocTags */
SELECT * FROM 
(SELECT DISTINCT DocId FROM DocTag WHERE IndexId = 30000) A INNER JOIN 
(SELECT * FROM DocTag WHERE IndexId = 30001) B USING (DocId)
;

/* compare new tags and old */
SELECT * FROM DocTag
WHERE TagId NOT IN (SELECT TagId FROM tmp);

SELECT TagId FROM tmp
WHERE TagId NOT IN (SELECT TagId FROM DocTag);

SELECT MAX(TagId) FROM DocTag;

/* Tags that are new  */
SELECT * FROM DocTag 
WHERE TagId >= 125158;

/* clean up tags */

DELETE FROM DocTag
WHERE IndexId = 30000
  AND FieldId BETWEEN 7 AND 11 
 AND LENGTH(VALUE) < 2;
 
SELECT * FROM DocTag
WHERE VALUE LIKE "%.%"
  AND IndexId = 1201
  ;
  
  
/* adhoc queries */
SELECT DISTINCT DocID FROM DocTag
WHERE IndexId = 1301
 