from django.conf.urls.defaults import *
from django.conf import settings

# Uncomment the next two lines to enable the admin:
# from django.contrib import admin
# admin.autodiscover()

urlpatterns = patterns('',
    # Example:
    (r'^osbuilder/datavis/', include('osbuilder.datavis.urls')),
    (r'^osbuilder/', include('osbuilder.web.urls')),    
    (r'^chickenegg/', include('osbuilder.chickenegg.urls')),
    (r'^osbuilder/$', 'django.views.static.serve',
        {'document_root': settings.TEMPLATE_DIRS[0], 'path': "static/index.html"}), #this is to show the directory (index.html)
    # Uncomment the next line to enable admin documentation:
    # (r'^admin/doc/', include('django.contrib.admindocs.urls')),

    # Uncomment the next line for to enable the admin:
    # (r'^admin/(.*)', admin.site.root),
)
