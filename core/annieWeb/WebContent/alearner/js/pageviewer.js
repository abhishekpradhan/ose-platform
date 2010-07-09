var CACHE_SERVLET_URL="/annieWeb/CacheServlet";
var DOCINFO_SERVLET = "/annieWeb/DocInfoServlet";

var gDatasetId=-1;
var gSessionId=-1;
var gIndexId = 30000;
var gDocId = -1; 
var gListOfSessions=null;
var queryParameters = null;

function init(){
	queryParameters = getQueryParameter(document.location.search);	
	gDocId = queryParameters["docId"];
	gIndexId = queryParameters["indexId"];
	item = ajaxLoadDocTagForDocument(gIndexId, gDocId);
	frameTagging.setTaggingItem(1, gIndexId, item)
	frameWebpage.location = CACHE_SERVLET_URL + document.location.search;	
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
	    			docId: docId},
	    asynchronous: false
	}

	var request = new Ajax.Request(DOCINFO_SERVLET, options);
	if (request.transport.responseText.match("failed") != null) {
		alert("Something wrong " + request.transport.responseText);
		return null; 
	}
	else
		return request.transport.responseText.evalJSON();
}