package map;

import java.awt.image.BufferedImage;

/**
 * Effet météorologique sur la Map
 */
public abstract class Meteo {
	
	/** différentes sortes d'intempéries */
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
			return TypeDeMeteo.RIEN;
		}
	}
	
	/**
	 * Obtenir le type de cette Météo.
	 * @return élément de l'énumération TypeDeMeteo
	 */
	public abstract TypeDeMeteo getType();
	public int intensite;
	
	/**
	 * Fabriquer l'image représentant l'effet Météo.
	 * @param numeroFrame numéro de la frame actuelle du LecteurMap
	 * @return image de l'effet Météo à superposer à l'écran
	 */
	public abstract BufferedImage calculerImage(int numeroFrame);
	
	/**
	 * Vérifier si deux Météos sont identiques.
	 * @param m1 une météo
	 * @param m2 une autre météo
	 * @return si elles sont équivalentes
	 */
	public static boolean verifierSiIdentiques(final Meteo m1, final Meteo m2) {
		if (m1 == null && m2 == null) {
			return true;
		}
		if (m1 == null && m2 != null) {
			return false;
		}
		if (m1 != null && m2 == null) {
			return false;
		}
		if (!m1.getType().equals(m2.getType())) {
			return false;
		}
		if (m1.intensite != m2.intensite) {
			return false;
		}
		return true;
	}
}
