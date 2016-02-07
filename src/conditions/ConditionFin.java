package conditions;

import commandes.CommandeEvent;
import commandes.CommandeMenu;
import map.PageEvent;
import menu.ElementDeMenu;

/**
 * Balise "fin" de la Condition située parmi les Commandes Event.
 */
public class ConditionFin extends Condition implements CommandeEvent, CommandeMenu {
	private PageEvent page;
	private ElementDeMenu element;

	/**
	 * Constructeur explicite
	 * @param numero de la Condition
	 */
	public ConditionFin(final int numero) {
		this.numero = numero;
	}
	
	@Override
	public final boolean estVerifiee() {
		return true;
	}
	
	/**
	 * Ce n'est pas une Condition qui implique une proximité avec le Héros.
	 * @return false 
	 */
	public final boolean estLieeAuHeros() {
		return false;
	}
	
	@Override
	public final ElementDeMenu getElement() {
		return this.element;
	}

	@Override
	public final void setElement(final ElementDeMenu element) {
		this.element = element;
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
