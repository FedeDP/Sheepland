package elementi_gioco;

import java.io.Serializable;

import mappa.MapInit;
/**
 * Elemento di gioco pastore.
 * @author federico
 */
public class Pastore implements Serializable {
	private static final long serialVersionUID = 1L;
	// indice dell'array globale di strade
	private int posizionePastore;
	/**
	 * utilizzato per settare la posizione del pastore a inizio gioco.
	 * @param pos : la posizione decisa dall'utente
	 */
	public void setPosizione(int pos) {
		posizionePastore = pos;
	}
	/**
	 * Troviamo la posizione del pastore!
	 * @return l'indice della strada in cui si trova il pastore.
	 */
	public int getPosizione() {
		return posizionePastore;
	}
	/**
	 * Chiamata dal Giocatore, questa mossa compra un terreno
	 * @param control : terreno da comprare (da 0 a 5)
	 * @param map (mappa)
	 */
	public void compraTerreno(CartaTerritorio control, MapInit map) {
		control.cartaAcquistata();		
	}
	
	/**
	 * Il pastore può spostare una pecora da una regione a un'altra.
	 * @param pecora : pecora da spostare
	 * @param nuovaPosizione : nuova posizione in cui spostarla
	 * @param map : mappa
	 */
	public void spostaPecora(Pecora pecora, int nuovaPosizione, MapInit map) {
		// la pecora viene rimossa dalla regione
		map.getRegione(pecora.getPosition()).removePecora(pecora);
		// viene settata la nuova posizione della pecora
		pecora.setPosition(nuovaPosizione);
		// la pecora viene aggiunta alla nuova regione
		map.getRegione(pecora.getPosition()).addPecora(pecora);
	}
}