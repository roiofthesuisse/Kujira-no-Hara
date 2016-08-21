package mouvements;

import java.util.HashMap;

import map.Event;
import map.Event.Direction;

/**
 * L'Event qui effectue le Mouvement va devoir regarder un certain Event de la Map.
 */
public class RegarderUnEvent extends RegarderDansUneDirection {
	int idEventARegarder;
	
	/**
	 * Constructeur explicite
	 * @param idEvent identifiant de l'Event à regarder
	 */
	public RegarderUnEvent(final int idEvent) {
		super(-1);
		this.idEventARegarder = idEvent;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public RegarderUnEvent(final HashMap<String, Object> parametres) {
		this((Integer) parametres.get("idEvent"));
	}
	
	/**
	 * 
	 * @return
	 */
	@Override
	public final int getDirectionImposee() {
		final Event celuiQuiRegarde = this.deplacement.getEventADeplacer();
		final Event celuiQuiEstRegarde = this.deplacement.page.event.map.eventsHash.get((Integer) this.idEventARegarder);
		this.direction = calculerDirectionDeRegard(celuiQuiRegarde, celuiQuiEstRegarde);
		return this.direction;
	}

	/**
	 * A regarde B.
	 * @param a Event qui regarde
	 * @param b Event qui est regardé
	 * @return direction dans laquelle A va regarder
	 */
	public static int calculerDirectionDeRegard(final Event a, final Event b) {
		final int distanceVerticale = a.y - b.y;
		final int distanceHorizontale = a.x - b.x;
		int direction;
		if (Math.abs(distanceVerticale) > Math.abs(distanceHorizontale)) {
			if (distanceVerticale < 0) {
				direction = Direction.BAS;
			} else {
				direction = Direction.HAUT;
			}
		} else {
			if (distanceHorizontale < 0) {
				direction = Direction.DROITE;
			} else {
				direction = Direction.GAUCHE;
			}
		}
		return direction;
	}
}
