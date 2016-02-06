package bibliothequeMenu;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import commandesMenu.RevenirAuJeu;
import menu.Element;
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
		
		
		
		//elements
		try {
			final Element courrier = new Element("Icons", "menu lettre icon.png", 85, 165, true, null, null, this);
			this.elements.add(courrier);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			final Element quetes = new Element("Icons", "cristal de baleine icon.png", 85, 240, true, null, null, this);
			this.elements.add(quetes);
		} catch (Exception e) {
			e.printStackTrace();
		}
		final Element argent = new Element(75, 315, LARGEUR_ELEMENT_PAR_DEFAUT, HAUTEUR_ELEMENT_PAR_DEFAUT, true, null, null, this);
		this.elements.add(argent);
		try {
			final Element objets = new Element("Icons", "menu coffre icon.png", 85, 390, true, null, null, this);
			this.elements.add(objets);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			final Element flecheGauche = new Element("Pictures", "fleche gauche.png", 10, 235, true, null, null, this);
			this.elements.add(flecheGauche);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			final Element flecheDroite = new Element("Pictures", "fleche droite.png", 610, 235, true, null, null, this);
			this.elements.add(flecheDroite);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		final Element perle1 = new Element(320, 160, LARGEUR_ELEMENT_PAR_DEFAUT, HAUTEUR_ELEMENT_PAR_DEFAUT, true, null, null, this);
		this.elements.add(perle1);
		final Element perle2 = new Element(425, 160, LARGEUR_ELEMENT_PAR_DEFAUT, HAUTEUR_ELEMENT_PAR_DEFAUT, true, null, null, this);
		this.elements.add(perle2);
		final Element perle3 = new Element(485, 240, LARGEUR_ELEMENT_PAR_DEFAUT, HAUTEUR_ELEMENT_PAR_DEFAUT, true, null, null, this);
		this.elements.add(perle3);
		final Element perle4 = new Element(320, 310, LARGEUR_ELEMENT_PAR_DEFAUT, HAUTEUR_ELEMENT_PAR_DEFAUT, true, null, null, this);
		this.elements.add(perle4);
		final Element perle5 = new Element(425, 310, LARGEUR_ELEMENT_PAR_DEFAUT, HAUTEUR_ELEMENT_PAR_DEFAUT, true, null, null, this);
		this.elements.add(perle5);
		final Element perle6 = new Element(260, 240, LARGEUR_ELEMENT_PAR_DEFAUT, HAUTEUR_ELEMENT_PAR_DEFAUT, true, null, null, this);
		this.elements.add(perle6);
		
		selectionner(perle1);
	}

}
