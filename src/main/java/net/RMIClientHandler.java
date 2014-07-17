package net;

import java.io.IOException;
import java.rmi.RemoteException;

import elementi_utente.Partita;
/**
 * RMIClientHandler, gestisce gli invii di oggetti dal gameloop al clientRMI.
 * @author federico
 */
public class RMIClientHandler implements ClientHandler {
	private final int playerNum;
	private final ClientInterface mine; 
	/**
	 * Inizializza il client dandogli la posizione nel turno, e una clientInterface per 
	 * utilizzare i metodi esposti dal client.
	 * @param i
	 * @param x
	 * @throws IOException
	 */
	public RMIClientHandler(final int i, final ClientInterface x) throws IOException {
		playerNum = i;
		mine = x;
	}
	/**
	 * Chiama getTurno, metodo esposto dalla ClientInterface, inserendo il playerNum
	 * sulla coda del client.
	 */
	public void sendTurno() throws RemoteException, InterruptedException {
		mine.getTurno(playerNum);
	}
	/**
	 * Chiama sendPartita, metodo esposto dalla ClientInterface, aggiungendo partita 
	 * sulla coda del client.
	 */
	public void sendPartita(Partita partita) throws InterruptedException, RemoteException {
		mine.getPartita(partita);
	}
	/**
	 * Chiama sendString, metodo esposto dalla ClientInterface, aggiungendo string 
	 * sulla coda del client.
	 */
	public void sendString(String string) throws RemoteException, InterruptedException {
		mine.getString(string);
	}
	/**
	 * Metodo run ereditato dalla interfaccia implementata ClientHandelr (che estende runnable).
	 * Qua non fa nulla, oltre che servire per avviare il thread.
	 */
	public void run() {
		// TODO Auto-generated method stub
	}
}
