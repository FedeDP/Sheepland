package elementi_utente;

import java.io.Serializable;
import java.util.ArrayList;

import mappa.Costanti;
import mappa.MapInit;
/**
 * Elemento Partita, che identifica lo stato di una partita.
 * @author federico
 */
public class Partita implements Serializable {
	private static final long serialVersionUID = 1L;
	// giocatori della partita
	private ArrayList <Giocatore> players = new ArrayList<Giocatore>();
	// la mappa della partita
	private MapInit map;
	// il turno di gioco nella partita
	private int turnoDiGioco;
	// variabile che indica se la partia è finita 
	private boolean finita; 
	// variabile che indica a quale mossa si è arrivati
	private int numMosseTurno;
	// variabile che indica in numero dei recinti nella partita
	private int numRecinti;
	/**
	 * Ogni partita viene inizializzata aggiungendo giocatori
	 * per ogni client connesso, impostando il turno di gioco iniziale a 0,
	 * il boolean finita a false e salvando la mappa ricevuta in ingresso.
	 * @param numPlayers : numero dei client della partita
	 * @param map : mappa della partita.
	 */
	public Partita (int numPlayers, MapInit map) {
		int j, i;
		int[] rand = new int[numPlayers];
		int[] available = new int[Costanti.NUMTERRITORI];
		for (i = 0; i < Costanti.NUMTERRITORI; i++) {
			available[i] = i;
		}
		// ciclo che assegna una carta territorio casuale ad una variabile che poi verrà passata al giocaotre
		for (i = 0; i < numPlayers; i++) {
			do {
				j = (int) (Math.random() * (Costanti.NUMTERRITORI));
			} while (available[j] == -1);		
			rand[i] = available[j];
			available[j] = -1;
		}
		// ciclo che aggiunge un nuovo giocatore alla partita
		for (i = 0; i < numPlayers; i++) {
			players.add(new Giocatore(numPlayers, i, rand[i]));
		}
		this.map = map;
		turnoDiGioco = 0;
		finita = false;
		numMosseTurno = 0;
		numRecinti = Costanti.NUMRECINTI;
	}
	/**
	 * @param i : index del giocatore che ci interessa
	 * @return : il giocatore i-esimo
	 */
	public Giocatore getGiocatore(int i) {
		return players.get(i);
	}
	/**
	 * @return : la mappa della partita.
	 */
	public MapInit getMap () {
		return map;
	}
	/**
	 * @return il numero di giocatori iniziali connessi
	 */
	public int getNumPlayers() {
		return players.size();
	}
	/**
	 * @return : il numero di giocatori attivi della partita.
	 */
	public int getActiveNumPlayers() {
		int cont = 0;
		for(int i = 0; i < players.size(); i++) {
			if (players.get(i).getAttivo()) {
				cont++;
			}
		}
		return cont;
	}
	/**
	 * @return : il turno attuale di gioco.
	 */
	public int getTurno() {
		return turnoDiGioco;
	}
	/**
	 * Cambia il turno di gioco, incrementandolo di una unità.
	 * Se turno di gioco diventa uguale al numero di giocatori, va riportato a 0.
	 */
	public void cambiaTurno() {
		turnoDiGioco++;
		if(turnoDiGioco >= players.size()) {
			turnoDiGioco = 0;
		}
	}
	/**
	 * @return : se la partita è finita.
	 */
	public boolean getFinita() {
		return finita;
	}
	/**
	 * Termina la partita: imposta a true il boolean finita.
	 */
	public void termina() {
		finita = true;
	}
	
	/**
	 * @return : il numero di mosse effettuate durante il turno
	 */
	public int getNumMosseTurno() {
		return numMosseTurno;
	}
	
	/**
	 *  incrementa l'intero numMosseTurno ogni volta che si esegue una mossa
	 */
	public void incrementaNumMosseTurno() {
		numMosseTurno++;
	}
	
	/**
	 * setta a 0 l'intero numMosseTurno
	 */
	public void azzeraMosse() {
		numMosseTurno = 0;
	}
	
	/**
	 * 
	 * @return : il numero di recinti rimanenti
	 */
	public int getRecintiRimanenti() {
		return numRecinti;
	}
	
	/**
	 *  diminuisce il numero dei recinti
	 */
	public void decrementaRecinti() {
		numRecinti--;
	}
}
