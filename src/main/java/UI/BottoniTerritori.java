package UI;


import javax.swing.ImageIcon;
import javax.swing.JButton;

import mappa.Costanti;

/**
 * Classe che crea i JButton dei territori
 * @author Matteo
 */
public class BottoniTerritori extends JButton {
	private static final long serialVersionUID = 1L;
	// array dei Jbutton dei territori
	JButton[] button = new JButton[Costanti.NUMTERRITORI];
	
	/**
	 * Inizializza JButton dei territori, associando ad ognuno di essi la
	 * corrispondente immagine
	 */
	public BottoniTerritori() {
		//creo i 6 bottoni ognuno con la sua immagine
		for(int i=0; i<Costanti.NUMTERRITORI; i++){ 
			button[i] = new JButton(new ImageIcon("terreno "+(i+1)+".jpg"));
			button[i].setEnabled(false);
		}
	}
	
	/**
	 * Metodo che restituisce il JButton del territorio che ci interessa 
	 * @param index , cioè l'indice della strada che si vuole considerare
	 * @return il JButton del territorio corrispondente all'indice index
	 */
	protected JButton getButton(int index){ 
		return button[index];
	}
}
