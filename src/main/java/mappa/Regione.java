package mappa;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import elementi_gioco.Pecora;
/**
 * Elemento della mappa: regione
 * @author federico
 */
public class Regione implements Serializable {
	private static final long serialVersionUID = 1L;
	// tipo di terreno della regione
	private int terreno;
	// le strade che delimitano il confine di una regione
	private List <Integer> stradeConfini = new ArrayList <Integer>();
	// le pecore presenti nella regione
	private List <Integer> pecore = new ArrayList <Integer>();
	/**
	 * Inizializzo le regioni dandogli una pecoraPresente e un terreno.
	 * Regione[0] è sheepsburg
	 * @param i
	 */
	public Regione (int i, int x){
		terreno = i;
		if (i == 0) {
			pecore.add(i);
		} else {
			pecore.add(x);
		}
	}
	/**
	 * Setto i confini della regione (le strade)
	 * @param confini
	 */
	public void setConfini(ArrayList <Integer> confini) {
		stradeConfini = confini;
	}
	/**
	 * @return territorio della regione
	 */
	public int getTerritorio() {
		return terreno;
	}
	/**
	 * @return il numero di pecore presenti
	 */
	public int getPecorePresenti() {
		return pecore.size();
	}
	/**
	 * @return i confini della regione.
	 */
	public List <Integer> getConfini (){
		return stradeConfini;
	}
	/**
	 * permette di selezionare una pecora presente in una regione
	 * @param map la mappa
	 * @return l'indice globale di una delle pecore presenti in questa regione
	 */
	public Pecora getPecora(MapInit map) {
		int randomNum = (int) (Math.random()* pecore.size());
		return map.getPecora(pecore.get(randomNum));
	}
	/**
	 * rimuove l'indice della pecora passata come parametro, dall'arraylist delle pecore della regione.
	 * @param pecora
	 */
	public void removePecora(Pecora pecora) {
		pecore.remove((Integer) pecora.getIndex());
	}
	/**
	 * aggiunge l'indice della pecora all'arraylist delle pecore della regione
	 * @param pecora
	 */
	public void addPecora(Pecora pecora) {
		pecore.add(pecora.getIndex());
	}
	/**
	 * @return arraylist degli indici globali delle pecore presenti
	 */
	public List <Integer> getPecore() {
		return pecore;
	}
}
