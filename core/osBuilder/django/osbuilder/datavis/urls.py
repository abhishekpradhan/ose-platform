"""
URLConf for Django user registration and authentication.

Recommended usage is a call to ``include()`` in your project's root
URLConf to include this URLConf for any URL beginning with
``/papershare/``.

"""

from django.conf.urls.defaults import *

urlpatterns = patterns('',
    (r'^explore', "osbuilder.datavis.views.exploreCube"),    
)
