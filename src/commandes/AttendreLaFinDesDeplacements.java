package commandes;

import java.util.HashMap;
import java.util.List;

import main.Commande;
import map.Event;

/**
 * Ajouter de l'argent au joueur.
 */
public class AttendreLaFinDesDeplacements extends Commande implements CommandeEvent, CommandeMenu {
	private final Integer idEvent;

	/**
	 * Constructeur explicite
	 * 
	 * @param idEvent identifiant de l'Event a attendre (-1 pour tous, 0 pour le
	 *                h�ros, aucun pour celui-ci)
	 */
	public AttendreLaFinDesDeplacements(final Integer idEvent) {
		this.idEvent = idEvent;
	}

	/**
	 * Constructeur g�n�rique
	 * 
	 * @param parametres liste de param�tres issus de JSON
	 */
	public AttendreLaFinDesDeplacements(final HashMap<String, Object> parametres) {
		this(parametres.containsKey("idEvent") ? (int) parametres.get("idEvent") : null);
	}

	@Override
	public final int executer(final int curseurActuel, final List<Commande> commandes) {
		if (idEvent != null && idEvent == -1) {
			// tout le monde
			for (Event e : this.page.event.map.events) {
				if (e.deplacementForce != null && e.deplacementForce.mouvements != null
						&& e.deplacementForce.mouvements.size() > 0) {
					return curseurActuel;
				}
			}

		} else {
			// un seul event
			final Event e;
			if (idEvent == null) {
				// cet event
				e = this.page.event;

			} else if (idEvent == 0) {
				// h�ros
				e = this.page.event.map.heros;

			} else {
				// un event particulier
				e = this.page.event.map.eventsHash.get((Integer) this.idEvent);
			}
			if (e.deplacementForce != null && e.deplacementForce.mouvements != null
					&& e.deplacementForce.mouvements.size() > 0) {
				return curseurActuel;
			}
		}
		return curseurActuel + 1;
	}

}