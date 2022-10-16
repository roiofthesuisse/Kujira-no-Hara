package mouvements;

import java.util.HashMap;

import main.Main;
import map.Event;
import map.Heros;
import map.LecteurMap;
import map.Passabilite;
import utilitaire.Maths;

/**
 * Deplacer un Event d'un pas en diagonale
 */
public class OrbiterAutourDUnEvent extends Mouvement {
	private final Object idEventCentral;
	private Event eventCentral = null;
	private double rayon = -1;
	private double angleParcouruAChaqueEtape = -1;
	private int nouveauX, nouveauY;
	private int deplacementX, deplacementY;
	
	/**
	 * Constructeur explicite
	 * @param idEventCentral Event autour duquel il faut decrire un cercle
	 */
	public OrbiterAutourDUnEvent(final Object idEventCentral) {
		this.etapes = Maths.ANGLE_TOUR_COMPLET;
		this.idEventCentral = idEventCentral;
	}
	
	/**
	 * Constructeur generique
	 * @param parametres liste de parametres issus de JSON
	 */
	public OrbiterAutourDUnEvent(final HashMap<String, Object> parametres) {
		this(parametres.get("eventCentral"));
	}
	
	/** 
	 * Applique l'effet du Mouvement sur la Map et les Events.
	 * Puis incremente le compteur "ceQuiAEteFait".
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
		
		//si c'est le Heros, il n'avance pas s'il est en animation d'attaque
		if (event instanceof Heros && ((Heros) event).animationAttaque > 0) { 
			return false;
		}
		
		//si l'Event est lui-meme traversable, il peut faire son mouvement
		if (event.traversableActuel == Passabilite.PASSABLE) {
			return true;
		}
		
		//collisions avec le decor et les autres Events
		calculerNouvellesCoordonnees(event, trouverEventCentral());
		return event.map.calculerSiLaPlaceEstLibre(this.nouveauX, this.nouveauY, event.largeurHitbox, event.hauteurHitbox, event.id);
	}
	
	@Override
	protected final void reinitialiserSpecifique() {
		this.eventCentral = null;
	}
	
	/**
	 * Calculer les deplacements (en pixels) en x et en y lors de cette frame-ci.
	 * @param event a deplacer
	 * @param eventCentral centre du cercle
	 */
	private void calculerNouvellesCoordonnees(final Event event, final Event eventCentral) {	
		final int deltaX = event.x - eventCentral.x;
		final int deltaY = event.y - eventCentral.y;
		
		//calcul du rayon du cercle
		if (this.rayon < 0) {
			this.rayon = Math.sqrt(deltaX*deltaX + deltaY*deltaY);
			this.angleParcouruAChaqueEtape = (double) event.vitesseActuelle.valeur / this.rayon;
		}
		final double angleActuel = angleActuel(deltaX, deltaY);
		this.nouveauX = eventCentral.x + (int) (this.rayon * Math.cos(angleActuel + this.angleParcouruAChaqueEtape));
		this.nouveauY = eventCentral.y + (int) (this.rayon * Math.sin(angleActuel + this.angleParcouruAChaqueEtape));
		this.deplacementX = this.nouveauX - event.x; //utile pour connaetre la direction regard�e
		this.deplacementY = this.nouveauY - event.y; //utile pour connaetre la direction regard�e
	}
	
	/**
	 * Angle (en radians) entre deux Events et l'axe des abscisses
	 * @param deltaX �cart horizontal
	 * @param deltaY �cart vertical
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
				this.eventCentral = ((LecteurMap) Main.lecteur).map.eventsHash.get((Integer) this.idEventCentral);
			} else if (this.idEventCentral instanceof String) {
				final String nomEventCentral = (String) this.idEventCentral;
				for (Event e : ((LecteurMap) Main.lecteur).map.events) {
					if (e.nom.equals(nomEventCentral)) {
						this.eventCentral = e;
						break;
					}
				}
			}
			this.eventCentral.nom.isEmpty(); //tester si l'event est null => NullPointerException
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