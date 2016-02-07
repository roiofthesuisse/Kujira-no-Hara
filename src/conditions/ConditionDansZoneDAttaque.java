package conditions;

import commandes.CommandeEvent;
import main.Fenetre;
import map.Event;
import map.Heros;
import map.Hitbox;
import map.PageEvent;

/**
 * Vérifier si le Héros est armé, et si l'Event se trouve dans la zone d'action de son Arme
 */
public class ConditionDansZoneDAttaque extends Condition implements CommandeEvent {
	private PageEvent page;
	
	/**
	 * Constructeur vide
	 */
	public ConditionDansZoneDAttaque() {
		
	}
	
	@Override
	public final boolean estVerifiee() {
		final boolean estCeQueLeHerosAUneArme = (Fenetre.getPartieActuelle().nombreDArmesPossedees > 0);
		if (estCeQueLeHerosAUneArme) {
			final Heros heros = ((CommandeEvent) this).getPage().event.map.heros;
			final Event event = ((CommandeEvent) this).getPage().event;
			final boolean reponse = Hitbox.estDansZoneDAttaque(event, heros);
			return reponse;
		} else {
			return false;
		}
	}
	
	/**
	 * C'est une Condition qui implique une proximité avec le Héros.
	 * @return true 
	 */
	public final boolean estLieeAuHeros() {
		return true;
	}
	
	@Override
	public final PageEvent getPage() {
		return this.page;
	}

	@Override
	public final void setPage(final PageEvent page) {
		this.page = page;
	}

}