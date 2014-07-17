package net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import mappa.Costanti;
import mappa.MapInit;

import org.xml.sax.SAXException;

import elementi_utente.Partita;
/**
 * Classe Server: avvia un server socket in ascolto su una porta predefinita.
 * @author federico
 */
public class Server extends UnicastRemoteObject implements ServerInterface {
	private static final long serialVersionUID = 1L;
	private final int port = 1337;
	public final static String SERVER_NAME = "server";
	public final static int SERVER_PORT = 12189;
	private static ArrayList <GameLoop> partite = new ArrayList <GameLoop> ();
	private ArrayBlockingQueue<Args> q;
	private ArrayBlockingQueue<Integer> output = new ArrayBlockingQueue<Integer>(1);  
	private ArrayBlockingQueue<PartitaInterface> client; 
	private ArrayList <ClientHandler> scl;
	private Registry registry;
	private ServerSocket serverSocket;
	private int contRMI;
	private static final Logger LOGGER = Logger.getLogger(Server.class.getName());
	ExecutorService executor;

	/**
	 * Server init: qua faccio partire il serverRMI.
	 * @throws RemoteException
	 */
	public Server() throws RemoteException  {
		super();
		registry = LocateRegistry.createRegistry(SERVER_PORT);
		registry.rebind(SERVER_NAME, this);
		System.out.println("Server RMI attivo.");
	}
	/**
	 * Avviamo il server, in ascolto su una porta.
	 * @throws IOException : porta non disponibile
	 * @throws SAXException : legato all'xml parser
	 * @throws ParserConfigurationException : legato all'xml parser
	 * @throws InterruptedException 
	 * @throws NotBoundException 
	 */
	public void startServer() throws IOException, SAXException, ParserConfigurationException, InterruptedException, NotBoundException {
		contRMI = 0;
		//necessaria per poi creare i thread
		executor = Executors.newCachedThreadPool(); 	
		q = new ArrayBlockingQueue<Args>(1);
		client = new ArrayBlockingQueue<PartitaInterface>(1);
		scl = new ArrayList<ClientHandler>();	
		try {
			// avvio il server socket su una porta.
			serverSocket = new ServerSocket(port);	
		} catch (IOException e) {
			LOGGER.log(Level.INFO, "Logging an IO error", e);
			return;
		}
		Timer timer = new Timer();
		System.out.println("Server Socket Attivo");
		//faccio partire un thread che esegue la funzione definita in connectionthread, poco sotto.
		new ConnectionThread().start();
		//finchè non ci sono connessi 4 players sto nel ciclo.s
		while (scl.size() < Costanti.MAXPLAYERS && !serverSocket.isClosed()) {
			if (scl.size() == 2) {
				//quando ci sono 2 giocatori connessi, faccio partire un timer che dopo 20sec chiude il serverSocket.
				timer.schedule(new TimerTask() {
		 			@Override
		  			public void run() {
		 				try {
							serverSocket.close();
						} catch (IOException e) {
							LOGGER.log(Level.INFO, "Logging an IO error", e);
						}
		 			}
				}, 20000);
			}
		}
		// se sono uscito prima che il timer finisse il suo lavoro (ossia ci sono 4 players), lo cancello.
		timer.cancel();
		if (!serverSocket.isClosed()) {
			serverSocket.close();
		}
		// inizializzo una nuova partita.
		newGameInit();
		// riavviamo il server, di modo che possa tornare in ascolto.
		startServer();
	}
	/**
	 * thread a parte che gestisce le connessioni socket.
	 * @author federico
	 *
	 */
	 private class ConnectionThread extends Thread {       
		   public void run() {
			   while (!serverSocket.isClosed()) {
				   try {
					   Socket socket = serverSocket.accept();
					   ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
					   ObjectInputStream in = new ObjectInputStream (socket.getInputStream());
					   // in un altro thread controllo cosa voglia fare il client socket collegato.
					   new CheckSocketThread(out, in, socket).start();
				   } catch (IOException e) {
					   break;
				  }			 
			   }
		   }
	 }
	 /**
	  * Thread a parte che gestisce la riconnessione dei socket 
	  * @author federico
	  *
	  */
	 private class CheckSocketThread extends Thread {  
		 private ObjectOutputStream out;
		 private ObjectInputStream in;
		 private Socket socket;
		 /**
		  * Costruttore della classe.
		  * @param out
		  * @param in
		  * @param socket
		  */
		 public CheckSocketThread(ObjectOutputStream out, ObjectInputStream in, Socket socket) {
			 this.out = out;
			 this.in = in;
			 this.socket = socket;
		 }
		 /**
		  * Run che viene avviato dal server per aggiungere client socket alle partite.
		  */
		 public void run() {
		   try {
			   /* se la funzione checksocketclientreconnection dice che il client non vuole/non può riconnettersi 
			    * a una vecchia partita, creo un socketclienthandler e lo aggiungo all'arraylist di quelli della partita. */
			   if (!checkSocketClientReconnection(socket, out, in)) {
				   scl.add(new SocketClientHandler(socket, out, in, q, scl.size()));
				   System.out.println(scl.size() + " client connessi");
			   }
		   } catch (IOException e) {
			   e.printStackTrace();
		   } catch (InterruptedException e) {
				e.printStackTrace();
		   }
		 }
	 }
	 /**
	  * Inizializzazione di un nuovo gameloop
	  * @throws SAXException
	  * @throws IOException
	  * @throws ParserConfigurationException
	  * @throws NotBoundException
	  * @throws InterruptedException
	  */
	 private void newGameInit() throws SAXException, IOException, ParserConfigurationException, NotBoundException, InterruptedException {
		// aggiungiamo una nuova partita con una sua mappa
			Partita partita = new Partita(scl.size(), new MapInit());	
			for (ClientHandler ch: scl) {	
				executor.submit(ch);	//crea realmente i thread
			}
			// avviamo un GameLoop per questa partita su un nuovo thread.
			GameLoop mainLoop = new GameLoop(partita, q, scl);
			partite.add(mainLoop);
			System.out.println("Partita " + partite.size() + " inizializzata.");
			PartitaInterface game = (PartitaInterface) registry.lookup("partita");
			for (int i = 0; i < contRMI; i++) {
				client.put(game);
			}
			executor.submit(mainLoop);
			executor.shutdown();
	 }
	 /**
	  * Metodo per gestire la riconnessione lato socket.
	  * @param socket
	  * @param out
	  * @param in
	  * @return
	  * @throws IOException
	  * @throws InterruptedException
	  */
	 private boolean checkSocketClientReconnection(Socket socket, ObjectOutputStream out, ObjectInputStream in) throws IOException, InterruptedException {
			int inputLine = 0;
			out.flush();
			// Invio al client il numero della partita che il server sta aspettando di creare.
			out.writeInt(partite.size());
			out.flush();
			// il client invierà 0 per cominciare una nuova partita, o 1 per riconnettersi. Qua ovviamente gestiamo solo 1.
			if (in.readInt() == 1) {
				inputLine = in.readInt();
				// se il numero della partita inviato dal client eccede la grandezza dell'arraylist di gameloop.
				if (inputLine > partite.size() || partite.size() == 0) {
					out.writeInt(0);
					out.flush();
					return false;
				}
				// se la partita scelta dal client esiste, ma non ha giocatori inattivi
				if (partite.get(inputLine).getPartita().getActiveNumPlayers() == partite.get(inputLine).getPartita().getNumPlayers()) {
					out.writeInt(2);
					out.flush();
					return false;
				}
				// se si sta riconnettendo ad una vecchia partita.
				out.writeInt(1);
				out.flush();
				for(int i = 0; i < partite.get(inputLine).getPartita().getNumPlayers(); i++) {
					if (!partite.get(inputLine).getPartita().getGiocatore(i).getAttivo()) {
						// creiamo un socketclienthandler per questo client e lo aggiungiamo a quelli della partita, al posto giusto. Poi lo avviamo.
						SocketClientHandler x = new SocketClientHandler(socket, out, in, partite.get(inputLine).getQueue(), i);
						partite.get(inputLine).addScl(x, i);
						executor.submit(x);
						break;
					}
				}
				return true;
			}
		return false;
	}
	/**
	 * Metodo per gestire la riconnessione lato rmi, funziona in maniera simile a quello socket.
	 * @param x
	 * @param first
	 * @param second
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private boolean checkRMIClientReconnection(ClientInterface x, int first, int second) throws IOException, InterruptedException {
		if (first == 1) {
			if (second > partite.size() || partite.size() == 0) {
				output.put(0);
				return false;
			}
			if (partite.get(second).getPartita().getActiveNumPlayers() == partite.get(second).getPartita().getNumPlayers()) {
				output.put(2);
				return false;
			}
			output.put(1);
			for(int i = 0; i < partite.get(second).getPartita().getNumPlayers(); i++) {
				if (!partite.get(second).getPartita().getGiocatore(i).getAttivo()) {
					RMIClientHandler rmi = new RMIClientHandler(i, x);
					partite.get(second).addScl(rmi, i);
					executor.submit(rmi);
				}
			}
			return true;
		}
		output.put(-1);
		return false;
	}
	 
	/**
	 * Aggiunge il clientRMI ai ClientHandler della partita.
	 * @throws InterruptedException 
	 * @throws NotBoundException 
	 */
	public int[] addMe(ClientInterface x, int first, int second) throws IOException, InterruptedException, NotBoundException {
		/* se il client rmi non vuole/non può riconnettersi ad una partita, creiamo un rmiclienthandler e lo
			aggiungiamo all'arraylist di clienthandler della partita che il server aspetta di creare. */
		if(!checkRMIClientReconnection(x, first, second)) {
			scl.add(new RMIClientHandler(scl.size(), x));	
			System.out.println(scl.size() + " client connessi");
		} else {
			// altrimenti gli inviamo l'interfaccia messa a disposizione dal gameloop.
			PartitaInterface game = (PartitaInterface) registry.lookup("partita");
			client.put(game);
		}
		/* finito tutto, gli inviamo due interi: il numero della partita alla quale si collega, e il numero generato dal metodo
			checkrmiclientreconnection.*/
		int[] input = new int[2];
		input[0] = partite.size();
		input[1] = output.take();
		return input;
	}
	/**
	 * Ritorna l'interfaccia del gameloop ai clientRMI.
	 */
	public PartitaInterface getPartitaInt() throws RemoteException, InterruptedException {
		return client.take();
	}
	/**
	 * ritorna al client RMI il contRMI del momento in cui si collega.
	 * Utile per far registrare una interfaccia client diversa per ogni client.
	 */
	public int getRMIClientNum() throws InterruptedException {
		ArrayBlockingQueue<Integer> n = new ArrayBlockingQueue<Integer>(1); 
		n.put(contRMI);
		contRMI++;
		return n.take();
	}
	
}

