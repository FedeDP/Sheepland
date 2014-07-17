package UI;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;




import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * JPanel che contiene la JLabel che conta i recinti durante il gioco
 * @author Matteo
 *
 */
public class PannelloContRecinti extends JPanel {
 	private static final long serialVersionUID = 1L;
 	// viene caricata l'immagine dei recinti
 	private BufferedImage image = ImageIO.read(new File("iconarecinti.png"));
 	// label dove compare il numero di recinti
 	private JLabel contRecinti;
 	
 	/**
 	 * Crea la JLabel che contiene il numero dei recinti, il quale decresce 
 	 * progressivamente ogni volta che un pastore si sposta 
 	 * @throws IOException
 	 */
 	public PannelloContRecinti(int recinti)throws IOException{
 		// setta a null il layout del pannello
 		this.setLayout(null);
 		// inizializza la label con il numero dei recinti
 		contRecinti = new JLabel(recinti + "");
 		//ridimensiona la grandezza del testo
 		contRecinti.setFont(new Font("monospaced", Font.BOLD, 12));
 		// setta posizione e dimensioni del pannello
 		contRecinti.setBounds(26, 61, 18, 18);
 		this.add(contRecinti);
 	}
 	
 	// imposta l'immagine caricata all'inizio come sfondo di questo pannello
 	protected void paintComponent(Graphics g) {
  	    setOpaque(false);
  	    g.drawImage(image, 0, 0, getWidth(),getHeight(), null);
  	    super.paintComponent(g);
  	 }
 	
 	/**
 	 * Metodo che aggiorna la JLabel con il numero dei recinti
 	 * @param recinti , cioè il numero dei recinti rimasti
 	 */
 	protected void updateRecinti(int recinti){
		contRecinti.setText(recinti + "");
	}
}
