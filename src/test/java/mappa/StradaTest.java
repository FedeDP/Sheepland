package mappa;

import static org.junit.Assert.*;

import org.junit.Test;

public class StradaTest {
	Strada street = new Strada();
	
	@Test
	public void testRecinta() {
		street.recinta();
		assertTrue(street.getRecintato());
	}
	
	/*@Test
	public void testGetRecintato() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetStradeAdiacenti() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetStradeAdiacenti() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetIdStrada() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetIdStrada() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetPrimaRegione() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetSecondaRegione() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetPrimaRegione() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetSecondaRegione() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetIndexPrimaRegione() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetIndexSecondaRegione() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetOccupato() {
		fail("Not yet implemented");
	}*/

	@Test
	public void testOccupa() {
		boolean x = true;
		street.occupa(x);
		assertTrue(street.getOccupato());
	}

}
