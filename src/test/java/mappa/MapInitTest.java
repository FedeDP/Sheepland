package mappa;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

public class MapInitTest {
	
	/*@Test
	public void testGetIdStrada() throws SAXException, IOException, ParserConfigurationException {
		MapInit map = new MapInit();
	
	}
	
	@Test
	public void testGetCarta(){
		fail("not yet implemented");
	}
	
	@Test
	public void testGetPecora(){
		fail("not yet implemented");
	}
	
	@Test
	public void testGetPecoraNera(){
		fail("not yet implemented");
	}*/
	
	@Test
	public void testGetStrada() throws SAXException, IOException, ParserConfigurationException {
		MapInit map = new MapInit();
		int index = 0;
		assertEquals (map.getStrada(index).getIdStrada(), 2);
	}

	@Test //non so se è corretto testarlo in questo modo
	public void testGetRegione() throws SAXException, IOException, ParserConfigurationException {
		MapInit map = new MapInit();
		int index = 3;
		assertEquals (map.getRegione(index).getTerritorio(), 1);
	}

}
