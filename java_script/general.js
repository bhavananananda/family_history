
  var xmlhttp = FactoryXMLHttpRequest();
  var tag,serviceFunction;
    
  function FactoryXMLHttpRequest(){
  if (window.XMLHttpRequest){
  // Mozilla, Opera and Safari should support this object
  //alert("window.XMLHttpRequest true");
  return new XMLHttpRequest();
  } else if (window.ActiveXObject) {
  // for IE
  //alert("window.ActiveXObject true");
  var msxmls = new Array(
  'Msxml2.XMLHTTP.5.0',
  'Msxml2.XMLHTTP.4.0',
  'Msxml2.XMLHTTP.3.0',
  'Msxml2.XMLHTTP',
  'Microsoft.XMLHTTP');
  for (var i = 0; i < msxmls.length; i++) {
  try {
  return new ActiveXObject(msxmls[i]);
  } catch (e) {
  alert("failed " + msxmls[i]);
  return false;
  }  // end try
  }  // end for
  } // not dealing properly with case of no object found
  } // end FactoryXMLHttpRequest


//Validate if the key entered is an integer
function isNumberKey(evt)
      {
         var charCode = (evt.which) ? evt.which : event.keyCode
         if (charCode > 31 && (charCode < 48 || charCode > 57))
         {
          return false;
		 }

         return true;
      }
      
//Validate if the key entered is valid email ID format
function isEmailIDformat(evt)
      {
         var returnValue = true;
         var charCode = (evt.which) ? evt.which : event.keyCode
         if (charCode > 31 && (charCode < 48 || charCode > 57))
         {
          returnValue = false;
		 }
		 if ((charCode > 64 && charCode < 91) || (charCode > 96 && charCode < 123)|| charCode==45 || charCode ==95 )
		 {
		  returnValue = true;
		 }

         return returnValue;
      }
      
      
      
//Validate if the key entered is valid email ID format
function isPasswordformat(evt)
      {
         var returnValue = true;
         var charCode = (evt.which) ? evt.which : event.keyCode
         if (charCode > 31 && (charCode < 48 || charCode > 57))
         {
          returnValue = false;
		 }
		 if ((charCode > 64 && charCode < 91) || (charCode > 96 && charCode < 123))
		 {
		  returnValue = true;
		 }

         return returnValue;
      }
      
//Validate if the key entered is an alphabet
function isAlphabetKey(evt,field)
      {

         var charCode = (evt.which) ? evt.which : event.keyCode;
         if ((charCode > 64 && charCode < 91) || (charCode > 96 && charCode < 123) || charCode == 32 || charCode == 45 || charCode == 39)
         {
           if(field.id == "other" && field.value.length == 0)
             {
             }
           return true;     
		 }

             if(field.id == "personName")
             {
              if (charCode == 44)
              {        
                return true;
              }
             }
             
         return false;
      }



      
//Validate if the key entered is an integer/floating point
function isFloatingNumberKey(evt)
      {
         var charCode = (evt.which) ? evt.which : event.keyCode
         if (charCode > 31 && (charCode < 45 || charCode > 57) && charCode != 47)
         {
          return false;
		 }

         return true;
      }
      
//function to validate location
function validateLocation(fieldIDArray)
 {
  var i=0; 
  var latitude,longitude;
  while(i<fieldIDArray.length)
   {
     if(fieldIDArray[i].id== "latitude")
     latitude = fieldIDArray[i].value;
      
     if(fieldIDArray[i].id== "longitude")
     longitude = fieldIDArray[i].value;
    
    i=i+1;
   }
   
     	if (latitude > 0 || latitude < 0 || latitude == 0) 
     	{
     	  if (longitude > 0 || longitude < 0 || longitude == 0)
		  return "true"; 
		  else
		  return "false"; 
		}
		else
		  return "false"; 
 }
 
//function to validate date
function validateDate(fieldIDArray)
 {
  var i=0; 
  var day,month,year;
  while(i<fieldIDArray.length)
   {
    if(fieldIDArray[i].id== "DD")
     day = fieldIDArray[i].value;
    
    if(fieldIDArray[i].id== "MM")
     month = fieldIDArray[i].value;
      
    if(fieldIDArray[i].id== "YY")
     year = fieldIDArray[i].value;
    
    i=i+1;
   }
   
   	if (month > 12 || day > 31) {
			return "false"; }
	
		if (day < 29) {
			return "true";
		}
		else {
			
			if (month == 1 | month == 3 | month == 5 | month == 7 | month == 8 | month == 10 | month == 12 ) {
				return "true";
				
			}
			else {
				if (month != 2) {
					if (day <= 30) {
						return "true";
					}
					else {
						return "false";
					}
				}
				else {
					//check for leap year because it is February
					
					if ( (1996-year)%4 == 0) {
						if (day <= 29) {
							return "true";
						}
						else {
							return "false";
						}
					}
					else {
						if (day <=28) {
							return "true";		
						}
						else {
							return "false";
						}
					}
				}
								
			}
 
 		}			
	}



//send the data to the web service
function sendData(method,serviceName,soapBodyElement) {

if (xmlhttp)
     {
     // alert("sending data ...");
      xmlhttp.open(method,serviceName, true);
	  xmlhttp.setRequestHeader("SOAPACTION", serviceFunction);
	  // construct SOAP body
	  var soapBody = "<?xml version='1.0' encoding='UTF-8'?>\n";
      soapBody= soapBody + "<env:Envelope";
      soapBody= soapBody + "   xmlns:env='http://schemas.xmlsoap.org/soap/envelope/'";
      soapBody= soapBody + "   xmlns:enc='http://schemas.xmlsoap.org/soap/encoding/'";
      soapBody= soapBody + "   xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'";
      soapBody= soapBody + "   xmlns:xsd='http://www.w3.org/2001/XMLSchema'";
      soapBody= soapBody + "   env:encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'>";
      soapBody= soapBody + "  <env:Body>";
	  soapBody= soapBody + " <myapp:FamilyHistoryRequest xmlns:myapp='http://http://cms.brookes.ac.uk/student/BAnanda'>";
      soapBody= soapBody + soapBodyElement ;
	  soapBody= soapBody + " </myapp:FamilyHistoryRequest>";
      soapBody= soapBody + " </env:Body>";
      soapBody= soapBody + "</env:Envelope>";
	  //alert(soapBody);
      xmlhttp.send(soapBody);
     }
     
    }


