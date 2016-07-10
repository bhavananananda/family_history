package familyHistoryService;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import javax.xml.crypto.dsig.XMLObject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;

import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSDatatype;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLIndividual;
import edu.stanford.smi.protegex.owl.ProtegeOWL;

public class FamilyHistoryDataGenerator {
	static DocumentBuilderFactory dbf = null;

	public static void main(String[] args) {
		String uri = "file:/C:/eclipse/FamilyHistoryEclipse/FamilyHistoryEclipseWorkspace/FamilyHistory/familyHistoryService/Web3FamilyTree.owl";
		String inputFirstName, inputMiddleName, inputLastName, inputNickName, inputImage, inputGender, dateString, inputAddress, inputHealthDisease, inputHeriditaryDisease, inputBloodGroup;
		String inputFather = null, inputMother = null, inputSpouse = null, inputSibling = null;
		Float inputLatitude, inputLongitude;
		OWLObjectProperty parentProperty = null ;
		int inputAge;

		JenaOWLModel owlModel;
		try {
			owlModel = ProtegeOWL.createJenaOWLModelFromURI(uri);

			String prefixedName = owlModel.getOWLNamedClass("NonProfileHolder")
					.getPrefixedName();

			Document xmldoc = parseURL("familyHistoryService/FamilyHistoryData.xml");
			NodeList firstNameList = xmldoc.getElementsByTagName("FirstName");
			NodeList middleNameList = xmldoc.getElementsByTagName("MiddleName");
			NodeList lastNameList = xmldoc.getElementsByTagName("LastName");
			NodeList dobList = xmldoc.getElementsByTagName("DOB");
			NodeList dodList = xmldoc.getElementsByTagName("DOD");
			NodeList genderList = xmldoc.getElementsByTagName("Gender");
			NodeList add1List = xmldoc.getElementsByTagName("Address1");
			NodeList add2List = xmldoc.getElementsByTagName("Address2");
			NodeList img1List = xmldoc.getElementsByTagName("ImageURL1");
			NodeList img2List = xmldoc.getElementsByTagName("ImageURL2");
			NodeList img3List = xmldoc.getElementsByTagName("ImageURL3");
			NodeList nickNameList = xmldoc.getElementsByTagName("NickName");
			NodeList ageList = xmldoc.getElementsByTagName("Age");
			NodeList livingList = xmldoc.getElementsByTagName("Living");
			NodeList latitudeList = xmldoc.getElementsByTagName("Latitude");
			NodeList longitudeList = xmldoc.getElementsByTagName("Longitude");
			NodeList healthDiseaseList = xmldoc
					.getElementsByTagName("HealthDiseases");
			NodeList heriditaryDiseaseList = xmldoc
					.getElementsByTagName("HeriditaryDiseases");
			NodeList bloodGroupList = xmldoc.getElementsByTagName("BloodGroup");
			NodeList motherList = xmldoc.getElementsByTagName("Mother");
			NodeList fatherList = xmldoc.getElementsByTagName("Father");
			NodeList spouseList = xmldoc.getElementsByTagName("Spouse");
			NodeList siblingList = xmldoc.getElementsByTagName("Sibling");

			if (firstNameList.getLength() != middleNameList.getLength()
					|| middleNameList.getLength() != lastNameList.getLength()
					|| lastNameList.getLength() != dobList.getLength()
					||dobList.getLength() != dodList.getLength()
					|| dobList.getLength() != genderList.getLength()
					|| genderList.getLength() != img1List.getLength()
					|| img1List.getLength() != img2List.getLength()
					|| img2List.getLength() != img3List.getLength()
					|| img3List.getLength() != add1List.getLength()
					|| add1List.getLength() != add2List.getLength()
					|| add2List.getLength() != livingList.getLength()
					|| livingList.getLength() != latitudeList.getLength()
					|| latitudeList.getLength() != longitudeList.getLength())

				throw (new Exception());

			int i = 0;
			while (i < firstNameList.getLength()) {

				int instancesCount = owlModel.getOWLNamedClass(
						"NonProfileHolder").getInstanceCount(true);
				int autoNumber = instancesCount + 1;
				String newInstanceName = prefixedName + "_" + autoNumber;

				OWLNamedClass namedClass = owlModel
						.getOWLNamedClass("NonProfileHolder");
				OWLIndividual newInstance = namedClass
						.createOWLIndividual(newInstanceName);

				Node firstNameNode = firstNameList.item(i);
				inputFirstName = firstNameNode.getTextContent();
				OWLDatatypeProperty firstNameProperty = owlModel
						.getOWLDatatypeProperty("FirstName");
				newInstance.setPropertyValue(firstNameProperty, inputFirstName);

				Node middleNameNode = middleNameList.item(i);
				inputMiddleName = middleNameNode.getTextContent();
				OWLDatatypeProperty middleNameProperty = owlModel
						.getOWLDatatypeProperty("MiddleName");
				newInstance.setPropertyValue(middleNameProperty,
						inputMiddleName);

				Node lastNameNode = lastNameList.item(i);
				inputLastName = lastNameNode.getTextContent();
				OWLDatatypeProperty lastNameProperty = owlModel
						.getOWLDatatypeProperty("LastName");
				newInstance.setPropertyValue(lastNameProperty, inputLastName);

				Node nickNameNode = nickNameList.item(i);
				inputNickName = nickNameNode.getTextContent();
				OWLDatatypeProperty nickNameProperty = owlModel
						.getOWLDatatypeProperty("NickName");
				newInstance.setPropertyValue(nickNameProperty, inputNickName);

				Node healthDiseaseNode = healthDiseaseList.item(i);
				if (healthDiseaseNode != null) {
					inputHealthDisease = healthDiseaseNode.getTextContent();
					OWLDatatypeProperty healthDiseaseProperty = owlModel
							.getOWLDatatypeProperty("Diseases");
					newInstance.setPropertyValue(healthDiseaseProperty,
							inputHealthDisease);
				}

				Node heriditaryDiseaseNode = heriditaryDiseaseList.item(i);
				if (heriditaryDiseaseNode != null) {
					inputHeriditaryDisease = heriditaryDiseaseNode
							.getTextContent();
					OWLDatatypeProperty heriditaryDiseaseProperty = owlModel
							.getOWLDatatypeProperty("Heriditary");
					newInstance.setPropertyValue(heriditaryDiseaseProperty,
							inputHeriditaryDisease);
				}

				Node bloodGroupNode = bloodGroupList.item(i);
				if (bloodGroupNode != null) {
					inputBloodGroup = bloodGroupNode.getTextContent();
					OWLDatatypeProperty bloodGroupProperty = owlModel
							.getOWLDatatypeProperty("BloodGroup");
					newInstance.setPropertyValue(bloodGroupProperty,
							inputBloodGroup);
				}

				Node img1Node = img1List.item(i);
				Node img2Node = img2List.item(i);
				Node img3Node = img3List.item(i);
				String img1 = img1Node.getTextContent();
				String img2 = img2Node.getTextContent();
				String img3 = img3Node.getTextContent();
				inputImage = img1 + img2 + img3;

				Node add1Node = add1List.item(i);
				Node add2Node = add2List.item(i);
				inputAddress = add1Node.getTextContent() + ","
						+ add2Node.getTextContent();

				OWLDatatypeProperty addressProperty = owlModel
						.getOWLDatatypeProperty("Address");
				newInstance.setPropertyValue(addressProperty, inputAddress);

				OWLDatatypeProperty imageURLProperty = owlModel
						.getOWLDatatypeProperty("ImageURL");
				newInstance.setPropertyValue(imageURLProperty, inputImage);

				Node genderNode = genderList.item(i);
				inputGender = genderNode.getTextContent();
				OWLDatatypeProperty genderProperty = owlModel
						.getOWLDatatypeProperty("Gender");
				if (inputGender.compareTo("M") == 0)
					inputGender = "Male";
				else
					inputGender = "Female";
				newInstance.setPropertyValue(genderProperty, inputGender);

				Node dobNode = dobList.item(i);
				String inputDate = dobNode.getTextContent();
				if (inputDate != null && inputDate != "") {
					OWLDatatypeProperty dateOfBirthProperty = owlModel
							.getOWLDatatypeProperty("DOB");
					Date date = new Date();
					DateFormat inputDateFormat = new SimpleDateFormat(
							"dd/MM/yyyy");
					DateFormat newDateFormat = new SimpleDateFormat(
							"yyyy-MM-dd");
					date = inputDateFormat.parse(inputDate);
					dateString = newDateFormat.format(date);
					RDFSDatatype xsdDate = owlModel
							.getRDFSDatatypeByName("xsd:date");
					RDFSLiteral dateLiteral = owlModel.createRDFSLiteral(
							dateString, xsdDate);
					newInstance.setPropertyValue(dateOfBirthProperty,
							dateLiteral);
				}
				
				
				Node dodNode = dodList.item(i);
				 inputDate = dodNode.getTextContent();
				if (inputDate != null && inputDate != "") {
					OWLDatatypeProperty dateOfDeathProperty = owlModel
							.getOWLDatatypeProperty("DOD");
					Date date = new Date();
					DateFormat inputDateFormat = new SimpleDateFormat(
							"dd/MM/yyyy");
					DateFormat newDateFormat = new SimpleDateFormat(
							"yyyy-MM-dd");
					date = inputDateFormat.parse(inputDate);
					dateString = newDateFormat.format(date);
					RDFSDatatype xsdDate = owlModel
							.getRDFSDatatypeByName("xsd:date");
					RDFSLiteral dateLiteral = owlModel.createRDFSLiteral(
							dateString, xsdDate);
					newInstance.setPropertyValue(dateOfDeathProperty,
							dateLiteral);
				}

				

				Node ageNode = ageList.item(i);
				if (ageNode.getTextContent() != "") {
					Integer ageInteger = Integer.parseInt(ageNode
							.getTextContent());
					inputAge = ageInteger.intValue();
					RDFSDatatype xsdPositiveInteger = owlModel
							.getRDFSDatatypeByName("xsd:int");
					RDFSLiteral positiveIntegerLiteral = owlModel
							.createRDFSLiteral(Integer.toString(inputAge),
									xsdPositiveInteger);
					OWLDatatypeProperty ageProperty = owlModel
							.getOWLDatatypeProperty("Age");
					newInstance.setPropertyValue(ageProperty,
							positiveIntegerLiteral);
				}

				Node livingNode = livingList.item(i);
				if (livingNode.getTextContent() != "") {
					boolean inputLiving;
					if (livingNode.getTextContent().compareTo("Yes") == 0)
						inputLiving = true;
					else
						inputLiving = false;
					OWLDatatypeProperty livingProperty = owlModel
							.getOWLDatatypeProperty("Living");
					newInstance.setPropertyValue(livingProperty, inputLiving);
				}

				Node latitudeNode = latitudeList.item(i);
				inputLatitude = Float.parseFloat(latitudeNode.getTextContent());
				OWLDatatypeProperty latitudeProperty = owlModel
						.getOWLDatatypeProperty("Latitude");
				newInstance.setPropertyValue(latitudeProperty, inputLatitude);

				Node longitudeNode = longitudeList.item(i);
				inputLongitude = Float.parseFloat(longitudeNode
						.getTextContent());
				OWLDatatypeProperty longitudeProperty = owlModel
						.getOWLDatatypeProperty("Longitude");
				newInstance.setPropertyValue(longitudeProperty, inputLongitude);
				i++;
			}

			Iterator itr = owlModel.getOWLNamedClass("NonProfileHolder")
					.getInstances(true).iterator();
			parentProperty = owlModel
			.getOWLObjectProperty("parentOf");
			i = 0;
			while (itr.hasNext()) {
				Object obj = itr.next();
				OWLIndividual individual = null;
				if (obj instanceof OWLIndividual)
					individual = (OWLIndividual) obj;

				Node motherNode = motherList.item(i);
				if (motherNode != null)
					inputMother = motherNode.getTextContent();
				if (inputMother.length() != 0) {
					String queryString = " PREFIX fh: <http://www.owl-ontologies.com/Ontology1275849080.owl#> "
							+ " PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
							+ " select ?subject where {?subject fh:FirstName '"
							+ inputMother + "'}";

					QueryExecution qexec = QueryExecutionFactory.create(
							QueryFactory.create(queryString), owlModel
									.getJenaModel());
					String ID = "";
					try {
						ResultSet results = qexec.execSelect();
						while (results.hasNext()) {
							QuerySolution soln = results.nextSolution();
							ID = soln.toString().substring(
									soln.toString().indexOf("#") + 1);
							ID = ID.substring(0, ID.length() - 3);
						}
					} finally {
						if (qexec != null)
							qexec.close();
					}

					OWLObjectProperty motherProperty = owlModel
							.getOWLObjectProperty("motherOf");
					if (ID.length() == 0) {
						OWLNamedClass namedClass = owlModel
								.getOWLNamedClass("NonProfileHolder");
						int instancesCount = owlModel.getOWLNamedClass(
								"NonProfileHolder").getInstanceCount(true);
						int autoNumber = instancesCount + 1;
						String newInstanceName = prefixedName + "_"
								+ autoNumber;
						OWLIndividual newInstance = namedClass
								.createOWLIndividual(newInstanceName);

						OWLDatatypeProperty firstNameProperty = owlModel
								.getOWLDatatypeProperty("FirstName");
						newInstance.setPropertyValue(firstNameProperty,
								inputMother);
//						newInstance
//								.setPropertyValue(motherProperty, individual);
				
						newInstance.addPropertyValue(parentProperty, individual);
						System.out.println("Created mother " + newInstanceName
								+ " = " + inputMother);
					} else

//						owlModel.getOWLIndividual(ID).setPropertyValue(
//								motherProperty, individual);
						owlModel.getOWLIndividual(ID).addPropertyValue(parentProperty, individual);

					OWLDatatypeProperty firstNameProperty = owlModel
							.getOWLDatatypeProperty("FirstName");
					String indName = (String) individual
							.getPropertyValue(firstNameProperty);
					System.out.println("Added " + inputMother + " motherOf "
							+ indName);
				}

				Node fatherNode = fatherList.item(i);
				if (fatherNode != null)
					inputFather = fatherNode.getTextContent();
				if (inputFather.length() != 0) {
					String queryString = " PREFIX fh: <http://www.owl-ontologies.com/Ontology1275849080.owl#> "
							+ " PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
							+ " select ?subject where {?subject fh:FirstName '"
							+ inputFather + "'}";

					QueryExecution qexec = QueryExecutionFactory.create(
							QueryFactory.create(queryString), owlModel
									.getJenaModel());
					String ID = "";
					try {
						ResultSet results = qexec.execSelect();
						while (results.hasNext()) {
							QuerySolution soln = results.nextSolution();
							ID = soln.toString().substring(
									soln.toString().indexOf("#") + 1);
							ID = ID.substring(0, ID.length() - 3);
						}
					} finally {
						if (qexec != null)
							qexec.close();
					}

					OWLObjectProperty fatherProperty = owlModel
							.getOWLObjectProperty("fatherOf");
					if (ID.length() == 0) {
						OWLNamedClass namedClass = owlModel
								.getOWLNamedClass("NonProfileHolder");
						int instancesCount = owlModel.getOWLNamedClass(
								"NonProfileHolder").getInstanceCount(true);
						int autoNumber = instancesCount + 1;
						String newInstanceName = prefixedName + "_"
								+ autoNumber;
						OWLIndividual newInstance = namedClass
								.createOWLIndividual(newInstanceName);

						OWLDatatypeProperty firstNameProperty = owlModel
								.getOWLDatatypeProperty("FirstName");
						newInstance.setPropertyValue(firstNameProperty,
								inputFather);
//						newInstance
//								.setPropertyValue(fatherProperty, individual);
						newInstance.addPropertyValue(parentProperty, individual);
				
						System.out.println("Created father " + newInstanceName
								+ " = " + inputFather);
					} else;

//						owlModel.getOWLIndividual(ID).setPropertyValue(
//								fatherProperty, individual);
					owlModel.getOWLIndividual(ID).addPropertyValue(parentProperty, individual);

					OWLDatatypeProperty firstNameProperty = owlModel
							.getOWLDatatypeProperty("FirstName");
					String indName = (String) individual
							.getPropertyValue(firstNameProperty);
					System.out.println("Added " + inputFather + " fatherOf "
							+ indName); 

				}

				Node spouseNode = spouseList.item(i);
				if (spouseNode != null)
					inputSpouse = spouseNode.getTextContent();
				if (inputSpouse.length() != 0) {
					String queryString = " PREFIX fh: <http://www.owl-ontologies.com/Ontology1275849080.owl#> "
							+ " PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
							+ " select ?subject where {?subject fh:FirstName '"
							+ inputSpouse + "'}";

					QueryExecution qexec = QueryExecutionFactory.create(
							QueryFactory.create(queryString), owlModel
									.getJenaModel());
					String ID = "";
					try {
						ResultSet results = qexec.execSelect();
						while (results.hasNext()) {
							QuerySolution soln = results.nextSolution();
							ID = soln.toString().substring(
									soln.toString().indexOf("#") + 1);
							ID = ID.substring(0, ID.length() - 3);
						}
					} finally {
						if (qexec != null)
							qexec.close();
					}

					OWLObjectProperty spouseProperty = owlModel
							.getOWLObjectProperty("spouseOf");
					if (ID.length() == 0) {
						OWLNamedClass namedClass = owlModel
								.getOWLNamedClass("NonProfileHolder");
						int instancesCount = owlModel.getOWLNamedClass(
								"NonProfileHolder").getInstanceCount(true);
						int autoNumber = instancesCount + 1;
						String newInstanceName = prefixedName + "_"
								+ autoNumber;
						OWLIndividual newInstance = namedClass
								.createOWLIndividual(newInstanceName);

						OWLDatatypeProperty firstNameProperty = owlModel
								.getOWLDatatypeProperty("FirstName");
						newInstance.setPropertyValue(firstNameProperty,
								inputSpouse);
						newInstance
								.setPropertyValue(spouseProperty, individual);
						System.out.println("Created Spouse " + newInstanceName
								+ " = " + inputSpouse);
					} else
						owlModel.getOWLIndividual(ID).setPropertyValue(
								spouseProperty, individual);
					
					OWLDatatypeProperty firstNameProperty = owlModel
					.getOWLDatatypeProperty("FirstName");
			String indName = (String) individual
					.getPropertyValue(firstNameProperty);
			System.out.println("Added " + inputSpouse + " spouseOf "
					+ indName);

				}

				Node siblingNode = siblingList.item(i);
				if (siblingNode != null)
					inputSibling = siblingNode.getTextContent();
				if (inputSibling.length() != 0) {
					String queryString = " PREFIX fh: <http://www.owl-ontologies.com/Ontology1275849080.owl#> "
							+ " PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
							+ " select ?subject where {?subject fh:FirstName '"
							+ inputSibling + "'}";

					QueryExecution qexec = QueryExecutionFactory.create(
							QueryFactory.create(queryString), owlModel
									.getJenaModel());
					String ID = "";
					try {
						ResultSet results = qexec.execSelect();
						while (results.hasNext()) {
							QuerySolution soln = results.nextSolution();
							ID = soln.toString().substring(
									soln.toString().indexOf("#") + 1);
							ID = ID.substring(0, ID.length() - 3);
						}
					} finally {
						if (qexec != null)
							qexec.close();
					}

					OWLObjectProperty siblingProperty = owlModel
							.getOWLObjectProperty("siblingOf");
					if (ID.length() == 0) {
						OWLNamedClass namedClass = owlModel
								.getOWLNamedClass("NonProfileHolder");
						int instancesCount = owlModel.getOWLNamedClass(
								"NonProfileHolder").getInstanceCount(true);
						int autoNumber = instancesCount + 1;
						String newInstanceName = prefixedName + "_"
								+ autoNumber;
						OWLIndividual newInstance = namedClass
								.createOWLIndividual(newInstanceName);

						OWLDatatypeProperty firstNameProperty = owlModel
								.getOWLDatatypeProperty("FirstName");
						newInstance.setPropertyValue(firstNameProperty,
								inputSibling);
//						newInstance.setPropertyValue(siblingProperty,
//								individual);
						newInstance.addPropertyValue(siblingProperty, individual);
						System.out.println("Created sibling " + newInstanceName
								+ " = " + inputSibling);
					} else
//						owlModel.getOWLIndividual(ID).setPropertyValue(
//								siblingProperty, individual);
						owlModel.getOWLIndividual(ID).addPropertyValue(siblingProperty, individual);
					
					OWLDatatypeProperty firstNameProperty = owlModel
					.getOWLDatatypeProperty("FirstName");
			String indName = (String) individual
					.getPropertyValue(firstNameProperty);
			System.out.println("Added " + inputSibling + " siblingOf "
					+ indName);

				}

				i++;

			}
			 owlModel.save(new File("familyHistoryService" + File.separator
			 + "Web3FamilyTree.owl").toURI());

		} catch (OntologyLoadException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * parseURL gets the Document at a particular URL, or throws exception
	 * 
	 * @param url
	 *            URL to parse
	 * @throws Exception
	 *             if error detected
	 **/
	public static Document parseURL(String url) throws Exception {
		DocumentBuilder db = getDBF().newDocumentBuilder();
		Document doc = db.parse(url);
		return doc;
	}

	/**
	 * Makes a new instance of a document builder factory; sets validation and
	 * namespace aware properties
	 */
	public static DocumentBuilderFactory getDBF() {
		if (dbf != null)
			return dbf;
		dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		dbf.setValidating(false);
		return dbf;
	}

}
