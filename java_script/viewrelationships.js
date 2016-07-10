
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

//initialises when the page loads and loads all the family members
function init() {
document.getElementById('uname').value=query;
loadFamilyMembers();
}


//loads all the family members
function loadFamilyMembers() {

   var fieldIDArray =  new Array("uname");
   var fieldValueArray = new Array();
   var i=0;
   while(i<fieldIDArray.length)
   {
    fieldValueArray[i] = document.getElementById(fieldIDArray[i]); 
    i=i+1;
   }
      var soapBodyElement =  " <loadfamilymembers user='"+fieldValueArray[0].value+"'/>";
	  tag="loadfamilymembers";
	  serviceFunction = "LoadFamilyMembers";
	  xmlhttp.onreadystatechange = onFamilyMembersReceived;
      sendData("POST","FamilyHistoryReadService",soapBodyElement);
   
} // end 




//Save Profile data
function getRelations() {
   var fieldIDArray =  new Array("relationship","relative");
  var fieldValueArray = new Array();
   var i=0;
   while( i<fieldIDArray.length)
   {
    fieldValueArray[i] = document.getElementById(fieldIDArray[i]); 
    i=i+1;;
   }

      var soapBodyElement =  " <relationship relationship='"+fieldValueArray[0].value
                                             +"' relative='"+fieldValueArray[1].value                                            
                                             +"'/>";
      //alert(soapBodyElement);
   
	  tag="relationship";
	  serviceFunction = "RelationshipView";
	  xmlhttp.onreadystatechange = onFamilyRelationQuery;

      sendData("POST","FamilyHistoryReadService",soapBodyElement); 
   
} // end save



//removes the options
function removeOption(field)
{
for(var i=field.length;i>0;i--)
{
 field.remove(i);
}
}

 //Add select options dynamically
 function onFamilyMembersReceived() {
  if (xmlhttp.readyState == 4) {
    if (xmlhttp.status==200) 
    { // http return code 200 - OK
      //alert("received");
	  //alert(xmlhttp.responseText);
      var xmlDoc = xmlhttp.responseXML;
      var l= xmlDoc.getElementsByTagName(tag);

       if (l.length != 0) 
      { 
	
        var f1 = document.getElementById("relative");
        removeOption(f1);

        var familyMemberList = l[0];
        
        var list = familyMemberList.getElementsByTagName("familymember");
       for (i = 0; i<list.length; i++) 
        {
         var familyMemberOptNew1 = document.createElement("option");
         var labellist =list[i].getElementsByTagName("label");
        familyMemberOptNew1.value = labellist[0].firstChild.nodeValue;
         var namelist =list[i].getElementsByTagName("fullname");
 	 	familyMemberOptNew1.text = namelist[0].firstChild.nodeValue;
 	 	
         try
         {
   		 
   		   f1.add(familyMemberOptNew1,null); // standards compliant doesn't work in IE
   		 }
  		 catch(ex) 
  		 {
  		  f1.add(familyMemberOptNew1); // IE only
 		 }
         
        }  // end for
      } 
    }
  }
} // end 



// view family relationships
function onFamilyRelationQuery() 
{
if (xmlhttp.readyState == 4) {
    if (xmlhttp.status==200) 
    { // http return code 200 - OK
     // alert("received");
	 // alert(xmlhttp.responseText+" tag = "+tag);
      var xmlDoc = xmlhttp.responseXML;
      var l= xmlDoc.getElementsByTagName(tag);

        var f = document.createDocumentFragment();
        var e = document.createElement("table");
        f.appendChild(e);
        var table = f.firstChild;
       if (l.length != 0) 
      { 
	
      var list = l[0].getElementsByTagName("relation");
       var thead = document.createElement("thead");
          table.appendChild(thead);
          
          //table header
          var th1 = document.createElement("th");   
          var latitudeHead = document.createTextNode("Names");
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
      
      var lastChildd = document.getElementById("submitt").lastChild;
      document.getElementById("submitt").removeChild(lastChildd);
      document.getElementById("submitt").appendChild(f);
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
         window.open("../html/FamilyHistory.htm","_self");    
       }
     }
  }
 } // end onReadyState()
 
 
 // traverses back to the home page 
 function home() {  
        window.open("../html/ProfileView.htm?user="+query,"_self"); 
}

 // logs out of the system
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
 
