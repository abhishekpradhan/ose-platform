@echo off
echo batch file to transfer old index to a combined new index
pause
set DJANGO_SETTINGS_MODULE=osbuilder.settings 
python runner.py osbuilder.tools.tagMigrator.py mapped_output.txt 601 602