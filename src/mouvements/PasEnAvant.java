package mouvements;

import java.util.HashMap;

import main.Fenetre;

public class PasEnAvant extends Avancer {

	/**
	 * Constructeur explicite
	 * @param direction dans laquelle l'Event doit avancer
	 */
	public PasEnAvant(int direction) {
		super(direction,Fenetre.TAILLE_D_UN_CARREAU);
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public PasEnAvant(final HashMap<String, Object> parametres) {
		this( (int) parametres.get("direction"));
	}

}
