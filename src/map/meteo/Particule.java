package map.meteo;

/**
 * Particule météorologique
 */
public class Particule {
	public int resteAVivre;
	public int x0;
	public int y0;
	public int type;
	
	/**
	 * Constructeur explicite
	 * @param x0 position horizontale finale de la particule
	 * @param y0 position verticale finale de la particule
	 * @param resteAVivre temps qu'il reste à vivre à la particule à l'écran (en frames)
	 * @param type un effet météorologique peut comporter plusieurs types de particules
	 */
	Particule(final int x0, final int y0, final int resteAVivre, final int type) {
		this.x0 = x0;
		this.y0 = y0;
		this.resteAVivre = resteAVivre;
		this.type = type;
	}
}
