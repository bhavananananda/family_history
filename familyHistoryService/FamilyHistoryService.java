package familyHistoryService;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import javax.activation.MimetypesFileTypeMap;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.apache.axis.session.Session;
import org.apache.axis.session.SimpleSession;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.inference.pellet.ProtegePelletOWLAPIReasoner;
import edu.stanford.smi.protegex.owl.inference.protegeowl.ReasonerManager;
import edu.stanford.smi.protegex.owl.inference.reasoner.ProtegeReasoner;
import edu.stanford.smi.protegex.owl.inference.reasoner.exception.ProtegeReasonerException;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.OWLAllDifferent;
import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.RDFSDatatype;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLIndividual;
import edu.stanford.smi.protegex.owl.swrl.SWRLRuleEngine;
import edu.stanford.smi.protegex.owl.swrl.SWRLRuleEngineFactory;
import edu.stanford.smi.protegex.owl.swrl.bridge.BridgeFactory;
import edu.stanford.smi.protegex.owl.swrl.bridge.SWRLRuleEngineBridge;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.SWRLRuleEngineBridgeException;
import edu.stanford.smi.protegex.owl.swrl.bridge.impl.OWLObjectPropertyAssertionAxiomImpl;
import edu.stanford.smi.protegex.owl.swrl.exceptions.SWRLRuleEngineException;

/**
 * <p>
 * Defines a simple API for sending and receiving SOAP messages It is based on
 * code provided in the book Nakhimovsky, Alexander and Myers, Tom,
 * "Google, Amazon, and Beyond", Apress, 2004, ISBN 1-59059-131-3 and available
 * at <a href="http://apress.com/">http://apress.com/</a>
 * </p>
 * <p>
 * Code does more than is necessary for simple SOAP applications; it will also
 * handle REST actions (basically actions using HTTP GET, PUT and DELETE
 * methods). The extra code has NOT been removed.
 * </p>
 * <p>
 * Code:
 * </p>
 * <ol>
 * <li>accepts socket connections, reads HTTP SOAP off them, interprets the
 * results in an application dependent way (this is where application code plugs
 * in), and sends the answers back down the socket</li>
 * <li>as an extension, it can be used for REST actions: GET,PUT,DELETE</li>
 * <li>as a further extension, GET can be used to invoke actions; see text</li>
 * </ol>
 * 
 * @author Modified by David Duce (Oxford Brookes University) from code provided
 *         with Nakhimovsky and Myers book
 */
public class FamilyHistoryService {

	protected ServerSocket serverSocket = null;
	private Session session;
	private static String uri = "file:/C:/eclipse/FamilyHistoryEclipse/FamilyHistoryEclipseWorkspace/FamilyHistory/familyHistoryService/Web3FamilyTree.owl";
	private String josonURI = "file:/C:/eclipse/FamilyHistoryEclipse/FamilyHistoryEclipseWorkspace/FamilyHistory/family.js";

	private static JenaOWLModel owlModel;

	protected String readRequestURL = "ok"; // if non-null, GET may invoke
	static Collection fullCollection;
	// actions.

	protected String simpleDateFormatString = "yyyy-MM-dd HH:mm:ss";
	protected SimpleDateFormat simpleDateFormat = null;

	protected String rfc1123DateFormatString = "EEE, d MMM y k:m:s 'GMT'";
	protected SimpleDateFormat rfc1123DateFormat = null;

	/** Gets date format for dates in RFC1123 format as used by HTTP */
	public SimpleDateFormat getRfc1123DateFormat() {
		if (rfc1123DateFormat == null) {
			rfc1123DateFormat = new SimpleDateFormat(rfc1123DateFormatString);
			rfc1123DateFormat
					.setTimeZone(new java.util.SimpleTimeZone(0, "GMT"));
		}
		return rfc1123DateFormat;
	}

	DocumentBuilderFactory dbf = null;

	/**
	 * Makes a new instance of a document builder factory; sets validation and
	 * namespace aware properties
	 */
	public DocumentBuilderFactory getDBF() {
		if (dbf != null)
			return dbf;
		dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		dbf.setValidating(false);
		return dbf;
	}

	/**
	 * Default constructor
	 */
	public FamilyHistoryService() {
	}

	/**
	 * parseURL gets the Document at a particular URL, or throws exception
	 * 
	 * @param url
	 *            URL to parse
	 * @throws Exception
	 *             if error detected
	 **/
	public Document parseURL(String url) throws Exception {
		DocumentBuilder db = getDBF().newDocumentBuilder();
		Document doc = db.parse(url);
		return doc;
	}

	/**
	 * Converts a string into an instance of a DOM document
	 * 
	 * @param str
	 *            <code>String</code> to convert to document
	 */
	public Document readDocument(String str) throws Exception {
		try {
			Document doc = getDBF().newDocumentBuilder().parse(
					new InputSource(new StringReader(str)));
			return doc;
		} catch (Throwable ex) {
			System.out.println("readDocument failure:" + ex);
			return null;
		}
	}

	/**
	 * Saves the given xml file with the revised document contents
	 * 
	 * @param xmlFile
	 *            File to be written to
	 * @param document
	 *            Revised document content
	 */
	public void writeDocument(String xmlFile, Document document)
			throws Exception {

		XMLSerializer serializer = new XMLSerializer();
		serializer.setOutputCharStream(new java.io.FileWriter(xmlFile));
		serializer.serialize(document);

	}

	// END OF DOM FUNCTIONALITY SECTION

	// PORTS AND SOCKETS FUNCTIONALITY SECTION
	/** Port number on which the server runs; hard-wired into code */
	protected int portNumber = 8004;
	public static HashSet set;

	public void setPortNumber(int N) {
		portNumber = N;
	}

	public int getPortNumber() {
		return portNumber;
	}

	/** creates a new socket */
	protected void initSocket() throws Exception {
		try {
			serverSocket = new ServerSocket(getPortNumber());
		} catch (Exception ex) {
			System.out.println("DBAuthService failed to listen on "
					+ getPortNumber());
			System.exit(1);
		}
	}

	// int nnn=1;
	/**
	 * badSocket can test the client.getInetAddress() to see if, for example, it
	 * is 127.0.0.1, or whatever we want to allow to connect.
	 **/
	protected boolean badSocket(Socket client) {
		return false;
		// return 0==(nnn=(1-nnn));
	}

	/**
	 * close specified socket
	 * 
	 * @param client
	 *            identifies socket to close
	 */
	protected void closeSocket(Socket client) {
		try {
			System.out.println("closing socket from: "
					+ client.getInetAddress());
			OutputStream os = client.getOutputStream();
			PrintWriter pW = new PrintWriter(
					new OutputStreamWriter(os, "utf-8"), true);
			String msg = "We do not like you, " + client.getInetAddress();
			sendExceptionFault(new Exception(msg), pW);
			client.close();
		} catch (Exception ex) {
		}
	}

	/**
	 * waits for incoming calls; invokes <code>doHttpTransaction</code> to
	 * process a call
	 * 
	 * @throws Exception
	 *             if error detected
	 */
	protected void listenOnPort() throws Exception {
		if (serverSocket == null)
			initSocket();
		Socket clientSocket = null;
		try {
			while (true) {
				try {
					clientSocket = serverSocket.accept();
					if (badSocket(clientSocket)) {
						closeSocket(clientSocket);
						continue;
					}
					System.out.println("got a client from "
							+ clientSocket.getInetAddress() + "to "
							+ clientSocket.getLocalAddress() + " on "
							+ getPortNumber());
				} catch (Exception ex) {
					System.out.println("DBAuthService failed to accept on "
							+ getPortNumber() + ": " + ex);
					System.exit(1);
				}
				doHttpTransaction(clientSocket);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Processes incoming HTTP request. Invokes <code>doPost</code> method if a
	 * POST request. Can also handle GET, PUT and DELETE requests, but currently
	 * commented out in the code.
	 * 
	 * @param clientSocket
	 *            reference to incoming call
	 * @throws Exception
	 *             if error detected
	 */
	public void doHttpTransaction(Socket clientSocket) throws Exception {
		OutputStream os = null;
		BufferedOutputStream bos = null;
		PrintWriter pW = null;
		try {
			InputStream is = clientSocket.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			InputStreamReader reader = new InputStreamReader(bis, "utf-8");
			os = clientSocket.getOutputStream();
			bos = new BufferedOutputStream(os);
			pW = new PrintWriter(new OutputStreamWriter(bos, "utf-8"), true);
			Hashtable httpData = readHttpData(reader);
			if (!authorized(httpData))
				throw new Exception("Authorization failure:\n" + httpData);
			System.out.println("httpData=" + httpData);
			String cmd = (String) httpData.get("METHOD");
			if ("POST".equals(cmd))
				doPost(httpData, pW);
			else if ("GET".equals(cmd))
				doGet(httpData, pW);
			// else if("PUT".equals(cmd))doPut(httpData,pW);
			// else if("DELETE".equals(cmd))doDelete(httpData,pW);
			else
				throw new Exception("unknown command [" + cmd
						+ "]; POST,GET,PUT,DELETE okay");
			reader.close();
			bis.close();
			is.close();
			pW.close();
			pW = null;
			bos.close();
			os.close();
		} catch (Exception ex) {

			if (pW != null) {
				sendExceptionFault(ex, pW);
				pW.close();

				bos.close();
				os.close();
			}
		}
	}

	// END OF PORTS AND SOCKETS FUNCTIONALITY SECTION
	// HTTP FUNCTIONALITY SECTION

	/**
	 * Sends a SOAPFault element. Assumes that no output has yet been sent. Code
	 * needs some tidying up to hande SOAP 1.2 fault reporting mechanisms
	 * correctly
	 * 
	 * @param ex
	 *            Exception from which message is extracted
	 * @param pW
	 *            output stream to which message is written
	 */

	protected void sendExceptionFault(Exception ex, PrintWriter pW) {
		String message = ex.getMessage();
		if (message != null && 0 <= message.indexOf("Authorization")) {
			sendAuthorizationException(ex, pW);
			return;
		}
		StringBuffer sB = new StringBuffer();
		sB
				.append("<?xml version='1.0' encoding='UTF-8'?>\n")
				.append("<SOAP-ENV:Envelope \n")
				// REMOVAL Aug 9
				// .append(
				// "  SOAP-ENV:encodingStyle='http://xml.apache.org/xml-soap/literalxml'\n"
				// )
				.append(
						"  xmlns:SOAP-ENV='http://schemas.xmlsoap.org/soap/envelope/'\n")
				.append("  xmlns:xsd='http://www.w3.org/2001/XMLSchema'\n")
				.append(
						"  xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>\n")
				.append(" <SOAP-ENV:Body>\n")
				.append("  <SOAP-ENV:Fault>\n")
				.append("    <SOAP-ENV:faultcode>")
				.append(42)
				.append("</SOAP-ENV:faultcode>\n")
				.append(
						"    <SOAP-ENV:faultstring>internal error</SOAP-ENV:faultstring>\n")
				.append("    <SOAP-ENV:detail>\n").append(ex.toString())
				.append("</SOAP-ENV:detail>\n").append("  </SOAP-ENV:Fault>\n")
				.append(" </SOAP-ENV:Body>\n").append("</SOAP-ENV:Envelope>\n");
		String msg = sB.toString();
		pW.print("HTTP/1.0 500 Internal Error\r\n");
		pW.print("Content-Type: text/xml; charset=utf-8\r\n");
		pW.print("Content-Length: " + msg.length() + "\r\n\r\n");
		pW.print(msg);
		pW.flush();
	}

	/**
	 * Sends response message including authorization request to the client
	 * 
	 * @param ex
	 *            exception message
	 * @param pW
	 *            stream to which response message is sent
	 */
	public void sendAuthorizationException(Exception ex, PrintWriter pW) {
		StringBuffer sB = new StringBuffer();
		sB.append("<p>Security Error:" + ex + "</p>\n");
		String msg = sB.toString();
		pW.print("HTTP/1.0 401 Unauthorized\r\n");
		pW
				.print("WWW-Authenticate: Basic realm=\"FamilyHistoryServiceServer Data\"\r\n");
		pW.print("Content-Type: text/html; charset=utf-8\r\n");
		pW.print("Content-Length: " + msg.length() + "\r\n\r\n");
		pW.print(msg);
		pW.flush();
	}

	/**
	 * Reads line from reader. Used in HTTP header processing
	 * 
	 * @param reader
	 *            stream to read from
	 * @throws Exception
	 *             if header not correctly terminated
	 */
	public String readLine(Reader reader) throws Exception {
		StringBuffer sB = new StringBuffer();
		int ch;
		for (ch = reader.read(); ch >= 0 && ch != '\r'; ch = reader.read())
			sB.append((char) ch);
		if (ch < 0)
			return sB.toString();
		ch = reader.read();
		if (ch != '\n')
			throw new Exception("line [" + sB.toString() + "] lacks \\n");
		return sB.toString();
	}

	/**
	 * Reads HTTP headers and separates message body,returning a Hashtable
	 * object, keyed by HTTP header field name.
	 * 
	 * <pre>
	 * METHOD URL HTTP/1.1, headers, value
	 * result.get(&quot;METHOD&quot;),...get(&quot;URL&quot;),&quot;HTTP&quot;,...&quot;Content-Length&quot;), &quot;HTTP_BODY&quot;;
	 * decodes &quot; Authorization: Basic cGvcmU6cGRcmU= &quot;
	 * </pre>
	 * 
	 * @param reader
	 *            stream from which to read HTTP request message
	 **/
	public Hashtable readHttpData(Reader reader) throws Exception {
		Hashtable hashtable = new Hashtable();
		hashtable.put("CONTENT-LENGTH", "0"); // default
		String cmdLine = readLine(reader);
		int firstBlank = cmdLine.indexOf(' ');
		int lastBlank = cmdLine.lastIndexOf(' ');
		if (firstBlank < 0 || firstBlank == lastBlank)
			throw new Exception("Invalid HTTP method line [" + cmdLine + "]");
		hashtable.put("METHOD", cmdLine.substring(0, firstBlank));
		hashtable.put("URL", cmdLine.substring(1 + firstBlank, lastBlank));
		hashtable.put("HTTP", cmdLine.substring(1 + lastBlank));
		;
		String hdr = readLine(reader);
		while (hdr.length() > 0) {
			String[] nameVal = hdr.split(": ", 2);
			if (nameVal.length > 1)
				hashtable.put(nameVal[0].toUpperCase(), nameVal[1]);
			// int n=hdr.indexOf(':');
			// if(n>=0)hashtable.put(hdr.substring(0,n),hdr.substring(n+1).trim()
			// );
			hdr = readLine(reader);
		}
		System.out.println("Content-Length=[" + hashtable.get("CONTENT-LENGTH")
				+ "]");
		int len = Integer.parseInt((String) hashtable.get("CONTENT-LENGTH"));
		hashtable.put("HTTP_BODY", readUpToLength(reader, len));
		if (null != readRequestURL)
			addURLArgs(hashtable, (String) hashtable.get("URL"));
		System.out.println("URLDecoded=["
				+ new java.net.URLDecoder().decode((String) hashtable
						.get("URL"), "utf-8") + "]");
		return hashtable;
	}

	/**
	 * Picks arguments out of a URL and adds into hashtable. Used for GET method
	 * proceessing.
	 * 
	 * @param hT
	 *            hashtable to which arguments are added as key, value pairs
	 * @param url
	 *            URL containing the arguments
	 */
	public static void addURLArgs(Hashtable hT, String url) throws Exception {
		int qLoc = url.indexOf('?');
		if (qLoc < 0)
			return;
		URLDecoder decoder = new URLDecoder();
		String[] nVs = url.substring(1 + qLoc).split("[&=]");
		hT.put("URL", url.substring(0, qLoc));
		for (int i = 1; i < nVs.length; i += 2)
			hT.put(nVs[i - 1], decoder.decode(nVs[i], "utf-8"));
	}

	/**
	 * Reads specified number of characters from stream reader
	 * 
	 * @param isr
	 *            name of stream reader
	 * @param len
	 *            number of characters to read
	 * @throws Exception
	 *             if error detected
	 */
	public String readUpToLength(Reader isr, int len) throws Exception {
		StringWriter sw = new StringWriter();
		try {
			int ch;
			while (len-- > 0 && 0 < (ch = isr.read())) {
				sw.write((char) ch);
			}
			return sw.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("**[\n" + sw.toString() + "\n]**");
			throw (ex);
		}
	}

	/**
	 * Checks user response to authorization request. Most of code is commented
	 * out; just returns <code>true</code>.
	 * 
	 * @param httpData
	 *            has table containing response to authorization request
	 * @throws Exception
	 *             if authorization is invalid (currently disabled)
	 */
	public boolean authorized(Hashtable httpData) throws Exception {
		/*
		 * httpData.put("Now",getRfc1123DateFormat().format(new
		 * java.util.Date())); httpData.put("userGroup",""); String
		 * auth=(String)httpData.get("AUTHORIZATION"); if(auth==null ||
		 * !auth.startsWith("Basic "))return true; // okay for GET queries //
		 * public static byte[] decode( byte[] base64Data ) auth=new
		 * String(Base64.decode(auth.substring(6).getBytes())); int
		 * colLoc=auth.indexOf(':'); if(colLoc<0)throw new
		 * Exception("invalid Basic Authorization: '"+auth+"'"); String
		 * userID=auth.substring(0,colLoc); String pwd=auth.substring(colLoc+1);
		 * 
		 * if(!pwd.equals(userPwd.getProperty(userID))) throw new
		 * Exception("invalid Authorization: userID/pwd='"
		 * +userID+"'/'"+pwd+"'"); httpData.put("userID",userID);
		 * httpData.put("pwd",pwd);
		 * httpData.put("userGroup",userGroup.getProperty(userID));
		 */
		return true;
	}

	/**
	 * Performs action specified in GET message. All we do here is to return an
	 * XHTML document; the document name is taken from the URL; handling of I/O
	 * errors needs tidying up
	 * 
	 * @param httpData
	 *            hashtable containing GET message decoded into key-value pairs
	 * @param pW
	 *            stream to which to send output
	 * @throws Exception
	 *             if error detected
	 */
	public void doGet(Hashtable httpData, PrintWriter pW) throws Exception {
		StringBuffer response = new StringBuffer();
		String line;
		// response.append(
		// "<html><head><title>My document</title></head><body><h1>My new document</h1><p>URL "
		// );
		// response.append((String)httpData.get("URL"));
		// response.append("</p></body></html>");
		// read file; name hard-wired; write into StringBuffer
		boolean isImage = false;
		String mimeType = null;
		try {
			String fileName = (String) httpData.get("URL");
			fileName = fileName.substring(1, fileName.length());
			System.out.println("Request for file " + fileName);
			mimeType = new MimetypesFileTypeMap().getContentType(fileName);
			if (mimeType.indexOf("image") != -1)
				isImage = true;
			FileReader input = new FileReader(fileName);
			BufferedReader bufRead = new BufferedReader(input);
			// read first line
			line = bufRead.readLine();
			response.append(line);
			response.append("\n");
			// read subsequent lines
			while (line != null) {
				line = bufRead.readLine();
				if (line != null)
					response.append(line);
				response.append("\n");
			}
			bufRead.close();
		} catch (IOException e) {
			writeXHTMLPage(e.getMessage(), pW, isImage, mimeType); // needs
			// fixing;
			// should
			// be
			// giving HTTP error
		}
		writeXHTMLPage(response.toString(), pW, isImage, mimeType);
	}

	/**
	 * Performs action specified in POST message; takes the action from the
	 * SOAPAction header. Note SOAP 1.2 doesn't use a SOAPAction header to
	 * identify the action. Invokes another method to actually do the action.
	 * The code checks that the request message is directed to the URL
	 * HOST/FamilyHistoryService
	 * 
	 * @param httpData
	 *            hashtable containing POST message decoded into key-value pairs
	 * @param pW
	 *            stream to which to send output
	 * @throws Exception
	 *             if error detected
	 */
	public void doPost(Hashtable httpData, PrintWriter pW) throws Exception {
		String serviceName = (String) httpData.get("URL");
		serviceName = serviceName.substring(1, serviceName.length());
		// This service provides access to the Family History Site given the
		// right credentials.
		if ("html/FamilyHistoryAccessService".equals(serviceName)) {
			String soapAction = (String) httpData.get("SOAPACTION");
			if ("LogIn".equals(pruneQuotes(soapAction))) {
				doLogIn(httpData, pW);
			} else if ("LogOut".equals(pruneQuotes(soapAction))) {
				doLogOut(httpData, pW);
			} else
				throw new Exception("POST with unknown SOAPAction:["
						+ soapAction + "]");
		}
		// This service creates the necessary Family History Site data after
		// successful login.
		else if ("html/FamilyHistoryCreateService".equals(serviceName)) {
			String soapAction = (String) httpData.get("SOAPACTION");
			if ("ProfileHolderRegistration".equals(pruneQuotes(soapAction))) {
				doProfileHolderRegistration(httpData, pW);
				inferRules();
			} else if ("FamilyMemberRegistration"
					.equals(pruneQuotes(soapAction))) {
				doRegisterFamilyMember(httpData, pW, 0);
			} else if ("RelationshipRegistration"
					.equals(pruneQuotes(soapAction))) {
				doRegisterFamilyRelationships(httpData, pW);
				inferRules();
			} else
				throw new Exception("POST with unknown SOAPAction:["
						+ soapAction + "]");
		}
		// This service updates the requested Family History Site data after
		// successful login.
		else if ("html/FamilyHistoryUpdateService".equals(serviceName)) {
			String soapAction = (String) httpData.get("SOAPACTION");
			if ("UpdateFamilyMemberProfile".equals(pruneQuotes(soapAction))) {
				doRegisterFamilyMember(httpData, pW, 1);
				inferRules();
			} else if ("UpdateProfileHolderProfile"
					.equals(pruneQuotes(soapAction))) {
				doEditProfileHolderProfile(httpData, pW);
			} else
				throw new Exception("POST with unknown SOAPAction:["
						+ soapAction + "]");
		}
		// This service reads and displays the requested Family History Site
		// data after successful login.
		else if ("html/FamilyHistoryReadService".equals(serviceName)) {
			String soapAction = (String) httpData.get("SOAPACTION");
			if ("LoadFamilyMembers".equals(pruneQuotes(soapAction))) {
				doLoadFamilyMembers(httpData, pW);
			} else if ("LoadFamilyMembersAndIndividuals"
					.equals(pruneQuotes(soapAction))) {
				doLoadFamilyMembersAndIndividuals(httpData, pW);
			} else if ("LoadNonFamilyMembers".equals(pruneQuotes(soapAction))) {
				doLoadNonFamilyMembers(httpData, pW);
			} else if ("LoadProfileHolderProfile"
					.equals(pruneQuotes(soapAction))) {
				doLoadProfile(httpData, pW, 0);
			} else if ("LoadFamilyMemberProfile"
					.equals(pruneQuotes(soapAction))) {
				doLoadProfile(httpData, pW, 1);
			} else if ("HeriditaryDiseases".equals(pruneQuotes(soapAction))) {
				doLoadHeriditaryDiseases(httpData, pW);
			} else if ("RelationshipView".equals(pruneQuotes(soapAction))) {
				doViewFamilyRelationships(httpData, pW);
			} else
				throw new Exception("POST with unknown SOAPAction:["
						+ soapAction + "]");
		}
		// This service produces the family history data in the form of JSON
		// file required for generating Exhibit visualization.
		else if ("html/FamilyHistoryVisualizationService".equals(serviceName)) {
			String soapAction = (String) httpData.get("SOAPACTION");
			if ("ExhibitMap".equals(pruneQuotes(soapAction))) {
				doGetJsonForExhibit(httpData, pW);
			} else if ("ExhibitTimeline".equals(pruneQuotes(soapAction))) {
				doGetJsonForExhibit(httpData, pW);
			} else
				throw new Exception("POST with unknown SOAPAction:["
						+ soapAction + "]");

		} else {
			throw new Exception("POST to unknown service:[" + serviceName + "]");
		}
	}

	/**
	 * Strips quotation marks off start and end of string. Used to remove
	 * quotation marks that some Web Services systems add to SOAPAction headers.
	 * 
	 * @param S
	 *            string to be stripped
	 */
	protected static String pruneQuotes(String S) { // ADDITION aug 9, for axis
		// quoted soapactions.
		if (S == null)
			return S;
		if (!S.startsWith("\"") || !S.endsWith("\""))
			return S;
		return S.substring(1, S.length() - 1);
	}

	/**
	 * Runs the Protege Pellet Reasoner
	 * 
	 */
	static void runProtegePelletReasoner() {
		ProtegeReasoner protegeReasoner = null;
		ReasonerManager reasonerManager = ReasonerManager.getInstance();
		protegeReasoner = reasonerManager.createProtegeReasoner(owlModel,
				ProtegePelletOWLAPIReasoner.class);
		try {
			protegeReasoner.computeInferredIndividualTypes();
		} catch (ProtegeReasonerException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Performs the SPARQL Query given the query string.
	 * 
	 * @param query
	 *            SPARQL query String File to be written to
	 */
	void doSPARQLQuery(String query) {

		String profileHolderLabel = (String) session.get("ProfileHolderID");
		String queryString = " PREFIX fh: <http://www.owl-ontologies.com/Ontology1275849080.owl#> "
				+ " PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
				+ " PREFIX  rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
				+ " PREFIX  owl: <http://www.w3.org/2002/07/owl#> "
				+ " select DISTINCT * where { ?subject fh:ancestorOf ?object "
				// +".}";
				// +". ?object fh:Heriditary ?heriditary"
				+ ". ?object owl:sameAs "
				+ "<http://www.owl-ontologies.com/Ontology1275849080.owl#"
				+ profileHolderLabel + "> . }";

		QueryExecution qexec = QueryExecutionFactory.create(QueryFactory
				.create(queryString), owlModel.getJenaModel());
		String ID = "";
		try {
			ResultSet results = qexec.execSelect();
			while (results.hasNext()) {
				QuerySolution soln = results.nextSolution();
				ID = soln.toString()
						.substring(soln.toString().indexOf("#") + 1);
				// ID = ID.substring(0, ID.length() - 3);
				System.out.println(ID);
			}
		} finally {
			if (qexec != null)
				qexec.close();
		}
	}

	/**
	 * This function checks the login details provided against the stored
	 * credentials to let access to the user.
	 * 
	 * @param httpData
	 *            hashtable of SOAP request message
	 * @param pW
	 *            stream to which output is to be written
	 * @throws Exception
	 *             if error is detected
	 */
	public void doLogIn(Hashtable httpData, PrintWriter pW) throws Exception {
		// HTTP_BODY key in hashtable retrieves the body of the HTTP request
		// message, i.e. the SOAP envelope
		String queryResult = null;
		String docString = (String) httpData.get("HTTP_BODY");
		if (null == docString || docString.length() == 0)
			throw new Exception("no body to HTTP request");
		// now need to process the SOAP envelope to find input parameters
		// here we just show how to set up a DOM document for this purpose,
		// then return a dummy answer
		Document doc = readDocument(docString);
		// validateDocument(doc, "obsEntry.xsd");
		NodeList nodeList = doc.getElementsByTagName("log");

		if (nodeList.getLength() != 0) {

			NamedNodeMap nodeMap = nodeList.item(0).getAttributes();
			String user = nodeMap.getNamedItem("user").getNodeValue();
			String inputPassword = nodeMap.getNamedItem("password")
					.getNodeValue();

			Collection collection = owlModel.getOWLNamedClass("ProfileHolder")
					.getInstances(true);
			Iterator iterator = collection.iterator();
			boolean userFound = false;
			while (iterator.hasNext() && !userFound) {
				DefaultOWLIndividual instance = (DefaultOWLIndividual) iterator
						.next();

				String instanceLabel = (String) (instance.getLocalName());

				Query query = new Query();

				String queryString = " for $b in doc(\"Login.xml\")/Login/person"
						+ " where  $b[id"
						+ " = \""
						+ instanceLabel
						+ "\"] "
						+ "return $b/email/text() ";
				String email = query.doQuery(queryString, "");
				String userInputEmail = user + "@familyhistory.com";

				if (userInputEmail.compareTo(email) == 0) {
					// check if password entered is correct
					userFound = true;

					queryString = " for $b in doc(\"Login.xml\")/Login/person"
							+ " where  $b[id" + " = \"" + instanceLabel
							+ "\"] " + "return $b/password/text()";
					String password = query.doQuery(queryString, "");

					// A session is created for the user loging in and user ID
					// and his profile holder lables are stored for the session.
					if (inputPassword.compareTo(password) == 0) {
						session = new SimpleSession();
						session.set("User", user);
						session.set("ProfileHolderID", instanceLabel);
						session.setTimeout(108000);
						queryResult = user;
					}

				}

			}

		}

		// if(queryResult==null)
		// queryResult = "Login Unsuccessful!";

		String res = "<log>" + queryResult + "</log>";
		StringBuffer response = new StringBuffer();
		response
				.append("<myapp:FamilyHistoryResponse xmlns:myapp=\"http://cms.brookes.ac.uk/staff/DADuce\">");
		response.append(res);

		response.append("</myapp:FamilyHistoryResponse>");

		writeSOAPResult(response.toString(), pW);

	}

	/**
	 *Protégé OWLModel is converted into a Jena OntModel, to get a static
	 * snapshot of the model at run time.
	 * 
	 * @throws OntologyLoadException
	 *             if error is detected
	 */
	public static void getowlModel() throws OntologyLoadException {
		owlModel = ProtegeOWL.createJenaOWLModelFromURI(uri);
		runProtegePelletReasoner();
	}

	/**
	 * This function logs out the user(Profile Holder) from the Family History
	 * site. The ongoing session is terminated by this service function.
	 * 
	 * @param httpData
	 *            hashtable of SOAP request message
	 * @param pW
	 *            stream to which output is to be written
	 * @throws Exception
	 *             if error is detected
	 */
	public void doLogOut(Hashtable httpData, PrintWriter pW) throws Exception {
		// HTTP_BODY key in hashtable retrieves the body of the HTTP request
		// message, i.e. the SOAP envelope
		String queryResult = null;
		String docString = (String) httpData.get("HTTP_BODY");
		if (null == docString || docString.length() == 0)
			throw new Exception("no body to HTTP request");

		Document doc = readDocument(docString);
		// validateDocument(doc, "obsEntry.xsd");
		NodeList nodeList = doc.getElementsByTagName("logout");

		if (nodeList.getLength() != 0) {

			NamedNodeMap nodeMap = nodeList.item(0).getAttributes();
			String user = nodeMap.getNamedItem("user").getNodeValue();

			// Delete the current session before loggin out.
			session.remove("User");
			session.remove("ProfileHolderID");

			// Clears up the previously created JSON file for visualization
			Writer output = null;
			try {
				output = new BufferedWriter(new FileWriter("family.js"));
				output.write("");

			} catch (IOException e) {
				e.printStackTrace();
			}

			finally {
				try {
					output.close();

				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}

		queryResult = "Logout Successful!";

		String res = "<logout>" + queryResult + "</logout>";
		StringBuffer response = new StringBuffer();
		response
				.append("<myapp:FamilyHistoryResponse xmlns:myapp=\"http://cms.brookes.ac.uk/staff/DADuce\">");
		response.append(res);

		response.append("</myapp:FamilyHistoryResponse>");

		writeSOAPResult(response.toString(), pW);

	}

	/**
	 * This function registers a new user/profile holder.
	 * 
	 * @param httpData
	 *            hashtable of SOAP request message
	 * @param pW
	 *            stream to which output is to be written
	 * @throws Exception
	 *             if error is detected
	 */
	public void doProfileHolderRegistration(Hashtable httpData, PrintWriter pW)
			throws Exception {
		String queryResult = null;
		String docString = (String) httpData.get("HTTP_BODY");
		if (null == docString || docString.length() == 0)
			throw new Exception("no body to HTTP request");

		Document doc = readDocument(docString);
		NodeList nodeList = doc.getElementsByTagName("register");

		if (nodeList.getLength() != 0) {
			// Gets the entered values from registration page
			NamedNodeMap nodeMap = nodeList.item(0).getAttributes();
			String inputFirstName = nodeMap.getNamedItem("firstName")
					.getNodeValue();
			String inputMiddleName = nodeMap.getNamedItem("middleName")
					.getNodeValue();
			String inputLastName = nodeMap.getNamedItem("lastName")
					.getNodeValue();
			String inputNickName = nodeMap.getNamedItem("nickName")
					.getNodeValue();
			int inputAge;
			String inputAddress = nodeMap.getNamedItem("address")
					.getNodeValue();
			boolean inputLiving = true;
			String inputGender = nodeMap.getNamedItem("gender").getNodeValue();
			String inputDate = nodeMap.getNamedItem("date").getNodeValue();
			String inputEmail = nodeMap.getNamedItem("email").getNodeValue();
			String inputPassword = nodeMap.getNamedItem("password")
					.getNodeValue();
			String inputImage = nodeMap.getNamedItem("image").getNodeValue();
			String inputDisease = nodeMap.getNamedItem("disease")
					.getNodeValue();
			String inputHeriditary = nodeMap.getNamedItem("heriditary")
					.getNodeValue();
			String inputBlood = nodeMap.getNamedItem("blood").getNodeValue();

			String lat = nodeMap.getNamedItem("latitude").getNodeValue();
			Float inputLatitude = Float.parseFloat(lat);

			String lng = nodeMap.getNamedItem("longitude").getNodeValue();
			Float inputLongitude = Float.parseFloat(lng);

			String inputOtherNotes = nodeMap.getNamedItem("other")
					.getNodeValue();

			// Creates a new label for the profile holder for unique
			// identification
			// This presently is not a proficient approach as
			// the auto-numbering needs to be done implicitly which takes care
			// of deletions and accordingly generates auto-numbering to fill in
			// those gaps.
			// Presently the auto-number being generated is
			// Total number of Profile Holders + 1
			// This API feature is available for Protege 4 plugins, the current
			// one being used is the older version 3.4

			owlModel = ProtegeOWL.createJenaOWLModelFromURI(uri);
			String prefixedName = owlModel.getOWLNamedClass("ProfileHolder")
					.getPrefixedName();
			int instancesCount = owlModel.getOWLNamedClass("ProfileHolder")
					.getInstanceCount(true);
			int autoNumber = instancesCount + 1;
			String newInstanceName = prefixedName + "_" + autoNumber;

			// The entered registration details obtained from SOAP message is
			// now saved against the newly created individual.
			OWLNamedClass namedClass = owlModel
					.getOWLNamedClass("ProfileHolder");
			OWLIndividual newInstance = namedClass
					.createOWLIndividual(newInstanceName);

			OWLDatatypeProperty firstNameProperty = owlModel
					.getOWLDatatypeProperty("FirstName");
			newInstance.setPropertyValue(firstNameProperty, inputFirstName);

			OWLDatatypeProperty middleNameProperty = owlModel
					.getOWLDatatypeProperty("MiddleName");
			newInstance.setPropertyValue(middleNameProperty, inputMiddleName);

			OWLDatatypeProperty lastNameProperty = owlModel
					.getOWLDatatypeProperty("LastName");
			newInstance.setPropertyValue(lastNameProperty, inputLastName);

			OWLDatatypeProperty nickNameProperty = owlModel
					.getOWLDatatypeProperty("NickName");
			newInstance.setPropertyValue(nickNameProperty, inputNickName);

			OWLDatatypeProperty emailProperty = owlModel
					.getOWLDatatypeProperty("Email");
			String formattedEmail = inputEmail + "@familyhistory.com";
			newInstance.setPropertyValue(emailProperty, formattedEmail);

			OWLDatatypeProperty genderProperty = owlModel
					.getOWLDatatypeProperty("Gender");
			newInstance.setPropertyValue(genderProperty, inputGender);

			OWLDatatypeProperty dateOfBirthProperty = owlModel
					.getOWLDatatypeProperty("DOB");
			Date date = new Date();
			DateFormat inputDateFormat = new SimpleDateFormat("dd/MM/yyyy");
			DateFormat newDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			date = inputDateFormat.parse(inputDate);
			String dateString = newDateFormat.format(date);
			RDFSDatatype xsdDate = owlModel.getRDFSDatatypeByName("xsd:date");
			RDFSLiteral dateLiteral = owlModel.createRDFSLiteral(dateString,
					xsdDate);
			newInstance.setPropertyValue(dateOfBirthProperty, dateLiteral);

			OWLDatatypeProperty latitudeProperty = owlModel
					.getOWLDatatypeProperty("Latitude");
			newInstance.setPropertyValue(latitudeProperty, inputLatitude);

			Date currentDate = new Date();
			DateFormat currentDateFormat = new SimpleDateFormat("dd/MM/yyyy");
			currentDateFormat.format(currentDate);
			Calendar currentCalendar = currentDateFormat.getCalendar();
			Calendar inputCalendar = inputDateFormat.getCalendar();
			inputAge = currentCalendar.get(Calendar.YEAR)
					- inputCalendar.get(Calendar.YEAR);
			RDFSDatatype xsdint = owlModel.getRDFSDatatypeByName("xsd:int");
			RDFSLiteral intLiteral = owlModel.createRDFSLiteral(Integer
					.toString(inputAge), xsdint);
			OWLDatatypeProperty ageProperty = owlModel
					.getOWLDatatypeProperty("Age");
			newInstance.setPropertyValue(ageProperty, intLiteral);

			OWLDatatypeProperty livingProperty = owlModel
					.getOWLDatatypeProperty("Living");
			newInstance.setPropertyValue(livingProperty, inputLiving);

			OWLDatatypeProperty imageProperty = owlModel
					.getOWLDatatypeProperty("ImageURL");
			newInstance.setPropertyValue(imageProperty, inputImage);

			OWLDatatypeProperty diseaseProperty = owlModel
					.getOWLDatatypeProperty("Diseases");
			newInstance.setPropertyValue(diseaseProperty, inputDisease);

			OWLDatatypeProperty heriditaryProperty = owlModel
					.getOWLDatatypeProperty("Heriditary");
			newInstance.setPropertyValue(heriditaryProperty, inputHeriditary);

			OWLDatatypeProperty bloodProperty = owlModel
					.getOWLDatatypeProperty("BloodGroup");
			newInstance.setPropertyValue(bloodProperty, inputBlood);

			OWLDatatypeProperty addressProperty = owlModel
					.getOWLDatatypeProperty("Address");
			newInstance.setPropertyValue(addressProperty, inputAddress);

			OWLDatatypeProperty longitudeProperty = owlModel
					.getOWLDatatypeProperty("Longitude");
			newInstance.setPropertyValue(longitudeProperty, inputLongitude);

			OWLDatatypeProperty otherProperty = owlModel
					.getOWLDatatypeProperty("OtherNotes");
			newInstance.setPropertyValue(otherProperty, inputOtherNotes);

			// String newProfileHolder = "<person id=\"" + newInstanceName +
			// "\" email=\"" + inputEmail+"@familyhistory.com"
			// + "\" password=\"" + inputPassword + "\" />";

			Document xmldoc = parseURL("Login.xml");
			Element newProfileHolder = xmldoc.createElement("person");
			Element loginID = xmldoc.createElement("id");
			loginID.setTextContent(newInstanceName);
			Element loginEmail = xmldoc.createElement("email");
			loginEmail.setTextContent(inputEmail + "@familyhistory.com");
			Element loginPassword = xmldoc.createElement("password");
			loginPassword.setTextContent(inputPassword);

			newProfileHolder.appendChild(loginID);
			newProfileHolder.appendChild(loginEmail);
			newProfileHolder.appendChild(loginPassword);

			xmldoc.getDocumentElement().appendChild(newProfileHolder);

			Query query = new Query();

			// The username and passwaor is saved in against the login
			// credentials.
			String queryString = " for $b in doc(\"Login.xml\")/Login/person"
					+ " where  $b[email" + " = \"" + inputEmail
					+ "@familyhistory.com" + "\"] " + "return $b/email/text() ";
			String email = query.doQuery(queryString, "");
			boolean userExists = false;
			if ((inputEmail + "@familyhistory.com").compareTo(email) == 0) {
				// check if password entered is correct
				userExists = true;
				queryResult = "Profile Holder already registered! Please login with your registered Email and Password.";
			}

			if (!userExists) {
				writeDocument("Login.xml", xmldoc);
				// Saves the model with the new Profile Holder Details.
				saveOwlModel();
				queryResult = "Profile Creation Successful!";
			}

		}

		String res = "<register>" + queryResult + "</register>";
		StringBuffer response = new StringBuffer();
		response
				.append("<myapp:FamilyHistoryResponse xmlns:myapp=\"http://cms.brookes.ac.uk/staff/DADuce\">");
		response.append(res);

		response.append("</myapp:FamilyHistoryResponse>");

		writeSOAPResult(response.toString(), pW);

	}

	/**
	 * This function queries and loads the profile holder’s data as well as
	 * family members' data.
	 * 
	 * @param httpData
	 *            hash table of SOAP request message
	 * @param pW
	 *            stream to which output is to be written
	 * @param flag
	 *            Loads profile holder's data if '0', and if '1' loads family
	 *            member's data
	 * @throws Exception
	 *             if error is detected
	 */
	public void doLoadProfile(Hashtable httpData, PrintWriter pW, int flag)
			throws Exception {

		String queryResult = null;
		String docString = (String) httpData.get("HTTP_BODY");
		String profileID = null;
		if (null == docString || docString.length() == 0)
			throw new Exception("no body to HTTP request");

		Document doc = readDocument(docString);
		NodeList nodeList = doc.getElementsByTagName("loadprofile");
		String inputFirstName = "", inputMiddleName = " ", inputLastName = " ", inputNickName = " ", inputEmail = " ", inputGender = "", DD = "", MM = "", YY = "", DDD = "", MMM = "", YYY = "", inputImage = "", inputLiving = "";
		String inputDisease = " ", inputHeriditary = " ", inputBlood = "", inputAddress = "", inputLatitude = "", inputLongitude = "", inputOtherNotes = " ";

		if (nodeList.getLength() != 0) {

			NamedNodeMap nodeMap = nodeList.item(0).getAttributes();

			if (flag == 1)
				profileID = nodeMap.getNamedItem("profileID").getNodeValue();
		}

		// If flag = 0, loads the profile holder's details. The Profile Holder's
		// ID is retrieved from the current session to load his details.
		String profileHolderLabel = (String) session.get("ProfileHolderID");

		// If flag = 1, loads family member's profile, hence the profile ID
		// received from soap message is used to get the details stored against
		// it.
		if (flag == 1)
			profileHolderLabel = profileID;

		OWLIndividual profileHolder = owlModel
				.getOWLIndividual(profileHolderLabel);

		OWLDatatypeProperty firstNameProperty = owlModel
				.getOWLDatatypeProperty("FirstName");
		inputFirstName = profileHolder.getPropertyValue(firstNameProperty)
				.toString();

		OWLDatatypeProperty middleNameProperty = owlModel
				.getOWLDatatypeProperty("MiddleName");
		if (profileHolder.getPropertyValue(middleNameProperty) != null)
			inputMiddleName = profileHolder
					.getPropertyValue(middleNameProperty).toString();

		OWLDatatypeProperty lastNameProperty = owlModel
				.getOWLDatatypeProperty("LastName");
		if (profileHolder.getPropertyValue(lastNameProperty) != null)
			inputLastName = profileHolder.getPropertyValue(lastNameProperty)
					.toString();

		OWLDatatypeProperty emailProperty = owlModel
				.getOWLDatatypeProperty("Email");
		if (flag == 0) {
			inputEmail = profileHolder.getPropertyValue(emailProperty)
					.toString();
			inputEmail = inputEmail.substring(0, inputEmail.indexOf("@"));
		}

		OWLDatatypeProperty genderProperty = owlModel
				.getOWLDatatypeProperty("Gender");
		inputGender = profileHolder.getPropertyValue(genderProperty).toString();

		Date date = new Date();
		OWLDatatypeProperty dateOfBirthProperty = owlModel
				.getOWLDatatypeProperty("DOB");

		RDFSLiteral dobLiteral = (RDFSLiteral) profileHolder
				.getPropertyValue(dateOfBirthProperty);
		if (dobLiteral != null) {
			String inputDate = dobLiteral.toString();
			DateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			date = inputDateFormat.parse(inputDate);
			Calendar cal = inputDateFormat.getCalendar();
			DD = Integer.toString(cal.get(Calendar.DAY_OF_MONTH));
			MM = Integer.toString(cal.get(Calendar.MONTH) + 1);
			YY = Integer.toString(cal.get(Calendar.YEAR));
		}

		// The date of death is searched for only a non-profile holder.
		// The project assumes that a profile holder is always living to be able
		// to use the software
		// If no more, the profile holder's credentials are stored as any other
		// individuals profile data.
		// He is no longer called a profile holder thereafter.

		if (flag == 1) {
			OWLDatatypeProperty dateOfDeathProperty = owlModel
					.getOWLDatatypeProperty("DOD");
			RDFSLiteral dodLiteral = (RDFSLiteral) profileHolder
					.getPropertyValue(dateOfDeathProperty);
			if (dodLiteral != null) {
				String inputDDate = dodLiteral.toString();
				Date ddate = new Date();
				DateFormat inputDDateFormat = new SimpleDateFormat("yyyy-MM-dd");
				date = inputDDateFormat.parse(inputDDate);
				Calendar cald = inputDDateFormat.getCalendar();
				DDD = Integer.toString(cald.get(Calendar.DAY_OF_MONTH));
				MMM = Integer.toString(cald.get(Calendar.MONTH) + 1);
				YYY = Integer.toString(cald.get(Calendar.YEAR));
			}
		}

		OWLDatatypeProperty nickNameProperty = owlModel
				.getOWLDatatypeProperty("NickName");
		if (profileHolder.getPropertyValue(nickNameProperty) != null)
			inputNickName = profileHolder.getPropertyValue(nickNameProperty)
					.toString();

		OWLDatatypeProperty livingProperty = owlModel
				.getOWLDatatypeProperty("Living");
		Boolean living = (Boolean) profileHolder
				.getPropertyValue(livingProperty);
		if (living)
			inputLiving = "Yes";
		else
			inputLiving = "No";

		OWLDatatypeProperty diseaseProperty = owlModel
				.getOWLDatatypeProperty("Diseases");
		if (profileHolder.getPropertyValue(diseaseProperty) != null)
			inputDisease = profileHolder.getPropertyValue(diseaseProperty)
					.toString();

		OWLDatatypeProperty heriditaryProperty = owlModel
				.getOWLDatatypeProperty("Heriditary");
		if (profileHolder.getPropertyValue(heriditaryProperty) != null)
			inputHeriditary = profileHolder
					.getPropertyValue(heriditaryProperty).toString();

		OWLDatatypeProperty bloodProperty = owlModel
				.getOWLDatatypeProperty("BloodGroup");
		if (profileHolder.getPropertyValue(bloodProperty) != null)
			inputBlood = profileHolder.getPropertyValue(bloodProperty)
					.toString();

		OWLDatatypeProperty addressProperty = owlModel
				.getOWLDatatypeProperty("Address");
		inputAddress = profileHolder.getPropertyValue(addressProperty)
				.toString();

		OWLDatatypeProperty imageURLProperty = owlModel
				.getOWLDatatypeProperty("ImageURL");
		inputImage = profileHolder.getPropertyValue(imageURLProperty)
				.toString();

		OWLDatatypeProperty latitudeProperty = owlModel
				.getOWLDatatypeProperty("Latitude");
		inputLatitude = profileHolder.getPropertyValue(latitudeProperty)
				.toString();

		OWLDatatypeProperty longitudeProperty = owlModel
				.getOWLDatatypeProperty("Longitude");
		inputLongitude = profileHolder.getPropertyValue(longitudeProperty)
				.toString();

		OWLDatatypeProperty otherProperty = owlModel
				.getOWLDatatypeProperty("OtherNotes");
		if (profileHolder.getPropertyValue(otherProperty) != null)
			inputOtherNotes = profileHolder.getPropertyValue(otherProperty)
					.toString();
		String inputPassword = "";

		// Password is retrieved as well but only for the profile holder ie.
		// when flag = 0.
		// A non-profile holder does not have login credentials stored against
		// his profile created by any profile holder.

		if (flag == 0) {
			String queryString = " for $b in doc(\"Login.xml\")/Login/person"
					+ " where  $b[id" + " = \"" + profileHolderLabel + "\"] "
					+ "return $b/password/text()";

			Query query = new Query();
			inputPassword = query.doQuery(queryString, "");
		}
		queryResult = "<firstname>" + inputFirstName + "</firstname>";
		queryResult = queryResult + "<middlename>" + inputMiddleName
				+ "</middlename>";
		queryResult = queryResult + "<lastname>" + inputLastName
				+ "</lastname>";
		queryResult = queryResult + "<nickname>" + inputNickName
				+ "</nickname>";
		queryResult = queryResult + "<gender>" + inputGender + "</gender>";
		queryResult = queryResult + "<dd>" + DD + "</dd>";
		queryResult = queryResult + "<mm>" + MM + "</mm>";
		queryResult = queryResult + "<yy>" + YY + "</yy>";
		queryResult = queryResult + "<living>" + inputLiving + "</living>";
		queryResult = queryResult + "<imageurl>" + inputImage + "</imageurl>";
		if (flag == 0) {
			queryResult = queryResult + "<email>" + inputEmail + "</email>";
			queryResult = queryResult + "<password>" + inputPassword
					+ "</password>";
		} else {
			queryResult = queryResult + "<ddd>" + DDD + "</ddd>";
			queryResult = queryResult + "<mmm>" + MMM + "</mmm>";
			queryResult = queryResult + "<yyy>" + YYY + "</yyy>";
		}
		queryResult = queryResult + "<disease>" + inputDisease + "</disease>";
		queryResult = queryResult + "<heriditary>" + inputHeriditary
				+ "</heriditary>";
		queryResult = queryResult + "<blood>" + inputBlood + "</blood>";
		queryResult = queryResult + "<address>" + inputAddress + "</address>";
		queryResult = queryResult + "<latitude>" + inputLatitude
				+ "</latitude>";
		queryResult = queryResult + "<longitude>" + inputLongitude
				+ "</longitude>";
		queryResult = queryResult + "<othernotes>" + inputOtherNotes
				+ "</othernotes>";

		String res = "<loadprofile>" + queryResult + "</loadprofile>";
		StringBuffer response = new StringBuffer();
		response
				.append("<myapp:FamilyHistoryResponse xmlns:myapp=\"http://cms.brookes.ac.uk/staff/DADuce\">");
		response.append(res);

		response.append("</myapp:FamilyHistoryResponse>");

		writeSOAPResult(response.toString(), pW);

	}

	/**
	 * This function updates the family member data keyed in by the user/profile
	 * holder.
	 * 
	 * @param httpData
	 *            hashtable of SOAP request message
	 * @param pW
	 *            stream to which output is to be written
	 * @throws Exception
	 *             if error is detected
	 */
	public void doEditProfileHolderProfile(Hashtable httpData, PrintWriter pW)
			throws Exception {
		String queryResult = null;
		String docString = (String) httpData.get("HTTP_BODY");
		if (null == docString || docString.length() == 0)
			throw new Exception("no body to HTTP request");

		Document doc = readDocument(docString);
		NodeList nodeList = doc.getElementsByTagName("editprofile");

		if (nodeList.getLength() != 0) {
			// Gets the data updated from the edit profile page
			NamedNodeMap nodeMap = nodeList.item(0).getAttributes();
			String inputFirstName = nodeMap.getNamedItem("firstName")
					.getNodeValue();
			String inputMiddleName = nodeMap.getNamedItem("middleName")
					.getNodeValue();
			String inputLastName = nodeMap.getNamedItem("lastName")
					.getNodeValue();
			String inputNickName = nodeMap.getNamedItem("nickName")
					.getNodeValue();
			String inputDisease = nodeMap.getNamedItem("disease")
					.getNodeValue();
			String inputHeriditary = nodeMap.getNamedItem("heriditary")
					.getNodeValue();
			String inputBlood = nodeMap.getNamedItem("blood").getNodeValue();

			int inputAge;
			String inputAddress = nodeMap.getNamedItem("address")
					.getNodeValue();
			boolean inputLiving = true;
			String inputGender = nodeMap.getNamedItem("gender").getNodeValue();
			String inputDate = nodeMap.getNamedItem("date").getNodeValue();
			String inputEmail = nodeMap.getNamedItem("email").getNodeValue();
			String inputPassword = nodeMap.getNamedItem("password")
					.getNodeValue();

			String lat = nodeMap.getNamedItem("latitude").getNodeValue();
			Float inputLatitude = Float.parseFloat(lat);

			String lng = nodeMap.getNamedItem("longitude").getNodeValue();
			Float inputLongitude = Float.parseFloat(lng);

			String inputOtherNotes = nodeMap.getNamedItem("other")
					.getNodeValue();

			// Gets the profileholderID stored in the session and extracts the
			// individual for the updated information to be saved against.
			String profileHolderLabel = (String) session.get("ProfileHolderID");

			OWLNamedClass namedClass = owlModel
					.getOWLNamedClass("ProfileHolder");
			OWLIndividual profileHolder = owlModel
					.getOWLIndividual(profileHolderLabel);

			OWLDatatypeProperty firstNameProperty = owlModel
					.getOWLDatatypeProperty("FirstName");
			profileHolder.setPropertyValue(firstNameProperty, inputFirstName);

			OWLDatatypeProperty middleNameProperty = owlModel
					.getOWLDatatypeProperty("MiddleName");
			profileHolder.setPropertyValue(middleNameProperty, inputMiddleName);

			OWLDatatypeProperty lastNameProperty = owlModel
					.getOWLDatatypeProperty("LastName");
			profileHolder.setPropertyValue(lastNameProperty, inputLastName);

			OWLDatatypeProperty nickNameProperty = owlModel
					.getOWLDatatypeProperty("NickName");
			profileHolder.setPropertyValue(nickNameProperty, inputNickName);

			OWLDatatypeProperty emailProperty = owlModel
					.getOWLDatatypeProperty("Email");
			String formattedEmail = inputEmail + "@familyhistory.com";
			profileHolder.setPropertyValue(emailProperty, formattedEmail);

			OWLDatatypeProperty genderProperty = owlModel
					.getOWLDatatypeProperty("Gender");
			profileHolder.setPropertyValue(genderProperty, inputGender);

			OWLDatatypeProperty dateOfBirthProperty = owlModel
					.getOWLDatatypeProperty("DOB");
			Date date = new Date();
			DateFormat inputDateFormat = new SimpleDateFormat("dd/MM/yyyy");
			DateFormat newDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			date = inputDateFormat.parse(inputDate);
			String dateString = newDateFormat.format(date);
			RDFSDatatype xsdDate = owlModel.getRDFSDatatypeByName("xsd:date");
			RDFSLiteral dateLiteral = owlModel.createRDFSLiteral(dateString,
					xsdDate);
			profileHolder.setPropertyValue(dateOfBirthProperty, dateLiteral);

			OWLDatatypeProperty latitudeProperty = owlModel
					.getOWLDatatypeProperty("Latitude");
			profileHolder.setPropertyValue(latitudeProperty, inputLatitude);

			Date currentDate = new Date();
			DateFormat currentDateFormat = new SimpleDateFormat("dd/MM/yyyy");
			currentDateFormat.format(currentDate);
			Calendar currentCalendar = currentDateFormat.getCalendar();
			Calendar inputCalendar = inputDateFormat.getCalendar();
			inputAge = currentCalendar.get(Calendar.YEAR)
					- inputCalendar.get(Calendar.YEAR);
			RDFSDatatype xsdint = owlModel.getRDFSDatatypeByName("xsd:int");
			RDFSLiteral intLiteral = owlModel.createRDFSLiteral(Integer
					.toString(inputAge), xsdint);
			OWLDatatypeProperty ageProperty = owlModel
					.getOWLDatatypeProperty("Age");
			profileHolder.setPropertyValue(ageProperty, intLiteral);

			OWLDatatypeProperty livingProperty = owlModel
					.getOWLDatatypeProperty("Living");
			profileHolder.setPropertyValue(livingProperty, inputLiving);

			OWLDatatypeProperty addressProperty = owlModel
					.getOWLDatatypeProperty("Address");
			profileHolder.setPropertyValue(addressProperty, inputAddress);

			OWLDatatypeProperty diseaseProperty = owlModel
					.getOWLDatatypeProperty("Diseases");
			profileHolder.setPropertyValue(diseaseProperty, inputDisease);

			OWLDatatypeProperty heriditaryProperty = owlModel
					.getOWLDatatypeProperty("Heriditary");
			profileHolder.setPropertyValue(heriditaryProperty, inputHeriditary);

			OWLDatatypeProperty bloodProperty = owlModel
					.getOWLDatatypeProperty("BloodGroup");
			profileHolder.setPropertyValue(bloodProperty, inputBlood);

			OWLDatatypeProperty longitudeProperty = owlModel
					.getOWLDatatypeProperty("Longitude");
			profileHolder.setPropertyValue(longitudeProperty, inputLongitude);

			OWLDatatypeProperty otherProperty = owlModel
					.getOWLDatatypeProperty("OtherNotes");
			profileHolder.setPropertyValue(otherProperty, inputOtherNotes);

			Document xmldoc = parseURL("Login.xml");

			// Edited login credentials are also updated.
			XPath xpath = XPathFactory.newInstance().newXPath();
			NodeList nodes = (NodeList) xpath.evaluate("//person[email='"
					+ inputEmail + "@familyhistory.com" + "']/password",
					xmldoc, XPathConstants.NODESET);

			// Rename these nodes
			for (int idx = 0; idx < nodes.getLength(); idx++) {
				nodes.item(idx).setTextContent(inputPassword);
			}

			writeDocument("Login.xml", xmldoc);
			saveOwlModel();
			queryResult = "Profile Edit Successful!";

		}

		String res = "<editprofile>" + queryResult + "</editprofile>";
		StringBuffer response = new StringBuffer();
		response
				.append("<myapp:FamilyHistoryResponse xmlns:myapp=\"http://cms.brookes.ac.uk/staff/DADuce\">");
		response.append(res);

		response.append("</myapp:FamilyHistoryResponse>");

		writeSOAPResult(response.toString(), pW);

	}

	/**
	 * This function registers/updates the family member data keyed in by the
	 * user/profile holder.
	 * 
	 * @param httpData
	 *            hashtable of SOAP request message
	 * @param pW
	 *            stream to which output is to be written
	 * @param flag
	 *            Registers a family member data if '0', or updates the family
	 *            member data if '1'
	 * 
	 * @throws Exception
	 *             if error is detected
	 */
	public void doRegisterFamilyMember(Hashtable httpData, PrintWriter pW,
			int flag) throws Exception {
		String queryResult = null;
		String docString = (String) httpData.get("HTTP_BODY");
		if (null == docString || docString.length() == 0)
			throw new Exception("no body to HTTP request");

		Document doc = readDocument(docString);

		NodeList nodeList;

		if (flag == 1)
			nodeList = doc.getElementsByTagName("editfamilymemberprofile");
		else
			nodeList = doc.getElementsByTagName("registerfamilymember");

		if (nodeList.getLength() != 0) {
			// Gets the data embedded in the SOAP Message
			NamedNodeMap nodeMap = nodeList.item(0).getAttributes();
			String profileID = "";
			if (flag == 1)
				profileID = nodeMap.getNamedItem("profileID").getNodeValue();
			String inputFirstName = nodeMap.getNamedItem("firstName")
					.getNodeValue();
			String inputMiddleName = nodeMap.getNamedItem("middleName")
					.getNodeValue();

			String inputLastName = nodeMap.getNamedItem("lastName")
					.getNodeValue();

			String inputNickName = nodeMap.getNamedItem("nickName")
					.getNodeValue();

			String inputRelationship = null, inputRelative = null;
			if (flag == 0) {
				inputRelationship = nodeMap.getNamedItem("relationship")
						.getNodeValue();

				inputRelative = nodeMap.getNamedItem("relative").getNodeValue();
			}
			String inputAge = "";
			if (nodeMap.getNamedItem("age") != null)
				inputAge = nodeMap.getNamedItem("age").getNodeValue();

			String inputAddress = nodeMap.getNamedItem("address")
					.getNodeValue();
			String inputLiving = nodeMap.getNamedItem("living").getNodeValue();

			String inputGender = nodeMap.getNamedItem("gender").getNodeValue();
			String inputDate = nodeMap.getNamedItem("date").getNodeValue();
			String inputDDate = nodeMap.getNamedItem("ddate").getNodeValue();

			String inputDisease = nodeMap.getNamedItem("disease")
					.getNodeValue();
			String inputHeriditary = nodeMap.getNamedItem("heriditary")
					.getNodeValue();
			String inputBlood = nodeMap.getNamedItem("blood").getNodeValue();

			String lat = nodeMap.getNamedItem("latitude").getNodeValue();
			Float inputLatitude = Float.parseFloat(lat);

			String lng = nodeMap.getNamedItem("longitude").getNodeValue();
			Float inputLongitude = Float.parseFloat(lng);

			String inputOtherNotes = nodeMap.getNamedItem("other")
					.getNodeValue();

			// If flag = 0, a new NonProfileHolder ID is created and an
			// individual is created for the entered data to be stored into.
			String prefixedName = owlModel.getOWLNamedClass("NonProfileHolder")
					.getPrefixedName();
			int instancesCount = owlModel.getOWLNamedClass("NonProfileHolder")
					.getInstanceCount(true);
			int autoNumber = instancesCount + 1;
			String newInstanceName = prefixedName + "_" + autoNumber;

			OWLNamedClass namedClass = owlModel
					.getOWLNamedClass("NonProfileHolder");

			OWLIndividual newNonProfileHolder;

			// Gets the Family Member profile ID if flag = 1 to be able to
			// update the individual information stored against that profile ID
			if (flag == 1)
				newNonProfileHolder = namedClass.getOWLModel()
						.getOWLIndividual(profileID);
			else
				newNonProfileHolder = namedClass
						.createOWLIndividual(newInstanceName);

			OWLAllDifferent allDiff = owlModel.createOWLAllDifferent();

			// add relationship of the new family member added
			if (flag == 0) {
				OWLIndividual existingProfileHolder = owlModel
						.getOWLIndividual(inputRelative);
				OWLObjectProperty relationProperty = owlModel
						.getOWLObjectProperty(inputRelationship);
				existingProfileHolder.setPropertyValue(relationProperty,
						newNonProfileHolder);
			}

			OWLDatatypeProperty firstNameProperty = owlModel
					.getOWLDatatypeProperty("FirstName");
			newNonProfileHolder.setPropertyValue(firstNameProperty,
					inputFirstName);

			OWLDatatypeProperty middleNameProperty = owlModel
					.getOWLDatatypeProperty("MiddleName");
			newNonProfileHolder.setPropertyValue(middleNameProperty,
					inputMiddleName);

			OWLDatatypeProperty lastNameProperty = owlModel
					.getOWLDatatypeProperty("LastName");
			newNonProfileHolder.setPropertyValue(lastNameProperty,
					inputLastName);

			OWLDatatypeProperty nickNameProperty = owlModel
					.getOWLDatatypeProperty("NickName");
			newNonProfileHolder.setPropertyValue(nickNameProperty,
					inputNickName);

			OWLDatatypeProperty genderProperty = owlModel
					.getOWLDatatypeProperty("Gender");
			newNonProfileHolder.setPropertyValue(genderProperty, inputGender);

			if (inputAge.length() != 0) {
				RDFSDatatype xsdint = owlModel.getRDFSDatatypeByName("xsd:int");
				RDFSLiteral intLiteral = owlModel.createRDFSLiteral(inputAge,
						xsdint);
				OWLDatatypeProperty ageProperty = owlModel
						.getOWLDatatypeProperty("Age");
				newNonProfileHolder.setPropertyValue(ageProperty, intLiteral);
			}

			if (inputDate.compareTo("//") != 0) {
				OWLDatatypeProperty dateOfBirthProperty = owlModel
						.getOWLDatatypeProperty("DOB");
				Date date = new Date();
				DateFormat inputDateFormat = new SimpleDateFormat("dd/MM/yyyy");
				DateFormat newDateFormat = new SimpleDateFormat("yyyy-MM-dd");
				date = inputDateFormat.parse(inputDate);
				String dateString = newDateFormat.format(date);
				RDFSDatatype xsdDate = owlModel
						.getRDFSDatatypeByName("xsd:date");
				RDFSLiteral dateLiteral = owlModel.createRDFSLiteral(
						dateString, xsdDate);
				newNonProfileHolder.setPropertyValue(dateOfBirthProperty,
						dateLiteral);
			}

			if (inputDDate.compareTo("//") != 0) {
				OWLDatatypeProperty dateOfDeathProperty = owlModel
						.getOWLDatatypeProperty("DOD");
				Date ddate = new Date();
				DateFormat inputDateFormat = new SimpleDateFormat("dd/MM/yyyy");
				DateFormat newDateFormat = new SimpleDateFormat("yyyy-MM-dd");
				ddate = inputDateFormat.parse(inputDDate);
				String dateString = newDateFormat.format(ddate);
				RDFSDatatype xsdDate = owlModel
						.getRDFSDatatypeByName("xsd:date");
				RDFSLiteral dateLiteral = owlModel.createRDFSLiteral(
						dateString, xsdDate);
				newNonProfileHolder.setPropertyValue(dateOfDeathProperty,
						dateLiteral);
			}

			OWLDatatypeProperty latitudeProperty = owlModel
					.getOWLDatatypeProperty("Latitude");
			newNonProfileHolder.setPropertyValue(latitudeProperty,
					inputLatitude);

			OWLDatatypeProperty livingProperty = owlModel
					.getOWLDatatypeProperty("Living");
			RDFSDatatype xsdBoolean = owlModel
					.getRDFSDatatypeByName("xsd:boolean");
			String livingBoolean;
			if (inputLiving.compareTo("Yes") == 0)
				livingBoolean = "true";
			else
				livingBoolean = "false";

			RDFSLiteral livingLiteral = owlModel.createRDFSLiteral(
					livingBoolean, xsdBoolean);
			newNonProfileHolder.setPropertyValue(livingProperty, livingLiteral);

			OWLDatatypeProperty diseaseProperty = owlModel
					.getOWLDatatypeProperty("Diseases");
			newNonProfileHolder.setPropertyValue(diseaseProperty, inputDisease);

			OWLDatatypeProperty heriditaryProperty = owlModel
					.getOWLDatatypeProperty("Heriditary");
			newNonProfileHolder.setPropertyValue(heriditaryProperty,
					inputHeriditary);

			OWLDatatypeProperty bloodProperty = owlModel
					.getOWLDatatypeProperty("BloodGroup");
			newNonProfileHolder.setPropertyValue(diseaseProperty, inputBlood);

			OWLDatatypeProperty addressProperty = owlModel
					.getOWLDatatypeProperty("Address");
			newNonProfileHolder.setPropertyValue(addressProperty, inputAddress);

			OWLDatatypeProperty longitudeProperty = owlModel
					.getOWLDatatypeProperty("Longitude");
			newNonProfileHolder.setPropertyValue(longitudeProperty,
					inputLongitude);

			OWLDatatypeProperty otherProperty = owlModel
					.getOWLDatatypeProperty("OtherNotes");
			newNonProfileHolder
					.setPropertyValue(otherProperty, inputOtherNotes);

			saveOwlModel();
			if (flag == 0)
				queryResult = "Family member registration successful!";
			else
				queryResult = "Family member data updated!";

		}

		String res;
		if (flag == 1)
			res = "<editfamilymemberprofile>" + queryResult
					+ "</editfamilymemberprofile>";
		else
			res = "<registerfamilymember>" + queryResult
					+ "</registerfamilymember>";
		StringBuffer response = new StringBuffer();
		response
				.append("<myapp:FamilyHistoryResponse xmlns:myapp=\"http://cms.brookes.ac.uk/staff/DADuce\">");
		response.append(res);

		response.append("</myapp:FamilyHistoryResponse>");

		writeSOAPResult(response.toString(), pW);

	}

	/**
	 * This function queries and loads the names of all the family history site
	 * registered individuals who may be or may not be linked to the profile
	 * holder. It provides a means for searching for people who could
	 * potentially be family members of the profile holder and to add them to
	 * the existing family tree. The other family members can either be related
	 * to the profile holder or any of his existing family members.
	 * 
	 * 
	 * @param httpData
	 *            hashtable of SOAP request message
	 * @param pW
	 *            stream to which output is to be written
	 * @throws Exception
	 *             if error is detected
	 */
	public void doLoadFamilyMembersAndIndividuals(Hashtable httpData,
			PrintWriter pW) throws Exception {

		String queryResult = null;
		String docString = (String) httpData.get("HTTP_BODY");
		if (null == docString || docString.length() == 0)
			throw new Exception("no body to HTTP request");

		Document doc = readDocument(docString);

		NodeList nodeList1 = doc
				.getElementsByTagName("loadfamilyandindividuals");

		if (nodeList1.getLength() != 0) {

			NamedNodeMap nodeMap = nodeList1.item(0).getAttributes();
			String user = nodeMap.getNamedItem("user").getNodeValue();
		}

		// Family History Individuals

		Collection profileholders, nonprofileholders;
		profileholders = owlModel.getOWLNamedClass("ProfileHolder")
				.getInstances(true);

		nonprofileholders = owlModel.getOWLNamedClass("NonProfileHolder")
				.getInstances(true);

		// familyhistoryindividuals is a superset of all Profile Holders and non
		// Profile Holders. In essence it contains all the members registered.

		List familyhistoryindividuals = new ArrayList(profileholders);
		familyhistoryindividuals.addAll(nonprofileholders);

		String familyMembersxml = "", familyHistoryIndividualsxml = "";
		Iterator itr = familyhistoryindividuals.iterator();
		int i = 0;
		while (itr.hasNext()) {
			Object obj = itr.next();
			if (obj instanceof DefaultOWLIndividual) {
				familyHistoryIndividualsxml = familyHistoryIndividualsxml
						+ "<familyhistoryindividual>";
				DefaultOWLIndividual instance = (DefaultOWLIndividual) obj;
				String label = instance.getLocalName();

				String fullName;

				OWLDatatypeProperty firstNameProperty = owlModel
						.getOWLDatatypeProperty("FirstName");
				fullName = (String) instance
						.getPropertyValue(firstNameProperty);

				OWLDatatypeProperty middleNameProperty = owlModel
						.getOWLDatatypeProperty("MiddleName");
				fullName = fullName
						+ " "
						+ (String) instance
								.getPropertyValue(middleNameProperty);

				OWLDatatypeProperty lastNameProperty = owlModel
						.getOWLDatatypeProperty("LastName");
				fullName = fullName + " "
						+ (String) instance.getPropertyValue(lastNameProperty);

				familyHistoryIndividualsxml = familyHistoryIndividualsxml
						+ "<label>" + label + "</label>";
				familyHistoryIndividualsxml = familyHistoryIndividualsxml
						+ "<fullname>" + fullName + "</fullname>";
				familyHistoryIndividualsxml = familyHistoryIndividualsxml
						+ "</familyhistoryindividual>";

				i++;
			}

		}

		// Family members
		String profileHolderLabel = (String) session.get("ProfileHolderID");

		OWLIndividual profileHolder = owlModel
				.getOWLIndividual(profileHolderLabel);
		fullCollection = owlModel.getClses();
		Collection familyMembers = getFamily((DefaultOWLIndividual) profileHolder);

		familyMembersxml = "";
		itr = familyMembers.iterator();
		i = 0;
		while (itr.hasNext()) {
			Object obj = itr.next();
			if (obj instanceof DefaultOWLIndividual) {
				familyMembersxml = familyMembersxml + "<familymember>";
				DefaultOWLIndividual instance = (DefaultOWLIndividual) obj;
				String label = instance.getLocalName();

				String fullName;

				OWLDatatypeProperty firstNameProperty = owlModel
						.getOWLDatatypeProperty("FirstName");
				fullName = (String) instance
						.getPropertyValue(firstNameProperty);

				OWLDatatypeProperty middleNameProperty = owlModel
						.getOWLDatatypeProperty("MiddleName");
				fullName = fullName
						+ " "
						+ (String) instance
								.getPropertyValue(middleNameProperty);

				OWLDatatypeProperty lastNameProperty = owlModel
						.getOWLDatatypeProperty("LastName");
				fullName = fullName + " "
						+ (String) instance.getPropertyValue(lastNameProperty);

				familyMembersxml = familyMembersxml + "<label>" + label
						+ "</label>";
				familyMembersxml = familyMembersxml + "<fullname>" + fullName
						+ "</fullname>";
				familyMembersxml = familyMembersxml + "</familymember>";

				i++;
			}

		}
		// Creating atag that contains both all individuals packed together Plus
		// Another set containing only family members.

		String res = "<loadfamilyandindividuals>" + familyHistoryIndividualsxml
				+ familyMembersxml + "</loadfamilyandindividuals>";
		StringBuffer response = new StringBuffer();
		response
				.append("<myapp:FamilyHistoryResponse xmlns:myapp=\"http://cms.brookes.ac.uk/staff/DADuce\">");
		response.append(res);

		response.append("</myapp:FamilyHistoryResponse>");

		writeSOAPResult(response.toString(), pW);

	}

	/**
	 * This function queries and loads the names and profile IDs of all the
	 * individuals who are not(not yet added) family members of a profile
	 * holder. In essence, this loads all the other individuals with whom the
	 * profile holder is not linked to and provides a means of searching for
	 * people.
	 * 
	 * @param httpData
	 *            hashtable of SOAP request message
	 * @param pW
	 *            stream to which output is to be written
	 * @throws Exception
	 *             if error is detected
	 */

	public void doLoadNonFamilyMembers(Hashtable httpData, PrintWriter pW)
			throws Exception {

		String queryResult = null;
		String docString = (String) httpData.get("HTTP_BODY");
		if (null == docString || docString.length() == 0)
			throw new Exception("no body to HTTP request");

		Document doc = readDocument(docString);

		NodeList nodeList1 = doc.getElementsByTagName("loadnonfamilymembers");

		if (nodeList1.getLength() != 0) {

			NamedNodeMap nodeMap = nodeList1.item(0).getAttributes();
			String user = nodeMap.getNamedItem("user").getNodeValue();
		}

		// All Family History Individuals

		Collection profileholders, nonprofileholders;
		profileholders = owlModel.getOWLNamedClass("ProfileHolder")
				.getInstances(true);
		nonprofileholders = owlModel.getOWLNamedClass("NonProfileHolder")
				.getInstances(true);

		List familyhistoryindividuals = new ArrayList(profileholders);
		familyhistoryindividuals.addAll(nonprofileholders);

		// All Family members
		String profileHolderLabel = (String) session.get("ProfileHolderID");

		OWLIndividual profileHolder = owlModel
				.getOWLIndividual(profileHolderLabel);
		fullCollection = owlModel.getClses();
		Collection familyMembers = getFamily((DefaultOWLIndividual) profileHolder);

		// All Non Family Members = All Family History Individuals - All Family
		// members
		familyhistoryindividuals.removeAll(familyMembers);
		List nonfamilymembers = familyhistoryindividuals;

		String nonfamilyMembersxml = "";
		Iterator itr = nonfamilymembers.iterator();
		int i = 0;
		while (itr.hasNext()) {
			Object obj = itr.next();
			if (obj instanceof DefaultOWLIndividual) {
				nonfamilyMembersxml = nonfamilyMembersxml + "<nonfamilymember>";
				DefaultOWLIndividual instance = (DefaultOWLIndividual) obj;
				String label = instance.getLocalName();

				String fullName;

				OWLDatatypeProperty firstNameProperty = owlModel
						.getOWLDatatypeProperty("FirstName");
				fullName = (String) instance
						.getPropertyValue(firstNameProperty);

				OWLDatatypeProperty middleNameProperty = owlModel
						.getOWLDatatypeProperty("MiddleName");
				fullName = fullName
						+ " "
						+ (String) instance
								.getPropertyValue(middleNameProperty);

				OWLDatatypeProperty lastNameProperty = owlModel
						.getOWLDatatypeProperty("LastName");
				fullName = fullName + " "
						+ (String) instance.getPropertyValue(lastNameProperty);

				nonfamilyMembersxml = nonfamilyMembersxml + "<label>" + label
						+ "</label>";
				nonfamilyMembersxml = nonfamilyMembersxml + "<fullname>"
						+ fullName + "</fullname>";
				nonfamilyMembersxml = nonfamilyMembersxml
						+ "</nonfamilymember>";

				i++;
			}

		}

		String res = "<loadnonfamilymembers>" + nonfamilyMembersxml
				+ nonfamilyMembersxml + "</loadnonfamilymembers>";
		StringBuffer response = new StringBuffer();
		response
				.append("<myapp:FamilyHistoryResponse xmlns:myapp=\"http://cms.brookes.ac.uk/staff/DADuce\">");
		response.append(res);

		response.append("</myapp:FamilyHistoryResponse>");

		writeSOAPResult(response.toString(), pW);

	}

	/**
	 * This function queries and loads the names and Profile IDs of all family
	 * members of a profile holder.
	 * 
	 * @param httpData
	 *            hashtable of SOAP request message
	 * @param pW
	 *            stream to which output is to be written
	 * @throws Exception
	 *             if error is detected
	 */
	public void doLoadFamilyMembers(Hashtable httpData, PrintWriter pW)
			throws Exception {

		String queryResult = null;
		String docString = (String) httpData.get("HTTP_BODY");
		if (null == docString || docString.length() == 0)
			throw new Exception("no body to HTTP request");

		Document doc = readDocument(docString);

		NodeList nodeList = doc.getElementsByTagName("loadfamilymembers");

		if (nodeList.getLength() != 0) {

			NamedNodeMap nodeMap = nodeList.item(0).getAttributes();
			String user = nodeMap.getNamedItem("user").getNodeValue();
		}

		String profileHolderLabel = (String) session.get("ProfileHolderID");

		OWLIndividual profileHolder = owlModel
				.getOWLIndividual(profileHolderLabel);
		fullCollection = owlModel.getClses();
		Collection familyMembers = getFamily((DefaultOWLIndividual) profileHolder);

		String familyMembersxml = "";
		Iterator itr = familyMembers.iterator();
		int i = 0;
		while (itr.hasNext()) {
			Object obj = itr.next();
			if (obj instanceof DefaultOWLIndividual) {
				familyMembersxml = familyMembersxml + "<familymember>";
				DefaultOWLIndividual instance = (DefaultOWLIndividual) obj;
				String label = instance.getLocalName();

				String fullName;

				OWLDatatypeProperty firstNameProperty = owlModel
						.getOWLDatatypeProperty("FirstName");
				fullName = (String) instance
						.getPropertyValue(firstNameProperty);

				OWLDatatypeProperty middleNameProperty = owlModel
						.getOWLDatatypeProperty("MiddleName");
				fullName = fullName
						+ " "
						+ (String) instance
								.getPropertyValue(middleNameProperty);

				OWLDatatypeProperty lastNameProperty = owlModel
						.getOWLDatatypeProperty("LastName");
				fullName = fullName + " "
						+ (String) instance.getPropertyValue(lastNameProperty);

				familyMembersxml = familyMembersxml + "<label>" + label
						+ "</label>";
				familyMembersxml = familyMembersxml + "<fullname>" + fullName
						+ "</fullname>";
				familyMembersxml = familyMembersxml + "</familymember>";

				i++;
			}

		}

		String res = "<loadfamilymembers>" + familyMembersxml
				+ "</loadfamilymembers>";
		StringBuffer response = new StringBuffer();
		response
				.append("<myapp:FamilyHistoryResponse xmlns:myapp=\"http://cms.brookes.ac.uk/staff/DADuce\">");
		response.append(res);

		response.append("</myapp:FamilyHistoryResponse>");

		writeSOAPResult(response.toString(), pW);

	}

	/**
	 * This function queries and lists all the ancestral hereditary diseases of
	 * the profile holder.
	 * 
	 * @param httpData
	 *            hashtable of SOAP request message
	 * @param pW
	 *            stream to which output is to be written
	 * @throws Exception
	 *             if error is detected
	 */
	public void doLoadHeriditaryDiseases(Hashtable httpData, PrintWriter pW)
			throws Exception {
		String queryResult = null;
		String docString = (String) httpData.get("HTTP_BODY");
		if (null == docString || docString.length() == 0)
			throw new Exception("no body to HTTP request");

		Document doc = readDocument(docString);

		NodeList nodeList = doc.getElementsByTagName("heriditarydiseases");

		if (nodeList.getLength() != 0) {

			NamedNodeMap nodeMap = nodeList.item(0).getAttributes();
			// String user = nodeMap.getNamedItem("user").getNodeValue();
		}

		String profileHolderLabel = (String) session.get("ProfileHolderID");

		OWLIndividual profileHolder = owlModel
				.getOWLIndividual(profileHolderLabel);

		// doSPARQLQuery("");

		// Gets all the ancestor individuals
		ArrayList ancestors = getAncestors();

		OWLDatatypeProperty heriditaryProperty = owlModel
				.getOWLDatatypeProperty("Heriditary");
		Iterator ancestorsIter = ancestors.iterator();
		String heriditary = "";
		ArrayList<String> heriditartydiseasestext = new ArrayList();
		while (ancestorsIter.hasNext()) {
			Object obj = (OWLIndividual) ancestorsIter.next();
			if (obj instanceof DefaultOWLIndividual) {
				DefaultOWLIndividual ancestorIndividual = (DefaultOWLIndividual) obj;
				if (ancestorIndividual.getPropertyValue(heriditaryProperty)
						.toString().length() != 0) {
					int i = 0;
					boolean added = false;
					while (i < heriditartydiseasestext.size() && !added) {
						if (ancestorIndividual.getPropertyValue(
								heriditaryProperty).toString().compareTo(
								(String) heriditartydiseasestext.get(i)) == 0)
							added = true;
						i++;
					}
					if (!added)
						heriditartydiseasestext.add(ancestorIndividual
								.getPropertyValue(heriditaryProperty)
								.toString());

				}
			}

		}

		for (int i = 0; i < heriditartydiseasestext.size(); i++) {
			heriditary = heriditary + "<heriditary>";
			heriditary = heriditary + heriditartydiseasestext.get(i);
			heriditary = heriditary + "</heriditary>";
		}

		String res = "<heriditarydiseases>" + heriditary
				+ "</heriditarydiseases>";
		StringBuffer response = new StringBuffer();
		response
				.append("<myapp:FamilyHistoryResponse xmlns:myapp=\"http://cms.brookes.ac.uk/staff/DADuce\">");
		response.append(res);

		response.append("</myapp:FamilyHistoryResponse>");

		writeSOAPResult(response.toString(), pW);

	}

	/**
	 * Gets all the relatives of the profile holder in breadth and depth.
	 * 
	 * @return Relatives All family members of the profile holder in breadth and
	 *         depth.
	 */
	private ArrayList getRelatives() {

		ArrayList ancestors = new ArrayList();
		String profileHolderLabel = (String) session.get("ProfileHolderID");
		OWLDatatypeProperty firstNameProperty = owlModel
				.getOWLDatatypeProperty("FirstName");
		OWLObjectProperty ancestorProperty = owlModel
				.getOWLObjectProperty("ancestorOf");

		Object[] objectArray = set.toArray();
		int len = objectArray.length;

		for (int i = 0; i < len; i++) {
			OWLObjectPropertyAssertionAxiomImpl ind = (OWLObjectPropertyAssertionAxiomImpl) objectArray[i];

			String ID = ind.getObject().getURI().toString().substring(
					ind.getObject().getURI().toString().indexOf("#") + 1);
			// ID = ID.substring(0, ID.length() - 3);

			if (ID.compareTo(profileHolderLabel) == 0
					&& ind.getProperty().getURI().compareTo(
							ancestorProperty.getURI()) == 0) {
				String objID = ind.getSubject().getURI().toString().substring(
						ind.getSubject().getURI().toString().indexOf("#") + 1);
				OWLIndividual indiv = owlModel.getOWLIndividual(objID);
				ancestors.add(indiv);
				String name = (String) indiv
						.getPropertyValue(firstNameProperty);

				System.out.println(name);
			}

		}

		// bridge.getInferredIndividuals();
		return ancestors;
	}

	/**
	 * Gets all the ancestors of the profile holder.
	 * 
	 * @return AncestorsList All ancestors members of the profile holder.
	 */
	private ArrayList getAncestors() {

		ArrayList ancestors = new ArrayList();
		String profileHolderLabel = (String) session.get("ProfileHolderID");
		OWLDatatypeProperty firstNameProperty = owlModel
				.getOWLDatatypeProperty("FirstName");
		OWLObjectProperty ancestorProperty = owlModel
				.getOWLObjectProperty("ancestorOf");

		Object[] objectArray = set.toArray();
		int len = objectArray.length;

		for (int i = 0; i < len; i++) {
			OWLObjectPropertyAssertionAxiomImpl ind = (OWLObjectPropertyAssertionAxiomImpl) objectArray[i];

			String ID = ind.getObject().getURI().toString().substring(
					ind.getObject().getURI().toString().indexOf("#") + 1);
			// ID = ID.substring(0, ID.length() - 3);

			if (ID.compareTo(profileHolderLabel) == 0
					&& ind.getProperty().getURI().compareTo(
							ancestorProperty.getURI()) == 0) {
				String objID = ind.getSubject().getURI().toString().substring(
						ind.getSubject().getURI().toString().indexOf("#") + 1);
				OWLIndividual indiv = owlModel.getOWLIndividual(objID);
				ancestors.add(indiv);
				String name = (String) indiv
						.getPropertyValue(firstNameProperty);

				System.out.println(name);
			}

		}

		// bridge.getInferredIndividuals();
		return ancestors;
	}

	/**
	 * Runs the SWRL Rule Engine
	 * 
	 * @throws SWRLRuleEngineException
	 *             if error is detected
	 * @throws SWRLRuleEngineBridgeException
	 *             if error is detected
	 */
	public static HashSet runSWRLRuleEngine() throws SWRLRuleEngineException,
			SWRLRuleEngineBridgeException {
		// The SWRL Rules are embedded in the Owl Model are loaded into the
		// SWRLRuleEngine
		SWRLRuleEngine queryEngine = SWRLRuleEngineFactory.create(owlModel);
		// SWRLRuleEngineBridge provides a bridge between an OWL model with SWRL
		// rules and a rule engine
		SWRLRuleEngineBridge bridge = BridgeFactory.createBridge(owlModel);
		// It infers all the individuals and links based on the rules.
		// There are 56 SWRL Rules defined in this Family History Model
		bridge.infer();
		HashSet set = (HashSet) bridge.getInferredAxioms();
		return set;
	}

	/**
	 * Gets all familymembers and creates the JSON file that is used by
	 * SIMILE-EXHIBIT to create maps, timeline and tabular output for
	 * visulization
	 * 
	 * @param httpData
	 *            hashtable of SOAP request message
	 * @param pW
	 *            stream to which output is to be written
	 * @throws Exception
	 *             if error is detected
	 */

	public void doGetJsonForExhibit(Hashtable httpData, PrintWriter pW)
			throws Exception {

		String queryResult = null;
		String docString = (String) httpData.get("HTTP_BODY");
		if (null == docString || docString.length() == 0)
			throw new Exception("no body to HTTP request");

		Document doc = readDocument(docString);
		NodeList nodeList = doc.getElementsByTagName("map");

		if (nodeList.getLength() != 0) {

			NamedNodeMap nodeMap = nodeList.item(0).getAttributes();
			String user = nodeMap.getNamedItem("user").getNodeValue();
		}

		String profileHolderLabel = (String) session.get("ProfileHolderID");

		OWLIndividual profileHolder = owlModel
				.getOWLIndividual(profileHolderLabel);
		Collection familyMembers = getFamily((DefaultOWLIndividual) profileHolder);
		createFamilyMap(familyMembers);

		String res = "<map>" + "</map>";
		StringBuffer response = new StringBuffer();
		response
				.append("<myapp:FamilyHistoryResponse xmlns:myapp=\"http://cms.brookes.ac.uk/staff/DADuce\">");
		response.append(res);

		response.append("</myapp:FamilyHistoryResponse>");

		writeSOAPResult(response.toString(), pW);

	}

	/**
	 * Gets family members of a profile holder
	 * 
	 * @param instance
	 *            OWL Individual instance for whom the family members needs to
	 *            be formulated
	 * 
	 * @return familyMembersCollection Collection of Family Members of a Profile
	 *         Holder
	 */
	static Collection getFamily(DefaultOWLIndividual instance) {
		Collection collection = instance.getReachableSimpleInstances();
		return collection;
	}

	/**
	 * Creates the JSON file that is used by SIMILE-EXHIBIT to create maps,
	 * timeline and tabular output for visualization
	 * 
	 * @param familyMemberCollection
	 *            Collection of Family Members
	 * @throws ParseException
	 *             if error is detected
	 */
	void createFamilyMap(Collection collection) throws ParseException {
		Iterator itr = collection.iterator();
		String type = "FamilyMember";
		String firstName = "", middleName = "", lastName = "", dob = "", spanDate = "", gender = "", age = "", living = "", imageURL = "", latitude = "", longitude = "";
		String givenDateOfBirth = "", givenDateOfDeath = "";

		String jsonFileContents = "{items:[" + "\n";
		int count = 0;
		OWLDatatypeProperty firstNameProperty = owlModel
				.getOWLDatatypeProperty("FirstName");

		OWLDatatypeProperty middleNameProperty = owlModel
				.getOWLDatatypeProperty("MiddleName");

		OWLDatatypeProperty lastNameProperty = owlModel
				.getOWLDatatypeProperty("LastName");

		OWLDatatypeProperty dateOfBirthProperty = owlModel
				.getOWLDatatypeProperty("DOB");

		OWLDatatypeProperty dateOfDeathProperty = owlModel
				.getOWLDatatypeProperty("DOD");

		OWLDatatypeProperty ageProperty = owlModel
				.getOWLDatatypeProperty("Age");

		OWLDatatypeProperty livingProperty = owlModel
				.getOWLDatatypeProperty("Living");

		OWLDatatypeProperty imageURLProperty = owlModel
				.getOWLDatatypeProperty("ImageURL");

		OWLDatatypeProperty genderProperty = owlModel
				.getOWLDatatypeProperty("Gender");

		OWLDatatypeProperty latitudeProperty = owlModel
				.getOWLDatatypeProperty("Latitude");

		OWLDatatypeProperty longitudeProperty = owlModel
				.getOWLDatatypeProperty("Longitude");
		while (itr.hasNext()) {
			count++;
			System.out.println(count);
			Object object = itr.next();

			if (object instanceof DefaultOWLIndividual) {

				DefaultOWLIndividual familyMember = (DefaultOWLIndividual) object;
				OWLModel owlModel = familyMember.getOWLModel();

				String label = familyMember.getLocalName();
				if (familyMember.getPropertyValue(firstNameProperty) != null)
					firstName = familyMember
							.getPropertyValue(firstNameProperty).toString();

				if (familyMember.getPropertyValue(middleNameProperty) != null)
					middleName = familyMember.getPropertyValue(
							middleNameProperty).toString();

				if (familyMember.getPropertyValue(lastNameProperty) != null)
					lastName = familyMember.getPropertyValue(lastNameProperty)
							.toString();

				if (familyMember.getPropertyValue(dateOfBirthProperty) != null) {

					RDFSLiteral dateLiteral = (RDFSLiteral) familyMember
							.getPropertyValue(dateOfBirthProperty);
					dob = dateLiteral.toString();
					dateLiteral.getPlainValue();
					DateFormat inputDateFormat = new SimpleDateFormat(
							"yyyy-MM-dd");
					inputDateFormat.parse(dob);
					Calendar cal = inputDateFormat.getCalendar();
					String DD = Integer
							.toString(cal.get(Calendar.DAY_OF_MONTH));
					String MM = Integer.toString(cal.get(Calendar.MONTH) + 1);
					String YY = Integer.toString(cal.get(Calendar.YEAR));
					dob = YY + "-" + MM + "-" + DD;
				}
				if (familyMember.getPropertyValue(ageProperty) != null) {
					age = familyMember.getPropertyValue(ageProperty).toString();

				} else
					age = "";

				if (familyMember.getPropertyValue(genderProperty) != null)
					gender = familyMember.getPropertyValue(genderProperty)
							.toString();
				else
					gender = "";

				Object obj1 = familyMember
						.getPropertyValue(dateOfBirthProperty);
				RDFSLiteral dobLiteral = null;
				if (obj1 != null) {
					dobLiteral = (RDFSLiteral) obj1;
					DateFormat birthDateFormat = new SimpleDateFormat(
							"yyyy-MM-dd");
					birthDateFormat.parse(dobLiteral.toString());
					Calendar cal1 = birthDateFormat.getCalendar();
					String DD = Integer.toString(cal1
							.get(Calendar.DAY_OF_MONTH));
					String MM = Integer.toString(cal1.get(Calendar.MONTH) + 1);
					String YY = Integer.toString(cal1.get(Calendar.YEAR));
					dob = YY + "-" + MM + "-" + DD;
					Date dobDate = birthDateFormat.parse(dob);
					dob = birthDateFormat.format(dobDate);
					givenDateOfBirth = dob;
				}

				Object obj2 = familyMember
						.getPropertyValue(dateOfDeathProperty);
				RDFSLiteral dodLiteral = null;
				if (obj2 != null) {
					dobLiteral = (RDFSLiteral) obj2;
					DateFormat deathDateFormat = new SimpleDateFormat(
							"yyyy-MM-dd");
					deathDateFormat.parse(dobLiteral.toString());
					Calendar cal1 = deathDateFormat.getCalendar();
					String DD = Integer.toString(cal1
							.get(Calendar.DAY_OF_MONTH));
					String MM = Integer.toString(cal1.get(Calendar.MONTH) + 1);
					String YY = Integer.toString(cal1.get(Calendar.YEAR));
					spanDate = YY + "-" + MM + "-" + DD;
					Date spannDate = deathDateFormat.parse(spanDate);
					spanDate = deathDateFormat.format(spannDate);
					givenDateOfDeath = spanDate;
				}

				if (familyMember.getPropertyValue(livingProperty) != null) {
					living = familyMember.getPropertyValue(livingProperty)
							.toString();
					if (living.compareTo("true") == 0)
						living = "Yes";
					else
						living = "No";
				}

				if (living.compareTo("Yes") == 0) {
					if (obj1 != null) {
						if (dobLiteral != null) {
							String birthDate = dobLiteral.toString();
							DateFormat birthDateFormat = new SimpleDateFormat(
									"yyyy-MM-dd");
							birthDateFormat.parse(birthDate);
							DateFormat spanDateFormat = new SimpleDateFormat(
									"yyyy-MM-dd");
							spanDate = spanDateFormat.format(new Date());

							// Calendar cal = spanDateFormat.getCalendar();
							//							
							// String DD = Integer.toString(cal
							// .get(Calendar.DAY_OF_MONTH));
							// String MM = Integer.toString(cal
							// .get(Calendar.MONTH) + 1);
							// String YY = Integer
							// .toString(cal.get(Calendar.YEAR));
							// spanDate = YY + "-" + MM + "-" + DD;

							// Calendar agecal = spanDateFormat.getCalendar();
							// long millisecs = cal.getTimeInMillis()
							// - birthDateFormat.getCalendar()
							// .getTimeInMillis();
							// agecal.setTimeInMillis(millisecs);
							//
							// age =
							// Integer.toString(agecal.get(Calendar.YEAR));

							age = Integer.toString(spanDateFormat.getCalendar()
									.get(Calendar.YEAR)
									- birthDateFormat.getCalendar().get(
											Calendar.YEAR));
						}
					} else if (obj1 == null && age.length() != 0) {

						DateFormat birthDateFormat = new SimpleDateFormat(
								"yyyy-MM-dd");
						birthDateFormat.format(new Date());
						Calendar cal1 = birthDateFormat.getCalendar();
						int currentYear = cal1.get(Calendar.YEAR);
						int currentage = Integer.parseInt(age);
						int birthYear = currentYear - currentage;
						cal1.set(Calendar.YEAR, birthYear);
						String DD = Integer.toString(cal1
								.get(Calendar.DAY_OF_MONTH));
						String MM = Integer
								.toString(cal1.get(Calendar.MONTH) + 1);
						String YY = Integer.toString(cal1.get(Calendar.YEAR));
						dob = YY + "-" + MM + "-" + DD;
						Date dobDate = birthDateFormat.parse(dob);
						dob = birthDateFormat.format(dobDate);

						DateFormat spanDateFormat = new SimpleDateFormat(
								"yyyy-MM-dd");
						spanDateFormat.format(new Date());
						Calendar cal2 = spanDateFormat.getCalendar();
						DD = Integer.toString(cal2.get(Calendar.DAY_OF_MONTH));
						MM = Integer.toString(cal2.get(Calendar.MONTH) + 1);
						YY = Integer.toString(cal2.get(Calendar.YEAR));
						spanDate = YY + "-" + MM + "-" + DD;
						Date spannDate = spanDateFormat.parse(spanDate);
						spanDate = spanDateFormat.format(spannDate);

						age = Integer.toString(cal2.get(Calendar.YEAR)
								- cal1.get(Calendar.YEAR));

					} else if (age.length() != 0 && obj1 == null
							&& obj2 == null) {
						DateFormat spanDateFormat = new SimpleDateFormat(
								"yyyy-MM-dd");
						spanDateFormat.format(new Date());
						Calendar cal = spanDateFormat.getCalendar();
						String DD = Integer.toString(cal
								.get(Calendar.DAY_OF_MONTH));
						String MM = Integer
								.toString(cal.get(Calendar.MONTH) + 1);
						String YY = Integer.toString(cal.get(Calendar.YEAR)
								- Integer.parseInt(age));
						spanDate = YY + "-" + MM + "-" + DD;
						Date spannDate = spanDateFormat.parse(spanDate);
						spanDate = spanDateFormat.format(spannDate);

						DateFormat birthDateFormat = new SimpleDateFormat(
								"yyyy-MM-dd");
						birthDateFormat.format(new Date());
						Calendar cal2 = birthDateFormat.getCalendar();
						DD = Integer.toString(cal.get(Calendar.DAY_OF_MONTH));
						MM = Integer.toString(cal.get(Calendar.MONTH) + 1);
						YY = Integer.toString(cal2.get(Calendar.YEAR)
								- Integer.parseInt(age));
						dob = YY + "-" + MM + "-" + DD;
						Date dobDate = birthDateFormat.parse(dob);
						dob = birthDateFormat.format(dobDate);

					} else if (age.length() != 0 && obj1 == null
							&& obj2 == null) {

						DateFormat spanDateFormat = new SimpleDateFormat(
								"yyyy-MM-dd");
						spanDateFormat.format(new Date());
						Calendar cal = spanDateFormat.getCalendar();
						String DD = Integer.toString(cal
								.get(Calendar.DAY_OF_MONTH));
						String MM = Integer
								.toString(cal.get(Calendar.MONTH) + 1);
						String YY = Integer.toString(cal.get(Calendar.YEAR));
						spanDate = YY + "-" + MM + "-" + DD;
						Date spannDate = spanDateFormat.parse(spanDate);
						spanDate = spanDateFormat.format(spannDate);

						DateFormat birthDateFormat = new SimpleDateFormat(
								"yyyy-MM-dd");
						birthDateFormat.format(new Date());
						Calendar cal2 = birthDateFormat.getCalendar();
						DD = Integer.toString(cal.get(Calendar.DAY_OF_MONTH));
						MM = Integer.toString(cal.get(Calendar.MONTH) + 1);
						YY = Integer.toString(cal2.get(Calendar.YEAR)
								- Integer.parseInt(age));
						dob = YY + "-" + MM + "-" + DD;
						Date dobDate = birthDateFormat.parse(dob);
						dob = birthDateFormat.format(dobDate);

					} else if (age.length() != 0 && obj1 != null) {
						DateFormat spanDateFormat = new SimpleDateFormat(
								"yyyy-MM-dd");
						spanDateFormat.format(new Date());
						Calendar cal = spanDateFormat.getCalendar();
						String DD = Integer.toString(cal
								.get(Calendar.DAY_OF_MONTH));
						String MM = Integer
								.toString(cal.get(Calendar.MONTH) + 1);
						String YY = Integer.toString(cal.get(Calendar.YEAR));
						spanDate = YY + "-" + MM + "-" + DD;
						Date spannDate = spanDateFormat.parse(spanDate);
						spanDate = spanDateFormat.format(spannDate);
					}
				}

				if (living.compareTo("No") == 0) {
					if (age.length() != 0 && obj1 != null && obj2 == null) {
						DateFormat birthDateFormat = new SimpleDateFormat(
								"yyyy-MM-dd");
						birthDateFormat.parse(dob);
						Calendar cal = birthDateFormat.getCalendar();

						DateFormat deathDateFormat = new SimpleDateFormat(
								"yyyy-MM-dd");
						deathDateFormat.format(new Date());
						Calendar cal2 = deathDateFormat.getCalendar();
						String DD = Integer.toString(cal
								.get(Calendar.DAY_OF_MONTH));
						String MM = Integer
								.toString(cal.get(Calendar.MONTH) + 1);
						String YY = Integer.toString(cal.get(Calendar.YEAR)
								+ Integer.parseInt(age));
						spanDate = YY + "-" + MM + "-" + DD;
						Date spannDate = deathDateFormat.parse(spanDate);
						spanDate = deathDateFormat.format(spannDate);

					} else if (age.length() != 0 && obj1 == null
							&& obj2 != null) {
						DateFormat deathDateFormat = new SimpleDateFormat(
								"yyyy-MM-dd");
						deathDateFormat.parse(spanDate);
						Calendar cal = deathDateFormat.getCalendar();

						DateFormat birthDateFormat = new SimpleDateFormat(
								"yyyy-MM-dd");
						birthDateFormat.format(new Date());
						Calendar cal2 = birthDateFormat.getCalendar();
						String DD = Integer.toString(cal
								.get(Calendar.DAY_OF_MONTH));
						String MM = Integer
								.toString(cal.get(Calendar.MONTH) + 1);
						String YY = Integer.toString(cal.get(Calendar.YEAR)
								- Integer.parseInt(age));
						dob = YY + "-" + MM + "-" + DD;
						Date dobDate = birthDateFormat.parse(dob);
						dob = birthDateFormat.format(dobDate);

					}

				}
				if (familyMember.getPropertyValue(imageURLProperty) != null)
					imageURL = familyMember.getPropertyValue(imageURLProperty)
							.toString();

				if (familyMember.getPropertyValue(latitudeProperty) != null)
					latitude = familyMember.getPropertyValue(latitudeProperty)
							.toString();

				if (familyMember.getPropertyValue(longitudeProperty) != null)
					longitude = familyMember
							.getPropertyValue(longitudeProperty).toString();
				// Creating a JSON FILE ROW FORMAT
				jsonFileContents = jsonFileContents + "{label:'" + firstName
						+ "'," + "type:'" + type + "'," + "FirstName:'"
						+ firstName + "'," + "MiddleName:'" + middleName + "',"
						+ "LastName:'" + lastName + "'," + "Gender:'" + gender
						+ "'," + "GivenDOB:'" + givenDateOfBirth + "',"
						+ "GivenDOD:'" + givenDateOfDeath + "'," + "DOB:'"
						+ dob + "'," + "SpanDate:'" + spanDate + "'," + "Age:'"
						+ age + "'," + "Living:'" + living + "',"
						+ "imageURL:'" + imageURL + "'," + "birthLatLng:'"
						+ latitude + "," + longitude + "'}," + "\n";
				System.out.println("FirstName:" + firstName + " ," + "DOB:"
						+ dob + " ," + "SpanDate:" + spanDate + " ," + "Age:"
						+ age + "\n");
				age = "";
				givenDateOfBirth = "";
				givenDateOfDeath = "";
				firstName = "";
				type = "FamilyMember";
				middleName = "";
				lastName = "";
				dob = "";
				spanDate = "";
				living = "";
				imageURL = "";
				latitude = "";
				latitude = "";

			}

		}
		jsonFileContents = jsonFileContents.substring(0, jsonFileContents
				.length() - 1)
				+ "\n" + "]}";

		Writer output = null;
		try {
			output = new BufferedWriter(new FileWriter("java_script/family.js"));
			output.write(jsonFileContents);

		} catch (IOException e) {
			e.printStackTrace();
		}

		finally {
			try {
				output.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * Validates an XML file with the schema provided.
	 * 
	 * @param Document
	 *            XML document to be validated against the schema
	 * @param schemaFileName
	 *            Schema File Name
	 * @throws SAXException
	 *             if error is detected
	 * @throws IOException
	 *             if error is detected
	 */
	private void validateDocument(Document doc, String schemaFileName)
			throws ParserConfigurationException, SAXException, IOException {

		// create a SchemaFactory capable of understanding WXS schemas
		SchemaFactory factory = SchemaFactory
				.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

		// load a WXS schema, represented by a Schema instance
		Source schemaFile = new StreamSource(new File(schemaFileName));
		Schema schema = factory.newSchema(schemaFile);
		// create a Validator instance, which can be used to validate an
		// instance document
		Validator validator = schema.newValidator();

		// validate the DOM tree
		try {
			validator.validate(new DOMSource(doc));
		} catch (SAXException e) {
			System.out
					.println("Invalid XML format/ does not match with the schema "
							+ e.getMessage());
		}

	}

	/**
	 * Utility function which writes an XML document to an output stream as body
	 * of a SOAP response message. If you want to use it, make sure it conforms
	 * to proper SOAP conventions.
	 * 
	 * @param data
	 *            message body
	 * @param pW
	 *            stream writer to send document to
	 */
	protected void writeXMLPage(String data, PrintWriter pW) {
		StringBuffer sB = new StringBuffer();
		sB
				.append("<?xml version='1.0' encoding='UTF-8'?>\n")
				.append("<SOAP-ENV:Envelope \n")
				// REMOVAL Aug 9
				// .append(
				// "  SOAP-ENV:encodingStyle='http://xml.apache.org/xml-soap/literalxml'\n"
				// )
				.append(
						"  xmlns:SOAP-ENV='http://schemas.xmlsoap.org/soap/envelope/'\n")
				.append("  xmlns:xsd='http://www.w3.org/2001/XMLSchema'\n")
				.append(
						"  xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>\n")
				.append(" <SOAP-ENV:Body>\n").append(data).append(
						" </SOAP-ENV:Body>\n").append("</SOAP-ENV:Envelope>\n");
		String msg = sB.toString();
		pW.print("HTTP/1.0 200 OK\r\n");
		pW.print("Content-Type: text/xml; charset=utf-8\r\n");
		pW.print("Content-Length: " + msg.length() + "\r\n\r\n");
		pW.print(msg);
		pW.flush();
	}

	/**
	 * Utility function which writes an XML document to an output stream as body
	 * of a SOAP response message. If you want to use it, make sure it conforms
	 * to proper SOAP conventions.
	 * 
	 * @param data
	 *            message body
	 * @param pW
	 *            stream writer to send document to
	 * @param isImage
	 *            If 'true', depicts that the HTML page contains image, 'false'
	 *            otherwise
	 * @param mimeType
	 *            Mime type for the HTML page
	 */
	protected void writeXHTMLPage(String data, PrintWriter pW, boolean isImage,
			String mimeType) {
		StringBuffer sB = new StringBuffer();

		sB.append(data);
		String msg = sB.toString();
		pW.print("HTTP/1.0 200 OK\r\n");

		// pW.print("Content-Type: text/html; charset=utf-8\r\n");
		pW.print("Content-Type: " + mimeType + ";");
		// if (isImage == false)
		pW.print(" charset=utf-8\r\n");

		pW.print("Content-Length: " + msg.length() + "\r\n\r\n");
		pW.print(msg);
		pW.flush();
	}

	/**
	 * Utility function which applies an XSLT transformation to an XML source
	 * document, then outputs resulting XML stream as body of a SOAP response
	 * message. If you are studying P00772 as well as P00773, you might find it
	 * interesting to see how this is done. If you want to use it, make sure it
	 * conforms to proper SOAP conventions.
	 * 
	 * @param data
	 *            message body
	 * @param pW
	 *            stream writer to send document to
	 * @param xslt
	 *            XSLT File Name String
	 */
	protected void writeXHTMLPage(String data, PrintWriter pW, String xslt) {
		try {
			Document doc = readDocument(data);
			xslt = "xslt/" + xslt; // should check for http: prefix or ".."
			// hacks
			if (!new File(xslt).exists())
				throw new Exception("no file " + xslt);

			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer(new StreamSource(
					xslt));
			StringWriter outStrW = new StringWriter();
			transformer
					.transform(new DOMSource(doc), new StreamResult(outStrW));
			String msg = outStrW.toString();

			pW.print("HTTP/1.0 200 OK\r\n");
			pW.print("Content-Type: text/html; charset=utf-8\r\n");
			pW.print("Content-Length: " + msg.length() + "\r\n\r\n");
			pW.print(msg);
			pW.flush();
		} catch (Exception ex) {
			sendExceptionFault(ex, pW);
		}
	}

	/**
	 * Constructs SOAP envelope and returns response message to client. Code
	 * doesn't generate SOAP 1.2 compliant messages.
	 * 
	 * @param soapRes
	 *            contains the body of the SOAP message to place in envelope
	 * @param pW
	 *            output stream to which message is to be written
	 * @throws Exception
	 *             if error is detected
	 * 
	 */
	public void writeSOAPResult(String soapRes, PrintWriter pW)
			throws Exception {
		StringBuffer sB = new StringBuffer();
		sB
				.append("<?xml version='1.0' encoding='UTF-8'?>\n")
				.append("<SOAP-ENV:Envelope \n")
				// REMOVAL AUg 9
				// .append(
				// "  SOAP-ENV:encodingStyle='http://xml.apache.org/xml-soap/literalxml'\n"
				// )
				.append(
						"  xmlns:SOAP-ENV='http://schemas.xmlsoap.org/soap/envelope/'\n")
				.append("  xmlns:xsd='http://www.w3.org/2001/XMLSchema'\n")
				.append(
						"  xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>\n")
				.append(" <SOAP-ENV:Body>\n").append(soapRes).append(
						" </SOAP-ENV:Body>\n").append("</SOAP-ENV:Envelope>\n");
		soapRes = sB.toString();
		System.out.println("SOAPRES=\n" + soapRes + "END SOAPRES");
		pW.print("HTTP/1.0 200 OK\r\n"); // ADDED / Aug 9
		pW.print("Content-Type: text/xml; charset=utf-8\r\n");
		pW.print("Content-Length: " + soapRes.length() + "\r\n");
		pW.print("Date: " + getRfc1123DateFormat().format(new java.util.Date())
				+ "\r\n");
		// pW.print("Date: Sun, 10 Feb 2002 22:19:37 GMT\r\n");
		// will need to change to give correct Server header for your
		// application
		pW.print("Server: FamilyHistoryService 0.11\r\n\r\n"); // blank line
		pW.print(soapRes);
		pW.flush();
		pW.flush();
	}

	// END OF HTTP FUNCTIONALITY SECTION

	/**
	 * This function registers the family member's relationship with the other
	 * family member or the profile holder himself.
	 * 
	 * @param httpData
	 *            hashtable of SOAP request message
	 * @param pW
	 *            stream to which output is to be written
	 * @throws Exception
	 *             if error is detected
	 */
	public void doRegisterFamilyRelationships(Hashtable httpData, PrintWriter pW)
			throws Exception {

		String queryResult = null;
		String docString = (String) httpData.get("HTTP_BODY");
		if (null == docString || docString.length() == 0)
			throw new Exception("no body to HTTP request");

		Document doc = readDocument(docString);

		NodeList nodeList = doc.getElementsByTagName("relationship");

		if (nodeList.getLength() != 0) {

			NamedNodeMap nodeMap = nodeList.item(0).getAttributes();

			String inputRelationship = nodeMap.getNamedItem("relationship")
					.getNodeValue();

			// This function registers the relationship between relative1 and
			// relative2.

			String inputRelative1 = nodeMap.getNamedItem("relative1")
					.getNodeValue();
			String inputRelative2 = nodeMap.getNamedItem("relative2")
					.getNodeValue();

			OWLIndividual existing1 = owlModel.getOWLIndividual(inputRelative1);
			OWLIndividual existing2 = owlModel.getOWLIndividual(inputRelative2);

			OWLObjectProperty relationProperty;
			relationProperty = owlModel.getOWLObjectProperty(inputRelationship);
			existing1.setPropertyValue(relationProperty, existing2);

			// Saves the newly added relationship status into the OWL Model
			saveOwlModel();
			queryResult = "Family member relationship registration successful!";

		}

		String res = "<relationship>" + queryResult + "</relationship>";
		StringBuffer response = new StringBuffer();
		response
				.append("<myapp:FamilyHistoryResponse xmlns:myapp=\"http://cms.brookes.ac.uk/staff/DADuce\">");
		response.append(res);

		response.append("</myapp:FamilyHistoryResponse>");

		writeSOAPResult(response.toString(), pW);

	}

	/**
	 * This function queries and loads the list of names of the family members
	 * based on the relationships requested.
	 * 
	 * @param httpData
	 *            hashtable of SOAP request message
	 * @param pW
	 *            stream to which output is to be written
	 * @throws Exception
	 *             if error is detected
	 */
	public void doViewFamilyRelationships(Hashtable httpData, PrintWriter pW)
			throws Exception {
		String queryResult = null;
		String docString = (String) httpData.get("HTTP_BODY");
		Object[] output = null;
		if (null == docString || docString.length() == 0)
			throw new Exception("no body to HTTP request");

		Document doc = readDocument(docString);

		NodeList nodeList = doc.getElementsByTagName("relationship");

		if (nodeList.getLength() != 0) {

			NamedNodeMap nodeMap = nodeList.item(0).getAttributes();

			String inputRelationship = nodeMap.getNamedItem("relationship")
					.getNodeValue();

			String inputRelative = nodeMap.getNamedItem("relative")
					.getNodeValue();

			OWLIndividual relative = owlModel.getOWLIndividual(inputRelative);

			OWLObjectProperty relationProperty;
			relationProperty = owlModel.getOWLObjectProperty(inputRelationship);

			HashMap relationOf = new HashMap();
			relationOf.put("fatherOf", "Male");
			relationOf.put("motherOf", "Female");
			relationOf.put("fatherinlawOf", "Male");
			relationOf.put("motherinlawOf", "Female");
			relationOf.put("soninlawOf", "Male");
			relationOf.put("daughterinlawOf", "Female");
			relationOf.put("sonOf", "Male");
			relationOf.put("daughterOf", "Female");
			relationOf.put("grandfatherOf", "Male");
			relationOf.put("grandmotherOf", "Female");
			relationOf.put("grandsonOf", "Male");
			relationOf.put("granddaughterOf", "Female");
			relationOf.put("uncleOf", "Male");
			relationOf.put("auntOf", "Female");
			relationOf.put("brotherOf", "Male");
			relationOf.put("sisterOf", "Female");
			relationOf.put("husbandOf", "Male");
			relationOf.put("wifeOf", "Female");
			relationOf.put("nephewOf", "Male");
			relationOf.put("nieceOf", "Female");

			if (relationOf.containsKey(inputRelationship)) {
				String gender = (String) relationOf.get(inputRelationship);
				OWLObjectProperty parentRelationProperty = (OWLObjectProperty) relationProperty
						.getFirstSuperproperty();
				Collection parentObjects = relative
						.getPropertyValues(parentRelationProperty
								.getInverseProperty());
				ArrayList outputArryList = new ArrayList();
				Iterator iterator = parentObjects.iterator();
				int found = 0;
				while (iterator.hasNext()) {
					OWLIndividual parentIndividual = (OWLIndividual) iterator
							.next();
					System.out.println(parentIndividual.getName());
					OWLDatatypeProperty genderProperty = owlModel
							.getOWLDatatypeProperty("Gender");

					if (parentIndividual.getPropertyValue(genderProperty)
							.toString().compareTo(gender) == 0) {
						if (relative != parentIndividual)
							outputArryList.add(parentIndividual);

					}

				}
				output = outputArryList.toArray();
			} else if (inputRelationship.compareTo("bloodlineOf") == 0) {
				OWLObjectProperty ancestralProperty = owlModel
						.getOWLObjectProperty("ancestorOf");
				Collection bloodlineancestralCollection = relative
						.getPropertyValues(ancestralProperty
								.getInverseProperty());

				OWLObjectProperty descentantProperty = owlModel
						.getOWLObjectProperty("descentantOf");
				Collection bloodlinedescentantCollection = relative
						.getPropertyValues(descentantProperty
								.getInverseProperty());

				ArrayList bloodlineCollection = new ArrayList();
				if (bloodlineancestralCollection.size() != 0) {
					bloodlineCollection.addAll(bloodlineancestralCollection);

					if (bloodlinedescentantCollection.size() != 0)
						bloodlineCollection
								.addAll(bloodlinedescentantCollection);
				} else
					bloodlineCollection.addAll(bloodlinedescentantCollection);

				output = bloodlineCollection.toArray();
			} else {
				Collection outputCollection = relative
						.getPropertyValues(relationProperty
								.getInverseProperty());

				if (outputCollection.contains(relative)) {
					Object temp[] = outputCollection.toArray();
					for (int i = 0; i < temp.length; i++) {
						if (temp[i] == relative)
							temp[i] = null;
					}
					ArrayList list = new ArrayList();
					for (int i = 0; i < temp.length; i++) {
						if (temp[i] != null)
							list.add(temp[i]);
					}
					output = list.toArray();// outputCollection.remove(relative.);
				} else
					output = outputCollection.toArray();
			}

			// ArrayList ancestors = getAncestors();
		}

		String relatives = "";
		for (int i = 0; i < output.length; i++) {

			OWLIndividual instance = ((OWLIndividual) output[i]);
			String fullName;

			OWLDatatypeProperty firstNameProperty = owlModel
					.getOWLDatatypeProperty("FirstName");
			fullName = (String) instance.getPropertyValue(firstNameProperty);

			OWLDatatypeProperty middleNameProperty = owlModel
					.getOWLDatatypeProperty("MiddleName");
			fullName = fullName + " "
					+ (String) instance.getPropertyValue(middleNameProperty);

			OWLDatatypeProperty lastNameProperty = owlModel
					.getOWLDatatypeProperty("LastName");
			fullName = fullName + " "
					+ (String) instance.getPropertyValue(lastNameProperty);

			relatives = relatives + "<relation>";
			relatives = relatives + fullName;
			relatives = relatives + "</relation>";
		}

		String res = "<relationship>" + relatives + "</relationship>";
		StringBuffer response = new StringBuffer();
		response
				.append("<myapp:FamilyHistoryResponse xmlns:myapp=\"http://cms.brookes.ac.uk/staff/DADuce\">");
		response.append(res);

		response.append("</myapp:FamilyHistoryResponse>");

		writeSOAPResult(response.toString(), pW);
	}

	// MISCELLANRY FUNCTIONALITY SECTION
	/**
	 * Used in security/authentication
	 */
	public static byte[] md5Digest(String x) throws Exception {
		java.security.MessageDigest d = java.security.MessageDigest
				.getInstance("MD5");
		d.reset();
		d.update(x.getBytes("utf-8"));
		return d.digest();
	}

	// END OF MISCELLANY FUNCTIONALITY SECTION

	/**
	 * This function is invoked when the OWL Model is changed. The web service
	 * FamilyHistoryCreateService and the corresponding SOAP ACTION functions
	 * change the model after processing. This function is then invoked to the
	 * reload the changed model.
	 * 
	 * @throws OntologyLoadException
	 *             if error is detected
	 */
	void owlModelChanged() throws OntologyLoadException {
		getowlModel();
	}

	/**
	 * This function runs the SWRL Rule Engine to infer the rules based on the
	 * SWRL rules processed by the SWRL Rule Engine
	 * 
	 * @throws OntologyLoadException
	 *             if error is detected
	 */
	static void inferRules() throws SWRLRuleEngineBridgeException,
			SWRLRuleEngineException {
		set = runSWRLRuleEngine();

	}

	/**
	 * Saves the OWL Model and serialisses into the Web3FamilyTree.owl file
	 * 
	 * @throws Exception
	 *             if error is detected
	 */
	void saveOwlModel() throws Exception {
		owlModel.save(new File("familyHistoryService" + File.separator
				+ "Web3FamilyTree.owl").toURI());
	}

	/**
	 * Main program.
	 * 
	 * @param args
	 *            currently not used
	 */
	public static void main(String[] args) throws Exception {

		FamilyHistoryService osService = new FamilyHistoryService();
		System.out.println("FamilyHistory Service running ...");
		getowlModel();
		inferRules();
		osService.listenOnPort();
	} // end of main()

}
