package map.positionInitiale;

import main.Main;
import map.Transition;
import utilitaire.Maths;

/**
 * Une certaine fa�on de calculer la position du H�ros a son arriv�e sur la Map.
 */
public class PositionInitialeExhaustive extends PositionInitiale {
	final int directionDebutHeros;
	final int xHerosMapPrecedente;
	final int yHerosMapPrecedente;
	final int xHerosNouvelleMap;
	final int yHerosNouvelleMap;
	
	/**
	 * Constructeur explicite
	 * @param directionDebutHeros direction initiale du H�ros
	 * @param xHerosMapPrecedente coordonn�e x (en pixels) du H�ros sur la Map pr�c�dente
	 * @param yHerosMapPrecedente coordonn�e y (en pixels) du H�ros sur la Map pr�c�dente
	 * @param xHerosNouvelleMap coordonn�e x (en carreaux) du H�ros sur la nouvelle Map 
	 * @param yHerosNouvelleMap coordonn�e y (en carreaux) du H�ros sur la nouvelle Map 
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
			// La transition DEFILEMENT est plus jolie en tenant compte de l'�cart du H�ros
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
		LOG.debug("Position intiale du h�ros sur la nouvelle map : "+resultat[0]+";"+resultat[1]+" direction : "+resultat[2]);
		return resultat;
	}
}