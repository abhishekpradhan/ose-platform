# This is an auto-generated Django model module.
# You'll have to do the following manually to clean this up:
#     * Rearrange models' order
#     * Make sure each model has one field with primary_key=True
# Feel free to rename the models, but don't rename db_table values or field names.
#
# Also note: You'll have to insert the output of 'django-admin.py sqlcustom [appname]'
# into your database.

from django.db import models


class DomainInfo(models.Model):
    id = models.IntegerField(primary_key=True, db_column='DomainId') # Field name made lowercase.
    name = models.CharField(max_length=189, db_column='Name', blank=True) # Field name made lowercase.
    description = models.CharField(max_length=765, db_column='Description', blank=True) # Field name made lowercase.
    class Meta:
        db_table = u'DomainInfo'

class FeatureInfo(models.Model):
    featureid = models.IntegerField(primary_key=True, db_column='FeatureId') # Field name made lowercase.
    fieldid = models.IntegerField(null=True, db_column='FieldId', blank=True) # Field name made lowercase.
    template = models.CharField(max_length=765, db_column='Template', blank=True) # Field name made lowercase.
    weight = models.FloatField(null=True, db_column='Weight', blank=True) # Field name made lowercase.
    datecreated = models.DateTimeField(db_column='DateCreated') # Field name made lowercase.
    isdeleted = models.IntegerField(null=True, db_column='IsDeleted', blank=True) # Field name made lowercase.
    class Meta:
        db_table = u'FeatureInfo'

class CandidateFeatureInfo(models.Model):
    id = models.IntegerField(primary_key=True, db_column='Id') # Field name made lowercase.
    datatype = models.CharField(max_length=31, db_column='Datatype', blank=False) # Field name made lowercase.
    feature = models.CharField(max_length=63, db_column='Feature', blank=False) # Field name made lowercase.
    argument = models.IntegerField(null=True, db_column='Argument', blank=True) # Field name made lowercase.
    class Meta:
        db_table = u'lbjseCandidateFeatureInfo'
        
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
    id = models.IntegerField(primary_key=True, db_column='FieldId') # Field name made lowercase.
    domain = models.ForeignKey(DomainInfo, db_column='DomainId') # Field name made lowercase.
    name = models.CharField(max_length=189, db_column='Name', blank=True) # Field name made lowercase.
    type = models.CharField(max_length=45, db_column='Type', blank=True) # Field name made lowercase.
    description = models.CharField(max_length=765, db_column='Description', blank=True) # Field name made lowercase.
    class Meta:
        db_table = u'FieldInfo'

class IndexInfo(models.Model):
    id = models.IntegerField(primary_key=True, db_column='IndexId') # Field name made lowercase.
    name = models.CharField(max_length=765, db_column='Name', blank=True) # Field name made lowercase.
    description = models.CharField(max_length=765, db_column='Description', blank=True) # Field name made lowercase.
    indexpath = models.CharField(max_length=765, db_column='IndexPath', blank=True) # Field name made lowercase.
    cachepath = models.CharField(max_length=765, db_column='CachePath', blank=True) # Field name made lowercase.
    class Meta:
        db_table = u'IndexInfo'

class DocTag(models.Model):
    tagid = models.IntegerField(primary_key=True, db_column='TagId') # Field name made lowercase.
    indexid = models.IntegerField(null=True, db_column='IndexId', blank=True) # Field name made lowercase.
    docid = models.IntegerField(null=True, db_column='DocId', blank=True) # Field name made lowercase.    
    field = models.ForeignKey(FieldInfo, db_column='FieldId')
    value = models.CharField(max_length=60, db_column='Value', blank=True) # Field name made lowercase.
    class Meta:
        db_table = u'DocTag'


class LbjseQuery(models.Model):
    queryid = models.IntegerField(primary_key=True, db_column='QueryId') # Field name made lowercase.
    fieldid = models.IntegerField(null=True, db_column='FieldId', blank=True) # Field name made lowercase.
    value = models.CharField(max_length=3072, db_column='Value', blank=True) # Field name made lowercase.
    class Meta:
        db_table = u'LBJSEQuery'

class ModelInfo(models.Model):
    modelid = models.IntegerField(primary_key=True, db_column='ModelId') # Field name made lowercase.
    fieldid = models.IntegerField(null=True, db_column='FieldId', blank=True) # Field name made lowercase.
    path = models.CharField(max_length=765, db_column='Path', blank=True) # Field name made lowercase.
    weight = models.FloatField(null=True, db_column='Weight', blank=True) # Field name made lowercase.
    class Meta:
        db_table = u'ModelInfo'

class QueryInfo(models.Model):
    queryid = models.IntegerField(primary_key=True, db_column='QueryId') # Field name made lowercase.
    querystring = models.CharField(max_length=3000, db_column='QueryString', blank=True) # Field name made lowercase.
    description = models.CharField(max_length=765, db_column='Description', blank=True) # Field name made lowercase.
    domainid = models.IntegerField(null=True, db_column='DomainId', blank=True) # Field name made lowercase.
    class Meta:
        db_table = u'QueryInfo'


