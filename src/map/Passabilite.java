package map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Une tuile de décor peut être traversable ou solide.
 * Les quatre faces de la tuile n'ont pas forcément la même passabilité :
 * on pourra y entrer par la gauche, mais pas forcément en resortir par la droite.
 */
public enum Passabilite {
	PASSABLE(0),
	GAUCHE_DROITE_HAUT(1), 
	BAS_DROITE_HAUT(2), 
	DROITE_HAUT(3), 
	BAS_GAUCHE_HAUT(4), 
	GAUCHE_HAUT(5), 
	BAS_HAUT(6),
	HAUT(7),
	BAS_GAUCHE_DROITE(8),
	GAUCHE_DROITE(9),
	BAS_DROITE(10),
	DROITE(11),
	BAS_GAUCHE(12),
	GAUCHE(13),
	BAS(14),
	OBSTACLE(15);
	
	private static final Logger LOG = LogManager.getLogger(Passabilite.class);
	private static final Passabilite[] BASE = {GAUCHE_DROITE_HAUT, BAS_DROITE_HAUT, BAS_GAUCHE_HAUT, BAS_GAUCHE_DROITE};
	final int code;
	
	/**
	 * Constructeur explicite
	 * @param code représentant cette passabilité
	 */
	Passabilite(final int code) {
		this.code = code;
	}
	
	/**
	 * Obtenir la passabilité par son code.
	 * @param code représentant la passabilité des quatre faces
	 * @return passabilité dont c'est le code
	 */
	public static Passabilite parCode(int code) {
		if (code > OBSTACLE.code) {
			code -= 128;
		}
		for (Passabilite p : Passabilite.values()) {
			if (p.code == code) {
				return p;
			}
		}
		LOG.error("Code de passabilité inconnu : "+code);
		return PASSABLE;
	}

	/**
	 * Ajoute les obstacles de deux Passabilités.
	 * @param p1 première passabilité
	 * @param p2 seconbde passabilité
	 * @return passabilité constituée des obstacles des deux
	 */
	public static Passabilite ajouter(Passabilite p1, Passabilite p2) {
		if (p1 == null) {
			return p2;
		}
		if (p2 == null) {
			return p1;
		}
		
		int codeResultat = 0;
		for (Passabilite dir : BASE) {
			if (p1.code%(2*dir.code) >= dir.code 
			 || p2.code%(2*dir.code) >= dir.code)
			{
				codeResultat += dir.code;
			}
		}
		return parCode(codeResultat);
	}
	
}
