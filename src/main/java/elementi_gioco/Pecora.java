package elementi_gioco;

import java.io.Serializable;
/**
 * L'elemento di gioco pecora: essa funge da "punteggio" e può solo essere mossa dall'utente.
 * @author federico
 */
public class Pecora implements Serializable {
	private static final long serialVersionUID = 1L;
	// posizione della pecora 
	protected int posizionePecora;
	// indice nell'array globale di regioni che rappresenta la posizione della pecora
	private int index;
	/**
	 * inizializzazione pecore: gli passo una regione da 1 a 18.
	 * @param x
	 */
	public Pecora(int x){
		// la pecora si troverà nella regione x e il suo indice nell'array sarà lo stesso x
		posizionePecora = x;
		index = x;
	}
	/**
	 * Utilizzata per spostare la pecora: gli passo un nuovo intero della posizione.
	 * @param position
	 */
	public void setPosition(int position) {
		posizionePecora = position;
	}
	/**
	 * Utilizzato per vedere dove si trova la pecora.
	 * @return posizione pecora.
	 */
	public int getPosition() {
		return posizionePecora;
	}
	/**
	 * Indice nell'array globale delle pecore.
	 * @return l'indice.
	 */
	public int getIndex() {
		return index;
	}
}


