package commandes;

import java.util.ArrayList;
import main.Commande;

/**
 * Une Boucle répète indéfiniment les Commandes qu'elle contient.
 */
public class BoucleFin extends Commande implements CommandeEvent, CommandeMenu {
	public int numero; //le numéro de Boucle est le même que le numéro de fin de Boucle qui correspond

	/**
	 * Une Boucle est une Commande Event, elle peut être executée pour faire des sauts de curseur.
	 * Son execution est instantanée.
	 * @param curseurActuel position du curseur avant l'execution
	 * @param commandes liste des Commandes de la Page
	 * @return nouvelle position du curseur
	 */
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		for (int i = 0; i < commandes.size(); i++) {
			final Commande commande = commandes.get(i);
			if (commande instanceof Boucle) {
				final Boucle debutDeBoucle = (Boucle) commande;
				if (debutDeBoucle.numero == this.numero) {
					//le début de Boucle a été trouvé
					return i+1;
				}
			}
		}
		//le début de Boucle n'a pas été trouvé
		System.err.println("Le début de boucle numéro "+numero+" n'a pas été trouvé !");
		return curseurActuel+1;
	}
	
	/**
	 * Les Commandes de Menu sont instantannées et donc n'utilisent pas de curseur.
	 */
	public void executer() {
		//rien
	}
}
