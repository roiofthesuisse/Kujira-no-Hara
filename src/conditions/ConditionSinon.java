package conditions;

import commandes.CommandeEvent;
import commandes.CommandeMenu;

/**
 * Balise "sinon" de la Condition située parmi les Commandes Event.
 */
public class ConditionSinon extends Condition implements CommandeEvent, CommandeMenu {

	/**
	 * Constructeur explicite
	 * @param numero de la Condition
	 */
	public ConditionSinon(final int numero) {
		this.numero = numero;
	}
	
	@Override
	public final boolean estVerifiee() {
		return false;
	}
	
	/**
	 * Ce n'est pas une Condition qui implique une proximité avec le Héros.
	 * @return false 
	 */
	public final boolean estLieeAuHeros() {
		return false;
	}

}
