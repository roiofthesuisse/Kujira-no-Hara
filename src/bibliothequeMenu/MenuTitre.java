package bibliothequeMenu;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import commandes.AllerVersUnAutreMenu;
import commandes.QuitterJeu;
import main.Commande;
import menu.Image;
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
		//Textes du Menu
		final ArrayList<Commande> commandesDemarrer = new ArrayList<Commande>();
		commandesDemarrer.add( new AllerVersUnAutreMenu(new MenuNouvellePartie(this)) );
		final Texte texteDemarrer = new Texte("Démarrer", 290, 320, true, null, commandesDemarrer, this);
		this.textes.add(texteDemarrer);
		
		final Texte texteBonus = new Texte("Bonus", 290, 350, true, null, null, this);
		this.textes.add(texteBonus);
		
		final ArrayList<Commande> commandesQuitter = new ArrayList<Commande>();
		commandesQuitter.add( new QuitterJeu() );
		final Texte texteQuitter = new Texte("Quitter", 290, 380, true, null, commandesQuitter, this);
		this.textes.add(texteQuitter);
		
		//sélectionner un Elément
		selectionner(texteDemarrer);
		
		//afficher l'image de fond du menu-titre
		/*
		try {
			final Image fond = new Image("Titles", "ecran-titre kujira immudelki.png", 0, 0, false, null, null, this);
			this.images.add(fond);
		} catch (IOException e) {
			e.printStackTrace();
		}
		 */
		
		//elements
		//...
	}

}
