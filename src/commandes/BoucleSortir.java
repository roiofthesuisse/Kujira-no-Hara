package commandes;

import java.util.ArrayList;
import main.Commande;

/**
 * Sortir de la Boucle actuelle.
 */
public class BoucleSortir extends Commande implements CommandeEvent, CommandeMenu {
	public int numero; //le numéro de Boucle est le même que le numéro de fin de Boucle qui correspond

	/**
	 * Constructeur explicite
	 * @param numero identifiant de la Boucle
	 */
	public BoucleSortir(final int numero) {
		this.numero = numero;
	}
	
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
			
			//puisqu'on sort de la Boucle, on efface le temps de début de boucle
			//(ce temps sert à vérifier que la Boucle ne dure pas trop longtemps)
			if (commande instanceof Boucle) {
				final Boucle debutDeBoucle = (Boucle) commande;
				if (debutDeBoucle.numero == this.numero) {
					debutDeBoucle.debutBoucle = -1;					
				}
			}
			
			//aller à la fin de la Boucle
			if (commande instanceof BoucleFin) {
				final BoucleFin finDeBoucle = (BoucleFin) commande;
				if (finDeBoucle.numero == this.numero) {
					//la fin de Boucle a été trouvée
					return i+1;
				}
			}
		}
		//la fin de Boucle n'a pas été trouvée
		System.err.println("La fin de la boucle numéro "+numero+" n'a pas été trouvée !");
		return curseurActuel+1;
	}
	
	/**
	 * Les Commandes de Menu sont instantannées et donc n'utilisent pas de curseur.
	 */
	public void executer() {
		//rien
	}
}
