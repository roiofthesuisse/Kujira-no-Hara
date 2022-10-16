package conditions;

import java.util.HashMap;

import commandes.CommandeEvent;
import commandes.CommandeMenu;
import utilitaire.Maths.Inegalite;

/**
 * verifier la valeur d'une variable
 */
public class ConditionVariable extends Condition implements CommandeEvent, CommandeMenu {
	private int numeroVariable;
	private Inegalite inegalite;
	private int valeurQuIlEstCenseAvoir;
	private final boolean comparerAUneAutreVariable;
	
	/**
	 * Utiliser les constantes situees dans la classe pour d�finir le type de comparaison.
	 * @param numero de la Condition
	 * @param numeroVariable Numero de la variable
	 * @param symboleInegalite = egal ; >= superieur large ; <= inferieur large ; > superieur strict ; < inferieur strict ; != different
	 * @param valeur comparative
	 * @param comparerAUneAutreVariable auquel cas la valeur comparative est un Numero de variable
	 */
	public ConditionVariable(final int numero, final int numeroVariable, final String symboleInegalite, final int valeur, final boolean comparerAUneAutreVariable) {
		this.numero = numero;
		this.numeroVariable = numeroVariable;
		this.inegalite = Inegalite.getInegalite(symboleInegalite);
		this.valeurQuIlEstCenseAvoir = valeur;
		this.comparerAUneAutreVariable = comparerAUneAutreVariable;
	}
	
	/**
	 * Constructeur generique
	 * @param parametres liste de parametres issus de JSON
	 */
	public ConditionVariable(final HashMap<String, Object> parametres) {
		this( parametres.containsKey("numero") ? (int) parametres.get("numero") : -1,
			(int) parametres.get("numeroVariable"),
			parametres.containsKey("inegalite") ? (String) parametres.get("inegalite") : Inegalite.PLUS_OU_AUTANT.symbole,
			(int) parametres.get("valeurQuIlEstCenseAvoir"),
			parametres.containsKey("comparerAUneAutreVariable") ? (boolean) parametres.get("comparerAUneAutreVariable") : false
		);
	}
	
	@Override
	public final boolean estVerifiee() {
		final int[] variables = getPartieActuelle().variables;
		if (this.comparerAUneAutreVariable) {
			// Comparer la variable a une autre variable
			return inegalite.comparer(variables[numeroVariable], variables[valeurQuIlEstCenseAvoir]);
		} else {
			// Comparer a une valeur brute
			return inegalite.comparer(variables[numeroVariable], this.valeurQuIlEstCenseAvoir);
		}
	}
	
	/**
	 * Ce n'est pas une Condition qui implique une proximite avec le Heros.
	 * @return false 
	 */
	public final boolean estLieeAuHeros() {
		return false;
	}

}