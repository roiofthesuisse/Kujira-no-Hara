package conditions;

/**
 * Si une animation d'attaque est en cours.
 */
public class ConditionAnimationAttaque extends Condition {
	
	/**
	 * Constructeur vide
	 */
	public ConditionAnimationAttaque() {
		
	}
	
	@Override
	public final boolean estVerifiee() {
		final boolean reponse = (this.page.event.map.heros.animationAttaque > 0);
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