package commandes;

import java.util.ArrayList;
import main.Commande;

/**
 * Une Boucle répète indéfiniment les Commandes qu'elle contient.
 */
public class Boucle extends Commande implements CommandeEvent, CommandeMenu {
	public int numero; //le numéro de Boucle est le même que le numéro de fin de Boucle qui correspond

	/**
	 * Constructeur explicite
	 * @param numero identifiant de la Boucle
	 */
	public Boucle(final int numero) {
		this.numero = numero;
	}
	
	/**
	 * Une Boucle est une Commande Event, elle peut être executée pour faire des sauts de curseur.
	 * Son execution est instantanée.
	 * @param curseurActuel position du curseur avant l'execution
	 * @param commandes liste des Commandes de la Page
	 * @return nouvelle position du curseur
	 */
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		return curseurActuel+1;
	}
	
	/**
	 * Les Commandes de Menu sont instantannées et donc n'utilisent pas de curseur.
	 */
	public void executer() {
		//rien
	}
}
