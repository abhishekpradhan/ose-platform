
var gDatasetId=-1;
var gSessionId=-1;
var gIndexId = 30000;
var gDocId = -1; 
var gListOfSessions=null;
var queryParameters = null;
var gDomainId = {{domain.id}};
var gResultPage = null;
var gListOfIndices = null; 
var gListOfDataSets = null;
	
function init(){
	new Dialog.Box("dlgProgress");
	queryParameters = getQueryParameter(document.location.search);	
	gDocId = queryParameters["docId"];
	gIndexId = queryParameters["indexId"];
	gResultPage = queryParameters["result"];
	if (gResultPage != null)
		frameResult.location = gResultPage ; 
	if (gDocId != null)
		loadPageViewer(gDocId, gIndexId, gDomainId);	
}

function loadPageViewer(docId, indexId,domainId, resultId){
	resultId = typeof(resultId) != 'undefined' ? resultId : 0;
	gIndexId = indexId;
	gDocId = docId;
	gDomainId = domainId;
	item = ajaxLoadDocTagForDocument(indexId, docId);
	if (item != null){
		frameTagging.setTaggingItem(resultId, indexId, item)
		$("linkPrevious").href = "javascript:frameResult.openResult(" + (resultId -1) + ")";
		$("linkNext").href = "javascript:frameResult.openResult(" + (resultId +1) + ")"; 
		frameWebpage.location = CACHE_SERVLET_URL + "?docId=" + gDocId + "&indexId="+ indexId;
		$("DivPlainBody").innerHTML = item["PlainBody"];
		reloadTagSuggestion(docId, indexId,domainId);
	}
}

function setResultListItemCopy(html){
	$("liResultCopy").innerHTML = html;
}

function getQueryParameter(qstring){
	var params = {};
	pairs = qstring.substring(1).split("&"); //qstring ="?docId=26903&indexId=30000"
	for (var i = 0 ; i < pairs.length; i++){
		var fields = pairs[i].split("=");
		if (fields.length >= 2)		
			params[fields[0]] = fields[1]; 
	}
	return params;
}

function ajaxLoadDocTagForDocument(indexId, docId){
	var options = {
	    method: "get",
	    parameters: {indexId: indexId,
	    			docId: docId,
	    			domainId : gDomainId},
	    asynchronous: false
	}

	var request = new Ajax.Request(DOCINFO_SERVLET, options);
	if (request.transport.responseText == "failed") {
		alert("Something wrong " + request.transport.responseText);
		return null; 
	}
	else
		return request.transport.responseText.evalJSON();
}

function reloadTagSuggestion(docId, indexId,domainId){
	var options = {
	    method: "get",
	    parameters: {indexId: indexId,
	    			docId: docId,
	    			domainId : domainId},	 
	    onSuccess: updateDivTagSuggestion,
	  	onFailure: function(){ alert('Something went wrong...') },   
	}
	new Ajax.Request(TAG_SUGGESTION_SERVLET, options);
}

function updateDivTagSuggestion(obj){
    logMessage("Got tag " + obj.responseText);
    var suggestion = obj.responseText.evalJSON();
    
    var domInfo = suggestion["domInfo"];
    var gtags = suggestion["gtags"];
    var divHtml = "";
    var link = new Template('<a href="javascript:addTag(\'#{fieldName}\', \'#{value}\')">#{value}</a>');
    var fieldIds = []
    for (var fieldId in domInfo){
    	fieldIds.push(fieldId);
    }
    fieldIds.sort();
    for (var k = 0 ; k < fieldIds.length ; k++){
    	var fieldId  = fieldIds[k];
    	divHtml += "<h3> " + domInfo[fieldId] + "</h3>";
    	for (var i = 0 ; i < gtags[fieldId].length ; i++){
	        divHtml += "<li>" + link.evaluate({
	        							'fieldName':domInfo[fieldId],
	        							'value':gtags[fieldId][i]}) + "</li>";
	    }    	
    }
    $(DIV_TAG_SUGGESTION).innerHTML = divHtml;
}

function addTag(fieldName, value){
	frameTagging.setTextForField(fieldName, value);
	frameTagging.addAllTags();
}

function loadIndices(){
	options = { method:'get',
	  			onSuccess: updateIndexSelectBox,
	  			onFailure: function(){ alert('Something went wrong...') },
				};
	$("dlgProgress").show();
	new Ajax.Request(GET_INDICES_SERVLET_URL, options);
    
}

function updateIndexSelectBox(obj){
	$("dlgProgress").hide();
    logMessage("Got Indices " + obj.responseText);
    gListOfIndices = obj.responseText.evalJSON();
    var theSelect = $("selIndex");
    removeAllChildren(theSelect);
    for (var i = 0 ; i < gListOfIndices.length ; i++){
        var newOption = document.createElement('option');
        newOption.text = gListOfIndices[i]['Name'];
        newOption.id = "q_" + gListOfIndices[i]['IndexId'];
        newOption.value = gListOfIndices[i]['IndexId'];
        newOption.selected = true;
        theSelect.add(newOption, null);
        logMessage("Added index " + newOption);
    }
    selectIndex();
}

function selectIndex(){
    var selectBox = $("selIndex");
    var selectedOption = selectBox.options[selectBox.selectedIndex]
    gIndexId = selectedOption.value;
    logMessage("Selecting index " + gIndexId);
    $("indDescription").innerHTML = getIndexInfo(gIndexId)["Description"];
}

function getIndexInfo(indexId){
	for (var i = 0 ; i < gListOfIndices.length ; i++){
		if (gListOfIndices[i]["IndexId"] == indexId)
			return gListOfIndices[i];
	}
}

function loadData(){
	options = { method:'get',parameters:{'opt':'listData'},
	  			onSuccess: updateDataSelectBox,
	  			onFailure: function(){ alert('Something went wrong...') },
				};	
	$("dlgProgress").show();
	new Ajax.Request(LEARNER_SERVLET_URL, options);
}

function updateDataSelectBox(obj){
	$("dlgProgress").hide();
    logMessage("Got Data " + obj.responseText);
    var listOfDataSets = obj.responseText.evalJSON();
    var theData = $("selData");
    removeAllChildren(theData);
    var emptyData = {};
    emptyData['Id'] = null;
    emptyData['Path'] = "data: None";
    listOfDataSets.push(emptyData);
    for (var i = 0 ; i < listOfDataSets.length ; i++){
        var newOption = document.createElement('option');
        newOption.text = listOfDataSets[i]['Path'];
        newOption.id = "ds_" + listOfDataSets[i]['Id'];
        newOption.value = listOfDataSets[i]['Id'];
        newOption.selected = true;
        theData.add(newOption, null);
        logMessage("Added dataset " + newOption);
    }    
    gListOfDataSets = listOfDataSets;
    selectData();
}

function getDataInfo(dataId){
	for (var i = 0 ; i < gListOfDataSets.length ; i++){
		if (gListOfDataSets[i]["Id"] == dataId)
			return gListOfDataSets[i];
	}
}

function selectData(){
    var selectBox = $("selData");
    var selectedOption = selectBox.options[selectBox.selectedIndex]
    gDatasetId = selectedOption.value;
    logMessage("Selecting dataset " + gDatasetId);
    if (gDatasetId * 1 > 0) //if it's a number
    	$("dataDescription").innerHTML = getDataInfo(gDatasetId)["Description"];
    else
    	gDatasetId = null;
}

function searchLucene(){
	if (gIndexId == null){
		alert("No index is selected");
		return;
	}	
	var query = $("luceneQuery").value;
	
	startIndex = 0;			
	logMessage("searching " + query);
	var url = KEYWORD_SEARCH_SERVLET + "?" 
		+ "domainId=" + gDomainId 
		+ "&indexId=" + gIndexId 
		+ "&start=" + startIndex 
		+ "&nresult=100" 
		+ "&query=" + query;   
	frameResult.location = url;	
}

function searchLbjse(){
	if (gIndexId == null){
		alert("No index is selected");
		return;
	}	
	var query = $("lbjseQuery").value;
	
	startIndex = 0;			
	logMessage("searching " + query);
	var url = LBJSE_SEARCH_SERVLET + "?" 
		+ "domainId=" + gDomainId 
		+ "&indexId=" + gIndexId 
		+ "&start=" + startIndex 
		+ "&nresult=100" 
		+ "&query=" + query;   
	if (gDatasetId != null){
		url += "&data=" + gDatasetId ;
	}
		
	frameResult.location = url;	
}

