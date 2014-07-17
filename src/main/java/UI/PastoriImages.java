package UI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import mappa.Costanti;
import mappa.XmlParser;

/**
 * Classe in cui vengono creati i bottoni dei pastori
 * @author Matteo
 *
 */
public class PastoriImages {
	// i bottoni dei pastori
	private JButton[] buttonPastore = new JButton[Costanti.NUMSTRADE];
	private int i;
	private static final Logger LOGGER = Logger.getLogger(PastoriImages.class.getName());

	/**
	 * Inizializza i bottoni dei pastori e aggiunge l'immagine 
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public PastoriImages(int j, final MainFrame f, final ArrayBlockingQueue<Integer> pastoreControllato) throws SAXException, IOException, ParserConfigurationException {
		for (i = 0; i < Costanti.NUMSTRADE; i++) {
			// viene settata l'immagine del bottone
			buttonPastore[i] = new JButton(new ImageIcon("pastoreplayer"+(j+1)+".png"));
			// viene settata l'immagine del bottone del pastore disabilitato
			buttonPastore[i].setDisabledIcon(new ImageIcon("pastoreplayer"+(j+1)+".png"));
    		// vengono settate posizioni e dimensioni del pastore sulla mappa
			buttonPastore[i].setBounds(XmlParser.parseXmlCoord(i, "coordX", "CoordPastori.xml", "strada"), XmlParser.parseXmlCoord(i, "coordY", "CoordPastori.xml", "strada"), 30, 50);
    		// il bottone viene reso trasparente
			buttonPastore[i].setContentAreaFilled(false);
    		// all'inizio i bottoni sono disabilitati; solo in caso di partita a due giocatori è necessario attivarli per scegliere quale pastore usare
    		buttonPastore[i].setEnabled(false);
    		buttonPastore[i].setBorderPainted(false);
    		buttonPastore[i].addActionListener(new ActionListener(){
    			private int x = i;
    			/**
    			 * azione che, al click del JButton bottonPastore, permette di 
    			 * scegliere quale pastore si vuole controllare in caso di partita
    			 * a 2 giocatori
    			 */
    			public void actionPerformed(ActionEvent e){
    				// setta come pastore controllato quello che si è cliccato
    				if (f.getPosPrimoPastore() == x) {
						try {
							pastoreControllato.put(0);
						} catch (InterruptedException e1) {
							LOGGER.log(Level.CONFIG, "Logging an interrupted error", e1);
						}
    				} else {
						try {
							pastoreControllato.put(1);
						} catch (InterruptedException e1) {
							LOGGER.log(Level.CONFIG, "Logging an interrupted error", e1);
						}
    				}
    			}
    		});
		}
	}
	
	/**
	 * Metodo che restituisce un singolo bottone del pastore
	 * @param index , cioè l'indice del pastore che si vuole considerare
	 * @return il JBuotton del pastore corrispondente all'indice index
	 */
	protected JButton getPastore(int index){
		return buttonPastore[index];
	}

}