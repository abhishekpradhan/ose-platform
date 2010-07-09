# This is an auto-generated Django model module.
# You'll have to do the following manually to clean this up:
#     * Rearrange models' order
#     * Make sure each model has one field with primary_key=True
# Feel free to rename the models, but don't rename db_table values or field names.
#
# Also note: You'll have to insert the output of 'django-admin.py sqlcustom [appname]'
# into your database.

from django.db import models

class DocInfo(models.Model):
    docid = models.IntegerField(primary_key=True, db_column='DocId') # Field name made lowercase.
    url = models.CharField(max_length=1536, db_column='Url', blank=True) # Field name made lowercase.
    indexid = models.IntegerField(primary_key=True, db_column='IndexId') # Field name made lowercase.
    title = models.CharField(max_length=1536, db_column='Title', blank=True) # Field name made lowercase.
    bodytext = models.TextField(db_column='BodyText', blank=True) # Field name made lowercase.
    html = models.TextField(db_column='Html', blank=True) # Field name made lowercase.
    class Meta:
        db_table = u'DocInfo'

class DocTag(models.Model):
    tagid = models.IntegerField(primary_key=True, db_column='TagId') # Field name made lowercase.
    indexid = models.IntegerField(null=True, db_column='IndexId', blank=True) # Field name made lowercase.
    docid = models.IntegerField(null=True, db_column='DocId', blank=True) # Field name made lowercase.
    fieldid = models.IntegerField(null=True, db_column='FieldId', blank=True) # Field name made lowercase.
    value = models.CharField(max_length=60, db_column='Value', blank=True) # Field name made lowercase.
    
    def __str__(self):
        return "DocTag[%s](index=%s,doc=%s,field=%s,%s)" % (self.tagid, self.indexid, self.docid,self.fieldid, self.value)
    
    class Meta:
        db_table = u'DocTag'
    

class DomainInfo(models.Model):
    domainid = models.IntegerField(primary_key=True, db_column='DomainId') # Field name made lowercase.
    name = models.CharField(max_length=189, db_column='Name', blank=True) # Field name made lowercase.
    description = models.CharField(max_length=765, db_column='Description', blank=True) # Field name made lowercase.
    class Meta:
        db_table = u'DomainInfo'

class Featureinfo(models.Model):
    featureid = models.IntegerField(primary_key=True, db_column='FeatureId') # Field name made lowercase.
    fieldid = models.IntegerField(null=True, db_column='FieldId', blank=True) # Field name made lowercase.
    template = models.CharField(max_length=765, db_column='Template', blank=True) # Field name made lowercase.
    weight = models.FloatField(null=True, db_column='Weight', blank=True) # Field name made lowercase.
    datecreated = models.DateTimeField(db_column='DateCreated') # Field name made lowercase.
    isdeleted = models.IntegerField(null=True, db_column='IsDeleted', blank=True) # Field name made lowercase.
    class Meta:
        db_table = u'FeatureInfo'

class FeatureinfoCopy(models.Model):
    featureid = models.IntegerField(primary_key=True, db_column='FeatureId') # Field name made lowercase.
    fieldid = models.IntegerField(null=True, db_column='FieldId', blank=True) # Field name made lowercase.
    template = models.CharField(max_length=765, db_column='Template', blank=True) # Field name made lowercase.
    weight = models.FloatField(null=True, db_column='Weight', blank=True) # Field name made lowercase.
    datecreated = models.DateTimeField(db_column='DateCreated') # Field name made lowercase.
    isdeleted = models.IntegerField(null=True, db_column='IsDeleted', blank=True) # Field name made lowercase.
    class Meta:
        db_table = u'FeatureInfo_copy'

class Feedback(models.Model):
    queryid = models.IntegerField(primary_key=True, db_column='QueryId') # Field name made lowercase.
    docid = models.IntegerField(primary_key=True, db_column='DocId') # Field name made lowercase.
    indexid = models.IntegerField(primary_key=True, db_column='IndexId') # Field name made lowercase.
    relevant = models.IntegerField(null=True, db_column='Relevant', blank=True) # Field name made lowercase.
    timestamp = models.DateTimeField(db_column='Timestamp') # Field name made lowercase.
    domainid = models.IntegerField(primary_key=True, db_column='DomainId') # Field name made lowercase.
    class Meta:
        db_table = u'Feedback'

class FieldInfo(models.Model):
    fieldid = models.IntegerField(primary_key=True, db_column='FieldId') # Field name made lowercase.
    domainid = models.IntegerField(null=True, db_column='DomainId', blank=True) # Field name made lowercase.
    name = models.CharField(max_length=189, db_column='Name', blank=True) # Field name made lowercase.
    type = models.CharField(max_length=45, db_column='Type', blank=True) # Field name made lowercase.
    description = models.CharField(max_length=765, db_column='Description', blank=True) # Field name made lowercase.
    class Meta:
        db_table = u'FieldInfo'

class Indexinfo(models.Model):
    indexid = models.IntegerField(primary_key=True, db_column='IndexId') # Field name made lowercase.
    name = models.CharField(max_length=765, db_column='Name', blank=True) # Field name made lowercase.
    description = models.CharField(max_length=765, db_column='Description', blank=True) # Field name made lowercase.
    indexpath = models.CharField(max_length=765, db_column='IndexPath', blank=True) # Field name made lowercase.
    cachepath = models.CharField(max_length=765, db_column='CachePath', blank=True) # Field name made lowercase.
    class Meta:
        db_table = u'IndexInfo'

class Lbjsequery(models.Model):
    queryid = models.IntegerField(primary_key=True, db_column='QueryId') # Field name made lowercase.
    fieldid = models.IntegerField(null=True, db_column='FieldId', blank=True) # Field name made lowercase.
    value = models.CharField(max_length=3072, db_column='Value', blank=True) # Field name made lowercase.
    class Meta:
        db_table = u'LBJSEQuery'

class Modelinfo(models.Model):
    modelid = models.IntegerField(primary_key=True, db_column='ModelId') # Field name made lowercase.
    fieldid = models.IntegerField(null=True, db_column='FieldId', blank=True) # Field name made lowercase.
    path = models.CharField(max_length=765, db_column='Path', blank=True) # Field name made lowercase.
    weight = models.FloatField(null=True, db_column='Weight', blank=True) # Field name made lowercase.
    class Meta:
        db_table = u'ModelInfo'

class Queryinfo(models.Model):
    queryid = models.IntegerField(primary_key=True, db_column='QueryId') # Field name made lowercase.
    querystring = models.CharField(max_length=3000, db_column='QueryString', blank=True) # Field name made lowercase.
    description = models.CharField(max_length=765, db_column='Description', blank=True) # Field name made lowercase.
    domainid = models.IntegerField(null=True, db_column='DomainId', blank=True) # Field name made lowercase.
    class Meta:
        db_table = u'QueryInfo'

class Tagruleinfo(models.Model):
    ruleid = models.IntegerField(primary_key=True, db_column='RuleId') # Field name made lowercase.
    fieldid = models.IntegerField(null=True, db_column='FieldId', blank=True) # Field name made lowercase.
    value = models.CharField(max_length=765, db_column='Value', blank=True) # Field name made lowercase.
    class Meta:
        db_table = u'TagRuleInfo'

class Trainingqueryinfo(models.Model):
    attributeid = models.IntegerField(primary_key=True, db_column='AttributeId') # Field name made lowercase.
    fieldid = models.IntegerField(null=True, db_column='FieldId', blank=True) # Field name made lowercase.
    value = models.CharField(max_length=381, db_column='Value', blank=True) # Field name made lowercase.
    class Meta:
        db_table = u'TrainingQueryInfo'

class Laptop(models.Model):
    url = models.CharField(max_length=4500, blank=True)
    mpn = models.CharField(max_length=900, blank=True)
    model = models.CharField(max_length=2295, blank=True)
    brand = models.CharField(max_length=450, blank=True)
    family = models.CharField(max_length=450, blank=True)
    harddrive = models.FloatField(null=True, blank=True)
    montior = models.FloatField(null=True, blank=True)
    probrand = models.CharField(max_length=900, db_column='proBrand', blank=True) # Field name made lowercase.
    proid = models.CharField(max_length=900, db_column='proId', blank=True) # Field name made lowercase.
    protype = models.CharField(max_length=900, db_column='proType', blank=True) # Field name made lowercase.
    prospeed = models.FloatField(null=True, db_column='proSpeed', blank=True) # Field name made lowercase.
    ram = models.FloatField(null=True, blank=True)
    ramtype = models.CharField(max_length=180, db_column='ramType', blank=True) # Field name made lowercase.
    color = models.CharField(max_length=450, blank=True)
    class Meta:
        db_table = u'laptops'

class LaptopPrice(models.Model):
    tagid = models.IntegerField(primary_key=True, db_column='id') # Field name made lowercase.
    url = models.CharField(max_length=4500, blank=True)
    price = models.FloatField(null=True, blank=True)
    class Meta:
        db_table = u'price'
        
