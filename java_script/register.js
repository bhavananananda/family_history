
// Fields validation
function validate(field) {
 if(field.value == "")
 { 
   if(field.id== "firstName")
   alert ("First name cannot be left blank!");
 
   if(field.id== "email")
   alert ("Email cannot be left blank!");
   
      if(field.id== "password")
   alert ("Password cannot be left blank!");
   
      if(field.id== "address")
   alert ("Address cannot be left blank!");
   
   if(field.id== "latitude")
   alert ("Latitude cannot be left blank!");
   
   if(field.id== "longitude")
   alert ("Longitude cannot be left blank!");
  
   if(field.id== "DD" )
   alert ("Date cannot be left blank!");
   
   if(field.id== "MM")
   alert ("Date cannot be left blank!");
   
   if(field.id== "YY")
   alert ("Date cannot be left blank!");

  return "false";
 }
 return "true";
}

//Save Profile data
function register() {
   var save = "true";
   var fieldIDArray =  new Array("firstName","middleName","lastName","nickName","gender","DD","MM","YY","email","password","profileImage","disease","heriditary","blood","address","latitude","longitude","other");
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
      var soapBodyElement =  " <register firstName='"+fieldValueArray[0].value
                                             +"' middleName='"+fieldValueArray[1].value
                                             +"' lastName='"+fieldValueArray[2].value
                                             +"' nickName='"+fieldValueArray[3].value
                                             +"' gender='"+fieldValueArray[4].value
                                             +"' date='"+fieldValueArray[5].value
                                             +"/"+fieldValueArray[6].value
                                             +"/"+fieldValueArray[7].value
                                             +"' email='"+fieldValueArray[8].value
                                             +"' password='"+fieldValueArray[9].value
                                             +"' image='"+fieldValueArray[10].value
                                             +"' disease='"+fieldValueArray[11].value
                                             +"' heriditary='"+fieldValueArray[12].value
                                             +"' blood='"+fieldValueArray[13].value
                                             +"' address='"+fieldValueArray[14].value
                                             +"' latitude='"+fieldValueArray[15].value
                                             +"' longitude='"+fieldValueArray[16].value
                                             +"' other='"+fieldValueArray[17].value
                                             +"'/>";
     alert(soapBodyElement);
   
	  tag="register";
	  serviceFunction = "ProfileHolderRegistration";
	  xmlhttp.onreadystatechange = onReadyStateChanged;

      sendData("POST","FamilyHistoryCreateService",soapBodyElement); 
   }
} // end save


//Google Geocoding Object API
function getLatLong() {
var geocoder = new GClientGeocoder();
var address = document.getElementById("address");
  geocoder.getLatLng(
    address.value,
    function(point) {
      if (!point) {
        alert(address.value + " not found");
      } 
      else {
        document.getElementById("latitude").value=point.lat();
       document.getElementById("longitude").value=point.lng();
      }
    }
  );
}


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
         alert(text);   
         window.open("../html/FamilyHistory.htm","_self");    
       }
     }
  }
 } // end onReadyState()
