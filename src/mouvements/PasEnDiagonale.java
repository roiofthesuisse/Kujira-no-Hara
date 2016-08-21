package mouvements;

import java.util.HashMap;

import map.Event;

/**
 * Déplacer un Event d'un pas en diagonale
 */
public class PasEnDiagonale extends Mouvement {
	
	int directionVerticale;
	int directionHorizontale;
	public Avancer pixelVertical;
	public Avancer pixelHorizontal;

	/**
	 * Constructeur explicite
	 * @param directionVerticale : composante verticale de la diagonale
	 * @param directionHorizontale : composante horizontale de la diagonale 
	 */
	public PasEnDiagonale(int directionVerticale, int directionHorizontale) {
		this.directionVerticale = directionVerticale;
		this.directionHorizontale = directionHorizontale;
		this.etapes = 4;
		this.pixelVertical = new Avancer(directionVerticale,1);
		this.pixelHorizontal = new Avancer(directionHorizontale,1);
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public PasEnDiagonale(final HashMap<String, Object> parametres) {
		this( (int) parametres.get("directionVerticale"),
			  (int) parametres.get("directionHorizontale"));
	}
	
	/** 
	 * Applique l'effet du Mouvement sur la Map et les Events.
	 * Puis incrémente le compteur "ceQuiAEteFait".
	 * @param event subissant le Mouvement
	 */
	@Override
	public final void calculDuMouvement(final Event event) {
		event.deplacementForce.mouvements.add(pixelHorizontal);
		event.deplacementForce.mouvements.add(pixelVertical);
		pixelVertical.calculDuMouvement(event);
		pixelHorizontal.calculDuMouvement(event);
		pixelHorizontal.executerLeMouvement(event.deplacementForce);
		pixelVertical.executerLeMouvement(event.deplacementForce);
		this.ceQuiAEteFait += 1;
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
	public boolean mouvementPossible() {
		final Event event = this.deplacement.getEventADeplacer();
		pixelVertical.deplacement = event.deplacementForce;
		pixelHorizontal.deplacement = event.deplacementForce;
		pixelVertical.deplacement.idEventADeplacer = event.id;
		pixelHorizontal.deplacement.idEventADeplacer = event.id;
		return (pixelHorizontal.mouvementPossible() && pixelVertical.mouvementPossible());
	}

	@Override
	protected void terminerLeMouvementSpecifique(Event event) {
		// rien
	}

	@Override
	protected void ignorerLeMouvementSpecifique(Event event) {
		// rien
		
	}

	@Override
	public String toString() {
		return "Un pas en diagonale vers "+this.directionVerticale+" et "+this.directionHorizontale;
	}

	@Override
	public int getDirectionImposee() {
		return -1;
	}
	
}
