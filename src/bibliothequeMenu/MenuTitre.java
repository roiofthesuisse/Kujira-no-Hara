package bibliothequeMenu;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import menu.AllerVersUnAutreMenu;
import menu.ElementDeMenu;
import menu.LecteurMenu;
import menu.Menu;
import menu.QuitterJeu;
import menu.Texte;

public class MenuTitre extends Menu {
	
	public MenuTitre(LecteurMenu lecteur){
		this.lecteur = lecteur;
		this.textes = new ArrayList<Texte>();
		Texte demarrer = new Texte("Démarrer", 290, 320, 0, true, 0, null, new AllerVersUnAutreMenu(new MenuNouvellePartie(this.lecteur)), this);
		Texte bonus = new Texte("Bonus",290,350,0,true,0, null, null, this);
		Texte quitter = new Texte("Quitter",290,380,0,true,0,null,new QuitterJeu(),this);
		this.textes.add(demarrer);
		this.textes.add(bonus);
		this.textes.add(quitter);
		selectionner(demarrer);
		
		this.elements = new ArrayList<ElementDeMenu>();
		
		//afficher l'image de fond du menu-titre
		
		try {
			BufferedImage imageFond;
			imageFond = ImageIO.read(new File("./ressources/Graphics/Titles/ecran-titre kujira immudelki.png"));
			ElementDeMenu fond = new ElementDeMenu(imageFond,0,0,false,this);
			elements.add(fond);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void quitter() {
		this.lecteur.fenetre.dispose();
	}

}
