package map;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import main.Fenetre;
import main.Lecteur;
import map.Event.Direction;
import utilitaire.graphismes.Graphismes;

/**
 * Transition pour les changements de Map.
 */
public enum Transition {
	AUCUNE("aucune"), DEFILEMENT("defilement"), ROND("rond");
	
	private final static int DUREE_TRANSITION = Lecteur.DUREE_FRAME;
	private final static int DEMI_DIAGONALE = (int) Math.sqrt(Fenetre.LARGEUR_ECRAN*Fenetre.LARGEUR_ECRAN/4+Fenetre.HAUTEUR_ECRAN*Fenetre.HAUTEUR_ECRAN/4);
	
	private final String nom;
	public BufferedImage captureDeLaMapPrecedente;
	/** Direction de la transition si défilement */
	public int direction;
	/** Centre de la Transition circulaire */
	public int xHerosAvant,  xHerosApres, yHerosAvant, yHerosApres;
	
	/**
	 * Constructeur explicite
	 * @param nom de la Transition
	 */
	Transition(final String nom){
		this.nom = nom;
	}
	
	/**
	 * Retrouver une Transition à partir de son nom.
	 * @param nom de la Transition cherchée
	 * @return Transition qui porte ce nom
	 */
	public final static Transition parNom(final String nom) {
		for (Transition transition : Transition.values()) {
			if (transition.nom.equals(nom)) {
				return transition;
			}
		}
		return AUCUNE;
	}

	public BufferedImage calculer(BufferedImage ecranNouvelleMap, int frame) {
		BufferedImage resultat;
		if (frame < DUREE_TRANSITION) {
			switch (this) {
			case DEFILEMENT:
				int xTransitionNouvelleMap = 0;
				int yTransitionNouvelleMap = 0;
				int xTransitionAncienneMap = 0;
				int yTransitionAncienneMap = 0;
				// La direction du héros donne le sens de la Transition
				switch (this.direction) {
				case Direction.BAS:
					yTransitionNouvelleMap = Fenetre.HAUTEUR_ECRAN - Fenetre.HAUTEUR_ECRAN * frame/DUREE_TRANSITION;
					yTransitionAncienneMap = yTransitionNouvelleMap - Fenetre.HAUTEUR_ECRAN;
					break;
				case Direction.HAUT:
					yTransitionNouvelleMap = - Fenetre.HAUTEUR_ECRAN + Fenetre.HAUTEUR_ECRAN * frame/DUREE_TRANSITION;
					yTransitionAncienneMap = yTransitionNouvelleMap + Fenetre.HAUTEUR_ECRAN;
					break;
				case Direction.DROITE:
					xTransitionNouvelleMap = Fenetre.LARGEUR_ECRAN - Fenetre.LARGEUR_ECRAN * frame/DUREE_TRANSITION;
					xTransitionAncienneMap = xTransitionNouvelleMap - Fenetre.LARGEUR_ECRAN;
					break;
				case Direction.GAUCHE:
					xTransitionNouvelleMap = - Fenetre.LARGEUR_ECRAN + Fenetre.LARGEUR_ECRAN * frame/DUREE_TRANSITION;
					xTransitionAncienneMap = xTransitionNouvelleMap + Fenetre.LARGEUR_ECRAN;
					break;
				}
				resultat = Graphismes.ecranColore(Color.BLACK);
				resultat = Graphismes.superposerImages(resultat, ecranNouvelleMap, xTransitionNouvelleMap, yTransitionNouvelleMap);
				resultat = Graphismes.superposerImages(resultat, captureDeLaMapPrecedente, xTransitionAncienneMap, yTransitionAncienneMap);
				
				return resultat;
			case ROND:
				int x, y, largeur, hauteur;
				if (frame <= DUREE_TRANSITION/2) {
					// Rond qui rétrécit
					resultat = Graphismes.clonerUneImage(captureDeLaMapPrecedente);
					Graphics2D g2d = resultat.createGraphics();
					g2d.setColor(Color.black);
					
					int ratio = DEMI_DIAGONALE*frame/(DUREE_TRANSITION/2);
					x = this.xHerosAvant - DEMI_DIAGONALE + ratio;
					y = this.yHerosAvant - DEMI_DIAGONALE + ratio;
					largeur = 2*(DEMI_DIAGONALE - ratio);
					hauteur = 2*(DEMI_DIAGONALE - ratio);
					
					Area rectangle = new Area(new Rectangle(0,0,Fenetre.LARGEUR_ECRAN,Fenetre.HAUTEUR_ECRAN));
					Area ellipse = new Area(new Ellipse2D.Float(x,y,largeur,hauteur));
					rectangle.subtract(ellipse);
					g2d.fill(rectangle);
					g2d.dispose();
					
					return resultat;
				} else {
					// Rond qui grandit
					resultat = Graphismes.clonerUneImage(ecranNouvelleMap);
					Graphics2D g2d = resultat.createGraphics();
					g2d.setColor(Color.black);
					
					int ratio = DEMI_DIAGONALE*frame/(DUREE_TRANSITION/2) - DEMI_DIAGONALE;
					x = this.xHerosApres - ratio;
					y = this.yHerosApres - ratio;
					largeur = 2*ratio;
					hauteur = 2*ratio;
					Area rectangle = new Area(new Rectangle(0,0,Fenetre.LARGEUR_ECRAN,Fenetre.HAUTEUR_ECRAN));
					Area ellipse = new Area(new Ellipse2D.Float(x,y,largeur,hauteur));
					rectangle.subtract(ellipse);
					g2d.fill(rectangle);
					g2d.dispose();
					
					return resultat;
				}
			default:
				//TODO
			}
		}
		return ecranNouvelleMap;
	}
	
}
