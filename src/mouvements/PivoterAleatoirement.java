package mouvements;

import java.util.HashMap;

import utilitaire.Maths;

/**
 * Pivoter un Event à 90 degrés aléatoirement dans le sens horaire ou anti-horaire.
 */
public class PivoterAleatoirement extends Pivoter {

	private PivoterAleatoirement() {
		super(-1); // la vraie valeur sera décidée à l'exécution
	}
	
	public PivoterAleatoirement(HashMap<String, Object> parametres) {
		this();
	}
	
	@Override
	public final int getDirectionImposee() {
		switch (Maths.generateurAleatoire.nextInt(2)) {
		case (0):
			pivoter90degres();
			break;
		case(1):
			pivoterMoins90degres();
			break;
		default:
			break;
		}
		return this.direction;
	}

}
