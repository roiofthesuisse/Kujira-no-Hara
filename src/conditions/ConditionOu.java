package conditions;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;

/**
 * ConditionOu est vraie si au moins une des conditions imbriqu�es est vraie.
 *
 */
public class ConditionOu extends Condition {
	
	private final ArrayList<Condition> conditions;
	private boolean pageTransmiseAuxSousConditions = false;
	
	/**
	 * Constructeur explicite
	 * @param numero de la Condition
	 * @param jsonConditions conditions au format JSON
	 */
	public ConditionOu(final int numero, final JSONArray jsonConditions) {
		this.numero = numero;
		
		this.conditions = new ArrayList<Condition>();
		Condition.recupererLesConditions(conditions, jsonConditions);
	}
	
	/**
	 * Constructeur generique
	 * @param parametres liste de parametres issus de JSON
	 */
	public ConditionOu(final HashMap<String, Object> parametres) {
		this(
				parametres.containsKey("numero") ? (int) parametres.get("numero") : -1,
				(JSONArray) parametres.get("conditions")
		);
	}

	
	@Override
	public final boolean estVerifiee() {
		apprendreLaPageAuxSousConditions();
		
		for (Condition condition : this.conditions) {
			if (condition.estVerifiee()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Les sous-Conditions ont besoin de connaetre a quelle PageEvent elles appartiennent
	 */
	private void apprendreLaPageAuxSousConditions() {
		if (!this.pageTransmiseAuxSousConditions) {
			for (Condition condition : this.conditions) {
				// On apprend aux sous-Conditions a quelle Page elles appartiennent
				condition.page = this.page;
			}
			this.pageTransmiseAuxSousConditions = true;
		}
	}

	@Override
	public final boolean estLieeAuHeros() {
		for (Condition condition : this.conditions) {
			if (condition.estLieeAuHeros()) {
				return true;
			}
		}
		return false;
	}

}
