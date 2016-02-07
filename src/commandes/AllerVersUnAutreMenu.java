package commandes;

import java.util.ArrayList;

import main.Commande;
import main.Fenetre;
import map.LecteurMap;
import menu.ElementDeMenu;
import menu.LecteurMenu;
import menu.Menu;

/**
 * Passer à un autre Menu
 */
public class AllerVersUnAutreMenu implements CommandeMenu {
	public Menu nouveauMenu;
	private ElementDeMenu element;
	
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
		final LecteurMap lecteurMapMemorise = this.getElement().menu.lecteur.lecteurMapMemorise;
		final LecteurMenu nouveauLecteur = new LecteurMenu(Fenetre.getFenetre(), this.nouveauMenu, lecteurMapMemorise);
		nouveauLecteur.changerMenu();
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
