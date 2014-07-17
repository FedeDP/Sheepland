package UI;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import mappa.Costanti;
import net.Args;
import net.PartitaInterface;

import org.xml.sax.SAXException;

import elementi_utente.Partita;
/**
 * Classe che avvia l'interfaccia da linea di comando.
 * @author federico
 *
 */
public class CLI {
	private final int turno;
	private Partita partita;
	private final ObjectOutputStream out;
	private final ObjectInputStream in;
	private final Scanner stdin;
	private final PartitaInterface game;
	private ArrayBlockingQueue <Partita> match;
	private ArrayBlockingQueue <String> string;
	private final Socket socket;
	private static final Logger LOGGER = Logger.getLogger(CLI.class.getName());
	/**
	 * Al costruttore passo tutto ciò di cui hanno bisogno le interfacce, rispettivamente
	 * lato socket e lato rmi.
	 * @param turno
	 * @param partita
	 * @param out
	 * @param in
	 * @param game
	 * @param match
	 * @param string
	 * @param socket
	 * @param stdin
	 */
	public CLI(final int turno, Partita partita, final ObjectOutputStream out, final ObjectInputStream in, final PartitaInterface game, ArrayBlockingQueue <Partita> match, ArrayBlockingQueue <String> string, final Socket socket, final Scanner stdin) {
		this.turno = turno;
		this.partita = partita;
		this.out = out;
		this.in = in;
		this.game = game;
		this.match = match;
		this.string = string;
		this.socket = socket;
		this.stdin = stdin;
	}
	/**
	 * Inizializza l'interfaccia da linea di comando.
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws ParserConfigurationException 
	 * @throws SAXException 
	 */
	public void startCLI(boolean reconnected) throws IOException, ClassNotFoundException, SAXException, ParserConfigurationException, InterruptedException {
		int mossaPrecedente = 0;
		boolean pastoreMosso = false;
		int pastoreControllato = 0;
		checkPastoreInitNeeded(reconnected);
		try {
			while (!partita.getFinita() && partita.getActiveNumPlayers() > 1) {
				//se è il proprio turno, parte un timer di 60secondi, dopo il quale viene notificato al client l'avvenuto kick.
				if (partita.getTurno() == turno) {	//se è il proprio turno
					Timer timer = new Timer();
					timer.schedule(new timerKick(), 60000);
					// se siamo alla prima mossa del turno, imponiamo queste variabili, e chiediamo all'utente quale pastore utilizzerà.
					if (partita.getNumMosseTurno() == 0) { //se siamo alla prima mossa
						mossaPrecedente = 0;
						pastoreMosso = false;
						pastoreControllato = getPastoreControllato();
					}
					mossaPrecedente = sendMossa(mossaPrecedente, pastoreMosso, pastoreControllato);
					// Una volta inviata la mossa, cancelliamo il timer.
					timer.cancel();
					// se abbiamo mosso il pastore, aggiorniamo la variabile.
					if (mossaPrecedente == Costanti.SPOSTAPASTORE && !pastoreMosso) {
						pastoreMosso = true;
					}
					updatePartita();
					afterMossa(mossaPrecedente, pastoreControllato);
				} else {
					System.out.println("turno del giocatore " + partita.getTurno());
					updatePartita();
				}
			}
			// se siamo usciti perché è terminato il gioco, aspettiamo la stringa finale dal server.
			endGame();
			} catch(NoSuchElementException e) {
				LOGGER.log(Level.INFO, "error, connection closed", e);
			} 
	}
	/**
	 * Aggiorna lo stato della partita.
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void updatePartita() throws ClassNotFoundException, IOException, InterruptedException {
		if (game == null) {
			partita = (Partita) in.readObject();
		} else { 
			partita = match.take();
		}
	}
	/**
	 * Invia la mossa al server.
	 * @param x
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void send(Args x) throws IOException, InterruptedException {
		if (game == null) {
			out.writeObject(x);
			out.flush();
		} else {
			game.inviaMossa(x);
		}
	}
	/**
	 * Metodo che gestisce la fine del gioco.
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void endGame() throws IOException, InterruptedException {
		if (partita.getActiveNumPlayers() > 1) {
			if (game == null) {
				//quando la partita è finita, attendo la stringa finale dal server
				Scanner scanner = new Scanner(socket.getInputStream());
				String end = scanner.nextLine();
				System.out.println(end);
				scanner.close();
			} else {
				System.out.println(string.take());
			}
		} else {
			System.out.println("Partita terminata. Sei rimasto l'unico giocatore.");
		}
	}
	
	/**
	 * Check se il client non si sta riconnettendo (e quindi dovrebbe saltare pastore Init)
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ClassNotFoundException
	 */
	private void checkPastoreInitNeeded(boolean reconnected) throws IOException, InterruptedException, ClassNotFoundException {
		// se il client non si sta riconnettendo, dovrà inizializzare i pastori
		if (!reconnected) {
			for(int i = 0; i < partita.getNumPlayers(); i++) {
				for(int j = 0; j < partita.getGiocatore(i).getNumPastori(); j++) {
					if (i == turno) {
						pastoreInit();
					}
					updatePartita();
				}
			}
			for(int i = 0; i < partita.getGiocatore(turno).getNumPastori(); i++) {
				System.out.println("Pastore " +i + " in posizione " + partita.getGiocatore(turno).getPastore(i).getPosizione());  
			}
		}
	}
	/**
	 * Funzione del timer previsto per avvisare dopo 60 secondi che il client corrente, se non ha fatto mosse, è stato kickato.
	 * @author federico
	 *
	 */
	private class timerKick extends TimerTask {
         @Override
         public void run() {
         	System.out.println("Timer mossa scaduto, sei stato kickato dal server.");
         	stdin.close();
         	if (game == null) {
         		try {
         			in.close();
         			out.close();
         			socket.close();
         		} catch (IOException e) {
         			LOGGER.log(Level.INFO, "Logging an IO", e);
         		}	
         	}
         }
     }
	
	/**
	 * Inizializzo gli n (1 < n < 2) pastori dell'utente.
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void pastoreInit() throws IOException, InterruptedException {
		int inputLine;
		do {
			System.out.println("Scegli una strada da 0 a 41 in cui inizializzare il pastore");
			while (!stdin.hasNextInt()) {
				System.out.println("Immettere un numero.");
				stdin.next();
			}
			inputLine = stdin.nextInt();
			if (inputLine >= 0 && inputLine < 42 && partita.getMap().getStrada(inputLine).getOccupato()) {
				System.out.println("Strada già occupata");
			}
		} while (inputLine < 0 || inputLine > 41 || partita.getMap().getStrada(inputLine).getOccupato());
		send(new Args(Costanti.SPOSTAPASTORE, inputLine, 0));
	}
	/**
	 * A inizio di ogni turno, se il numero di giocatori è 2,
	 * chiedo che pastore vuole controllare il giocatore attivo.
	 * @param stdin : per leggere da tastiera
	 * @return : pastoreControllato (0 o 1)
	 */
	private int getPastoreControllato() {
		int pastoreControllato = 0;
		if (partita.getGiocatore(turno).getNumPastori() == 2) {
			System.out.println("Che pastore vuoi usare? 0 o 1");
			do {
				while (!stdin.hasNextInt()) {
					System.out.println("Immettere un numero.");
					stdin.next();
				}
				pastoreControllato = stdin.nextInt();
			} while (pastoreControllato != 0 && pastoreControllato != 1);
		}
		return pastoreControllato;
	}
	/**
	 * Metodo che invia la mossa scelta al ServerClientHandler (e di conseguenza al GameLoop)
	 * @param out : per inviare al ServerClientHandler
	 * @param stdin : per ricevere input da tastiera
	 * @param mossaPrecedente : intero che identifica l'ultima mossa eseguita dal client,
	 * utilizzata in "CheckMossa" per controllare le mosse possibili.
	 * @param pastoreMosso : Booloeano che, nel turno, identifica se il pastore è stato già mosso o no;
	 * Sempre utile in CheckMossa.
	 * @param pastoreControllato : 0 o 1, il pastore che si è deciso di controllare.
	 * @return ritorna la mossa corrente, che verrà salvata come "mossaPrecedente"
	 * @throws IOException : se qualche processo I/O dà errore
	 * @throws InterruptedException 
	 */
	private int sendMossa(int mossaPrecedente, boolean pastoreMosso, int pastoreControllato) throws IOException, InterruptedException {
		int inputLine = 0;
		int attributoMossa;
		do {
			System.out.println("Seleziona l'azione da compiere 1)Muovi Pastore 2)Muovi Pecora 3)Compra Territorio. Hai 60 secondi da ora per compiere la tua azione.");
			while (!stdin.hasNextInt()) {
				System.out.println("Immettere un numero.");
				stdin.next();
			}
			inputLine = stdin.nextInt();
		} while(inputLine < 1 || inputLine > Costanti.NUMMOSSE || !checkMossa(inputLine, mossaPrecedente, pastoreMosso));
		attributoMossa = manageMosse(inputLine, pastoreControllato);
		if(attributoMossa == -1) {
			return sendMossa(mossaPrecedente, pastoreMosso, pastoreControllato);
		}
		send(new Args(inputLine, attributoMossa, pastoreControllato));
		return inputLine;
	}
	/**
	 * Controlla che la mossa che si vuole fare sia corretta(regole del gioco)
	 * @param mossa : da 1 a 3, la mossa che si ha intenzione di fare
	 * @param mossaPrecedente : l'ultima mossa effettuata. 0 se si è al primo turno del giocatore
	 * @param pastoreMosso : boolean che indica se si è già mosso il pastore. False a inizio di ogni turno
	 * @return true se la mossa è valida, false altrimenti.
	 */
	private boolean checkMossa(int mossa, int mossaPrecedente, boolean pastoreMosso) {
		if (mossa != Costanti.SPOSTAPASTORE) {	//se la mossa non è muoviPastore(che non dà mai problemi)
			if(mossaPrecedente == mossa) { //se si sta cercando di fare 2 volte di fila la stessa mossa
				System.out.println("Non puoi fare due mosse uguali consecutivamente");
				return false;
			}
			if (partita.getNumMosseTurno() == 2 && !pastoreMosso){ //se siamo all'ultima mossa, e non si è ancora mosso il pastore
				System.out.println("Devi muovere il pastore una volta per turno");
				return false;
			}
		}
		return true;
	}
	/**
	 * Una volta che si è scelto che mossa fare, ed essa è valida, si crea
	 * l'attributo secondario della mossa scelta.
	 * @param inputLine : è la mossa che si è scelto di fare
	 * @param pastoreControllato : il pastore che si controlla
	 * @param stdin : per ricevere input da tastiera
	 * @return : l'attributo secondario della mossa (ad esempio la nuova posizione del pastore
	 * @throws IOException
	 */
	private int manageMosse(int inputLine, int pastoreControllato) throws IOException {
		int scelta = 0;
		switch (inputLine) {
		case Costanti.SPOSTAPASTORE:
			System.out.println("Hai scelto di muovere il Pastore. -1 per tornare indietro e cambiare mossa.");
			System.out.println("Puoi muoverti gratuitamente in: " + (partita.getMap().getStrada(partita.getGiocatore(turno).getPastore(pastoreControllato).getPosizione())).getStradeAdiacenti());
			if	(partita.getGiocatore(turno).getSoldi() > 0) {
				System.out.println("Altrimenti ti costerà 1. Dove vuoi andare (da 0 a 41)?");
			}
			do {
				while (!stdin.hasNextInt()) {
					System.out.println("Immettere un numero.");
					stdin.next();
				}
				scelta = stdin.nextInt();
				if (scelta >= 0 && scelta < 42 && partita.getMap().getStrada(scelta).getOccupato()) {
					System.out.println("Strada già occupata");
				}
				if (scelta >= 0 && scelta < 42 && partita.getMap().getStrada(scelta).getRecintato()) {
					System.out.println("Non puoi andare di là, è recintato.");
				}
				if (!((partita.getMap().getStrada(partita.getGiocatore(turno).getPastore(pastoreControllato).getPosizione())).getStradeAdiacenti().contains((int)scelta)) && (partita.getGiocatore(turno).getSoldi() == 0)) {
					System.out.println("Non hai soldi per muoverti a pagamento.");
					return manageMosse(inputLine, pastoreControllato);
				}
				if (scelta == -1) {
					return scelta;
				}
			} while (scelta < 0 || scelta > 41 || partita.getMap().getStrada(scelta).getOccupato() || partita.getMap().getStrada(scelta).getRecintato());
			break;
		case Costanti.SPOSTAPECORA:
			System.out.println("Hai scelto di spostare una pecora. -1 per tornare indietro e cambiare mossa.");
			int pecorePrimaRegione = partita.getMap().getStrada(partita.getGiocatore(turno).getPastore(pastoreControllato).getPosizione()).getPrimaRegione(partita.getMap()).getPecorePresenti();
			int pecoreSecondaRegione = partita.getMap().getStrada(partita.getGiocatore(turno).getPastore(pastoreControllato).getPosizione()).getSecondaRegione(partita.getMap()).getPecorePresenti();
			int primaRegione = partita.getMap().getStrada(partita.getGiocatore(turno).getPastore(pastoreControllato).getPosizione()).getIndexPrimaRegione();
			int secondaRegione = partita.getMap().getStrada(partita.getGiocatore(turno).getPastore(pastoreControllato).getPosizione()).getIndexSecondaRegione();
			System.out.println("Nella regione " + primaRegione + " hai " + pecorePrimaRegione + " pecore");
			System.out.println("Nella regione " + secondaRegione + " hai " + pecoreSecondaRegione + " pecore");
			do {
				System.out.println("Da dove vuoi spostare? -1 per tornare indietro e scegliere nuovamente la mossa.");
				while (!stdin.hasNextInt()) {
					System.out.println("Immettere un numero.");
					stdin.next();
				}
				scelta = stdin.nextInt();
				if (scelta >= 0 && scelta < Costanti.NUMREGIONI && partita.getMap().getRegione(scelta).getPecorePresenti() == 0) {
					System.out.println("Non ci sono pecore in quella regione."); 
					return manageMosse(inputLine, pastoreControllato);
				}
			} while (scelta != primaRegione && scelta != secondaRegione && scelta != -1) ;
			break;
		case Costanti.COMPRATERRITORIO: 
			System.out.println("Hai scelto di comprare una carta territorio. -1 per tornare indietro e cambiare mossa.");
			int primoTerritorio = partita.getMap().getStrada(partita.getGiocatore(turno).getPastore(pastoreControllato).getPosizione()).getPrimaRegione(partita.getMap()).getTerritorio();
			int secondoTerritorio = partita.getMap().getStrada(partita.getGiocatore(turno).getPastore(pastoreControllato).getPosizione()).getSecondaRegione(partita.getMap()).getTerritorio();
			if(primoTerritorio != Costanti.SHEEPSBURG) {
				System.out.println("Puoi comprare carta tipo: " + primoTerritorio);
			}
			if(secondoTerritorio != Costanti.SHEEPSBURG && secondoTerritorio != primoTerritorio) {
				System.out.println("Puoi comprare carta tipo: " + secondoTerritorio);
			} else {
				secondoTerritorio = primoTerritorio;
			}
			do {
				System.out.println("Che carta vuoi comprare? -1 per tornare indietro e scegliere nuovamente la mossa.");
				while (!stdin.hasNextInt()) {
					System.out.println("Immettere un numero.");
					stdin.next();
				}
				scelta = stdin.nextInt();
				if (scelta > 0 && scelta < Costanti.NUMTERRITORI + 1 && partita.getMap().getCarta(scelta - 1).getCarteRimaste() == 0) {
					System.out.println("Non sono rimaste carte di quel territorio."); 	
					return manageMosse(inputLine, pastoreControllato);
				}
				if (scelta > 0 && scelta < Costanti.NUMTERRITORI + 1 && partita.getGiocatore(turno).getSoldi() < partita.getMap().getCarta(scelta - 1).getCosto()) {
					System.out.println("Non hai soldi a sufficienza");
					return manageMosse(inputLine, pastoreControllato);
				}
			} while (scelta != primoTerritorio && scelta != secondoTerritorio && scelta != -1);
			if(scelta != -1) {
				scelta--;
			}
			break;
		}
		return scelta;
	}
	/**
	 * Serie di print in base alla mossa che è stata fatta
	 * @param mossaPrecedente : mossa appena fatta
	 * @param pastoreControllato
	 */
	private void afterMossa(int mossaPrecedente, int pastoreControllato) {
		switch(mossaPrecedente) {
		case Costanti.SPOSTAPASTORE:
			System.out.println("Il tuo pastore si trova ora in: " + partita.getGiocatore(turno).getPastore(pastoreControllato).getPosizione());
			System.out.println("Hai ancora: " + partita.getGiocatore(turno).getSoldi() + " soldi.");
			break;
		case Costanti.SPOSTAPECORA:
			int pecorePrimaRegione = partita.getMap().getStrada(partita.getGiocatore(turno).getPastore(pastoreControllato).getPosizione()).getPrimaRegione(partita.getMap()).getPecorePresenti();
			int pecoreSecondaRegione = partita.getMap().getStrada(partita.getGiocatore(turno).getPastore(pastoreControllato).getPosizione()).getSecondaRegione(partita.getMap()).getPecorePresenti();
			int primaRegione = partita.getMap().getStrada(partita.getGiocatore(turno).getPastore(pastoreControllato).getPosizione()).getIndexPrimaRegione();
			int secondaRegione = partita.getMap().getStrada(partita.getGiocatore(turno).getPastore(pastoreControllato).getPosizione()).getIndexSecondaRegione();
			System.out.println("Nella regione " + primaRegione + " hai " + pecorePrimaRegione + " pecore");
			System.out.println("Nella regione " + secondaRegione + " hai " + pecoreSecondaRegione + " pecore");
			break;
		case Costanti.COMPRATERRITORIO:
			for(int i = 0; i < Costanti.NUMTERRITORI; i++) {
				System.out.println("ora hai: " + partita.getGiocatore(turno).getPossedimenti().getPossedimenti(i) + " carte territorio " + (i+1));
			}
			System.out.println("Hai ancora: " + partita.getGiocatore(turno).getSoldi() + " soldi.");
			break;
		}
	}
}
