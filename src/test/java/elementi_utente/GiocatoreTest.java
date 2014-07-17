package elementi_utente;

import static org.junit.Assert.*;

import java.io.IOException;





import javax.xml.parsers.ParserConfigurationException;

import mappa.MapInit;

import org.junit.Test;
import org.xml.sax.SAXException;

import elementi_gioco.CartaTerritorio;

public class GiocatoreTest {
	Giocatore player = new Giocatore(3, 1, 2); //crea un giocatore che ha un pastore, è il primo nel turno ha e una tessere territorio iniziale 2  
	/*@Test
	public void testGetSoldi() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetTurno() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testGetPastore(){
		fail("Not yet implemented");
	}
	
	@Test
	public void testGetNumPastori(){
		fail("Not yet implemented");
	}
	
	@Test
	public void testGetPossedimenti(){
		fail("Not yet implemented");
	}*/
	
	@Test
	public void testAcquista() throws SAXException, IOException, ParserConfigurationException {
		CartaTerritorio control = new CartaTerritorio(1);
		MapInit map = new MapInit();
		int numPastore = 0;
		player.acquista(control, numPastore, map);
		assertEquals (control.getCarteRimaste(), 4);
		assertEquals(control.getCosto(),1);
	}

	@Test //test per il movimento gratuito (nelle strade collegate)
	public void testMuoviPastore() throws SAXException, IOException, ParserConfigurationException {
		MapInit map = new MapInit();
		int pastore = 0;
		int idStrada = 7;
		player.getPastore(0).setPosizione(2);
		player.muoviPastore(idStrada, pastore, map);
		assertTrue(map.getStrada(7).getOccupato());
		assertTrue(map.getStrada(2).getRecintato());
		assertEquals(player.getPastore(pastore).getPosizione(), 7);
	}

	@Test //test per il movimento a pagamento (nelle strade non collegate) 
	public void testMuoviPastore2() throws SAXException, IOException, ParserConfigurationException {
		MapInit map = new MapInit();
		int pastore = 0;
		int idStrada = 10;
		player.getPastore(0).setPosizione(2);
		player.muoviPastore(idStrada, pastore, map);
		assertTrue(map.getStrada(10).getOccupato());
		assertTrue(map.getStrada(2).getRecintato());
		assertEquals(player.getSoldi(), 19);
		assertEquals(player.getPastore(pastore).getPosizione(), 10);
	}
}
