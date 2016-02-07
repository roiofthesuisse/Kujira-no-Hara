package conditions;

import commandes.CommandeEvent;
import map.PageEvent;

/**
 * Vérifier si le Lecteur de Map a figé tous les Events.
 */
public class ConditionStopEvent extends Condition implements CommandeEvent {
	private PageEvent page;
	
	boolean valeurQuIlEstCenseAvoir;
	
	/**
	 * Constructeur explicite
	 * @param valeur attendue
	 */
	public ConditionStopEvent(final boolean valeur) {
		this.valeurQuIlEstCenseAvoir = valeur;
	}
	
	@Override
	public final boolean estVerifiee() {
		return ((CommandeEvent) this).getPage().event.map.lecteur.stopEvent == valeurQuIlEstCenseAvoir;
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