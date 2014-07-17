package net;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import UI.CLI;
import UI.DynamicFrame;
import UI.MainFrame;
import elementi_utente.Partita;
/**
 * ClientRMI.
 * @author federico
 */
public class ClientRMI extends UnicastRemoteObject implements ClientInterface {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Partita partita;
	private static int turno;
	private final Registry registry;
	private ArrayBlockingQueue <Partita> match = new ArrayBlockingQueue<Partita>(1);
	private ArrayBlockingQueue <Integer> x = new ArrayBlockingQueue <Integer>(1);
	private ArrayBlockingQueue <String> string = new ArrayBlockingQueue <String>(1);
	private final Scanner stdin;
	private static PartitaInterface game;
	private boolean reconnected = false;

	/**
	 * Costruttore ClientRMI. Binda il proprio registro a "client" per farlo trovare dal server.
	 */
	public ClientRMI(final Scanner stdin) throws RemoteException, AlreadyBoundException {
		super();
		registry = LocateRegistry.getRegistry("localhost", Server.SERVER_PORT);
		this.stdin = stdin;
	}
	/**
	 * Si comporta in maniera identica al ClientSocket.
	 * @throws RemoteException
	 * @throws IOException
	 * @throws NotBoundException
	 * @throws InterruptedException
	 * @throws ClassNotFoundException
	 * @throws ParserConfigurationException 
	 * @throws SAXException 
	 */
	public void startClient() throws RemoteException, IOException, NotBoundException, InterruptedException, ClassNotFoundException, SAXException, ParserConfigurationException {
		// interfaccia del server!
		ServerInterface client = (ServerInterface) registry.lookup(Server.SERVER_NAME);	
		System.out.println("Connection established");
		int num = client.getRMIClientNum();
		registry.rebind("client" + num, this);	
		int[] x = new int [2];
		x = tryReconnection();
		x = client.addMe((ClientInterface) registry.lookup("client" + num), x[0], x[1]);			
		switch (x[1]) {
		case 0:
			System.out.println("Partita inesistente. Attendi inizio nuova partita.");
			break;
		case 1:
			System.out.println("Ti riconnetterai alla partita selezionata.");
			reconnected = true;
			break;
		case 2:
			System.out.println("La partita selezionata non ha giocatori inattivi. Cominci una nuova partita.");
			break;
		}
		game = client.getPartitaInt();
		System.out.println("Partita numero: " + x[0] + "; ricordatene per riconnetterti.");
		partitaInit();
	}
	/**
	 * Inizializza la partita, riceve il turno e lo stato iniziale della partita.
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws InterruptedException
	 * @throws ParserConfigurationException 
	 * @throws SAXException 
	 */
	private void partitaInit() throws IOException, ClassNotFoundException, InterruptedException, SAXException, ParserConfigurationException {
		int scelta;
		//ricevo turno del client dal server
		turno = x.take(); 
	    //ricevo stato iniziale dal server
		partita = match.take();
		System.out.println("Partita con: " + partita.getNumPlayers() + " giocatori");
		System.out.println("Sei il giocatore numero: " + turno);
		System.out.println("Controllerai: " + partita.getGiocatore(turno).getNumPastori() + " pastori");
		System.out.println("Come vuoi giocare? 0 CLI, 1 static UI, 2 dynamic UI.");
		do {
			while (!stdin.hasNextInt()) {
				System.out.println("Immettere un numero.");
				stdin.next();
			}
			scelta = stdin.nextInt();	
		} while (scelta != 0 && scelta != 1 && scelta != 2);
		switch (scelta) {
		case 0:
			CLI c = new CLI(turno, partita, null, null, game, match, string, null, stdin);
			c.startCLI(reconnected);
			break;
		case 1:
			MainFrame f = new MainFrame(partita, turno, null, game, string);
			f.startUI(null, match, null, reconnected);
			break;
		case 2:
			DynamicFrame df = new DynamicFrame(partita, turno, null, game, string);
			df.startUI(null, match, null, reconnected);
			break;
		}	
	}
	/**
	 * Prima di far qualsiasi cosa, chiedo all'utente se vuole riconnettersi a una partita
	 * precedentemente abbandonata, o cominciarne una nuova.
	 * Invio la scelta al server, che gestirà l'evento.
	 * @param client
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws NotBoundException
	 */
	private int[] tryReconnection() throws IOException, InterruptedException, NotBoundException {
		int[] x = new int [2] ;
		System.out.println("Benvenuto! Cosa vuoi fare? 0 per iniziare una nuova partita, 1 per collegarti ad una partita abbandonata");
		do {
			while (!stdin.hasNextInt()) {
				System.out.println("Immettere un numero.");
				stdin.next();
			}
			x[0] = stdin.nextInt();
		} while (x[0] != 0 && x[0] != 1);
		if (x[0] == 1) {
			System.out.println("Indica il numero della partita abbandonata.");
			do {
				while (!stdin.hasNextInt()) {
					System.out.println("Immettere un numero.");
					stdin.next();
				}
				x[1] = stdin.nextInt();
			} while (x[1] < 0);
		}
		return x;
	}
	/**
	 * Per evitare *pessime* wait, utilizzo una blockingqueue di 1 elemento
	 * sulla quale inserisco il valore del turno, che poi spillo.
	 */
	public void getTurno(int turno) throws RemoteException, InterruptedException {
		x.put(turno);
	}
	/**
	 * Come sopra, ma con la partita.
	 */
	public void getPartita(Partita partita) throws RemoteException, InterruptedException {
		match.put(partita);;
	}
	/**
	 * Come sopra, ma con la stringa finale.
	 */
	public void getString(String string) throws RemoteException, InterruptedException {
		this.string.put(string);
		
	}
}