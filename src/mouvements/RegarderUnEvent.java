package mouvements;

import java.util.HashMap;

import map.Event;
import map.Event.Direction;

/**
 * L'Event qui effectue le Mouvement va devoir regarder un certain Event de la Map.
 */
public class RegarderUnEvent extends RegarderDansUneDirection {
	private int idEventARegarder;
	private Sens sens; 
	
	/**
	 * Constructeur explicite
	 * @param idEvent identifiant de l'Event à regarder
	 * @param sens "suivre" ou "fuir" du regard
	 */
	public RegarderUnEvent(final int idEvent, final Sens sens) {
		super(-1);
		this.idEventARegarder = idEvent;
		this.sens = sens;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public RegarderUnEvent(final HashMap<String, Object> parametres) {
		this(
				(int) parametres.get("idEvent"), //0 est le Héros
				parametres.containsKey("sens") ? (((String) parametres.get("sens")).equals("fuir") ? Sens.FUIR : Sens.SUIVRE) : Sens.SUIVRE //si non précisé : on suit
		);
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
		
		if (Sens.FUIR.equals(this.sens)) {
			this.direction = Event.Direction.directionOpposee(this.direction);
		}
		
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
		final int direction;
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
