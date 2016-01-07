package menu;

import java.awt.Color;
import java.awt.image.BufferedImage;

import main.Lecteur;

/**
 * Tout ce qui est Sélectionnable (surlignage jaune) dans un Menu ou un Message.
 */
public abstract class Selectionnable {
	//constantes
	public static final int CONTOUR = 16;
	
	public Menu menu;
	public Boolean selectionnable = true;
	public Boolean selectionne = false;
	public ComportementElementDeMenu comportementSelection;
	public ComportementElementDeMenu comportementConfirmation;
	public BufferedImage image;
	public int x;
	public int y;
	
	/**
	 * Lorsqu'on survole l'élément, il peut déclencher une action.
	 */
	public abstract void executerLeComportementALArrivee();
	
	/**
	 * Lorsqu'il est sélectionné, le Sélectionnable est surligné en jaune.
	 * @return image contenant le surlignage jaune adapté au Sélectionnable
	 */
	public final BufferedImage creerImageDeSelection() {
		final int largeur = image.getWidth() + 2*ElementDeMenu.CONTOUR;
		final int hauteur = image.getHeight() + 2*ElementDeMenu.CONTOUR;
		final BufferedImage selection = new BufferedImage(largeur, hauteur, Lecteur.TYPE_DES_IMAGES);
		for (int i = 0; i<largeur; i++) {
			for (int j = 0; j<hauteur; j++) {
				int r, g, b, a, r1, r2, g1, g2, b1, b2, a1, a2;
				//couleur du vide
				r = 0;
				g = 0;
				b = 0;
				a = 0;
				//couleur au centre de la sélection
				r1 = 255;
				g1 = 255;
				b1 = 120;
				a1 = 175;
				//couleur à l'extérieur de la sélection
				r2 = 255;
				g2 = 150;
				b2 = 0;
				a2 = 0; 
				double rate = 0.0, hypotenuse = 0.0;
				//calcul du taux "rate" d'éloignement avec le centre de la sélection
				if (i>=ElementDeMenu.CONTOUR && i<=largeur-ElementDeMenu.CONTOUR) {
					//centre centre
					if (j>=ElementDeMenu.CONTOUR && j<=hauteur-ElementDeMenu.CONTOUR) {
						rate = 1.0;
					}
					//centre haut
					if (j<ElementDeMenu.CONTOUR) {
						rate = (double) (j) / (double) (ElementDeMenu.CONTOUR);
					}
					//centre bas
					if (j>hauteur-ElementDeMenu.CONTOUR) {
						rate = (double) (hauteur-j) / (double) ElementDeMenu.CONTOUR;
					}
				} else {
					if (i<ElementDeMenu.CONTOUR) {
						//gauche centre
						if (j>=ElementDeMenu.CONTOUR && j<=hauteur-ElementDeMenu.CONTOUR) {
							rate = (double) (i) / (double) ElementDeMenu.CONTOUR;
						}
						//gauche haut
						if (j<ElementDeMenu.CONTOUR) {
							hypotenuse = Math.sqrt( Math.pow(i-ElementDeMenu.CONTOUR, 2) + Math.pow(j-ElementDeMenu.CONTOUR, 2) );
						}
						//gauche bas
						if (j>hauteur-ElementDeMenu.CONTOUR) {
							hypotenuse = Math.sqrt( Math.pow(i-ElementDeMenu.CONTOUR, 2) + Math.pow(j-(hauteur-ElementDeMenu.CONTOUR), 2) );
						}
					} else {
						if (i>largeur-ElementDeMenu.CONTOUR) {
							//droite centre
							if (j>=ElementDeMenu.CONTOUR && j<=hauteur-ElementDeMenu.CONTOUR) {
								rate = (double) (largeur-i) / (double) ElementDeMenu.CONTOUR;
							}
							//droite haut
							if (j<ElementDeMenu.CONTOUR) {
								hypotenuse = Math.sqrt( Math.pow(i-(largeur-ElementDeMenu.CONTOUR), 2) + Math.pow(j-ElementDeMenu.CONTOUR, 2) );
							}
							//droite bas
							if (j>hauteur-ElementDeMenu.CONTOUR) {
								hypotenuse = Math.sqrt( Math.pow(i-(largeur-ElementDeMenu.CONTOUR), 2) + Math.pow(j-(hauteur-ElementDeMenu.CONTOUR), 2) );
							}
						}
					}
				}
				if (hypotenuse!=0) {
					if (hypotenuse>ElementDeMenu.CONTOUR) {
						rate = 0;
					} else {
						rate = 1.0-hypotenuse/(double) ElementDeMenu.CONTOUR;
					}
				}
				//calcul de la couleur en fonction du taux "rate" d'éloignement du centre de la sélection
				r = (int) (r1*rate+r2*(1-rate));
				g = (int) (g1*rate+g2*(1-rate));
				b = (int) (b1*rate+b2*(1-rate));
				a = (int) (a1*rate+a2*(1-rate));
				final Color couleur = new Color(r, g, b, a);
				selection.setRGB(i, j, couleur.getRGB());
			}
		}
		return selection;
	}
	
	/**
	 * Valider ce choix
	 */
	public final void confirmer() {
		if (this.comportementConfirmation != null) {
			this.comportementConfirmation.executer();
		}
	}
}
