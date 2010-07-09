"""
URLConf for Django user registration and authentication.

Recommended usage is a call to ``include()`` in your project's root
URLConf to include this URLConf for any URL beginning with
``/papershare/``.

"""

from django.conf.urls.defaults import *
from django.conf import settings

urlpatterns = patterns('',
    (r'^exportDocuments', "osbuilder.web.exporter.exportDocuments"),
    (r'^exportDomainInfo', "osbuilder.web.exporter.exportDomainInfo"),
    (r'^static/(?P<path>.*)$', 'django.views.static.serve',
        {'document_root': settings.OSBUILDER_DIR + "/static", 'show_indexes': True}),
    (r'^(?P<domain>[a-z]*)/(?P<file>[\S]*)', "osbuilder.web.views.html"),
    
)
