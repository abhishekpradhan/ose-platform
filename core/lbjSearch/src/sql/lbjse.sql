/* show matching training session and fields */ 
SELECT * FROM FieldInfo F, lbjseTrainingSession S
WHERE F.DomainId = 3
  AND F.FieldId = S.FieldId;

/* copy concrete search feature from pointwise to pairwise sessions */  
INSERT INTO lbjseSearchFeature_tmp(SessionId, VALUE) 
SELECT 250 AS SessionId,VALUE FROM lbjseSearchFeature
WHERE SessionId = 5;

INSERT INTO lbjseSearchFeature(SessionId, VALUE) 
SELECT SessionId, VALUE FROM lbjseSearchFeature_tmp;

DELETE FROM lbjseSearchFeature_tmp;
DELETE FROM lbjseQueryValue_tmp;

SELECT * FROM lbjseTrainingSession;

INSERT INTO lbjseQueryValue_tmp(SessionId, VALUE) 
SELECT 240 AS SessionId,VALUE FROM lbjseQueryValue
WHERE SessionId = 4;

INSERT INTO lbjseQueryValue(SessionId, VALUE) 
SELECT SessionId, VALUE FROM lbjseQueryValue_tmp;

UPDATE QueryInfo
SET DomainId = 200
WHERE QueryId BETWEEN 1001 AND 1010;