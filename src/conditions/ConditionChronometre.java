package conditions;

import java.util.HashMap;

import commandes.CommandeEvent;
import utilitaire.Maths.Inegalite;

/**
 * Est-ce que l'Event regarde dans cette Direction ?
 */
public class ConditionChronometre extends Condition implements CommandeEvent {

	private final int nombreDeSecondes; 
	private final Inegalite inegalite;
	
	/**
	 * Constructeur explicite
	 * @param numero de la Condition
	 * @param nombreDeSecondes auquel on comapre le Chronometre
	 * @param symboleInegalite = egal ; >= superieur large ; <= inferieur large ; > superieur strict ; < inferieur strict ; != different
	 */
	public ConditionChronometre(final int numero, final int nombreDeSecondes, final String symboleInegalite) {
		this.numero = numero;
		this.inegalite = Inegalite.getInegalite(symboleInegalite);
		this.nombreDeSecondes = nombreDeSecondes;
	}
	
	/**
	 * Constructeur generique
	 * @param parametres liste de parametres issus de JSON
	 */
	public ConditionChronometre(final HashMap<String, Object> parametres) {
		this( parametres.containsKey("numero") ? (int) parametres.get("numero") : -1,
			(int) parametres.get("nombreDeSecondes"),
			(String) parametres.get("inegalite")
		);
	}
	
	@Override
	public final boolean estVerifiee() {
		return this.inegalite.comparer(getPartieActuelle().chronometre.secondes, this.nombreDeSecondes);
	}
	
	/**
	 * Ce n'est pas une Condition qui implique une proximite avec le Heros.
	 * @return false 
	 */
	public final boolean estLieeAuHeros() {
		return false;
	}
	
}