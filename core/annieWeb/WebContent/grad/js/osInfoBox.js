
var gIndexId = null;

function init(){
}

function setBoxInfo(indexId){
	indexId = typeof(indexId) != 'undefined' ? indexId : gIndexId;
	gIndexId = indexId;
	loadIndexInfo(indexId);
}

function loadIndexInfo(indexId){
	var domainId = $('inpDomainId').value;
	var info = ajaxLoadIndexInfo(indexId,domainId);
	if (info == null)
		return;
		
	removeAllChildren($('ulInfoList'));
	addToInfoListDOM("<b>Number of tags</b> : " + info["totalTags"]);
	var fieldList = info["fields"];
	for (var i = 0 ; i < fieldList.length; i++){
		var item = fieldList[i];
		addToInfoListDOM("<b>" + item["FieldName"] +"</b> : " + item["Count"]);
	}
}

function addToInfoListDOM(listItem){
	var domInfoList = $('ulInfoList');
	var item = document.createElement("li");
	item.innerHTML = listItem;
	domInfoList.appendChild(item);
}

function ajaxLoadIndexInfo(indexId,domainId){
	var options = {
	    method: "get",
	    parameters: {action:"summary",
	    			domainId: domainId,
	    			indexId : indexId},
	    asynchronous: false
	}
	var request = new Ajax.Request("/annieWeb/TaggingServlet", options);
	if (request.transport.responseText.match("failed") != null) {
		alert("Something wrong " + request.transport.responseText);
		return null; 
	}
	else
		return request.transport.responseText.evalJSON();
}

