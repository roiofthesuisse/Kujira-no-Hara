package conditions;

import java.util.HashMap;

import commandes.CommandeEvent;
import commandes.CommandeMenu;

/**
 * Balise "sinon" de la Condition situee parmi les Commandes Event.
 */
public class ConditionSinon extends Condition implements CommandeEvent, CommandeMenu {

	/**
	 * Constructeur explicite
	 * @param numero de la Condition
	 */
	public ConditionSinon(final int numero) {
		this.numero = numero;
	}
	
	/**
	 * Constructeur generique
	 * @param parametres liste de parametres issus de JSON
	 */
	public ConditionSinon(final HashMap<String, Object> parametres) {
		this( parametres.get("numero") != null ? (int) parametres.get("numero") : -1);
	}
	
	@Override
	public final boolean estVerifiee() {
		return false;
	}
	
	/**
	 * Ce n'est pas une Condition qui implique une proximit� avec le Heros.
	 * @return false 
	 */
	public final boolean estLieeAuHeros() {
		return false;
	}

}
