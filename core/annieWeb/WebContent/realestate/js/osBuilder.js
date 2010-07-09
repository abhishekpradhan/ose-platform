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
	
	
		popup1.add('Loca', function(target) {
	    	addSelectionToTaggingField('loca');
			});
	
		popup1.add('Type', function(target) {
	    	addSelectionToTaggingField('type');
			});
	
		popup1.add('Price', function(target) {
	    	addSelectionToTaggingField('price');
			});
	
		popup1.add('Beds', function(target) {
	    	addSelectionToTaggingField('beds');
			});
	
		popup1.add('Baths', function(target) {
	    	addSelectionToTaggingField('baths');
			});
			
		popup1.add('Area', function(target) {
			addSelectionToTaggingField('area');
			});	
	
		popup1.add('Other', function(target) {
	    	addSelectionToTaggingField('other');
			});
	
		
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