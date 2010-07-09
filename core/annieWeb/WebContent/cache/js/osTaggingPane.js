var TAGGING_SERVLET = "/annieWeb/TaggingServlet";

var gIndexId = null;
var gDocId = null;
var lastTagsForField = {}

function addAllTags(){
	fieldNames = getFieldNames()
	for (var i = 0 ; i < fieldNames.length ; i++){
		var fieldName = fieldNames[i];
		var tag = $(fieldName + '_txtTag');
		if ( !(tag.value == null || tag.value.length == 0)){
			addTag(fieldName,tag.value);
			tag.value = "";
		}		
	}
}

function addTag(fieldName, tag){
	 
	if (tag == null || tag.length == 0){
		alert("Empty tag");
		return;
	}
	var fieldId = getFieldIdFromName(fieldName);
	var fieldType = getFieldTypeFromId(fieldId);
	tag = tag.toLowerCase(); //lower case 
	var tagList = [];
	if (fieldType == "text"){ //don't tokenize if this field is "text" field
		tagList = [tag];
	}
	else{
		tag = tag.replace(/,/g,""); //remove "," for numbers like price
		tagList = tag.split(/[^a-zA-Z0-9.,]/);
	}
	
	for (var i = 0 ; i < tagList.length; i++){
		var tagToken = tagList[i];
		if (tagToken.length == 0) continue;
		var tagId = ajaxAddTagToField(gIndexId, gDocId, fieldName, tagToken);
		if (tagId == null){
			continue;
		}
		addTagToDOM(tagId, tagToken, fieldName);		
	}
	lastTagsForField[fieldName] = tag;	
}

/*localId is the item number on the same result page, is used to enable Previous/Next link */
function setTaggingItem(localId, indexId, item){
	gIndexId = indexId;
	gDocId = item["DocId"];
	clearTagInfo();
	$('anchorCurrentPage').href = item["Url"];
	$('anchorCacheLink').href = item["CacheUrl"] + "&nofilter";
	$('previousLink').href = "javascript:parent.frameResult.openResult("+(localId-1) + ")";
	$('nextLink').href = "javascript:parent.frameResult.openResult("+(localId+1) + ")";
	$('anchorCurrentPage').innerHTML = item["Title"];
	$('spanDocId').innerHTML = item["DocId"];
	$('spanScore').innerHTML = item["Score"];
	$('debugDiv').innerHTML = item["Features"];
	loadTagInfoForDocument(gIndexId, gDocId);	
}

function ajaxAddTagToField(indexId, docId, fieldName, tagValue){
	var options = {
	    method: "get",
	    parameters: {action: "add",
	    			indexId: indexId,
	    			docId: docId, 
	    			fieldId: getFieldIdFromName(fieldName), 
	    			value: tagValue},
	    asynchronous: false
	}

	var request = new Ajax.Request(TAGGING_SERVLET, options);
	if (request.transport.responseText.match("failed") != null) {
		alert("Something wrong " + request.transport.responseText);
		return null; 
	}
	else
		return request.transport.responseText.substring("3").strip();
}

function clearTagInfo(){
	var fieldNames = $('inpFieldNames').value.split(',');
	for (var i = 0 ; i < fieldNames.length ; i++){
		removeAllChildren($(fieldNames[i] + "_span"));
	}
}


function loadTagInfoForDocument(indexId, docId){
	var tags = ajaxLoadDocTagForDocument(indexId,docId);
	if (tags == null)
		return;
	for (var i = 0 ; i < tags.length ; i++){
		var fieldId = tags[i]["FieldId"];
		var tagValue = tags[i]["Value"];
		var tagId = tags[i]["TagId"];
		addTagToDOM(tagId, tagValue, getFieldNameFromId(fieldId));
	}
}

function ajaxLoadDocTagForDocument(indexId, docId){
	var options = {
	    method: "get",
	    parameters: {action: "query",
	    			indexId: indexId,
	    			docId: docId},
	    asynchronous: false
	}

	var request = new Ajax.Request(TAGGING_SERVLET, options);
	if (request.transport.responseText.match("failed") != null) {
		alert("Something wrong " + request.transport.responseText);
		return null; 
	}
	else
		return request.transport.responseText.evalJSON();
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

function addTagToDOM(tagId, tagValue, fieldName){
	var span = $(fieldName + "_span");
	var tagLink = document.createElement("a")
	tagLink.id = "anchor_tag_" + tagId;
	tagLink.href = "javascript:removeTag('" + tagId + "')";
	tagLink.innerHTML = tagValue;
	span.appendChild(tagLink);
	span.appendChild(document.createTextNode(" , "));
}

function removeTag(tagId){
	if (!ajaxRemoveTag(tagId))
		return;
		
	var thisTag = $("anchor_tag_" + tagId)
	var tagValue = thisTag.innerHTML;
	var parentId = thisTag.parentNode.id;
	thisTag.parentNode.removeChild(thisTag);
	alert("Tag '" + tagValue + "' has been removed from group " + parentId);	
}

function ajaxRemoveTag(tagId){
	var options = {
	    method: "get",
	    parameters: {action: "delete",
	    			tagId: tagId},
	    asynchronous: false
	}
	
	var request = new Ajax.Request(TAGGING_SERVLET, options);
	if (request.transport.responseText.match("failed") != null) {
		alert("Something wrong " + request.transport.responseText);
		return false; 
	}
	else
		return true;
}

/* TODO : move to osBuilderUtils.js */
function getFieldNames(){
	return $('inpFieldNames').value.split(',');
}

function setTextForField(fieldName, text){
	$(fieldName + '_txtTag').value = text;
}

function setLastTags(fieldName){
	var tags = lastTagsForField[fieldName];
	$(fieldName + '_txtTag').value = tags;
}

function tagAllResult(){
	var currentDocId = gDocId;
	var resultPageDocIds = parent.frameResult.getAllDocIds();
	logMessage("Adding tags for " + resultPageDocIds);
	fieldNames = getFieldNames()
	for (var i = 0 ; i < fieldNames.length ; i++){
		var fieldName = fieldNames[i];
		var tag = $(fieldName + '_txtTag');
		if ( !(tag.value == null || tag.value.length == 0)){
			for (var j = 0 ; j < resultPageDocIds.length ; j ++){
				gDocId = resultPageDocIds[j];
				addTag(fieldName,tag.value);
			}
		}		
	}	
}