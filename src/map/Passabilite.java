package map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Une tuile de d�cor peut etre traversable ou solide.
 * Les quatre faces de la tuile n'ont pas forc�ment la m�me passabilit� :
 * on pourra y entrer par la gauche, mais pas forc�ment en resortir par la droite.
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
		this.code = genererCode(passableEnBas, passableAGauche, passableADroite, passableEnHaut);
		this.passableEnBas = passableEnBas;
		this.passableAGauche = passableAGauche;
		this.passableADroite = passableADroite;
		this.passableEnHaut = passableEnHaut;
	}
	
	/**
	 * Obtenir la passabilit� par son code.
	 * @param code repr�sentant la passabilit� des quatre faces
	 * @return passabilit� dont c'est le code
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
		LOG.error("Code de passabilit� inconnu : "+code);
		return PASSABLE;
	}

	/**
	 * Union des deux Passabilit�s.
	 * @param p1 premi�re passabilit�
	 * @param p2 seconbde passabilit�
	 * @return passabilit�s ajout�es
	 */
	public static Passabilite union(final Passabilite p1, final Passabilite p2) {
		if (p1 == null) {
			return p2;
		}
		if (p2 == null) {
			return p1;
		}
		
		final int codeResultat = genererCode(p1.passableEnBas || p2.passableEnBas, 
				p1.passableAGauche || p2.passableAGauche, 
				p1.passableADroite || p2.passableADroite,
				p1.passableEnHaut || p2.passableEnHaut);
		return parCode(codeResultat);
	}
	
	/**
	 * G�n�rer le code de passabilit� d'une case.
	 * @param passableEnBas peut-on entrer sur cette case par le bas ?
	 * @param passableAGauche peut-on entrer sur cette case par la gauche ?
	 * @param passableADroite peut-on entrer sur cette case par la droite ?
	 * @param passableEnHaut peut-on entrer sur cette case par le haut ?
	 * @return code correspondant a cette passabilit�
	 */
	private static int genererCode(final boolean passableEnBas, final boolean passableAGauche, final boolean passableADroite, 
			final boolean passableEnHaut) {
		return (passableEnBas ? 0 : BIT_OBSTACLE_BAS) 
				+ (passableAGauche ? 0 : BIT_OBSTACLE_GAUCHE) 
				+ (passableADroite ? 0 : BIT_OBSTACLE_DROITE) 
				+ (passableEnHaut ? 0 : BIT_OBSTACLE_HAUT);
	}

	/**
	 * Intersection des passabilit�s.
	 * @param p1 Passabilit� de base
	 * @param p2 Passabilit� a soustraire
	 * @return obstacles ajout�s
	 */
	public static Passabilite intersection(final Passabilite p1, final Passabilite p2) {
		if (p1 == null) {
			return p2;
		}
		if (p2 == null) {
			return p1;
		}
		
		final int codeResultat = genererCode(p1.passableEnBas && p2.passableEnBas, 
				p1.passableAGauche && p2.passableAGauche, 
				p1.passableADroite && p2.passableADroite,
				p1.passableEnHaut && p2.passableEnHaut);
		return parCode(codeResultat);
	}

	/**
	 * La passabilit� est-elle multilat�rale ?
	 * C'est-�-dire ni passable ni compl�tement solide.
	 * @param passabilite d'un Event ou d'un tile de d�cor
	 * @return true si passabilit� complexe, false sinon
	 */
	public static boolean estMultilateral(final Passabilite passabilite) {
		return passabilite != Passabilite.PASSABLE && passabilite != Passabilite.OBSTACLE;
	}
	
	/**
	 * Comparaison
	 * @param passabilite autre
	 * @return true si egaux
	 */
	public boolean equals(final Passabilite passabilite) {
		return this.code == passabilite.code;
	}
	
}
