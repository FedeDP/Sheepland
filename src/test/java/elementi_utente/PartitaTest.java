package elementi_utente;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import mappa.MapInit;

import org.junit.Test;
import org.xml.sax.SAXException;

public class PartitaTest{
	MapInit map;
	Partita game = new Partita(3, map); //partita con 3 giocatori
	
	/*@Test
	public void testGetGiocatore() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetMap() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testGetTurno(){
		fail("Not yet implemented");
	}

	@Test
	public void testGetFinita(){
		fail("Not yet implemented");
	}	
		
	@Test
	public void testGetNumMosseTurno(){
		fail("Not yet implemented");
	}*/
	
	@Test
	public void testGetActiveNumPlayers(){
		assertEquals(game.getActiveNumPlayers(),3);
	}
	
	@Test
	public void testCambiaTurno() throws SAXException, IOException, ParserConfigurationException{
		assertEquals(game.getTurno(), 0);
		game.cambiaTurno();
		assertEquals(game.getTurno(), 1);
	}
	
	@Test
	public void testGetNumPlayers() {
		assertEquals(game.getNumPlayers(), 3);
	}

	@Test
	public void testTermina(){
		game.termina();
		assertTrue(game.getFinita());
	}
	
	@Test
	public void testIncrementaNumMosseTurno() {
		game.incrementaNumMosseTurno();
		game.incrementaNumMosseTurno();
		assertEquals(game.getNumMosseTurno(),2);
	}
	
	@Test
	public void testAzzeraMosse() {
		game.incrementaNumMosseTurno();
		int numMosse = game.getNumMosseTurno();
		assertEquals(numMosse, 1);
		game.azzeraMosse();
		numMosse = game.getNumMosseTurno();
		assertEquals(numMosse, 0);
	}
	
}
