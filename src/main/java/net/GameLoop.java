package net;

import java.io.IOException;
import java.rmi.AccessException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import mappa.Costanti;
import elementi_utente.Partita;
/**
 * Classe lanciata in un nuovo thread dal server, che gestisce lo svoglimento di una e una sola partita.
 * @author federico
 */
public class GameLoop extends UnicastRemoteObject implements Runnable, PartitaInterface {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Partita partita;
	private ArrayBlockingQueue<Args> q;
	private ArrayList <ClientHandler> scl;
	private final Registry registry;
	private static final Logger LOGGER = Logger.getLogger(GameLoop.class.getName());

	/**
	 * Inizializza il GameLoop assegnandogli una partita, una coda da cui 
	 * riceverà informazioni, e l'arraylist dei serverclienthandler per dialogare coi client.
	 * Infine, rebinda se stesso su "partita", per farsi trovare su RMI.
	 * @param partita
	 * @param q
	 * @param scl
	 * @throws RemoteException 
	 * @throws AccessException 
	 */
	public GameLoop(Partita partita, ArrayBlockingQueue<Args> q, ArrayList <ClientHandler> scl) throws AccessException, RemoteException{
		this.partita = partita;
		this.q = q;
		this.scl = scl;
		registry = LocateRegistry.getRegistry("localhost", Server.SERVER_PORT);
		registry.rebind("partita", this);
	}
	/**
	 * Il run della funzione.
	 */
	public void run() {
		Args clientMossa = null;
		//invio lo stato iniziale della partita a tutti i client 
		partitaInit();
		//inizializziamo i pastori
		try {
			pastoreInit();
		} catch (IOException e1) {
			LOGGER.log(Level.INFO, "Logging an IO error", e1);
		} catch (InterruptedException e) {
			LOGGER.log(Level.INFO, "Logging an interrupted error", e);
		}
		// primo loop del gioco
		startLoop(clientMossa);
		//Se siamo usciti perché son finiti i recinti, si passa al loop finale.
		if (partita.getActiveNumPlayers() > 1) {
			// calcolo il numero delle mosse rimanenti per quel round di gioco
			int mosseRimanenti = (Costanti.NUMMOSSE * partita.getNumPlayers()) - (partita.getTurno() * Costanti.NUMMOSSE) - partita.getNumMosseTurno();
			try {
				finalLoop(partita.getNumMosseTurno(), mosseRimanenti);
			} catch (IOException e1) {
				LOGGER.log(Level.INFO, "Logging an IO error", e1);
			} catch (InterruptedException e) {
				LOGGER.log(Level.INFO, "Logging an interrupted error", e);
			}
			// se ci sono ancora almeno 2 giocatori connessi, inviamo i vincitori
			if (partita.getActiveNumPlayers() > 1) {
				int[] pecore = new int[partita.getNumPlayers()];
				pecore = andTheWinnerIs(partita.getMap().getPecoraNera().getPosition());
				try {
					sendWinners(pecore);
				} catch (IOException e) {
					LOGGER.log(Level.INFO, "Logging an IO error", e);
				} catch (InterruptedException e) {
					LOGGER.log(Level.INFO, "Logging an interrupted error", e);
				}
			}
		}
		System.out.println("Partita terminata");
	}
	/**
	 * Invio stato iniziale e turno nella partita a tutti i client connessi.
	 */
	private void partitaInit() {
		//invio lo stato iniziale della partita a tutti i client 
		for(int i = 0; i < scl.size(); i++) {
			try {
				scl.get(i).sendTurno();
				scl.get(i).sendPartita(partita);
			} catch (Exception e) {
				LOGGER.log(Level.INFO, "Logging an error", e);
				manageDisconnection(i);
			}
		}
	}
	/**
	 * Ricevuta una mossa dal client, la effettuo.
	 * @param clientMossa
	 */
	private void applicaMossa(Args clientMossa) {
		switch (clientMossa.mossa) {
		case Costanti.SPOSTAPASTORE:
			// muoviamo il pastore e decrementiamo il numero di recinti della partita.
			partita.getGiocatore(partita.getTurno()).muoviPastore(clientMossa.attributoMossa, clientMossa.pastoreControllato, partita.getMap());
			partita.decrementaRecinti();
			break;
		case Costanti.SPOSTAPECORA:
			// spostiamo la pecora da una regione all'altra adiacente alla pos del pastore.
			int primaRegione = partita.getMap().getStrada(partita.getGiocatore(partita.getTurno()).getPastore(clientMossa.pastoreControllato).getPosizione()).getIndexPrimaRegione();
			int secondaRegione = partita.getMap().getStrada(partita.getGiocatore(partita.getTurno()).getPastore(clientMossa.pastoreControllato).getPosizione()).getIndexSecondaRegione();
			if (clientMossa.attributoMossa == primaRegione) {
				partita.getGiocatore(partita.getTurno()).getPastore(clientMossa.pastoreControllato).spostaPecora(partita.getMap().getRegione(primaRegione).getPecora(partita.getMap()), secondaRegione, partita.getMap());
			} else {
				partita.getGiocatore(partita.getTurno()).getPastore(clientMossa.pastoreControllato).spostaPecora(partita.getMap().getRegione(secondaRegione).getPecora(partita.getMap()), primaRegione, partita.getMap());
			}
			break;
		case Costanti.COMPRATERRITORIO:
			// compriamo il territorio numero clientMossa.attributoMossa.
			partita.getGiocatore(partita.getTurno()).acquista(partita.getMap().getCarta(clientMossa.attributoMossa), clientMossa.pastoreControllato, partita.getMap());
			break;
		}
	}
	/**
	 * Inizializzazione dei pastori.
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void pastoreInit() throws IOException, InterruptedException {
		Args pos;
		int i;
		// ogni giocatore ci invia la pos del/dei proprio/propri pastore/i.
		for (int j = 0; j < partita.getNumPlayers(); j++) {
			for (i = 0; i < partita.getGiocatore(j).getNumPastori(); i++) {
				pos = q.take();
				partita.getGiocatore(j).getPastore(i).setPosizione((int)pos.attributoMossa);
				partita.getMap().getStrada(pos.attributoMossa).occupa(true);	
				System.out.println("SERVER: Il pastore " + i + " del client " + j  + " è in posizione " + partita.getGiocatore(j).getPastore(i).getPosizione());
				// quando siamo all'ultimo pastore dell'ultimo giocatore, facciamo il primo spostamento casuale della pecore nera.
				if (j == partita.getNumPlayers() - 1 && i == partita.getGiocatore(j).getNumPastori() - 1)  {
					partita.getMap().getPecoraNera().setRandomDirection(partita.getMap());
					System.out.println("La pecora nera si trova ora in: " + partita.getMap().getPecoraNera().getPosition());	
				}
				sendPartita();
			}
		}
	}
	/** 
	 * Stiamo nel while finché:
	 * 1) il numero di recinti usati è inferiore a 20
	 * 2) ci sono almeno 2 giocatori attivi.
	 * Loop iniziale del gioco.
	 */
	private void startLoop(Args clientMossa) {
		while(partita.getRecintiRimanenti() != 0 && partita.getActiveNumPlayers() > 1) {
			try {
				// aspettiamo per 60sec una mossa. Se non la riceviamo, kickiamo il giocatore.
				clientMossa = q.poll(60, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				LOGGER.log(Level.INFO, "Logging an interrupted error", e);
			}
			if (clientMossa == null) {
				System.out.println("Timeout mossa terminato");
				manageDisconnection(partita.getTurno());
			} else {
				applicaMossa(clientMossa);
				partita.incrementaNumMosseTurno();
				System.out.println("mossa numero " + partita.getNumMosseTurno() + " del client " + partita.getTurno());
				if (partita.getNumMosseTurno() == Costanti.NUMMOSSE) {
					partita.azzeraMosse();
					do {
						partita.cambiaTurno();
					} while (!(partita.getGiocatore(partita.getTurno()).getAttivo()));
					partita.getMap().getPecoraNera().setRandomDirection(partita.getMap());
					System.out.println("La pecora nera si trova ora in: " + partita.getMap().getPecoraNera().getPosition());
				}
			}
			//l'oggetto partita viene inviato ai socket e rmi connessi.			
			sendPartita();
		}
	}
	/**
	 * LoopFinale, che andrà da i = 0 fino alle mosseRimanenti per quel round del gioco,
	 * @param numMosse : numMosse a cui siamo usciti dal loop principale
	 * @param mosseRimanenti : mosse restanti per quel round del gioco
	 * @return 
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	private void finalLoop(int numMosse, int mosseRimanenti) throws IOException, InterruptedException {
		Args clientMossa = null;
		int i = 0;
		while(i < mosseRimanenti && partita.getActiveNumPlayers() > 1) {
			try {
				clientMossa = q.poll(60, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				LOGGER.log(Level.INFO, "Logging an interrupted error", e);
			}
			if (clientMossa == null) {
				System.out.println("Timeout mossa terminato");
				manageDisconnection(partita.getTurno());
			} else {
				i++;
				applicaMossa(clientMossa);
				partita.incrementaNumMosseTurno();
				System.out.println("mossa numero " + partita.getNumMosseTurno() + " del client " + partita.getTurno());
				if (partita.getNumMosseTurno() == Costanti.NUMMOSSE) {
					partita.azzeraMosse();
					do {
						partita.cambiaTurno();
					} while (!(partita.getGiocatore(partita.getTurno()).getAttivo()));
					partita.getMap().getPecoraNera().setRandomDirection(partita.getMap());
					System.out.println("La pecora nera si trova ora in: " + partita.getMap().getPecoraNera().getPosition());
				}
				if (i == mosseRimanenti) {
					partita.termina();
				}
			}
			//l'oggetto partita viene inviato ai socket e rmi connessi.			
			sendPartita();
		}
	}
	/**
	 * Calcolo i punti di ciascun giocatore
	 * @param posPecoraNera : la pecora nera vale doppio!
	 * @return l'array con i punti di ciascun giocatore.
	 */
	private int[] andTheWinnerIs(int posPecoraNera) {
		// creo un array che ci dirà per ogni giocatore quante pecore (punti) possiede.
		int[] pecore = new int[partita.getNumPlayers()];
		for (int i = 0; i < partita.getNumPlayers(); i++) {
			pecore[i] = 0;
			for(int j = 0; j < Costanti.NUMTERRITORI; j++) {
				for(int x = 1; x < Costanti.NUMREGIONI; x++) { //0 è sheepsburg
					if (partita.getMap().getRegione(x).getTerritorio() == (j + 1) && partita.getGiocatore(i).getPossedimenti().getPossedimenti(j) != 0) {
							pecore[i] = pecore[i] + partita.getMap().getRegione(x).getPecorePresenti() * partita.getGiocatore(i).getPossedimenti().getPossedimenti(j);
							// la pecora nera vale doppio!
							if (i == posPecoraNera) {
								pecore[i]++;
							}
					}
				}
			}
		}
		return pecore;
	}
	/**
	 * Calcolo il massimo nell'array di punti, e per ogni massimo, lo aggiungo a un arraylist
	 * dei vincitori. Quindi invio la stringa vincente ai vincitori, e la stringa loser ai perdenti.
	 * @param pecore : array con i punti di ciascun giocatore
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	private void sendWinners(int[] pecore) throws IOException, InterruptedException {
		int max = 0;
		for (int i = 0; i < partita.getNumPlayers(); i++) {
			if (pecore[i] > max) {
				max = pecore[i]; 
			}
		}
		ArrayList<Integer> winners = new ArrayList<Integer>();
		for (int i = 0; i < partita.getNumPlayers(); i++) {
			if (pecore[i] == max) {
				winners.add(i);
			}
		}
		for (int i = 0; i < scl.size(); i++) {
			if (winners.contains((int) i)) {
				scl.get(i).sendString("Hai vinto! Hai totalizzato: " + pecore[i] + " punti!");
			} else {
				scl.get(i).sendString("Hai perso; hai totalizzato: " + pecore[i] + " punti!");
			}
		}
	}
	/**
	 * Metodo utilizzato per inviare lo stato aggiornato della partita ai client.
	 * Se si accorge che un client si è disconnesso, lo disabilita dalla partita.
	 */
	private void sendPartita() {
		for(int i = 0; i < scl.size(); i++) {
			if (partita.getGiocatore(i).getAttivo()) {
				try {
					scl.get(i).sendPartita(partita);
				} catch (Exception e) {
					manageDisconnection(i);
					LOGGER.log(Level.INFO, "Logging an interrupted error while sending partita objects back to clients");
				}
			}
		}
	}
	/**
	 * Disabilita un giocatore e rimuove il suo scl dall'arraylist.
	 * Se era il giocatore attivo del turno, cambia turno.
	 * @param turn
	 */
	private void manageDisconnection(int turn) {
		System.out.println("Il giocatore " + turn + " è stato disconnesso.");
		partita.getGiocatore(turn).setAttivo(false);
		//scl.remove(turn);
		if (partita.getTurno() == turn) {
			do {
				partita.cambiaTurno();
			} while (!(partita.getGiocatore(partita.getTurno()).getAttivo()));
			partita.azzeraMosse();
		}
	}
	
	/**
	 * @return questo stato della partita
	 */
	public Partita getPartita() {
		return partita;
	}
	/**
	 * Ritorna la coda di questo gameloop.
	 * @return
	 */
	public ArrayBlockingQueue<Args> getQueue() {
		return q;
	}
	/**
	 * @param x: aggiunge il clienthandler x all'arraylist (per la riconnessione), al posto giusto e gli invia 
	 * lo stato della partita e il suo turno di gioco.
	 * @throws IOException 
	 * @throws InterruptedException 
	 * @throws RemoteException 
	 */
	public void addScl(ClientHandler x, int i) throws RemoteException, InterruptedException, IOException {
		scl.remove(i);
		scl.add(i, x);
		partita.getGiocatore(i).setAttivo(true);
		scl.get(i).sendTurno();
		scl.get(i).sendPartita(partita);
	}
	/**
	 * Intefaccia messa a disposizione su RMI per i client.
	 * Mette la mossa stabilita dal clientRMI sulla coda.
	 */
	public void inviaMossa(Args arg) throws RemoteException, InterruptedException {
		q.put(arg);
	}
}
