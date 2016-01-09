package menu;

import main.Fenetre;

/**
 * Eteindre le jeu.
 */
public class QuitterJeu extends ComportementElementDeMenu {
	
	/**
	 * Constructeur vide
	 */
	public QuitterJeu() {
		
	}
	
	@Override
	public final void executer() {
		Fenetre fenetre = this.element.menu.lecteur.fenetre;
		fenetre.fermer();
	}

}
