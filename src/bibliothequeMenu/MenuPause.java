package bibliothequeMenu;

import java.util.ArrayList;

import menu.ElementDeMenu;
import menu.LecteurMenu;
import menu.Menu;
import menu.QuitterJeu;
import menu.RevenirAuJeu;
import menu.Texte;

public class MenuPause extends Menu {
	
	public MenuPause(LecteurMenu lecteur){
		this.lecteur = lecteur;
		
		this.textes = new ArrayList<Texte>();
		Texte retour = new Texte("Revenir au jeu", 290, 320, 0, true, 0, null, new RevenirAuJeu(), this);
		this.textes.add(retour);
		Texte quitter = new Texte("Quitter le jeu", 290, 350, 0, true, 0, null, new QuitterJeu(), this);
		this.textes.add(quitter);
		
		selectionner(retour);
		
		this.elements = new ArrayList<ElementDeMenu>();
	}

	@Override
	public void quitter() {
		this.lecteur.fenetre.dispose();
	}

}
