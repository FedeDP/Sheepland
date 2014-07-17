package elementi_gioco;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import mappa.MapInit;

import org.junit.Test;
import org.xml.sax.SAXException;


public class PecoraNeraTest {
	PecoraNera bsheep = new PecoraNera(0);
	
	@Test
	public void testSetRandomDirection() throws SAXException, IOException, ParserConfigurationException{
		MapInit map = new MapInit();
		int randomNum = 6;
		int posizionePecora = bsheep.getPosition();
		for (Integer x : map.getRegione(posizionePecora).getConfini()) {
			if (map.getIdStrada(x) == randomNum) {
				if (posizionePecora == map.getStrada(x).getIndexPrimaRegione())
					posizionePecora = map.getStrada(x).getIndexSecondaRegione();
				else
					posizionePecora = map.getStrada(x).getIndexPrimaRegione();
				break;
			}
		}
		assertEquals(posizionePecora, 6);
	}

	
	@Test
	public void testGetPosition() {
		assertEquals(bsheep.getPosition(),0);
	}
	
	@Test
	public void testSetPosition() {
		bsheep.setPosition(5);
		assertEquals(bsheep.getPosition(), 5);
	}
	
	/*@Test
	public void testGetIndex(){
		fail("not yet implemented");
	}*/
}
