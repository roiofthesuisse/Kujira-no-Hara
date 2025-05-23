package conditions;

import java.util.HashMap;

import commandes.CommandeEvent;

/**
 * verifier si le Lecteur de Map a fig� tous les Events.
 */
public class ConditionStopEvent extends Condition implements CommandeEvent {
	boolean valeurQuIlEstCenseAvoir;
	
	/**
	 * Constructeur partiel
	 * R�serv� aux Conditions de Pages et Menus
	 * @param valeur attendue
	 */
	public ConditionStopEvent(final boolean valeur) {
		this.valeurQuIlEstCenseAvoir = valeur;
	}
	
	/**
	 * Constructeur explicite
	 * @param numero de la Condition
	 * @param valeur attendue
	 */
	public ConditionStopEvent(final int numero, final boolean valeur) {
		this.valeurQuIlEstCenseAvoir = valeur;
	}
	
	/**
	 * Constructeur generique
	 * @param parametres liste de parametres issus de JSON
	 */
	public ConditionStopEvent(final HashMap<String, Object> parametres) {
		this( parametres.get("numero") != null ? (int) parametres.get("numero") : -1,
			(boolean) parametres.get("valeur") 
		);
	}
	
	@Override
	public final boolean estVerifiee() {
		return this.page.event.map.lecteur.stopEvent == valeurQuIlEstCenseAvoir;
	}
	
	/**
	 * Ce n'est pas une Condition qui implique une proximite avec le Heros.
	 * @return false 
	 */
	public final boolean estLieeAuHeros() {
		return false;
	}

}