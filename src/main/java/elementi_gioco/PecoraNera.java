package elementi_gioco;

import java.io.Serializable;

import mappa.MapInit;
/**
 * L'elemento di gioco pecora nera, che estende pecora, aggiungendo il movimento casuale.
 * @author federico
 */
public class PecoraNera extends Pecora implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * inizializzazione della pecora nera passandole un intero ( 0, che è sheepsburg)
	 * @param x
	 */
	public PecoraNera(int x) {
		// stesso tipo di inizializzazione della classe Pecora, che PecoraNera estende 
		super(x);
	}
	/**
	 * Ricevuta una mappa, genera un numero casuale da 1 a 6, e vede se la pecora nera 
	 * ha la possibilità di andare in quella direzione (ossia se la casella 
	 * random non è occupata, non è recintata e esiste).
	 * @param map , cioè la mappa della partita in corso
	 */
	public void setRandomDirection(MapInit map) {
		// viene scelta casualmente una nuova strada (da 1 a 6)	
		int randomNum = (int) (Math.random()* 6 + 1);
		// si controllano le strade confini per vedere se il numero generato faccia parte di essi
		for (Integer x : map.getRegione(posizionePecora).getConfini()) {
			// la pecora nera si muove solo se trova una casella col numero casuale, e se quella casella non ha nessun pastore sopra, nè è recintata
			if (map.getIdStrada(x) == randomNum && map.getStrada(x).getOccupato() == false && map.getStrada(x).getRecintato() == false ) {
				// viene rimossa la pecora dalla regione e si controlla in quale regione spostarla
				map.getRegione(posizionePecora).removePecora(this);
				if (posizionePecora == map.getStrada(x).getIndexPrimaRegione()) {
					posizionePecora = map.getStrada(x).getIndexSecondaRegione();
				} else {
					posizionePecora = map.getStrada(x).getIndexPrimaRegione();
				}
				// viene aggiunta la pecora alla nuova regione
				map.getRegione(posizionePecora).addPecora(this);
				break;
			}
		}		
	}
}
