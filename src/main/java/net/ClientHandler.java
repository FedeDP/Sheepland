package net;

import java.io.IOException;
import java.rmi.RemoteException;

import elementi_utente.Partita;
/**
 * Interfaccia sopra i SocketClientHandler e gli RMIClientHandler.
 * Grazie ad essa, al gameloop è trasparente il tipo di connessioni coi client. 
 * @author federico
 *
 */
public interface ClientHandler extends Runnable {
	/**
	 * Invio del turno di gioco ai client.
	 * @throws RemoteException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public void sendTurno() throws RemoteException, InterruptedException, IOException ;
	/**
	 * Invio dell'oggetto partita (stato della), aggiornato, ai client.
	 * @param partita
	 * @throws InterruptedException
	 * @throws RemoteException
	 * @throws IOException
	 */
	public void sendPartita(Partita partita) throws InterruptedException, RemoteException, IOException;
	/**
	 * Invio della stringa conclusiva del gioco (es: "hai vinto!")
	 * @param string
	 * @throws RemoteException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void sendString(String string) throws RemoteException, IOException, InterruptedException;
}
