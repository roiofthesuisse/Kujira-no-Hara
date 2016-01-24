package bibliothequeMenu;

import main.Fenetre;
import menu.LecteurMenu;
import menu.Menu;
import menu.OuvrirNouvellePartie;
import menu.Texte;

/**
 * Menu pour créer une nouvelle partie.
 */
public class MenuNouvellePartie extends Menu {

	/**
	 * Constructeur explicite
	 */
	public MenuNouvellePartie() {
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
	
	@Override
	public final void quitter() {
		final Menu menuTitre = new MenuTitre();
		final LecteurMenu nouveauLecteur = new LecteurMenu(Fenetre.getFenetre(), menuTitre, null);
		nouveauLecteur.changerMenu();
	}

}
