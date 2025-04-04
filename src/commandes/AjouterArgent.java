package commandes;

import java.util.HashMap;
import java.util.List;

import main.Commande;

/**
 * Ajouter de l'argent au joueur.
 */
public class AjouterArgent extends Commande implements CommandeEvent, CommandeMenu {
	private final int quantite;

	/**
	 * Constructeur explicite
	 * 
	 * @param quantite a ajouter
	 */
	public AjouterArgent(final int quantite) {
		this.quantite = quantite;
	}

	/**
	 * Constructeur generique
	 * 
	 * @param parametres liste de parametres issus de JSON
	 */
	public AjouterArgent(final HashMap<String, Object> parametres) {
		this((int) parametres.get("quantite"));
	}

	@Override
	public final int executer(final int curseurActuel, final List<Commande> commandes) {
		getPartieActuelle().argent += quantite;

		return curseurActuel + 1;
	}

}