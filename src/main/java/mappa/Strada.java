package mappa;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
/**
 * Elemento della mappa: strada
 * @author federico
 */
public class Strada implements Serializable {
	private static final long serialVersionUID = 1L;
	// indica se la strada è recintata 
	private boolean recintato;
	//numero strada da 1 a 6
	private int idStrada;	
	// una delle regioni adiacenti alla strada considerata
	private int primaRegione;
	// l'altra regione adiacente alla strada considerata
	private int secondaRegione;
	// indica se la strada è occupata
	private boolean occupato;
	// le strade direttamente collegate a quella considerata
	private List <Integer> stradeAdiacenti = new ArrayList <Integer>();
	/**
	 * Ogni strada viene inizializzata tramite xml parser e con recintato e occupato a false.
	 */
	public Strada(){
		this.recintato = false;
		this.occupato = false;
	}
	/**
	 * @return recintato
	 */
	public boolean getRecintato() {
		return recintato;
	}
	/**
	 * Recinta la strada
	 */
	public void recinta() {
		this.recintato = true;
	}
	/**
	 * setta le strade adiacenti a questa.
	 * @param stradeAdiacenti : arraylist a cui assegnare il nostro arraylist locale di strade adiacenti.
	 */
	public void setStradeAdiacenti(ArrayList<Integer> stradeAdiacenti) {
		this.stradeAdiacenti = stradeAdiacenti;
	}
	/**
	 * @return le strade adiacenti
 	 */
	public List<Integer> getStradeAdiacenti() {
		return stradeAdiacenti;
	}
	/**
	 * setta l'id della strada
	 * @param idStrada
	 */
	public void setIdStrada(int idStrada) {
		this.idStrada = idStrada;
	}
	/**
	 * @return id della strada
	 */
	public int getIdStrada() {
		return idStrada;
	}
	/**
	 * Setta l'indice della prima regione con cui confina la strada
	 * @param primaRegione
	 */
	public void setPrimaRegione(int primaRegione) {
		this.primaRegione = primaRegione;
	}
	/**
	 * Setta l'indice della seconda regione con cui confina la strada
	 * @param primaRegione
	 */
	public void setSecondaRegione(int secondaRegione) {
		this.secondaRegione = secondaRegione;
	}
	/**
	 * @param map
	 * @return la prima regione con cui confina
	 */
	public Regione getPrimaRegione(MapInit map) {
		return map.getRegione(primaRegione);
	}
	/**
	 * @param map
	 * @return la seconda regione con cui confina
	 */
	public Regione getSecondaRegione(MapInit map) {
		return map.getRegione(secondaRegione);
	}
	/**
	 * @return indice della prima regione con cui confina
	 */
	public int getIndexPrimaRegione() {
		return primaRegione;
	}
	/**
	 * @return indice della seconda regione con cui confina
	 */
	public int getIndexSecondaRegione() {
		return secondaRegione;
	}
	/**
	 * @return occupato
	 */
	public boolean getOccupato() {
		return occupato;
	}
	/**
	 * occupa la strada
	 * @param x true/false
	 */
	public void occupa(boolean x) {
		occupato = x;
	}
}