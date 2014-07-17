package UI;

import java.awt.Color;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.xml.parsers.ParserConfigurationException;

import net.PartitaInterface;

import org.xml.sax.SAXException;

import elementi_utente.Partita;

/**
 * Classe che rappresenta il frame in cui la pecora nera e i pastori si muovono
 * dinamicamente (estende MainFrame)
 * @author Matteo
 */
public class DynamicFrame extends MainFrame {
	private static final long serialVersionUID = 1L;

	/**
	 * Inizializzazione del DynamicFrame, attraverso tutti i parametri passati
	 * @param partita
	 * @param turno
	 * @param out
	 * @param game
	 * @param string
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	public DynamicFrame(Partita partita, int turno, ObjectOutputStream out, PartitaInterface game, ArrayBlockingQueue<String> string) throws IOException, SAXException, ParserConfigurationException {
		super(partita, turno, out, game, string);
	}
	
	/**
	 * Printa il pastore spostandolo dinamicamente dalla posizione di partenza
	 * a quella di arrivo.
	 */
    protected void printPastoreMappa(int oldPos, int pastore, int turnoDiGioco) throws SAXException, IOException, ParserConfigurationException, InterruptedException {
    	final int newPos = partita.getGiocatore(turnoDiGioco).getPastore(pastore).getPosizione();
    	final int oldX = panelMap.getBottoniStrade().getPastoreButton(turnoDiGioco).getPastore(oldPos).getX();
    	final int oldY = panelMap.getBottoniStrade().getPastoreButton(turnoDiGioco).getPastore(oldPos).getY();
    	final int newX = panelMap.getBottoniStrade().getPastoreButton(turnoDiGioco).getPastore(newPos).getX();
    	final int newY = panelMap.getBottoniStrade().getPastoreButton(turnoDiGioco).getPastore(newPos).getY();
    	panelMap.remove(panelMap.getBottoniStrade().getPastoreButton(turnoDiGioco).getPastore(oldPos)); 
    	final JLabel pastoreImg = new JLabel();
    	pastoreImg.setIcon(new ImageIcon("pastoreplayer"+(turnoDiGioco+1)+".png"));	
    	dynamicMovement(oldX, oldY, newX, newY, pastoreImg);
    	JLabel labelRecinti = new JLabel(new ImageIcon("recintopiccolo.png"));    	
    	panelMap.add(pastoreImmagine[turnoDiGioco].getPastore(newPos));
        panelMap.getBottoniStrade().getStradeButton(newPos).setBorderPainted(false);
    	panelMap.add(labelRecinti);
    	labelRecinti.setBounds(panelMap.getBottoniStrade().getStradeButton(oldPos).getX()-7, panelMap.getBottoniStrade().getStradeButton(oldPos).getY()-7, 30,30);
    	panelMap.getBottoniStrade().getStradeButton(oldPos).setBorderPainted(false);
    }
	/**
	 * Chiamata a fine turno o in ogni caso quando cambia la posizione della pecora nera,
	 * simula un movimento dinamico della pecora stessa.
	 */
    protected void managePecoraNera(int newPosPecoraNera) throws InterruptedException {
    	if (newPosPecoraNera != oldPosPecoraNera) {
    		int oldX = panelMap.getPecore(oldPosPecoraNera).getX();
    		int oldY = panelMap.getPecore(oldPosPecoraNera).getY();
    		int newX = panelMap.getPecore(newPosPecoraNera).getX();
    		int newY = panelMap.getPecore(newPosPecoraNera).getY();
    		final JLabel pecoraImg = new JLabel();
        	pecoraImg.setIcon(new ImageIcon("PecoraNera600.png"));	
    		dynamicMovement(oldX, oldY, newX, newY, pecoraImg);
    		panelMap.getPecore(newPosPecoraNera).setIcon(new ImageIcon("PecoraNera600.png"));
    		panelMap.getPecore(newPosPecoraNera).setDisabledIcon(new ImageIcon("PecoraNera600.png"));
    		panelMap.getPecore().getLabelNumPecore(newPosPecoraNera).setForeground(Color.BLACK);
    		panelMap.getPecore(oldPosPecoraNera).setIcon(new ImageIcon("PecoraBianca600.png"));
    		panelMap.getPecore(oldPosPecoraNera).setDisabledIcon(new ImageIcon("PecoraBianca600.png"));
    		panelMap.getPecore().getLabelNumPecore(oldPosPecoraNera).setForeground(Color.WHITE);
    		panelMap.getPecore().updateLabelNumPecore(oldPosPecoraNera, partita.getMap().getRegione(oldPosPecoraNera).getPecorePresenti());
    		panelMap.getPecore().updateLabelNumPecore(newPosPecoraNera, partita.getMap().getRegione(newPosPecoraNera).getPecorePresenti());
    		oldPosPecoraNera = newPosPecoraNera;
    	}
    }
	/**
	 * Metodo che descrive il movimento dinamico.
	 * Calcolo la distanza tra i due punti, e in base a quello decido quante volte
	 * ridisegnare l'immagine in una nuova posizione (quanti punti della retta prendere in considerazione).
	 * Quindi controllo che non ci troviamo su una retta verticale, e divido i 2 casi.
	 * Nel caso normale, calcolo "m" e "q" come da algebra di prima superiore, e individuo l'incremento come
	 * la distanze sull'asse x diviso il numero di "passi".
	 * Altrimenti l'incremento sarà semplicemente la distanza tra le y diviso il numero di passi.
	 * Poi nel for, ad ogni ciclo incremento la x (o la y, nel secondo caso) e sposto l'immagine
	 * nella nuova posizione.
	 * Quindi attendo 600ms / numero di passi (tutta l'animazione deve durare 600ms).
	 * @param oldX
	 * @param oldY
	 * @param newX
	 * @param newY
	 * @param img
	 * @throws InterruptedException
	 */
    protected void dynamicMovement(final float oldX, final float oldY, final float newX, final float newY, final JLabel img) throws InterruptedException {
    	final float dist = (float) Math.sqrt((float) Math.pow(newX - oldX, 2) +  (float) Math.pow(newY - oldY, 2));
    	final int passi = Math.round(dist / 2);
    	final float inc;
    	float m = 0;
    	float q = 0;
    	float x = oldX, y = oldY;
    	if (newX - oldX != 0) {
    		m = (newY - oldY) / (newX - oldX);
    		q = (m * (0 - oldX)) + oldY;
    		inc = (newX - oldX) / passi;
    		x = oldX;
    	} else {
    		inc = (newY - oldY) / passi;
    		y = oldY;
    	}
    	for (int i = 0; i < passi - 2; i++) {
    		if (newX - oldX != 0) {
    			x = x + inc;
    			img.setBounds(Math.round(x), Math.round((m * x) + q), 30, 50); 
    		} else {
    			y = y + inc;
    			img.setBounds(Math.round(oldX), Math.round(y), 30, 50); 
    		}
    		panelMap.add(img);
    		repaint();
    		TimeUnit.MILLISECONDS.sleep(600/passi); //l'animazione deve durare 6 decimi
    	}
    	panelMap.remove(img);
    }

}
