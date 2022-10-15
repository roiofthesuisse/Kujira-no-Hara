package commandes;

import java.util.HashMap;
import java.util.List;

import main.Commande;

/**
 * Ajouter un certain nombre d'Objets au joueur.
 */
public class AjouterObjet extends Commande implements CommandeEvent, CommandeMenu {
	private int idObjet;
	private final int quantite;

	/**
	 * Constructeur explicite
	 * 
	 * @param idObjet  identifiant de l'Objet a ajouter : soit son nom, soit son
	 *                 Numero
	 * @param quantite a ajouter pour cet Objet
	 */
	public AjouterObjet(final int idObjet, final int quantite) {
		this.idObjet = idObjet;
		this.quantite = quantite;
	}

	/**
	 * Constructeur generique
	 * 
	 * @param parametres liste de parametres issus de JSON
	 */
	public AjouterObjet(final HashMap<String, Object> parametres) {
		this((int) parametres.get("idObjet"), parametres.containsKey("quantite") //
				? (int) parametres.get("quantite") //
				: 1 // ajouter 1 par d�faut
		);
	}

	@Override
	public final int executer(final int curseurActuel, final List<Commande> commandes) {
		// on proc�de a l'ajout
		final int[] objetsPossedes = getPartieActuelle().objetsPossedes;
		objetsPossedes[this.idObjet] += quantite;

		return curseurActuel + 1;
	}

}
