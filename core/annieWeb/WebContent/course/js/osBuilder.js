function getFieldNames(){
	return $('inpFieldNames').value.split(',');
}

function getFieldIds(){
	return $('inpFieldIds').value.split(',');
}

/* TODO : move to osBuilderUtils.js */
function getFieldNameFromId(fieldId){
	var fieldNames = $('inpFieldNames').value.split(',');
	var fieldIds = $('inpFieldIds').value.split(',');
	for (var i = 0 ; i < fieldIds.length ; i++){
		if (fieldIds[i] == fieldId)
			return fieldNames[i];
	}
	return null;
}

/* TODO : move to osBuilderUtils.js */
function getFieldIdFromName(fieldName){
	var fieldNames = $('inpFieldNames').value.split(',');
	var fieldIds = $('inpFieldIds').value.split(',');
	for (var i = 0 ; i < fieldNames.length ; i++){
		if (fieldNames[i] == fieldName)
			return fieldIds[i];
	}
	return null;
}

function getFieldTypeFromId(fieldId){
	var fieldTypes= $('inpFieldTypes').value.split(',');
	var fieldIds = $('inpFieldIds').value.split(',');
	for (var i = 0 ; i < fieldIds.length ; i++){
		if (fieldIds[i] == fieldId)
			return fieldTypes[i];
	}
	return null;
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
	
	
		popup1.add('Name', function(target) {
	    	addSelectionToTaggingField('name');
			});
	
		popup1.add('Prof', function(target) {
	    	addSelectionToTaggingField('prof');
			});
	
		popup1.add('Sem', function(target) {
	    	addSelectionToTaggingField('sem');
			});
	
		popup1.add('Year', function(target) {
	    	addSelectionToTaggingField('year');
			});
	
		popup1.add('Loca', function(target) {
	    	addSelectionToTaggingField('loca');
			});
	
		popup1.add('Univ', function(target) {
	    	addSelectionToTaggingField('univ');
			});
	
		popup1.add('Cont', function(target) {
	    	addSelectionToTaggingField('cont');
			});
	
		popup1.add('Cnum', function(target) {
	    	addSelectionToTaggingField('cnum');
			});
	
		popup1.add('Dept', function(target) {
	    	addSelectionToTaggingField('dept');
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