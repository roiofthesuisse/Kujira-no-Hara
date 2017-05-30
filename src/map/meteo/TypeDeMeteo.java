package map.meteo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** 
 * Différentes sortes d'intempéries 
 */
public enum TypeDeMeteo {
	PLUIE("pluie"), NEIGE("neige"), TROCHOIDE("trochoide"), RIEN("rien");
	
	private static final Logger LOG = LogManager.getLogger(TypeDeMeteo.class);
	
	public String nom;
	
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
		LOG.error("Effet météorologique inconnu : "+nom);
		return TypeDeMeteo.RIEN;
	}
}