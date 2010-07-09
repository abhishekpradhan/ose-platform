var GET_INDICES_SERVLET_URL = "/annieWeb/GetIndices"; 
var GET_QUERIES_SERVLET_URL = "/annieWeb/GetQueries";
var DOMAIN_ID = 2;

var gIndexId = null;
var gQueryId = null;

function init(){
	new Dialog.Box("dlgProgress");
}

function add_feature(groupName){
	var featureDiv = document.getElementById(groupName + "_features_list_div");
	if (!featureDiv){
		alert("Cannot find feature div " + featureDiv);
		return;
	}
	
	//count how many features already there
	var i = 0;
	while (i < 1000){
		var divElem = document.getElementById(groupName + "_feature_div_"+i);
		if (!divElem){			
			break;
		}
		i ++;
	}
	
	featureDiv.appendChild(get_feature_node( groupName, i ) );
	alert("New feature has been added (" + i + ")");
}

function get_feature_node(groupName, i){
	var featureNode = document.createElement("div");
	featureNode.id = groupName + "_feature_div_"+i;
	logMessage("ID " + featureNode.id);
	featureNode.innerHTML = 'Feature <b>' + (i+1) + ' </b><a href="javascript:remove_feature(\'' + featureNode.id + '\');">Remove</a><br>\
				<input type="text" name="weight_' + groupName + '_' + (i + 1) + '" size="6" value="1.0" onkeyup="updateFeatures()"><input type="text" name="template_' + groupName + '_' + (i + 1) + '"size="21" onkeyup="updateFeatures()"><br>';
	return featureNode;
}

function remove_feature(nodeId){
	var featureDiv = document.getElementById(nodeId);
	featureDiv.parentNode.removeChild(featureDiv);
	alert("Feature node " + nodeId + " has been removed");	
}

function toggleFeature(groupName){
	var featureDiv = document.getElementById(groupName + "_features_list_div");
	if (!featureDiv){
		alert("Cannot find feature div " + featureDiv);
		return;
	}
	
	var featureList = featureDiv.getElementsByTagName("input");
	for (var i = 0 ; i < featureList.length; i++){
		featureList[i].disabled = !featureList[i].disabled ;
	}
	updateFeatures();
}

function updateFeatures(){
	var featureString = "";
	var fieldNames = getFieldNames();
	for (var i = 0 ; i < fieldNames.length ; i++ ){
		featureString += getAllFeaturesInGroup(fieldNames[i],false) + " ";
	}
	
	document.getElementById('txtFeatureString').value = featureString;
}

/* return all features in group. If gropu is disable, return "" */
function getAllFeaturesInGroup(groupName, ignoreWeight){

	var featureDiv = document.getElementById(groupName + "_features_list_div");
	if (!featureDiv){
		alert("Cannot find feature div " + featureDiv);
		return;
	}

	var featureString = "";	
	var i = 0;
	while (i < 100){
		var divElem = document.getElementById(groupName + "_feature_div_"+i);
		i ++;
		if (!divElem){			
			break;
		}
		
		var inputElems = divElem.getElementsByTagName('input');
		for (var j = 0 ; j < inputElems.length; j++){
			if (inputElems[j].disabled) {
				return "";
			} 
			
			if ( !( ignoreWeight && inputElems[j].name.startsWith("weight") ) ){
				featureString += inputElems[j].value;
				featureString += " ";
			}
		}
		
		
	}
	
	return featureString;
}

function searchByFeatures(){
	var featureString = $('txtFeatureString').value;
	if (gIndexId == null){
		alert("No index is selected");
		return;
	}	

	featureString = validateQuery(featureString);
	if (featureString == null){
		return;
	}
	
	parent.frameResult.searchByFeatures(gIndexId, featureString);
}

function searchByFeaturesOnAnnotation(){
	var featureString = $('txtFeatureString').value;
	if (gIndexId == null){
		alert("No index is selected");
		return;
	}	

	featureString = validateQuery(featureString);
	if (featureString == null){
		return;
	}
	
	domainId = $('inpDomainId').value;
	parent.frameResult.searchByFeaturesOnAnnotation(domainId, gIndexId, featureString);
}


function searchLogistic(){
	
	var featureString = "";
	var fieldNames = getFieldNames();
	for (var i = 0 ; i < fieldNames.length; i++){
		var query = getAllFeaturesInGroup(fieldNames[i],true) ;
		if (query == "")
			query = "%Null()";		
		featureString += query + " ";
	}
	
	if (gIndexId == null){
		alert("No index is selected");
		return;
	}	

	featureString = validateQuery(featureString);
	if (featureString == null){
		return;
	}
	
	parent.frameResult.searchLogistic(gIndexId, DOMAIN_ID, featureString);
}

function searchAnnotation(){
	var oquery = {};
	var fieldNames = getFieldNames();
	for (var i = 0 ; i < fieldNames.length; i++){
		var fieldValue = $("inpField_" + fieldNames[i]).value;
		oquery[fieldNames[i]] = fieldValue;
	}
	
	if (gIndexId == null){
		alert("No index is selected");
		return;
	}	

	parent.frameResult.searchAnnotation(gIndexId, DOMAIN_ID, Object.toJSON(oquery));
}



function searchLucene(){
	var query = $("txtFeatureString").value;
	
	if (gIndexId == null){
		alert("No index is selected");
		return;
	}	

	parent.frameResult.searchLucene(gIndexId, DOMAIN_ID, query);
}

function validateQuery(featureString){
	var fieldNames = getFieldNames();
	for (var i = 0 ; i < fieldNames.length ; i++){
		var fieldMacroName = fieldNames[i].toUpperCase();
		if (featureString.match(fieldMacroName) != null){
			var fieldValue = $('inpField_' + fieldNames[i]).value.strip().toLowerCase();
			if (fieldValue.length == 0){
				alert("Field '" + fieldMacroName + "' is used in feature string but not given");
				return null;
			}
			featureString = featureString.gsub(fieldMacroName,fieldValue);
		}
	}
	return featureString;
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
    var listOfIndices = obj.responseText.evalJSON();
    var theSelect = $("selIndex");
    removeAllChildren(theSelect);
    for (var i = 0 ; i < listOfIndices.length ; i++){
        var newOption = document.createElement('option');
        newOption.text = listOfIndices[i]['Name'];
        newOption.id = "q_" + listOfIndices[i]['IndexId'];
        newOption.value = listOfIndices[i]['IndexId'];
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
    parent.frameInfoBox.setBoxInfo(gIndexId);
}

function loadFeatures(){
	
	domainId = $('inpDomainId').value;
	var options = {
	    method: "get",
	    parameters: {action:"GetFeaturesFromDomain",
	    			domainId:domainId
	    			},
	    asynchronous: false
	}
	$("dlgProgress").show();
	var request = new Ajax.Request("/annieWeb/FeatureInfoServlet", options);
	$("dlgProgress").hide();
	var response = request.transport.responseText.evalJSON();
	var listOfFields =response['result']; 
	for (var i = 0 ; i < listOfFields.length ; i++){
		var fieldName = listOfFields[i]["field"];
		var featureDiv = document.getElementById(fieldName + "_features_list_div");
		while (featureDiv.childNodes.length > 0){
			featureDiv.removeChild(featureDiv.childNodes[0]);
		}
		var features = listOfFields[i]["features"];
		for (var j = 0 ; j < features.length;j++){
			var featureId = features[j]["FeatureId"];
			var featureNode = get_feature_node(fieldName, j);
			var inputBoxes = featureNode.getElementsByTagName("input");
			if (inputBoxes.length != 2){
				alert("Can not find 2 input boxes in featureNode " + featureNode.id);
				return;
			}
			inputBoxes[0].value = features[j]["Weight"];
			inputBoxes[1].value = features[j]["Template"];
			featureDiv.appendChild(featureNode);
		}
	}
	updateFeatures();
	alert("Features loaded successfully");
}

function saveFeatures(){
	if (!validateInputFeatures()){
		return;
	}
	
	var fieldNames = getFieldNames();
	var fieldIds = getFieldIds();
	var domainId = $('inpDomainId').value;
	if (!ajaxRemoveExistingFeatures(domainId)){
		return;
	}
	$("dlgProgress").show();
	for (var i = 0 ; i < fieldNames.length ; i++){
		saveFeatureForField(domainId, fieldNames[i], fieldIds[i]);
	}
	alert("All features saved");
	$("dlgProgress").hide();
}


function validateInputFeatures(){
	var allInputElems = $$('input');
	for (var i=0 ; i < allInputElems.length; i++){
		if (allInputElems[i].name.startsWith("template") && allInputElems[i].value.strip() == ""){
			alert("Input element " + allInputElems[i].name + " is not valid");
			return false; 
		}
	}
	return true;
}

function saveFeatureForField(domainId, fieldName, fieldId){
	var featureDiv = $(fieldName + "_features_list_div");
	if (!featureDiv){
		return;
	}

	var i = 0;
	while (i < 100){
		var divElem = $(fieldName + "_feature_div_"+i);
		i ++;
		if (!divElem){			
			continue;
		}
		
		var weight = 1.0;
		var template = "";
		var inputElems = divElem.getElementsByTagName('input');
		for (var j = 0 ; j < inputElems.length; j++){
			if (inputElems[j].name.startsWith("weight")){
				weight = inputElems[j].value;
			}
			else{
				template = inputElems[j].value;
			}
		}
		if (! ajaxAddFeature(domainId, fieldId, template,weight)){
			return;
		}
		
	}
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
	var domainId = $("inpDomainId").value;
	options = { method:'get',
				parameters : {domainId : domainId}, 
	  			onSuccess: updateQuerySelectBox,
	  			onFailure: function(){ alert('Something went wrong...') },
				};
				
	$("dlgProgress").show();
	new Ajax.Request(GET_QUERIES_SERVLET_URL, options);
    
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
        newOption.id = "query_" + listOfQueries[i]['QueryId'];
        newOption.value = listOfQueries[i]['QueryString'];
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