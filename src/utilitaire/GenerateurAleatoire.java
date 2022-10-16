package utilitaire;

import java.util.Random;

/**
 * Pour g�n�rer des nombres al�atoires
 */
public class GenerateurAleatoire extends Random {
	private static final long serialVersionUID = 1L;
	
	private static GenerateurAleatoire instance = new GenerateurAleatoire();
	
	/**
	 * G�n�rer un nombre al�atoire
	 * @param a borne inf�rieure incluse
	 * @param b borne superieure exclue
	 * @return nombre al�atoire entre a (inclus) et b (exclu)
	 */
	public static final int nombreEntre(final int a, final int b) {
		return instance.nextInt(b-a)+a;
	}

}
