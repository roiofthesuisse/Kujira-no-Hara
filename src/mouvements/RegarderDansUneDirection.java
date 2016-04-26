package mouvements;

import java.util.HashMap;

import map.Event;

/**
 * Fait regarder l'event dans une direction donnée.
 */
public class RegarderDansUneDirection extends Mouvement {
	
	protected int direction;
	
	public final int getDirection() {
		return direction;
	}

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
		// Le mouvement n'est pas rejoué
	}

	@Override
	public boolean mouvementPossible() {
		// C'est toujours possible de regarder dans une direction
		return true;
	}

	/** 
	 * Applique l'effet du Mouvement sur la Map et les Events.
	 * @param event subissant le Mouvement
	 */
	@Override
	protected final void calculDuMouvement(final Event event) {
		event.direction = this.getDirection();
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
		return "Regarder dans la direction " + this.getDirection();
	}

}
