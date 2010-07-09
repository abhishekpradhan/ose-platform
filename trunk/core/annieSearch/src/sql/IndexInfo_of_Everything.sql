/* show tag value for the index, drill down to field */
SELECT FieldInfo.FieldId, FieldInfo.Name , COUNT(VALUE)  
FROM DocTag, FieldInfo
WHERE IndexId = 30000 AND DocTag.FieldId  = FieldInfo.FieldId
GROUP BY FieldId 
ORDER BY FieldId;

/* show all docid tagged for a domain and an index */
SELECT DomainId, COUNT(DISTINCT DocId)
FROM DocTag, FieldInfo
WHERE IndexId = 30000
  AND DocTag.FieldId = FieldInfo.FieldId 
GROUP BY DomainId;

SELECT VALUE, COUNT(DISTINCT DocId) AS C FROM DocTag
WHERE IndexId = 30000
  AND FieldId = 8
GROUP BY VALUE
ORDER BY C DESC