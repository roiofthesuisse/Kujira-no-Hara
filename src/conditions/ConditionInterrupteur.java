package conditions;

import commandes.CommandeEvent;
import commandes.CommandeMenu;
import main.Fenetre;
import map.PageEvent;
import menu.ElementDeMenu;

/**
 * Vérifier la valeur d'un interrupteur
 */
public class ConditionInterrupteur extends Condition implements CommandeEvent, CommandeMenu {
	private PageEvent page;
	private ElementDeMenu element;
	
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