package comportementEvent;

import java.util.ArrayList;
import java.util.Random;

public class ModifierVariable extends CommandeEvent {
	int numeroVariable;
	int operationAFaire;
	int operationAFaire2;
	int valeurADonner;
	int valeurADonner2;
	
	/**
	 * Opération à effectuer sur la variable
	 */
	public final static int RENDRE_EGAL_A = 0;
	public final static int AJOUTER = 1;
	public final static int RETIRER = 2;
	public final static int MULTIPLIER = 3;
	public final static int DIVISER = 4;
	public final static int MODULO = 5;
	
	/**
	 * Provenance de la valeur modificatrice
	 */
	public final static int VALEUR_BRUTE = 0;
	public final static int CONTENU_DE_LA_VARIABLE = 1;
	public final static int NOMBRE_ALEATOIRE = 2;
	
	/**
	 * Utiliser les constantes situées dans la classe pour créer une telle opération.
	 * @param numeroVariable numéro de la variable à modifier
	 * @param operationAFaire 0:rendreEgalA 1:ajouter 2:retirer 3:multiplier 4:diviser 5:modulo
	 * @param operationAFaire2 0:valeur 1:contenuDeLaVariable 2:nombreAleatoire
	 * @param valeurADonner valeur brute, numéro de la variable, ou borne inférieure aléatoire
	 * @param valeurADonner2 éventuelle borne supérieure aléatoire
	 */
	public ModifierVariable(int numeroVariable, int operationAFaire, int operationAFaire2, int valeurADonner, int valeurADonner2){
		this.numeroVariable = numeroVariable;
		this.operationAFaire = operationAFaire;
		this.operationAFaire2 = operationAFaire2;
		this.valeurADonner = valeurADonner;
		this.valeurADonner2 = valeurADonner2;
	}
	
	@Override
	public int executer(int curseurActuel, ArrayList<CommandeEvent> commandes) {
		int valeur;
		//operationAFaire2 donne la provenance de la valeur modificatrice
		switch(operationAFaire2){
			case VALEUR_BRUTE : valeur = valeurADonner; break;
			case CONTENU_DE_LA_VARIABLE : valeur = this.page.event.map.lecteur.fenetre.partie.variables[valeurADonner]; break;
			case NOMBRE_ALEATOIRE : valeur = (new Random()).nextInt(valeurADonner2-valeurADonner)+valeurADonner; break;
			default: valeur = 0; break;
		}
		//operationAFaire donne le type d'opération à effectuer
		switch(operationAFaire){
			case RENDRE_EGAL_A : this.page.event.map.lecteur.fenetre.partie.variables[numeroVariable] = valeur; break;
			case AJOUTER : this.page.event.map.lecteur.fenetre.partie.variables[numeroVariable] += valeur; break;
			case RETIRER : this.page.event.map.lecteur.fenetre.partie.variables[numeroVariable] -= valeur; break;
			case MULTIPLIER : this.page.event.map.lecteur.fenetre.partie.variables[numeroVariable] *= valeur; break;
			case DIVISER : this.page.event.map.lecteur.fenetre.partie.variables[numeroVariable] /= valeur; break;
			case MODULO : this.page.event.map.lecteur.fenetre.partie.variables[numeroVariable] %= valeur; break;
			default: break;
		}
		return curseurActuel+1;
	}

}
