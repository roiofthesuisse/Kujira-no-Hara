package map.positionInitiale;

import map.Transition;

/**
 * Une certaine façon de calculer la position du Héros à son arrivée sur la Map.
 */
public class PositionInitialeBrute extends PositionInitiale {
	final int xDebutHeros, yDebutHeros;
	final int directionDebutHeros;
	
	/**
	 * Constructeur explicite
	 * @param xDebutHeros position x (en pixels) initiale du Héros
	 * @param yDebutHeros position y (en pixels) initiale du Héros
	 * @param directionDebutHeros direction initiale du Héros
	 */
	public PositionInitialeBrute(final int xDebutHeros, final int yDebutHeros, final int directionDebutHeros) {
		this.xDebutHeros = xDebutHeros;
		this.yDebutHeros = yDebutHeros;
		this.directionDebutHeros = directionDebutHeros;
	}

	@Override
	public final int[] calculer(final int largeurMap, final int hauteurMap, final Transition transition) {
		final int[] resultat = new int[3];
		resultat[0] = this.xDebutHeros;
		resultat[1] = this.yDebutHeros;
		resultat[2] = this.directionDebutHeros;
		LOG.debug("Position intiale du héros sur la nouvelle map : "+resultat[0]+";"+resultat[1]+" direction : "+resultat[2]);
		return resultat;
	}
}
