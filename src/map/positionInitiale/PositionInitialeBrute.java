package map.positionInitiale;

import map.Transition;

/**
 * Une certaine fa�on de calculer la position du H�ros a son arriv�e sur la Map.
 */
public class PositionInitialeBrute extends PositionInitiale {
	final int xDebutHeros, yDebutHeros;
	final int directionDebutHeros;
	
	/**
	 * Constructeur explicite
	 * @param xDebutHeros position x (en pixels) initiale du H�ros
	 * @param yDebutHeros position y (en pixels) initiale du H�ros
	 * @param directionDebutHeros direction initiale du H�ros
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
		LOG.debug("Position intiale du h�ros sur la nouvelle map : "+resultat[0]+";"+resultat[1]+" direction : "+resultat[2]);
		return resultat;
	}
}
