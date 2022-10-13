package utilitaire.graphismes;

/**
 * Fa�on dont les images sont superpos�es.
 */
public enum ModeDeFusion {
	/** Recouvrir en utilisant les couleurs de l'image */
	NORMAL("normal"),
	/** Recouvrir en utilisant les couleurs du n�gatif l'image */
	NEGATIF("negatif"),
	/** Eclaircir en utilisant les couleurs de l'image */
	ADDITION("addition"),
	/** Eclaircir en utilisant les couleurs du n�gatif de l'image */
	ADDITION_NEGATIF("addition negatif"), 
	/** Assombrir en utilisant les couleurs du n�gatif de l'image */
	SOUSTRACTION("soustraction"),
	/** Assombrir en conservant les couleurs que sur l'image */
	SOUSTRACTION_NEGATIF("soustraction negatif"), 
	/** Assombrir ou �clairer (selon la distance au gris m�dian) */
	TOPKEK("topkek"),
	/** Pour modifier le ton de l'ecran */
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
	 * Obtenir le mode de fusion a partir de son nom
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