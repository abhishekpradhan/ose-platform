/* select trainingQueryInfo for a domain */
select * from TrainingQueryInfo, FieldInfo
where FieldInfo.FieldId = TrainingQueryInfo.FieldId
  and FieldInfo.DomainId = 2;

/* select trainingQueryInfo for a field*/
select * from TrainingQueryInfo
where FieldId in (36);

/* show tag statistics for each value from field 1 for Index 1000 */
select Value, IndexId, count(distinct DocId) as doc, length(Value) as TagLen from DocTag
where FieldId = 66
  and IndexId = 602
group by Value
order by doc DESC, TagLen ;

/* count distinct docId tagged for a numeric field, for a index id */
select Value, count(distinct DocId) as doc from DocTag
where FieldId = 66
 and indexId = 602
group by Value
order by convert(Value, DECIMAL) DESC;

/* count non documents */
select * from 
(
select count(distinct DocID) as "Number of doc tagged for field 'other'" from DocTag
where FieldId = 45
  and IndexId = 401) as A, 

(select count(distinct DocID) as "Number of doc tagged for this domain" 
from DocTag , FieldInfo
where FieldInfo.FieldId = DocTag.FieldId
  and IndexId = 401) as B;

insert into TrainingQueryInfo(FieldId, Value)
select 66, Value from DocTag
where FieldId = 66
  and IndexId = 602
group by Value;

select * from TrainingQueryInfo
where FieldId = 66;

select FieldId, QueryId, count(*) from TrainingFeedback, TrainingQueryInfo
where TrainingFeedback.QueryId = TrainingQueryInfo.AttributeId
  and FieldId between 66 and 66
group by QueryId;

select FieldId, QueryId, count(*) from TrainingFeedback, TrainingQueryInfo
where TrainingFeedback.QueryId = TrainingQueryInfo.AttributeId
group by FieldId, QueryId;

select * from TrainingFeedback
where QueryId >= 944