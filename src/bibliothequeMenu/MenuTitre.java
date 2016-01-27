package bibliothequeMenu;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import commandesMenu.AllerVersUnAutreMenu;
import commandesMenu.QuitterJeu;
import menu.ElementDeMenu;
import menu.Menu;
import menu.Texte;

/**
 * Menu sur lequel on arrive en ouvrant le jeu
 */
public class MenuTitre extends Menu {
	
	/**
	 * Constructeur explicite
	 */
	public MenuTitre() {
		final Texte demarrer = new Texte("Démarrer", 290, 320, true, null, new AllerVersUnAutreMenu(new MenuNouvellePartie(this)), this);
		final Texte bonus = new Texte("Bonus", 290, 350, true, null, null, this);
		final Texte quitter = new Texte("Quitter", 290, 380, true, null, new QuitterJeu(), this);
		this.textes.add(demarrer);
		this.textes.add(bonus);
		this.textes.add(quitter);
		selectionner(demarrer);
		
		//afficher l'image de fond du menu-titre
		/*
		try {
			BufferedImage imageFond;
			imageFond = ImageIO.read(new File("./ressources/Graphics/Titles/ecran-titre kujira immudelki.png"));
			final ElementDeMenu fond = new ElementDeMenu(imageFond, 0, 0, false, this);
			elements.add(fond);
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
		
		//elements
		//...
	}

}
