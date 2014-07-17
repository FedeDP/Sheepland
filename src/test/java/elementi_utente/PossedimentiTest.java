package elementi_utente;

import static org.junit.Assert.*;

import org.junit.Test;

public class PossedimentiTest {
	Possedimenti proprietà = new Possedimenti(3);
	
	@Test
	public void testUpdatePossedimenti(){
		int terreno = 3;
		proprietà.updatePossedimenti(3);
		assertEquals(proprietà.getPossedimenti(terreno), 2);
	}

	/*@Test
	public void testGetPossedimenti(){
		fail("not yet implemented");
	}*/
	
}