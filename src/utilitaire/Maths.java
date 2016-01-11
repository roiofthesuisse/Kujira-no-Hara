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
	public static int modulo(int a, final int b) {
		if (b<=0) {
			return -1;
		}
		while (a<0) {
			a += b;
		}
		while (a>=b) {
			a -= b;
		}
		return a;
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
