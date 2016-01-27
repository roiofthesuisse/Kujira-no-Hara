package commandesMenu;

import main.Fenetre;
import map.LecteurMap;
import menu.LecteurMenu;
import menu.Menu;

/**
 * Passer à un autre Menu
 */
public class AllerVersUnAutreMenu extends CommandeMenu {
	public Menu nouveauMenu;
	
	/**
	 * Constructeur explicite
	 * @param nouveauMenu Menu qui remplacera l'actuel
	 */
	public AllerVersUnAutreMenu(final Menu nouveauMenu) {
		this.nouveauMenu = nouveauMenu;
	}
	
	@Override
	public final void executer() {
		//FIXME TODO changerMenu() devrait être appelée par le nouveau Lecteur
		final LecteurMap lecteurMapMemorise = this.element.menu.lecteur.lecteurMapMemorise;
		final LecteurMenu nouveauLecteur = new LecteurMenu(Fenetre.getFenetre(), this.nouveauMenu, lecteurMapMemorise);
		nouveauLecteur.changerMenu();
	}

}
