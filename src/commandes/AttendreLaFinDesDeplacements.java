package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import main.Commande;
import map.Event;

/**
 * Ajouter de l'argent au joueur.
 */
public class AttendreLaFinDesDeplacements extends Commande implements CommandeEvent, CommandeMenu {
	
	/**
	 * Constructeur explicite
	 */
	public AttendreLaFinDesDeplacements() {
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public AttendreLaFinDesDeplacements(final HashMap<String, Object> parametres) {
		this();
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		for (Event e : this.page.event.map.events) {
			if (e.deplacementForce != null 
					&& e.deplacementForce.mouvements != null 
					&& e.deplacementForce.mouvements.size() > 0) {
				return curseurActuel;
			}
		}
		return curseurActuel+1;
	}

}