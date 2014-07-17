package net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import elementi_utente.Partita;
/**
 * Classe che gestisce la connessione client/server.
 * @author federico
 */
public class SocketClientHandler implements ClientHandler {
	private final Socket socket;
	private BlockingQueue<Args> q;
	private final int playerNum;
	private final ObjectOutputStream out;
    private final ObjectInputStream in;
    private static final Logger LOGGER = Logger.getLogger(SocketClientHandler.class.getName());
	/**
	 * Inizializza un gestore server/client passandogli il socket con cui dialogare,
	 * una coda che servirà per inviare le mosse al gameloop, e un intero relativo al 
	 * numero (nel turno di gioco) del client a cui è collegato.
	 * @param socket
	 * @param q
	 * @param i
	 * @param server
	 * @throws IOException
	 */
	public SocketClientHandler(final Socket socket, final ObjectOutputStream out, final ObjectInputStream in, BlockingQueue<Args> q, final int i) throws IOException {
		this.socket = socket;
		this.q = q;
		playerNum = i;
		this.out = out;//new ObjectOutputStream(socket.getOutputStream());
		this.in = in;//new ObjectInputStream(socket.getInputStream());
	}
	/**
	 * Il run della classe.	
	 */
	public void run() {
		try {
			while (!socket.isClosed()) {
				Args x = (Args) in.readObject();
				q.put(x);
			}
		//chiudo gli stream e il socket
			in.close();
			out.close();
			socket.close();
		} catch (IOException e) {
			LOGGER.log(Level.INFO, "Logging an IO error");
		} catch (InterruptedException e) {
			LOGGER.log(Level.INFO, "Logging an interrupted error", e);
		} catch (ClassNotFoundException e) {
			LOGGER.log(Level.INFO, "Logging a class not fuond error", e);
		}
	}
	/**
	 * usata dal gameloop per inviare il nuovo stato della partita a tutti i client.
	 * @param partita
	 * @throws IOException 
	 */
	public void sendPartita(Partita partita) throws IOException {
		out.reset();
		out.writeObject(partita);
		out.flush();
	}
	/**
	 * utilizzata dal gameloop per inviare il turno di gioco di ciascun client.	
	 * @throws IOException 
	 */
	public void sendTurno() throws IOException {
		out.writeInt(playerNum);
		out.flush();
	}
	/**
	 * utilizzata dal gameloop per inviare la stringa finale ai client.
	 * @param x
	 * @throws IOException
	 */
	public void sendString(String x) throws IOException {
		PrintWriter output = new PrintWriter(socket.getOutputStream());
		output.println(x);
		output.flush();
		output.close();
	}
}