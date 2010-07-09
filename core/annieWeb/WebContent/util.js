function getDefaultText(object, defaultText){
    if (object == undefined || object == null)
        return defaultText;
    else
        return object;
}

function logMessage(message){
    if (typeof(console) != "undefined"){
        console.log(message);
    }
}

function collectFormData(formEl){
    if (typeof formEl == "string"){
        formEl = new YAHOO.util.Element(formEl);
    }

    var children = formEl.getElementsByTagName('input');
    var resString = "";
    for (var i = 0; i < children.length ; i++){
        if (i > 0) resString += "&";
        resString += children[i].name + "=" + children[i].value;
    }
    return resString;
}

/* sometime the getElementsByTagName('input') doesnot work
   this uses DOM elements attributes */
function collectFormData2(formEl){
    var children = formEl.elements;
    var resString = "";
    for (var i = 0; i < children.length ; i++){
        if (i > 0) resString += "&";
        resString += children[i].name + "=" + children[i].value;
    }
    return resString;
}

/* Toggle an HTML item */
function toggleLayer( whichLayer ) {
	var elem, vis;
	if( document.getElementById ) // this is the way the standards work
		elem = document.getElementById( whichLayer );
	else if( document.all ) // this is the way old msie versions work
	elem = document.all[whichLayer];
	else if( document.layers ) // this is the way nn4 works
	elem = document.layers[whichLayer];

	vis = elem.style;
	// if the style.display value is blank we try to figure it out here
	if(vis.display==''&&elem.offsetWidth!=undefined&&elem.offsetHeight!=undefined)
	vis.display = (elem.offsetWidth!=0&&elem.offsetHeight!=0)?'block':'none';
	vis.display = (vis.display==''||vis.display=='block')?'none':'block';
}

/* remove all children from a DOM node */
function removeAllChildren(domNode){
	while (domNode.childNodes.length > 0){
		domNode.removeChild(domNode.childNodes[0]);
	}
}

/**************** COOKIE ************************/
function getCookie(c_name)
{
    if (document.cookie.length>0)
    {
        c_start=document.cookie.indexOf(c_name + "=")
            if (c_start!=-1)
            { 
                c_start=c_start + c_name.length+1 
                    c_end=document.cookie.indexOf(";",c_start)
                    if (c_end==-1) c_end=document.cookie.length
                        return unescape(document.cookie.substring(c_start,c_end))
            } 
    }
    return ""
}

function setCookie(c_name,value,expiredays)
{
    var exdate=new Date()
        exdate.setDate(exdate.getDate()+expiredays)
        document.cookie=c_name+ "=" +escape(value)+
        ((expiredays==null) ? "" : ";expires="+exdate.toGMTString())
}

