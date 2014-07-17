package mappa;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

import elementi_gioco.Pecora;

public class RegioneTest {
	Regione regione = new Regione(1,3);
	
	/*@Test
	public void testSetConfini() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testAddPecora() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetPecore() {
		fail("not yet implemented");
	}	
		
	@Test
	public void testGetConfini() {
		fail("Not yet implemented");
	}*/

	@Test
	public void testGetTerritorio() {
		assertEquals(regione.getTerritorio(),1);
	}

	@Test
	public void testGetPecorePresenti() {
		int pecorePresenti;
		pecorePresenti=regione.getPecorePresenti();
		assertEquals(pecorePresenti, 1);
	}


	@Test
	public void testGetPecora() throws SAXException, IOException, ParserConfigurationException {
		MapInit map = new MapInit();
		Pecora pecora=new Pecora(3);
		int numRandom = 0;
		pecora=map.getPecora((regione.getPecore().get(numRandom)));
		assertEquals(pecora.getIndex(),3);
	}

	@Test
	public void testRemovePecora() {
		Pecora pecora=new Pecora(3);
		regione.removePecora(pecora);
		assertEquals(regione.getPecore().size(),0);
	}

}
