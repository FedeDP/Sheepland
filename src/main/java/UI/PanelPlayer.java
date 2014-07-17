package UI;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
/**
 * JPanel che rappresenta un giocatore
 * @author Matteo
 *
 */
public class PanelPlayer extends JPanel {
 	private static final long serialVersionUID = 1L;
 	// viene caricata l'immagine del giocatore
 	private BufferedImage image;
 	// label che indica i soldi del giocatore
 	private JLabel contSoldi;
 	// label che compare quando è il turno di un giocatore
 	private JLabel indicatoreTurno;
 
 	/**
 	 * Inizializza il pannello mettendo un immagine del giocatore e una label
 	 * che rappresenta i suoi soldi
 	 * @param i ,cioè l'indice del giocatore 
 	 * @throws IOException
 	 */
 	public PanelPlayer(int i, int soldi)throws IOException{
 		// viene associata un'immagine differente ad ogni giocatore
 		image = ImageIO.read(new File("immagineplayer" + i + ".png"));
 		// viene settato a null il layout del pannello
 		this.setLayout(null);
 		// viene inizializzata la label in cui compaiono i soldi del giocatore
 		contSoldi = new JLabel(soldi+"$");
 		// viene inizilizzata la label che compare quando è il turno del giocatore
 		indicatoreTurno = new JLabel(new ImageIcon("pallinoplayer"+ i +".png"));
    	// viene settata la posizione e la dimensione del pannello 
 		indicatoreTurno.setBounds(27, 55, 30,30);
    	// l'indicatore del turno inizialmente non si vede
 		indicatoreTurno.setVisible(false);    	
    	this.add(indicatoreTurno);
    	// vengono settate posizione e dimensioni della label dove compaiono i soldi del player
 		contSoldi.setBounds(10, 8, 25, 25);
 		// modifica la dimensione e il tipo di testo della label
 		contSoldi.setFont(new Font("sansserif",Font.CENTER_BASELINE,12));
 		this.add(contSoldi);
 	}
 	
	protected void paintComponent(Graphics g) {
  	    setOpaque(false);
  	    g.drawImage(image, 0, 0, getWidth(),getHeight(), null);
  	    super.paintComponent(g);
  	 }
	
	/**
	 * Metodo che aggiorna la JLabel che rappresenta i soldi di un giocatore
	 * @param soldi , cioè il valore che deve assumere il testo della JLabel
	 */
	protected void updateSoldi(int soldi){
		contSoldi.setText(soldi+"$");
	}
	
	/**
	 * Metodo che rende visibile l'indicatore di del turno del giocatore
	 * @param x , cioè il parametro che, se settato a true, fa sì che il metodo
	 * setVisible visualizzi la JLabel
	 */
	protected void setIndicatoreTurnoVisibility(boolean x) {
		indicatoreTurno.setVisible(x);
	}
 }


