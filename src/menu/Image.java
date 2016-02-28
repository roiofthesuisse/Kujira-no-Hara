package menu;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import conditions.Condition;
import jeu.Objet;
import main.Commande;

/**
 * Un Menu est composé d'Images.
 */
public class Image extends ElementDeMenu {

	/** Conditions d'affichage */
	public final ArrayList<Condition> conditions;
	
	/**
	 * Constructeur explicite (avec image comme apparence)
	 * @param dossierImage nom du dossier où se trouve l'image
	 * @param nomImage nom de l'image
	 * @param x position x à l'écran
	 * @param y position y à l'écran
	 * @param conditions d'affichage
	 * @param selectionnable peut-on le sélectionner ?
	 * @param comportementSelection comportement au survol
	 * @param comportementConfirmation comportement à la validation
	 * @param menu auquel appartient l'Elément
	 * @throws IOException 
	 */
	public Image(final String dossierImage, final String nomImage, final int x, final int y, final ArrayList<Condition> conditions, final boolean selectionnable, final ArrayList<Commande> comportementSelection, final ArrayList<Commande> comportementConfirmation, final Menu menu) throws IOException {
		this(ImageIO.read(new File(".\\ressources\\Graphics\\"+dossierImage+"\\"+nomImage)),
				x, y, conditions, selectionnable, comportementSelection, comportementConfirmation, menu);
	}
	
	/**
	 * Constructeur explicite (avec rectangle vide comme apparence)
	 * @param x position x à l'écran
	 * @param y y position y à l'écran
	 * @param largeur de l'Elément
	 * @param hauteur de l'Elément
	 * @param selectionnable peut-on le sélectionner ?
	 * @param comportementSelection comportement au survol
	 * @param comportementConfirmation comportement à la validation
	 * @param menu auquel appartient l'Elément
	 */
	//TODO supprimer
	public Image(final int x, final int y, final int largeur, final int hauteur, final boolean selectionnable, final ArrayList<Commande> comportementSelection, final ArrayList<Commande> comportementConfirmation, final Menu menu) {
		this(new int[]{largeur, hauteur}, x, y, null, selectionnable, comportementSelection, comportementConfirmation, menu);
	}
	
	/**
	 * Constructeur explicite
	 * @param apparence de l'Elément de Menu : une BufferedImage ou bien un tableau (largeur,hauteur) représentant une image vide
	 * @param x position x à l'écran
	 * @param y position y à l'écran
	 * @param conditions d'affichage
	 * @param selectionnable peut-on le sélectionner ?
	 * @param comportementSelection comportement au survol
	 * @param comportementConfirmation comportement à la validation
	 * @param menu auquel appartient l'Image
	 */
	private Image(final Object apparence, final int x, final int y, final ArrayList<Condition> conditions, final boolean selectionnable, final ArrayList<Commande> comportementSelection, final ArrayList<Commande> comportementConfirmation, final Menu menu) {
		this.menu = menu;
		
		if (apparence instanceof BufferedImage) {
			//l'Elément a une image comme apparence
			this.image = (BufferedImage) apparence;
			this.largeur = this.image.getWidth();
			this.hauteur = this.image.getHeight();
		} else {
			//l'Elément est un rectangle vide
			this.image = null;
			final int[] dimensionsImageVide = (int[]) apparence;
			this.largeur = dimensionsImageVide[0];
			this.hauteur = dimensionsImageVide[1];
		}
		this.x = x;
		this.y = y;
		this.conditions = conditions;
		this.selectionnable = selectionnable;
		this.comportementSelection = comportementSelection;
		this.comportementConfirmation = comportementConfirmation;
		
		this.selectionne = false;
	}
	
	/**
	 * Constructeur explicite (basé sur un Objet).
	 * Il faut posséder l'Objet pour que l'Image s'affiche.
	 * L'Image est l'icône de l'Objet.
	 * @param objet représenté par cette Image dans le Menu
	 * @param x position x à l'écran
	 * @param y position y à l'écran
	 * @param selectionnable peut-on le sélectionner ?
	 * @param menu auquel appartient l'Image
	 */
	public Image(final Objet objet, final int x, final int y, final boolean selectionnable, final Menu menu) {
		this(objet.getIcone(), x, y, objet.getConditions(), selectionnable, objet.getComportementSelection(), objet.getComportementConfirmation(), menu);
	}

	/**
	 * Lancer le comportement de l'Elément au survol.
	 */
	@Override
	public final void executerLeComportementALArrivee() {
		this.selectionne = true;
	}
	
	/**
	 * Lancer le comportement de l'Element à la sélection ou à l'annulation
	 * @param keycode numéro de la touche pressée lorsque l'Elément est sélectionné
	 */
	public void comportementSiTouchePressee(final Integer keycode) {
		
	}
}
