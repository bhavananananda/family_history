
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


//initialises and loads the family members
function init() {
document.getElementById('uname').value=query;
loadFamilyMembers();
}

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

//loads family members
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


Validate DOB and age integer fields
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


//  fields validation
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

//Save Profile data
function register() {
   var save = "true";
   var fieldIDArray =  new Array("firstName","middleName","lastName","nickName","gender","living","DD","MM","YY","DDD","MMM","YYY","relationship","relative","age","disease","heriditary","blood","address","latitude","longitude","other");
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
                                             +"' ddate='"+fieldValueArray[9].value
                                             +"/"+fieldValueArray[10].value
                                             +"/"+fieldValueArray[11].value
                                             +"' relationship='"+fieldValueArray[12].value
                                             +"' relative='"+fieldValueArray[13].value
                                             +"' age='"+fieldValueArray[14].value
                                             +"' disease='"+fieldValueArray[15].value
                                             +"' heriditary='"+fieldValueArray[16].value
                                             +"' blood='"+fieldValueArray[17].value
                                             +"' address='"+fieldValueArray[18].value
                                             +"' latitude='"+fieldValueArray[19].value
                                             +"' longitude='"+fieldValueArray[20].value
                                             +"' other='"+fieldValueArray[21].value
                                             +"'/>";
     alert(soapBodyElement);
   
	  tag="registerfamilymember";
	  serviceFunction = "FamilyMemberRegistration";
	  xmlhttp.onreadystatechange = onFamilyMemberRegistration;

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


//remove the previously loaded options to be able to load new sets
// of options derived dynamically
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
         window.open("../html/ProfileView.htm","_self");    
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
 
 
 //transfers to the home page
 
 function home() {  
        window.open("../html/ProfileView.htm?user="+query,"_self"); 
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
   
} // end logout
 
