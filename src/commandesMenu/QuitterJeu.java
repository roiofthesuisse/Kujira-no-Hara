package commandesMenu;

import main.Fenetre;

/**
 * Eteindre le jeu.
 */
public class QuitterJeu extends CommandeMenu {
	
	/**
	 * Constructeur vide
	 */
	public QuitterJeu() {
		
	}
	
	@Override
	public final void executer() {
		Fenetre.getFenetre().fermer();
	}

}
