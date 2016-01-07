package comportementEvent;

import java.util.ArrayList;
import java.util.Random;

import main.Fenetre;
import main.Partie;

/**
 * Modifier la valeur d'une variable
 */
public class ModifierVariable extends CommandeEvent {
	int numeroVariable;
	int operationAFaire;
	int operationAFaire2;
	int valeurADonner;
	int valeurADonner2;
	
	/**
	 * Opération à effectuer sur la variable
	 */
	public static final int RENDRE_EGAL_A = 0;
	public static final int AJOUTER = 1;
	public static final int RETIRER = 2;
	public static final int MULTIPLIER = 3;
	public static final int DIVISER = 4;
	public static final int MODULO = 5;
	
	/**
	 * Provenance de la valeur modificatrice
	 */
	public static final int VALEUR_BRUTE = 0;
	public static final int CONTENU_DE_LA_VARIABLE = 1;
	public static final int NOMBRE_ALEATOIRE = 2;
	
	/**
	 * Constructeur explicite
	 * Utiliser les constantes situées dans la classe pour créer une telle opération.
	 * @param numeroVariable numéro de la variable à modifier
	 * @param operationAFaire 0:rendreEgalA 1:ajouter 2:retirer 3:multiplier 4:diviser 5:modulo
	 * @param operationAFaire2 0:valeur 1:contenuDeLaVariable 2:nombreAleatoire
	 * @param valeurADonner valeur brute, numéro de la variable, ou borne inférieure aléatoire
	 * @param valeurADonner2 éventuelle borne supérieure aléatoire
	 */
	public ModifierVariable(final int numeroVariable, final int operationAFaire, final int operationAFaire2, final int valeurADonner, final int valeurADonner2) {
		this.numeroVariable = numeroVariable;
		this.operationAFaire = operationAFaire;
		this.operationAFaire2 = operationAFaire2;
		this.valeurADonner = valeurADonner;
		this.valeurADonner2 = valeurADonner2;
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<CommandeEvent> commandes) {
		int valeur;
		final Partie partieActuelle = Fenetre.getPartieActuelle();
		//operationAFaire2 donne la provenance de la valeur modificatrice
		switch (operationAFaire2) {
			case VALEUR_BRUTE : valeur = valeurADonner; break;
			case CONTENU_DE_LA_VARIABLE : valeur = partieActuelle.variables[valeurADonner]; break;
			case NOMBRE_ALEATOIRE : valeur = (new Random()).nextInt(valeurADonner2-valeurADonner)+valeurADonner; break;
			default: valeur = 0; break;
		}
		//operationAFaire donne le type d'opération à effectuer
		switch (operationAFaire) {
			case RENDRE_EGAL_A : partieActuelle.variables[numeroVariable] = valeur; break;
			case AJOUTER : partieActuelle.variables[numeroVariable] += valeur; break;
			case RETIRER : partieActuelle.variables[numeroVariable] -= valeur; break;
			case MULTIPLIER : partieActuelle.variables[numeroVariable] *= valeur; break;
			case DIVISER : partieActuelle.variables[numeroVariable] /= valeur; break;
			case MODULO : partieActuelle.variables[numeroVariable] %= valeur; break;
			default: break;
		}
		return curseurActuel+1;
	}

}
