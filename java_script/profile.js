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

// Greets the user with the user namess passed as GET query string 
//and then loads the corresponding details of the profile holder 
// to be able to view on this profile view page
function init() {
document.getElementById('uname').value=query;
loadProfile();
}

// fields validation
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
  
   if(field.id== "DD")
   alert ("Date cannot be left blank!");
   
   if(field.id== "MM")
   alert ("Date cannot be left blank!");
   
   if(field.id== "YY")
   alert ("Date cannot be left blank!");
   
  return "false";
 }
 return "true";
}


//disable the enable fields after the profile has been updated
function disableFields(enabled) {
   var fieldIDArray =  new Array("firstName","middleName","lastName","nickName","gender","DD","MM","YY","password","profileImage","disease","heriditary","blood","address","latitude","longitude","other","latlong","profileHolderDataSubmit");
  var fieldValueArray = new Array();
   var i=0;
    while(i<fieldIDArray.length)
   {
    fieldValueArray[i] = document.getElementById(fieldIDArray[i]); 
    fieldValueArray[i].disabled=enabled;
    i=i+1;;
   }
    
}

//Save Profile data
function editProfile() {
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
     var soapBodyElement =  " <editprofile firstName='"+fieldValueArray[0].value
                                             +"' middleName='"+fieldValueArray[1].value
                                             +"' lastName='"+fieldValueArray[2].value
                                             +"' nickName='"+fieldValueArray[3].value
                                             +"' gender='"+fieldValueArray[4].value
                                             +"' date='"+fieldValueArray[5].value
                                             +"/"+fieldValueArray[6].value
                                             +"/"+fieldValueArray[7].value
                                             +"' email='"+fieldValueArray[8].value
                                             +"' password='"+fieldValueArray[9].value
                                             +"' profileImage='"+fieldValueArray[10].value
                                             +"' disease='"+fieldValueArray[11].value
                                             +"' heriditary='"+fieldValueArray[12].value
                                             +"' blood='"+fieldValueArray[13].value
                                             +"' address='"+fieldValueArray[14].value
                                             +"' latitude='"+fieldValueArray[15].value
                                             +"' longitude='"+fieldValueArray[16].value
                                             +"' other='"+fieldValueArray[17].value
                                             +"'/>";
     // alert(soapBodyElement);
   
	  tag="editprofile";
	  serviceFunction = "UpdateProfileHolderProfile";
	  xmlhttp.onreadystatechange = onProfileEdited;

      sendData("POST","FamilyHistoryUpdateService",soapBodyElement); 
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

//logs out of the system
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
   
      window.open("../html/FamilyHistory.htm","_self");  
} // end logout



//loads the profile data
function loadProfile() {

   var fieldIDArray =  new Array("uname");
   var fieldValueArray = new Array();
   var i=0;
   while(i<fieldIDArray.length)
   {
    fieldValueArray[i] = document.getElementById(fieldIDArray[i]); 
    i=i+1;;
   }
      var soapBodyElement =  " <loadprofile user='"+fieldValueArray[0].value+"'/>";
	  tag="loadprofile";
	  serviceFunction = "LoadProfileHolderProfile";
	  xmlhttp.onreadystatechange = onReadyStateChanged;
      sendData("POST","FamilyHistoryReadService",soapBodyElement);
   
} // end logout



//loads the exhibut  map
function getExhibitMap() {
   var fieldIDArray =  new Array("uname");
   var fieldValueArray = new Array();
   var i=0;
   while(i<fieldIDArray.length)
   {
    fieldValueArray[i] = document.getElementById(fieldIDArray[i]); 
    i=i+1;;
   }

      var soapBodyElement =  " <map user='"+fieldValueArray[0].value+"'/>";
	  tag="map";
	  serviceFunction = "ExhibitMap";
	  xmlhttp.onreadystatechange = retrieveProfileData;
      sendData("POST","FamilyHistoryVisualizationService",soapBodyElement);
   
} // end logout


//loads the exhibit timeline
function getTimeline() {

   var fieldIDArray =  new Array("uname");
   var fieldValueArray = new Array();
   var i=0;
   while(i<fieldIDArray.length)
   {
    fieldValueArray[i] = document.getElementById(fieldIDArray[i]); 
    i=i+1;;
   }

      var soapBodyElement =  " <map user='"+fieldValueArray[0].value+"'/>";
	  tag="timeline";
	  serviceFunction = "ExhibitTimeline";
	  xmlhttp.onreadystatechange = retrieveProfileData;
      sendData("POST","FamilyHistoryVisualizationService",soapBodyElement);

} // end logout



function retrieveProfileData() 
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
       else{
       window.open("../html/ViewFamilyTimeline.htm"); 
       }
      if (list.length != 0)
       {
         var text = list[0].firstChild.nodeValue; 
         if(tag!="map")
         window.open("../html/FamilyHistory.htm","_self");       
       }
       
     }
  }
 } // end 
 
 function onProfileEdited() 
{
 // alert("onProfileEdited");
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
         disableFields(true);
         document.getElementById('uname').value=document.getElementById('firstName').value;
       }
     }
  }
 } // end 


// adds a new family member
function addFamilyMember()
{
  var text = document.getElementById('uname').value;
  window.open("../html/FamilyMemberRegistration.htm?user="+text,"_self"); 
}

// view family member profile
function viewFamilyMemberProfile()
{
  var text = document.getElementById('uname').value;
  window.open("../html/FamilyMemberProfile.htm?user="+text,"_self"); 
}

//view non family member profile
function viewNonFamilyMemberProfile()
{
  var text = document.getElementById('uname').value;
  window.open("../html/NonFamilyMemberProfile.htm?user="+text,"_self"); 
}

//add relationships
function addRelationships()
{
  var text = document.getElementById('uname').value;
   window.open("../html/AddRelationships.htm?user="+text,"_self"); 
}

//view relationships
function viewRelationships()
{
  var text = document.getElementById('uname').value;
   window.open("../html/ViewRelationships.htm?user="+text,"_self"); 
}

//view health details
function viewHealthDetails()
{
  var text = document.getElementById('uname').value;
  window.open("../html/HealthDetails.htm?user="+text,"_self"); 

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
      
      var text;
      var list = xmlDoc.getElementsByTagName("firstname");  
      if( list[0].firstChild!=null)   
      text = list[0].firstChild.nodeValue; 
      if(text==null) text="";
      document.getElementById('firstName').value=text;
      text="";
      
      list = xmlDoc.getElementsByTagName("middlename");   
      if( list[0].firstChild!=null)   
      text = list[0].firstChild.nodeValue;      
      document.getElementById('middleName').value=text;
      text="";
      
      list = xmlDoc.getElementsByTagName("lastname");  
      if( list[0].firstChild!=null)      
      text = list[0].firstChild.nodeValue; 
      if(text==null) text="";
      document.getElementById('lastName').value=text;
      text="";
      
      list = xmlDoc.getElementsByTagName("nickname");     
      if( list[0].firstChild!=null)
      text = list[0].firstChild.nodeValue; 
      if(text==null) text="";
      document.getElementById('nickName').value=text;
      text="";
      
      list = xmlDoc.getElementsByTagName("imageurl");   
      if( list[0].firstChild!=null)    
      text = list[0].firstChild.nodeValue;
      if(text==null) text=""; 
      {
       document.getElementById('imageURL').src=text;
       document.getElementById('profileImage').value=text;
      }
      text="";
     
      list = xmlDoc.getElementsByTagName("gender"); 
      var gender = document.getElementById('gender');
      if( list[0].firstChild!=null)     
      text = list[0].firstChild.nodeValue; 
      if(text=="Male")
      gender.options[0].selected=true;
      else
      gender.options[1].selected=true;
      text="";
      
      list = xmlDoc.getElementsByTagName("dd");     
      if( list[0].firstChild!=null)
      text = list[0].firstChild.nodeValue; 
      if(text==null) text=""; 
      document.getElementById('DD').value=text;
      text=""; 
      
      list = xmlDoc.getElementsByTagName("mm"); 
      if( list[0].firstChild!=null)    
      text = list[0].firstChild.nodeValue; 
      if(text==null) text=""; 
      document.getElementById('MM').value=text;
      text="";
      
      list = xmlDoc.getElementsByTagName("yy"); 
      if( list[0].firstChild!=null)    
      text = list[0].firstChild.nodeValue; 
      if(text==null) text=""; 
      document.getElementById('YY').value=text;
      text="";
      
      list = xmlDoc.getElementsByTagName("email");
      if( list[0].firstChild!=null)     
      text = list[0].firstChild.nodeValue; 
      document.getElementById('email').value=text;
      text="";
      
      list = xmlDoc.getElementsByTagName("disease");  
      if( list[0].firstChild!=null)   
      text = list[0].firstChild.nodeValue; 
      document.getElementById('disease').value=text;
      text="";
      
      list = xmlDoc.getElementsByTagName("heriditary");  
      var heriditary = document.getElementById('heriditary');
      if( list[0].firstChild!=null)
      text = list[0].firstChild.nodeValue; 
      if(text=="Heart Disease")
      heriditary.options[0].selected=true;
      if(text=="Diabeties")
      heriditary.options[1].selected=true;
      if(text=="Haemophilia")
      heriditary.options[2].selected=true;
      if(text=="Sickle-Cell Diasease")
      heriditary.options[3].selected=true;
      text="";
      
      list = xmlDoc.getElementsByTagName("blood");  
      var blood = document.getElementById('blood');
      if( list[0].firstChild!=null)
      text = list[0].firstChild.nodeValue; 
      if(text=="O+")
      blood.options[0].selected=true;
      if(text=="O-")
      blood.options[1].selected=true;
      if(text=="A+")
      blood.options[2].selected=true;
      if(text=="A-")
      blood.options[3].selected=true;
      if(text=="B+")
      blood.options[4].selected=true;
      if(text=="B-")
      blood.options[5].selected=true;
      if(text=="AB+")
      blood.options[6].selected=true;
      if(text=="AB-")
      blood.options[7].selected=true;
      text="";
      
      list = xmlDoc.getElementsByTagName("password");  
      if( list[0].firstChild!=null)   
      text = list[0].firstChild.nodeValue; 
      document.getElementById('password').value=text;
      text="";
      
      list = xmlDoc.getElementsByTagName("address");   
      if( list[0].firstChild!=null)  
      text = list[0].firstChild.nodeValue; 
      document.getElementById('address').value=text;
      text="";
      
      list = xmlDoc.getElementsByTagName("latitude"); 
      if( list[0].firstChild!=null)    
      text = list[0].firstChild.nodeValue; 
      document.getElementById('latitude').value=text;
      text="";
      
      list = xmlDoc.getElementsByTagName("longitude");  
      if( list[0].firstChild!=null)   
      text = list[0].firstChild.nodeValue; 
      document.getElementById('longitude').value=text;
      text="";
      
      list = xmlDoc.getElementsByTagName("othernotes");  
      if( list[0].firstChild!=null)       
      text = list[0].firstChild.nodeValue; 
      if(text==null) text="";       
      document.getElementById('other').value=text;
      text="";
 
     }
  }
 } // end onReadyState()
