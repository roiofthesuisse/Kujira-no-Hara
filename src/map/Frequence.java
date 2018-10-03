package map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Fréquence d'animation d'un Event (en frames).
 */
public enum Frequence {
	LA_PLUS_BASSE("la plus basse", 7),
	TRES_BASSE("tres basse", 6),
    BASSE("basse", 5),
    HAUTE("haute", 4),
    TRES_HAUTE("tres haute", 3),
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
	 * @param nom de la vitesse cherchée
	 * @return vitesse correspondant
	 */
	public static Frequence parNom(final String nom) {
		for (Frequence frequence : Frequence.values()) {
			if (frequence.nom.equals(nom)) {
				return frequence;
			}
		}
		LOG.error("Fréquence inconnue : "+(nom == null ? "null" : nom));
		return null;
	}
}
