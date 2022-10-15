package conditions;

import java.util.HashMap;

import commandes.CommandeEvent;
import map.Event;
import map.Map;

/**
 * Est-ce que l'Event regarde dans cette Direction ?
 */
public class ConditionDirection extends Condition implements CommandeEvent {
	/**
	 * 0 : le Heros
	 * null : cet Event
	 */
	private final Integer idEventConcerne; 
	private final int directionVoulue;
	
	/**
	 * Constructeur explicite
	 * @param numero de la Condition
	 * @param event dont on v�rifie la direction (0 : le Heros, null : cet Event)
	 * @param direction attendue
	 */
	public ConditionDirection(final int numero, final Integer event, final int direction) {
		this.numero = numero;
		this.idEventConcerne = event;
		this.directionVoulue = direction;
	}
	
	/**
	 * Constructeur generique
	 * @param parametres liste de parametres issus de JSON
	 */
	public ConditionDirection(final HashMap<String, Object> parametres) {
		this( parametres.containsKey("numero") ? (int) parametres.get("numero") : -1,
			(parametres.containsKey("idEventConcerne") ? (Integer) parametres.get("idEventConcerne") : null),
			(int) parametres.get("direction")
		);
	}
	
	@Override
	public final boolean estVerifiee() {
		final Map map = this.page.event.map;
		final Event eventConcerne;
		
		if (idEventConcerne == null) {
			//cet Event
			eventConcerne = this.page.event;
		} else if (idEventConcerne == 0) {
			//le Heros
			eventConcerne = map.heros;
		} else {
			//un Event particulier
			eventConcerne = map.eventsHash.get((Integer) idEventConcerne);
		}
		return eventConcerne.direction == directionVoulue;
	}
	
	/**
	 * Ce n'est pas une Condition qui implique une proximit� avec le Heros.
	 * @return false 
	 */
	public final boolean estLieeAuHeros() {
		return false;
	}
	
}