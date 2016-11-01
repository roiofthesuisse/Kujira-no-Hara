package mouvements;

import java.util.HashMap;

import main.Fenetre;
import map.Event;
import map.Heros;
import map.Event.Direction;

/**
 * Déplacer un Event dans une Direction et d'un certain nombre de cases.
 */
public class Avancer extends Mouvement {	
	protected int direction;
	
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
			  (int) parametres.get("nombreDeCarreaux")*Fenetre.TAILLE_D_UN_CARREAU );
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
				event.y += event.vitesseActuelle; 
				break;
			case Direction.GAUCHE : 
				event.x -= event.vitesseActuelle; 
				break;
			case Direction.DROITE : 
				event.x += event.vitesseActuelle; 
				break;
			case Direction.HAUT : 
				event.y -= event.vitesseActuelle; 
				break;
		}
		this.ceQuiAEteFait += event.vitesseActuelle;
	}
	
	/**
	 * Le mouvement dans cette Direction est-il possible ?
	 * @return si le mouvement est possible oui ou non
	 */
	@Override
	public boolean mouvementPossible() {
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
		int xAInspecter = event.x;
		int yAInspecter = event.y;
		switch(this.direction) {
		case Event.Direction.BAS : 
			yAInspecter += event.vitesseActuelle; 
			break;
		case Event.Direction.GAUCHE : 
			xAInspecter -= event.vitesseActuelle; 
			break;
		case Event.Direction.DROITE : 
			xAInspecter += event.vitesseActuelle; 
			break;
		case Event.Direction.HAUT : 
			yAInspecter -= event.vitesseActuelle; 
			break;
		default : 
			break;
		}
		return event.map.calculerSiLaPlaceEstLibre(xAInspecter, yAInspecter, event.largeurHitbox, event.hauteurHitbox, event.numero);
	}

	@Override
	protected final void terminerLeMouvementSpecifique(final Event event) {
		event.avance = false;
	}

	@Override
	protected final void ignorerLeMouvementSpecifique(final Event event) {
		//même si Avancer est impossible (mur...), l'Event regarde dans la direction du Mouvement
		mettreEventDansLaDirectionDuMouvement();
		
		if (!event.animeALArretActuel && !event.avancaitALaFramePrecedente && !event.avance) {
			//l'event ne bouge plus depuis 2 frames, on arrête son animation
			event.animation = 0; 
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
	 * Calcule la direction opposée.
	 * @param dir direction à inverser
	 * @return direction opposée
	 */
	protected final int calculerDirectionOpposee(final int dir) {
		switch(dir) {
			case Direction.BAS:
				return Direction.HAUT;
			case Direction.HAUT:
				return Direction.BAS;
			case Direction.GAUCHE:
				return Direction.DROITE;
			case Direction.DROITE:
				return Direction.GAUCHE;
		}
		return -1;
	}
	
}
