package UI;

import java.io.IOException;

import javax.swing.JLabel;

import mappa.Costanti;

/**
 * Classe che crea le JLabel che indicano il numero di territori acquistati per
 * ogni tipo
 * @author Matteo
 */
public class LabelContTerritori extends JLabel {
	private static final long serialVersionUID = 1L;
	// array delle JLabel che indicano i territori
	private JLabel[] labelIndicatore = new JLabel[Costanti.NUMTERRITORI];
	// testo della JLabel
	private int numCarte;

	/**
	 * Inizializza il vettore di JLabel, settando come testo di ogni elemento
	 *  il numero dei territori acquistati per ogni tipo
	 * @throws IOException
	 */
	public LabelContTerritori() throws IOException{
		//creo i 6 label dove ci sarà il num di territori posseduti
		for(int i=0; i<Costanti.NUMTERRITORI; i++){ 
			labelIndicatore[i]=new JLabel(numCarte+"");
			repaint();
		}
	}
	
	/**
	 *Metodo che restituisce JLabel che indica in numero di territori posseduti
	 *del tipo che ci interessa 
	 * @param index , cioè l'indice della JLabel che si vuole considerare
	 * @returnil JBuotton del territorio corrispondente all'indice index
	 */
	protected JLabel getLabelIndicatore(int index){ 
		return labelIndicatore[index];
	}
}