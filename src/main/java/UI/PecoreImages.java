package UI;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.xml.parsers.ParserConfigurationException;

import mappa.Costanti;
import mappa.XmlParser;
import net.Args;
import net.PartitaInterface;

import org.xml.sax.SAXException;

/**
 * JPanel che contiene i bottoni delle pecore e una JLabel che indica il numero
 * di pecore presenti nella regione
 * @author Matteo
 *
 */
public class PecoreImages extends JPanel {
	private static final long serialVersionUID = 1L;
	// bottone associato che  alla pecora
	private JButton[] buttonPecora = new JButton[Costanti.NUMPECORE];
	// label che rappresenta il numero di pecore in una regione
	private JLabel[] labelNumPecore = new JLabel[Costanti.NUMPECORE];
	private int i;
	private static final Logger LOGGER = Logger.getLogger(PecoreImages.class.getName());
	/**
	 * Inizializza i bottoni delle pecore e le label con il numero
	 * delle pecore presenti in una regione
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	public PecoreImages(final MainFrame f, final ObjectOutputStream out, final PartitaInterface game) throws IOException, SAXException, ParserConfigurationException {	
		this.setOpaque(false);		
		for (i = 0; i < Costanti.NUMPECORE; i++) {
			buttonPecora[i] = new JButton();
			// vengono associate le immagini ai bottoni delle pecore
			if (i == 0) {
				buttonPecora[i].setIcon(new ImageIcon("PecoraNera600.png"));
				// quando la pecora nera si sposta si vede la sua immagine in una regione
				buttonPecora[i].setDisabledIcon(new ImageIcon("PecoraNera600.png"));
			} else {
				buttonPecora[i].setIcon(new ImageIcon("PecoraBianca600.png"));
				buttonPecora[i].setDisabledIcon(new ImageIcon("PecoraBianca600.png"));
			}
			buttonPecora[i].setContentAreaFilled(false);	
			// i bottoni delle pecore sono disabilitati
			buttonPecora[i].setEnabled(false);
			buttonPecora[i].setBorderPainted(false);
			// vengono settati la posizione e le dimensioni dei bottoni delle pecore 
    		buttonPecora[i].setBounds(XmlParser.parseXmlCoord(i, "coordX", "CoordPecore.xml", "regione"), XmlParser.parseXmlCoord(i, "coordY", "CoordPecore.xml", "regione"),38, 31);
    		// viene settato il testo della label che mostra il numero di pecore presenti in una regione 
    		labelNumPecore[i] = new JLabel("1");
    		//vengono settati i colori della label 
    		if (i == 0) {
    			labelNumPecore[i].setForeground(Color.BLACK);
    		} else {
    			labelNumPecore[i].setForeground(Color.WHITE);
    		}
    		// vengono settate le dimensioni e la posizione della label
    		labelNumPecore[i].setBounds((buttonPecora[i].getX())+10, buttonPecora[i].getY(), 38, 31);
    		buttonPecora[i].addActionListener(new ActionListener(){
    			private int x = i;
    			/**
    			 * azione che, al click del JButton buttonPecora, permette,se possibile
    			 * di diminuire il numero delle pecore presenti in una regione e aumentare
    			 * il numero di quelle nella regione adiacente
    			 */
				public void actionPerformed(ActionEvent e){
				//azione che permette al giocatore di spostare una pecora 
					if (game == null) {
						try {
							out.writeObject(new Args(Costanti.SPOSTAPECORA, x, f.getPastoreControllato()));
						} catch (IOException e1) {
							LOGGER.log(Level.CONFIG, "Logging an IO error", e1);
						}
					} else {
						try {
							game.inviaMossa(new Args(Costanti.SPOSTAPECORA, x, f.getPastoreControllato()));
						} catch (RemoteException e1) {
							LOGGER.log(Level.CONFIG, "Logging an interrupted error", e1);
						} catch (InterruptedException e1) {
							LOGGER.log(Level.CONFIG, "Logging an interrupted error", e1);
						}
					}
	    		}
			});	
    	}
	}
	
	/**
	 * Metodo che restituisce un singolo bottone della pecora
	 * @param index , cioè l'indice della pecora che si vuole considerare
	 * @return il JBuotton della pecora corrispondente all'indice index
	 */
	protected JButton getPecoreImages(int index){
		return buttonPecora[index];
	}
	
	/**
	 * Metodo che restituisce una singola label del numero di pecore
	 * @param index , cioè l'indice della label che si vuole considerare
	 * @return il JBuotton della label corrispondente all'indice index
	 */
	protected JLabel getLabelNumPecore(int index){
		return labelNumPecore[index];
	}
	
	/**
	 * Metodo che aggiorna la JLabel che conta le pecore presenti in una regione
	 * @param index , cioè l'indice della pecora della quale si vuole modificare
	 * la label
	 * @param num , cioè il nuovo numero di pecore presenti in una regione
	 */
	protected void updateLabelNumPecore(int index, int num){
		labelNumPecore[index].setText(num + "");
	}
}
