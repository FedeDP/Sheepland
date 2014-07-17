package net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import UI.CLI;
import UI.DynamicFrame;
import UI.MainFrame;
import elementi_utente.Partita;
/**
 * Classe Client: i client si connettono tramite socket al server.
 * Ciascun client ha un proprio attributo Partita, inviato dal GameLoop.
 * Ha un proprio turno di gioco (l'indice del socket nell'arraylist di socket)
 * @author federico
 */
public class ClientSocket {
	private final String ip;
	private final int port;
	private static int turno;
	private Partita partita;
	private Socket socket;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private final Scanner stdin;
	private boolean reconnected = false;
	private int numPartita;

	/**
	 * Inizializza il client dandogli un ip e una porta su cui ascoltare.
	 * @param ip
	 * @param port
	 */
	public ClientSocket(String ip, int port, Scanner stdin) {
		this.ip = ip;
		this.port = port;
		this.stdin = stdin;
	}
	/**
	 * Loop dei client, che va avanti finché la partita 
	 * non è terminata(stato modificato dal server quando entra nella parte finale)
	 * @throws IOException : se qualche processo I/O dà errore
	 * @throws ClassNotFoundException : lanciato dall'ObjectOutputStream
	 * @throws InterruptedException : se il server viene interrotto
	 * @throws ParserConfigurationException 
	 * @throws SAXException 
	 */
	public void startClient() throws IOException, ClassNotFoundException, InterruptedException, SAXException, ParserConfigurationException {
		System.out.println("Connection established");
		socket = new Socket(ip, port); //crea il socket da mandare al server (server e client devono avere la stessa porta) 
		out = new ObjectOutputStream(socket.getOutputStream());
	    in = new ObjectInputStream (socket.getInputStream());
	    out.flush();
		tryReconnection();
	    partitaInit();
		stdin.close();
	    in.close();
		out.close();
		socket.close();
	}
	
	/**
	 * Prima di fare qualsiasi altra cosa, chiedo all'utente se vuole riconnettersi ad una partita o
	 * giocarne una nuova. Tramite socket invio le varie scelte (come interi) al server che gestisce l'evento.
	 * @throws IOException
	 */
	private void tryReconnection() throws IOException {
		int inputLine;
		numPartita = in.readInt();
		System.out.println("Benvenuto! Cosa vuoi fare? 0 per iniziare una nuova partita, 1 per collegarti ad una partita abbandonata");
		do {
			while (!stdin.hasNextInt()) {
				System.out.println("Immettere un numero.");
				stdin.next();
			}
			inputLine = stdin.nextInt();
		} while (inputLine != 0 && inputLine != 1);
		out.writeInt(inputLine);
		out.flush();
		if (inputLine == 1) {
			System.out.println("Indica il numero della partita abbandonata.");
			do {
				while (!stdin.hasNextInt()) {
					System.out.println("Immettere un numero.");
					stdin.next();
				}
				inputLine = stdin.nextInt();
			} while (inputLine < 0);
			out.writeInt(inputLine);
			out.flush();
			switch (in.readInt()) {
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
		}
	}
	
	/**
	 * Questo metodo riceve il turno di questo client e lo stato iniziale della partita. 
	 * In più, all'inizio, chiede al giocatore se vuole iniziare una nuova partita o
	 * riconnettersi ad una precedentemente abbandonata.
	 * @param in : utilizzato per leggere lo stato iniziale della partita dal GameLoop.
	 * @throws IOException : se qualche processo I/O dà errore
	 * @throws ClassNotFoundException
	 * @throws ParserConfigurationException 
	 * @throws SAXException 
	 * @throws InterruptedException 
	 */
	private void partitaInit() throws IOException, ClassNotFoundException, SAXException, ParserConfigurationException, InterruptedException {
		int scelta;
		//ricevo turno del client dal server
	    turno = in.readInt();
	    //ricevo stato iniziale dal server
	    partita = (Partita) in.readObject();
	    System.out.println("Partita numero: " + numPartita + "; ricordatene per riconnetterti.");
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
			CLI c = new CLI(turno, partita, out, in, null, null, null, socket, stdin);
			c.startCLI(reconnected);
			break;
		case 1:
			MainFrame f = new MainFrame(partita, turno, out, null, null);
			f.startUI(in, null, socket, reconnected);
			break;
		case 2:
			DynamicFrame df = new DynamicFrame(partita, turno, out, null, null);
			df.startUI(in, null, socket, reconnected);
			break;
		}
	}
}