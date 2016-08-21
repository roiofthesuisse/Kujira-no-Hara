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
	 * 0 : le Héros
	 * null : cet Event
	 */
	private final Integer idEventConcerne; 
	private final int directionVoulue;
	
	/**
	 * Constructeur explicite
	 * @param numero de la Condition
	 * @param event dont on vérifie la direction (0 : le Héros, null : cet Event)
	 * @param direction attendue
	 */
	public ConditionDirection(final int numero, final Integer event, final int direction) {
		this.numero = numero;
		this.idEventConcerne = event;
		this.directionVoulue = direction;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public ConditionDirection(final HashMap<String, Object> parametres) {
		this( parametres.get("numero") != null ? (int) parametres.get("numero") : -1,
			(Integer) (parametres.get("idEventConcerne") != null ?  parametres.get("idEventConcerne") : null),
			(int) parametres.get("direction")
		);
	}
	
	@Override
	public final boolean estVerifiee() {
		final Map map = this.page.event.map;
		Event eventConcerne;
		
		if (idEventConcerne == null) {
			//cet Event
			eventConcerne = this.page.event;
		} else if (idEventConcerne == 0) {
			//le Héros
			eventConcerne = map.heros;
		} else {
			//un Event particulier
			eventConcerne = map.eventsHash.get((Integer) idEventConcerne);
		}
		System.out.println("event concerne:"+eventConcerne.nom);
		System.out.println("directionPresente:"+eventConcerne.direction+" directionVoulue:"+directionVoulue);
		return eventConcerne.direction == directionVoulue;
	}
	
	/**
	 * Ce n'est pas une Condition qui implique une proximité avec le Héros.
	 * @return false 
	 */
	public final boolean estLieeAuHeros() {
		return false;
	}
	
}