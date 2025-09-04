//package com.vision.wb;
//
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.InputStream;
//
//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
//import javax.xml.parsers.ParserConfigurationException;
//import javax.xml.transform.OutputKeys;
//import javax.xml.transform.Transformer;
//import javax.xml.transform.TransformerException;
//import javax.xml.transform.TransformerFactory;
//import javax.xml.transform.dom.DOMSource;
//import javax.xml.transform.stream.StreamResult;
//
//import org.w3c.dom.Document;
//import org.w3c.dom.Element;
//
//import com.jamesmurty.utils.XMLBuilder;
//
//public class XmlBuilder{
//    public static void main(String[] args) throws ParserConfigurationException, TransformerException {
//        // Create root element
//        XMLBuilder builder = XMLBuilder.create("Employees")
//                .attr("version", "1.0")
//                .attr("company", "TechCorp");
//
//        // Create first employee
//        builder.elem("Employee")
//                .attr("id", "1001")
//                .elem("Name")
//                .text("John Doe")
//                .up() // Move up to Employee element
//                .elem("Role")
//                .text("Software Engineer");
//
//        // Create second employee
//        builder.elem("Employee")
//                .attr("id", "1002")
//                .elem("Name")
//                .text("Jane Smith")
//                .up() // Move up to Employee element
//                .elem("Role")
//                .text("Data Scientist");
//
//        // Output the generated XML
//        String xmlString = builder.asString();
//        System.out.println(beautifyXML(xmlString));
//    }
//    public static String beautifyXML(String xmlString) {
//		try {
//			// Create a DocumentBuilderFactory
//			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//			factory.setNamespaceAware(true);
//
//			// Create a DocumentBuilder
//			DocumentBuilder builder = factory.newDocumentBuilder();
//
//			// Convert the XML string to an InputStream
//			InputStream inputStream = new ByteArrayInputStream(xmlString.getBytes("UTF-8"));
//
//			// Parse the XML InputStream into a Document
//			Document document = builder.parse(inputStream);
//
//			// Check if the root element has the namespace, if not add it
//			Element root = document.getDocumentElement();
//			if (!root.hasAttribute("xmlns:crs")) {
//				root.setAttribute("xmlns:crs", "http://example.com/crs-namespace");
//			}
//
//			// Create a TransformerFactory
//			TransformerFactory transformerFactory = TransformerFactory.newInstance();
//
//			// Create a Transformer for the XML
//			Transformer transformer = transformerFactory.newTransformer();
//
//			// Set output properties for pretty-printing
//			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
//			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
//
//			// Create a DOMSource
//			DOMSource source = new DOMSource(document);
//
//			// Create a ByteArrayOutputStream to hold the output
//			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//
//			// Create a StreamResult to get the output
//			StreamResult result = new StreamResult(outputStream);
//
//			// Transform the XML Document to a string
//			transformer.transform(source, result);
//
//			return outputStream.toString("UTF-8");
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//
//	}
//}
