package mouvements;

import java.util.HashMap;

import main.Fenetre;
import map.Event;
import map.Heros;
import map.Hitbox;
import map.Event.Direction;

/**
 * Déplacer un Event dans une Direction et d'un certain nombre de cases
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
	 * Obtenir la Direction du mouvement
	 * @return la Direction du mouvement
	 */
	public final int getDirection() {
		return direction;
	}
	
	/** 
	 * Applique l'effet du Mouvement sur la Map et les Events.
	 * Puis incrémente le compteur "ceQuiAEteFait".
	 * @param event subissant le Mouvement
	 */
	@Override
	public final void calculDuMouvement(final Event event) {
		final int sens = this.getDirection();
		
		event.avance = true;
		//déplacement :
		switch (sens) {
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
	public final boolean mouvementPossible() {
		final Event event = this.deplacement.getEventADeplacer();
		final int sens = this.getDirection();
		
		//si c'est le Héros, il n'avance pas s'il est en animation d'attaque
		if (event instanceof Heros && ((Heros) event).animationAttaque > 0) { 
			return false;
		}
		
		//si l'Event est lui-même traversable, il peut faire son mouvement
		if (event.traversableActuel) {
			return true;
		}
		
		boolean reponse = true;
		int xAInspecter = event.x; //pour le décor
		int yAInspecter = event.y;
		int xAInspecter2 = event.x; //pour le décor, deuxième case à vérifier si entre deux cases
		int yAInspecter2 = event.y;
		int xAInspecter3 = event.x; //pour les events
		int yAInspecter3 = event.y;
		switch(sens) {
		case Event.Direction.BAS : 
			yAInspecter += event.hauteurHitbox;   
			yAInspecter2 += event.hauteurHitbox;   
			xAInspecter2 += Fenetre.TAILLE_D_UN_CARREAU; 
			yAInspecter3 += event.vitesseActuelle; 
			break;
		case Event.Direction.GAUCHE : 
			xAInspecter -= event.vitesseActuelle; 
			xAInspecter2 -= event.vitesseActuelle; 
			yAInspecter2 += Fenetre.TAILLE_D_UN_CARREAU; 
			xAInspecter3 -= event.vitesseActuelle; 
			break;
		case Event.Direction.DROITE : 
			xAInspecter += event.largeurHitbox;   
			xAInspecter2 += event.largeurHitbox;   
			yAInspecter2 += Fenetre.TAILLE_D_UN_CARREAU; 
			xAInspecter3 += event.vitesseActuelle; 
			break;
		case Event.Direction.HAUT : 
			yAInspecter -= event.vitesseActuelle; 
			yAInspecter2 -= event.vitesseActuelle; 
			xAInspecter2 += Fenetre.TAILLE_D_UN_CARREAU; 
			yAInspecter3 -= event.vitesseActuelle; 
			break;
		default : 
			break;
		}
		try {
			//si rencontre avec un élément de décor non passable -> false
			if (!event.map.casePassable[xAInspecter/Fenetre.TAILLE_D_UN_CARREAU][yAInspecter/Fenetre.TAILLE_D_UN_CARREAU]) {
				return false;
			}
			if ((sens==Direction.BAS||sens==Direction.HAUT) && ((event.x+event.largeurHitbox-1)/Fenetre.TAILLE_D_UN_CARREAU!=(event.x/Fenetre.TAILLE_D_UN_CARREAU)) && !event.map.casePassable[xAInspecter2/Fenetre.TAILLE_D_UN_CARREAU][yAInspecter2/Fenetre.TAILLE_D_UN_CARREAU]) {
				return false;
			}
			if ((sens==Direction.GAUCHE||sens==Direction.DROITE) && ((event.y+event.hauteurHitbox-1)/Fenetre.TAILLE_D_UN_CARREAU!=(event.y/Fenetre.TAILLE_D_UN_CARREAU)) && !event.map.casePassable[xAInspecter2/Fenetre.TAILLE_D_UN_CARREAU][yAInspecter2/Fenetre.TAILLE_D_UN_CARREAU]) {
				return false;
			}
			//voilà
			
			//si rencontre avec un autre évènement non traversable -> false
			int xmin1 = xAInspecter3;
			int xmax1 = xAInspecter3 + event.largeurHitbox;
			int ymin1 = yAInspecter3;
			int ymax1 = yAInspecter3 + event.hauteurHitbox;
			int xmin2;
			int xmax2;
			int ymin2;
			int ymax2;
			for (Event autreEvent : event.map.events) {
				xmin2 = autreEvent.x;
				xmax2 = autreEvent.x + autreEvent.largeurHitbox;
				ymin2 = autreEvent.y;
				ymax2 = autreEvent.y + autreEvent.hauteurHitbox;
				if (event.numero != autreEvent.numero 
					&& !autreEvent.traversableActuel
					&& Hitbox.lesDeuxRectanglesSeChevauchent(xmin1, xmax1, ymin1, ymax1, xmin2, xmax2, ymin2, ymax2, event.largeurHitbox, event.hauteurHitbox, autreEvent.largeurHitbox, autreEvent.hauteurHitbox) 
				) {
					return false;
				}
			}
		} catch (Exception e) {
			//on sort de la map !
			e.printStackTrace();
			reponse = true;
		}
		return reponse;
	}

	@Override
	protected final void terminerLeMouvementSpecifique(final Event event) {
		event.avance = false;
	}

	@Override
	protected final void ignorerLeMouvementSpecifique(final Event event) {
		event.avance = false;
	}

	@Override
	protected void reinitialiserSpecifique() {
		// rien
	}
	
	@Override
	public final String toString() {
		return "Avancer "+this.etapes+" pixels vers "+this.direction;
	}
	
}
