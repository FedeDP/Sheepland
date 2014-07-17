package it.polimi.deib.provaFinale2014.matteo1.basilico_federico.dipierro;


import java.io.IOException;
import java.rmi.NotBoundException;

import javax.xml.parsers.ParserConfigurationException;

import net.Server;

import org.xml.sax.SAXException;
/**
 * Main Class del programma, che (per ora) avvia il server socket.
 * @author federico
 */
public class StartSheepland {
	/**
	 * Il main, che avvia il server (socket)
	 * @param args
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws InterruptedException 
	 * @throws NotBoundException 
	 */
	public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException, InterruptedException, NotBoundException {
		Server gameServer = new Server();
		gameServer.startServer();
	}
}
