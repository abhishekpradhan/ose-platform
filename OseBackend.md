# Introduction #

OSE uses MySQL DB to store the following data:
  * Object domain specs
  * Web pages Annotation (Tags)
  * Training related data (model, weights, ...)

# Details #

  1. Import MySQL Schema in
/trunk/core/annieDB/schema/mysql\_schema.sql

  1. Run the following queries to load data

<pre>

insert  into DomainInfo(DomainId,Name,Description) values<br>
(1,'camera','Digital Camera Search'),<br>
(2,'professor','Professor Homepage Search'),<br>
(3,'laptop','Laptop Search'),<br>
(4,'grad','Graduate Student Search'),<br>
(5,'realestate','Housing Search'),<br>
(6,'course','Course Search'),<br>
(200,'pairprof','Professor Homepage Search (lbjse)');<br>
<br>
insert  into FieldInfo(FieldId,DomainId,Name,Type,Description,TrainingSessionId) values (1,1,'brand','keyword','keyword brand name',-1),<br>
(2,1,'model','super_keyword','text model',-1),<br>
(3,1,'mpix','number','megapixel value',-1),<br>
(4,1,'zoom','number','zoom value',-1),<br>
(5,1,'price','number','price value',-1),<br>
(6,1,'other','other','domain features',-1),<br>
(7,2,'name','super_keyword','name',4),<br>
(8,2,'dept','super_keyword','department',2),<br>
(9,2,'univ','super_keyword','university',3),<br>
(10,2,'area','text','area of interest',5),<br>
(11,2,'other','other','domain features',1),<br>
(31,3,'brand','keyword','lenovo,hp,sony...',31),<br>
(32,3,'model','keyword','thinkpad,vaio...',-1),<br>
(33,3,'moni','number','15 inch...',32),<br>
(34,3,'hdd','number','160GB...',36),<br>
(35,3,'proc','number','1.6Gz...',37),<br>
(36,3,'price','number','$3000',33),<br>
(37,3,'other','other','domain laptop',35),<br>
(41,4,'name','keyword','name',-1),<br>
(42,4,'univ','super_keyword','university',42),<br>
(43,4,'area','text','research interest',-1),<br>
(44,4,'advi','super_keyword','adviser',-1),<br>
(45,4,'other','other','other',45),<br>
(51,5,'loca','super_keyword','city, street name',-1),<br>
(52,5,'type','keyword','property type',-1),<br>
(53,5,'price','number','price',-1),<br>
(54,5,'beds','number','number of bedrooms',-1),<br>
(55,5,'baths','number','number of bathrooms',-1),<br>
(56,5,'other','other','domain features',-1),<br>
(61,6,'name','super_keyword','course name',-1),<br>
(62,6,'prof','super_keyword','course instructor name',-1),<br>
(63,6,'sem','keyword','fall/spring/summer/winter',-1),<br>
(64,6,'year','number','2008,08,2009,09...',-1),<br>
(65,6,'loca','super_keyword','location',-1),<br>
(66,6,'univ','super_keyword','university',-1),<br>
(67,6,'cont','text','content',-1),<br>
(68,6,'cnum','number','course number',-1),<br>
(69,6,'dept','super_keyword','department',-1),<br>
(70,6,'other','other','domain of interest',-1),<br>
(201,200,'name','super_keyword','professor name',240),<br>
(202,200,'dept','super_keyword','professor department',220),<br>
(203,200,'univ','super_keyword','professor university',230),<br>
(204,200,'area','text','professor research area',250),<br>
(205,200,'other','other','is professor homepage',210);<br>
<br>
#This is just an example, create each entry for the lucene index resided in your machine.<br>
<br>
insert  into IndexInfo(IndexId,Name,Description,IndexPath,CachePath) values<br>
(9,'random_50K','50K random pages from dmoz[error]','C:\\working\\annieIndex\\random_part1_index','C:\\working\\annieIndex\\random_part1_index_cache');<br>
<br>
</pre>