package commandes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import jeu.Partie;
import main.Commande;
import main.Fenetre;
import map.PageEvent;

/**
 * Modifier la valeur d'une variable
 * TODO rendre cette classe abstraite et utiliser l'héritage pour traiter tous les cas séparément
 */
public class ModifierVariable implements CommandeEvent {
	private PageEvent page;
	
	final int numeroVariable;
	final String operationAFaire;
	final String operationAFaire2;
	final int valeurADonner;
	final int valeurADonner2;
	
	/**
	 * Opération à effectuer sur la variable
	 */
	public static final String RENDRE_EGAL_A = "rendre egal a";
	public static final String AJOUTER = "ajouter";
	public static final String RETIRER = "retirer";
	public static final String MULTIPLIER = "multiplier";
	public static final String DIVISER = "diviser";
	public static final String MODULO = "modulo";
	
	/**
	 * Provenance de la valeur modificatrice
	 */
	public static final String VALEUR_BRUTE = "valeur brute";
	public static final String CONTENU_DE_LA_VARIABLE = "contenu de la variable";
	public static final String NOMBRE_ALEATOIRE = "nombre aleatoire";
	
	/**
	 * Constructeur explicite
	 * Utiliser les constantes situées dans la classe pour créer une telle opération.
	 * @param numeroVariable numéro de la variable à modifier
	 * @param operationAFaire 0:rendreEgalA 1:ajouter 2:retirer 3:multiplier 4:diviser 5:modulo
	 * @param operationAFaire2 0:valeur 1:contenuDeLaVariable 2:nombreAleatoire
	 * @param valeurADonner valeur brute, numéro de la variable, ou borne inférieure aléatoire
	 * @param valeurADonner2 éventuelle borne supérieure aléatoire
	 */
	public ModifierVariable(final int numeroVariable, final String operationAFaire, final String operationAFaire2, final int valeurADonner, final int valeurADonner2) {
		this.numeroVariable = numeroVariable;
		this.operationAFaire = operationAFaire;
		this.operationAFaire2 = operationAFaire2;
		this.valeurADonner = valeurADonner;
		this.valeurADonner2 = valeurADonner2;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public ModifierVariable(final HashMap<String, Object> parametres) {
		this( (int) parametres.get("numeroVariable"),
			(String) parametres.get("operationAFaire"),
			(String) parametres.get("operationAFaire2"),
			(int) parametres.get("valeurADonner"),
			(int) parametres.get("valeurADonner2")
		);
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<? extends Commande> commandes) {
		int valeur;
		final Partie partieActuelle = Fenetre.getPartieActuelle();
		//operationAFaire2 donne la provenance de la valeur modificatrice
		switch (operationAFaire2) {
			case VALEUR_BRUTE : 
				valeur = valeurADonner; 
				break;
			case CONTENU_DE_LA_VARIABLE : 
				valeur = partieActuelle.variables[valeurADonner]; 
				break;
			case NOMBRE_ALEATOIRE : 
				valeur = (new Random()).nextInt(valeurADonner2-valeurADonner)+valeurADonner; 
				break;
			default: 
				System.err.println("ModifierVariable.executer() : valeur inconnue pour operationAFaire2");
				valeur = 0; 
				break;
		}
		//operationAFaire donne le type d'opération à effectuer
		switch (operationAFaire) {
			case RENDRE_EGAL_A : 
				partieActuelle.variables[numeroVariable] = valeur; 
				break;
			case AJOUTER : 
				partieActuelle.variables[numeroVariable] += valeur; 
				break;
			case RETIRER : 
				partieActuelle.variables[numeroVariable] -= valeur; 
				break;
			case MULTIPLIER : 
				partieActuelle.variables[numeroVariable] *= valeur; 
				break;
			case DIVISER : 
				partieActuelle.variables[numeroVariable] /= valeur; 
				break;
			case MODULO : 
				partieActuelle.variables[numeroVariable] %= valeur; 
				break;
			default: 
				System.err.println("ModifierVariable.executer() : valeur inconnue pour operationAFaire");
				break;
		}
		return curseurActuel+1;
	}
	
	@Override
	public final PageEvent getPage() {
		return this.page;
	}

	@Override
	public final void setPage(final PageEvent page) {
		this.page = page;
	}

}
