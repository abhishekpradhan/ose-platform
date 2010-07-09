#!/bin/sh
rsync -r WebContent/professor kimpham2@warbler.cs.uiuc.edu:rsyncBridge/
rsync -r WebContent/WEB-INF/lib kimpham2@warbler.cs.uiuc.edu:rsyncBridge/WEB-INF/
rsync -r AnnieWeb.jar kimpham2@warbler.cs.uiuc.edu:rsyncBridge/WEB-INF/lib/
