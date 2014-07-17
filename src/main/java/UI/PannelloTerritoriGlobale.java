package UI;

import java.awt.GridLayout;
import java.io.IOException;
import java.io.ObjectOutputStream;

import javax.swing.JPanel;

import net.PartitaInterface;

/**
 * JPanel che contiene tutti i pannelli con i bottoni dei territori
 * @author Matteo
 *
 */
public class PannelloTerritoriGlobale extends JPanel {
	private static final long serialVersionUID = 1L;
 	// i singoli pannelli dei bottoni dei territori 
	PannelliTerritori panel;

	/**
	 * Aggiunge tutti i singoli pannelli dei territori al PannelloTerritoriGlobale
	 * e disabilita i bottoni  
	 * @throws IOException
	 */
	public PannelloTerritoriGlobale(ObjectOutputStream out, PartitaInterface game) throws IOException {
		panel = new PannelliTerritori(out, game);
		// viene settato il layout del pannello (6 righe , 1 colonna; gli altri 2 parametri indicano al distanza tra un pannello e l'altro)
		setLayout(new GridLayout(6,1, 10, 8));
		this.setBounds(0, 27, 189, 617);
		// aggiungo al pannello globale altri pannelli
		add(panel.getPanel(0)); 
		add(panel.getPanel(1)).setEnabled(false);
		add(panel.getPanel(2)).setEnabled(false);
		add(panel.getPanel(3)).setEnabled(false);
     	add(panel.getPanel(4)).setEnabled(false);
     	add(panel.getPanel(5)).setEnabled(false);
     	// disattivo i pannelli all'inizio
     	panel.getButton(0).setEnabled(false);
     	panel.getButton(1).setEnabled(false);
     	panel.getButton(2).setEnabled(false);
     	panel.getButton(3).setEnabled(false);
     	panel.getButton(4).setEnabled(false);
     	panel.getButton(5).setEnabled(false);
     	
	}
	
	/**
	 * Metodo che restituisce un singolo pannello dei territori
	 * @return un pannello dei territori
	 */
	protected PannelliTerritori getPanel(){
		return panel;
	}
}

