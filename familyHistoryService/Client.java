package familyHistoryService;
import java.net.URL;
import java.net.Socket;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;
import java.io.StringWriter;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Hashtable;
/** <p>Defines a simple API for sending and receiving SOAP messages
It is based on code provided in the book Nakhimovsky, Alexander and Myers, Tom, "Google, Amazon, and Beyond", Apress, 2004, ISBN 1-59059-131-3
and available at <a href="http://apress.com/">http://apress.com/</a></p>

<p>The idea is to receive URL and envelope as strings, then do SOAP call, e.g. </p>
<pre>
POST /axis/PrimeFactorsString.jws HTTP/1.0
Content-Length: 402
Host: localhost
Content-Type: text/xml; charset=utf-8
SOAPAction: ""

&lt;?xml version="1.0" encoding="UTF-8"?>
&lt;SOAP-ENV:Envelope 
   SOAP-ENV:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/"
   xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/"
   xmlns:xsd="http://www.w3.org/2001/XMLSchema"
   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
 &lt;SOAP-ENV:Body>
  &lt;factors>
   &lt;arg0 xsi:type="xsd:int">36&lt;/arg0>
  &lt;/factors>
 &lt;/SOAP-ENV:Body>
&lt;/SOAP-ENV:Envelope>
</pre>
<p>and pass the response back to the user.</p>

<p>The SOAP generated is closer to SOAP 1.0/1.1 than 1.2. Also the HTTP binding uses SOAPAction, which 1.2 does not.</p>
@author Modified by David Duce (Oxford Brookes University) from code provided with Nakhimovsky and Myers book
*/
public class Client {


/**
<p>Returns the next line from a character string reader. Used to read HTTP headers</p>

@param reader the object to read from
@throws Exception if string doesn't end correctly
*/
public String readLine(Reader reader)throws Exception{
  StringBuffer sB=new StringBuffer();
  int ch;
  for(ch=reader.read();ch>=0 && ch!='\r';ch=reader.read())sB.append((char)ch);
  if(ch<0)return sB.toString();
  ch=reader.read();
  if(ch!='\n')
    throw new Exception("line ["+sB.toString()+"] lacks \\n");
  return sB.toString();
}
/** Reads specified number of characters
@param isr the Reader
@param len the number of characters to read
@throws Exception if error occurs
*/
public String readUpToLength(Reader isr, int len)throws Exception{
  StringWriter sw=new StringWriter();
try{ int ch;
  while(len-- > 0 && 0 < (ch=isr.read())){
    sw.write((char)ch); 
  }
  return sw.toString();
 }catch(Exception ex){ex.printStackTrace();System.out.println("**[\n"+sw.toString()+"\n]**");throw(ex);}
}

/** 
Sends a SOAP request message and returns the response message (without the HTTP header) as a {@link String}
@param urlString URL of SOAP ultimate receiver
@param soapAction content of SOAPAction header
@param body SOAP payload to place in the HTTP request message
@throws IOException if error
*/

public String sendSoap(String urlString, String soapAction,
                           String body)throws IOException{
  URL url=new URL(urlString);
  int timeout=20000;
  int port= url.getPort(); if(port<0)port=80;
  Socket s = new Socket(url.getHost(),port);
  s.setSoTimeout(timeout);
  OutputStream outStream = s.getOutputStream ();
  InputStream inStream = s.getInputStream ();
  
  String payload = Envelope(body); // wrap the body supplied in a SOAP envelope
  
  StringBuffer headerbuf = new StringBuffer();
  headerbuf.append("POST ")
              .append(url.getFile()).append(" HTTP/1.0\r\n")
          .append("Host: ")
             .append(url.getHost()).append(':').append(port).append("\r\n")
          .append("Content-Type: text/xml; charset=utf-8\r\n")
          .append("Content-Length: ")
             .append(payload.length()).append("\r\n")
          .append("SOAPAction: \"").append(soapAction).append("\"\r\n")
          .append("\r\n");
                                     /* Send headerbuf and payload. */
      BufferedOutputStream bOutStream = new BufferedOutputStream(outStream);
      bOutStream.write(headerbuf.toString().getBytes("utf-8"));
      bOutStream.write(payload.getBytes("utf-8"));
      bOutStream.flush(); outStream.flush();

      BufferedInputStream bInStream = new BufferedInputStream(inStream);
      InputStreamReader reader=new InputStreamReader(bInStream,"utf-8");
      
      // now examine the response message

      Hashtable hashtable=new Hashtable();
  try{
      
      hashtable.put("CONTENT-LENGTH","0"); // default
      String cmdLine=readLine(reader);
      int firstBlank=cmdLine.indexOf(' ');
      int lastBlank=cmdLine.lastIndexOf(' ');
      if(firstBlank < 0 || firstBlank == lastBlank)
        throw new Exception("Invalid HTTP status line ["+cmdLine+"]");
      // check the status code
      String scode = cmdLine.substring(firstBlank,lastBlank);
      System.out.println("Status code: " + scode);
      String hdr=readLine(reader);
      while(hdr.length()>0){  
        String[]nameVal=hdr.split(": ",2);
        if(nameVal.length > 1)hashtable.put(nameVal[0].toUpperCase(),nameVal[1]);
        hdr=readLine(reader);
      }
      System.out.println("Content-Length=["+hashtable.get("CONTENT-LENGTH")+"]");
      int len=Integer.parseInt((String)hashtable.get("CONTENT-LENGTH"));
      hashtable.put("HTTP_BODY",readUpToLength(reader,len));
      // now decide whether request was ok or not
      // if it wasn't, then terminate here - needs tidying up
      //
      if (("200".equals(scode.trim()))) {
        // ok
        System.out.println("Returned OK status code");
      } else {
        // error code, so dump what we have and quit
        // needs tidying up to raise an exception
        //
        System.out.println("Return status: " + scode);
        System.out.println((String) hashtable.get("HTTP_BODY"));
        System.exit(1);
      }
  } catch (Exception ex) {
       System.out.println("Exception processing response");
  }
      bOutStream.close(); outStream.close();
      bInStream.close();  inStream.close();
      s.close();
      String resString = (String) hashtable.get("HTTP_BODY");
      return resString;
}
/**
Generates a SOAP Envelope
@param body content to place inside the SOAP Body element
*/
public String Envelope(String body){
  System.out.println("Envelope: " + body);
  String S="<?xml version='1.0' encoding='UTF-8'?>\n";
  S+="<SOAP-ENV:Envelope";
  S+="   xmlns:SOAP-ENV='http://schemas.xmlsoap.org/soap/envelope/'";
  S+="   xmlns:SOAP-ENC='http://schemas.xmlsoap.org/soap/encoding/'";
  S+="   xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'";
  S+="   xmlns:xsd='http://www.w3.org/2001/XMLSchema'";
  S+="   SOAP-ENV:encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'>";
  S+="  <SOAP-ENV:Body>";
  S+=   body;
  S+="  </SOAP-ENV:Body>";
  S+="</SOAP-ENV:Envelope>";
  return S;
}


}
