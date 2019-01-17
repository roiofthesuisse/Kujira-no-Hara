package jeu.courrier;

/**
 * Adresse pour envoyer du courrier à un personnage du jeu.
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
}
