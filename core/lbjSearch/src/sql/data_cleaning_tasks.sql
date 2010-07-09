/* show tag statistics for each value from field 1 for Index 1000 */
SELECT VALUE, /* IndexId, */ COUNT(DISTINCT DocId) AS doc FROM DocTag
WHERE FieldId = 36
  AND IndexId = 302
GROUP BY VALUE
ORDER BY CONVERT(VALUE, SIGNED) ;