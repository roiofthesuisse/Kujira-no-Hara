package map.positionInitiale;

import main.Main;
import map.Transition;
import map.Event.Direction;

/**
 * Une certaine fa�on de calculer la position du Heros a son arriv�e sur la Map.
 */
public class PositionInitialeParAdjacence extends PositionInitiale {
	final int directionDebutHeros;
	final int decalageDebutHeros;
	final int xHerosMapPrecedente;
	final int yHerosMapPrecedente;
	
	/**
	 * Constructeur explicite
	 * @param decalageDebutHeros d�calage (en nombre de cases) du Heros par rapport a l'ancienne Map
	 * @param directionDebutHeros direction initiale du Heros
	 * @param xHerosMapPrecedente Coordonnee x (en pixels) du Heros sur la Map pr�c�dente
	 * @param yHerosMapPrecedente Coordonnee y (en pixels) du Heros sur la Map pr�c�dente
	 */
	public PositionInitialeParAdjacence(final int decalageDebutHeros, final int directionDebutHeros, 
			final int xHerosMapPrecedente, final int yHerosMapPrecedente) {
		this.decalageDebutHeros = decalageDebutHeros; 
		this.directionDebutHeros = directionDebutHeros;
		this.xHerosMapPrecedente = xHerosMapPrecedente;
		this.yHerosMapPrecedente = yHerosMapPrecedente;
	}
	
	@Override
	public final int[] calculer(final int largeurMap, final int hauteurMap, final Transition transition) {
		final int[] resultat = new int[3];
		switch (this.directionDebutHeros) {
		case Direction.HAUT:
			resultat[0] = this.xHerosMapPrecedente + decalageDebutHeros*Main.TAILLE_D_UN_CARREAU;
			resultat[1] = (hauteurMap-1)*Main.TAILLE_D_UN_CARREAU;
			break;
		case Direction.BAS:
			resultat[0] = this.xHerosMapPrecedente + decalageDebutHeros*Main.TAILLE_D_UN_CARREAU;
			resultat[1] = 0;
			break;
		case Direction.GAUCHE:
			resultat[0] = (largeurMap-1)*Main.TAILLE_D_UN_CARREAU;
			resultat[1] = this.yHerosMapPrecedente + decalageDebutHeros*Main.TAILLE_D_UN_CARREAU;
			break;
		case Direction.DROITE:
			resultat[0] = 0;
			resultat[1] = this.yHerosMapPrecedente + decalageDebutHeros*Main.TAILLE_D_UN_CARREAU;
			break;
		default:
			LOG.error("Direction inconnue !");
			break;
		}
		LOG.debug("Position intiale du Heros sur la nouvelle map : "+resultat[0]+";"+resultat[1]+" direction : "+resultat[2]);
		return resultat;
	}
}