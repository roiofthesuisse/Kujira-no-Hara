package mouvements;

import java.util.HashMap;

import main.Fenetre;
import map.Event;
import map.Event.Direction;
import map.LecteurMap;

/**
 * Déplacer un Event d'un pas vers un autre Event.
 */
public class AvancerVersEvent extends Avancer {
	
	private int idEventSuivi;
	/** La direction a-t-elle été décidée ? Si oui on n'y touche plus */
	private boolean directionDecidee;

	/**
	 * Constructeur explicite
	 * @param idEventSuivi : id de l'event vers lequel l'event avance d'un pas
	 */
	public AvancerVersEvent(final int idEventSuivi) {
		super(-1, Fenetre.TAILLE_D_UN_CARREAU);
		this.idEventSuivi = idEventSuivi;
		this.directionDecidee = false;
	}

	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public AvancerVersEvent(final HashMap<String, Object> parametres) {
		this((int) parametres.get("idEventSuivi"));
	}

	/**
	 * Le mouvement dans cette Direction est-il possible ?
	 * @return si le mouvement est possible oui ou non
	 */
	@Override
	public final boolean mouvementPossible() {
		if (!this.directionDecidee) {
			final Event eventSuiveur = this.deplacement.getEventADeplacer();
			final Event eventSuivi = ((LecteurMap) Fenetre.getFenetre().lecteur).map.eventsHash.get((Integer) this.idEventSuivi);
			final int distanceVerticale = eventSuiveur.y - eventSuivi.y;
			final int distanceHorizontale = eventSuiveur.x - eventSuivi.x;
			this.calculerDirection(distanceVerticale, distanceHorizontale);
			this.directionDecidee = true;
		}
		
		return super.mouvementPossible();
	}
	
	/**
	 * Donne une direction au Mouvement en fonction de la position des deux Events.
	 * @param distanceVerticale difference entre la coordonnée y de l'Event suiveur et de l'Event suivi
	 * @param distanceHorizontale difference entre la coordonnée x de l'Event suiveur et de l'Event suivi
	 */
	private void calculerDirection(final int distanceVerticale, final int distanceHorizontale) {
		if (Math.abs(distanceVerticale) > Math.abs(distanceHorizontale)) {
			if (distanceVerticale < 0) {
				this.direction = Direction.BAS;
			} else {
				this.direction = Direction.HAUT;
			}
		} else {
			if (distanceHorizontale < 0) {
				this.direction = Direction.DROITE;
			} else {
				this.direction = Direction.GAUCHE;
			}
		}
	}
	
	@Override
	protected final void reinitialiserSpecifique() {
		super.reinitialiserSpecifique();
		this.directionDecidee = false;
	}
	
}
