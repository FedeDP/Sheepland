package net;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * metodi che può chiamare il client tramite serverRMI.
 */
public interface ServerInterface extends Remote {
	/**
	 * Aggiunge  il client all'arrayList di clientHandler della partita.
	 * @param clientInterface
	 * @param x
	 * @param y
	 * @return 2 valori usati dal client per printare informazioni.
	 * @throws RemoteException
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws NotBoundException
	 */
	public int[] addMe(ClientInterface clientInterface, int x, int y) throws RemoteException, IOException, InterruptedException, NotBoundException;
	/**
	 * Ritorna ai client RMI l'interfaccia remota messa a disposizione dal gameloop.
	 * @return interfaccia
	 * @throws RemoteException
	 * @throws InterruptedException
	 */
	public PartitaInterface getPartitaInt() throws RemoteException, InterruptedException;
	/**
	 * Ritorna ad ogni client RMI il proprio "numero" in base a quanti client RMI si
	 * sono già connessi.
	 * @return 
	 * @throws RemoteException
	 * @throws InterruptedException
	 */
	public int getRMIClientNum() throws RemoteException, InterruptedException;
}
