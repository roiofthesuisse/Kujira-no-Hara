package utilitaire.graphismes;

/**
 * Façon dont les images sont superposées.
 */
public enum ModeDeFusion {
	NORMAL("normal"), ADDITION("addition"), SOUSTRACTION("soustraction");
	
	public String nom;
	
	/**
	 * Constructeur explicite
	 * @param nom du mode de fusion
	 */
	ModeDeFusion(final String nom) {
		this.nom = nom;
	}
	
	/**
	 * Obtenir le mode de fusion à partir de son nom
	 * @param nom du mode de fusion
	 * @return mode de fusion qui porte ce nom
	 */
	public static ModeDeFusion parNom(final Object nom) {
		for (ModeDeFusion mode : ModeDeFusion.values()) {
			if (mode.nom.equals(nom)) {
				return mode;
			}
		}
		return ModeDeFusion.NORMAL;
	}
}