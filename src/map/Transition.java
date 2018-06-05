package map;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import main.Fenetre;
import main.Lecteur;
import main.Main;
import map.Event.Direction;
import utilitaire.graphismes.Graphismes;

/**
 * Transition pour les changements de Map.
 */
public enum Transition {
	AUCUNE("aucune") {
		@Override
		public BufferedImage calculer(BufferedImage ecranNouvelleMap, Map nouvelleMap, int frame) {
			return ecranNouvelleMap;
		}
	},
	DEFILEMENT("defilement") {
		@Override
		public BufferedImage calculer(BufferedImage ecranNouvelleMap, Map nouvelleMap, int frame) {
			BufferedImage resultat;
			if (frame < DUREE_TRANSITION) {
				//transition en cours
				int xTransitionNouvelleMap = 0;
				int yTransitionNouvelleMap = 0;
				int xTransitionAncienneMap = 0;
				int yTransitionAncienneMap = 0;
				int xDeplacementHeros = 0; //décalage à compenser du Héros qui marche sur la nouvelle Map
				int yDeplacementHeros = 0;
				//sens de la Transition
				switch (this.direction) {
				case Direction.BAS:
					yTransitionNouvelleMap = Fenetre.HAUTEUR_ECRAN - Fenetre.HAUTEUR_ECRAN * frame/DUREE_TRANSITION;
					yTransitionAncienneMap = yTransitionNouvelleMap - Fenetre.HAUTEUR_ECRAN;
					xDeplacementHeros = nouvelleMap.xDebutHeros - nouvelleMap.heros.x;
					break;
				case Direction.HAUT:
					yTransitionNouvelleMap = -Fenetre.HAUTEUR_ECRAN + Fenetre.HAUTEUR_ECRAN * frame/DUREE_TRANSITION;
					yTransitionAncienneMap = yTransitionNouvelleMap + Fenetre.HAUTEUR_ECRAN;
					xDeplacementHeros = nouvelleMap.xDebutHeros - nouvelleMap.heros.x;
					break;
				case Direction.DROITE:
					xTransitionNouvelleMap = Fenetre.LARGEUR_ECRAN - Fenetre.LARGEUR_ECRAN * frame/DUREE_TRANSITION;
					xTransitionAncienneMap = xTransitionNouvelleMap - Fenetre.LARGEUR_ECRAN;
					yDeplacementHeros = nouvelleMap.yDebutHeros - nouvelleMap.heros.y;
					break;
				case Direction.GAUCHE:
					xTransitionNouvelleMap = -Fenetre.LARGEUR_ECRAN + Fenetre.LARGEUR_ECRAN * frame/DUREE_TRANSITION;
					xTransitionAncienneMap = xTransitionNouvelleMap + Fenetre.LARGEUR_ECRAN;
					yDeplacementHeros = nouvelleMap.yDebutHeros - nouvelleMap.heros.y;
					break;
				}
				resultat = Graphismes.ecranColore(Color.BLACK);
				resultat = Graphismes.superposerImages(resultat, ecranNouvelleMap, xTransitionNouvelleMap, yTransitionNouvelleMap);
				resultat = Graphismes.superposerImages(resultat, captureDeLaMapPrecedente, xTransitionAncienneMap+xDeplacementHeros, 
						yTransitionAncienneMap+yDeplacementHeros);
				
				return resultat;
			} else {
				//transition terminée
				return ecranNouvelleMap;
			}
		}
	},
	ROND("rond") {
		@Override
		public BufferedImage calculer(BufferedImage ecranNouvelleMap, Map nouvelleMap, int frame) {
			BufferedImage resultat;
			if (frame < DUREE_TRANSITION) {
				//transition en cours
				final int x, y, largeur, hauteur;
				if (frame <= DUREE_TRANSITION/2) {
					// Rond qui rétrécit
					resultat = Graphismes.clonerUneImage(captureDeLaMapPrecedente);
					Graphics2D g2d = resultat.createGraphics();
					g2d.setColor(Color.black);
					
					final int ratio = DEMI_DIAGONALE*frame/(DUREE_TRANSITION/2);
					x = this.xHerosAvant - DEMI_DIAGONALE + ratio;
					y = this.yHerosAvant - DEMI_DIAGONALE + ratio;
					largeur = 2*(DEMI_DIAGONALE - ratio);
					hauteur = 2*(DEMI_DIAGONALE - ratio);
					
					final Area rectangle = new Area(new Rectangle(0,0,Fenetre.LARGEUR_ECRAN,Fenetre.HAUTEUR_ECRAN));
					final Area ellipse = new Area(new Ellipse2D.Float(x,y,largeur,hauteur));
					rectangle.subtract(ellipse);
					g2d.fill(rectangle);
					
					return resultat;
				} else {
					// Rond qui grandit
					resultat = Graphismes.clonerUneImage(ecranNouvelleMap);
					Graphics2D g2d = resultat.createGraphics();
					g2d.setColor(Color.black);
					
					final int ratio = DEMI_DIAGONALE*frame/(DUREE_TRANSITION/2) - DEMI_DIAGONALE;
					x = this.xHerosApres - ratio;
					y = this.yHerosApres - ratio;
					largeur = 2*ratio;
					hauteur = 2*ratio;
					final Area rectangle = new Area(new Rectangle(0, 0, Fenetre.LARGEUR_ECRAN, Fenetre.HAUTEUR_ECRAN));
					final Area ellipse = new Area(new Ellipse2D.Float(x, y, largeur, hauteur));
					rectangle.subtract(ellipse);
					g2d.fill(rectangle);
					
					return resultat;
				}
			} else {
				//transition terminée
				return ecranNouvelleMap;
			}
		}
	};
	
	// Constantes
	public static final int DUREE_TRANSITION = Lecteur.DUREE_FRAME;
	private static final int DEMI_DIAGONALE = (int) Math.sqrt(Fenetre.LARGEUR_ECRAN*Fenetre.LARGEUR_ECRAN/4+Fenetre.HAUTEUR_ECRAN*Fenetre.HAUTEUR_ECRAN/4);
	
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
	Transition(final String nom) {
		this.nom = nom;
	}
	
	/**
	 * Retrouver une Transition à partir de son nom.
	 * @param nom de la Transition cherchée
	 * @return Transition qui porte ce nom
	 */
	public static final Transition parNom(final String nom) {
		for (Transition transition : Transition.values()) {
			if (transition.nom.equalsIgnoreCase(nom)) {
				return transition;
			}
		}
		return AUCUNE;
	}
	
	/**
	 * Calculer l'étape de la Transition en fonction de la frame.
	 * @param ecranNouvelleMap écran de la nouvelle Map
	 * @param nouvelleMap contenant la position intiale du Héros
	 * @param frame du Lecteur de la nouvelle Map
	 * @return image de Transition entre l'ancienne Map et la Nouvelle Map
	 */
	public abstract BufferedImage calculer(BufferedImage ecranNouvelleMap, Map nouvelleMap, int frame);

	/** Trouver la tansition la plus adaptée si aucune n'est précisée
	 * @param xHerosNouvelleMap coordonnée x (en carreaux) du Héros à l'arrivée sur la nouvelle Map
	 * @param yHerosNouvelleMap coordonnée y (en carreaux) du Héros à l'arrivée sur la nouvelle Map
	 * @param tilesetNouvelleMap Tileset de la nouvelle Map
	 * @return type de transition à effectuer
	 */
	public static Transition parDefaut(final int xHerosNouvelleMap, final int yHerosNouvelleMap, 
			final Tileset tilesetNouvelleMap) {
		
		
		return DEFILEMENT;
	}
	
	/**
	 * Calculer la Direction du défilement.
	 * @param xAncienHeros coordonnée x (en pixels) du Héros sur l'ancienne Map
	 * @param yAncienHeros coordonnée y (en pixels) du Héros sur l'ancienne Map
	 * @param xNouveauHeros coordonnée x (en pixels) du Héros sur la nouvelle Map
	 * @param yNouveauHeros coordonnée y (en pixels) du Héros sur la nouvelle Map
	 * @param largeurAncienneMap largeur (en carreaux) de l'ancienne Map
	 * @param hauteurAncienneMap hauteur (en carreaux) de l'ancienne Map
	 * @param largeurNouvelleMap largeur (en carreaux) de la nouvelle Map
	 * @param hauteurNouvelleMap hauteur (en carreaux) de la nouvelle Map
	 * @return direction de la Transition
	 */
	public static int calculerDirectionDefilement(final int xAncienHeros, final int yAncienHeros, final int xNouveauHeros, 
			final int yNouveauHeros, final int largeurAncienneMap, final int hauteurAncienneMap, final int largeurNouvelleMap,
			final int hauteurNouvelleMap) {
		
		// On calcule la direction globale de la transition par un vecteur
		final int xVecteurAncienneMap = xAncienHeros - largeurAncienneMap*Main.TAILLE_D_UN_CARREAU/2;
		final int yVecteurAncienneMap = yAncienHeros - hauteurAncienneMap*Main.TAILLE_D_UN_CARREAU/2;
		final int xVecteurNouvelleMap = xNouveauHeros - largeurNouvelleMap*Main.TAILLE_D_UN_CARREAU/2;
		final int yVecteurNouvelleMap = yNouveauHeros - hauteurNouvelleMap*Main.TAILLE_D_UN_CARREAU/2;
		
		final int deltaX = xVecteurAncienneMap - xVecteurNouvelleMap;
		final int deltaY = yVecteurAncienneMap - yVecteurNouvelleMap;
		if (Math.abs(deltaX) > Math.abs(deltaY)) {
			// La Transition est horizontale
			if (deltaX > 0) {
				return Direction.DROITE;
			} else {
				return Direction.GAUCHE;
			}
		} else {
			// La Transition est verticale
			if (deltaY > 0) {
				return Direction.BAS;
			} else {
				return Direction.HAUT;
			}
		}
	}
	
}
