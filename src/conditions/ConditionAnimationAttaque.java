package conditions;

import commandes.CommandeEvent;
import map.PageEvent;

/**
 * Si une animation d'attaque est en cours.
 */
public class ConditionAnimationAttaque extends Condition implements CommandeEvent {
	private PageEvent page;
	
	/**
	 * Constructeur vide
	 */
	public ConditionAnimationAttaque() {
		
	}
	
	@Override
	public final boolean estVerifiee() {
		final boolean reponse = (((CommandeEvent) this).getPage().event.map.heros.animationAttaque > 0);
		return reponse;
	}
	
	/**
	 * Ce n'est pas une Condition qui implique une proximité avec le Héros.
	 * @return false 
	 */
	public final boolean estLieeAuHeros() {
		return false;
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