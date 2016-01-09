package conditions;

/**
 * Balise "fin" de la Condition située parmi les Commandes Event.
 */
public class ConditionFin extends Condition {

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

}
