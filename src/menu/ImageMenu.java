package menu;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import conditions.Condition;
import jeu.Objet;
import main.Commande;

/**
 * Un Menu est composé d'Images.
 */
public class ImageMenu extends ElementDeMenu {
	
	/**
	 * Constructeur explicite
	 * @param apparence de l'Elément de Menu : une BufferedImage ou bien un tableau (largeur,hauteur) représentant une image vide
	 * @param x position x à l'écran
	 * @param y position y à l'écran
	 * @param largeurForcee si spécifiée, l'image sera affichée dans un rectangle invisible de telle largeur
	 * @param hauteurForcee si spécifiée, l'image sera affichée dans un rectangle invisible de telle hauteur
	 * @param conditions d'affichage
	 * @param selectionnable peut-on le sélectionner ?
	 * @param comportementSelection comportement au survol
	 * @param comportementConfirmation comportement à la validation
	 * @param id identifiant de l'ElementDeMenu
	 */
	public ImageMenu(final BufferedImage apparence, 
			final int x, final int y, final int largeurForcee, final int hauteurForcee,
			final ArrayList<Condition> conditions, final boolean selectionnable, 
			final ArrayList<Commande> comportementSelection, final ArrayList<Commande> comportementConfirmation, 
			final int id) {
		super(id, selectionnable, x, y, comportementSelection, comportementConfirmation, conditions);
		
		if (apparence instanceof BufferedImage) {
			//l'Elément a une image comme apparence
			this.image = (BufferedImage) apparence;
			this.largeur = largeurForcee>0 ? largeurForcee : this.image.getWidth();
			this.hauteur = hauteurForcee>0 ? hauteurForcee : this.image.getHeight();
		}
		
		//on associe les Commandes à leur ElementDeMenu
		if (comportementSelection != null && comportementSelection.size() > 0) {
			for (Commande commande : comportementSelection) {
				commande.element = this;
			}
		}
		if (comportementConfirmation != null && comportementConfirmation.size() > 0) {
			for (Commande commande : comportementConfirmation) {
				commande.element = this;
			}
		}
		
		this.selectionne = false;
	}
	
	/**
	 * Constructeur explicite (basé sur un Objet).
	 * Il faut posséder l'Objet pour que l'Image s'affiche.
	 * L'Image est l'icône de l'Objet.
	 * @param objet représenté par cette Image dans le Menu
	 * @param x position x à l'écran
	 * @param y position y à l'écran
	 * @param largeurForcee si spécifiée, l'image sera affichée dans un rectangle invisible de telle largeur
	 * @param hauteurForcee si spécifiée, l'image sera affichée dans un rectangle invisible de telle hauteur
	 * @param selectionnable peut-on le sélectionner ?
	 * @param id identifiant de l'ElementDeMenu
	 */
	public ImageMenu(final Objet objet, final int x, final int y, final int largeurForcee, final int hauteurForcee, final boolean selectionnable, final int id) {
		this(objet.getIcone(), x, y, largeurForcee, hauteurForcee, objet.getConditions(), selectionnable, objet.getComportementSelection(), objet.getComportementConfirmation(), id);
		for (Commande commande : comportementSurvol) {
			commande.element = this;
		}
		for (Commande commande : comportementConfirmation) {
			commande.element = this;
		}
	}
	
	@Override
	public BufferedImage getImage(){
		return this.image;
	}

}
