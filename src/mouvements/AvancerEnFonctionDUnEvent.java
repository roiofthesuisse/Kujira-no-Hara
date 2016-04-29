package mouvements;

import java.util.HashMap;

import main.Fenetre;
import map.Event;
import map.Event.Direction;
import map.LecteurMap;

/**
 * Approcher ou éloigner l'Event à déplacer d'un pas d'un autre Event.
 */
public class AvancerEnFonctionDUnEvent extends Avancer {
	
	private int idEventObserve;
	/** sens = 1 lorsque l'Event suit l'Event observé, sens = 2 lorsque l'Event fuit l'Event observé*/
	private int sens;
	private int directionPossibleVerticale;
	private int directionPossibleHorizontale;
	/** La direction a-t-elle été décidée ? Si oui on n'y touche plus */
	private boolean directionDecidee;

	/**
	 * Constructeur explicite
	 * @param idEventObserve : id de l'Event en fonction duquel l'Event à déplacer avance
	 * @param sens : l'event s'approche ou s'éloigne
	 */
	public AvancerEnFonctionDUnEvent(final int idEventObserve, final int sens) {
		super(-1, Fenetre.TAILLE_D_UN_CARREAU);
		this.idEventObserve = idEventObserve;	
		this.sens = sens;
		this.directionDecidee = false;
	}

	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public AvancerEnFonctionDUnEvent(final HashMap<String, Object> parametres) {
		this((int) parametres.get("idEventObserve"), (int) parametres.get("sens"));
	}

	/**
	 * Le mouvement dans cette Direction est-il possible ?
	 * @return si le mouvement est possible oui ou non
	 */
	@Override
	public final boolean mouvementPossible() {
		if (!this.directionDecidee) {
			final Event eventObservateur = this.deplacement.getEventADeplacer();
			final Event eventObserve = ((LecteurMap) Fenetre.getFenetre().lecteur).map.eventsHash.get((Integer) this.idEventObserve);
			int distanceVerticale = eventObservateur.y - eventObserve.y;
			int distanceHorizontale = eventObservateur.x - eventObserve.x;
			
			calculerDirection(distanceVerticale, distanceHorizontale);
			if (this.sens == 2) { // l'Event fuit
				prendreDirectionOpposee();
			} 
			if (!super.mouvementPossible()) {
				essayerAutreDirection();
			}
			this.directionDecidee = true;
		}
		return super.mouvementPossible();
	}
	
	/**
	 * Détermine la direction du Mouvement pour suivre l'event observé
	 * @param distanceVerticale
	 * @param distanceHorizontale
	 */
	private void calculerDirection(final int distanceVerticale, final int distanceHorizontale) {
		if (distanceVerticale < 0) {
			this.directionPossibleVerticale = Direction.BAS;
		} else {
			this.directionPossibleVerticale = Direction.HAUT;
		}
		if (distanceHorizontale < 0) {
			this.directionPossibleHorizontale = Direction.DROITE;
		} else {
			this.directionPossibleHorizontale = Direction.GAUCHE;
		}
		
		if (Math.abs(distanceVerticale) > Math.abs(distanceHorizontale)) {
			this.direction = this.directionPossibleVerticale;
		} else {
			this.direction = this.directionPossibleHorizontale;
		}
	}
	
	/**
	 * Inverse la direction du Mouvement
	 * Utile pour les Event qui fuient
	 * @return direction opposée à la direction donnée en paramètre
	 */
	private void prendreDirectionOpposee() {
		if (this.direction == Direction.BAS) {
			this.direction = Direction.HAUT;
		} else if (this.direction == Direction.HAUT) {
			this.direction = Direction.BAS;
		} else if (this.direction == Direction.GAUCHE) {
			this.direction = Direction.DROITE;
		} else if (this.direction == Direction.DROITE) {
			this.direction = Direction.GAUCHE;
		}
	}
	
	/**
	 * Si le Mouvement est impossible dans la direction calculée, une autre direction est proposée
	 */
	private void essayerAutreDirection(){
		if (this.direction == Direction.HAUT || this.direction == Direction.BAS) {
			this.direction = directionPossibleHorizontale;
			if (this.sens == 2) { // l'Event fuit
				prendreDirectionOpposee();
			} 
		} else if (this.direction == Direction.GAUCHE || this.direction == Direction.DROITE) {
			this.direction = directionPossibleVerticale;
			if (this.sens == 2) { // l'Event fuit
				prendreDirectionOpposee();
			} 
		}	
	}
	
	@Override
	protected final void reinitialiserSpecifique() {
		super.reinitialiserSpecifique();
		this.directionDecidee = false;
		this.directionPossibleHorizontale = -1;
		this.directionPossibleVerticale = -1;
	}
	
}
