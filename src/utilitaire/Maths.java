package utilitaire;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Classe utilitaire qui contient des fonctions mathématiques.
 */
public abstract class Maths {
	private static final Logger LOG = LogManager.getLogger(Maths.class);
	public static final int NOMBRE_PREMIER1 = 7;
	public static final int NOMBRE_PREMIER2 = 31;
	public static final int NOMBRE_PREMIER3 = 41;
	public static final int POURCENTS = 100;
	
	/** Générateur de nombres aléatoires */
	public final static Random generateurAleatoire = new Random();
	
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
		return a*POURCENTS/b;
	}
	
	/** 
	 * Inégalités possibles pour comparer des valeurs 
	 */
	public enum Inegalite {
		PLUS_OU_AUTANT(">="), PLUS_STRICTEMENT(">"), MOINS_OU_AUTANT("<="), MOINS_STRICTEMENT("<"), AUTANT("=="), DIFFERENT("!=");
		
		public String symbole;
		
		/**
		 * Constructeur explicite
		 * @param symbole mathématique de comparaison
		 */
		Inegalite(final String symbole) {
			this.symbole = symbole;
		}
		
		/**
		 * Accesseur statique
		 * @param symbole mathématique de comparaison
		 * @return une des Inégalités possibles
		 */
		public static Inegalite getInegalite(final String symbole) {
			for (Inegalite inegalite : Inegalite.values()) {
				if (inegalite.symbole.equals(symbole)) {
					return inegalite;
				}
			}
			LOG.error("Cette inegalité n'a pas été trouvée : "+symbole);
			return null;
		}
		
		/**
		 * Effectuer la comparaison mathématique sur deux valeurs.
		 * @param valeur1 à comparer
		 * @param valeur2 à comparer
		 * @return résultat de la comparaison
		 */
		public boolean comparer(final double valeur1, final double valeur2) {
			switch(this) {
			case AUTANT:
				return valeur1 == valeur2;
			case PLUS_OU_AUTANT:
				return valeur1 >= valeur2;
			case PLUS_STRICTEMENT:
				return valeur1 > valeur2;
			case MOINS_STRICTEMENT:
				return valeur1 < valeur2;
			case MOINS_OU_AUTANT:
				return valeur1 <= valeur2;
			case DIFFERENT:
				return valeur1 != valeur2;
			default:
				LOG.error("Inégalité inconnue : " + this.symbole);
				return false;
			}
		}
	}

	/**
	 * Maximum
	 * @param nombres en entrée
	 * @return plus grand des nombres
	 */
	public static int max(final int... nombres) {
		int max = nombres[0];
		for (int i : nombres) {
			if (i>max) {
				max = i;
			}
		}
		return max;
	}
	
	/**
	 * Minimum
	 * @param nombres en entrée
	 * @return plus petit des nombres
	 */
	public static int min(final int... nombres) {
		int min = nombres[0];
		for (int i : nombres) {
			if (i<min) {
				min = i;
			}
		}
		return min;
	}
	
	/**
	 * Puissance approximative, calculée rapidement
	 * @param x à mettre à la puissance y
	 * @param y exposant de x
	 * @return x^y à peu près
	 */
	public static double pow(final double x, final double y) {
		final int partieEntiere = (int) y;
		final double partieDecimale = y - partieEntiere;
		double res = 1;
		for (int i = 0; i<partieEntiere; i++ ) {
			res *= x;
		}
		return res * (1.0-partieDecimale) + res*x * partieDecimale; //moyenne pondérée entre puissance entière précédente et puissance entière suivante
	}
}
