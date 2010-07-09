var GET_INDICES_SERVLET_URL = "/annieWeb/GetIndices"; 
var GET_QUERIES_SERVLET_URL = "/annieWeb/GetQueries";
var GET_DOMAINS_SERVLET_URL = "/annieWeb/GetDomains";


var DOMAIN_ID = 1;

var gIndexId = null;
var gQueryId = null;

function init(){
	//new Dialog.Box("dlgProgress");	
	loadDomains();	
}

function loadIndices(){
	options = { method:'get',
	  			onSuccess: updateIndexSelectBox,
	  			onFailure: function(){ alert('Something went wrong...') },
				};
	//$("dlgProgress").show();
	new Ajax.Request(GET_INDICES_SERVLET_URL, options);
    
}
function loadDomains(){
	options = { method:'get',
	  			onSuccess: updateDomainSelectBox,
	  			onFailure: function(){ alert('Something went wrong...') },
				};
	//$("dlgProgress").show();
	new Ajax.Request(GET_DOMAINS_SERVLET_URL, options);
    
}
function updateIndexSelectBox(obj){
	$("dlgProgress").hide();
    logMessage("Got Indices " + obj.responseText);
    var listOfIndices = obj.responseText.evalJSON();
    var theSelect = $("selIndex");
    removeAllChildren(theSelect);
    for (var i = 0 ; i < listOfIndices.length ; i++){
        var newOption = document.createElement('option');
        newOption.text = listOfIndices[i]['Name'];
        newOption.id = listOfIndices[i]['IndexId'];
        newOption.value = listOfIndices[i]['IndexId'];
        newOption.selected = true;
        theSelect.add(newOption, null);
        logMessage("Added index " + newOption);
    }
   // selectIndex();
}
function updateDomainSelectBox(obj){
	$("dlgProgress").hide();
    logMessage("Got Domains " + obj.responseText);
    var listOfIndices = obj.responseText.evalJSON();
    var theSelect = $("selDomain");
    removeAllChildren(theSelect);
    for (var i = 0 ; i < listOfIndices.length ; i++){
        var newOption = document.createElement('option');
        newOption.text = listOfIndices[i]['Name'];
        newOption.id = "q_" + listOfIndices[i]['DomainId'];
        newOption.value = listOfIndices[i]['DomainId'];
        newOption.selected = true;
        theSelect.add(newOption, null);
        logMessage("Added domain " + newOption);
    }
    //selectIndex();
}
function selectIndex(){
    var selectBox = $("selIndex");
    var selectedOption = selectBox.options[selectBox.selectedIndex]
    gIndexId = selectedOption.value;
    logMessage("Selecting index " + gIndexId);
    parent.frameInfoBox.setBoxInfo(gIndexId);
}

/* TODO : move to osBuilderUtils.js */
function getFieldNames(){
	return $('inpFieldNames').value.split(',');
}

/* TODO : move to osBuilderUtils.js */
function getFieldIds(){
	return $('inpFieldIds').value.split(',');
}


function ajaxAddFeature(domainId, fieldId, template, weight){
	var options = {
	    method: "get",
	    parameters: {action:"add",
	    			domainId:domainId,
	    			fieldId:fieldId, 
	    			template:template, 
	    			weight:weight},
	    asynchronous: false
	}
	var request = new Ajax.Request("/annieWeb/FeatureInfoServlet", options);
	if (request.transport.responseText.match("failed") != null) {
		alert("Something wrong " + request.transport.responseText);
		return false; 
	}
	else
		return true;
}

function ajaxRemoveExistingFeatures(domainId){
	var options = {
	    method: "get",
	    parameters: {action:"removeAll",domainId:domainId},
	    asynchronous: false
	}
	var request = new Ajax.Request("/annieWeb/FeatureInfoServlet", options);
	if (request.transport.responseText.match("ok") == null){
		alert("Something wrong" + request.transport.responseText);
		return false;
	}
	else
		return true;
}

function loadQueries(){
	//var domainId = $("selDomain");
	
	var selectBox = $("selDomain");
	//alert('a' +selectBox.selectedIndex);
    var domainId = selectBox.options[selectBox.selectedIndex].value;
    
	options = { method:'get',
				parameters : {domainId : domainId}, 
	  			onSuccess: updateQuerySelectBox,
	  			onFailure: function(){ alert('Something went wrong...') },
				};
				
	//$("dlgProgress").show();
	new Ajax.Request(GET_QUERIES_SERVLET_URL, options);
    
}

function createNewIndex(){
	var selectBox = $("selDomain");
    var domainId = selectBox.options[selectBox.selectedIndex];
	var indexFile = $("indexFile").value;
	var indexName = $("indexName").value;
	alert('a');
	options = { method:'post',
				parameters : {domainId : domainId, indexFile: indexFile, indexName: indexName}, 
				ENCTYPE: 'multipart/form-data',
	  			onSuccess: updateQuerySelectBox,
	  			onFailure: function(){ alert('Something went wrong...') },
				};
				
	//$("dlgProgress").show();
	new Ajax.Request(CREATE_NEW_INDEX_SERVLET, options);
}

function updateQuerySelectBox(obj){
	$("dlgProgress").hide();
    logMessage("Got Queries " + obj.responseText);
    var listOfQueries = obj.responseText.evalJSON();
    var theSelect = $("selQuery");
    removeAllChildren(theSelect);
    for (var i = 0 ; i < listOfQueries.length ; i++){
        var newOption = document.createElement('option');
        newOption.text = listOfQueries[i]['Description'];
        newOption.id = listOfQueries[i]['QueryId'];
        newOption.value = listOfQueries[i]['QueryId'];
        newOption.selected = true;
        theSelect.add(newOption, null);
        logMessage("Added query " + newOption);
    }
    selectQuery();
}

function selectQuery(){
    var selectBox = $("selQuery");
    var selectedOption = selectBox.options[selectBox.selectedIndex]
    var queryJSON = selectedOption.value;
    gQueryId = selectedOption.id.substring(6);
    logMessage("Selecting query " + queryJSON);
    var queryDict = queryJSON.evalJSON();
    var fields = getFieldNames();
    for (var i= 0 ; i < fields.length ; i++){
    	var fieldName = fields[i];
    	if (queryDict[fieldName] != null){
    		$('inpField_' + fieldName).value = queryDict[fieldName];
    	}
    }
    
	var featureString = "";
	featureString = selectedOption.text;

	document.getElementById('txtFeatureString').value = featureString;
}


function ShowElementByID(id) {
	if (document.getElementById) { // DOM3 = IE5, NS6
		if(document.getElementById(id).style.display == "none"){
			document.getElementById(id).style.display = 'block';
		}else document.getElementById(id).style.display = 'block';
	}else{
		if(document.layers){
			if(document.id.display == "none"){ document.id.display = 'block'; }else document.id.display = 'block';
			}else{
				if(document.all.id.style.visibility == "none"){
					document.all.id.style.visibility = 'block';
				} else document.all.id.style.visibility = 'block';
			}
	}
}

function ShowHideElementByID(id) {
	if (document.getElementById) { // DOM3 = IE5, NS6
		if(document.getElementById(id).style.display == "block"){
			document.getElementById(id).style.display = 'none';
		}else document.getElementById(id).style.display = 'none';
	}else{
		if(document.layers){
			if(document.id.display == "block"){ document.id.display = 'none'; }else document.id.display = 'none';
		}else{
			if(document.all.id.style.visibility == "block"){
				document.all.id.style.visibility = 'none';
			} else document.all.id.style.visibility = 'none';
		}
	}
}

function showQueryFeedback(){
	if (gQueryId == null){
		alert("No query is selected");
		return;
	}
	//score_id_
	var resultList = parent.frameResult.$('result_list').childNodes;
	logMessage(resultList);
	for (var i = 0 ; i < resultList.length; i++){
		var item = resultList[i];
		logMessage(item.childNodes[1].id);
		var docId = item.childNodes[1].id.substring(9);
		var relevant = ajaxGetFeedback(gQueryId, docId, gIndexId);
		if (relevant == "true"){
			item.childNodes[1].style.color = 'green';
		}
		else if (relevant == "false"){
			item.childNodes[1].style.color = 'red';
		}
	}
}

function ajaxGetFeedback(queryId, docId, indexId){
	var options = {
	    method: "get",
	    parameters: {action:"get",
	    			queryId:queryId,
	    			docId:docId, 
	    			indexId:indexId},
	    asynchronous: false
	}
	var request = new Ajax.Request("/annieWeb/FeedbackServlet", options);
	return request.transport.responseText;
}

function mergerIndex(){
	var selectBox = $("selIndex");
	var selectedOption = selectBox.options[selectBox.selectedIndex].text;
    msg = "Are you sure that you want to merger: new trec file to " + selectedOption + " ?";
    //all we have to do is return the return value of the confirm() method
    if (confirm(msg)) // merger file;
    alert('ok');
    else alert('no');

}

function choiseIndex(obj){	
	document.frm.index.value= obj;
	if (obj==2){
		alert("Create New Index!!!");		
		ShowHideElementByID('Load_Index_div');
		ShowElementByID('NameIndex_div');
	}
	else{
		alert("Update Existed Index");	
		ShowElementByID('Load_Index_div');
		ShowHideElementByID('NameIndex_div');
		loadIndices();
		
	}
	
}
function createIndex(){
	 var indexName = prompt("Index Name","");	
	 var indexFile = $("trecFile"); 
	 var selectBox = $("selDomain");
		//alert('a' +selectBox.selectedIndex);
	 var domainId = selectBox.options[selectBox.selectedIndex].value;
	 //$("dlgProgress").show();
		options = { method:'POST',
				parameters : {domainId : domainId, indexFile: indexFile,  indexName: indexName}, 
				//ENCTYPE: 'multipart/form-data',	 
				onSuccess: showOutput,
	  			onFailure: function(){ alert('Something went wrong...') },
				};					
	 new Ajax.Request(CREATE_NEW_INDEX_SERVLET, options);
	 	 
	 alert('abc');
	// $("dlgProgress").hide();
}

function validate(){
	var index = $("index").value;
	var indexName= $("indexName").value;	
	//if(document.frm.trecfile!='') alert(trecFile);
	//alert(index+"-"+indexName+"-");
	if((index==2) && (indexName==null|| indexName=='')){
		alert("You must enter new index name!!!");
		return false;
	}else{		
	document.frm.submit();
	return true;
	}
}
