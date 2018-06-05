package map.positionInitiale;

import main.Main;
import map.Transition;
import utilitaire.Maths;

/**
 * Une certaine façon de calculer la position du Héros à son arrivée sur la Map.
 */
public class PositionInitialeExhaustive extends PositionInitiale {
	final int directionDebutHeros;
	final int xHerosMapPrecedente;
	final int yHerosMapPrecedente;
	final int xHerosNouvelleMap;
	final int yHerosNouvelleMap;
	
	/**
	 * Constructeur explicite
	 * @param directionDebutHeros direction initiale du Héros
	 * @param xHerosMapPrecedente coordonnée x (en pixels) du Héros sur la Map précédente
	 * @param yHerosMapPrecedente coordonnée y (en pixels) du Héros sur la Map précédente
	 * @param xHerosNouvelleMap coordonnée x (en carreaux) du Héros sur la nouvelle Map 
	 * @param yHerosNouvelleMap coordonnée y (en carreaux) du Héros sur la nouvelle Map 
	 */
	public PositionInitialeExhaustive(final int directionDebutHeros, 
			final int xHerosMapPrecedente, final int yHerosMapPrecedente, final int xHerosNouvelleMap,
	final int yHerosNouvelleMap) {
		this.directionDebutHeros = directionDebutHeros;
		this.xHerosMapPrecedente = xHerosMapPrecedente;
		this.yHerosMapPrecedente = yHerosMapPrecedente;
		this.xHerosNouvelleMap = xHerosNouvelleMap;
		this.yHerosNouvelleMap = yHerosNouvelleMap;
	}
	
	@Override
	public final int[] calculer(final int largeurMap, final int hauteurMap, final Transition transition) {
		final int[] resultat = new int[3];
		resultat[0] = this.xHerosNouvelleMap*Main.TAILLE_D_UN_CARREAU;
		resultat[1] = this.yHerosNouvelleMap*Main.TAILLE_D_UN_CARREAU;
		if (Transition.DEFILEMENT.equals(transition)) {
			// La transition DEFILEMENT est plus jolie en tenant compte de l'écart du Héros
			int ecartX = Maths.modulo(this.xHerosMapPrecedente, Main.TAILLE_D_UN_CARREAU);
			int ecartY = Maths.modulo(this.yHerosMapPrecedente, Main.TAILLE_D_UN_CARREAU);
			if (ecartX > Main.TAILLE_D_UN_CARREAU/2) {
				ecartX -= Main.TAILLE_D_UN_CARREAU;
			}
			if (ecartY > Main.TAILLE_D_UN_CARREAU/2) {
				ecartY -= Main.TAILLE_D_UN_CARREAU;
			}
			resultat[0] += ecartX;
			resultat[1] += ecartY;
		}
		resultat[2] = this.directionDebutHeros;
		LOG.debug("Position intiale du héros sur la nouvelle map : "+resultat[0]+";"+resultat[1]+" direction : "+resultat[2]);
		return resultat;
	}
}