package map.meteo;

/** 
 * Différentes sortes d'intempéries 
 */
public enum TypeDeMeteo {
	PLUIE("pluie"), NEIGE("neige"), RIEN("rien");
	
	private String nom;
	
	/**
	 * Constructeur explicite
	 * @param nom du type d'intempérie
	 */
	TypeDeMeteo(final String nom) {
		this.nom = nom;		
	}
	
	/**
	 * Obtenir un type d'intempérie à partir de son nom.
	 * @param nom du type d'intempérie
	 * @return type d'intempérie
	 */
	public static TypeDeMeteo obtenirParNom(final String nom) {
		for (TypeDeMeteo type : TypeDeMeteo.values()) {
			if (type.nom.equalsIgnoreCase(nom)) {
				return type;
			}
		}
		System.err.println("Effet météorologique inconnu : "+nom);
		return TypeDeMeteo.RIEN;
	}
}