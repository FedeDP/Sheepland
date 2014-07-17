package elementi_utente;

import java.io.Serializable;
/**
 * Ogni giocatore ha dei possedimenti, che sono un array di 6 elementi
 * di contatori delle carte territorio per ogni tipologia.
 * @author federico
 */
public class Possedimenti implements Serializable {
	private static final long serialVersionUID = 1L;
	// array che rappresenta i territori posseduti da un giocatore
	private int[] contProprietà = new int[6];
	 /**
	  * Tutto l'array che rappresenta i possedimenti è inizializzato a 0 
	  * tranne un elemento determinato casualmente (rand) che rappresenta 
	  * il territorio ricevuto all'inizio del gioco.
	  */
	 public Possedimenti (int rand){
		 for (int i = 0; i < 6; i++) {
			 contProprietà[i] = 0;
		 }
		contProprietà[rand] = 1;
	 }
	 /**
	  * Dopo aver comprato qualcosa, viene chiamata questo metodo
	  * che aumenta il contatore per quel terreno.
	  */
	 public void updatePossedimenti(int terreno) {
		 contProprietà[terreno]++;
	 }
	 /**
	  * @param index : indice del territorio da guardare
	  * @return : il numero di carte del giocatore per quel territorio.
	  */
	 public int getPossedimenti(int index) {
		 return contProprietà[index];
	 }
}

