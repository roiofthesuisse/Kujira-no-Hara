package conditions;

import map.Event;

/**
 * Est-ce que l'Event regarde dans cette Direction ?
 */
public class ConditionDirection extends Condition {
	Event eventConcerne;
	int directionVoulue;
	
	/**
	 * Constructeur explicite
	 * @param event dont on vérifie la direction
	 * @param direction attendue
	 */
	public ConditionDirection(final Event event, final int direction) {
		this.eventConcerne = event;
		this.directionVoulue = direction;
	}
	
	@Override
	public final boolean estVerifiee() {
		return eventConcerne.direction==directionVoulue;
	}
	
	public final boolean estLieeAuHeros() {
		return false;
	}

}