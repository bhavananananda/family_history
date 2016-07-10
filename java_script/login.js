
// Fields validation
function validate(field) {
 if(field.value == "")
 { 
   if(field.id== "user")
   alert ("User's name cannot be left blank!");
   
 
   if(field.id== "password")
   alert ("Password cannot be left blank!");
  
  return "false";
 }
 return "true";
}

//login function
function login() {
   
   var save = "true";
   var fieldIDArray =  new Array("user","password");
   var fieldValueArray = new Array();
   var i=0;
   while(save=="true" && i<fieldIDArray.length)
   {
    fieldValueArray[i] = document.getElementById(fieldIDArray[i]); 
    save = validate(fieldValueArray[i]);
    i=i+1;;
   }
    
   if(save=="true")
   {
      var soapBodyElement =  " <log user='"+fieldValueArray[0].value+"' password='"+fieldValueArray[1].value+"'/>";
	  tag="log";
	  serviceFunction = "LogIn";
	  xmlhttp.onreadystatechange = onReadyStateChanged;
      sendData("POST","FamilyHistoryAccessService",soapBodyElement);
   }
} // end save


// after successful login , the user is taken to the profile view page else a login
// unsuccessful message is displayed.
function onReadyStateChanged() 
{
  //alert("onReadyStateChanged");
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
         if(text=="null")
         alert("Login Unsuccessful!");    
         else
         window.open("../html/ProfileView.htm?user="+text,"_self");       
       }
       
     }
  }
 } // end onReadyState()
