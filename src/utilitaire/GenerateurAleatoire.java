package utilitaire;

import java.util.Random;

/**
 * Pour générer des nombres aléatoires
 */
public class GenerateurAleatoire extends Random {
	private static final long serialVersionUID = 1L;
	
	private static GenerateurAleatoire instance = new GenerateurAleatoire();
	
	/**
	 * Générer un nombre aléatoire
	 * @param a borne inférieure incluse
	 * @param b borne supérieure exclue
	 * @return nombre aléatoire entre a (inclus) et b (exclu)
	 */
	public static final int nombreEntre(final int a, final int b) {
		return instance.nextInt(b-a)+a;
	}

}
