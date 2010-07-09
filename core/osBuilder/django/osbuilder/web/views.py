from django.http import HttpResponse
from django.conf import settings
from django.shortcuts import render_to_response
from django.template import RequestContext
from lbjse.models import DomainInfo, CandidateFeatureInfo

def html(request,domain=None,file=None):
    print "Got request for domain",domain, "file", file
    context = RequestContext(request)
    if domain is None:
        return HttpResponse("bad request")

    domainInfo = DomainInfo.objects.filter(name=domain)
    if len(domainInfo) == 0:
        return HttpResponse("invalid domain")
    
    domainInfo = domainInfo[0] #domain name should be unique
    
    featureList = {}
    for candidate in CandidateFeatureInfo.objects.all():
        if not featureList.has_key(candidate.datatype):
            featureList[candidate.datatype] = []
        featureList[candidate.datatype].append(candidate.feature)
        
    fields = domainInfo.fieldinfo_set.all().values()
    fieldNames = []
    fieldIds = []
    fieldTypes = []
    fieldInfos = []
    for field in fields:
        fieldNames.append(field['name'])
        fieldIds.append(field['id'])
        fieldTypes.append(field['type'])
        if str(field['name']) != u"other":            
            fieldInfos.append(field)
            
    fieldMap = {"ids" : fieldIds,
                "names" : fieldNames,
                "types" : fieldTypes,
                "fieldInfos" : fieldInfos}
    
    context.update({"domain" : domainInfo,
                    "field"  : fieldMap})
    return render_to_response("web/" + file,context,mimetype="text/html")

