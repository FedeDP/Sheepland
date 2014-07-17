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
import javax.xml.parsers.ParserConfigurationException;

import net.Args;
import net.PartitaInterface;

import org.xml.sax.SAXException;

import mappa.Costanti;
import mappa.XmlParser;

/**
 * Il JPanel che contiene i JButton delle strade
 * @author Matteo
 *
 */

public class BottoniStrade extends JPanel{
	private static final long serialVersionUID = 1L;
	// bottoni che si trovano su ogni strada
	private JButton[] nodiStrade = new JButton[Costanti.NUMSTRADE];
	// immagini dei pastori che compariranno sulla mappa
	private PastoriImages[] pastore;
	private int i;
	private static final Logger LOGGER = Logger.getLogger(BottoniStrade.class.getName());

	/**
	 * Inizializza il JPanel, creando i Jbutton in corrispondenza degli incroci delle
	 * strade. Ad ogni JButton è associato un evento	
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public BottoniStrade(final MainFrame f, final ObjectOutputStream out, PastoriImages[] pastore, final PartitaInterface game) throws SAXException, IOException, ParserConfigurationException {
		this.pastore = pastore;
		for (i = 0; i < Costanti.NUMSTRADE; i++) {
    		nodiStrade[i] = new JButton();
    		// determina la posizione e la grandezza dei bottoni all'interno della mappa
    		nodiStrade[i].setBounds(XmlParser.parseXmlCoord(i, "coordX", "CoordStrade.xml", "strada"), XmlParser.parseXmlCoord(i, "coordY", "CoordStrade.xml", "strada"),18, 18);  
    		// bottoni trasparenti
    		nodiStrade[i].setContentAreaFilled(false);
    		// i bottoni sono disabilitati alla creazione del frame
    		nodiStrade[i].setEnabled(false);
    		// i bottoni non hanno bordo in evidenza
    		nodiStrade[i].setBorderPainted(false);
    		// viene aggiunta un'azione che si attiva al click del bottone
    		nodiStrade[i].addActionListener(new ActionListener(){
    			private int x = i;
    			/**
    			 * azione che, al click del JButton nodiStrade, permette di 
    			 * spostare il pastore sulla strada che è stata cliccata
    			 */
				public void actionPerformed(ActionEvent e){   
	    			if (game == null) {
						try {
							// viene posizionato il pastore sulla mappa
							out.writeObject(new Args(Costanti.SPOSTAPASTORE, x, f.getPastoreControllato()));
						} catch (IOException e1) {
							LOGGER.log(Level.CONFIG, "Logging an IO error", e1);
						}
					} else {
						try {
							// viene eseguita la mossa "sposta pastore"
							game.inviaMossa(new Args(Costanti.SPOSTAPASTORE, x, f.getPastoreControllato()));
						} catch (RemoteException e1) {
							LOGGER.log(Level.CONFIG, "Logging a remote error", e1);
						} catch (InterruptedException e1) {
							LOGGER.log(Level.CONFIG, "Logging an interrupted error", e1);
						}
					}
	    		}
			});
		}		
	}
	
	/**
	 * Metodo che restituisce l'i-esimo JButton delle strade
	 * @param index , cioè l'indice della strada che si vuole considerare
	 * @return il JBuotton della strada corrispondente all'indice index
	 */
	protected JButton getStradeButton(int index){
		return nodiStrade[index];
	}
	
	/**
	 * Metodo che restituisce un elemento di classe PastoriIMmages 
	 * @param x , cioè l'indice dell'elemento che interessa
	 * @return
	 */
	protected PastoriImages getPastoreButton(int x){ //restituisce la un generico elemento della classe PastoriImages
		return pastore[x];
	}
}
