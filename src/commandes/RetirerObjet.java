package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import main.Commande;
import main.Fenetre;

/**
 * Retirer un certain nombre d'Objets au joueur.
 */
public class RetirerObjet extends Commande implements CommandeMenu, CommandeEvent {
	private int idObjet;
	private final int quantite;
	
	/**
	 * Constructeur explicite
	 * @param identifiantObjet identifiant de l'Objet à retirer : soit son nom, soit son numéro
	 * @param quantite à retirer pour cet Objet
	 */
	public RetirerObjet(final int idObjet, final int quantite) {
		this.idObjet = idObjet;
		this.quantite = quantite;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public RetirerObjet(final HashMap<String, Object> parametres) {
		this( (int) parametres.get("numeroObjet"),
			parametres.containsKey("quantite") ? (int) parametres.get("quantite") : 1 //retirer 1 par défaut
		);
	}

	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {		
		//on procède à la suppression
		final int[] objetsPossedes = Fenetre.getPartieActuelle().objetsPossedes;
		objetsPossedes[this.idObjet] -= quantite;
		
		//on ne doit pas aller dans les négatifs
		if (objetsPossedes[this.idObjet] < 0) {
			objetsPossedes[this.idObjet] = 0;
		}
		return curseurActuel+1;
	}

}
