package mouvements;

import java.util.HashMap;

import conditions.ConditionContact;
import conditions.ConditionContact.TypeDeContact;
import main.Main;
import map.Event;
import map.Event.Direction;
import map.LecteurMap;

/**
 * Approcher ou éloigner l'Event à déplacer d'un pas d'un autre Event.
 */
public class AvancerEnFonctionDUnEvent extends Avancer {
	
	private int idEventObserve;
	
	private Sens sens;
	private int directionPossibleVerticale;
	private int directionPossibleHorizontale;
	/** La direction a-t-elle été décidée ? Si oui on n'y touche plus */
	private boolean directionDecidee;

	/**
	 * Constructeur explicite
	 * @param idEventObserve : id de l'Event en fonction duquel l'Event à déplacer avance
	 * @param sens : l'event s'approche ou s'éloigne
	 */
	public AvancerEnFonctionDUnEvent(final int idEventObserve, final Sens sens) {
		super(-1, Main.TAILLE_D_UN_CARREAU);
		this.idEventObserve = idEventObserve;	
		this.sens = sens;
		this.directionDecidee = false;
	}

	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public AvancerEnFonctionDUnEvent(final HashMap<String, Object> parametres) {
		this(
				(int) parametres.get("idEventObserve"), //0 est le Héros
				parametres.containsKey("sens") ? (((String) parametres.get("sens")).equals("fuir") ? Sens.FUIR : Sens.SUIVRE) : Sens.SUIVRE //si non précisé : on suit
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
			
			//s'il est déjà arrivé auprès de sa cible, l'Event arrête d'avancer
			if (this.sens == Sens.SUIVRE) {
				final ConditionContact contact = new ConditionContact(-1, eventObservateur.id, eventObserve.id, TypeDeContact.SUPERPOSITION_PARTIELLE);
				contact.page = this.deplacement.page;
				if (contact.estVerifiee()) {
					return false;
				}
			}
			
			//calcul de la direction à prendre pour atteindre la cible
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
	 * Détermine la direction du Mouvement pour suivre l'event observé
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
	 * Si le Mouvement est impossible dans la direction calculée, une autre direction est proposée.
	 */
	private void essayerAutreDirection() {
		if (this.direction == Direction.HAUT || this.direction == Direction.BAS) {
			this.direction = directionPossibleHorizontale;	 
		} else {
			this.direction = directionPossibleVerticale; 
		}
		//si l'Event fuit on prend la direction opposée
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
