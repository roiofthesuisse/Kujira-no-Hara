package conditions;

import commandes.CommandeEvent;

/**
 * Si une animation d'attaque est en cours.
 */
public class ConditionAnimationAttaque extends Condition implements CommandeEvent {
	
	/**
	 * Constructeur vide
	 * Réservé aux Conditions de Pages 
	 */
	public ConditionAnimationAttaque() {
	}
	
	/**
	 * Constructeur explicite
	 * @param numero de la Condition
	 */
	public ConditionAnimationAttaque(final int numero) {
		this.numero = numero;
	}
	
	@Override
	public final boolean estVerifiee() {
		final boolean reponse = this.page.event.map.heros.animationAttaque > 0;
		return reponse;
	}
	
	/**
	 * Ce n'est pas une Condition qui implique une proximité avec le Héros.
	 * @return false 
	 */
	public final boolean estLieeAuHeros() {
		return false;
	}

}