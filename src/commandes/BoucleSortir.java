package commandes;

import java.util.HashMap;
import java.util.List;

import main.Commande;

/**
 * Sortir de la Boucle actuelle.
 */
public class BoucleSortir extends Commande implements CommandeEvent, CommandeMenu {
	public int numero; // le Numero de Boucle est le meme que le Numero de fin de Boucle qui correspond

	/**
	 * Constructeur explicite
	 * 
	 * @param numero identifiant de la Boucle
	 */
	public BoucleSortir(final int numero) {
		this.numero = numero;
	}

	/**
	 * Constructeur generique
	 * 
	 * @param parametres liste de parametres issus de JSON
	 */
	public BoucleSortir(final HashMap<String, Object> parametres) {
		this((int) parametres.get("numero"));
	}

	/**
	 * Une Boucle est une Commande Event, elle peut etre execut�e pour faire des
	 * sauts de curseur. Son execution est instantan�e.
	 * 
	 * @param curseurActuel position du curseur avant l'execution
	 * @param commandes     liste des Commandes de la Page
	 * @return nouvelle position du curseur
	 */
	public final int executer(final int curseurActuel, final List<Commande> commandes) {
		for (int i = 0; i < commandes.size(); i++) {
			final Commande commande = commandes.get(i);

			// puisqu'on sort de la Boucle, on efface le temps de d�but de boucle
			// (ce temps sert a verifier que la Boucle ne dure pas trop longtemps)
			if (commande instanceof Boucle) {
				final Boucle debutDeBoucle = (Boucle) commande;
				if (debutDeBoucle.numero == this.numero) {
					debutDeBoucle.debutBoucle = -1;
				}
			}

			// aller a la fin de la Boucle
			if (commande instanceof BoucleFin) {
				final BoucleFin finDeBoucle = (BoucleFin) commande;
				if (finDeBoucle.numero == this.numero) {
					// la fin de Boucle a ete trouv�e
					return i + 1;
				}
			}
		}
		// la fin de Boucle n'a pas ete trouv�e
		System.err.println("La fin de la boucle Numero " + numero + " n'a pas ete trouv�e !");
		return curseurActuel + 1;
	}

	/**
	 * Les Commandes de Menu sont instantann�es et donc n'utilisent pas de curseur.
	 */
	public void executer() {
		// rien
	}
}
