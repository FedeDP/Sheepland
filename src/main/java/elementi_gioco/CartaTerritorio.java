package elementi_gioco;

import java.io.Serializable;
/**
 * Elemento di gioco: carta territorio.
 * @author federico
 */
public class CartaTerritorio implements Serializable {
	private static final long serialVersionUID = 1L;
	// Carte rimaste di un certo tipo di territorio
	private int contRimaste;
	// Costo della carta territorio
	private int costo;
	// Tipo del territorio(bosco, pianura, lago ecc)
	int terreno;
	/**
	 * Inizializza ogni territorio passandogli un terreno (0 a 5)
	 * e inizializzando il costo a 0 e il contatore delle carte rimaste a 5	
	 * @param terreno
	 */
	public CartaTerritorio(int terreno) {
		this.terreno = terreno;
		// A inizio partita ci sono 5 carte territorio per tipo. La prima carta di ogni tipo costa 0
		contRimaste = 5;
		costo = 0;
	}
	/**
	 * Quando una carta viene comprata, le carte di quella tipologia incrementano il costo,
	 * e ovviamente va decrementato il contatore.
	 */
	public void cartaAcquistata() {
		// cartaAcquistata viene chiamato ogni volta che si compra una carta territorio
		contRimaste--;
		costo++;
	}
	/**
	 * @return : numero carte rimaste
	 */
	public int getCarteRimaste(){
		return contRimaste;
	}
	/**
	 * @return : il costo della carta
	 */
	public int getCosto() {
		return costo;
	}
	/**
	 * @return : il territorio della carta.
	 */
	public int getTerritorio() {
		return terreno;
	}
	
}
