package mouvements;

import java.util.HashMap;

import main.Fenetre;
import map.Event;

/**
 * Déplacer un Event d'un pas en diagonale
 */
public class PasEnDiagonale extends Avancer {
	int directionVerticale;
	int directionHorizontale;
	/** partie du mouvement effectuée pendant 1 frame selon X */
	public Avancer pixelVertical;
	/** partie du mouvement effectuée pendant 1 frame selon Y */
	public Avancer pixelHorizontal;

	/**
	 * Constructeur explicite
	 * @param directionVerticale composante verticale de la diagonale
	 * @param directionHorizontale composante horizontale de la diagonale 
	 * @param nombreDePixels distance parcourue
	 */
	public PasEnDiagonale(final int directionVerticale, final int directionHorizontale, final int nombreDePixels) {
		super(-1, nombreDePixels);
		this.directionVerticale = directionVerticale;
		this.directionHorizontale = directionHorizontale;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public PasEnDiagonale(final HashMap<String, Object> parametres) {
		this( (int) parametres.get("directionVerticale"),
			  (int) parametres.get("directionHorizontale"),
			  (parametres.containsKey("nombreDeCarreaux") ? (int) parametres.get("parametres") : 1) * Fenetre.TAILLE_D_UN_CARREAU
		);
	}
	
	/** 
	 * Applique l'effet du Mouvement sur la Map et les Events.
	 * Puis incrémente le compteur "ceQuiAEteFait".
	 * @param event subissant le Mouvement
	 */
	@Override
	public final void calculDuMouvement(final Event event) {
		//on applique les conséquences des deux Mouvements fictifs
		pixelVertical.calculDuMouvement(event);
		pixelHorizontal.calculDuMouvement(event);
		
		this.ceQuiAEteFait += event.vitesseActuelle;
	}

	/**
	 * Le mouvement dans cette Direction est-il possible ?
	 * @return si le mouvement est possible oui ou non
	 */
	@Override
	public final boolean mouvementPossible() {
		final Event event = this.deplacement.getEventADeplacer();
		
		//on décompose ce Mouvement en deux Mouvements fictifs pour vérifier s'il est possible
		this.pixelVertical = new Avancer(directionVerticale, event.vitesseActuelle);
		this.pixelHorizontal = new Avancer(directionHorizontale, event.vitesseActuelle);
		pixelVertical.deplacement = event.deplacementForce;
		pixelHorizontal.deplacement = event.deplacementForce;
		pixelVertical.deplacement.idEventADeplacer = event.id;
		pixelHorizontal.deplacement.idEventADeplacer = event.id;

		return (pixelHorizontal.mouvementPossible() && pixelVertical.mouvementPossible());
	}

	@Override
	public final String toString() {
		return "Un pas en diagonale vers "+this.directionVerticale+" et "+this.directionHorizontale;
	}

	@Override
	public final int getDirectionImposee() {
		if (this.deplacement.getEventADeplacer().direction == this.directionVerticale) {
			//si l'Event regarde déjà dans une des deux directions de la diagonale, on prend celle-ci
			return this.directionVerticale;
		} else {
			//sinon par défaut on prend la composante horizontale de la diagonale
			return this.directionHorizontale;
		}
	}
	
}