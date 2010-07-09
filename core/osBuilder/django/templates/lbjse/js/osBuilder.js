function getFieldNames(){
	return $('inpFieldNames').value.split(',');
}

function getFieldIds(){
	return $('inpFieldIds').value.split(',');
}

function getSelText()
{
    var txt = '';
     if (window.getSelection)
    {
        txt = window.getSelection();
    }
    else if (document.getSelection)
    {
        txt = document.getSelection();
	}
    else if (document.selection)
    {
        txt = document.selection.createRange().text;
    }
    else return;
	alert(txt);
}

var popup1 = null;

var gSelection = null;

function addSelectionToTaggingField(fieldName){
	frameTagging.setTextForField(fieldName, gSelection);
	gSelection = null;
}

function init(){
	popup1 = new PopupMenu();
	var fieldNames = getFieldNames();
	
	{% for fieldName in field.names %}
		popup1.add('{{fieldName|capfirst}}', function(target) {
	    	addSelectionToTaggingField('{{fieldName}}');
			});
	{% endfor %}
		
	popup1.addSeparator();
	popup1.add('close', function(target) {
	    window.close();
	});
	popup1.setSize(140, 0);
	//popup1.bind('example1'); // target is this pre block
	popup1.bind(); // target is document 
}

function showSelection(e){
	if (!e) var e = window.event;
	//alert("button " + e.button + " type " + e.type + ' Character was ' + character + " " + code + " ctrl " + e.ctrlKey);	
	if (e.ctrlKey){
		gSelection = e.currentTarget.getSelection();
		popup1.show(e);
		//alert(e.currentTarget.getSelection());
		//getSelText();
	}
	return false;
}