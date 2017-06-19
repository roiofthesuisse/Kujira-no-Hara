package mouvements;

import java.util.HashMap;

import main.Fenetre;
import map.Event;
import map.Heros;
import map.Event.Direction;

/**
 * Déplacer un Event d'un pas en diagonale
 */
public class PasEnDiagonale extends Avancer {
	int directionVerticale;
	int directionHorizontale;

	/**
	 * Constructeur explicite
	 * @param directionVerticale composante verticale de la diagonale
	 * @param directionHorizontale composante horizontale de la diagonale 
	 * @param nombreDePixels distance parcourue
	 */
	public PasEnDiagonale(final int directionVerticale, final int directionHorizontale, final int nombreDePixels) {
		super(-1, nombreDePixels);
		this.directionVerticale = directionVerticale;
		this.directionHorizontale = directionHorizontale;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public PasEnDiagonale(final HashMap<String, Object> parametres) {
		this( (int) parametres.get("directionVerticale"),
			  (int) parametres.get("directionHorizontale"),
			  (parametres.containsKey("nombreDeCarreaux") ? (int) parametres.get("parametres") : 1) * Fenetre.TAILLE_D_UN_CARREAU
		);
	}
	
	/** 
	 * Applique l'effet du Mouvement sur la Map et les Events.
	 * Puis incrémente le compteur "ceQuiAEteFait".
	 * @param event subissant le Mouvement
	 */
	@Override
	public final void calculDuMouvement(final Event event) {
		event.avance = true;
		
		//déplacement :
		switch (this.directionHorizontale) {
			case Direction.GAUCHE : 
				event.x -= event.vitesseActuelle; 
				break;
			case Direction.DROITE : 
				event.x += event.vitesseActuelle; 
				break;
		}
		switch (this.directionVerticale) {
			case Direction.BAS : 
				event.y += event.vitesseActuelle; 
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
		int xAInspecter = event.x;
		int yAInspecter = event.y;
		switch (this.directionVerticale) {
		case Event.Direction.BAS : 
			yAInspecter += event.vitesseActuelle; 
			break;
		case Event.Direction.HAUT : 
			yAInspecter -= event.vitesseActuelle; 
			break;
		}
		switch (this.directionHorizontale) {
		case Event.Direction.GAUCHE : 
			xAInspecter -= event.vitesseActuelle; 
			break;
		case Event.Direction.DROITE : 
			xAInspecter += event.vitesseActuelle; 
			break;
		}
		return event.map.calculerSiLaPlaceEstLibre(xAInspecter, yAInspecter, event.largeurHitbox, event.hauteurHitbox, event.id);
	}

	@Override
	public final String toString() {
		return "Un pas en diagonale vers "+this.directionVerticale+" et "+this.directionHorizontale;
	}

	@Override
	public final int getDirectionImposee() {
		if (this.deplacement.getEventADeplacer().direction == this.directionVerticale) {
			//si l'Event regarde déjà dans une des deux directions de la diagonale, on prend celle-ci
			return this.directionVerticale;
		} else {
			//sinon par défaut on prend la composante horizontale de la diagonale
			return this.directionHorizontale;
		}
	}
	
}