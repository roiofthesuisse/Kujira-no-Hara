package commandes;

import java.util.HashMap;
import java.util.List;

import main.Commande;
import map.Event;

/**
 * Supprimer un Event de la Map
 */
public class SupprimerEvent extends Commande implements CommandeEvent {
	private final Integer idEvent;

	/**
	 * Constructeur explicite
	 * 
	 * @param idEvent id de l'Event a supprimer
	 */
	public SupprimerEvent(final Integer idEvent) {
		this.idEvent = idEvent;
	}

	/**
	 * Constructeur g�n�rique
	 * 
	 * @param parametres liste de param�tres issus de JSON
	 */
	public SupprimerEvent(final HashMap<String, Object> parametres) {
		this(parametres.containsKey("idEvent") ? (Integer) parametres.get("idEvent") : null);
	}

	@Override
	public final int executer(final int curseurActuel, final List<Commande> commandes) {
		final Event eventASupprimer;
		if (this.idEvent == null) {
			eventASupprimer = this.page.event;
		} else {
			eventASupprimer = this.page.event.map.eventsHash.get(this.idEvent);
		}
		eventASupprimer.pageActive = null;
		eventASupprimer.map.supprimerEvenement(eventASupprimer.id);
		return curseurActuel + 1;
	}

}
