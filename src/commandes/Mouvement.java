package commandes;

import map.Deplacement;
import map.Event;

/**
 * Toute CommandeEvent qui provoque le Mouvement d'un Event doit implémenter cette interface.
 */
public interface Mouvement {
	
	/**
	 * Procéder aux modifications de données permettant au LecteurMap d'afficher l'Event au bon endroit.
	 * @param deplacement dont fait partie ce mouvement
	 */
	void executerLeMouvement(final Deplacement deplacement);
	
	/**
	 * Le Mouvement est-il possible sur cette Map ?
	 * @return true si le Mouvement est possible
	 */
	boolean mouvementPossible();
	
	/**
	 * Tout Mouvement déplace un Event.
	 * Cet Event doit être accessible par les classes qui implémentent Mouvement.
	 * @return l'Event qui va être déplacé
	 */
	Event getEventADeplacer();
	
	/**
	 * Le Mouvement est-il terminé sur la Map ?
	 * Utile à savoir pour passer à la CommandeEvent suivante.
	 * @return true si le Mouvement est terminé
	 */
	boolean isTermine();
	
	/**
	 * Prévenir le Mouvement qu'il a été terminé sur la Map.
	 */
	void setTermine();

}
