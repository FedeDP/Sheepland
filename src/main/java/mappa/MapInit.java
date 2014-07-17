package mappa;

import java.io.IOException;
import java.io.Serializable;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import elementi_gioco.CartaTerritorio;
import elementi_gioco.Pecora;
import elementi_gioco.PecoraNera;
/**
 * Inizializza tutti gli elementi del gioco (la mappa)
 * @author federico
 */
public class MapInit implements Serializable {
	private static final long serialVersionUID = 1L;
	// array delle regioni della mappa
	private Regione[] regione = new Regione [Costanti.NUMREGIONI];
	// array delle strade della mappa
	private Strada[] strada = new Strada [Costanti.NUMSTRADE];
	// array delle pecore sulla mappa
	private Pecora[] pecora = new Pecora [Costanti.NUMPECORE];
	// carte territorio che rappresentano i territori della mappa
	private CartaTerritorio[] carte = new CartaTerritorio [Costanti.NUMTERRITORI];
	/**
	 * Inizializza regioni, strade, pecore e carte territorio.
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public MapInit () throws SAXException, IOException, ParserConfigurationException {
		int i;
		// regione[0] è sheepsburg
		regione[0] = new Regione(0, 0); 
		// ciclo che permette di inizializzare le regioni a seconda del tipo di territorio (da 1 a 3 pianura, da 4 a 6 pascolo ecc)
		for (i = 0; i < Costanti.NUMREGIONI - 1; i++) {
			regione[i + 1] = new Regione(i/3 + 1, i + 1);	
		}
		XmlParser.parseXmlRegione(regione);
		for (i = 0; i < Costanti.NUMSTRADE; i++) {
			strada[i] = new Strada();
		}
		XmlParser.parseXmlStrade(strada);
		// viene messa la pecora nera a sheepsburg
		pecora[Costanti.SHEEPSBURG] = new PecoraNera(Costanti.SHEEPSBURG);
		for (i = 1; i < Costanti.NUMPECORE; i++) {
			pecora[i] = new Pecora(i);
		}
		for (i = 0; i < Costanti.NUMTERRITORI; i++) {
			carte[i] = new CartaTerritorio(i+1);
		}
	}
	/**
	 * @param index della strada che ci interessa
	 * @return i-esima strada.
	 */
	public Strada getStrada(int index) {
		return strada[index];
	}
	/**
	 * @param index della regione che ci interessa
	 * @return i-esima regione
	 */
	public Regione getRegione(int index) {
		return regione[index];
	}
	/**
	 * Per comodità.
	 * @param index della strada che ci interessa
	 * @return l'id (da 1 a 6) della i-esima strada
	 */
	public int getIdStrada(int index) {
		return strada[index].getIdStrada();
	}
	/**
	 * @param index della carta che ci interessa
	 * @return la carta che ci interessa
	 */
	public CartaTerritorio getCarta(int index) {
		return carte[index];
	}
	/**
	 * @param index della pecora
	 * @return la pecora 
	 */
	public Pecora getPecora(int index) {
		return pecora[index];
	}
	/**
	 * @return la pecora nera.
	 */
	public PecoraNera getPecoraNera() {
		return (PecoraNera) pecora[Costanti.SHEEPSBURG];
	}
}