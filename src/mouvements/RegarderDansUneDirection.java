package mouvements;

import java.util.HashMap;

import map.Event;

/**
 * Fait regarder l'event dans une direction donnée.
 */
public class RegarderDansUneDirection extends Mouvement {
	
	private int direction;

	/**
	 * Constructeur explicite
	 * @param direction dans laquelle l'Event doit regarder
	 */
	public RegarderDansUneDirection(final int direction) {
		this.direction = direction;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public RegarderDansUneDirection(final HashMap<String, Object> parametres) {
		this((int) parametres.get("direction"));
	}
	

	@Override
	protected void reinitialiserSpecifique() {
		// rien
	}

	@Override
	public boolean mouvementPossible() {
		// Regarder dans une direction est toujours possible.
		return true;
	}

	/** 
	 * Applique l'effet du Mouvement sur la Map et les Events.
	 * @param event subissant le Mouvement
	 */
	@Override
	protected final void calculDuMouvement(final Event event) {
		event.direction = this.direction;
	}

	@Override
	protected void terminerLeMouvementSpecifique(Event event) {
		// Mouvement non spécifique	
	}

	@Override
	protected void ignorerLeMouvementSpecifique(Event event) {
		// Mouvement non spécifique
		
	}

	@Override
	public String toString() {
		return "Regarder dans la direction " + this.direction;
	}

}
