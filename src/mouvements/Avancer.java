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
 * D�placer un Event dans une Direction et d'un certain nombre de cases.
 */
public class Avancer extends Mouvement {
	protected static final Logger LOG = LogManager.getLogger(Avancer.class);
	private static final int TOLERANCE_COIN = Main.TAILLE_D_UN_CARREAU /2;
	
	/** Direction dans laquelle l'Event doit avancer */
	protected int direction;
	/** Si l'Event marche vers un coin, on le d�cale l�g�rement pour qu'il puisse passer */
	protected boolean onPeutContournerUnCoin;
	/** D�calage de l'Event pour l'aider a franchir un coin */
	protected int realignementX, realignementY;
	/** Nombre de pixels parcourus a chaque pas */
	private int enjambee;
	
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
	 * Constructeur generique
	 * @param parametres liste de parametres issus de JSON
	 */
	public Avancer(final HashMap<String, Object> parametres) {
		this( (int) parametres.get("direction"), 
			  (int) (parametres.containsKey("nombreDeCarreaux") ? parametres.get("nombreDeCarreaux") : 1) * Main.TAILLE_D_UN_CARREAU 
		);
	}
	
	/** 
	 * Applique l'effet du Mouvement sur la Map et les Events.
	 * Puis incr�mente le compteur "ceQuiAEteFait".
	 * @param event subissant le Mouvement
	 */
	@Override
	public void calculDuMouvement(final Event event) {
		event.avance = true;
		
		//d�placement :
		switch (this.direction) {
			case Direction.BAS : 
				event.y += this.enjambee; 
				break;
			case Direction.GAUCHE : 
				event.x -= this.enjambee; 
				break;
			case Direction.DROITE : 
				event.x += this.enjambee; 
				break;
			case Direction.HAUT : 
				event.y -= this.enjambee; 
				break;
		}
		//on actualise la compl�tion du Mouvement
		this.ceQuiAEteFait += this.enjambee;
	}
	
	/**
	 * Le mouvement dans cette Direction est-il possible ?
	 * @return si le mouvement est possible oui ou non
	 */
	@Override
	public boolean mouvementPossible() {
		final Event event = this.deplacement.getEventADeplacer();
		
		//pas besoin d'aller a la vitesse de l'Event si l'objectif est tr�s proche
		this.enjambee = Math.min(this.etapes - this.ceQuiAEteFait, event.vitesseActuelle.valeur);
		
		//si c'est le H�ros, il n'avance pas s'il est en animation d'attaque
		if (event instanceof Heros && ((Heros) event).animationAttaque > 0) { 
			return false;
		}
		
		//si l'Event est lui-m�me traversable, il peut faire son mouvement
		if (event.traversableActuel == Passabilite.PASSABLE) {
			return true;
		}
		
		//o� sera l'Event apres son pas ?
		int xAInspecter = event.x;
		int yAInspecter = event.y;
		switch (this.direction) {
		case Event.Direction.BAS : 
			yAInspecter += this.enjambee; 
			break;
		case Event.Direction.GAUCHE : 
			xAInspecter -= this.enjambee; 
			break;
		case Event.Direction.DROITE : 
			xAInspecter += this.enjambee; 
			break;
		case Event.Direction.HAUT : 
			yAInspecter -= this.enjambee; 
			break;
		}
		//calcul des collisions avec le d�cor et les autres Events
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
			// L'Event ne peut pas avancer a cause d'un obstacle infranchissable
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
		// M�me si Avancer est impossible (mur...), l'Event regarde dans la direction du Mouvement
		mettreEventDansLaDirectionDuMouvement();
		
		if (this.onPeutContournerUnCoin) {
			// Contournement d'un coin
			contournerUnCoin(event, this.realignementX, this.realignementY);
			this.onPeutContournerUnCoin = false;
			this.realignementX = 0;
			this.realignementY = 0;
			
		} else {
			// L'event ne bouge plus depuis 2 frames, on arr�te son animation
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
	 * Si l'Event ne peut pas avancer parce qu'il d�borde l�g�rement sur un coin, on le r�aligne pour l'aider a passer.
	 * @param xAInspecter coordonn�e X o� l'Event voudrait aller
	 * @param yAInspecter coordonn�e Y o� l'Event voudrait aller
	 * @param event qui veut avancer
	 * @return true si on peut l'aider a contourner le coin, false sinon
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
				// On peut contourner le coin en d�calant un peu l'Event
				return event.map.calculerSiLaPlaceEstLibre(xAInspecterApresRealignement, event.y, event.largeurHitbox, event.hauteurHitbox, event.id); //on peut r�aligner l'Event
			} else {
				// On essaye de contourner un coin droit
				xAInspecterApresRealignement = (xAInspecter/Main.TAILLE_D_UN_CARREAU + 1) * Main.TAILLE_D_UN_CARREAU;
				this.realignementX = xAInspecterApresRealignement - xAInspecter;
				if (event.map.calculerSiLaPlaceEstLibre(xAInspecterApresRealignement, yAInspecter, event.largeurHitbox, event.hauteurHitbox, event.id) //c'est un coin
						&& Math.abs(this.realignementX) <= TOLERANCE_COIN) //le coin est petit
				{
					// On peut contourner le coin en d�calant un peu l'Event
					return event.map.calculerSiLaPlaceEstLibre(xAInspecterApresRealignement, event.y, event.largeurHitbox, event.hauteurHitbox, event.id); //on peut r�aligner l'Event
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
				// On peut contourner le coin en d�calant un peu l'Event
				return event.map.calculerSiLaPlaceEstLibre(event.x, yAInspecterApresRealignement, event.largeurHitbox, event.hauteurHitbox, event.id); //on peut r�aligner l'Event
			} else {
				// On essaye de contourner un coin bas
				yAInspecterApresRealignement = (yAInspecter/Main.TAILLE_D_UN_CARREAU + 1) * Main.TAILLE_D_UN_CARREAU;
				this.realignementY = yAInspecterApresRealignement - yAInspecter;
				if (event.map.calculerSiLaPlaceEstLibre(xAInspecter, yAInspecterApresRealignement, event.largeurHitbox, event.hauteurHitbox, event.id) //c'est un coin
						&& Math.abs(this.realignementY) <= TOLERANCE_COIN) //le coin est petit
				{
					// On peut contourner le coin en d�calant un peu l'Event
					return event.map.calculerSiLaPlaceEstLibre(event.x, yAInspecterApresRealignement, event.largeurHitbox, event.hauteurHitbox, event.id); //on peut r�aligner l'Event
				}
			}
			// L'obstacle n'est pas un coin que l'on peut contourner
			return false;
			
		default : 
			return false;
		}
	}
	
	/**
	 * Aider l'Event a contourner un coin.
	 * @param event qui veut avancer
	 * @param realignementX d�calage en X pour ne pas qu'il soit bloqu� sur le coin
	 * @param realignementY d�calage en Y pour ne pas qu'il soit bloqu� sur le coin
	 */
	public static void contournerUnCoin(final Event event, int realignementX, int realignementY) {
		// On contourne un coin
		if (realignementX != 0) {
			// Le r�alignement ne se fait pas plus rapidement que la vitesse de l'Event
			if (Math.abs(realignementX) > event.vitesseActuelle.valeur) {
				final int signeRealigmement = Math.abs(realignementX) / realignementX; //-1 ou +1
				realignementX = event.vitesseActuelle.valeur * signeRealigmement;
			}
			// On r�aligne l'Event pour qu'il puisse contourner un coin
			event.x += realignementX;
			
		} else if (realignementY != 0) {
			// Le r�alignement ne se fait pas plus rapidement que la vitesse de l'Event
			if (Math.abs(realignementY) > event.vitesseActuelle.valeur) {
				final int signeRealigmement = Math.abs(realignementY) / realignementY; //-1 ou +1
				realignementY = event.vitesseActuelle.valeur * signeRealigmement;
			}
			// On r�aligne l'Event pour qu'il puisse contourner un coin
			event.y += Math.abs(realignementY) <= Math.abs(event.vitesseActuelle.valeur) 
					? realignementY 
					: event.vitesseActuelle.valeur;
		}
		LOG.debug("realignement de l'event id:"+event.id+" x:"+realignementX+" y:"+realignementY);
	}
	
}
