package conditions;

import commandes.CommandeEvent;
import map.Event;
import map.PageEvent;

/**
 * Est-ce que l'Event regarde dans cette Direction ?
 */
public class ConditionDirection extends Condition implements CommandeEvent {
	private PageEvent page;
	
	private final Event eventConcerne;
	private final int directionVoulue;
	
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
	
	/**
	 * Ce n'est pas une Condition qui implique une proximité avec le Héros.
	 * @return false 
	 */
	public final boolean estLieeAuHeros() {
		return false;
	}

	@Override
	public final PageEvent getPage() {
		return this.page;
	}

	@Override
	public final void setPage(final PageEvent page) {
		this.page = page;
	}

}