package utilitaire;

/**
 * Classe utilitaire qui contient des fonctions mathématiques.
 */
public abstract class Maths {
	
	/**
	 * Modulo
	 * @param a nombre à diviser
	 * @param b diviseur
	 * @return reste de la division de a par b
	 */
	public static int modulo(final int a, final int b) {
		int c = a;
		if (b<=0) {
			return -1;
		}
		while (c<0) {
			c += b;
		}
		while (c>=b) {
			c -= b;
		}
		return c;
	}
	
	/**
	 * Pourcentage
	 * @param a partie
	 * @param b tout
	 * @return pourcentage de la partie sur le tout
	 */
	public static long pourcentage(final long a, final long b) {
		return a*100/b;
	}
	
}
