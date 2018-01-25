package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import main.Commande;

/**
 * Ajouter de l'argent au joueur.
 */
public class AjouterArgent extends Commande implements CommandeEvent, CommandeMenu {
	private final int quantite;
	
	/**
	 * Constructeur explicite
	 * @param quantite à ajouter
	 */
	public AjouterArgent(final int quantite) {
		this.quantite = quantite;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public AjouterArgent(final HashMap<String, Object> parametres) {
		this((int) parametres.get("quantite"));
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		getPartieActuelle().argent += quantite;
		
		return curseurActuel+1;
	}

}