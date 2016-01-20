package conditions;

import java.util.ArrayList;

import map.Event;
import map.PageDeComportement;

/**
 * Vérifier qu'il n'y a pas d'interlocuteur potentiel autour du Héros.
 */
public class ConditionPasDInterlocuteurAutour extends Condition {
	
	@Override
	public final boolean estVerifiee() {
		final ArrayList<Event> events = this.page.event.map.events;
		for (Event event : events) {
			if (event.pages!=null) {
				for (PageDeComportement page : event.pages) {
					if (page.conditions!=null) {
						for (Condition condition : page.conditions) {
							if ( condition.getClass().getName().equals(ConditionParler.class.getName()) && condition.estVerifiee() ) {
								return false;
							}
						}
					}
				}
			}
		}
		return true;
	}
	
	/**
	 * Ce n'est pas une Condition qui implique une proximité avec le Héros.
	 * Cette Contition s'adresse uniquement au Héros, pas aux autres Events.
	 * @return false 
	 */
	public final boolean estLieeAuHeros() {
		return false;
	}

}
