package bibliothequeMenu;

import java.util.ArrayList;

import menu.ElementDeMenu;
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
	 * @param lecteur de Menu qui va lire celui-ci
	 */
	public MenuNouvellePartie(final LecteurMenu lecteur) {
		this.lecteur = lecteur;
		this.textes = new ArrayList<Texte>();
		final Texte phrase = new Texte("tadam", 290, 300, 0, false, 0, null, null, this);
		final Texte choix1 = new Texte("1", 290, 330, 0, true, 0, null, new OuvrirNouvellePartie(), this);
		final Texte choix2 = new Texte("2", 290, 360, 0, true, 0, null, new OuvrirNouvellePartie(), this);
		this.textes.add(phrase);
		this.textes.add(choix1);
		this.textes.add(choix2);
		selectionner(choix1);
		
		this.elements = new ArrayList<ElementDeMenu>();
	}
	
	@Override
	public final void quitter() {
		this.lecteur.changerMenu(new MenuTitre(this.lecteur));
	}

}
