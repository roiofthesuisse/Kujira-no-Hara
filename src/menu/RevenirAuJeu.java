package menu;

/**
 * Quitter le Menu et revenir sur la Map mémorisée.
 */
public class RevenirAuJeu extends ComportementElementDeMenu {

	@Override
	public final void executer() {
		LecteurMenu lecteurMenu = this.element.menu.lecteur;
		lecteurMenu.fenetre.futurLecteur = lecteurMenu.lecteurMapMemorise;
		lecteurMenu.allume = false;
	}

}
