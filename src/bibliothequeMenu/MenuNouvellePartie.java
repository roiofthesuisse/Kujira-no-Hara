package bibliothequeMenu;

import java.util.ArrayList;

import commandes.OuvrirNouvellePartie;
import main.Commande;
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
		this.textes.add(phrase);
		
		final ArrayList<Commande> commandes1 = new ArrayList<Commande>();
		commandes1.add( new OuvrirNouvellePartie() );
		final Texte choix1 = new Texte("1", 290, 330, true, null, commandes1, this);
		this.textes.add(choix1);
		
		final ArrayList<Commande> commandes2 = new ArrayList<Commande>();
		commandes2.add( new OuvrirNouvellePartie() );
		final Texte choix2 = new Texte("2", 290, 360, true, null, commandes2, this);
		this.textes.add(choix2);
		
		//sélectionner un Elément
		selectionner(choix1);
		
		//elements
		//...
	}

}
