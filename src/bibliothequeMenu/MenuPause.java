package bibliothequeMenu;

import java.util.ArrayList;

import menu.ElementDeMenu;
import menu.LecteurMenu;
import menu.Menu;
import menu.RevenirAuJeu;
import menu.Texte;

public class MenuPause extends Menu {
	
	public MenuPause(LecteurMenu lecteur){
		this.lecteur = lecteur;
		this.textes = new ArrayList<Texte>();
		Texte retour = new Texte("Revenir au jeu", 290, 320, 0, true, 0, null, new RevenirAuJeu(), this);
		this.textes.add(retour);
		selectionner(retour);
		
		this.elements = new ArrayList<ElementDeMenu>();
	}

	@Override
	public void quitter() {
		this.lecteur.fenetre.dispose();
	}

}
