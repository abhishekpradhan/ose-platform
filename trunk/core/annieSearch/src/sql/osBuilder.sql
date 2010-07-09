

select TagRuleInfo.* from DomainInfo, FieldInfo, TagRuleInfo
where DomainInfo.DomainId = FieldInfo.DomainId
  and FieldInfo.FieldId = TagRuleInfo.FieldId
  and DomainInfo.DomainId = 15;

select TrainingQueryInfo.FieldId, count(*) from TrainingQueryInfo, FieldInfo
where TrainingQueryInfo.FieldId = FieldInfo.FieldId
  and FieldInfo.DomainId = 1
group by TrainingQueryInfo.FieldId;

insert into TrainingQueryInfo (FieldId, Value)
select FieldId, Value  from DocTag
where FieldId in (2)
  and IndexId = 100
group by Value
order by FieldId, Value;
;

select * from TrainingQueryInfo 
where FieldId = 5;

insert into LBJSEQuery
select * from TrainingQueryInfo
where FieldId in (5);


select * from FieldInfo
where DomainId = 4;

update ModelInfo
set Weight = 0.81, FieldId = 5, Path = "model/camera/price.logistic"
where ModelId = 5
