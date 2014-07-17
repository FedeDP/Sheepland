package UI;

import java.awt.Color;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.xml.parsers.ParserConfigurationException;

import mappa.Costanti;
import net.PartitaInterface;

import org.xml.sax.SAXException;

import elementi_utente.Partita;

/**
 *Il JFrame principale. Contiene  4 JPanel:
 *1)panelPlayer, che contiene le immagini dei giocatori;
 *2)panelTerritori, che contiene i JButton dei territori;
 *3)panelMap, che contiene il la mappa del gioco;
 *4)mosse, che contiene i bottoni con le mosse possibili nel gioco;    
 * @author Matteo
 */
public class MainFrame extends JFrame {
	// la partita che si sta giocando
	protected Partita partita;
	// il turno di gioco
	protected final int turno;
	protected static final long serialVersionUID = 1L;
	// il pannello globale dei giocatori
	protected PannelloPlayerGlobale panelPlayer;
	// il pannello globale dei territori
	protected PannelloTerritoriGlobale panelTerritori;
	// il pannello centrale con la mappa
	protected PannelloMappa panelMap;
	// il pannello delle mosse in alto
	protected PanelMosse mosse; 
	// i pastori dei giocatori
	protected PastoriImages[] pastoreImmagine;
	protected ArrayBlockingQueue<Integer> pastoreControlled = new ArrayBlockingQueue<Integer> (1);
	protected int pastoreControllato;
	protected final PartitaInterface game;
	protected ArrayBlockingQueue<String> string;
	protected int oldPosPecoraNera = 0;
	protected static final Logger LOGGER = Logger.getLogger(MainFrame.class.getName());
	/**
	 * Inizializza il JFrame settando tutti i vari parametri necessari
	 */
	public MainFrame(Partita partita, int turno, ObjectOutputStream out, PartitaInterface game, ArrayBlockingQueue<String> string) throws IOException, SAXException, ParserConfigurationException {
		this.partita = partita;
    	this.turno = turno;
    	this.game = game;
    	this.string = string;
    	// viene creato un pastore per ogni giocatore della partita
    	pastoreImmagine = new PastoriImages[partita.getNumPlayers()];
    	for (int i = 0; i < partita.getNumPlayers(); i++) {
    		pastoreImmagine[i] = new PastoriImages(i, this, pastoreControlled);
    	}
    	// viene inizializzato il pannello dei giocatori
    	panelPlayer = new PannelloPlayerGlobale(partita.getNumPlayers(), partita.getGiocatore(turno).getSoldi());
    	// viene inizializzato il pannello dei territori
    	panelTerritori = new PannelloTerritoriGlobale(out, game);
    	// viene inizializzato il pannello della mappa
    	panelMap = new PannelloMappa(out, pastoreImmagine, game, this, partita.getRecintiRimanenti());
    	// viene inizializzato il pannello delle mosse
    	mosse = new PanelMosse(this);
		// all'inizio della partita i bottoni delle mosse sono disabilitati
    	mosse.disabilitaBottoniMosse();
    	setTitle("Sheepland");
     	setLocationRelativeTo(null);
     	setDefaultCloseOperation(EXIT_ON_CLOSE);
     	setResizable(false);
     	setLayout(null);
     	add(mosse);
     	add(panelPlayer);
     	add(panelMap);
     	add(panelTerritori);
    	pack();
    	setBounds(300,30, 850, 672);
     	setVisible(true);
 	}
	/**
	 * avvia la UI (equivalente di STARTCLI)
	 * @param in
	 * @param match
	 * @param socket
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws InterruptedException
	 */
    public void startUI(ObjectInputStream in, ArrayBlockingQueue <Partita> match, Socket socket, boolean reconnected) throws ClassNotFoundException, IOException, SAXException, ParserConfigurationException, InterruptedException {
    	Timer timer = new Timer();
    	pastoreControllato = 0;
		int[] oldPos = new int[partita.getGiocatore(turno).getNumPastori()];
		int turnoDiGioco = 0;
		boolean ending = false;
		disableEverything();
		// se non ci stiamo riconnettendo, inizializzo i pastori e faccio il primo spostamento casuale della pecora nera.
		if (!reconnected) {
			initPastore(match, in); 
			managePecoraNera(partita.getMap().getPecoraNera().getPosition());
		} else {
			// funzione che aggiorna sul frame dell'utente appena connesso lo stato della partita
			manageReconnection();
		}	
		while (!partita.getFinita() && partita.getActiveNumPlayers() > 1) {
			if (turnoDiGioco != partita.getTurno()) {
				// se abbiamo cambiato turno di gioco, disegno lo spostamento della pecora nera.
				managePecoraNera(partita.getMap().getPecoraNera().getPosition());
			}
			// se sono rimasti 0 recinti, dico a tutti i giocatori che questo è il round finale.
			if (partita.getRecintiRimanenti() == 0 && !ending) {
				finalRound();
				ending = true;
			}
			// turno di gioco di questo ciclo del loop.
			turnoDiGioco = partita.getTurno();
			// disabilito tutto a schermo a tutti i giocatori.
			disableEverything();
			evidenziaGiocatoreAttivo();
			if (partita.getTurno() == turno) { //se è il proprio turno	
				// parte un timer che dopo 60sec avvisa l'utente che è stato kickato, se non ha fatto una mossa.
				timer.schedule(new timerKick(), 60000);
				//se siamo alla prima mossa
				if (partita.getNumMosseTurno() == 0){ 
					mostraTurno();
					// se ci sono due giocatori, rendo visibili e abilito i pulsanti sul pastore, per scegliere quale usare nel round.
					if (partita.getNumPlayers() == 2) {
						pastoreSetEnabled(true);
						pastoreControllato = pastoreControlled.take();
						pastoreSetEnabled(false);
					}
				}
				// solo all'utente attivo, abilito le mosse possibili per questo turno.
				mosse.abilitaBottoniMosse();		
			}
			// salvo la vecchia posizione dei pastori in un array.
			for (int i = 0; i < partita.getGiocatore(turnoDiGioco).getNumPastori(); i++) {
				oldPos[i] = partita.getGiocatore(turnoDiGioco).getPastore(i).getPosizione();
			}
			if (match == null) {
	    		partita = (Partita) in.readObject();
			} else { 
	    		partita = match.take();
			}
			// aggiorno tutta la grafica.
			updateEverything(turnoDiGioco, oldPos);
		}
		// quando esco dal loop, aspetto di ricevere la stringa finale dal server.
		endGame(socket);
	}
    /**
     * Timer per avvisare l'utente dopo 1 minuto di inattività che è stato kickato.
     * @author federico
     *
     */
    private class timerKick extends TimerTask {
		int num = partita.getNumMosseTurno();
		int x = partita.getRecintiRimanenti();
		int turn = partita.getTurno();
		@Override
       	public void run() {
			/* Per capire se l'utente ha fatto mosse, in maniera univoca, ho bisogno di controllare 3 cose:
			 * che il numero dei recinti rimanenti sia rimasto lo stesso.
			 * che il turno di gioco sia lo stesso.
			 * che il numero di mosse nel turno siano le stesse.
			 * (la seconda e la terza da sole non bastano perché sono cicliche, e in 60 secondi
			 * può capitare che si ritorni allo stesso numero di mosse dello stesso utente attivo.
			 * Ma in quel caso sarà per forza, viste le regole del gioco, cambiato il numero di recinti.)
			 */
			if (x == partita.getRecintiRimanenti() && turn == partita.getTurno() && num == partita.getNumMosseTurno()) {
				JOptionPane.showMessageDialog(null, "Timer mossa scaduto, sei stato kickato dal server.");
			}
        }
    }
    
    /**
     * Metodo che dabilita i JButton dei territori che si possono comprare
     * quando il pastore si trova in una determinata posizione
     * @param partita , cioè la partita che si sta giocando
     */
    protected void abilitaTerritori() {
    	int primoTerritorio = partita.getMap().getStrada(partita.getGiocatore(partita.getTurno()).getPastore(pastoreControllato).getPosizione()).getPrimaRegione(partita.getMap()).getTerritorio();
    	int secondoTerritorio = partita.getMap().getStrada(partita.getGiocatore(partita.getTurno()).getPastore(pastoreControllato).getPosizione()).getSecondaRegione(partita.getMap()).getTerritorio();
    	if(primoTerritorio > 0)	{
    		// se il giocatore ha soldi sufficienti e se ci sono ancora tessere territorio di quel tipo, viene abilitato il pannello
    		if (partita.getMap().getCarta(primoTerritorio - 1).getCarteRimaste() != 0 && partita.getMap().getCarta(primoTerritorio - 1).getCosto() <= partita.getGiocatore(partita.getTurno()).getSoldi()) {
        		panelTerritori.panel.getButton(primoTerritorio - 1).setEnabled(true);
    		}
    	}
    	if(secondoTerritorio > 0)	{
    		// se il giocatore ha soldi sufficienti e se ci sono ancora tessere territorio di quel tipo, viene abilitato il pannello
    		if (partita.getMap().getCarta(secondoTerritorio - 1).getCarteRimaste() != 0 && partita.getMap().getCarta(secondoTerritorio - 1).getCosto() <= partita.getGiocatore(partita.getTurno()).getSoldi()) {
        		panelTerritori.panel.getButton(secondoTerritorio - 1).setEnabled(true);
    		}
    	}
    }
    
    /**
     * Metodo che abilita i JButton delle pecore che si possono spostare
     * @param partita , cioè la partita che si sta giocando
     */
    protected void abilitaPecore(){
		final int primaRegione = partita.getMap().getStrada(partita.getGiocatore(partita.getTurno()).getPastore(pastoreControllato).getPosizione()).getIndexPrimaRegione();
		final int secondaRegione = partita.getMap().getStrada(partita.getGiocatore(partita.getTurno()).getPastore(pastoreControllato).getPosizione()).getIndexSecondaRegione();
		int pecorePrimaRegione = partita.getMap().getStrada(partita.getGiocatore(partita.getTurno()).getPastore(pastoreControllato).getPosizione()).getPrimaRegione(partita.getMap()).getPecorePresenti();
		int pecoreSecondaRegione = partita.getMap().getStrada(partita.getGiocatore(partita.getTurno()).getPastore(pastoreControllato).getPosizione()).getSecondaRegione(partita.getMap()).getPecorePresenti();
		if (pecorePrimaRegione != 0) {
			// abilita i bottoni dopo aver controllato che ci siano pecore nella regione
			panelMap.getPecore(primaRegione).setEnabled(true);
			panelMap.getPecore(primaRegione).setBorderPainted(true);
		} 
		if (pecoreSecondaRegione != 0) {
			// abilita i bottoni dopo aver controllato che ci siano pecore nella regione
			panelMap.getPecore(secondaRegione).setEnabled(true);
			panelMap.getPecore(secondaRegione).setBorderPainted(true);
		}
    }
    
    /**
     * Metodo che abilita le strade collegate direttamente a 
     * quella in cui ci si trova se non si hanno soldi sufficienti per spostarsi, viceversa abilita tutte le strade non occupate e non recintate
     * @param partita , cioè la partita che si sta giocando
     */
    protected void abilitaStrade() {
    	for(int i = 0; i < Costanti.NUMSTRADE; i++) {
    		if (partita.getMap().getStrada(partita.getGiocatore(partita.getTurno()).getPastore(pastoreControllato).getPosizione()).getStradeAdiacenti().contains((Integer) i)) {
    			if (!partita.getMap().getStrada(i).getOccupato() && !partita.getMap().getStrada(i).getRecintato()) {
    				// il bottone della strada viene abilitato se la strada non è recintata o occupata
    				panelMap.getBottoniStrade().getStradeButton(i).setEnabled(true);
    				panelMap.getBottoniStrade().getStradeButton(i).setBorderPainted(true);
    			}
    		} else {
    			if (!partita.getMap().getStrada(i).getOccupato() && !partita.getMap().getStrada(i).getRecintato() && partita.getGiocatore(partita.getTurno()).getSoldi() != 0) {
    				// il bottone di una strada non direttamente collegata a quella in cui si trova il pastore viene attivato solo se il giocatore ha soldi
    				panelMap.getBottoniStrade().getStradeButton(i).setEnabled(true);
    				panelMap.getBottoniStrade().getStradeButton(i).setBorderPainted(true);
    			}
    		}	
    	}
    }
    /**
     * Printa i pastori sulla mappa
     * @param oldPos : vecchia pos del pastore
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws InterruptedException 
     */	
    protected void printPastoreMappa(int oldPos, int pastore, int turnoDiGioco) throws SAXException, IOException, ParserConfigurationException, InterruptedException {
    	final int newPos = partita.getGiocatore(turnoDiGioco).getPastore(pastore).getPosizione();
    	// rimuovo il pastore dalla vecchia posizione.
    	panelMap.remove(panelMap.getBottoniStrade().getPastoreButton(turnoDiGioco).getPastore(oldPos)); 
    	JLabel labelRecinti = new JLabel(new ImageIcon("recintopiccolo.png"));    	
    	// aggiungo nella nuova posizione il pastore.
    	panelMap.add(pastoreImmagine[turnoDiGioco].getPastore(newPos));
        panelMap.getBottoniStrade().getStradeButton(newPos).setBorderPainted(false);
        // aggiungo sulla vecchia posizione, il recinto.
    	panelMap.add(labelRecinti);
    	labelRecinti.setBounds(panelMap.getBottoniStrade().getStradeButton(oldPos).getX()-7, panelMap.getBottoniStrade().getStradeButton(oldPos).getY()-7, 30,30);
    	panelMap.getBottoniStrade().getStradeButton(oldPos).setBorderPainted(false);
    }
    /**
     * disabilita tutti i button
     */
    protected void disableEverything() {
    	// vengono disabilitati i bottoni delle mosse in alto
    	mosse.disabilitaBottoniMosse();
    	// vengono disabilitati i bottoni dei territori
    	for(int i=0;i<Costanti.NUMTERRITORI;i++) {
    		panelTerritori.panel.getButton(i).setEnabled(false);
    	}
    	// vengono disabilitati i bottoni delle pecore
		for(int i=0; i<Costanti.NUMPECORE;i++) {
			panelMap.getPecore(i).setEnabled(false);
			panelMap.getPecore(i).setBorderPainted(false);
		}
		// vengono disabilitati i bottoni delle strade
		for(int i=0;i<Costanti.NUMSTRADE;i++) {
			panelMap.getBottoniStrade().getStradeButton(i).setEnabled(false);	
			panelMap.getBottoniStrade().getStradeButton(i).setBorderPainted(false);	
		}
    }
    /**
     * Genera un pallino sull'immagine del giocatore, del colore del giocatore, per far
     * capire chi sia quello attivo
     */
    protected void evidenziaGiocatoreAttivo() {
    	for (int i = 0; i < partita.getNumPlayers(); i++) {
    		if (i != partita.getTurno()) {
    			// se è il turno del giocatore viene visualizzato il pallino
    			panelPlayer.getPannello(i).setIndicatoreTurnoVisibility(false);
    		} else { 
    			panelPlayer.getPannello(i).setIndicatoreTurnoVisibility(true);
    		}
    	}
    	repaint();
    }
    /**
     * abilita/disabilita il bottone sul pastore
     * @param x
     */
    protected void pastoreSetEnabled(boolean x) {
    	for (int j = 0; j < partita.getGiocatore(turno).getNumPastori(); j++) {
    		pastoreImmagine[turno].getPastore(partita.getGiocatore(turno).getPastore(j).getPosizione()).setEnabled(x);
    		pastoreImmagine[turno].getPastore(partita.getGiocatore(turno).getPastore(j).getPosizione()).setBorderPainted(x);
		}
    }
    /**
     * Ci dice quando è il nostro turno
     */
    protected void mostraTurno(){
    	JOptionPane.showMessageDialog(null,"è il tuo turno");
    	if (partita.getNumPlayers() == 2) {
    		JOptionPane.showMessageDialog(null,"Clicca su un pastore per scegliere di utilizzarlo per questo turno.");
    	}
    }
    
    /**
     * Metodo che mostra il messaggio che invita il giocatore
     * a inizializzare il pastore in una posizione, e ce lo inizializza 
     * @param partita , cioè la partita che si sta giocando
     * @throws IOException 
     * @throws ClassNotFoundException 
     * @throws InterruptedException 
     */
    protected void initPastore(ArrayBlockingQueue <Partita> match, ObjectInputStream in) throws ClassNotFoundException, IOException, InterruptedException{ //mostra il box a inizio partita
    	switch (turno) {
    	case 0:
    		JOptionPane.showMessageDialog(null, "Sarai il giocatore rosso");
    		break;
    	case 1: 
    		JOptionPane.showMessageDialog(null, "Sarai il giocatore verde");
    		break;
    	case 2:
    		JOptionPane.showMessageDialog(null, "Sarai il giocatore giallo");
    		break;
    	case 3:
    		JOptionPane.showMessageDialog(null, "Sarai il giocatore blu");
    		break;
    	}
    	for(int i = 0; i < partita.getNumPlayers(); i++) {
			for(int j = 0; j < partita.getGiocatore(i).getNumPastori(); j++) {
				if (i == turno) {
	    			pastoreInit();
				}
				if (match == null) {
					partita = (Partita) in.readObject();
				} else { 
					partita = match.take();
				}
				disableEverything();
				panelMap.add(pastoreImmagine[i].getPastore(partita.getGiocatore(i).getPastore(j).getPosizione()));
				panelMap.getBottoniStrade().getStradeButton(partita.getGiocatore(i).getPastore(j).getPosizione()).setBorderPainted(false);
				repaint();
			}
    	}
    }
    /**
     * Metodo che ci invita a cliccare su una strada per inizializzare il pastore, dopodichè abilita tutte le strade non occupate
     */
    protected void pastoreInit() {
    	JOptionPane.showMessageDialog(null, "Inizializza il tuo pastore cliccando su una strada.");
		abilitaStrade();
    }
    /**
     * ritorna il pastore Controllato dall'utente, nel caso ce ne sia più d'uno
     * @return
     */
    protected int getPastoreControllato() {
    	return pastoreControllato;
    }
    /**
     * Ritorna la posizione del primo pastore.
     * @return
     */
    protected int getPosPrimoPastore() {
    	return partita.getGiocatore(partita.getTurno()).getPastore(0).getPosizione();
    }
    /**
     * Quando esauriamo i 20 recinti, ci avvisa che siamo entrati nella fase finale
     */
    protected void finalRound() {
    	JOptionPane.showMessageDialog(null, "Recinti terminati! Ultimo round di gioco!");
    }
    /**
     * Riceverà dal server una stringa che ci dirà se abbiamo vinto o perso
     * @param socket
     * @throws IOException
     * @throws InterruptedException
     */
    protected void endGame(Socket socket) throws IOException, InterruptedException {
    	if (partita.getActiveNumPlayers() == 1) {
    		JOptionPane.showMessageDialog(null, "Partita terminata: sei rimasto solo tu.");
    	} else {
    		if (game == null) {
    			Scanner scanner = new Scanner(socket.getInputStream());
    			String end = scanner.nextLine();
    			JOptionPane.showMessageDialog(null, end);
    			scanner.close();
    		} else {
    			JOptionPane.showMessageDialog(null, string.take());
    		}
    	}
    }
    /**
     * Dopo che l'utente attivo ha fatto la mossa, e ha ricevuto la partita,
     * questo metodo aggiorna tutte le label necessarie.
     * @param turnoDiGioco
     * @param oldPos
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws InterruptedException 
     */
    protected void updateEverything(int turnoDiGioco, int[] oldPos) throws SAXException, IOException, ParserConfigurationException, InterruptedException {
    	// aggiorno la label dei soldi del giocatore attivo.
    	panelPlayer.getPannello(turnoDiGioco).updateSoldi(partita.getGiocatore(turnoDiGioco).getSoldi());
    	// aggiorno la posizione dei pastori del giocatore attivo.
    	for (int i = 0; i < partita.getGiocatore(turnoDiGioco).getNumPastori(); i++) {
			if (partita.getGiocatore(turnoDiGioco).getPastore(i).getPosizione() != oldPos[i]) {
				try {
					printPastoreMappa(oldPos[i], i, turnoDiGioco);
				} catch (InterruptedException e) {
					LOGGER.log(Level.CONFIG, "Logging an interrupted error", e);
				}
			}
		}
    	// aggiorno la label dei contatori delle pecore nelle regioni.
    	for(int j = 0; j < Costanti.NUMPECORE; j++) {
			panelMap.getPecore().updateLabelNumPecore(j, partita.getMap().getRegione(j).getPecorePresenti());
			managePecoraNera(partita.getMap().getPecoraNera().getPosition());
		}
    	// solo per il giocaotre del turno, aggiorno i possedimenti.
		if (turnoDiGioco == turno) {
			for(int j = 0; j < Costanti.NUMTERRITORI; j++) {
				panelTerritori.getPanel().updateLabel(j, partita.getGiocatore(turnoDiGioco).getPossedimenti().getPossedimenti(j));
			}
		}		
		if (partita.getRecintiRimanenti() > 0) {
			panelMap.getRecinti().updateRecinti(partita.getRecintiRimanenti()); //aggiorna la label dei recinti
		} else {
			panelMap.getRecinti().updateRecinti(0);
		}
	    repaint();	
    }
    /**
     * Gestisce lo spostamento casuale e non della pecora nera, riprintando una pecora nera nella
     * regione in cui si è spostata, e rimettendone una bianca nella vecchia regione.
     * @param newPosPecoraNera
     * @throws InterruptedException
     */
    protected void managePecoraNera(int newPosPecoraNera) throws InterruptedException {
    	// se la pecora nera si è spostata, rimetto una pecora bianca nella vecchia posizione, e ne printo una nera nella nuova.
    	if (newPosPecoraNera != oldPosPecoraNera) {
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
     * Gestisce la riconnessione: appena si riconnette, l'utente deve visualizzare tutte le informazioni della partita a schermo.
     */
    protected void manageReconnection() {
    	for (int i = 0; i < partita.getNumPlayers(); i++) {
    		panelPlayer.getPannello(i).updateSoldi(partita.getGiocatore(i).getSoldi());
    		for (int j = 0; j < partita.getGiocatore(i).getNumPastori(); j++) {
    			panelMap.add(pastoreImmagine[i].getPastore(partita.getGiocatore(i).getPastore(j).getPosizione()));
				panelMap.getBottoniStrade().getStradeButton(partita.getGiocatore(i).getPastore(j).getPosizione()).setBorderPainted(false);
    		}
    		if (i == turno) {
    			for(int j = 0; j < Costanti.NUMTERRITORI; j++) {
    				panelTerritori.getPanel().updateLabel(j, partita.getGiocatore(i).getPossedimenti().getPossedimenti(j));
    			}
    		}
    	}
    	checkStradeRecintate();
    	for(int j = 0; j < Costanti.NUMPECORE; j++) {
			panelMap.getPecore().updateLabelNumPecore(j, partita.getMap().getRegione(j).getPecorePresenti());
		}			
		if (partita.getRecintiRimanenti() > 0) {
			panelMap.getRecinti().updateRecinti(partita.getRecintiRimanenti()); //aggiorna la label dei recinti
		} else {
			panelMap.getRecinti().updateRecinti(0);
		}
	    repaint();	
    }
    /**
     * Quando un utente si riconnette, metto tutti i recinti al loro posto sul suo frame.
     */
    protected void checkStradeRecintate() { 
    	for (int i = 0; i < Costanti.NUMSTRADE; i++) {
    		if (partita.getMap().getStrada(i).getRecintato()) {
    			JLabel labelRecinti = new JLabel(new ImageIcon("recintopiccolo.png"));
    			panelMap.add(labelRecinti);
    			labelRecinti.setBounds(panelMap.getBottoniStrade().getStradeButton(i).getX()-7, panelMap.getBottoniStrade().getStradeButton(i).getY()-7, 30,30);
    		}
    	}
    }
}
