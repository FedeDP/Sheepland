package mappa;

import javax.xml.parsers.*;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
/**
 * XMLParser per inizializzare regioni, strade e coordinate pecore e strade(UI).
 * @author federico
 */
public class XmlParser {
	/**
	 * Parse del xml delle regioni
	 * @param regione : array delle regioni a cui assegnare i valori.
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public static void parseXmlRegione(Regione[] regione) throws SAXException, IOException, ParserConfigurationException{
		File fXmlFile = new File("Regione.xml");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		doc.getDocumentElement().normalize();
		NodeList regioni = doc.getDocumentElement().getElementsByTagName("regione");
	    for (int temp = 0; temp < regioni.getLength(); temp++) {
			ArrayList <Integer> stradeConfini = new ArrayList <Integer>();
	        Node nNode = regioni.item(temp);
	        if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	            Element regioneElement = (Element) nNode;
	            for (int i = 0; (i < (regioneElement.getElementsByTagName("stradeConfini").getLength())); i++) {
	            	stradeConfini.add(Integer.parseInt(regioneElement.getElementsByTagName("stradeConfini").item(i).getTextContent()));	         
	            }
	            regione[temp].setConfini(stradeConfini);
	        }
	    }
	}
	/**
	 * Parser del xml delle strade.
	 * @param strade : array a cui assegnare i valori
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public static void parseXmlStrade(Strada[] strade) throws SAXException, IOException, ParserConfigurationException {
		File fXmlFile = new File("Strade.xml");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		doc.getDocumentElement().normalize();
		NodeList strada = doc.getDocumentElement().getElementsByTagName("strada");
	    for (int temp = 0; temp < strada.getLength(); temp++) {
	    	ArrayList <Integer> stradeAdiacenti = new ArrayList <Integer>();
	        Node nNode = strada.item(temp);
	        if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	            Element stradaElement = (Element) nNode;
	            strade[temp].setIdStrada(Integer.parseInt(stradaElement.getElementsByTagName("idStrada").item(0).getTextContent()));
	            strade[temp].setPrimaRegione(Integer.parseInt(stradaElement.getElementsByTagName("primaRegione").item(0).getTextContent()));
	            strade[temp].setSecondaRegione(Integer.parseInt(stradaElement.getElementsByTagName("secondaRegione").item(0).getTextContent()));
	            for (int i = 0; i < stradaElement.getElementsByTagName("stradeAdiacenti").getLength(); i++) {
	            	stradeAdiacenti.add(Integer.parseInt(stradaElement.getElementsByTagName("stradeAdiacenti").item(i).getTextContent()));
	            }
	            strade[temp].setStradeAdiacenti(stradeAdiacenti);
	        }
	    }
	}
	/**
	 * Parser delle coordinate per UI.
	 * @param temp : indice del nodo da guardare.
	 * @param s : Tag da cercare
	 * @param file : nome del file
	 * @param type : tag da cercare
	 * @return il valore delle coordinate X e Y.
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public static int parseXmlCoord(int temp, String s, String file, String type) throws SAXException, IOException, ParserConfigurationException{
		File fXmlFile = new File(file);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		doc.getDocumentElement().normalize();
		NodeList strada = doc.getDocumentElement().getElementsByTagName(type);
	    Node nNode = strada.item(temp);
	    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	    	Element stradaElement = (Element) nNode;
	    	return Integer.parseInt(stradaElement.getElementsByTagName(s).item(0).getTextContent());
	    }
	    return -1;
	}
}
