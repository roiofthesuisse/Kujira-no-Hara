package mouvements;

import java.util.HashMap;

import main.Fenetre;
import map.Event;
import map.LecteurMap;
import map.Event.Direction;

/**
 * Sauter d'un nombre de cases vers un autre Event
 *
 */
public class SauterVersEvent extends Sauter {
	
	private int idEventObserve;
	private int nombreDeCases;
	//private boolean directionDecidee;
	private int directionPossibleVerticale;
	private int directionPossibleHorizontale;

	/**
	 * Constructeur explicite
	 * @param idEventObserve : id de l'Event vers lequel on saute
	 * @param nombreDeCases : nombre de cases dont on se rapproche
	 */
	public SauterVersEvent(final int idEventObserve, final int nombreDeCases) {
		super(0, 0);
		this.idEventObserve = idEventObserve;
		this.nombreDeCases = nombreDeCases;
		//this.directionDecidee = false;
	}

	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public SauterVersEvent(final HashMap<String, Object> parametres) {
		this((int) parametres.get("idEventObserve"), (int) parametres.get("nombreDeCases"));
	}

	/**
	 * Le mouvement dans cette Direction est-il possible ?
	 * @return si le mouvement est possible oui ou non
	 */
	@Override
	public final boolean mouvementPossible() {
		//if (!this.directionDecidee) { //ne calculer la direction qu'une seule fois par pas
			//calcul de la direction à prendre
			final Event eventObservateur = this.deplacement.getEventADeplacer();
			final Event eventObserve = ((LecteurMap) Fenetre.getFenetre().lecteur).map.eventsHash.get((Integer) this.idEventObserve);
			int distanceVerticale;
			int distanceHorizontale;
			System.out.println(nombreDeCases);
			for(int i=0;i<nombreDeCases;i++){
				distanceVerticale = eventObservateur.y - eventObserve.y;
				distanceHorizontale = eventObservateur.x - eventObserve.x;
				System.out.println("x="+x+" y="+y);
				calculerDirection(distanceVerticale, distanceHorizontale);
				switch(this.direction){
				case Direction.BAS:
					this.y += 1;
					System.out.println("case bas y ="+y);
					break;
				case Direction.HAUT:
					this.y -= 1;
					break;
				case Direction.GAUCHE:
					this.x -= 1;
					System.out.println("case gauche x ="+x);
					break;
				case Direction.DROITE:
					this.x += 1;
					break;
				}
				if (!super.mouvementPossible()) {
					essayerAutreDirection();
				}
			}
			
			//this.directionDecidee = true; 
		//}
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
	}
	
	@Override
	protected final void reinitialiserSpecifique() {
		super.reinitialiserSpecifique();
		//this.directionDecidee = false;
		this.directionPossibleHorizontale = -1;
		this.directionPossibleVerticale = -1;
		this.x=0;
		this.y=0;
	}
	
}
