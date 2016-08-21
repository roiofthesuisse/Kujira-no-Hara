package menu;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import main.Commande;
import main.Lecteur;

/**
 * Tout Elément constitutif d'un Menu : Image, Texte, Liste...
 * Un Elément de Menu est éventuellement sélectionnable (surlignage jaune).
 */
public abstract class ElementDeMenu {
	//constantes
	public static final int CONTOUR = 16;
	private static final int COULEUR_CENTRE_SELECTION_R = 255;
	private static final int COULEUR_CENTRE_SELECTION_G = 255;
	private static final int COULEUR_CENTRE_SELECTION_B = 120;
	private static final int COULEUR_CENTRE_SELECTION_A = 175;
	private static final int COULEUR_CONTOUR_SELECTION_R = 255;
	private static final int COULEUR_CONTOUR_SELECTION_G = 150;
	private static final int COULEUR_CONTOUR_SELECTION_B = 0;
	private static final int COULEUR_CONTOUR_SELECTION_A = 0;
	
	public Menu menu;
	public final int id;
	public final boolean selectionnable;
	public boolean selectionne = false;
	protected final ArrayList<Commande> comportementSelection;
	protected final ArrayList<Commande> comportementConfirmation;
	/** l'élément de Menu peut être une image */
	public BufferedImage image;
	/** surlignage de l'image lors de la Sélection */
	public BufferedImage imageDeSelection = null;
	public int x;
	public int y;
	public int hauteur;
	public int largeur;
	
	/**
	 * Constructeur pour obliger l'affectation
	 * @param id identifiant de l'ElementDeMenu
	 */
	protected ElementDeMenu(final int id, final boolean selectionnable, final int x, final int y, ArrayList<Commande> comportementSelection,
	final ArrayList<Commande> comportementConfirmation) {
		this.id = id;
		this.selectionnable = selectionnable;
		this.x = x;
		this.y = y;
		this.comportementSelection = comportementSelection;
		this.comportementConfirmation = comportementConfirmation;
	}
	
	public final void selectionner() {
		this.selectionne = true;
		//TODO désélectionner le précédent ?
		
		executerLeComportementALArrivee();
	}
	
	public final void deselectionner() {
		this.selectionne = false;
	}
	
	/**
	 * Lorsqu'on survole l'élément, il peut déclencher une action.
	 */
	private final void executerLeComportementALArrivee() {
		if ( comportementSelection!=null && comportementSelection.size()>0) {
			int i = 0;
			for (Commande commande : comportementSelection) {
				i = commande.executer(i, comportementSelection);
			}
		}
	}
	
	/**
	 * Lorsqu'il est sélectionné, le Sélectionnable est surligné en jaune.
	 * @return image contenant le surlignage jaune adapté au Sélectionnable
	 */
	public final BufferedImage creerImageDeSelection() {
		if (this.imageDeSelection == null) { //ne calculer qu'une seule fois l'image
			int larg;
			int haut;
			if (this.image!=null) {
				larg = this.image.getWidth() + 2*Image.CONTOUR;
				haut = this.image.getHeight() + 2*Image.CONTOUR;
			} else {
				larg = this.largeur + 2*Image.CONTOUR;
				haut = this.hauteur + 2*Image.CONTOUR;
			}
			final BufferedImage selection = new BufferedImage(larg, haut, Lecteur.TYPE_DES_IMAGES);
			for (int i = 0; i<larg; i++) {
				for (int j = 0; j<haut; j++) {
					int r, g, b, a, r1, r2, g1, g2, b1, b2, a1, a2;
					//couleur du vide
					r = 0;
					g = 0;
					b = 0;
					a = 0;
					//couleur au centre de la sélection
					r1 = COULEUR_CENTRE_SELECTION_R;
					g1 = COULEUR_CENTRE_SELECTION_G;
					b1 = COULEUR_CENTRE_SELECTION_B;
					a1 = COULEUR_CENTRE_SELECTION_A;
					//couleur à l'extérieur de la sélection
					r2 = COULEUR_CONTOUR_SELECTION_R;
					g2 = COULEUR_CONTOUR_SELECTION_G;
					b2 = COULEUR_CONTOUR_SELECTION_B;
					a2 = COULEUR_CONTOUR_SELECTION_A; 
					double rate = 0.0, hypotenuse = 0.0;
					//calcul du taux "rate" d'éloignement avec le centre de la sélection
					if (i>=Image.CONTOUR && i<=larg-Image.CONTOUR) {
						//centre centre
						if (j>=Image.CONTOUR && j<=haut-Image.CONTOUR) {
							rate = 1.0;
						}
						//centre haut
						if (j<Image.CONTOUR) {
							rate = (double) (j) / (double) (Image.CONTOUR);
						}
						//centre bas
						if (j>haut-Image.CONTOUR) {
							rate = (double) (haut-j) / (double) Image.CONTOUR;
						}
					} else {
						if (i<Image.CONTOUR) {
							//gauche centre
							if (j>=Image.CONTOUR && j<=haut-Image.CONTOUR) {
								rate = (double) (i) / (double) Image.CONTOUR;
							}
							//gauche haut
							if (j<Image.CONTOUR) {
								hypotenuse = Math.sqrt( Math.pow(i-Image.CONTOUR, 2) + Math.pow(j-Image.CONTOUR, 2) );
							}
							//gauche bas
							if (j>haut-Image.CONTOUR) {
								hypotenuse = Math.sqrt( Math.pow(i-Image.CONTOUR, 2) + Math.pow(j-(haut-Image.CONTOUR), 2) );
							}
						} else {
							if (i>larg-Image.CONTOUR) {
								//droite centre
								if (j>=Image.CONTOUR && j<=haut-Image.CONTOUR) {
									rate = (double) (larg-i) / (double) Image.CONTOUR;
								}
								//droite haut
								if (j<Image.CONTOUR) {
									hypotenuse = Math.sqrt( Math.pow(i-(larg-Image.CONTOUR), 2) + Math.pow(j-Image.CONTOUR, 2) );
								}
								//droite bas
								if (j>haut-Image.CONTOUR) {
									hypotenuse = Math.sqrt( Math.pow(i-(larg-Image.CONTOUR), 2) + Math.pow(j-(haut-Image.CONTOUR), 2) );
								}
							}
						}
					}
					if (hypotenuse!=0) {
						if (hypotenuse>Image.CONTOUR) {
							rate = 0;
						} else {
							rate = 1.0-hypotenuse/(double) Image.CONTOUR;
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
			this.imageDeSelection = selection;
		}
		return this.imageDeSelection;
	}
	
	/**
	 * Valider ce choix
	 */
	public final void confirmer() {
		if (this.comportementConfirmation != null && this.comportementConfirmation.size()>0) {
			int i = 0;
			for (Commande commande : this.comportementConfirmation) {
				i = commande.executer(i, comportementConfirmation);
			}
		}
	}
	
}
