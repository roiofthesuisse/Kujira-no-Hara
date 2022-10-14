package conditions;

import java.util.HashMap;

/**
 * ConditionAppel renvoie toujours false
 *
 */
public class ConditionAppel extends Condition {
	
	/**
	 * Constructeur explicite
	 * @param numero de la Condition
	 */
	public ConditionAppel(final int numero) {
		this.numero = numero;
	}
	
	/**
	 * Constructeur generique
	 * @param parametres liste de parametres issus de JSON
	 */
	public ConditionAppel(final HashMap<String, Object> parametres) {
		this( parametres.get("numero") != null ? (int) parametres.get("numero") : -1);
	}

	@Override
	public final boolean estVerifiee() {
		return false;
	}

	@Override
	public final boolean estLieeAuHeros() {
		return false;
	}

}
