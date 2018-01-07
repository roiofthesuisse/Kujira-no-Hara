package utilitaire.graphismes;

/**
 * Façon dont les images sont superposées.
 */
public enum ModeDeFusion {
	/** Recouvrir en utilisant les couleurs de l'image */
	NORMAL("normal"),
	/** Recouvrir en utilisant les couleurs du négatif l'image */
	NEGATIF("negatif"),
	/** Eclaircir en utilisant les couleurs de l'image */
	ADDITION("addition"),
	/** Eclaircir en utilisant les couleurs du négatif de l'image */
	ADDITION_NEGATIF("addition negatif"), 
	/** Assombrir en utilisant les couleurs du négatif de l'image */
	SOUSTRACTION("soustraction"),
	/** Assombrir en conservant les couleurs que sur l'image */
	SOUSTRACTION_NEGATIF("soustraction negatif"), 
	/** Assombrir ou éclairer (selon la distance au gris médian) */
	TOPKEK("topkek"),
	/** Pour modifier le ton de l'écran */
	TON_DE_L_ECRAN("ton de l'ecran");
	
	public final String nom;
	
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