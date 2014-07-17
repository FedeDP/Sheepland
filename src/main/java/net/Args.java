package net;

import java.io.Serializable;
/**
 * Classe che viene usata per spedire le mosse e i loro attributi al server, dal client.
 * @author federico
 */
public class Args implements Serializable {
	private static final long serialVersionUID = 1L;
	int mossa;
	int attributoMossa;
	int pastoreControllato;
	/**
	 * Assegna i valori della mossa
	 * @param mossa : da 1 a 3 che mossa stiamo inviando (muovipastore, spostapecora e compraterritorio
	 * @param attributoMossa : secondo attributo della mossa, ad esempio dove vogliamo muovere il pastore, o che territorio vogliamo comprare
	 * @param pastoreControllato : 0 o 1. In caso di partita con 2 giocatori, quale pastore vogliamo muovere.
	 */
	public Args(int mossa, int attributoMossa, int pastoreControllato) {
		this.mossa = mossa;
		this.attributoMossa = attributoMossa;
		this.pastoreControllato = pastoreControllato;
	}
}
