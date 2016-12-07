package menu;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import conditions.Condition;
import jeu.Objet;
import main.Commande;
import utilitaire.graphismes.Graphismes;

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
	 * @param id identifiant de l'ElementDeMenu
	 * @throws IOException 
	 */
	public Image(final String dossierImage, final String nomImage, final int x, final int y, 
			final ArrayList<Condition> conditions, final boolean selectionnable, 
			final ArrayList<Commande> comportementSelection, final ArrayList<Commande> comportementConfirmation, 
			final int id) throws IOException {
		this(
				Graphismes.ouvrirImage(dossierImage, nomImage), 
				x, 
				y, 
				conditions, 
				selectionnable, 
				comportementSelection, 
				comportementConfirmation, 
				id
		);
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
	 * @param id identifiant de l'ElementDeMenu
	 */
	//TODO supprimer
	public Image(final int x, final int y, final int largeur, final int hauteur, final boolean selectionnable, final ArrayList<Commande> comportementSelection, final ArrayList<Commande> comportementConfirmation, final int id) {
		this(new int[]{largeur, hauteur}, x, y, null, selectionnable, comportementSelection, comportementConfirmation, id);
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
	 * @param id identifiant de l'ElementDeMenu
	 */
	private Image(final Object apparence, final int x, final int y, final ArrayList<Condition> conditions, final boolean selectionnable, final ArrayList<Commande> comportementSelection, final ArrayList<Commande> comportementConfirmation, final int id) {
		super(id, selectionnable, x, y, comportementSelection, comportementConfirmation);
		
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
		this.conditions = conditions;
		deselectionner();
	}
	
	/**
	 * Constructeur explicite (basé sur un Objet).
	 * Il faut posséder l'Objet pour que l'Image s'affiche.
	 * L'Image est l'icône de l'Objet.
	 * @param objet représenté par cette Image dans le Menu
	 * @param x position x à l'écran
	 * @param y position y à l'écran
	 * @param selectionnable peut-on le sélectionner ?
	 * @param id identifiant de l'ElementDeMenu
	 */
	public Image(final Objet objet, final int x, final int y, final boolean selectionnable, final int id) {
		this(objet.getIcone(), x, y, objet.getConditions(), selectionnable, objet.getComportementSelection(), objet.getComportementConfirmation(), id);
		for (Commande commande : comportementSelection) {
			commande.element = this;
		}
		for (Commande commande : comportementConfirmation) {
			commande.element = this;
		}
	}

	
	/**
	 * Lancer le comportement de l'Element à la sélection ou à l'annulation
	 * @param keycode numéro de la touche pressée lorsque l'Elément est sélectionné
	 */
	public void comportementSiTouchePressee(final Integer keycode) {
		
	}
}
