package map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Une tuile de décor peut être traversable ou solide.
 * Les quatre faces de la tuile n'ont pas forcément la même passabilité :
 * on pourra y entrer par la gauche, mais pas forcément en resortir par la droite.
 */
public enum Passabilite {
	PASSABLE(true, true, true, true),
	GAUCHE_DROITE_HAUT(false, true, true, true), 
	BAS_DROITE_HAUT(true, false, true, true), 
	DROITE_HAUT(false, false, true, true), 
	BAS_GAUCHE_HAUT(true, true, false, true), 
	GAUCHE_HAUT(false, true, false, true), 
	BAS_HAUT(true, false, false, true),
	HAUT(false, false, false, true),
	BAS_GAUCHE_DROITE(true, true, true, false),
	GAUCHE_DROITE(false, true, true, false),
	BAS_DROITE(true, false, true, false),
	DROITE(false, false, true, false),
	BAS_GAUCHE(true, true, false, false),
	GAUCHE(false, true, false, false),
	BAS(true, false, false, false),
	OBSTACLE(false, false, false, false);
	
	private static final Logger LOG = LogManager.getLogger(Passabilite.class);
	private static final int BIT_OBSTACLE_BAS = 1;
	private static final int BIT_OBSTACLE_GAUCHE = 2;
	private static final int BIT_OBSTACLE_DROITE = 4;
	private static final int BIT_OBSTACLE_HAUT = 8;
	
	final int code;
	final boolean passableEnBas;
	final boolean passableAGauche;
	final boolean passableADroite;
	final boolean passableEnHaut;
	
	/**
	 * Constructeur explicite
	 * @param passableEnBas peut-on entrer sur cette case par le bas ?
	 * @param passableAGauche peut-on entrer sur cette case par la gauche ?
	 * @param passableADroite peut-on entrer sur cette case par la droite ?
	 * @param passableEnHaut peut-on entrer sur cette case par le haut ?
	 */
	Passabilite(final boolean passableEnBas, final boolean passableAGauche, final boolean passableADroite, 
			final boolean passableEnHaut) {
		this.code = (passableEnBas ? 0 : BIT_OBSTACLE_BAS) 
				+ (passableAGauche ? 0 : BIT_OBSTACLE_GAUCHE) 
				+ (passableADroite ? 0 : BIT_OBSTACLE_DROITE) 
				+ (passableEnHaut ? 0 : BIT_OBSTACLE_HAUT);
		this.passableEnBas = passableEnBas;
		this.passableAGauche = passableAGauche;
		this.passableADroite = passableADroite;
		this.passableEnHaut = passableEnHaut;
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
	 * Ajoute les deux Passabilités.
	 * @param p1 première passabilité
	 * @param p2 seconbde passabilité
	 * @return passabilité unie
	 */
	public static Passabilite ajouter(Passabilite p1, Passabilite p2) {
		if (p1 == null) {
			return p2;
		}
		if (p2 == null) {
			return p1;
		}
		
		int codeResultat = (p1.passableEnBas || p2.passableEnBas ? 0 : 1) 
				+ (p1.passableAGauche || p2.passableAGauche ? 0 : 2) 
				+ (p1.passableADroite || p2.passableADroite ? 0 : 4) 
				+ (p1.passableEnHaut || p2.passableEnHaut ? 0 : 8);
		return parCode(codeResultat);
	}
	
}
