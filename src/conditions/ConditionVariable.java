package conditions;

import commandes.CommandeEvent;
import commandes.CommandeMenu;
import main.Fenetre;
import map.PageEvent;
import menu.ElementDeMenu;

/**
 * Vérifier la valeur d'une variable
 */
public class ConditionVariable extends Condition implements CommandeEvent, CommandeMenu {
	private PageEvent page;
	private ElementDeMenu element;
	
	private int numeroVariable;
	private int typeInegalite;
	private int valeurQuIlEstCenseAvoir;
	
	
	/**
	 *  Types de comparaisons
	 */
	public static final int EGALE = 0;
	public static final int SUPERIEURE_LARGE = 1;
	public static final int INFERIEURE_LARGE = 2;
	public static final int SUPERIEURE_STRICTE = 3;
	public static final int INFERIEURE_STRICTE = 4;
	public static final int DIFFERENT = 5;
	
	/**
	 * Utiliser les constantes situées dans la classe pour définir le type de comparaison.
	 * @param numeroVariable numéro de la variable
	 * @param inegalite 0 egal ; 1 superieur large ; 2 inferieur large ; 3 superieur strict ; 4 inferieur strict ; 5 différent
	 * @param valeur comparative
	 * @param numero de la condition
	 */
	public ConditionVariable(final int numeroVariable, final int inegalite, final int valeur, final int numero) {
		this.numeroVariable = numeroVariable;
		this.typeInegalite = inegalite;
		this.valeurQuIlEstCenseAvoir = valeur;
		this.numero = numero;
	}
	
	@Override
	public final boolean estVerifiee() {
		final int[] variables = Fenetre.getPartieActuelle().variables;
		switch (typeInegalite) {
			case EGALE : return variables[numeroVariable]==valeurQuIlEstCenseAvoir;
			case SUPERIEURE_LARGE : return variables[numeroVariable]>=valeurQuIlEstCenseAvoir;
			case INFERIEURE_LARGE : return variables[numeroVariable]<=valeurQuIlEstCenseAvoir;
			case SUPERIEURE_STRICTE : return variables[numeroVariable]>valeurQuIlEstCenseAvoir;
			case INFERIEURE_STRICTE : return variables[numeroVariable]<valeurQuIlEstCenseAvoir;
			case DIFFERENT : return variables[numeroVariable]!=valeurQuIlEstCenseAvoir;
			default : return false;
		}
	}
	
	/**
	 * Ce n'est pas une Condition qui implique une proximité avec le Héros.
	 * @return false 
	 */
	public final boolean estLieeAuHeros() {
		return false;
	}

	@Override
	public final ElementDeMenu getElement() {
		return this.element;
	}

	@Override
	public final void setElement(final ElementDeMenu element) {
		this.element = element;
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