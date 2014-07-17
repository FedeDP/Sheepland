package UI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JPanel;

import mappa.Costanti;
import net.Args;
import net.PartitaInterface;

/**
 * Classe in cui vengono creati i JPanel che andranno a contenere i bottoni 
 * dei territori
 * @author Matteo
 *
 */
public class PannelliTerritori{
	// array dei 6 pannelli territorio
	private JPanel[] panel = new JPanel[Costanti.NUMTERRITORI];
	public int i;
	// bottone associato ad ogni territorio
	final BottoniTerritori button = new BottoniTerritori();
	// label dove compare il numero di territori acquistati
	final LabelContTerritori label = new LabelContTerritori();
	private static final Logger LOGGER = Logger.getLogger(PannelliTerritori.class.getName());
	/**
	 * Inizializza il vettore dei JPanel e aggiunge ad ogni elemento il bottone
	 * e la label che indica i territori acquistati. Ad ogni bottone è associato
	 * un evento
	 * @param out , per inviare la mossa al server
	 * @param game , cioè l'interfaccia per inviare la mossa dal client RMI
	 * @throws IOException
	 */
	public PannelliTerritori(final ObjectOutputStream out, final PartitaInterface game) throws IOException{
		//ciclo che crea un pannello e gli mette sopra il suo bottone, la label associata
		for(i = 0; i < Costanti.NUMTERRITORI; i++){
			panel[i] = new JPanel();
			panel[i].add(button.getButton(i));
			panel[i].add(label.getLabelIndicatore(i));
			// ad ogni bottone viene aggiunto un action listener
			button.getButton(i).addActionListener(new ActionListener(){
				int x = i;
				/**
				 * Quando si acquista un territorio, incrementa il numero
				 * nella label corrispondente
				 */
				public void actionPerformed(ActionEvent e){
					// azione che permette al giocatore di comprare un territorio
					if (game == null) {
					try {
						out.writeObject(new Args(Costanti.COMPRATERRITORIO, x, 0));
					} catch (IOException e1) {
						LOGGER.log(Level.CONFIG, "Logging an IO error", e1);
					}
					} else {
						try {
							game.inviaMossa(new Args(Costanti.COMPRATERRITORIO, x, 0));
						} catch (RemoteException e1) {
							LOGGER.log(Level.CONFIG, "Logging an remote error", e1);
						} catch (InterruptedException e1) {
							LOGGER.log(Level.CONFIG, "Logging an interrupted error", e1);
						}
					}
	    		}
			});
		}
	}
	
	/**
	 * Metodo che restituisce il JPanel del territorio che ci interessa
	 * @param index , cioè l'indice del JPanel che si vuole considerare
	 * @return il JPanel del territorio corrispondente all'indice index
	 */
	protected JPanel getPanel(int index){ 
		return panel[index];
	}
	
	/**
	 * Metodo che restituisce il JButton del territorio che ci interessa
	 * @param index , cioè l'indice del JPanel che si vuole considerare
	 * @return il JButton del territorio corrispondente all'indice index
	 */
	protected JButton getButton(int index) {
		return button.button[index];
	}
	
	/**
	 * Metodo che aggiorna la JLabel che conta il numero dei territori
	 * @param index , cioè l'indice della JLabel da aggiornare
	 * @param num , cioè il nuovo valore che avrà la JLabel
	 */
	protected void updateLabel(int index, int num) {
		label.getLabelIndicatore(index).setText(num + "");
	}
}
