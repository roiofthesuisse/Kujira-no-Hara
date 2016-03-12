package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import jeu.Partie;
import main.Commande;
import main.Fenetre;

/**
 * Ajouter de l'argent au joueur.
 */
public class RetirerArgent extends Commande implements CommandeEvent, CommandeMenu {
	private final int quantite;
	
	/**
	 * Constructeur explicite
	 * @param quantite à ajouter
	 */
	public RetirerArgent(final int quantite) {
		this.quantite = quantite;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public RetirerArgent(final HashMap<String, Object> parametres) {
		this((int) parametres.get("quantite"));
	}
	
	@Override
	public final void executer() {
		final Partie partie = Fenetre.getPartieActuelle();
		partie.argent -= quantite;
		
		//on ne peut pas avoir de l'argent négatif
		if (partie.argent < 0) {
			partie.argent = 0;
		}
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		this.executer();
		return curseurActuel+1;
	}

}