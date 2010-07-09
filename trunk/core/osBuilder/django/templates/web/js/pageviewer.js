var CACHE_SERVLET_URL="/annieWeb/CacheServlet";
var DOCINFO_SERVLET = "/annieWeb/DocInfoServlet";
var TAG_SUGGESTION_SERVLET = "/annieWeb/TagGuessingServlet";
var DIV_TAG_SUGGESTION = "DivTagSuggestion";

var gDatasetId=-1;
var gSessionId=-1;
var gIndexId = 30000;
var gDocId = -1; 
var gListOfSessions=null;
var queryParameters = null;
var gDomainId = {{domain.id}};
var gResultPage = null;

function init(){
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
