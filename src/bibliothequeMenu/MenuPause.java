package bibliothequeMenu;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import conditions.Condition;
import conditions.ConditionObjetPossede;
import jeu.Objet;
import menu.Image;
import menu.Menu;
import menu.Texte;

/**
 * Menu sur lequel on arrive lorsqu'on appuie sur "pause" en jeu.
 */
public class MenuPause extends Menu {
	
	/**
	 * Constructeur explicite
	 */
	public MenuPause() {
		try {
			this.fond = ImageIO.read(new File(".\\ressources\\Graphics\\Pictures\\menu statut.png"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		//textes
		final Texte titre = new Texte("STATUT", 152, 20, Texte.TAILLE_GRANDE, false, null, null, this);
		this.textes.add(titre);
		
		final Texte description = new Texte("", 27, 66, false, null, null, this);
		this.textes.add(description);
		
		this.xTexteDescriptif = 30;
		this.xTexteDescriptif = 65;
		this.largeurTexteDescriptif = 475;

		
		//elements
		try {
			final Image courrier = new Image("Icons", "menu lettre icon.png", 85, 165, null, true, null, null, this);
			this.images.add(courrier);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			final Image quetes = new Image("Icons", "cristal de baleine icon.png", 85, 240, null, true, null, null, this);
			this.images.add(quetes);
		} catch (Exception e) {
			e.printStackTrace();
		}
		final Image argent = new Image(75, 315, LARGEUR_ELEMENT_PAR_DEFAUT, HAUTEUR_ELEMENT_PAR_DEFAUT, true, null, null, this);
		this.images.add(argent);
		try {
			final Image objets = new Image("Icons", "menu coffre icon.png", 85, 390, null, true, null, null, this);
			this.images.add(objets);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			final Image flecheGauche = new Image("Pictures", "fleche gauche.png", 10, 235, null, true, null, null, this);
			this.images.add(flecheGauche);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			final Image flecheDroite = new Image("Pictures", "fleche droite.png", 610, 235, null, true, null, null, this);
			this.images.add(flecheDroite);
		} catch (IOException e) {
			e.printStackTrace();
		}

		final Image perle1 = new Image(Objet.objetsDuJeuHash.get("Perle rouge"), 320, 160, true, this);
		this.images.add(perle1);
		final Image perle2 = new Image(425, 160, LARGEUR_ELEMENT_PAR_DEFAUT, HAUTEUR_ELEMENT_PAR_DEFAUT, true, null, null, this);
		this.images.add(perle2);
		final Image perle3 = new Image(485, 240, LARGEUR_ELEMENT_PAR_DEFAUT, HAUTEUR_ELEMENT_PAR_DEFAUT, true, null, null, this);
		this.images.add(perle3);
		final Image perle4 = new Image(320, 310, LARGEUR_ELEMENT_PAR_DEFAUT, HAUTEUR_ELEMENT_PAR_DEFAUT, true, null, null, this);
		this.images.add(perle4);
		final Image perle5 = new Image(425, 310, LARGEUR_ELEMENT_PAR_DEFAUT, HAUTEUR_ELEMENT_PAR_DEFAUT, true, null, null, this);
		this.images.add(perle5);
		final Image perle6 = new Image(260, 240, LARGEUR_ELEMENT_PAR_DEFAUT, HAUTEUR_ELEMENT_PAR_DEFAUT, true, null, null, this);
		this.images.add(perle6);
		
		selectionner(perle1);
	}

}
