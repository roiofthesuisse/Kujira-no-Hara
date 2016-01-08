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
	public final Boolean estVerifiee() {
		Boolean reponse = (this.page.event.map.heros.animationAttaque > 0);
		return reponse;
	}

}