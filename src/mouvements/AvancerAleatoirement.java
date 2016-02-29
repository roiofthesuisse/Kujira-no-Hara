package mouvements;

import java.util.HashMap;

import main.Fenetre;
import map.Event.Direction;
import utilitaire.GenerateurAleatoire;
import utilitaire.Maths;

/**
 * Déplacer un Event dans une Direction aléatoire et d'un certain nombre de cases
 */
public class AvancerAleatoirement extends Avancer {
	//constantes
	public static final int NOMBRE_DE_DIRECTIONS_POSSIBLES = 4; 
	private static GenerateurAleatoire rand = new GenerateurAleatoire();
	
	/** 
	 * Constructeur explicite 
	 */
	public AvancerAleatoirement() {
		super(rand.nextInt(NOMBRE_DE_DIRECTIONS_POSSIBLES), Fenetre.TAILLE_D_UN_CARREAU);
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public AvancerAleatoirement(final HashMap<String, Object> parametres) {
		this();
	}
	
	@Override
	public final void reinitialiserSpecifique() {
		int nouvelleDirection = rand.nextInt(NOMBRE_DE_DIRECTIONS_POSSIBLES);
		//ne pas faire demi-tour, ça donne l'impression que l'Event ne sait pas où il va
		if ( (  this.direction==Direction.BAS && nouvelleDirection==Direction.HAUT) 
			|| (this.direction==Direction.GAUCHE && nouvelleDirection==Direction.DROITE) 
			|| (this.direction==Direction.DROITE && nouvelleDirection==Direction.GAUCHE) 
			|| (this.direction==Direction.HAUT && nouvelleDirection==Direction.BAS) 
		) {
			nouvelleDirection += (1 + rand.nextInt(NOMBRE_DE_DIRECTIONS_POSSIBLES-1));
			nouvelleDirection = Maths.modulo(nouvelleDirection, NOMBRE_DE_DIRECTIONS_POSSIBLES);
		}
		this.direction = nouvelleDirection;
	}
}
