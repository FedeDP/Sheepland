package elementi_gioco;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import mappa.MapInit;

import org.junit.Test;
import org.xml.sax.SAXException;

public class PastoreTest {
	
	Pastore p = new Pastore();
	
	@Test
	public void testSetPosizione() {
		p.setPosizione(2);
		assertEquals(p.getPosizione(), 2);
	}

	@Test
	public void testGetPosizione() {
		p.setPosizione(2);
		assertEquals (p.getPosizione(), 2);
	}

	@Test
	public void testCompraTerreno() throws SAXException, IOException, ParserConfigurationException {
		MapInit map = new MapInit();
		CartaTerritorio control = new CartaTerritorio(1); 
		p.compraTerreno(control, map);
 		assertEquals(control.getTerritorio(), 1);
 		assertEquals (control.getCarteRimaste(), 4);
 		assertEquals (control.getCosto(), 1);
	}
	
	@Test
	public void testSpostaPecora() throws SAXException, IOException, ParserConfigurationException {
		Pecora pecora = new Pecora(1);
		MapInit map = new MapInit();
		int nuovaPosizione = 2;
		p.spostaPecora(pecora, nuovaPosizione, map);
		assertEquals(pecora.getPosition(), 2);
		assertEquals(map.getRegione(1).getPecorePresenti(), 0);
		assertEquals(map.getRegione(nuovaPosizione).getPecorePresenti(), 2);
	}
}
	