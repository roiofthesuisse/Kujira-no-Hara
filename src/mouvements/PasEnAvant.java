package mouvements;

import java.util.HashMap;

import main.Main;
import map.Event;

/**
 * Déplacer un Event dans sa direction actuelle.
 */
public class PasEnAvant extends Avancer {

	/** La direction a-t-elle été décidée ? Si oui on n'y touche plus */
	private boolean directionDecidee;
	
	/**
	 * Constructeur explicite
	 */
	public PasEnAvant() {
		//le -1 est bidon, il sera remplacé par la direction de l'Event lors de la vérification
		super(-1, Main.TAILLE_D_UN_CARREAU);
		this.directionDecidee = false;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public PasEnAvant(final HashMap<String, Object> parametres) {
		this();
	}
	
	/**
	 * Le mouvement dans cette Direction est-il possible ?
	 * @return si le mouvement est possible oui ou non
	 */
	@Override
	public final boolean mouvementPossible() {
		//on assigne la vraie direction de l'Event
		if (!this.directionDecidee) {
			final Event event = this.deplacement.getEventADeplacer();
			this.direction = event.direction;
			this.directionDecidee = true;
		}
		
		//puis on lance la vérification traditionnelle
		return super.mouvementPossible();
	}
	
	@Override
	protected final void reinitialiserSpecifique() {
		super.reinitialiserSpecifique();
		this.directionDecidee = false;
	}

}
