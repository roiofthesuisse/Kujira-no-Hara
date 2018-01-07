package mouvements;

import java.util.HashMap;

import main.Fenetre;
import map.Event;
import map.Heros;
import map.PageEvent.Traversabilite;

/**
 * Déplacer un Event dans une Direction et d'un certain nombre de cases
 */
public class Sauter extends Mouvement {
	//constantes
	private static final int DUREE_DU_SAUT_SUR_PLACE = 10;
	private static final int DUREE_DU_SAUT_PAR_CASE = 1;
	private static final String NOM_EVENT_RESERVATION_PLACE_D_ARRIVEE = "ReservationSaut";
	
	private int xEventAvantSaut;
	private int yEventAvantSaut;
	protected int x;
	protected int y;
	private int xEventApresSaut;
	private int yEventApresSaut;
	protected int direction;
	private Integer distance = null;
	/** Event fictif pour réserver la place d'arrivée du Saut */
	private Event eventDeReservation = null;
	
	/**
	 * Constructeur explicite
	 * @param x nombre de cases de déplacement en horizontal
	 * @param y nombre de cases de déplacement en vertical
	 */
	public Sauter(final int x, final int y) {
		this.x = x;
		this.y = y;
		calculerDirectionSaut();
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public Sauter(final HashMap<String, Object> parametres) {
		this( (int) parametres.get("x"), 
			  (int) parametres.get("y") );
	}
	
	/**
	 * La direction du Saut est calculée selon le x et le y du Saut.
	 */
	protected final void calculerDirectionSaut() {
		if (this.x > this.y) {
			//haut droite
			if (this.y >= -this.x) {
				//bas droite
				this.direction = Event.Direction.DROITE;
			} else {
				//haut gauche
				this.direction = Event.Direction.HAUT;
			}
		} else {
			//bas gauche
			if (this.y > -this.x) {
				//bas droite
				this.direction = Event.Direction.BAS;
			} else {
				//haut gauche
				this.direction = Event.Direction.GAUCHE;
			}
		}
	}
	
	/**
	 * Le Mouvement est-il possible pour cet Event ?
	 * @return true si le Mouvement est possible, false sinon
	 */
	public boolean mouvementPossible() {
		final Event event = this.deplacement.getEventADeplacer();
		
		if (!event.saute) { //le Saut n'a pas commencé
			//si c'est le Héros, il n'avance pas s'il est en animation d'attaque
			if (event instanceof Heros && ((Heros) event).animationAttaque > 0) { 
				return false;
			}
			
			//si l'Event est lui-même traversable, il peut faire son mouvement
			if (event.traversableActuel == Traversabilite.TRAVERSABLE) {
				return true;
			}
			
			this.xEventAvantSaut = event.x;
			this.yEventAvantSaut = event.y;
			this.xEventApresSaut = xEventAvantSaut + this.x*Fenetre.TAILLE_D_UN_CARREAU;
			this.yEventApresSaut = yEventAvantSaut + this.y*Fenetre.TAILLE_D_UN_CARREAU;
			
			//on ne peut pas Sauter en dehors de la Map
			if (this.xEventAvantSaut<0 
				|| this.xEventAvantSaut+event.largeurHitbox>event.map.largeur*Fenetre.TAILLE_D_UN_CARREAU
				|| this.yEventApresSaut<0
				|| this.yEventApresSaut+event.hauteurHitbox>event.map.hauteur*Fenetre.TAILLE_D_UN_CARREAU
			) {
				return false;
			}
			
			//la case d'arrivée est-elle libre ?
			final boolean reponse = event.map.calculerSiLaPlaceEstLibre(this.xEventApresSaut, this.yEventApresSaut, event.largeurHitbox, event.hauteurHitbox, event.id);
			return reponse;
		} else {
			//le Saut est en cours
			return true;
		}
	}
	
	/** 
	 * Applique l'effet du Mouvement sur la Map et les Events.
	 * Puis incrémente le compteur "ceQuiAEteFait".
	 * @param event subissant le Mouvement
	 */
	public final void calculDuMouvement(final Event event) {
		if (!event.saute) {
			//le Mouvement n'a pas encore commencé, on initialise
			event.saute = true;
			this.ceQuiAEteFait = 0;
			calculerDistance();
			this.etapes = DUREE_DU_SAUT_SUR_PLACE + DUREE_DU_SAUT_PAR_CASE*((int) (distance/Fenetre.TAILLE_D_UN_CARREAU));
			if (this.x==0 && this.y==0) {
				this.direction = event.direction;
			}
			reserverLaPlaceDArrivee(event);
		}

		//déplacement :
		final double t = (double) ceQuiAEteFait /(double) etapes;
		final int x0 = xEventAvantSaut;
		final int y0 = yEventAvantSaut;
		final int xf = xEventApresSaut;
		final int yf = yEventApresSaut;
		
		final int xDroite = (int) Math.round( (1-t)*x0 + t*xf );
		final int yDroite = (int) Math.round( (1-t)*y0 + t*yf );
		
		final int yParabole = (int) Math.round( 1.5*(distance+2*Fenetre.TAILLE_D_UN_CARREAU)*(t*t-t) );
		event.coordonneeApparenteXLorsDuSaut = (int) xDroite;
		event.coordonneeApparenteYLorsDuSaut = (int) (yParabole + yDroite);
		
		this.ceQuiAEteFait++;
		
		if (this.ceQuiAEteFait >= etapes) {
			//le saut est fini, on déplace l'Event à l'arrivée
			event.x = this.xEventApresSaut;
			event.y = this.yEventApresSaut;
			
			if (this.x!=0 || this.y!=0) {
				event.direction = this.direction; //on garde la direction prise à cause du saut
			}
		}
	}

	/**
	 * Initialiser la valeur de la distance parcourue au sol.
	 */
	private void calculerDistance() {
		if (this.distance == null) {
			final int deltaX = this.xEventApresSaut - this.xEventAvantSaut;
			final int deltaY = yEventApresSaut - yEventAvantSaut;
			this.distance = (int) Math.round( Math.sqrt(deltaX*deltaX + deltaY*deltaY) );
		}
	}
	
	/**
	 * Placer un Event fictif pour réserver la position d'arrivée du Saut.
	 * @param event qui saute
	 */
	private void reserverLaPlaceDArrivee(final Event event) {
		final Event reservation = Event.creerEventGenerique(-1, NOM_EVENT_RESERVATION_PLACE_D_ARRIVEE, this.xEventApresSaut, this.yEventApresSaut, event.map);
		reservation.largeurHitbox = event.largeurHitbox;
		reservation.hauteurHitbox = event.hauteurHitbox;
		event.map.eventsAAjouter.add(reservation);
		this.eventDeReservation = reservation;
	}

	@Override
	protected final void terminerLeMouvementSpecifique(final Event event) {
		event.saute = false;
		this.distance = null;

		//supprimer l'Event de réservation de place d'arrivée
		if (this.eventDeReservation != null) {
			this.eventDeReservation.supprime = true;
		}
	}

	@Override
	protected final void ignorerLeMouvementSpecifique(final Event event) {
		event.saute = false;
		this.distance = null;
		//supprimer l'Event de réservation de place d'arrivée
		if (this.eventDeReservation != null) {
			this.eventDeReservation.supprime = true;
		}
	}

	@Override
	protected void reinitialiserSpecifique() {
		//rien
	}
	
	@Override
	public final int getDirectionImposee() {
		return this.direction;
	}
	
	@Override
	public final String toString() {
		return "Sauter de "+this.x+" en X et de "+this.y+" en Y";
	}
	
}
