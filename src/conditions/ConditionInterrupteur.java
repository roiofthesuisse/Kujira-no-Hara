package conditions;

import java.util.HashMap;

import commandes.CommandeEvent;
import commandes.CommandeMenu;

/**
 * V�rifier la valeur d'un interrupteur
 */
public class ConditionInterrupteur extends Condition implements CommandeEvent, CommandeMenu {
	int numeroInterrupteur;
	boolean valeurQuIlEstCenseAvoir;
	
	/**
	 * Constructeur explicite
	 * @param numeroInterrupteur num�ro de l'interrupteur a inspecter
	 * @param valeur attendue pour cet interrupteur
	 * @param numeroCondition identifiant de la condition
	 */
	public ConditionInterrupteur(final int numeroInterrupteur, final boolean valeur, final int numeroCondition) {
		this.numeroInterrupteur = numeroInterrupteur;
		this.valeurQuIlEstCenseAvoir = valeur;
		this.numero = numeroCondition;
	}
	
	/**
	 * Constructeur generique
	 * @param parametres liste de parametres issus de JSON
	 */
	public ConditionInterrupteur(final HashMap<String, Object> parametres) {
		this((int) parametres.get("numeroInterrupteur"),
			parametres.containsKey("valeurQuIlEstCenseAvoir") ? (boolean) parametres.get("valeurQuIlEstCenseAvoir") : true,
			parametres.containsKey("numero") ? (int) parametres.get("numero") : -1
		);
	}
	
	@Override
	public final boolean estVerifiee() {
		return getPartieActuelle().interrupteurs[numeroInterrupteur] == valeurQuIlEstCenseAvoir;
	}
	
	/**
	 * Ce n'est pas une Condition qui implique une proximit� avec le H�ros.
	 * @return false 
	 */
	public final boolean estLieeAuHeros() {
		return false;
	}

}