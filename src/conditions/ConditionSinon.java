package conditions;

/**
 * Balise "sinon" de la Condition située parmi les Commandes Event.
 */
public class ConditionSinon extends Condition {

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

}
