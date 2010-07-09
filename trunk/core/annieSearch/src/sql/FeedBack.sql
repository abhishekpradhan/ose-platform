SELECT IndexId, COUNT(*) FROM Feedback
GROUP BY IndexId;;

SELECT DomainId, COUNT(*) FROM Feedback
GROUP BY DomainId;

SELECT QueryId, IndexId, COUNT(*) FROM Feedback
WHERE IndexId = 30000
GROUP BY QueryId;

SELECT IndexId, QueryId, COUNT(*) FROM Feedback
WHERE QueryId IN (102,103,107)
 AND IndexId = 1000
GROUP BY QueryId;

SELECT * FROM Feedback
WHERE IndexId = 1301
  AND DocId = 2348;

SELECT * FROM Feedback
WHERE IndexId = 301
  AND DocId = 12
  and QueryId = 301;

select * from DocTag
where DocId = 2348;

select * from Feedback
where IndexId = 1301;

%BooleanFeature(Proximity(Token(life),Token(book),-10,10))
%BooleanFeature(Token(lifebook));

select * from Feedback
where IndexId = 11201
  and Relevant = 1;

select * from TagRuleInfo
where FieldId between 7 and 11;

select * from FieldInfo
where FieldId between 7 and 11;