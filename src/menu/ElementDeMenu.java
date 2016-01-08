package menu;

import java.awt.image.BufferedImage;

/**
 * Un Menu est composé d'Eléments de Menu
 */
public class ElementDeMenu extends Selectionnable {
	
	/**
	 * Constructeur explicite
	 * @param image l'Elément de Menu peut être une image
	 * @param x position x à l'écran
	 * @param y position y à l'écran
	 * @param selectionnable peut-on le sélectionner ?
	 * @param menu auquel appartient l'Elément
	 */
	public ElementDeMenu(final BufferedImage image, final int x, final int y, final Boolean selectionnable, final Menu menu) {
		this.menu = menu;
		this.image = image;
		this.x = x;
		this.y = y;
		this.selectionnable = selectionnable;
		this.selectionne = false;
	}
	
	/**
	 * Lancer le comportement de l'Elément au survol
	 */
	@Override
	public void executerLeComportementALArrivee() {
		this.selectionne = true;
	}
	
	/**
	 * Lancer le comportement de l'Element à la sélection ou à l'annulation
	 * @param keycode numéro de la touche pressée lorsque l'Elément est sélectionné
	 */
	public void comportementSiTouchePressee(final Integer keycode) {
		
	}
}
