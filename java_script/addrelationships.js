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

// initializes the Search dropwdown with all the members of the family history site
function init() {
document.getElementById('uname').value=query;
loadfamilyandindividuals();
}


// loads the Search dropwdown with all the members of the family history site
function loadfamilyandindividuals() {

   var fieldIDArray =  new Array("uname");
   var fieldValueArray = new Array();
   var i=0;
   while(i<fieldIDArray.length)
   {
    fieldValueArray[i] = document.getElementById(fieldIDArray[i]); 
    i=i+1;;
   }
      var soapBodyElement =  " <loadfamilyandindividuals user='"+fieldValueArray[0].value+"'/>";
	  tag="loadfamilyandindividuals";
	  serviceFunction = "LoadFamilyMembersAndIndividuals";
	  xmlhttp.onreadystatechange = onFamilyHistoryMembersAndIndividualsReceived;
      sendData("POST","FamilyHistoryReadService",soapBodyElement);
   
} // end 




//Save Profile data
function register() {
   var fieldIDArray =  new Array("relationship","relative1","relative2");
  var fieldValueArray = new Array();
   var i=0;
   while( i<fieldIDArray.length)
   {
    fieldValueArray[i] = document.getElementById(fieldIDArray[i]); 
    i=i+1;;
   }

      var soapBodyElement =  " <relationship relationship='"+fieldValueArray[0].value
                                             +"' relative1='"+fieldValueArray[1].value
                                             +"' relative2='"+fieldValueArray[2].value                                             
                                             +"'/>";
     alert(soapBodyElement);
   
	  tag="relationship";
	  serviceFunction = "RelationshipRegistration";
	  xmlhttp.onreadystatechange = onFamilyRelationshipRegistration;

      sendData("POST","FamilyHistoryCreateService",soapBodyElement); 
   
} // end save



// Removes the preloaded option when the page is refreshed 
function removeOption(field)
{
for(var i=field.length;i>0;i--)
{
 field.remove(i);
}
}



//Add select options dynamically
 function onFamilyHistoryMembersAndIndividualsReceived() {
  if (xmlhttp.readyState == 4) {
    if (xmlhttp.status==200) 
    {
      // http return code 200 - OK
      // alert("received");
	  // alert(xmlhttp.responseText);
	  
      var xmlDoc = xmlhttp.responseXML;
      var l= xmlDoc.getElementsByTagName(tag);
 
       if (l.length != 0) 
       { 
        var f1 = document.getElementById("relative1");
        removeOption(f1);
        var f2 = document.getElementById("relative2");
        removeOption(f2);
        var familyMemberList = l[0];
        
        var list1 = familyMemberList.getElementsByTagName("familyhistoryindividual");
        var list2 = familyMemberList.getElementsByTagName("familymember");
       
       for (i = 0; i<list1.length; i++) 
        {
         var familyMemberOptNew1 = document.createElement("option");
         var labellist =list1[i].getElementsByTagName("label");
        familyMemberOptNew1.value = labellist[0].firstChild.nodeValue;
         var namelist =list1[i].getElementsByTagName("fullname");
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
        
        
           for (i = 0; i<list2.length; i++) 
        {
         var familyMemberOptNew2 = document.createElement("option");
         var labellist =list2[i].getElementsByTagName("label");
        familyMemberOptNew2.value = labellist[0].firstChild.nodeValue;
         var namelist =list2[i].getElementsByTagName("fullname");
 	 	familyMemberOptNew2.text = namelist[0].firstChild.nodeValue;
 	 	
         try
         {
   		 
   		   f2.add(familyMemberOptNew2,null); // standards compliant doesn't work in IE
   		 }
  		 catch(ex) 
  		 {
  		  f2.add(familyMemberOptNew2); // IE only
 		 }
         
        }  // end for
      } 
    }
  }
} // end 


 //Add select options dynamically
 function onFamilyMembersReceived() {
  if (xmlhttp.readyState == 4) {
    if (xmlhttp.status==200) 
    { // http return code 200 - OK
      //alert("received");
	 
      var xmlDoc = xmlhttp.responseXML;
      var l= xmlDoc.getElementsByTagName(tag);

       if (l.length != 0) 
      {  
        var f1 = document.getElementById("relative2");
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



function onFamilyRelationshipRegistration() 
{
  //alert("onFamilyRelationshipRegistration");
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
         alert(text);   
         loadFamilyMembers();    
       }
     }
  }
 } // end onReadyState()

function onReadyStateChanged() 
{
 // alert("onReadyStateChanged");
  if (xmlhttp.readyState == 4) 
  {
    if (xmlhttp.status==200) 
    { // http return code 200 - OK
      // alert("received"+tag);
	  // alert(xmlhttp.responseText);
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
 
