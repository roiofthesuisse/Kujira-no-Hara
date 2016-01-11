package bibliothequeMenu;

import java.util.ArrayList;

import menu.ElementDeMenu;
import menu.Menu;
import menu.QuitterJeu;
import menu.RevenirAuJeu;
import menu.Texte;

/**
 * Menu sur lequel on arrive lorsqu'on appuie sur "pause" en jeu.
 */
public class MenuPause extends Menu {
	
	/**
	 * Constructeur explicite
	 */
	public MenuPause() {
		this.textes = new ArrayList<Texte>();
		final Texte retour = new Texte("Revenir au jeu", 290, 320, 0, true, 0, null, new RevenirAuJeu(), this);
		this.textes.add(retour);
		final Texte quitter = new Texte("Quitter le jeu", 290, 350, 0, true, 0, null, new QuitterJeu(), this);
		this.textes.add(quitter);
		
		selectionner(retour);
		
		this.elements = new ArrayList<ElementDeMenu>();
	}

	@Override
	public final void quitter() {
		this.lecteur.fenetre.dispose();
	}

}
