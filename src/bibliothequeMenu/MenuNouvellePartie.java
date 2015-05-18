package bibliothequeMenu;

import java.util.ArrayList;

import menu.ElementDeMenu;
import menu.LecteurMenu;
import menu.Menu;
import menu.OuvrirNouvellePartie;
import menu.Texte;

public class MenuNouvellePartie extends Menu {

	public MenuNouvellePartie(LecteurMenu lecteur){
		this.lecteur = lecteur;
		this.textes = new ArrayList<Texte>();
		Texte phrase = new Texte("tadam", 290,300, 0, false, 0, null, null, this);
		Texte choix1 = new Texte("1", 290,330, 0, true, 0, null, new OuvrirNouvellePartie(), this);
		Texte choix2 = new Texte("2", 290,360, 0, true, 0, null, new OuvrirNouvellePartie(), this);
		this.textes.add(phrase);
		this.textes.add(choix1);
		this.textes.add(choix2);
		selectionner(choix1);
		
		this.elements = new ArrayList<ElementDeMenu>();
	}
	
	@Override
	public void quitter() {
		this.lecteur.changerMenu(new MenuTitre(this.lecteur));
	}

}
