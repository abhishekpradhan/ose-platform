var GET_INDICES_SERVLET_URL = "/annieWeb/GetIndices"; 
var GET_QUERIES_SERVLET_URL = "/annieWeb/GetQueries";
var EXPORT_ANNOTATION_SERVLET_URL = "/lbjseWeb/ExportDataServlet";
var gDOMAIN_ID = null;

var gIndexId = null;


function init(){
	gDOMAIN_ID = $('inpDomainId').value;
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

function get_feature_node(groupName, rank){
	var featureNode = document.createElement("div");
	featureNode.id = groupName + "_feature_div_"+rank;
	logMessage("ID " + featureNode.id);
	var optionString = "";
	var featureList = gFeatureList[groupName];
	for (var i = 0 ; i < featureList.length; i++){
		optionString += '<option value="' + featureList[i] + '">' + featureList[i] + '</option>';
	}
	
	featureNode.innerHTML = 'Feature <b>' + (rank+1) + ' </b><a href="javascript:remove_feature(\'' + featureNode.id + '\');">Remove</a><br>\
				<input type="text" name="weight_' + groupName + '_' + (rank + 1) + '" size="6" value="1.0" onkeyup="updateFeatures()">	\
				<select size="1" name="template_' + groupName + '_' + (rank + 1) + '" onchange="updateFeatures()">'
	            		+ optionString + 
	            '</select><br>';
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
			
			
			if ( inputElems[j].name.startsWith("weight_") ) {
				var selectElemName = "template_" + inputElems[j].name.substring(7);
				
				if (! ignoreWeight ) {
					featureString += inputElems[j].value;
					featureString += " ";
				}
				
				var selectElem = document.getElementsByName(selectElemName);
				if (selectElem.length == 1){
					featureString += selectElem[0].value;
					featureString += " ";
				}
				else{
					alert("Ambiguous select element");
				}
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

	parent.frameResult.searchAnnotation(gIndexId, gDOMAIN_ID, Object.toJSON(oquery));
}

function searchLBJ(){
	
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

	parent.frameResult.searchLBJ(gIndexId, gDOMAIN_ID, Object.toJSON(oquery));
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
	    			domainId:gDOMAIN_ID
	    			},
	    asynchronous: false
	}
	$("dlgProgress").show();
	var request = new Ajax.Request("/lbjseWeb/FeatureInfoServlet", options);
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
			if (inputBoxes.length != 1){
				alert("Can not find the input boxes in featureNode " + featureNode.id);
				return;
			}
			inputBoxes[0].value = features[j]["Weight"];
			
			var selectBoxes = featureNode.getElementsByTagName("select");
			if (selectBoxes.length != 1){
				alert("Can not find the select box in featureNode " + featureNode.id);
				return;
			}
			selectBoxes[0].value = features[j]["Template"];
			
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
	if (!ajaxRemoveExistingFeatures(gDOMAIN_ID)){
		return;
	}
	$("dlgProgress").show();
	for (var i = 0 ; i < fieldNames.length ; i++){
		saveFeatureForField(gDOMAIN_ID, fieldNames[i], fieldIds[i]);
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
		var selectElems = divElem.getElementsByTagName('select');
		weight = inputElems[0].value;
		template = selectElems[0].value;
			
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
	var request = new Ajax.Request("/lbjseWeb/FeatureInfoServlet", options);
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
	var request = new Ajax.Request("/lbjseWeb/FeatureInfoServlet", options);
	if (request.transport.responseText.match("ok") == null){
		alert("Something wrong" + request.transport.responseText);
		return false;
	}
	else
		return true;
}

function loadQueries(){	
	options = { method:'get',
				parameters : {domainId : gDOMAIN_ID}, 
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

function exportAnnotation(){
	var selectBox = $("selIndex");
	var selectedOption = selectBox.options[selectBox.selectedIndex]
    var indexId = selectedOption.value;
    var url = EXPORT_ANNOTATION_SERVLET_URL + "?indexId=" + indexId + "&domainId=" + gDOMAIN_ID;
    window.open(url, "_blank");
}