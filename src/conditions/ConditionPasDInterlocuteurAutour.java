package conditions;

import java.util.ArrayList;

import commandes.CommandeEvent;
import map.Event;
import map.PageEvent;

/**
 * verifier qu'il n'y a pas d'interlocuteur potentiel autour du Heros.
 */
public class ConditionPasDInterlocuteurAutour extends Condition implements CommandeEvent {
	
	@Override
	public final boolean estVerifiee() {
		final ArrayList<Event> events = this.page.event.map.events;
		for (Event event : events) {
			if (event.pages!=null) {
				for (PageEvent page : event.pages) {
					if (page.conditions!=null) {
						for (Condition condition : page.conditions) {
							if ( condition.getClass().getName().equals(ConditionParler.class.getName()) 
									&& condition.estVerifiee() ) {
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
	 * Ce n'est pas une Condition qui implique une proximite avec le Heros.
	 * Cette Contition s'adresse uniquement au Heros, pas aux autres Events.
	 * @return false 
	 */
	public final boolean estLieeAuHeros() {
		return false;
	}

}
