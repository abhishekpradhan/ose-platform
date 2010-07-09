
/* load current features */
select * from FeatureInfo
where IsDeleted = 0;

/* load current features for osearch*/
select FeatureInfo.* from FeatureInfo, FieldInfo
where IsDeleted = 0
  and FeatureInfo.FieldId = FieldInfo.FieldId
  and FieldInfo.DomainId = 3
order by FeatureId;

select ModelInfo.* from ModelInfo, FieldInfo
where ModelInfo.FieldId = FieldInfo.FieldId
  and FieldInfo.DomainId = 3
order by FieldId;


select * from ModelInfo, FieldInfo
where ModelInfo.FieldId = FieldInfo.FieldId
  and FieldInfo.DomainId = 1
order by FieldInfo.FieldId;

select Template from FeatureInfo
where FieldId = 6
order by DateCreated DESC;

select * from FeatureInfo
where IsDeleted = 0
order by DateCreated DESC;

select * from FeatureInfo
where DateCreated = "2008-10-15 22:09:09";

select * from FeatureInfo
where FeatureId = 262;

/* back up features info */
insert into FeatureInfo_backup
select * from FeatureInfo
where IsDeleted = 0;

