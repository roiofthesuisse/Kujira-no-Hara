package commandes;

import java.util.ArrayList;

import main.Commande;
import main.Fenetre;
import menu.ElementDeMenu;

/**
 * Eteindre le jeu.
 */
public class QuitterJeu implements CommandeMenu {
	private ElementDeMenu element;
	
	/**
	 * Constructeur vide
	 */
	public QuitterJeu() {
		
	}
	
	@Override
	public final void executer() {
		Fenetre.getFenetre().fermer();
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
	public final int executer(final int curseurActuel, final ArrayList<? extends Commande> commandes) {
		this.executer();
		return curseurActuel+1;
	}

}
