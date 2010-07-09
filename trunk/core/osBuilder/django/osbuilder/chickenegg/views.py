# Create your views here.
from django.http import HttpResponse
from forms import ChickenEggForm
from django.shortcuts import render_to_response

def collect(request):
    if request.method == 'POST': # If the form has been submitted...
        form = ChickenEggForm(request.POST) # A form bound to the POST data
        if form.is_valid(): # All validation rules pass
            form.save()
            status = "success"
            form = ChickenEggForm() # An unbound form
        else:
            status = "failed"            
    else:
        form = ChickenEggForm() # An unbound form
        status = ""
    return render_to_response('chickenegg/ChickenEggForm.html', {
        'form': form,
        'status' : status
    })
