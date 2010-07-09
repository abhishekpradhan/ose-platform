var LEARNER_SERVLET_URL="/annieWeb/LearnerServices?";

var gDatasetId=-1;
var gSessionId=-1;
var gListOfSessions=null;
var gNumberFeatures = 100;

function init(){
	initYUI();
}

function initYUI(){
	/* Initialize the temporary Panel to display while waiting for external content to load */
    YAHOO.namespace("example.container");
	YAHOO.example.container.wait =  
	        new YAHOO.widget.Panel("wait",   
	            { width:"240px",  
	              fixedcenter:true,  
	              close:false,  
	              draggable:false,  
	              zindex:4, 
	              modal:true, 
	              visible:false 
	            }  
	        ); 

	YAHOO.example.container.wait.setHeader("Loading, please wait..."); 
	YAHOO.example.container.wait.setBody('<img src="http://us.i1.yimg.com/us.yimg.com/i/us/per/gr/gp/rel_interstitial_loading.gif" />'); 
	YAHOO.example.container.wait.render(document.body); 
	
	
	YAHOO.example.TableTrainingSession = function() {
        var myColumnHeaders = [
        	{key:"name", label:"Name",  sortable:true},
            {key:"value", label:"Session Info",  sortable:true},
        ];
 		var myColumnSet = new YAHOO.widget.ColumnSet(myColumnHeaders);
        var myDataSource = new YAHOO.util.DataSource(LEARNER_SERVLET_URL);
        myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSON;
        myDataSource.responseSchema = {
        	resultsList: "items",
            fields: ["name","value"]
        };
        
        var myDataTable = new YAHOO.widget.DataTable("DivTrainingSessionTable",
                myColumnSet, myDataSource, {initialLoad:false});
                
        return {
            oDS: myDataSource,
            oDT: myDataTable
        };
    }();
    
    /*
    Example code to manipulate yui table
    var a = [{"name":"DomainId","value":1},{"name":"FieldId","value":1}];
    YAHOO.example.TableTrainingSession.oDT.addRow(a[0],0);
    var dt = YAHOO.example.TableTrainingSession.oDT;
    dt.deleteRows(dt.getRecordSet().getLength()-1,-1);    
    //dt.initializeTable(); 
	//dt.render(); 
	YAHOO.example.TableTrainingSession.oDT.addRow(a[1],0);
	*/
    
	YAHOO.example.TableCurrentFeatures = function() {
        var myColumnHeaders = [
            {key:"Value",label : "Current Features", /* formatter:YAHOO.example.TableCurrentFeatures.formatFeature, */ sortable:true},
        ];
 		var myColumnSet = new YAHOO.widget.ColumnSet(myColumnHeaders);
        var myDataSource = new YAHOO.util.DataSource(LEARNER_SERVLET_URL);
        myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSON;
        myDataSource.responseSchema = {
        	resultsList: "items",
            fields: ["FeatureId","Value"]
        };
        
        var myDataTable = new YAHOO.widget.DataTable("DivCurrentFeatureTable",
                myColumnSet, myDataSource, {initialLoad:false});
        
        myDataTable.subscribe("dataReturnEvent", function(oArgs){
            YAHOO.example.container.wait.hide(); 
        });
        
        var sendQuery = function(sessionId){
            YAHOO.example.container.wait.show(); 
            var callback = { 
	            success : this.oDT.onDataReturnInitializeTable, 
	            failure : function () { alert("something wrong"); }, 
	            scope : this.oDT
	        }; 
            this.oDS.sendRequest("opt=getSearchFeature&sessionId=" + sessionId, callback); 
        };      
        
        return {
            oDS: myDataSource,
            oDT: myDataTable,  
            sendQuery : sendQuery          
        };
    }();
    
    YAHOO.example.TableQueryValues = function() {
        var myColumnHeaders = [
            {key:"Value", /* formatter:YAHOO.example.TableCurrentFeatures.formatFeature, */ sortable:true},
        ];
 		var myColumnSet = new YAHOO.widget.ColumnSet(myColumnHeaders);
        var myDataSource = new YAHOO.util.DataSource(LEARNER_SERVLET_URL);
        myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSON;
        myDataSource.responseSchema = {
        	resultsList: "items",
            fields: ["Id","Value"]
        };
        
        var myDataTable = new YAHOO.widget.DataTable("DivQueryValues",
                myColumnSet, myDataSource, {initialLoad:false});
        
        myDataTable.subscribe("dataReturnEvent", function(oArgs){
            YAHOO.example.container.wait.hide(); 
        });
        
        var sendQuery = function(sessionId){
            YAHOO.example.container.wait.show(); 
            var callback = { 
	            success : this.oDT.onDataReturnInitializeTable, 
	            failure : function () { alert("something wrong"); }, 
	            scope : this.oDT
	        }; 
            this.oDS.sendRequest("opt=getQueryValue&sessionId=" + sessionId, callback); 
        };     
        
        return {
            oDS: myDataSource,
            oDT: myDataTable,  
            sendQuery : sendQuery
        };
    }();
    
    YAHOO.example.TableFeatureTemplates = function() {
        var myColumnHeaders = [
            {key:"Template", label:"Feature Templates", /* formatter:YAHOO.example.TableCurrentFeatures.formatFeature, */ sortable:true},
        ];
 		var myColumnSet = new YAHOO.widget.ColumnSet(myColumnHeaders);
        var myDataSource = new YAHOO.util.DataSource(LEARNER_SERVLET_URL);
        myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSON;
        myDataSource.responseSchema = {
        	resultsList: "items",
            fields: ["Id","Template"]
        };
        
        var myDataTable = new YAHOO.widget.DataTable("DivFeatureTemplates",
                myColumnSet, myDataSource, {initialLoad:false});
        
        myDataTable.subscribe("dataReturnEvent", function(oArgs){
            YAHOO.example.container.wait.hide(); 
        });
        
        var sendQuery = function(sessionId){
            YAHOO.example.container.wait.show(); 
            var callback = { 
	            success : this.oDT.onDataReturnInitializeTable, 
	            failure : function () { alert("something wrong"); }, 
	            scope : this.oDT
	        }; 
            this.oDS.sendRequest("opt=getFeatureTemplate&sessionId=" + sessionId, callback); 
        };
              
        return {
            oDS: myDataSource,
            oDT: myDataTable,  
            sendQuery : sendQuery
        };
    }();
    
    YAHOO.example.formatPosNegFeatures = function(elCell, oRecord, oColumn, oData) {
    	if (oRecord.getData().posCount > oRecord.getData().negCount && oColumn.key == "posCount") {
    		elCell.innerHTML =  "<b>" + oData+ "</b>";      	
    	}
    	else if (oRecord.getData().posCount < oRecord.getData().negCount && oColumn.key == "negCount") {
    		elCell.innerHTML =  "<b>" + oData+ "</b>";      	
    	}
    	else {
    		elCell.innerHTML =  oData;      	
    	}
    };
    
    YAHOO.example.TableRankedFeatures = function() {
        var myColumnHeaders = [
        	{key:"score", label:"Score",  sortable:true},
            {key:"feature", label:"Feature",  sortable:true},
            {key:"posCount", label:"Positive", formatter:YAHOO.example.formatPosNegFeatures, sortable:true},
            {key:"negCount", label:"Negative", formatter:YAHOO.example.formatPosNegFeatures, sortable:true},
        ];
 		var myColumnSet = new YAHOO.widget.ColumnSet(myColumnHeaders);
        var myDataSource = new YAHOO.util.DataSource(LEARNER_SERVLET_URL);
        myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSON;
        myDataSource.responseSchema = {
        	resultsList: "items",
            fields: ["score","feature", "posCount","negCount"]
        };
        
        var myDataTable = new YAHOO.widget.DataTable("DivRankedFeaturesTable",
                myColumnSet, myDataSource, {initialRequest:"opt=getRankedFeatures&sessionId=1&dataId=1"});
           
        myDataTable.subscribe("dataReturnEvent", function(oArgs){
            YAHOO.example.container.wait.hide(); 
        });
             
        var sendQuery = function(sessionId, dataId){
            YAHOO.example.container.wait.show(); 
            var callback = { 
	            success : this.oDT.onDataReturnInitializeTable, 
	            failure : function () { alert("something wrong"); }, 
	            scope : this.oDT
	        }; 
            this.oDS.sendRequest(
            		"opt=getRankedFeatures"            		
            		+ "&sessionId=" + sessionId 
            		+ "&dataId=" + dataId
            		+ "&nresult=" + gNumberFeatures
            		, callback); 
        };
        
        return {
            oDS: myDataSource,
            oDT: myDataTable,  
            sendQuery : sendQuery
        };
    }();
    
    
}

function loadData(){
	options = { method:'get',parameters:{'opt':'listData'},
	  			onSuccess: updateDataSelectBox,
	  			onFailure: function(){ alert('Something went wrong...') },
				};
	YAHOO.example.container.wait.show();
	new Ajax.Request(LEARNER_SERVLET_URL, options);
}

function updateDataSelectBox(obj){
	YAHOO.example.container.wait.hide();
    logMessage("Got Indices " + obj.responseText);
    var listOfDataSets = obj.responseText.evalJSON();
    var theSelect = $("selData");
    removeAllChildren(theSelect);
    for (var i = 0 ; i < listOfDataSets.length ; i++){
        var newOption = document.createElement('option');
        newOption.text = listOfDataSets[i]['Path'];
        newOption.id = "ds_" + listOfDataSets[i]['Id'];
        newOption.value = listOfDataSets[i]['Id'];
        newOption.selected = true;
        theSelect.add(newOption, null);
        logMessage("Added dataset " + newOption);
    }
    selectData();
}

function selectData(){
    var selectBox = $("selData");
    var selectedOption = selectBox.options[selectBox.selectedIndex]
    gDatasetId = selectedOption.value;
    logMessage("Selecting dataset " + gDatasetId);
    refreshRankedFeatures();
}

function refreshRankedFeatures(){
	if (gDatasetId == -1 || gSessionId == -1){
		alert("No data or session selected");
		return;
	}
	$('linkResetCache').href = LEARNER_SERVLET_URL  
		+ "opt=getRankedFeatures"
		+ "&nocache=yes"
		+ "&sessionId=" + gSessionId 
		+ "&dataId=" + gDatasetId
		+ "&nresult=" + gNumberFeatures;	
	YAHOO.example.TableRankedFeatures.sendQuery(gSessionId, gDatasetId);
}

function loadSession(){
	options = { method:'get',parameters:{'opt':'listTrainingSession'},
	  			onSuccess: updateSessionSelectBox,
	  			onFailure: function(){ alert('Something went wrong...') },
				};
	YAHOO.example.container.wait.show();
	new Ajax.Request(LEARNER_SERVLET_URL, options);
}

function updateSessionSelectBox(obj){
	YAHOO.example.container.wait.hide();
    logMessage("Got Session " + obj.responseText);
    var listOfSessions = obj.responseText.evalJSON();
    var theSelect = $("selSession");
    removeAllChildren(theSelect);
    for (var i = 0 ; i < listOfSessions.length ; i++){
        var newOption = document.createElement('option');
        newOption.text = listOfSessions[i]['Description'];
        newOption.id = "ds_" + listOfSessions[i]['SessionId'];
        newOption.value = listOfSessions[i]['SessionId'];
        newOption.selected = true;
        theSelect.add(newOption, null);
        logMessage("Added session " + listOfSessions[i]);
    }
    gListOfSessions = listOfSessions;
    
    selectSession();
}

function selectSession(){
    var selectBox = $("selSession");
    var selectedOption = selectBox.options[selectBox.selectedIndex]
    gSessionId = selectedOption.value;
    logMessage("Selecting session " + gSessionId);
     
    var session = getSession(gSessionId);
    showInfoOnTable(session, YAHOO.example.TableTrainingSession.oDT);  
	YAHOO.example.TableCurrentFeatures.sendQuery(gSessionId);
    YAHOO.example.TableQueryValues.sendQuery(gSessionId);
    YAHOO.example.TableFeatureTemplates.sendQuery(gSessionId);
    
}

function showInfoOnTable(session, dataTable){
	dataTable.deleteRows(dataTable.getRecordSet().getLength()-1,-1);
	dataTable.initializeTable();
	dataTable.render();
	dataTable.addRow({"name":"Session ID", "value" : session['SessionId']}, 0);
	dataTable.addRow({"name":"Domain ID", "value" : session['DomainId']}, 0);
	dataTable.addRow({"name":"Field ID", "value" : session['FieldId']}, 1);
	dataTable.addRow({"name":"FG Class", "value" : session['FeatureGeneratorClass']}, 2);
	dataTable.addRow({"name":"Ranker Class", "value" : session['ClassifierClass']}, 3);
	if (session['CurrentPerformance'] != undefined && session['CurrentPerformance']['accuracy'] != undefined)
		dataTable.addRow({"name":"Test Accuracy", "value" : session['CurrentPerformance']['accuracy']}, 4);
	
}

function getSession(sessionId){
	for (var i = 0; i < gListOfSessions.length; i++){
		if (gListOfSessions[i]['SessionId'] == sessionId){
			return gListOfSessions[i];
		}
	}
	return null;
}

function addFieldValue(){
	var fieldValue = $("txtFieldValue").value.trim();
	if (fieldValue.length == 0)
		return;
	
	var options = {
	    method: "get",
	    parameters: {opt:"addQueryValue",
	    			SessionId:gSessionId,
	    			Value:fieldValue},
	    asynchronous: false
	}
	var request = new Ajax.Request(LEARNER_SERVLET_URL, options);
	if (request.transport.responseText == "failed") {
		alert("Something wrong " + request.transport.responseText);
	}
	else{
		$("txtFieldValue").value = "";
		selectSession();
	}
}

function addSearchFeature(){
	var feature = $("txtSearchFeature").value.trim();
	if (feature.length == 0)
		return;
	
	var options = {
	    method: "get",
	    parameters: {opt:"addSearchFeature",
	    			SessionId:gSessionId,
	    			Value:feature},
	    asynchronous: false
	}
	var request = new Ajax.Request(LEARNER_SERVLET_URL, options);
	if (request.transport.responseText == "failed") {
		alert("Something wrong " + request.transport.responseText);
	}
	else{
		$("txtSearchFeature").value = "";
		selectSession();
	}
}

function addFeatureTemplate(){
	var feature = $("txtFeatureTemplate").value.trim();
	if (feature.length == 0)
		return;
	
	var options = {
	    method: "get",
	    parameters: {opt:"addFeatureTemplate",
	    			SessionId:gSessionId,
	    			Template:feature},
	    asynchronous: false
	}
	var request = new Ajax.Request(LEARNER_SERVLET_URL, options);
	if (request.transport.responseText == "failed") {
		alert("Something wrong " + request.transport.responseText);
	}
	else{
		$("txtFeatureTemplate").value = "";
		selectSession();
	}
}

function changeNumberOfFeatures(){
	var selectBox = $("selNumberFeatures");
    var selectedOption = selectBox.options[selectBox.selectedIndex]
    gNumberFeatures = selectedOption.value;
    logMessage("Displaying " + gNumberFeatures);
}