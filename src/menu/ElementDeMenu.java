package menu;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Un Menu est composé d'Eléments de Menu
 */
public class ElementDeMenu extends Selectionnable {
	
	/**
	 * Constructeur explicite (avec image comme apparence)
	 * @param dossierImage nom du dossier où se trouve l'image
	 * @param nomImage nom de l'image
	 * @param x position x à l'écran
	 * @param y position y à l'écran
	 * @param selectionnable peut-on le sélectionner ?
	 * @param comportementSelection comportement au survol
	 * @param comportementConfirmation comportement à la validation
	 * @param menu auquel appartient l'Elément
	 * @throws IOException 
	 */
	public ElementDeMenu(final String dossierImage, final String nomImage, final int x, final int y, final boolean selectionnable, final ComportementElementDeMenu comportementSelection, final ComportementElementDeMenu comportementConfirmation, final Menu menu) throws IOException {
		this(ImageIO.read(new File(".\\ressources\\Graphics\\"+dossierImage+"\\"+nomImage)),
				x, y, selectionnable, comportementSelection, comportementConfirmation, menu);
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
	public ElementDeMenu(final int x, final int y, final int largeur, final int hauteur, final boolean selectionnable, final ComportementElementDeMenu comportementSelection, final ComportementElementDeMenu comportementConfirmation, final Menu menu) {
		this(new int[]{largeur, hauteur}, x, y, selectionnable, comportementSelection, comportementConfirmation, menu);
	}
	
	/**
	 * Constructeur explicite
	 * @param apparence de l'Elément de Menu : une BufferedImage ou bien un tableau (largeur,hauteur) représentant une image vide
	 * @param x position x à l'écran
	 * @param y position y à l'écran
	 * @param selectionnable peut-on le sélectionner ?
	 * @param comportementSelection comportement au survol
	 * @param comportementConfirmation comportement à la validation
	 * @param menu auquel appartient l'Elément
	 */
	private ElementDeMenu(final Object apparence, final int x, final int y, final boolean selectionnable, final ComportementElementDeMenu comportementSelection, final ComportementElementDeMenu comportementConfirmation, final Menu menu) {
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
		this.selectionnable = selectionnable;
		this.comportementSelection = comportementSelection;
		this.comportementConfirmation = comportementConfirmation;
		
		this.selectionne = false;
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
