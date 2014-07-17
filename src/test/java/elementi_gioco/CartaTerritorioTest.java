package elementi_gioco;

import static org.junit.Assert.*;

import org.junit.Test;

public class CartaTerritorioTest {

	CartaTerritorio card = new CartaTerritorio(1);
	
	@Test
	public void testCartaAcquistata() {
		card.cartaAcquistata();
		int costo = card.getCosto();
		int contRimaste = card.getCarteRimaste();
		assertEquals(costo,1);
		assertEquals(contRimaste, 4);
	}

	@Test
	public void testGetCarteRimaste() {
		int contRimaste = card.getCarteRimaste();
		assertEquals(contRimaste, 5);
	}

	@Test
	public void testGetCosto() {
		int costo = card.getCosto();
		assertEquals(costo, 0);
	}

	@Test
	public void testGetTerritorio() {
		int terreno = card.getTerritorio();
		assertEquals(terreno, 1);
	}

}
