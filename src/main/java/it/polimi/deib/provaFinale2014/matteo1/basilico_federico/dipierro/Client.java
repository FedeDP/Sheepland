package it.polimi.deib.provaFinale2014.matteo1.basilico_federico.dipierro;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;

import javax.xml.parsers.ParserConfigurationException;

import net.ClientRMI;
import net.ClientSocket;

import org.xml.sax.SAXException;
/**
 * Classe astratta client, utilizzata per avviare o client
 * socket o client RMI.
 * @author federico
 *
 */
public abstract class Client {
	private static Scanner stdin = new Scanner(System.in);
	/**
	 * Il main, che avvia un client. Starà poi all'utente scegliere se avviare
	 * un client rmi o uno socket.
	 * @param args
	 * @throws InterruptedException
	 * @throws RemoteException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws NotBoundException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws AlreadyBoundException
	 */
	public static void main(String[] args) throws InterruptedException, RemoteException, ClassNotFoundException, IOException, NotBoundException, SAXException, ParserConfigurationException, AlreadyBoundException {
		int inputLine;
		System.out.println("Socket (0) o RMI (1)?");
		do {
			while (!stdin.hasNextInt()) {
				System.out.println("Immettere un numero.");
				stdin.next();
			}
			inputLine = stdin.nextInt();
		} while (inputLine != 0 && inputLine != 1);
		if (inputLine == 0) {
			new ClientSocket("localhost", 1337, stdin).startClient();
		} else {
			new ClientRMI(stdin).startClient();
		}
	}
}