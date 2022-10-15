package map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Vitesse d'un Event (en pixels par frame).
 */
public enum Vitesse {
	// Puissances de 2 avec un ratio 25/30
	TRES_LENTE("tres lente", 1),
	LENTE("lente", 2),
    MODEREE("moderee", 3),
	NORMALE("normale", 6),
    RAPIDE("rapide", 13),
    TRES_RAPIDE("tres rapide", 26);
	
	private static final Logger LOG = LogManager.getLogger(Vitesse.class);
	
	public final String nom;
	public final int valeur;
	
	/**
	 * Constructeur explicite
	 * @param nom usuel
	 * @param valeur (en pixels par frame)
	 */
	Vitesse(final String nom, final int valeur) {
		this.nom = nom;
		this.valeur = valeur;
	}
	
	/**
	 * Trouver une vitesse par son nom.
	 * @param nom de la vitesse cherch�e
	 * @return vitesse correspondant
	 */
	public static Vitesse parNom(final String nom) {
		for (Vitesse vitesse : Vitesse.values()) {
			if (vitesse.nom.equals(nom)) {
				return vitesse;
			}
		}
		LOG.error("Vitesse inconnue : "+ (nom == null ? "null" : nom));
		return null;
	}
}
