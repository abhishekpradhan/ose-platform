from django import forms
import datetime
from django.conf import settings
import codecs

class ChickenEggForm(forms.Form):    
    key = forms.CharField(max_length=256)
    value = forms.CharField(widget=forms.Textarea)
    
    def save(self):
         key = self.cleaned_data['key']
         value = self.cleaned_data['value']
         timestamp = datetime.datetime.now()
         file = codecs.open(settings.FILE_UPLOAD_TEMP_DIR + "/chickenegg.data", encoding='utf-8', mode='a')
         file.write(timestamp.strftime("%Y:%m:%d %H:%M:%S") + "\n" +
                     key + "\n" +
                     value + "\n" +
                     "------------A-B-C-D-E-F-G-H-------\n")
         file.close()
         