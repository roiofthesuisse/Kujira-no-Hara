package conditions;

import main.Fenetre;

/**
 * Vérifier la valeur d'un interrupteur
 */
public class ConditionInterrupteur extends Condition {
	int numeroInterrupteur;
	boolean valeurQuIlEstCenseAvoir;
	
	/**
	 * Constructeur explicite
	 * @param numeroInterrupteur numéro de l'interrupteur à inspecter
	 * @param valeur attendue pour cet interrupteur
	 * @param numeroCondition identifiant de la condition
	 */
	public ConditionInterrupteur(final int numeroInterrupteur, final boolean valeur, final int numeroCondition) {
		this.numeroInterrupteur = numeroInterrupteur;
		this.valeurQuIlEstCenseAvoir = valeur;
		this.numero = numeroCondition;
	}
	
	@Override
	public final boolean estVerifiee() {
		return Fenetre.getPartieActuelle().interrupteurs[numeroInterrupteur] == valeurQuIlEstCenseAvoir;
	}

}