package commandes;

import java.util.ArrayList;

import main.Commande;
import main.Fenetre;
import map.LecteurMap;
import menu.LecteurMenu;
import menu.Menu;

/**
 * Passer à un autre Menu
 */
public class AllerVersUnAutreMenu extends Commande implements CommandeMenu {
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
		final LecteurMap lecteurMapMemorise = this.element.menu.lecteur.lecteurMapMemorise;
		final LecteurMenu nouveauLecteur = new LecteurMenu(Fenetre.getFenetre(), this.nouveauMenu, lecteurMapMemorise);
		nouveauLecteur.changerMenu();
	}

	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		this.executer();
		return curseurActuel+1;
	}

}
