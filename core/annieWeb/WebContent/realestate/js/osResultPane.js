var gIndexId = null;
var gLastFeatureString = null;
var gLastSearch = null;
var gLastDomainId = null;

//var ANNIE_FEATURE_SEARCH_SERVLET = '/annieWeb/FeatureSearchServlet';
var ANNIE_FEATURE_SEARCH_SERVLET = '/annieWeb/WeightedFeatureSearchServlet';
var ANNIE_LOGISTIC_SEARCH_SERVLET = '/annieWeb/LogisticSearchServlet';
var LUCENE_SEARCH_SERVLET = '/annieWeb/LuceneSearchServlet';

function init(){
	new Dialog.Box("dlgSearchProgress");
}

function searchByFeatures(indexId, featureString, startIndex){	
	startIndex = typeof(startIndex) != 'undefined' ? startIndex : -1;

	gIndexId = indexId;
	gLastFeatureString = featureString;
	gLastSearch = "feature";
	logMessage("searching " + featureString);
	options = { method:'post',
				parameters: {indexId : gIndexId, fquery : featureString, start : startIndex},
	  			onSuccess: showResult,
	  			onFailure: function(){ alert('osResultPane ---> Something went wrong...') },
				};
	parent.frameQueryForm.$("dlgProgress").show();
	$('dlgSearchProgress').show();
	new Ajax.Request(ANNIE_FEATURE_SEARCH_SERVLET, options);
}

function searchLogistic(indexId, domainId, featureString, startIndex){
	startIndex = typeof(startIndex) != 'undefined' ? startIndex : -1;

	gIndexId = indexId;
	gLastDomainId = domainId;
	gLastFeatureString = featureString;
	gLastSearch = "logistic";
	logMessage("searching " + featureString);
	options = { method:'post',
				parameters: {indexId : gIndexId, domainId : domainId, fquery : featureString, start : startIndex},
	  			onSuccess: showResult,
	  			onFailure: function(){ alert('Something went wrong...') },
				};
	parent.frameQueryForm.$("dlgProgress").show();
	$('dlgSearchProgress').show();
	new Ajax.Request(ANNIE_LOGISTIC_SEARCH_SERVLET, options);
}

function searchLucene(indexId, domainId, queryId, startIndex) {
	startIndex = typeof(startIndex) != 'undefined' ? startIndex : -1;
	
	gIndexId = indexId;
	gLastDomainId = domainId;
	gLastFeatureString = queryId;
	gLastSearch = "lucene";
	logMessage("searching " + queryId);
	options = { method:'post',
				parameters: {indexId : gIndexId, domainId : domainId, queryId : queryId, start : startIndex},
	  			onSuccess: showResult,
	  			onFailure: function(){ alert('searchLucene ---> Something went wrong...') },
				};
	parent.frameQueryForm.$("dlgProgress").show();
	$('dlgSearchProgress').show();
	new Ajax.Request(LUCENE_SEARCH_SERVLET, options);
}

function searchFeatureFromIndex(startIndex){
	if (gLastSearch == "feature"){
		searchByFeatures(gIndexId, gLastFeatureString , startIndex);
	} 
	else if (gLastSearch == "logistic"){
		searchLogistic(gIndexId, gLastDomainId, gLastFeatureString , startIndex);
	} 
	else if (gLastSearch == "lucene") {
		searchLucene(gIndexId, gLastDomainId, gLastFeatureString, startIndex);
	} 
	else {
		alert("No search has been done yet");
	}
}

var resultItems = null;
var RESULT_PER_PAGE = 10;

function showResult(transport){
	$('dlgSearchProgress').hide();
	parent.frameQueryForm.$("dlgProgress").hide();
	if (!transport.responseText){
		alert("no JSON object returned");
	}	
	var json = transport.responseText.evalJSON();
	logMessage(json);
	$('startIndex').innerHTML = json['startIndex'] + 1;
	$('endIndex').innerHTML = json['startIndex'] + json['recordsReturned'];
	$('totalRecords').innerHTML = json['totalRecords'];
	$('anchorPrevious').href = "javascript:searchFeatureFromIndex(" + (json['startIndex'] - RESULT_PER_PAGE) + ")" ;
	$('anchorNext').href = "javascript:searchFeatureFromIndex(" + (json['startIndex'] + RESULT_PER_PAGE) + ")" ;
	resultItems = json['records'];
	var resultList = $('result_list');
	removeAllChildren(resultList);
	for (var i = 0 ; i < resultItems.length; i++){
		var item = resultItems[i];
		var itemNode = document.createElement("li");
		var docId = item["DocId"];
		var tagList = getTagForDocId(gIndexId, docId);
		item["TagList"] = tagList;
		item['Title'] = defaultForEmpty(item['Title'],'___')
		itemNode.innerHTML = '[<b id="score_id_' + docId + '">' + item["Score"] + '</b>] ' + 
				'<a href="javascript:openResult(' + i + ')">' +  item['Title'] + '</a><br>'+
				getTagSnippet(tagList) ;
		resultList.appendChild(itemNode);
	}
}

function getTagSnippet(tagList){
	var tagDict = {};
	for (var i = 0 ; i < tagList.length ; i++){
		var fieldName = getFieldNameFromId(tagList[i]["FieldId"]);
		if (tagDict[fieldName] == undefined)
			tagDict[fieldName] = "";
		tagDict[fieldName] += tagList[i]["Value"] + " ";		
	}
	var snippet = "| ";
	var fieldNames = $('inpFieldNames').value.split(',');
	for (var i = 0 ; i < fieldNames.length ; i++){
		var fieldName = fieldNames[i];
		snippet += "<i>" + fieldName + "</i> : <b>" + getDefaultText(tagDict[fieldName],"") + "</b> | ";
	}
	return snippet;
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

function getTagForDocId(indexId, docId){
	var options = {
	    method: "get",
	    parameters: {action:"query",
	    			indexId : indexId,
	    			docId : docId 
	    			},
	    asynchronous: false
	}
	var request = new Ajax.Request("/annieWeb/TaggingServlet", options);
	if (request.transport.responseText.match("failed") != null) {
		alert("Something wrong " + request.transport.responseText);
		return null; 
	}
	else{
		return request.transport.responseText.evalJSON();
	}
}

function openResult(id){
	if (resultItems == null){
		alert("No result is found");
		return;
	}
	var item = resultItems[id];
	parent.frameWebpage.location = item["CacheUrl"];
	//logMessage("Open page " + parent.frameWebpage.location);
	parent.frameTagging.setTaggingItem(id, gIndexId, item);
}

function defaultForEmpty(strValue, defaultVal){
	if (strValue == undefined ||  strValue == null || strValue == ""){
		return defaultVal;
	}
	else return strValue;
}

function test(){
	alert("yes " );
} 

function jumpTo(){
	var index = $('txtJumpTo').value;
	searchFeatureFromIndex(index);
}

function getAllDocIds(){
	var docIds = new Array();
	for (var i = 0 ; i < resultItems.length; i++){
		docIds.push(resultItems[i]["DocId"]);
	}
	return docIds;
}