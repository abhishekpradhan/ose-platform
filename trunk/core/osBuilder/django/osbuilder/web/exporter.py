import datetime
import tempfile
import os


from django.core.servers.basehttp import FileWrapper
from django.http import HttpResponse
from django.conf import settings

from osbuilder.tools.dbToText import dbToText, exportDomainInfoToFile

# Create your views here.
def exportDocuments(request):
    indexId = int(request.GET.get('index', '-1'))
    domainId = int(request.GET.get('domain','-1'))
    print settings.FILE_UPLOAD_TEMP_DIR
    (handle,name) = tempfile.mkstemp(suffix=".trec",prefix="annotated_docs_index_%d_domain_%d" % (indexId, domainId),dir=settings.FILE_UPLOAD_TEMP_DIR)    
    os.close(handle)
    dbToText(indexId, domainId, name)
    
    filename = name                                
    wrapper = FileWrapper(file(filename))
    response = HttpResponse(wrapper, content_type='text/bin')
    response['Content-Disposition'] = 'attachment; filename=%s' % os.path.basename(filename)
    response['Content-Length'] = os.path.getsize(filename)
    return response

def exportDomainInfo(request):
    domainId = int(request.GET.get('domain','-1'))
    print settings.FILE_UPLOAD_TEMP_DIR
    (handle,name) = tempfile.mkstemp(suffix=".domain",prefix="domain_info_%d_" % domainId,dir=settings.FILE_UPLOAD_TEMP_DIR)    
    os.close(handle)
    exportDomainInfoToFile(domainId, name)
    
    filename = name
    wrapper = FileWrapper(file(filename))
    response = HttpResponse(wrapper, content_type='text/bin')
    response['Content-Disposition'] = 'attachment; filename=%s' % os.path.basename(filename)
    response['Content-Length'] = os.path.getsize(filename)
    return response


    