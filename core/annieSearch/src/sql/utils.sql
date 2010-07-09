/* DocInfo */
select count(*) from Feedback;

select * from DocInfo
where IndexId = 6;


/* update feedback */
update Feedback
set IndexId = 0
where IndexId is null;

/* query Feedback */
select count(*) from Feedback;

select * from Feedback 
where IndexId = 6 and QueryId = 121;


insert into temp(f1,f2)
select Old.DocId, New.DocId from 
(select * from DocInfo
where indexId = 0 ) as Old,
(select * from DocInfo
where indexId = 3 ) as New
where Old.Url = New.Url
 ;

insert into feedback(QueryId, IndexId, Relevant, DocId);
select FB.QueryId,3, FB.Relevant, temp.f2 as New from 
Feedback  as FB, temp
where FB.DocId = temp.f1
  and FB.IndexId = 0;


select * from DocInfo
where indexId = 6; 

176 + 155 + 42 + 191;

/* DocTagging */
insert DocTagging(DocId,IndexId, TUniversity, TDepartment)
select DocId,IndexId, "wisconsin" as University, "computer,science" as Department from DocInfo
where IndexId = 6
  and (Url like "%wisc_cs%") ;
/* on duplicate key update TUniversity = 'wisconsin', TDepartment = 'mathematics' ; concat(TUniversity, ',illinois' ) */

insert DocTagging(DocId,IndexId, TUniversity, TDepartment)
select DocId,IndexId, "wisconsin" as University, "mathematics" as Department from DocInfo
where IndexId = 6
  and (Url like "%wisc_math%") ;

insert DocTagging(DocId,IndexId, TUniversity, TDepartment)
select DocId,IndexId, "illinois" as University, "computer,science" as Department from DocInfo
where IndexId = 6
  and (Url like "%uiuc_cs%") ;

insert DocTagging(DocId,IndexId, TUniversity, TDepartment)
select DocId,IndexId, "illinois" as University, "mathematics" as Department from DocInfo
where IndexId = 6
  and (Url like "%uiuc_math%") ;

select * from DocTagging
where IndexId = 6;

select * from DocTagging 
where DocId = 343;

/* Feedback for homepage/non homepages */
insert into FeedBack(DocId, IndexId, QueryId, Relevant)
select DocId, IndexId, 100 as QueryId, 1 as Relevant from DocInfo
where IndexId = 6
  and DocId < 414;

select Relevant,Url,Feedback.DocId from Feedback , DocInfo
where Feedback.IndexId = 6
  And QueryId = 108
  And Feedback.DocId = DocInfo.DocId
  And Feedback.IndexId = DocInfo.IndexId;

/* Feedback for dept queries */
insert into FeedBack(DocId, IndexId, QueryId, Relevant)
select DocId, IndexId, 111 as QueryId, 1 as Relevant from DocTagging
where TDepartment = 'mathematics';

select * from Feedback as F left join DocTagging as D
on F.IndexId = D.IndexId
  and F.docId = D.DocId
where 
  F.IndexId = 6
  and F.queryId between 150 and 159
  
  ;

/* Feedback for uni queries */
insert into FeedBack(DocId, IndexId, QueryId, Relevant)
select DocId, IndexId, 120 as QueryId, 0 as Relevant from DocTagging
where TUniversity = 'wisconsin';

select * from DocTagging
where IndexId = 6
  and TArea like "%net%";

insert into FeedBack(DocId, IndexId, QueryId, Relevant)
select DocId,IndexId, 133 as QueryId, 0 as Relevant from DocTagging
where IndexId = 6
  and TArea not like "%net%";

insert into Feedback(QueryId, DocId, IndexId, Relevant)
values (300,72, 6,1);

select * from DocTagging
where IndexId = 10
group by TZoom;

select DT.*, DI.Url from DocTagging as DT, DocInfo As DI
where DT.IndexId = 6
  and DT.IndexId = DI.IndexId
  and DT.DocId = DI.DocId
  and TArea like "%ml%";

