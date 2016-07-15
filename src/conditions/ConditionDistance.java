package conditions;

import java.util.HashMap;

import main.Fenetre;
import map.Event;

public class ConditionDistance extends Condition {
	private final Integer idEvent1;
	private final Integer idEvent2;
	private final int distance;
	
	/**
	 * Constructeur explicite
	 * @param idEvent1 identifiant de l'Event 1
	 * @param idEvent2 identifiant de l'Event 2
	 * @param distance entre les deux Events (en carreaux)
	 */
	public ConditionDistance(final Integer idEvent1, final Integer idEvent2, final int distance) {
		this.idEvent1 = idEvent1;
		this.idEvent2 = idEvent2;
		this.distance = distance*Fenetre.TAILLE_D_UN_CARREAU;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public ConditionDistance(final HashMap<String, Object> parametres) {
		this(parametres.containsKey("idEvent1") ? (Integer) parametres.get("idEvent1") : null,
			 parametres.containsKey("idEvent2") ? (Integer) parametres.get("idEvent2") : null,
			 (int) parametres.get("distance")
		);
	}
	
	@Override
	public boolean estVerifiee() {
		final Event event1;
		final Event event2;
		if (idEvent1 == null) {
			event1 = this.page.event;
		} else if (idEvent1 ==  0) {
			event1 = this.page.event.map.heros;
		} else {
			event1 = this.page.event.map.eventsHash.get(idEvent1);
		}
		if (idEvent2 == null) {
			event2 = this.page.event;
		} else if (idEvent2 == 0) {
			event2 = this.page.event.map.heros;
		} else {
			event2 = this.page.event.map.eventsHash.get(idEvent2);
		}
		int deltaX = event1.x - event2.x;
		int deltaY = event1.y - event2.y;
		return Math.sqrt(deltaX*deltaX + deltaY*deltaY) <= distance;
	}

	@Override
	public boolean estLieeAuHeros() {
		return true;
	}

}
