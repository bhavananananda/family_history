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

// greets the user with the nam obtained from the get query string
function init() {
document.getElementById('uname').value=query;
}

//Displays the heriditary diseases of the profile holder
function viewHeriditaryDiseases() {

      var soapBodyElement =  " <heriditarydiseases/>";
  
	  tag="heriditarydiseases";
	  serviceFunction = "HeriditaryDiseases";
	  xmlhttp.onreadystatechange = onHealthDetailsReceived;

      sendData("POST","FamilyHistoryReadService",soapBodyElement); 
   

}

 //Add select options dynamically
 function onHealthDetailsReceived() {
  if (xmlhttp.readyState == 4) {
    if (xmlhttp.status==200) 
    { // http return code 200 - OK
     // alert("received");
	  //alert(xmlhttp.responseText);
      var xmlDoc = xmlhttp.responseXML;
      var l= xmlDoc.getElementsByTagName(tag);

        var f = document.createDocumentFragment();
        var e = document.createElement("table");
        f.appendChild(e);
        var table = f.firstChild;
       if (l.length != 0) 
      { 
	
      var list = l[0].getElementsByTagName("heriditary");
       var thead = document.createElement("thead");
          table.appendChild(thead);
          
          //table header
          var th1 = document.createElement("th");   
          var latitudeHead = document.createTextNode("Heriditary Diseases");
          th1.appendChild(latitudeHead);
          thead.appendChild(th1);
         
       for (i = 0; i<list.length; i++) 
        {

        var t = list[i].firstChild.nodeValue;
        var dtText = document.createTextNode(t);

        //alert(t);
        var tr = document.createElement("tr");
          table.appendChild(tr);
          
          var h = document.createElement("td");
          tr.appendChild(h);
          h.appendChild(dtText);

         
        }  // end for
      } 
      
      var lastChildd = document.getElementById("heriditary").lastChild;
      document.getElementById("heriditary").removeChild(lastChildd);
      document.getElementById("heriditary").appendChild(f);
    }
  }
} // end 


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
      if (list.length != 0)
       { 
         var text = list[0].firstChild.nodeValue; 
         //alert(text);   
  
       }
     }
  }
           window.open("../html/FamilyHistory.htm","_self");  
 } // end onReadyState()
 
 
 
// Return back to home page
 function home() {  
        window.open("../html/ProfileView.htm?user="+query,"_self"); 
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

      var soapBodyElement =  " <logout user='"+fieldValueArray[0].value+"'/>";
	  tag="logout";
	  serviceFunction = "LogOut";
	  xmlhttp.onreadystatechange = onReadyStateChanged;
      sendData("POST","FamilyHistoryAccessService",soapBodyElement);
   
} // end logout
 
