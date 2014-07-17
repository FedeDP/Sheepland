package net;

import java.rmi.Remote;
import java.rmi.RemoteException;

import elementi_utente.Partita;
/**
 * metodi che può chiamare il RMIClientHandler sul client. 
 * @author federico
 */
public interface ClientInterface extends Remote {
	/**
	 * Mette sulla coda del clientRMI il suo turno di gioco.
	 * @param turno
	 * @throws RemoteException
	 * @throws InterruptedException
	 */
	public void getTurno(int turno) throws RemoteException, InterruptedException;
	/**
	 * ritorna al client RMI la partita inviata dal server, inserendola sulla 
	 * coda del client di Partita.
	 * @param match
	 * @throws RemoteException
	 * @throws InterruptedException
	 */
	public void getPartita(Partita match) throws RemoteException, InterruptedException;
	/**
	 * inserisce sulla coda di stringhe del client, la stringa finale.
	 * @param string
	 * @throws RemoteException
	 * @throws InterruptedException
	 */
	public void getString(String string) throws RemoteException, InterruptedException;
}
