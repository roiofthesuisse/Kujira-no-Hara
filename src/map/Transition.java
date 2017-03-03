package map;

import java.awt.Color;
import java.awt.image.BufferedImage;

import main.Fenetre;
import map.Event.Direction;
import utilitaire.graphismes.Graphismes;

/**
 * Transition pour les changements de Map.
 */
public enum Transition {
	AUCUNE("aucune"), DEFILEMENT("defilement"), ROND("rond");
	
	private final static int DUREE_TRANSITION = 20;
	
	private final String nom;
	public BufferedImage captureDeLaMapPrecedente;
	/** Direction de la transition si défilement */
	public int direction;
	
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
		
		if (frame < DUREE_TRANSITION) {
			switch (this) {
			case DEFILEMENT:
				//TODO
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
				BufferedImage resultat = Graphismes.ecranColore(Color.BLACK);
				resultat = Graphismes.superposerImages(resultat, ecranNouvelleMap, xTransitionNouvelleMap, yTransitionNouvelleMap);
				resultat = Graphismes.superposerImages(resultat, captureDeLaMapPrecedente, xTransitionAncienneMap, yTransitionAncienneMap);
				
				return resultat;
			default:
				//TODO
			}
		}
		return ecranNouvelleMap;
	}
	
}
