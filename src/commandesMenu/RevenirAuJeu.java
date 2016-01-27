package commandesMenu;

import menu.LecteurMenu;

/**
 * Quitter le Menu pour revenir sur la Map mémorisée.
 */
public class RevenirAuJeu extends CommandeMenu {
	private final LecteurMenu lecteurMenu;
	
	/**
	 * Constructeur explicite
	 * @param lecteurMenu du Menu qui appelle cette CommandeMenu
	 */
	public RevenirAuJeu(final LecteurMenu lecteurMenu) {
		this.lecteurMenu = lecteurMenu;
	}
	
	@Override
	public final void executer() {
		this.lecteurMenu.fenetre.futurLecteur = this.lecteurMenu.lecteurMapMemorise;
		this.lecteurMenu.allume = false;
	}

}
