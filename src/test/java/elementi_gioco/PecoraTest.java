package elementi_gioco;

import static org.junit.Assert.*;

import org.junit.Test;

public class PecoraTest {
	Pecora sheep = new Pecora(3);

	/*@Test
	public void testGetIndex(){
		fail("not yet implemented");
	}*/
	
	@Test
	public void testGetPosition() {
		assertEquals(sheep.getPosition(),3);
	}
	
	@Test
	public void testSetPosition() {
		sheep.setPosition(5);
		assertEquals(sheep.getPosition(), 5);
	}

}
