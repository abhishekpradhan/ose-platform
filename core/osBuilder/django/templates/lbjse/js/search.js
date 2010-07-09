//Global variables
var myDataTable;
var gServerTime;
var SERVLET_URL = "/annieWeb/ObjectSearchServlet?";
var GET_QUERIES_SERVLET_URL = "/annieWeb/GetQueries";  
var GET_INDICES_SERVLET_URL = "/annieWeb/GetIndices";  
var FEEDBACK_SERVLET_URL = "/annieWeb/FeedbackServlet";  
 

var gQueryId = -1;
var gIndexId = null;

var gRelevantDocs = null;
var gNonRelevantDocs = null;
var gRawResponseData = null;

/****************************************************************************/

function init(){
    initYUIComponents();
    renderMyPageTable();
    touchQueryForm(); //to pickup pre-filled data by browser
};

function touchQueryForm(){
	var fieldNames = $("inpFieldNames").value.split(",");	
	for (var i = 0 ; i < fieldNames.length ; i++){
		var fieldName = fieldNames[i];
		if ($(fieldName.toUpperCase())){
			//$(fieldName.toUpperCase()).show(); 
			//$(fieldName.toUpperCase()).fire();
		}
	}
}

function initYUIComponents(){
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
}

/****************************************************************************/


function trimTitle(title) {
    var my_text = "";

    var max_length = 90;
    if (!title) {
	my_text = "untitled";
    } else {
    	if (title.length <= max_length) {
	    my_text = title;
	} else {
	   my_text = title.substring(0, max_length - 1) + "...";
        }
    }
    
    return my_text;
}

var TitleTemplate = new Template(
   '<h2 class="ostitle"> \
    <a target="frameWebpage" href="#{CACHE_URL}">#{TITLE}</a></h2>\
    <table border="0" cellpadding="0" cellspacing="0"> \
        <tr> \
            <td class="j">#{DESCRIPTION}<br> <span class="a">#{DOMAIN} - </span> \
            <nobr> \
            <a class="fl" target="_blank" href="#{URL}"> Original</a> \
            </nobr></font>\
            </td> \
        </tr> \
    </table>');

var FeedbackTemplate = new Template(
    "<input type=\"checkbox\" name=\"relevant\" #{RELEVANT} value=\"#{ID}\"><b>#{QUESTION}</b>"
);

/****************************************************************************/

function renderMyPageTable() {
	var formatTitle = function(elCell, oRecord, oColumn, sData) { 
        var url = oRecord.getData("Url");
		elCell.innerHTML = TitleTemplate.evaluate(
            {"TITLE":trimTitle(sData),
            "URL" : url,
            "DESCRIPTION" : oRecord.getData("Features"),
            "DOMAIN" : getDomainFromUrl(url),
            "CACHE_URL" : oRecord.getData("CacheUrl"), 
            } 
            ) ;
    };

	var formatFeedbackBox = function(elCell, oRecord, oColumn, sData) {
        var docId = oRecord.getData("DocId") ;
        var relevant = "";
        var question = "";
        if (gRelevantDocs != null && gRelevantDocs.indexOf(docId) != -1)
            relevant = "checked";
        else if (gNonRelevantDocs != null && gNonRelevantDocs.indexOf(docId) == -1){
            question = "?";

        }
        elCell.innerHTML = FeedbackTemplate.evaluate(
            {"ID": docId, "RELEVANT": relevant, "QUESTION" : question}
        );
    };
       
    var onRowClickEvent = function(oArgs){
        var ip = oArgs.target.getElementsByTagName("input");
        ip[0].checked = ! ip[0].checked;
    }


    /* This holds the table with ClientPagination information */
    YAHOO.example.ClientPagination = new function() { 

        var myColumnHeaders = [
        {key: "No", label: "No", formatter: null, sortable:true, width : "10px"},
        {key: "DocId", label: "DocID", formatter: null, sortable: true, width : "10px"},
        {key: "Score", label: "Score", formatter: null, sortable: true, width : "10px"},
        {key: "Title", label: "Title", formatter: formatTitle, width : "auto"},        
        {key: "NOEXISTENCE", label: "OK?", formatter: formatFeedbackBox, sortable: false, width : "auto"},
        ];

        var myColumnSet = new YAHOO.widget.ColumnSet(myColumnHeaders);
        this.myDataSource = new YAHOO.util.DataSource(SERVLET_URL);
        this.myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSON;
        this.myDataSource.responseSchema = {
            resultsList: "records",
            fields: ["No", "DocId", "Score", "Title", "Url", "Features", "CacheUrl" ],
			metaFields: { 
				totalRecords: "totalRecords", // Access to value in the server response 
				recordsReturned: "recordsReturned",
				evaluation: "evaluation" 
			} 
        };

		this.myRequestBuilder = function(oState, oSelf) {
		    // Get states or use defaults
		    oState = oState || {pagination:null, sortedBy:null};
		    var sort = (oState.sortedBy) ? oState.sortedBy.key : "myDefaultColumnKey";
		    var dir = (oState.sortedBy && oState.sortedBy.dir === DT.CLASS_DESC) ? "false" : "true";
		    var startIndex = (oState.pagination) ? oState.pagination.recordOffset : 0;
		    var results = (oState.pagination) ? oState.pagination.rowsPerPage : 100;
		    
		    // Build custom request
		    return  "start=" + startIndex +
		            "&nRecords=" + results + 
		            "&" + oSelf.getQuery;
		};

		
        var oConfigs = {        	
        	initialLoad:false,
        	dynamicData: true,
        	paginator: new YAHOO.widget.Paginator({ 
					rowsPerPage: 10, 
					//totalRecords   : myData.length, // OPTIONAL 
					// use a custom layout for pagination controls 
					//template       : "{PreviousPageLink} <strong>{CurrentPageReport}</strong> {NextPageLink} {PageLinks} Show {RowsPerPageDropdown} per page", 
 					// show all links 
					//pageLinks : YAHOO.widget.Paginator.VALUE_UNLIMITED, 
					// use these in the rows-per-page dropdown 
					rowsPerPageOptions : [5,10,20,50,100], 
					// use custom page link labels 
					pageLabelBuilder : function (page,paginator) { 
						var recs = paginator.getPageRecords(page); 
						return (recs[0] + 1) + ' - ' + (recs[1] + 1); 
					} 		
			}),
			generateRequest : this.myRequestBuilder
					  
        }; 
        		
        this.myDataTable = new YAHOO.widget.DataTable("MyPageTableDiv", myColumnSet, this.myDataSource, oConfigs);        
        /* Subscribe to events for row selection  */
        this.myDataTable.subscribe("rowMouseoverEvent", this.myDataTable.onEventHighlightRow); 
        this.myDataTable.subscribe("rowMouseoutEvent", this.myDataTable.onEventUnhighlightRow); 
        this.myDataTable.subscribe("rowClickEvent", onRowClickEvent); 
        this.myDataTable.subscribe("dataReturnEvent", function(oArgs){
            YAHOO.example.container.wait.hide(); 
        });
        
     	this.myDataTable.handleDataReturnPayload = function(oRequest, oResponse, oPayload) {
     		if (!oPayload)
     			oPayload = {};
        	oPayload.totalRecords = oResponse.meta.totalRecords;
        	updateEvaluationBox(oResponse.meta);        	
        	return oPayload;
    	}
    	
        this.myDataSource.doBeforeCallback = function(oRequest, oFullResponse, oParsedResponse) {            
            updateEvaluationBox(oFullResponse); 
            return oParsedResponse; 
		}; 

        this.sendQuery = function(query){
        	this.myDataTable.getQuery = query;
        	//alert("Sending query " + getQuery );
            YAHOO.example.container.wait.show(); 
            var callback = { 
	            success : this.myDataTable.onDataReturnInitializeTable, 
	            failure : function () { alert("something wrong"); }, 
	            scope : this.myDataTable 
	        }; 
            this.myDataSource.sendRequest("nRecords=100&"+this.myDataTable.getQuery , callback); 
        };        
    };
}

function submitSearchQuery(){
	var domainId = $("inpDomainId").value;
	if (gIndexId == null){
		alert("Please select an index");
		return;
	}
	if (gQueryId != -1){
		YAHOO.example.ClientPagination.sendQuery("queryId=" + gQueryId + "&domainId=" + domainId + "&indexId=" + gIndexId + "&oquery=" + getObjectQuery());
	}
	else
		YAHOO.example.ClientPagination.sendQuery("domainId=" + domainId + "&indexId=" + gIndexId + "&oquery=" + getObjectQuery());
}



function getConstraint(lower,upper){
    if (lower == "" || upper == "")
        return "";
    else 
        return "_range(" + lower + "," + upper + ")";
}

function submitFeedbackData(){
    if (gQueryId == -1){
        alert("You must select a query from the query box");
        return;
    }
    var theForm = $("annieForm");
    var relevantDocIDs = "";
    var nonRelevantDocIDs = "";
    var countRelevant = 0;
    for (var i = 0 ; i < theForm.relevant.length;i++){
        if (theForm.relevant[i].checked){
            relevantDocIDs += "_" + theForm.relevant[i].value;
            countRelevant += 1;
        }
        else{
            nonRelevantDocIDs += "_" + theForm.relevant[i].value;
        }
    }
    logMessage("Relevant " + relevantDocIDs);
    logMessage("Non Relevant " + nonRelevantDocIDs);

    if (!confirm('Do you want to submit a feedback for query ' + gQueryId + ' index ' + gIndexId + ' with ' + countRelevant + ' relevant docs and ' + (theForm.relevant.length - countRelevant) + ' non relevant docs?')) {
        return;
    }
    
    var callback = {  
        success: function(o) {
            YAHOO.example.container.wait.hide(); 
        },  
        failure: function(o) {  
            alert("Something is wrong");
            YAHOO.example.container.wait.hide(); 
        }  
    }  

	var domainId = $("inpDomainId").value;
    YAHOO.example.container.wait.show(); 	
    var transaction = YAHOO.util.Connect.asyncRequest('GET', FEEDBACK_SERVLET_URL + "?domainId=" + domainId + "&queryId=" + gQueryId + "&indexId=" + gIndexId + "&positive=" + relevantDocIDs + "&negative=" + nonRelevantDocIDs, callback, null);  
        
}


function selectQuery(){
    var selectBox = $("selQuery");
    var selectedOption = selectBox.options[selectBox.selectedIndex]
    gQueryId = selectedOption.id.substring(6);
    var query = selectedOption.value.evalJSON();
    logMessage("Query : " + query);
    for (key in query){
    	handleFieldValue(key,query[key]);
    }
}

function selectIndex(){
    var selectBox = $("selIndex");
    var selectedOption = selectBox.options[selectBox.selectedIndex]
    gIndexId = selectedOption.value;
    logMessage("Selecting index " + gIndexId);
}

function updateEvaluationBox(responseObject){
	if (responseObject.evaluation){
	    gRawResponseData = responseObject;
	    gRelevantDocs = responseObject.evaluation.relevantDocs;
	    gNonRelevantDocs = responseObject.evaluation.nonRelevantDocs;
	    $("evalQueryId").innerHTML = responseObject.evaluation.queryId;
	    $("avgPrecision").innerHTML = responseObject.evaluation.averagePrecision;
	    $("nRelevant").innerHTML = gRelevantDocs.length;
	    $("scratchBox").value = "" + responseObject.evaluation.relevantPosition;
	}
}

function submitSearchQueryAndJumpTo(){
    var jumpTo = parseInt($("txtJumpTo").value);
    if (gLastType == 'professor'){
        submitSearchProfessorQueryFromIndex(jumpTo); 
    } else if (gLastType == 'camera'){
        submitCameraSearchQueryFromIndex(jumpTo); 
    } else {
        submitBaselineSearchQueryFromIndex(jumpTo);
    }
}

function loadIndices(){
	options = { method:'get',
	  			onSuccess: updateIndexSelectBox,
	  			onFailure: function(){ alert('Something went wrong...') },
				};
	YAHOO.example.container.wait.show();
	new Ajax.Request(GET_INDICES_SERVLET_URL, options);
    
}


function updateIndexSelectBox(obj){
	YAHOO.example.container.wait.hide();
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

function loadQueries(){
	var domainId = $("inpDomainId").value;
	options = { method:'get',
				parameters : {domainId : domainId}, 
	  			onSuccess: updateQuerySelectBox,
	  			onFailure: function(){ alert('Something went wrong...') },
				};
	YAHOO.example.container.wait.show();
	new Ajax.Request(GET_QUERIES_SERVLET_URL, options);
    
}

function updateQuerySelectBox(obj){
	YAHOO.example.container.wait.hide();
    logMessage("Got Queries " + obj.responseText);
    var listOfQueries = obj.responseText.evalJSON();
    var theSelect = $("selQuery");
    removeAllChildren(theSelect);
    for (var i = 0 ; i < listOfQueries.length ; i++){
        var newOption = document.createElement('option');
        newOption.text = listOfQueries[i]['Description'];
        newOption.id = "query_" + listOfQueries[i]['QueryId'];
        newOption.value = listOfQueries[i]['QueryString'];
        theSelect.add(newOption, null);
        logMessage("Added query " + newOption);
    }
    var newOption = document.createElement('option');
    newOption.text = "New Query";
    newOption.id = "query_-1";
    newOption.value = "";
    newOption.selected = true;
    theSelect.add(newOption, null);
    selectQuery();
}

function updateRange(fieldName){	
	var minValue = $(fieldName + "_min").value;
	var maxValue = $(fieldName + "_max").value;
	$(fieldName.toUpperCase()).value = getConstraint(minValue, maxValue); 
}

function handleFieldValue(fieldName, value){
	if (value.startsWith("_range")){
		var s = value.match(/\((.+),(.+)\)/);
		if (s.length < 3) {
			alert("invalid value " + fieldName + " " + value);
			return;
		}
		$(fieldName + "_min").value = s[1];
		$(fieldName + "_max").value = s[2];
		$(fieldName.toUpperCase()).value = value;
	}
	else{
		$(fieldName.toUpperCase()).value = value;
	}
	 
}


function getObjectQuery(){
	var fieldNames = $("inpFieldNames").value.split(",");
	var query = {};
	for (var i = 0 ; i < fieldNames.length ; i++){
		var fieldName = fieldNames[i];
		if ( $(fieldName.toUpperCase()) ){
			query[fieldName] = $(fieldName.toUpperCase()).value;
		}
	}
	//alert(YAHOO.lang.JSON.stringify(query));
	return YAHOO.lang.JSON.stringify(query);
}

function getDomainFromUrl(url){
	var s = url.match("://[^/]+");
	if (s != null && s.length > 0 && s[0].length >= 3)
		return s[0].substring(3); 
	else
		return url;
}