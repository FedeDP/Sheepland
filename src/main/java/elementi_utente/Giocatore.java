package elementi_utente;

import java.io.Serializable;
import java.util.ArrayList;

import mappa.MapInit;
import elementi_gioco.CartaTerritorio;
import elementi_gioco.Pastore;
/**
 * Il giocatore. Ogni client ne identifica uno.
 * @author federico
 */
public class Giocatore implements Serializable {
	private static final long serialVersionUID = 1L;
	// territori posseduti dal giocatore
	private Possedimenti properties;
	// soldi del giocatore
	private int soldi;
	// turno del giocatore
	private int turno;
	// quando attivo è true significa che il giocatore sta giocando
	private boolean attivo;
	// in caso di partita a due giocatori, ognuno di essi controlla 2 pastori 
	private ArrayList<Pastore> pastoreControllato = new ArrayList<Pastore>();
	/**
	 * Inizializzazione giocatori.
	 * @param n : numero dei giocatori di quella partita.
	 * @param pos : posizione nel turno di gioco del giocatore.
	 * @param rand : da passare a new Possedimenti.
	 */
	public Giocatore(int n, int pos, int rand) {
		int i; 
		turno = pos;
		// in caso di partita a 2 giocatori, vengono dati 30 denari a testa e si controllano 2 pastori
		if (n == 2) {
			soldi = 30;
			// ciclo che aggiunge un elemento all'array di pastori controllati 
			for (i = 0; i < n; i++) {
				pastoreControllato.add(new Pastore()); 
			}
		// in caso di partita a 3 o 4 giocatori vengono dati 20 denari a testa e si controlla 1 pastore	
		} else {
			soldi = 20;
			pastoreControllato.add(new Pastore());
		}
		// viene assegnata una tessera territorio casuale all'inizio del gioco
		properties = new Possedimenti(rand);
		attivo = true;
	}
	/**
	 * @return i soldi del giocatore.
	 */
	public int getSoldi(){
		return soldi;
	}
	/**
	 * @return : il turno del giocatore.
	 */
	public int getTurno(){
		return turno;
	}
	/**
	 * @param index : del pastore che ci interessa (0 o 1)
	 * @return il pastore.
	 */
	public Pastore getPastore(int index){
		return pastoreControllato.get(index);
	}
	/**
	 * @return il numero di pastori controllati dal giocatore.
	 */
	public int getNumPastori() {
		return pastoreControllato.size();
	}
	/**
	 * @return i possedimenti del giocatore.
	 */
	public Possedimenti getPossedimenti() {
		return properties;
	}
	/**
	 * Acquista una carta terreno.
	 * @param control : tipo del territorio
	 * @param numPastore : pastore con cui si vuole comprare
	 * @param map : mappa della partita.
	 */
	public void acquista (CartaTerritorio control, int numPastore, MapInit map){
			// vengono decrementati i soldi in base al costo della carta
			soldi = soldi - control.getCosto();
			// si compra il territorio in base alla posizione del pastore
			pastoreControllato.get(numPastore).compraTerreno(control, map);
			// si aggiornano i possedimenti del giocatore
			properties.updatePossedimenti(control.getTerritorio() - 1);
	}
	/**
	 * Muove il pastore in una nuova posizione.
	 * @param nuovaPosizione : indice della nuova strada
	 * @param pastore : quale pastore vogliamo muovere (nel caso ne controlliamo più d'uno)
	 * @param map : mappa della partita.
	 */
	public void muoviPastore(int nuovaPosizione, int pastore, MapInit map) {
		// viene recintata la strada su cui si trovava il pastore prima dello spostamento
		map.getStrada(pastoreControllato.get(pastore).getPosizione()).recinta();
		// viene settato occupato a false, perchè ora la strada è recintata 
		map.getStrada(pastoreControllato.get(pastore).getPosizione()).occupa(false);
		// viene settato occupato a true sulla nuova strada
		map.getStrada(nuovaPosizione).occupa(true);
		for (int x : map.getStrada(pastoreControllato.get(pastore).getPosizione()).getStradeAdiacenti()) {
			if (x == nuovaPosizione) {
				// viene settata la nuova posizione del pastore
				pastoreControllato.get(pastore).setPosizione(nuovaPosizione);
				return;
			}
		}
		pastoreControllato.get(pastore).setPosizione(nuovaPosizione);
		// i soldi vengono decrementati solo se ci si sposta su una strada non direttamente collegata a quella in cui ci si trova
		soldi--;	
	}
	
	public boolean getAttivo() {
		return attivo;
	}
	
	public void setAttivo(boolean x) {
		attivo = x;
	}
		
}