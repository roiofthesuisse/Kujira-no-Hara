package mouvements;

import java.util.HashMap;

import conditions.ConditionContact;
import conditions.ConditionContact.TypeDeContact;
import main.Main;
import map.Event;
import map.Event.Direction;
import map.LecteurMap;

/**
 * Approcher ou �loigner l'Event a d�placer d'un pas d'un autre Event.
 */
public class AvancerEnFonctionDUnEvent extends Avancer {
	
	private int idEventObserve;
	
	private Sens sens;
	private int directionPossibleVerticale;
	private int directionPossibleHorizontale;
	/** La direction a-t-elle ete d�cid�e ? Si oui on n'y touche plus */
	private boolean directionDecidee;

	/**
	 * Constructeur explicite
	 * @param idEventObserve : id de l'Event en fonction duquel l'Event a d�placer avance
	 * @param sens : l'event s'approche ou s'�loigne
	 */
	public AvancerEnFonctionDUnEvent(final int idEventObserve, final Sens sens) {
		super(-1, Main.TAILLE_D_UN_CARREAU);
		this.idEventObserve = idEventObserve;	
		this.sens = sens;
		this.directionDecidee = false;
	}

	/**
	 * Constructeur generique
	 * @param parametres liste de parametres issus de JSON
	 */
	public AvancerEnFonctionDUnEvent(final HashMap<String, Object> parametres) {
		this(
				(int) parametres.get("idEventObserve"), //0 est le Heros
				parametres.containsKey("sens") ? (((String) parametres.get("sens")).equals("fuir") ? Sens.FUIR : Sens.SUIVRE) : Sens.SUIVRE //si non pr�cis� : on suit
		);
	}

	/**
	 * Le mouvement dans cette Direction est-il possible ?
	 * @return si le mouvement est possible oui ou non
	 */
	@Override
	public final boolean mouvementPossible() {
		if (!this.directionDecidee) { //ne calculer la direction qu'une seule fois par pas
			
			final Event eventObservateur = this.deplacement.getEventADeplacer();
			final Event eventObserve = ((LecteurMap) Main.lecteur).map.eventsHash.get((Integer) this.idEventObserve);
			
			//s'il est deja arriv� aupr�s de sa cible, l'Event arrete d'avancer
			if (this.sens == Sens.SUIVRE) {
				final ConditionContact contact = new ConditionContact(-1, eventObservateur.id, eventObserve.id, TypeDeContact.SUPERPOSITION_PARTIELLE);
				contact.page = this.deplacement.page;
				if (contact.estVerifiee()) {
					return false;
				}
			}
			
			//calcul de la direction a prendre pour atteindre la cible
			final int distanceVerticale = eventObservateur.y - eventObserve.y;
			final int distanceHorizontale = eventObservateur.x - eventObserve.x;
			calculerDirection(distanceVerticale, distanceHorizontale);
			
			//si l'Event fuit, on inverse la direction
			if (this.sens == Sens.FUIR) {
				this.direction = Event.Direction.directionOpposee(this.direction);
			} 
			
			if (!super.mouvementPossible()) {
				essayerAutreDirection();
			}
			
			this.directionDecidee = true; 
		}
		return super.mouvementPossible();
	}
	
	/**
	 * D�termine la direction du Mouvement pour suivre l'event observ�
	 * @param distanceVerticale difference entre le y destination et le y actuel
	 * @param distanceHorizontale difference entre le x destination et le x actuel
	 */
	public final void calculerDirection(final int distanceVerticale, final int distanceHorizontale) {
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
	 * Si le Mouvement est impossible dans la direction calcul�e, une autre direction est propos�e.
	 */
	private void essayerAutreDirection() {
		if (this.direction == Direction.HAUT || this.direction == Direction.BAS) {
			this.direction = directionPossibleHorizontale;	 
		} else {
			this.direction = directionPossibleVerticale; 
		}
		//si l'Event fuit on prend la direction oppos�e
		if (this.sens == Sens.FUIR) {
			this.direction = Event.Direction.directionOpposee(this.direction);
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
