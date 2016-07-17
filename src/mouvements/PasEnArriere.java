package mouvements;

import java.util.HashMap;

import main.Fenetre;
import map.Event;

/**
 * Déplacer un Event d'un pas dans la direction opposée à la direction de l'Event.
 */
public class PasEnArriere extends Avancer {
	
	/**
	 * Constructeur explicite
	 */
	public PasEnArriere() {
		//le -1 est bidon, il sera remplacé par la direction de l'Event lors de la vérification
		super(-1, Fenetre.TAILLE_D_UN_CARREAU);
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public PasEnArriere(final HashMap<String, Object> parametres) {
		this();
	}
	
	/**
	 * Le mouvement dans cette Direction est-il possible ?
	 * @return si le mouvement est possible oui ou non
	 */
	@Override
	public final boolean mouvementPossible() {
		//on assigne la vraie direction de l'Event
		final Event event = this.deplacement.getEventADeplacer();
		this.direction = event.direction;
		
		//puis on lance la vérification traditionnelle
		return super.mouvementPossible();
	}

	@Override
	public int getDirectionImposee() {
		prendreDirectionOpposee();
		return this.direction;
	}

}
