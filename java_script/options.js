// The user name is obtained by the query string passed to this page
var Request = {	
 	parameter: function(name) {
 		return this.parameters()[name];
 	},
 	
 	parameters: function() {
 		var result = {};
 		var url = window.location.href;
 		var parameters = url.slice(url.indexOf('?') + 1).split('&');
 		
 		for(var i = 0;  i < parameters.length; i++) {
 			var parameter = parameters[i].split('=');
 			result[parameter[0]] = parameter[1];
 		}
 		return result;
 	}
 }
var query = Request.parameter('user');

//greets the user with the username obtained from the GET querystring
function init() {
document.getElementById('uname').value=query;
}


// Logs out of the system
function logout() {
   var fieldIDArray =  new Array("uname");
   var fieldValueArray = new Array();
   var i=0;
   while(i<fieldIDArray.length)
   {
    fieldValueArray[i] = document.getElementById(fieldIDArray[i]); 
    i=i+1;;
   }

      var soapBodyElement =  " <log user='"+fieldValueArray[0].value+"'/>";
	  tag="logout";
	  serviceFunction = "LogOut";
	  xmlhttp.onreadystatechange = onReadyStateChanged;
      sendData("POST","FamilyHistoryAccessService",soapBodyElement);
   
} // end logout



//loads Exhibit Map
function getExhibitMap() {

   var fieldIDArray =  new Array("uname");
   var fieldValueArray = new Array();
   var i=0;
   while(i<fieldIDArray.length)
   {
    fieldValueArray[i] = document.getElementById(fieldIDArray[i]); 
    i=i+1;;
   }
    
      var soapBodyElement =  " <log user='"+fieldValueArray[0].value+"'/>";
	  tag="map";
	  serviceFunction = "ExhibitMap";
	  xmlhttp.onreadystatechange = onReadyStateChanged;
      sendData("POST","FamilyHistoryVisualizationService",soapBodyElement);
   
} // end logout


function onReadyStateChanged() 
{
 // alert("onReadyStateChanged");
  if (xmlhttp.readyState == 4) 
  {
    if (xmlhttp.status==200) 
    { // http return code 200 - OK
      //alert("received"+tag);
	  //alert(xmlhttp.responseText);
      var xmlDoc = xmlhttp.responseXML;
      var list = xmlDoc.getElementsByTagName(tag);
     
      if(tag=="map"){
       window.open("../html/ViewFamilyLocationMap.htm"); 
       }
      if (list.length != 0)
       {
         var text = list[0].firstChild.nodeValue; 
         if(tag!="map")
         window.open("../html/FamilyHistory.htm","_self");       
       }
       
     }
  }
 } // end onReadyState()


