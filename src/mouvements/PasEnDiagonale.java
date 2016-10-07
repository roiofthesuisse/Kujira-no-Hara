package mouvements;

import java.util.HashMap;

import main.Fenetre;
import map.Event;

/**
 * D�placer un Event d'un pas en diagonale
 */
public class PasEnDiagonale extends Mouvement {
	int directionVerticale;
	int directionHorizontale;
	/** partie du mouvement effectu�e pendant 1 frame selon X */
	public Avancer pixelVertical;
	/** partie du mouvement effectu�e pendant 1 frame selon Y */
	public Avancer pixelHorizontal;

	/**
	 * Constructeur explicite
	 * @param directionVerticale composante verticale de la diagonale
	 * @param directionHorizontale composante horizontale de la diagonale 
	 */
	public PasEnDiagonale(final int directionVerticale, final int directionHorizontale) {
		this.directionVerticale = directionVerticale;
		this.directionHorizontale = directionHorizontale;
		this.etapes = Fenetre.TAILLE_D_UN_CARREAU;
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public PasEnDiagonale(final HashMap<String, Object> parametres) {
		this( (int) parametres.get("directionVerticale"),
			  (int) parametres.get("directionHorizontale"));
	}
	
	/** 
	 * Applique l'effet du Mouvement sur la Map et les Events.
	 * Puis incr�mente le compteur "ceQuiAEteFait".
	 * @param event subissant le Mouvement
	 */
	@Override
	public final void calculDuMouvement(final Event event) {
		//on applique les cons�quences des deux Mouvements fictifs
		pixelVertical.calculDuMouvement(event);
		pixelHorizontal.calculDuMouvement(event);
		
		this.ceQuiAEteFait += event.vitesseActuelle;
	}

	@Override
	protected void reinitialiserSpecifique() {
		// rien	
	}

	/**
	 * Le mouvement dans cette Direction est-il possible ?
	 * @return si le mouvement est possible oui ou non
	 */
	@Override
	public final boolean mouvementPossible() {
		final Event event = this.deplacement.getEventADeplacer();
		
		//on d�compose ce Mouvement en deux Mouvements fictifs pour v�rifier s'il est possible
		this.pixelVertical = new Avancer(directionVerticale, event.vitesseActuelle);
		this.pixelHorizontal = new Avancer(directionHorizontale, event.vitesseActuelle);
		pixelVertical.deplacement = event.deplacementForce;
		pixelHorizontal.deplacement = event.deplacementForce;
		pixelVertical.deplacement.idEventADeplacer = event.id;
		pixelHorizontal.deplacement.idEventADeplacer = event.id;
		
		return (pixelHorizontal.mouvementPossible() && pixelVertical.mouvementPossible());
	}

	@Override
	protected final void terminerLeMouvementSpecifique(final Event event) {
		event.avance = false;
	}

	@Override
	protected final void ignorerLeMouvementSpecifique(final Event event) {
		if (!event.animeALArretActuel && !event.avancaitALaFramePrecedente && !event.avance) {
			//l'event ne bouge plus depuis 2 frames, on arr�te son animation
			event.animation = 0; 
		}
	}

	@Override
	public final String toString() {
		return "Un pas en diagonale vers "+this.directionVerticale+" et "+this.directionHorizontale;
	}

	@Override
	public final int getDirectionImposee() {
		if (this.deplacement.getEventADeplacer().direction == this.directionVerticale) {
			//si l'Event regarde d�j� dans une des deux directions de la diagonale, on prend celle-ci
			return this.directionVerticale;
		} else {
			//sinon par d�faut on prend la composante horizontale de la diagonale
			return this.directionHorizontale;
		}
	}
	
}