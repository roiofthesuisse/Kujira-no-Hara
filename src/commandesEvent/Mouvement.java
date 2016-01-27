package commandesEvent;

import map.Deplacement;
import map.Event;

/**
 * Toute CommandeEvent qui provoque le Mouvement d'un Event doit implémenter cette interface.
 */
public interface Mouvement {
	
	/**
	 * Procéder aux modifications de données permettant au LecteurMap d'afficher l'Event au bon endroit.
	 * @param event qui se déplace
	 * @param deplacement dont fait partie ce mouvement
	 */
	void executerLeMouvement(final Event event, final Deplacement deplacement);
	
	/**
	 * Le Mouvement est-il possible sur cette Map ?
	 * @param event à déplacer
	 * @return true si le Mouvement est possible
	 */
	boolean mouvementPossible(Event event);
	
}
