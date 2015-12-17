package menu;

public class RevenirAuJeu extends ComportementElementDeMenu {

	@Override
	public void executer() {
		LecteurMenu lecteurMenu = this.element.menu.lecteur;
		lecteurMenu.fenetre.futurLecteur = lecteurMenu.lecteurMapMemorise;
		lecteurMenu.allume = false;
	}

}
