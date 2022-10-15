package conditions;

import java.util.ArrayList;
import java.util.HashMap;

import main.Main;
import map.Event;
import utilitaire.Maths.Inegalite;

/**
 * Condition sur la distance qui s�pare deux Events sur la Map.
 */
public class ConditionDistance extends Condition {
	private final Object idEvent1;
	private final Object idEvent2;
	private final int distanceVoulue;
	private final Inegalite inegalite;
	
	/**
	 * Constructeur explicite
	 * @param identifiant (Numero ou nom) du premier Event ; par d�faut, le Heros
	 * @param identifiant (Numero ou nom) du second Event ; par d�faut, cet Event
	 * @param distance entre les deux Events (en carreaux)
	 * @param symboleInegalite pour la comparaison
	 */
	public ConditionDistance(final Object idEvent1, final Object idEvent2, final int distance, final String symboleInegalite) {
		this.idEvent1 = idEvent1;
		this.idEvent2 = idEvent2;
		this.distanceVoulue = distance * Main.TAILLE_D_UN_CARREAU;
		this.inegalite = Inegalite.getInegalite(symboleInegalite);
	}
	
	/**
	 * Constructeur generique
	 * @param parametres liste de parametres issus de JSON
	 */
	public ConditionDistance(final HashMap<String, Object> parametres) {
		this(
				parametres.containsKey("idEvent1") ? parametres.get("idEvent1") : 0, //par d�faut, le Heros
				parametres.containsKey("idEvent2") ? parametres.get("idEvent2") : null, //par defaut, cet Event
				(int) parametres.get("distance"),
				parametres.containsKey("inegalite") ? (String) parametres.get("inegalite") : Inegalite.MOINS_OU_AUTANT.symbole
		);
	}
	
	@Override
	public final boolean estVerifiee() {
		final ArrayList<Event> events1 = recupererLesEventsCandidats(idEvent1);
		final ArrayList<Event> events2 = recupererLesEventsCandidats(idEvent2);
		for (Event event1 : events1) {
			for (Event event2 : events2) {
				final int deltaX = event1.x - event2.x;
				final int deltaY = event1.y - event2.y;
				final double distanceReelle = Math.sqrt(deltaX*deltaX + deltaY*deltaY);
				if (inegalite.comparer(distanceReelle, distanceVoulue)) {
					return true; //au moins un couple d'events doit etre a la bonne distance 
				}
			}
		}
		return false;
	}

	@Override
	public final boolean estLieeAuHeros() {
		return true;
	}

}
