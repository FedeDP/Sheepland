package net;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interfaccia dei metodi esposti in remoto dal gameloop.
 * @author federico
 */
public interface PartitaInterface extends Remote {
	/**
	 * Unico metodo che espone il gameloop: l'invio della mossa da parte 
	 * dei client RMI, ossia l'inserimento nella coda delle mosse di un
	 * new Args.
	 * @param arg
	 * @throws RemoteException
	 * @throws InterruptedException
	 */
	public void inviaMossa(Args arg) throws RemoteException, InterruptedException;
}
