package conditions;

import java.util.HashMap;

import main.Main;
import map.Event;
import map.LecteurMap;

/**
 * Quel est le terrain de l'Event ?
 */
public class ConditionTerrain extends Condition {

	final Integer idEvent;
	final int terrain;
	
	/**
	 * Constructeur explicite
	 * @param numero de la Condition
	 * @param idEvent identifiant de l'Event observ�
	 * @param terrain attendu pour cet Event
	 */
	public ConditionTerrain(final int numero, final Integer idEvent, final int terrain) {
		this.numero = numero;
		this.idEvent = idEvent;
		this.terrain = terrain;
	}
	
	/**
	 * Constructeur generique
	 * @param parametres liste de parametres issus de JSON
	 */
	public ConditionTerrain(final HashMap<String, Object> parametres) {
		this(
				parametres.get("numero") != null ? (int) parametres.get("numero") : -1,
				parametres.containsKey("idEvent") ? (int) parametres.get("idEvent") : null,
				(int) parametres.get("terrain")
		);
	}
	
	@Override
	public final boolean estVerifiee() {
		final Event event;
		if (this.idEvent != null && this.idEvent >= 0) {
			event = ((LecteurMap) Main.lecteur).map.eventsHash.get((Integer) this.idEvent);
		} else {
			// si aucun identifiant d'Event n'est sp�cifi�, on prend l'Event de la Commande
			event = this.page.event;
		}
		return event.calculerTerrain() == this.terrain;
	}

	@Override
	public final boolean estLieeAuHeros() {
		return false;
	}

}
