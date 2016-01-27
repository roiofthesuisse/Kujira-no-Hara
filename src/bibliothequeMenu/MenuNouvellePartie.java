package bibliothequeMenu;

import commandesMenu.OuvrirNouvellePartie;
import menu.Menu;
import menu.Texte;

/**
 * Menu pour créer une nouvelle partie.
 */
public class MenuNouvellePartie extends Menu {

	/**
	 * Constructeur explicite
	 * @param menuParent vers lequel revenir si annulation
	 */
	public MenuNouvellePartie(final Menu menuParent) {
		this.menuParent = menuParent;
		
		//textes
		final Texte phrase = new Texte("tadam", 290, 300, false, null, null, this);
		final Texte choix1 = new Texte("1", 290, 330, true, null, new OuvrirNouvellePartie(), this);
		final Texte choix2 = new Texte("2", 290, 360, true, null, new OuvrirNouvellePartie(), this);
		this.textes.add(phrase);
		this.textes.add(choix1);
		this.textes.add(choix2);
		selectionner(choix1);
		
		//elements
		//...
	}

}
