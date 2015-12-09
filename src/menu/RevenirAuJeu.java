package menu;

import main.Fenetre;

public class RevenirAuJeu extends ComportementElementDeMenu {

	@Override
	public void executer() {
		LecteurMenu lecteurMenu = this.element.menu.lecteur;
		Fenetre.futurLecteur = lecteurMenu.lecteurMapMemorise;
		lecteurMenu.allume = false;
	}

}
