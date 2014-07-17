package UI;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.xml.parsers.ParserConfigurationException;

import mappa.Costanti;

import org.xml.sax.SAXException;

/**
 * JPanel che contiene i 4 bottoni che corrispondono alle mosse del gioco:
 * 1) Spostare il pastore;
 * 2) Spostare una pecora;
 * 3) Comprare un territorio;
 * 4) Cambiare mossa;
 * @author Matteo
 */
public class PanelMosse extends JPanel {
 	private static final long serialVersionUID = 1L;
 	// bottone che permette di eseguire la mossa "Sposta Pastore"
 	private JButton bottone = new JButton("Sposta Pastore");
 	// bottone che permette di eseguire la mossa "Sposta Pecora"
	private JButton bottone2 = new JButton("Sposta Pecora");
 	// bottone che permette di eseguire la mossa "Compra Territorio"
	private JButton bottone3 = new JButton("Compra Territorio");
 	// bottone che permette di cambiare l'ultima mossa effettuata prima di completarla
 	private JButton bottone4 = new JButton("Cambia mossa");
 	// indica il pastore spostato
 	private boolean pastoreMosso;
 	// indica il numero delle mosse
 	private int numMosse = 0;
 	// serve per controllare qual è l'ultima mossa fatta
 	private int[] mossaPrecedente = new int[3];
	
 	/**
 	 * Inizializza i JButton, li aggiunge al JPanel e assegna ad ognuno un evento
 	 * @param f , cioè il frame principale
 	 * @throws IOException
 	 * @throws SAXException
 	 * @throws ParserConfigurationException
 	 */
 	public PanelMosse(final MainFrame f) throws IOException, SAXException, ParserConfigurationException{
 		// setta il layout del Pannello mosse; 1 riga, 4 colonne
 		this.setLayout(new GridLayout(1,4));
 		// setta posizione e dimensioni del pannello nel frame
 		this.setBounds(0, 0, 850, 30);
 		// si aggiungono i bottoni
 		this.add(bottone);
 		this.add(bottone2);
 		this.add(bottone3);
 		this.add(bottone4);
 				
 		bottone.addActionListener(new ActionListener(){
 			/**
 			 * azione che, al click del JButton bottone, abilita tutti i 
 			 * bottoni delle strade e disabilita tutti gli altri presenti nel 
 			 * frame
 			 */
 			public void actionPerformed(ActionEvent e){
 				f.abilitaStrade();
 				disabilitaBottoniMosse();
 				bottone4.setEnabled(true);
 				numMosse++;
 				if (numMosse == 3) {
 					numMosse = 0;
 					mossaPrecedente[numMosse] = 0;
 				} else {
 					mossaPrecedente[numMosse] = Costanti.SPOSTAPASTORE;
 				}
    		}
 		});
 		
 		bottone2.addActionListener(new ActionListener(){
 			/**
 			 * azione che, al click del JButton bottone2, abilita i 
 			 * bottoni delle pecore che si possono comprare
 			 * e disabilita tutti gli altri presenti nel frame
 			 */
 			public void actionPerformed(ActionEvent e){
 				f.abilitaPecore();
 				disabilitaBottoniMosse();
 				bottone4.setEnabled(true);
 				numMosse++;
 				if (numMosse == 3) {
 					numMosse = 0;
 					mossaPrecedente[numMosse] = 0;
 				} else {
 					mossaPrecedente[numMosse] = Costanti.SPOSTAPECORA;
 				}
 			}
 		});
 		
 		bottone3.addActionListener(new ActionListener(){
 			/**
 			 * azione che, al click del JButton bottone3, abilita i 
 			 * bottoni dei territori che si possono comprare
 			 * e disabilita tutti gli altri presenti nel frame
 			 */
 			public void actionPerformed(ActionEvent e){
 				f.abilitaTerritori();
 				disabilitaBottoniMosse();
 				bottone4.setEnabled(true);
 				numMosse++;
 				if (numMosse == 3) {
 					numMosse = 0;
 					mossaPrecedente[numMosse] = 0;
 				} else {
 					mossaPrecedente[numMosse] = Costanti.COMPRATERRITORIO;
 				}
 			}
 		});
 		
 		bottone4.addActionListener(new ActionListener(){
 			/**
 			 * azione che, al click del JButton bottone, abilita tutti i 
 			 * bottoni delle strade e disabilita tutti gli altri presenti nel 
 			 * frame
 			 */
 			public void actionPerformed(ActionEvent e){
 				f.disableEverything();
 				if(numMosse != 0) {
 					numMosse--;
 				} else { 
 					numMosse = 2;
 				}
 				bottone4.setEnabled(false);
 				abilitaBottoniMosse();
 			}
 		});
 	}

 	/**
 	 * Metodo che disabilita tutti i bottoni delle mosse
 	 */
 	protected void disabilitaBottoniMosse(){ 
		bottone.setEnabled(false);
		bottone2.setEnabled(false);
		bottone3.setEnabled(false);
		bottone4.setEnabled(false);
	}
 	
 	/**
 	 * Metodo che abilita i bottoni delle mosse dopo aver controllato quali di 
 	 * esse è possibile fare in base alle regole del gioco
 	 */
 	protected void abilitaBottoniMosse(){ 
		bottone.setEnabled(true);
		if (numMosse == 0) {
			pastoreMosso = false;
		}
		switch (mossaPrecedente[numMosse]) {
		// caso in cui si sta eseguendo la mossa "sposta pastore"
		case Costanti.SPOSTAPASTORE:
			pastoreMosso = true;
			bottone2.setEnabled(true);
			bottone3.setEnabled(true);
			break;
		// caso in cui si sta eseguendo la mossa "sposta pecora"	
		case Costanti.SPOSTAPECORA:
			bottone2.setEnabled(false);
			bottone3.setEnabled(true);
			break;
		// caso in cui si sta eseguendo la mossa "compra territorio"	
		case Costanti.COMPRATERRITORIO:
			bottone2.setEnabled(true);
			bottone3.setEnabled(false);
			break;
		default:
			bottone2.setEnabled(true);
			bottone3.setEnabled(true);
			break;
		}
		// controllo che obbliga il giocatore ad eseguire "sposta pastore" almeno una volta per turno
		if (numMosse == 2 && !pastoreMosso) {
			bottone2.setEnabled(false);
			bottone3.setEnabled(false);
		}
	}
}
