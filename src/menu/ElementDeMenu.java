package menu;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import conditions.Condition;
import main.Commande;
import utilitaire.graphismes.Graphismes;

/**
 * Tout Elément constitutif d'un Menu : Image, Texte, Liste...
 * Un Elément de Menu est éventuellement sélectionnable (surlignage jaune).
 */
public abstract class ElementDeMenu {
	//constantes
	private static final Logger LOG = LogManager.getLogger(ElementDeMenu.class);
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
	public int id;
	
	/** Cet ElementDeMenu est il Selectionnable ? */
	public final boolean selectionnable;
	/** Cet ElementDeMenu est il selectionné ? */
	public boolean selectionne = false;
	/** Faut-il lire les Commandes de survol ? */
	public boolean executionDesCommandesDeSurvol = false;
	/** Commandes à executer au survol */
	protected final ArrayList<Commande> comportementSurvol;
	/** Curseur de l'execution des commandes à executer au survol */
	public int curseurComportementSurvol = 0;
	/** Faut-il lire les Commandes de confirmation ? */
	public boolean executionDesCommandesDeConfirmation = false;
	/** Commandes à executer à la confirmation */
	protected final ArrayList<Commande> comportementConfirmation;
	/** Curseur de l'execution des commandes à executer à la confirmation */
	public int curseurComportementConfirmation = 0;
	/** L'ElementDeMenu est-il masqué ? */
	public boolean invisible = false;
	
	/** Conditions d'affichage */
	public final ArrayList<Condition> conditions;
	
	/** L'ElementDeMenu appartient à une Liste */
	public Liste liste = null;
	
	/** L'élément de Menu peut être une image */
	protected BufferedImage image;
	/** Surlignage de l'image lors de la Sélection */
	public BufferedImage imageDeSelection = null;
	public int x;
	public int y;
	public int hauteur;
	public int largeur;
	
	/**
	 * Constructeur pour obliger l'affectation
	 * @param id identifiant de l'ElementDeMenu
	 * @param selectionnable peut-on sélectionner cet ElementDeMenu ?
	 * @param x position sur l'écran
	 * @param y position sur l'écran
	 * @param comportementSelection CommandesMenu executées lors du survol
	 * @param comportementConfirmation CommandesMenu executées lors de la confirmation
	 * @param conditions d'affichage
	 */
	protected ElementDeMenu(final int id, final boolean selectionnable, final int x, final int y, 
			final ArrayList<Commande> comportementSelection, final ArrayList<Commande> comportementConfirmation,
			final ArrayList<Condition> conditions) {
		this.id = id;
		this.selectionnable = selectionnable;
		this.x = x;
		this.y = y;
		this.comportementSurvol = comportementSelection;
		this.comportementConfirmation = comportementConfirmation;
		this.conditions = conditions;
	}
	
	/**
	 * Lorsqu'il est sélectionné, le Sélectionnable est surligné en jaune.
	 * @return image contenant le surlignage jaune adapté au Sélectionnable
	 */
	public final BufferedImage creerImageDeSelection() {
		if (this.imageDeSelection == null) { //ne calculer qu'une seule fois l'image
			this.getImage(); //calculer image
			final int larg;
			final int haut;
			//largeur et hauteur sont soit la taille de l'image, soit un rectangle de taille forcée par le JSON
			larg = this.largeur + 2*ImageMenu.CONTOUR;
			haut = this.hauteur + 2*ImageMenu.CONTOUR;
			final BufferedImage selection = new BufferedImage(larg, haut, Graphismes.TYPE_DES_IMAGES);
			for (int i = 0; i<larg; i++) {
				for (int j = 0; j<haut; j++) {
					int r, g, b, a;
					final int r1, r2, g1, g2, b1, b2, a1, a2;
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
					if (i>=ImageMenu.CONTOUR && i<=larg-ImageMenu.CONTOUR) {
						//centre centre
						if (j>=ImageMenu.CONTOUR && j<=haut-ImageMenu.CONTOUR) {
							rate = 1.0;
						}
						//centre haut
						if (j<ImageMenu.CONTOUR) {
							rate = (double) (j) / (double) (ImageMenu.CONTOUR);
						}
						//centre bas
						if (j>haut-ImageMenu.CONTOUR) {
							rate = (double) (haut-j) / (double) ImageMenu.CONTOUR;
						}
					} else {
						if (i<ImageMenu.CONTOUR) {
							//gauche centre
							if (j>=ImageMenu.CONTOUR && j<=haut-ImageMenu.CONTOUR) {
								rate = (double) (i) / (double) ImageMenu.CONTOUR;
							}
							//gauche haut
							if (j<ImageMenu.CONTOUR) {
								hypotenuse = Math.sqrt( Math.pow(i-ImageMenu.CONTOUR, 2) + Math.pow(j-ImageMenu.CONTOUR, 2) );
							}
							//gauche bas
							if (j>haut-ImageMenu.CONTOUR) {
								hypotenuse = Math.sqrt( Math.pow(i-ImageMenu.CONTOUR, 2) + Math.pow(j-(haut-ImageMenu.CONTOUR), 2) );
							}
						} else {
							if (i>larg-ImageMenu.CONTOUR) {
								//droite centre
								if (j>=ImageMenu.CONTOUR && j<=haut-ImageMenu.CONTOUR) {
									rate = (double) (larg-i) / (double) ImageMenu.CONTOUR;
								}
								//droite haut
								if (j<ImageMenu.CONTOUR) {
									hypotenuse = Math.sqrt( Math.pow(i-(larg-ImageMenu.CONTOUR), 2) + Math.pow(j-ImageMenu.CONTOUR, 2) );
								}
								//droite bas
								if (j>haut-ImageMenu.CONTOUR) {
									hypotenuse = Math.sqrt( Math.pow(i-(larg-ImageMenu.CONTOUR), 2) + Math.pow(j-(haut-ImageMenu.CONTOUR), 2) );
								}
							}
						}
					}
					if (hypotenuse!=0) {
						if (hypotenuse>ImageMenu.CONTOUR) {
							rate = 0;
						} else {
							rate = 1.0-hypotenuse/(double) ImageMenu.CONTOUR;
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
	 * Commandes de Menu à executer à la confirmation de l'ElementDeMenu.
	 */
	public void executerLesCommandesDeConfirmation() {
		LOG.info("Execution des commandes de confirmation de l'élément de menu.");
		boolean commandeInstantanee = true;
		int nouvelleValeurDuCurseur;
		try {
			while (commandeInstantanee) {
				final Commande commandeActuelle = this.comportementConfirmation.get(this.curseurComportementConfirmation);
				LOG.debug("Commande de menu executée : "+commandeActuelle.getClass());
				nouvelleValeurDuCurseur = commandeActuelle.executer(this.curseurComportementConfirmation, this.comportementConfirmation);
				commandeInstantanee = (nouvelleValeurDuCurseur != this.curseurComportementConfirmation);
				this.curseurComportementConfirmation = nouvelleValeurDuCurseur;
			}
		} catch (IndexOutOfBoundsException e) {
			//fin de la lecture des commandes
			LOG.trace("Fin de la lecture des commandes de confirmation de l'élément de menu.", e);
			this.curseurComportementConfirmation = 0;
			this.executionDesCommandesDeConfirmation = false; //ne lire qu'une seule fois
		}
	}

	/**
	 * Commandes de Menu à executer au survol de l'ElementDeMenu.
	 */
	public void executerLesCommandesDeSurvol() {
		boolean commandeInstantanee = true;
		int nouvelleValeurDuCurseur;
		try {
			while (commandeInstantanee) {
				final Commande commandeActuelle = this.comportementSurvol.get(this.curseurComportementSurvol);
				nouvelleValeurDuCurseur = commandeActuelle.executer(this.curseurComportementSurvol, this.comportementSurvol);
				commandeInstantanee = (nouvelleValeurDuCurseur != this.curseurComportementSurvol);
				this.curseurComportementSurvol = nouvelleValeurDuCurseur;
			}
		} catch (IndexOutOfBoundsException e) {
			//fin de la lecture des commandes
			LOG.trace("Fin de la lecture des commandes de survol de l'élément de menu.", e);
			this.curseurComportementSurvol = 0;
			this.executionDesCommandesDeSurvol = false;
		}
	}
	
	/**
	 * Faut-il afficher l'Element ? Ses Conditions sont-elles toutes vérifiées ?
	 * @return true s'il faut afficher l'Element, false sinon
	 */
	public final boolean ilFautAfficherCetElement() {
		// L'ElementDeMenu est-il masqué ?
		if (this.invisible) {
			return false;
		}
		
		// Conditions d'affichage
		if (this.conditions==null || this.conditions.size()<=0) {
			//pas de contrainte particulière sur l'affichage
			return true;
		}
		//on essaye toutes les Conditions
		for (Condition condition : this.conditions) {
			if (!condition.estVerifiee()) {
				return false;
			}
		}
		return true;
	}
	
	public abstract BufferedImage getImage();
	
}
