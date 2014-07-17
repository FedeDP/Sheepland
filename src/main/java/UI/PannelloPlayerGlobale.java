package UI;

import java.io.IOException;

import javax.swing.JPanel;

/**
 * JPanel che contiene tutti i JPanel dei player che stanno partecipando alla
 * partita
 * @author Matteo
 *
 */
public class PannelloPlayerGlobale extends JPanel {
	private static final long serialVersionUID = 1L;
	// il singolo pannello del giocatore
	private PanelPlayer[] player;
	
	/**
	 * Aggiunge al PannelloPlayerGlobale i singoli pannelli dei giocatri che
	 * partecipano alla partita
	 * @param numPlayers
	 * @throws IOException
	 */
	public PannelloPlayerGlobale(int numPlayers, int soldi) throws IOException {
		// array dei singoli pannelli dei player 
		player = new PanelPlayer[numPlayers];
		setLayout(null);
		// vengono settate posizione e dimensioni di PannelloPlayerGlobale
		this.setBounds(662,30,189,617);
		// ciclo che crea e ridimensiona i pannelli dei singoli player
		for (int i = 0; i < numPlayers; i++) {
			player[i] = new PanelPlayer(i + 1, soldi);
			player[i].setBounds(10, 20 + i * 140, 170, 120);
			//aggiungo al pannello globale i singoli pannelli
			add(player[i]);
		}
	}
	
	/**
	 * Metodo un singolo pannello dei giocatori
	 * @param i , cioè l'indice del pannello che si vuole considerare
	 * @return il JPanel del giocator corrispondente all'indice index
	 */
	protected PanelPlayer getPannello(int i){
		return player[i];
	}
}