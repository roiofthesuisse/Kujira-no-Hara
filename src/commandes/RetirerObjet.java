package commandes;

import java.util.HashMap;
import java.util.List;

import main.Commande;

/**
 * Retirer un certain nombre d'Objets au joueur.
 */
public class RetirerObjet extends Commande implements CommandeMenu, CommandeEvent {
	private int idObjet;
	private final int quantite;

	/**
	 * Constructeur explicite
	 * 
	 * @param idObjet  identifiant de l'Objet a retirer : soit son nom, soit son
	 *                 num�ro
	 * @param quantite a retirer pour cet Objet
	 */
	public RetirerObjet(final int idObjet, final int quantite) {
		this.idObjet = idObjet;
		this.quantite = quantite;
	}

	/**
	 * Constructeur generique
	 * 
	 * @param parametres liste de parametres issus de JSON
	 */
	public RetirerObjet(final HashMap<String, Object> parametres) {
		this((int) parametres.get("idObjet"), parametres.containsKey("quantite") ? (int) parametres.get("quantite") : 1 // retirer
																														// 1
																														// par
																														// d�faut
		);
	}

	@Override
	public final int executer(final int curseurActuel, final List<Commande> commandes) {
		// on proc�de a la suppression
		final int[] objetsPossedes = getPartieActuelle().objetsPossedes;
		objetsPossedes[this.idObjet] -= quantite;

		// on ne doit pas aller dans les n�gatifs
		if (objetsPossedes[this.idObjet] < 0) {
			objetsPossedes[this.idObjet] = 0;
		}
		return curseurActuel + 1;
	}

}
