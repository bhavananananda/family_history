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


 
// Return back to home page
function home() {  
        window.open("../html/ProfileView.htm?user="+query,"_self"); 
}


// initializes the family history dropwdown with all the members of the family history site
function init() {
document.getElementById('uname').value=query;
loadFamilyMembers();
}

//loads all the family members of the profile holder
function loadFamilyMembers() {
   var fieldIDArray =  new Array("uname");
   var fieldValueArray = new Array();
   var i=0;
   while(i<fieldIDArray.length)
   {
    fieldValueArray[i] = document.getElementById(fieldIDArray[i]); 
    i=i+1;;
   }
      var soapBodyElement =  " <loadfamilymembers user='"+fieldValueArray[0].value+"'/>";
	  tag="loadfamilymembers";
	  serviceFunction = "LoadFamilyMembers";
	  xmlhttp.onreadystatechange = onFamilyMembersReceived;
      sendData("POST","FamilyHistoryReadService",soapBodyElement);
   
} // end 

// Removes the preloaded option when the page is refreshed 
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
	
        var f = document.getElementById("relative");
        removeOption(f);
        var familyMemberList = l[0];
        
        var list = familyMemberList.getElementsByTagName("familymember");
       for (i = 0; i<list.length; i++) 
        {
         var familyMemberOptNew = document.createElement("option");
         var labellist =list[i].getElementsByTagName("label");
        familyMemberOptNew.value = labellist[0].firstChild.nodeValue;
         var namelist =list[i].getElementsByTagName("fullname");
 	 	familyMemberOptNew.text = namelist[0].firstChild.nodeValue;
         try
         {
   		  f.add(familyMemberOptNew,null); // standards compliant doesn't work in IE
   		 }
  		 catch(ex) 
  		 {
  		  f.add(familyMemberOptNew); // IE only
 		 }
         
        }  // end for
      } 
    }
  }
} // end 

// Fields validation
function validate(field) {

 if(field.value == "")
 { 
   if(field.id== "firstName")
  { alert ("First name cannot be left blank!"); return "false";}
 
   if(field.id== "address")
  {  alert ("Address cannot be left blank!"); return "false";}
   
   if(field.id== "latitude")
  {  alert ("Latitude cannot be left blank!"); return "false";}
   
   if(field.id== "longitude")
  {  alert ("Longitude cannot be left blank!"); return "false";}
  
 
 }
 return "true";
}


// enable and disable the fields when edit link is clicked
function enableFields(enabled) {
   var fieldIDArray =  new Array("firstName","middleName","lastName","nickName","gender","DD","MM","YY","living","DDD","MMM","YYY","disease","heriditary","blood","address","latitude","longitude","other","latlong","nonProfileHolderDataSubmit");
   var fieldValueArray = new Array();
   var i=0;
   var rel = document.getElementById("relative");
if(rel.value.charAt(0)=='P' && enabled==true)
{
alert("Sorry! Your do not have permission to edit this profile! If you are trying to edit your profile, please do it from profile view.");

}
   if(rel.value.charAt(0)=='N' && enabled==true)
   { 
    while(i<fieldIDArray.length)
    {  fieldValueArray[i] = document.getElementById(fieldIDArray[i]); 
       fieldValueArray[i].disabled=!enabled;
      // alert(fieldIDArray[i]);
      i=i+1;;
    }
  }
   if((rel.value.charAt(0)=='N' || rel.value.charAt(0)=='P' )&& enabled==false)
   {if(enabled==false){
    while(i<fieldIDArray.length)
    {  fieldValueArray[i] = document.getElementById(fieldIDArray[i]); 
       fieldValueArray[i].disabled=!enabled;
      i=i+1;;
    }
   }
  }  
}

//Save Profile data
function editProfile() {
   var save = "true";
   var fieldIDArray =  new Array("firstName","middleName","lastName","nickName","gender","DD","MM","YY","living","DDD","MMM","YYY","disease","heriditary","blood","address","latitude","longitude","other");
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
      var soapBodyElement =  " <editfamilymemberprofile profileID='"+document.getElementById("relative").value
      										 +"' firstName='"+fieldValueArray[0].value
                                             +"' middleName='"+fieldValueArray[1].value
                                             +"' lastName='"+fieldValueArray[2].value
                                             +"' nickName='"+fieldValueArray[3].value
                                             +"' gender='"+fieldValueArray[4].value
                                             +"' date='"+fieldValueArray[5].value
                                             +"/"+fieldValueArray[6].value
                                             +"/"+fieldValueArray[7].value
                                             +"' living='"+fieldValueArray[8].value
                                             +"' ddate='"+fieldValueArray[9].value
                                             +"/"+fieldValueArray[10].value
                                             +"/"+fieldValueArray[11].value
                                             +"' disease='"+fieldValueArray[12].value
                                             +"' heriditary='"+fieldValueArray[13].value
                                             +"' blood='"+fieldValueArray[14].value
                                             +"' address='"+fieldValueArray[15].value
                                             +"' latitude='"+fieldValueArray[16].value
                                             +"' longitude='"+fieldValueArray[17].value
                                             +"' other='"+fieldValueArray[18].value
                                             +"'/>";
     //alert(soapBodyElement);
   
	  tag="editfamilymemberprofile";
	  serviceFunction = "UpdateFamilyMemberProfile";
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
      
      window.open("../html/FamilyHistory.htm","_self");  
   
} // end logout



function loadProfile() {

   var fieldIDArray =  new Array("relative");
   var fieldValueArray = new Array();
   var i=0;
   while(i<fieldIDArray.length)
   {
    fieldValueArray[i] = document.getElementById(fieldIDArray[i]); 
    i=i+1;;
   }
      var soapBodyElement =  " <loadprofile profileID='"+fieldValueArray[0].value+"'/>";
	  tag="loadprofile";
	  serviceFunction = "LoadFamilyMemberProfile";
	  xmlhttp.onreadystatechange = onReadyStateChanged;
      sendData("POST","FamilyHistoryReadService",soapBodyElement);
   
} 



//retrieve profile data
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
      if (list.length != 0)
       {
         var text = list[0].firstChild.nodeValue; 
         if(tag!="map")
         window.open("../html/FamilyHistory.htm","_self");       
       }
       
     }
  }
 } // end 
 
 
 //disable the enabled fields when clicked on edit link
 function disableFields(enabled) {
   var fieldIDArray =  new Array("firstName","middleName","lastName","nickName","gender","DD","MM","YY","living","profileImage","DDD","MMM","YYY","disease","heriditary","blood","address","latitude","longitude","other","latlong","nonProfileHolderDataSubmit");

  var fieldValueArray = new Array();
   var i=0;
    while(i<fieldIDArray.length)
   {
    fieldValueArray[i] = document.getElementById(fieldIDArray[i]); 
    fieldValueArray[i].disabled=enabled;
    
    i=i+1;;
   }
}
 
 
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

// transfers to the add family member page
function addFamilyMember()
{
  var text = document.getElementById('uname').value;
  window.open("../html/FamilyMemberRegistration.htm?user="+text,"_self"); 
}

// transfers to the view family member page
function viewFamilyMemberProfile()
{
  var text = document.getElementById('uname').value;
  window.open("../html/FamilyMemberProfile.htm?user="+text,"_self"); 
}

// transfers to the add relationships page
function addRelationships()
{
  var text = document.getElementById('uname').value;
   window.open("../html/Relationships.htm?user="+text,"_self"); 
}

// Receives the data
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
      document.getElementById('imageURL').src=text;
      text="";
            
      list = xmlDoc.getElementsByTagName("gender"); 
      if( list[0].firstChild!=null)     
      text = list[0].firstChild.nodeValue; 
      var gender = document.getElementById('gender');
      if(text=="Male"){
      gender.options[0].selected=true;}
      else{
      gender.options[1].selected=true;
      }
      text="";
      
             
      list = xmlDoc.getElementsByTagName("living"); 
      var living = document.getElementById('living');
      if( list[0].firstChild!=null)     
      text = list[0].firstChild.nodeValue; 
      if(text=="Yes")
      living.options[0].selected=true;
      else
      living.options[1].selected=true;
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
      
      list = xmlDoc.getElementsByTagName("ddd");     
      if( list[0].firstChild!=null)
      text = list[0].firstChild.nodeValue; 
      if(text==null) text=""; 
      document.getElementById('DDD').value=text;
      text=""; 
      
      list = xmlDoc.getElementsByTagName("mmm"); 
      if( list[0].firstChild!=null)    
      text = list[0].firstChild.nodeValue; 
      if(text==null) text=""; 
      document.getElementById('MMM').value=text;
      text="";
      
      list = xmlDoc.getElementsByTagName("yyy"); 
      if( list[0].firstChild!=null)    
      text = list[0].firstChild.nodeValue; 
      if(text==null) text=""; 
      document.getElementById('YYY').value=text;
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

      enableFields(false);

     }
  }

 } // end onReadyState()
 
 
 
function validateDOBAge()
{
if( document.getElementById("DD").value=="" || document.getElementById("MM").value=="" || document.getElementById("YY").value=="")
{
if( document.getElementById("age").value== "")
{ 
  alert ("Please enter DOB or age of the family member!");
  return "false";
}

} 
 return "true";
}
 
 //Save Profile data
function register() {
   var save = "true";
   var fieldIDArray =  new Array("firstName","middleName","lastName","nickName","gender","living","DD","MM","YY","age","DDD","MMM","YYY","disease","heriditary","blood","address","latitude","longitude","other");
  var fieldValueArray = new Array();
   var i=0;
   while(save=="true" && i<fieldIDArray.length)
   {
    fieldValueArray[i] = document.getElementById(fieldIDArray[i]); 
    save = validate(fieldValueArray[i]);
    i=i+1;;
   }
    
    if(save=="true")
    save = validateDOBAge();
    
   if(save=="true")
   {
      var soapBodyElement =  " <registerfamilymember firstName='"+fieldValueArray[0].value
                                             +"' middleName='"+fieldValueArray[1].value
                                             +"' lastName='"+fieldValueArray[2].value
                                             +"' nickName='"+fieldValueArray[3].value
                                             +"' gender='"+fieldValueArray[4].value
                                             +"' living='"+fieldValueArray[5].value
                                             +"' date='"+fieldValueArray[6].value
                                             +"/"+fieldValueArray[7].value
                                             +"/"+fieldValueArray[8].value
                                             +"' age='"+fieldValueArray[9].value
                                             +"' ddate='"+fieldValueArray[10].value
                                             +"/"+fieldValueArray[11].value
                                             +"/"+fieldValueArray[12].value
                                             +"' disease='"+fieldValueArray[13].value
                                             +"' heriditary='"+fieldValueArray[14].value
                                             +"' blood='"+fieldValueArray[15].value
                                             +"' address='"+fieldValueArray[16].value
                                             +"' latitude='"+fieldValueArray[17].value
                                             +"' longitude='"+fieldValueArray[18].value
                                             +"' other='"+fieldValueArray[19].value
                                             +"'/>";
     alert(soapBodyElement);
   
	  tag="registerfamilymember";
	  serviceFunction = "UpdateFamilyMemberProfile";
	  xmlhttp.onreadystatechange = onFamilyMemberRegistration;

      sendData("POST","FamilyHistoryUpdateService",soapBodyElement); 
   }
} // end save
 
 
 
function onFamilyMemberRegistration() 
{
  //alert("onFamilyMemberRegistration");
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
         window.open("../html/FamilyMemberProfile.htm","_self");    
       }
     }
  }
 } // end onReadyState()
 