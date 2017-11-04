package mouvements;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.Fenetre;
import map.Event;
import map.Heros;
import map.LecteurMap;
import utilitaire.Maths;

/**
 * Déplacer un Event d'un pas en diagonale
 */
public class OrbiterAutourDUnEvent extends Mouvement {
	protected static final Logger LOG = LogManager.getLogger(OrbiterAutourDUnEvent.class);
	
	private final Object idEventCentral;
	private Event eventCentral = null;
	private double rayon = -1;
	private double angleInitial = -1;
	private double angleParcouruAChaqueEtape = -1;
	private int nouveauX, nouveauY;
	private int deplacementX, deplacementY;
	
	/**
	 * Constructeur explicite
	 * @param idEventCentral Event autour duquel il faut décrire un cercle
	 */
	public OrbiterAutourDUnEvent(final Object idEventCentral) {
		this.etapes = Maths.ANGLE_TOUR_COMPLET;
		this.idEventCentral = idEventCentral;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public OrbiterAutourDUnEvent(final HashMap<String, Object> parametres) {
		this(parametres.get("eventCentral"));
	}
	
	/** 
	 * Applique l'effet du Mouvement sur la Map et les Events.
	 * Puis incrémente le compteur "ceQuiAEteFait".
	 * @param event subissant le Mouvement
	 */
	@Override
	public final void calculDuMouvement(final Event event) {
		calculerNouvellesCoordonnees(event, trouverEventCentral());
		event.x = this.nouveauX;
		event.y = this.nouveauY;
		
		event.avance = true;
		
		this.ceQuiAEteFait += (int) (this.angleParcouruAChaqueEtape*Maths.ANGLE_DEMI_TOUR/Math.PI);
	}

	/**
	 * Le mouvement dans cette Direction est-il possible ?
	 * @return si le mouvement est possible oui ou non
	 */
	@Override
	public final boolean mouvementPossible() {
		final Event event = this.deplacement.getEventADeplacer();
		
		//si c'est le Héros, il n'avance pas s'il est en animation d'attaque
		if (event instanceof Heros && ((Heros) event).animationAttaque > 0) { 
			return false;
		}
		
		//si l'Event est lui-même traversable, il peut faire son mouvement
		if (event.traversableActuel) {
			return true;
		}
		
		//collisions avec le décor et les autres Events
		calculerNouvellesCoordonnees(event, trouverEventCentral());
		return event.map.calculerSiLaPlaceEstLibre(this.nouveauX, this.nouveauY, event.largeurHitbox, event.hauteurHitbox, event.id);
	}
	
	@Override
	protected final void reinitialiserSpecifique() {
		this.eventCentral = null;
	}
	
	/**
	 * Calculer les déplacements (en pixels) en x et en y lors de cette frame-ci.
	 * @param event à déplacer
	 * @param eventCentral centre du cercle
	 */
	private void calculerNouvellesCoordonnees(final Event event, final Event eventCentral) {	
		final int deltaX = event.x - eventCentral.x;
		final int deltaY = event.y - eventCentral.y;
		
		//calcul du rayon du cercle
		if (this.rayon < 0) {
			this.rayon = Math.sqrt(deltaX*deltaX + deltaY*deltaY);
			LOG.info(event.id+" rayon = "+rayon);
			this.angleInitial = angleActuel(deltaX, deltaY);
			LOG.info(event.id+" angleInitial = "+this.angleInitial);
			this.angleParcouruAChaqueEtape = (double) event.vitesseActuelle / this.rayon;
			LOG.info(event.id+" angleParcouruAChaqueEtape = "+angleParcouruAChaqueEtape);
		}
		final double angleActuel = angleActuel(deltaX, deltaY);
		LOG.info(event.id+" angleActuel = "+angleActuel);
		this.nouveauX = eventCentral.x + (int) (this.rayon * Math.cos(angleActuel + this.angleParcouruAChaqueEtape));
		this.nouveauY = eventCentral.y + (int) (this.rayon * Math.sin(angleActuel + this.angleParcouruAChaqueEtape));
		this.deplacementX = this.nouveauX - event.x; //utile pour connaître la direction regardée
		this.deplacementY = this.nouveauY - event.y; //utile pour connaître la direction regardée
	}
	
	/**
	 * Angle (en radians) entre deux Events et l'axe des abscisses
	 * @param deltaX écart horizontal
	 * @param deltaY écart vertical
	 * @return radians
	 */
	private double angleActuel(final double deltaX, final double deltaY) {
		if (deltaX == 0) {
			if (deltaY > 0) {
				return Math.PI/2;
			} else {
				return -Math.PI/2;
			}
		}
		if (deltaX>0) {
			return Math.atan(deltaY/deltaX);
		} else {
			return Math.atan(deltaY/deltaX) + Math.PI;
		}
	}
	
	/**
	 * Rechercher l'Event qui est le centre du cercle.
	 * @return Event qui sert de centre au Mouvement
	 */
	private Event trouverEventCentral() {
		if (this.eventCentral == null) {
			if (this.idEventCentral instanceof Integer) {
				this.eventCentral = ((LecteurMap) Fenetre.getFenetre().lecteur).map.eventsHash.get((Integer) this.idEventCentral);
			} else if (this.idEventCentral instanceof String) {
				final String nomEventCentral = (String) this.idEventCentral;
				for (Event e : ((LecteurMap) Fenetre.getFenetre().lecteur).map.events) {
					if (e.nom.equals(nomEventCentral)) {
						this.eventCentral = e;
						break;
					}
				}
			}
			LOG.info("Orbiter autour de l'event central "+this.eventCentral.nom);
		}
		return this.eventCentral;
	}

	@Override
	public final int getDirectionImposee() {
		if (Math.abs(this.deplacementX) > Math.abs(this.deplacementY)) {
			if (this.deplacementX > 0) {
				return Event.Direction.DROITE;
			} else {
				return Event.Direction.GAUCHE;
			}
		} else {
			if (this.deplacementY > 0) {
				return Event.Direction.BAS;
			} else {
				return Event.Direction.HAUT;
			}
		}
	}
	
	@Override
	public final String toString() {
		return "Orbiter autour de l'Event " + idEventCentral;
	}

	@Override
	protected void terminerLeMouvementSpecifique(final Event event) {
		//rien
	}

	@Override
	protected void ignorerLeMouvementSpecifique(final Event event) {
		//rien
	}
	
}