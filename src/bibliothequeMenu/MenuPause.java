package bibliothequeMenu;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import commandesMenu.RevenirAuJeu;
import menu.ElementDeMenu;
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
			final ElementDeMenu courrier = new ElementDeMenu("Icons", "menu lettre icon.png", 85, 165, true, null, null, this);
			this.elements.add(courrier);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			final ElementDeMenu quetes = new ElementDeMenu("Icons", "cristal de baleine icon.png", 85, 240, true, null, null, this);
			this.elements.add(quetes);
		} catch (Exception e) {
			e.printStackTrace();
		}
		final ElementDeMenu argent = new ElementDeMenu(75, 315, LARGEUR_ELEMENT_PAR_DEFAUT, HAUTEUR_ELEMENT_PAR_DEFAUT, true, null, null, this);
		this.elements.add(argent);
		try {
			final ElementDeMenu objets = new ElementDeMenu("Icons", "menu coffre icon.png", 85, 390, true, null, null, this);
			this.elements.add(objets);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			final ElementDeMenu flecheGauche = new ElementDeMenu("Pictures", "fleche gauche.png", 10, 235, true, null, null, this);
			this.elements.add(flecheGauche);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			final ElementDeMenu flecheDroite = new ElementDeMenu("Pictures", "fleche droite.png", 610, 235, true, null, null, this);
			this.elements.add(flecheDroite);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		final ElementDeMenu perle1 = new ElementDeMenu(320, 160, LARGEUR_ELEMENT_PAR_DEFAUT, HAUTEUR_ELEMENT_PAR_DEFAUT, true, null, null, this);
		this.elements.add(perle1);
		final ElementDeMenu perle2 = new ElementDeMenu(425, 160, LARGEUR_ELEMENT_PAR_DEFAUT, HAUTEUR_ELEMENT_PAR_DEFAUT, true, null, null, this);
		this.elements.add(perle2);
		final ElementDeMenu perle3 = new ElementDeMenu(485, 240, LARGEUR_ELEMENT_PAR_DEFAUT, HAUTEUR_ELEMENT_PAR_DEFAUT, true, null, null, this);
		this.elements.add(perle3);
		final ElementDeMenu perle4 = new ElementDeMenu(320, 310, LARGEUR_ELEMENT_PAR_DEFAUT, HAUTEUR_ELEMENT_PAR_DEFAUT, true, null, null, this);
		this.elements.add(perle4);
		final ElementDeMenu perle5 = new ElementDeMenu(425, 310, LARGEUR_ELEMENT_PAR_DEFAUT, HAUTEUR_ELEMENT_PAR_DEFAUT, true, null, null, this);
		this.elements.add(perle5);
		final ElementDeMenu perle6 = new ElementDeMenu(260, 240, LARGEUR_ELEMENT_PAR_DEFAUT, HAUTEUR_ELEMENT_PAR_DEFAUT, true, null, null, this);
		this.elements.add(perle6);
		
		selectionner(perle1);
	}

}
