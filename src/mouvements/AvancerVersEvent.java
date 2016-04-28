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

	/**
	 * Constructeur explicite
	 * @param idEventSuivi : id de l'event vers lequel l'event avance d'un pas
	 */
	public AvancerVersEvent(final int idEventSuivi) {
		super(-1, Fenetre.TAILLE_D_UN_CARREAU);
		this.idEventSuivi = idEventSuivi;	
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
		final Event eventSuiveur = this.deplacement.getEventADeplacer();
		final Event eventSuivi = ((LecteurMap) Fenetre.getFenetre().lecteur).map.eventsHash.get((Integer) this.idEventSuivi);
		int distanceVerticale = eventSuiveur.y - eventSuivi.y;
		int distanceHorizontale = eventSuiveur.x - eventSuivi.x;
		
		this.calculerDirection(distanceVerticale, distanceHorizontale);
		return super.mouvementPossible();
	}
	
	/**
	 * Donne une direction au mouvement en fonction de la position des deux event
	 * @param distanceVerticale difference entre la coordonnée y de l'event suiveur et de l'event suivi
	 * @param distanceHorizontale difference entre la coordonnée x de l'event suiveur et de l'event suivi
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
	
}
