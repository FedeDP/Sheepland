 package UI;
 
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.xml.parsers.ParserConfigurationException;

import mappa.Costanti;
import net.PartitaInterface;

import org.xml.sax.SAXException;

/**
 * JPanel che contiene l'immagine della mappa, i JButton delle strade e
 * i JButton delle pecore
 * @author Matteo
 *
 */
 public class PannelloMappa extends JPanel {
 	private static final long serialVersionUID = 1L;
 	// viene caricata l'immagine della mappa del gioco
 	private BufferedImage image = ImageIO.read(new File("sheepland.jpg"));
 	// i bottoni delle strade
 	private final BottoniStrade buttonStrada;
	// le immagini delle pecore che appariranno sulla mappa
 	private	PecoreImages pecore;
	// il pannello dove compare la label che conta i territori
 	private PannelloContRecinti contRecinti;
	/**
	 * Inizializza il JPanel aggiungendo i bottoni sulle strade, le pecore nelle
	 * regioni. Come sfondo del pannello è impostata l'immagine della mappa e
	 * i bottoni delle pecore sono disabilitati
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
 	public PannelloMappa(ObjectOutputStream out, PastoriImages[] pastore, PartitaInterface game, MainFrame f, int recinti) throws IOException, SAXException, ParserConfigurationException {		 		
 		// inizializzo i bottoni delle strade e le immagini delle pecore
 		buttonStrada = new BottoniStrade(f, out, pastore, game);
 		pecore = new PecoreImages(f, out, game);
 		this.setLayout(null);
 		//ridimensiono il pannello in modo che ci stia la mappa
 		this.setBounds(189,30, 473, 617);
 		//aggiungo le pecore alla mappa 
 		for(int i = 0; i < Costanti.NUMPECORE; i++){ 
 			add(pecore.getLabelNumPecore(i));
 			add(pecore.getPecoreImages(i));
 			pecore.getPecoreImages(i).setEnabled(false);
 		}
 		// inizializzo e aggiungo il pannello del contatore dei recinti alla mappa
 		contRecinti = new PannelloContRecinti(recinti); 
 		contRecinti.setBounds(20, 500, 70, 100);
 		add(contRecinti);
 		//aggiungo le strade alla mappa
 		for(int i = 0; i < Costanti.NUMSTRADE; i++) { 		
 			add(buttonStrada.getStradeButton(i));
 			buttonStrada.getStradeButton(i).setEnabled(false);
 		}
 	}
 	
 	/**
 	 * Metodo che restituisce l'intero JPanel
 	 * @return il PannelloMappa
 	 */
 	protected JPanel getPanel(){ 
		return this;
	}
 	
 	// metto come sfondo del pannello l'immagine della mappa
 	 protected void paintComponent(Graphics g) {
 	    setOpaque(false);
 	    g.drawImage(image, 0, 0, getWidth(),getHeight(), null);
 	    super.paintComponent(g);
 	 }
 	 
 	 /**
 	  * Metodo che restituisce un elemento della classe BottoniStrade
 	  * @return un elemento della classe BottoniStrade
 	  */
 	 protected BottoniStrade getBottoniStrade() {
 		 return buttonStrada;
 	 }
 	 
 	 /**
 	  * Metodo che restituisce un singolo bottone della pecora
 	  * @param index , cioè l'indice della strada che si vuole considerare
 	  * @return il JBuotton della pecora corrispondente all'indice index
 	  */
 	 protected JButton getPecore(int index){
 		 return pecore.getPecoreImages(index);
 	 }
 	 
 	/**
 	 * Metodo che restituisce un elemento di classe PecoreImages
 	 * @return  un bottone pecora
 	 */
 	 protected PecoreImages getPecore(){
 		 return pecore;
 	 }
 	 
 	 /**
 	  * Metodo che restituisce un elemento di classe PannelloContRecinti
 	  * @return il pannello con la label che conta i recinti
 	  */
 	 protected PannelloContRecinti getRecinti(){
 		 return contRecinti;
 	 }
 }

