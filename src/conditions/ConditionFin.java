package conditions;

import java.util.HashMap;

import commandes.CommandeEvent;
import commandes.CommandeMenu;

/**
 * Balise "fin" de la Condition située parmi les Commandes Event.
 */
public class ConditionFin extends Condition implements CommandeEvent, CommandeMenu {

	/**
	 * Constructeur explicite
	 * @param numero de la Condition
	 */
	public ConditionFin(final int numero) {
		this.numero = numero;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public ConditionFin(final HashMap<String, Object> parametres) {
		this((int) parametres.get("numero"));
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

}
