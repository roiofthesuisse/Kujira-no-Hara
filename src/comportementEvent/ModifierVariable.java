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
	 * @param numeroVariable numéro de la variable à modifier
	 * @param operationAFaire 0:rendreEgalA 1:ajouter 2:retirer 3:multiplier 4:diviser 5:modulo
	 * @param operationAFaire2 0:valeur 1:contenuDeLaVariable 2:nombreAleatoire
	 * @param valeurADonner
	 * @param valeurADonner2
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
		//operationAFaire2 donne le type de valeur utilisée
		switch(operationAFaire2){
			case 0 : valeur = valeurADonner; break;
			case 1 : valeur = this.page.event.map.lecteur.fenetre.partie.variables[valeurADonner]; break;
			case 2 : valeur = (new Random()).nextInt(valeurADonner2-valeurADonner)+valeurADonner; break;
			default: valeur = 0; break;
		}
		//operationAFaire donne le type d'opération à effectuer
		switch(operationAFaire){
			case 0 : this.page.event.map.lecteur.fenetre.partie.variables[numeroVariable] = valeur; break;
			case 1 : this.page.event.map.lecteur.fenetre.partie.variables[numeroVariable] += valeur; break;
			case 2 : this.page.event.map.lecteur.fenetre.partie.variables[numeroVariable] -= valeur; break;
			case 3 : this.page.event.map.lecteur.fenetre.partie.variables[numeroVariable] *= valeur; break;
			case 4 : this.page.event.map.lecteur.fenetre.partie.variables[numeroVariable] /= valeur; break;
			case 5 : this.page.event.map.lecteur.fenetre.partie.variables[numeroVariable] %= valeur; break;
			default: break;
		}
		return curseurActuel+1;
	}

}
