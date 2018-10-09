package mouvements;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.Main;
import map.Event;
import map.Heros;
import map.Passabilite;
import map.Event.Direction;

/**
 * Déplacer un Event dans une Direction et d'un certain nombre de cases.
 */
public class Avancer extends Mouvement {
	protected static final Logger LOG = LogManager.getLogger(Avancer.class);
	private static final int TOLERANCE_COIN = Main.TAILLE_D_UN_CARREAU /2;
	
	/** Direction dans laquelle l'Event doit avancer */
	protected int direction;
	/** Si l'Event marche vers un coin, on le décale légèrement pour qu'il puisse passer */
	protected boolean onPeutContournerUnCoin;
	/** Décalage de l'Event pour l'aider à franchir un coin */
	protected int realignementX, realignementY;
	/** Nombre de pixels parcourus à chaque pas */
	private int vitesse;
	
	/**
	 * Constructeur explicite
	 * @param direction dans laquelle l'Event doit avancer
	 * @param nombreDePixels distance parcourue
	 */
	public Avancer(final int direction, final int nombreDePixels) {
		this.direction = direction;
		this.etapes = nombreDePixels;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public Avancer(final HashMap<String, Object> parametres) {
		this( (int) parametres.get("direction"), 
			  (int) (parametres.containsKey("nombreDeCarreaux") ? parametres.get("nombreDeCarreaux") : 1) * Main.TAILLE_D_UN_CARREAU 
		);
	}
	
	/** 
	 * Applique l'effet du Mouvement sur la Map et les Events.
	 * Puis incrémente le compteur "ceQuiAEteFait".
	 * @param event subissant le Mouvement
	 */
	@Override
	public void calculDuMouvement(final Event event) {
		event.avance = true;
		
		//déplacement :
		switch (this.direction) {
			case Direction.BAS : 
				event.y += this.vitesse; 
				break;
			case Direction.GAUCHE : 
				event.x -= this.vitesse; 
				break;
			case Direction.DROITE : 
				event.x += this.vitesse; 
				break;
			case Direction.HAUT : 
				event.y -= this.vitesse; 
				break;
		}
		this.ceQuiAEteFait += this.vitesse;
	}
	
	/**
	 * Le mouvement dans cette Direction est-il possible ?
	 * @return si le mouvement est possible oui ou non
	 */
	@Override
	public boolean mouvementPossible() {
		final Event event = this.deplacement.getEventADeplacer();
		
		//pas besoin d'aller à la vitesse de l'Event si l'objectif est très proche
		this.vitesse = Math.min(this.etapes, event.vitesseActuelle.valeur);
		
		//si c'est le Héros, il n'avance pas s'il est en animation d'attaque
		if (event instanceof Heros && ((Heros) event).animationAttaque > 0) { 
			return false;
		}
		
		//si l'Event est lui-même traversable, il peut faire son mouvement
		if (event.traversableActuel == Passabilite.PASSABLE) {
			return true;
		}
		
		//collisions avec le décor et les autres Events
		int xAInspecter = event.x;
		int yAInspecter = event.y;
		switch (this.direction) {
		case Event.Direction.BAS : 
			yAInspecter += this.vitesse; 
			break;
		case Event.Direction.GAUCHE : 
			xAInspecter -= this.vitesse; 
			break;
		case Event.Direction.DROITE : 
			xAInspecter += this.vitesse; 
			break;
		case Event.Direction.HAUT : 
			yAInspecter -= this.vitesse; 
			break;
		default : 
			break;
		}
		if (event.map.calculerSiLaPlaceEstLibre(xAInspecter, yAInspecter, event.largeurHitbox, event.hauteurHitbox, event.id)) {
			// Aucun obstacle
			// On peut avancer tout droit
			return true;
		} else if (lObstacleEstUnCoinQueLOnPeutContourner(xAInspecter, yAInspecter, event)) {
			// L'obstacle est un coin que l'on peut contourner
			LOG.info("on peut contourner le coin");
			this.onPeutContournerUnCoin = true;
			return false;
		} else {
			// L'Event ne peut pas avancer à cause d'un obstacle infranchissable
			this.onPeutContournerUnCoin = false;
			return false;
		}
		
	}

	@Override
	protected final void terminerLeMouvementSpecifique(final Event event) {
		event.avance = false;
	}

	@Override
	protected final void ignorerLeMouvementSpecifique(final Event event) {
		// Même si Avancer est impossible (mur...), l'Event regarde dans la direction du Mouvement
		mettreEventDansLaDirectionDuMouvement();
		
		if (this.onPeutContournerUnCoin) {
			// Contournement d'un coin
			contournerUnCoin(event, this.realignementX, this.realignementY);
			this.onPeutContournerUnCoin = false;
			this.realignementX = 0;
			this.realignementY = 0;
			
		} else {
			// L'event ne bouge plus depuis 2 frames, on arrête son animation
			if (!event.animeALArretActuel && !event.avancaitALaFramePrecedente && !event.avance) {
				event.animation = 0; 
			}
		}
	}

	@Override
	protected void reinitialiserSpecifique() {
		// rien
	}
	
	@Override
	public int getDirectionImposee() {
		return this.direction;
	}
	
	@Override
	public String toString() {
		return "Avancer "+this.etapes+" pixels vers "+this.direction;
	}
	
	/**
	 * Si l'Event ne peut pas avancer parce qu'il déborde légèrement sur un coin, on le réaligne pour l'aider à passer.
	 * @param xAInspecter coordonnée X où l'Event voudrait aller
	 * @param yAInspecter coordonnée Y où l'Event voudrait aller
	 * @param event qui veut avancer
	 * @return true si on peut l'aider à contourner le coin, false sinon
	 */
	private boolean lObstacleEstUnCoinQueLOnPeutContourner(final int xAInspecter, final int yAInspecter, final Event event) {
		this.realignementX = 0;
		this.realignementY = 0;
		
		int xAInspecterApresRealignement, yAInspecterApresRealignement;
		
		switch (this.direction) {
		case Event.Direction.BAS :
		case Event.Direction.HAUT : 
			// On essaye de contourner un coin gauche
			xAInspecterApresRealignement = (xAInspecter/Main.TAILLE_D_UN_CARREAU) * Main.TAILLE_D_UN_CARREAU + (Main.TAILLE_D_UN_CARREAU - event.largeurHitbox);
			this.realignementX = xAInspecterApresRealignement - xAInspecter;
			if (event.map.calculerSiLaPlaceEstLibre(xAInspecterApresRealignement, yAInspecter, event.largeurHitbox, event.hauteurHitbox, event.id)  //c'est un coin
					&& Math.abs(this.realignementX) <= TOLERANCE_COIN) //le coin est petit
			{
				// On peut contourner le coin en décalant un peu l'Event
				return event.map.calculerSiLaPlaceEstLibre(xAInspecterApresRealignement, event.y, event.largeurHitbox, event.hauteurHitbox, event.id); //on peut réaligner l'Event
			} else {
				// On essaye de contourner un coin droit
				xAInspecterApresRealignement = (xAInspecter/Main.TAILLE_D_UN_CARREAU + 1) * Main.TAILLE_D_UN_CARREAU;
				this.realignementX = xAInspecterApresRealignement - xAInspecter;
				if (event.map.calculerSiLaPlaceEstLibre(xAInspecterApresRealignement, yAInspecter, event.largeurHitbox, event.hauteurHitbox, event.id) //c'est un coin
						&& Math.abs(this.realignementX) <= TOLERANCE_COIN) //le coin est petit
				{
					// On peut contourner le coin en décalant un peu l'Event
					return event.map.calculerSiLaPlaceEstLibre(xAInspecterApresRealignement, event.y, event.largeurHitbox, event.hauteurHitbox, event.id); //on peut réaligner l'Event
				}
			}
			// L'obstacle n'est pas un coin que l'on peut contourner
			return false;

		case Event.Direction.GAUCHE : 
		case Event.Direction.DROITE : 
			// On essaye de contourner un coin haut
			yAInspecterApresRealignement = (yAInspecter/Main.TAILLE_D_UN_CARREAU) * Main.TAILLE_D_UN_CARREAU + (Main.TAILLE_D_UN_CARREAU - event.hauteurHitbox);
			this.realignementY = yAInspecterApresRealignement - yAInspecter;
			if (event.map.calculerSiLaPlaceEstLibre(xAInspecter, yAInspecterApresRealignement, event.largeurHitbox, event.hauteurHitbox, event.id) //c'est un coin
					&& Math.abs(this.realignementY) <= TOLERANCE_COIN) //le coin est petit
			{
				// On peut contourner le coin en décalant un peu l'Event
				return event.map.calculerSiLaPlaceEstLibre(event.x, yAInspecterApresRealignement, event.largeurHitbox, event.hauteurHitbox, event.id); //on peut réaligner l'Event
			} else {
				// On essaye de contourner un coin bas
				yAInspecterApresRealignement = (yAInspecter/Main.TAILLE_D_UN_CARREAU + 1) * Main.TAILLE_D_UN_CARREAU;
				this.realignementY = yAInspecterApresRealignement - yAInspecter;
				if (event.map.calculerSiLaPlaceEstLibre(xAInspecter, yAInspecterApresRealignement, event.largeurHitbox, event.hauteurHitbox, event.id) //c'est un coin
						&& Math.abs(this.realignementY) <= TOLERANCE_COIN) //le coin est petit
				{
					// On peut contourner le coin en décalant un peu l'Event
					return event.map.calculerSiLaPlaceEstLibre(event.x, yAInspecterApresRealignement, event.largeurHitbox, event.hauteurHitbox, event.id); //on peut réaligner l'Event
				}
			}
			// L'obstacle n'est pas un coin que l'on peut contourner
			return false;
			
		default : 
			return false;
		}
	}
	
	/**
	 * Aider l'Event à contourner un coin.
	 * @param event qui veut avancer
	 * @param realignementX décalage en X pour ne pas qu'il soit bloqué sur le coin
	 * @param realignementY décalage en Y pour ne pas qu'il soit bloqué sur le coin
	 */
	public static void contournerUnCoin(final Event event, int realignementX, int realignementY) {
		// On contourne un coin
		if (realignementX != 0) {
			// Le réalignement ne se fait pas plus rapidement que la vitesse de l'Event
			if (Math.abs(realignementX) > event.vitesseActuelle.valeur) {
				final int signeRealigmement = Math.abs(realignementX) / realignementX; //-1 ou +1
				realignementX = event.vitesseActuelle.valeur * signeRealigmement;
			}
			// On réaligne l'Event pour qu'il puisse contourner un coin
			event.x += realignementX;
			
		} else if (realignementY != 0) {
			// Le réalignement ne se fait pas plus rapidement que la vitesse de l'Event
			if (Math.abs(realignementY) > event.vitesseActuelle.valeur) {
				final int signeRealigmement = Math.abs(realignementY) / realignementY; //-1 ou +1
				realignementY = event.vitesseActuelle.valeur * signeRealigmement;
			}
			// On réaligne l'Event pour qu'il puisse contourner un coin
			event.y += Math.abs(realignementY) <= Math.abs(event.vitesseActuelle.valeur) 
					? realignementY 
					: event.vitesseActuelle.valeur;
		}
		LOG.debug("realignement de l'event id:"+event.id+" x:"+realignementX+" y:"+realignementY);
	}
	
}
