package jeu.courrier;

/**
 * Où en est la lettre?
 */
public enum EtatCourrier {
	PAS_ENVOYEE("pas envoyee"), 
	ENVOYEE_PAS_REPONDUE("envoyee pas repondue"), 
	ENVOYEE_REPONDUE("envoyee repondue"), 
	SPAM("spam");
	
	final String nom;
	
	/**
	 * Constructeur explicite
	 * @param nom de l'etat du courrier
	 */
	EtatCourrier(final String nom) {
		this.nom = nom;
	}
	
	/**
	 * Obtenir un etat de courrier par son nom.
	 * @param nom de l'etat
	 * @return etat du courrier
	 */
	public static EtatCourrier parNom(final String nom) {
		for (EtatCourrier etat : EtatCourrier.values()) {
			if (etat.nom.equals(nom)) {
				return etat;
			}
		}
		return null;
	}
}
