package commandesEvent;

import java.util.ArrayList;

/**
 * Activer ou désactiver l'animation à l'arrêt d'un Event.
 * La modification se fait sur la propriété actuelle de l'Event, et non sur la Page.
 */
public class AnimeALArret extends CommandeEvent {
	private boolean valeur;
	
	/**
	 * Constructeur explicite
	 * @param valeur à affecter
	 */
	public AnimeALArret(final boolean valeur) {
		this.valeur = valeur;
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<CommandeEvent> commandes) {
		this.page.event.animeALArretActuel = this.valeur;
		return curseurActuel+1;
	}

}
