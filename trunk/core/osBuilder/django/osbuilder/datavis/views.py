# Create your views here.

from django.shortcuts import render_to_response
from django.http import HttpResponse
from lbjse.models import DomainInfo, IndexInfo, FieldInfo, DocTag
from django.template import RequestContext
from django.db.models import Count

def showAllIndexAllDomain(request):
    print "computing index cube..."
    indexList = IndexInfo.objects.values()
    for index in indexList:
        index["aggCount"] = DocTag.objects.filter(indexid=index["id"]).values('docid').distinct().count()
        
    print "computing domain cube..."
    domainList = DomainInfo.objects.values()
    for domain in domainList:
        domain["aggCount"] = DocTag.objects.filter(field__domain__id=domain["id"]).values('docid').distinct().count()
    
    context = RequestContext(request)
    context.update({"request" : request,
                    "indexList" : indexList,
                    "domainList" : domainList})
    return render_to_response("datavis/AllIndexAllDomain.html",context,mimetype="text/html")

def showAllDomainForIndex(request, indexId):
    print "computing index cube..."
    indexList = IndexInfo.objects.filter(pk=indexId).values()
    for index in indexList:
        index["aggCount"] = DocTag.objects.filter(indexid=index["id"]).values('docid').distinct().count()
        
    print "computing domain cube for indexId ",indexId
    
    domainList = DomainInfo.objects.values()
    for domain in domainList:
        domain["aggCount"] = DocTag.objects.filter(indexid = indexId, field__domain__id=domain["id"]).values('docid').distinct().count()
    
    context = RequestContext(request)
    context.update({"request" : request,
                    "indexList" : indexList,
                    "domainList" : domainList})
    return render_to_response("datavis/AllIndexAllDomain.html",context,mimetype="text/html")

def showAllFieldForDomainIndex(request, domainId, indexId):
    print "computing field cube..."
    fieldList = FieldInfo.objects.filter(domain=domainId).values()
    for field in fieldList:
        field["aggCount"] = DocTag.objects.filter(indexid=indexId,field=field['id']).values('docid').distinct().count()
        
    print "computing value cube for indexId = %d,domainId=%d" % (indexId, domainId)
    
    valueList = DocTag.objects.filter(indexid=indexId,field__domain=domainId).values('value').annotate(aggCount=Count('docid', distinct=True))
    valueList = list(valueList)
    print type(valueList)
    valueList.sort(key=lambda item:item['aggCount'], reverse=True)
    #print valueList
    context = RequestContext(request)
    context.update({"request" : request,
                    "fieldList" : fieldList,
                    "valueList" : valueList})
    return render_to_response("datavis/AllFieldAllValueForIndexDomain.html",context,mimetype="text/html")

def showAllValueForIndexField(request, indexId, fieldId):
    print "computing field cube..."
    domainId = FieldInfo.objects.get(pk=fieldId).domain.id
    fieldList = FieldInfo.objects.filter(domain=domainId).values()
    for field in fieldList:
        field["aggCount"] = DocTag.objects.filter(indexid=indexId,field=field['id']).values('docid').distinct().count()
        
    print "computing value cube for indexId = %d,domainId=%d" % (indexId, domainId)
    
    valueList = DocTag.objects.filter(indexid=indexId,field=fieldId).values('value').annotate(aggCount=Count('docid', distinct=True))
    valueList = list(valueList)
    valueList.sort(key=lambda item:item['aggCount'], reverse=True)
    #print valueList
    context = RequestContext(request)
    context.update({"request" : request,
                    "fieldList" : fieldList,
                    "valueList" : valueList})
    return render_to_response("datavis/AllFieldAllValueForIndexDomain.html",context,mimetype="text/html")


def showAllDocForIndexFieldTag(request, indexId, fieldId, tagValue):
        
    theField = FieldInfo.objects.get(pk=fieldId)
    docList = DocTag.objects.filter(indexid=indexId,field=fieldId,value=tagValue).values('docid').distinct()
    for doc in docList:
        allTags = DocTag.objects.filter(indexid=indexId,docid=doc['docid'])
        content = ""
        fieldTags = {}
        for tag in allTags:
            if not fieldTags.has_key(tag.field.name):
                fieldTags[tag.field.name] = ""
            fieldTags[tag.field.name] += tag.value + " "
        for (key,value) in fieldTags.items():
            content += "%s:<b>%s</b> " % (key, value)
        doc["tags"] = content 
        doc["viewerUrl"] = "/osbuilder/%s/pageviewer.html?indexId=%d&docId=%d" % (theField.domain.name, indexId,doc['docid'])
    
    context = RequestContext(request)
    context.update({"request" : request,
                    "docList" : docList,
                    })
    return render_to_response("datavis/AllDocs.html",context,mimetype="text/html")

def exploreCube(request):
    # print "This request : ", request.GET.urlencode()
    # print "----", request.GET
    # print "---- type ", type(request.GET)
    # print "---- dir ", dir(request.GET)
    index = request.GET.get("index")
    domain = request.GET.get("domain")
    field = request.GET.get("field")
    tag = request.GET.get("tag")
    doc = request.GET.get("doc")
    if index is None and domain is None and doc is None :
        return showAllIndexAllDomain(request)
    elif index is not None and field is not None and tag is not None:
        index = int(index)
        field = int(field)
        return showAllDocForIndexFieldTag(request,index, field, tag)
    elif index is not None and field is not None:
        index = int(index)
        field = int(field)
        return showAllValueForIndexField(request, index, field)
    elif index is not None and domain is not None:
        index = int(index)
        domain = int(domain)
        return showAllFieldForDomainIndex(request, domain, index)
    elif index is not None:
        index = int(index)
        return showAllDomainForIndex(request, index)
    
        