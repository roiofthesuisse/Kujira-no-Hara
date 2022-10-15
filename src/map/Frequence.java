package map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Fr�quence d'animation d'un Event (en frames).
 */
public enum Frequence {
	// Puissances de 2, avec un ratio 30/25
	LA_PLUS_BASSE("la plus basse", 77),
	TRES_BASSE("tres basse", 38),
    BASSE("basse", 19),
    HAUTE("haute", 10),
    TRES_HAUTE("tres haute", 5),
    LA_PLUS_HAUTE("la plus haute", 2);
	
	private static final Logger LOG = LogManager.getLogger(Frequence.class);
	
	public final String nom;
	public final int valeur;
	
	/**
	 * Constructeur explicite
	 * @param nom usuel
	 * @param valeur (en frames)
	 */
	Frequence(final String nom, final int valeur) {
		this.nom = nom;
		this.valeur = valeur;
	}
	
	/**
	 * Trouver une vitesse par son nom.
	 * @param nom de la vitesse cherch�e
	 * @return vitesse correspondant
	 */
	public static Frequence parNom(final String nom) {
		for (Frequence frequence : Frequence.values()) {
			if (frequence.nom.equals(nom)) {
				return frequence;
			}
		}
		LOG.error("Fr�quence inconnue : "+(nom == null ? "null" : nom));
		return null;
	}
}
