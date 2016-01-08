package menu;

/**
 * Passer à un autre Menu
 */
public class AllerVersUnAutreMenu extends ComportementElementDeMenu {
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
		this.element.menu.lecteur.changerMenu(nouveauMenu);
	}

}
