package jeu.courrier;

/**
 * Adresse pour envoyer du courrier a un personnage du jeu.
 */
public enum Adresse {
	MATHUSALEM("mathusalem"), 
	MAGICIEN_DOSE("magicien dose"), 
	MERCATOR("mercator"), 
	POESIE("poesie"), 
	FIOLIN("fiolin");
	
	public final String nomPersonnage;
	
	/**
	 * Constructeur explicite
	 * @param nomPersonnage nom du personnage avec lequel on correspond
	 */
	Adresse(final String nomPersonnage) {
		this.nomPersonnage = nomPersonnage;
	}

	/**
	 * Obtenir une adresse par le nom du personnage.
	 * @param nom du personnage
	 * @return adresse
	 */
	public static Adresse parNom(final String nom) {
		for (Adresse adresse : Adresse.values()) {
			if (adresse.nomPersonnage.equals(nom)) {
				return adresse;	
			}
		}
		return null;
	}
}
