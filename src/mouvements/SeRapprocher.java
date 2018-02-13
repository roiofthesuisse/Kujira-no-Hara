package mouvements;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import map.Event;
import utilitaire.Maths;

/**
 * Rapprocher un Event auprès d'un autre, jusqu'à ce qu'ils soient face à face.
 */
public class SeRapprocher extends Avancer {
	protected static final Logger LOG = LogManager.getLogger(SeRapprocher.class);
	
	private final Integer idEventARapprocher;
	private final Integer idEventCible;
	private Event eventARapprocher;
	private Event eventCible;
	private boolean initialisation;
	private int directionDurantLeMouvement;
	private int xInitialEventARapprocher;
	private int yInitialEventARapprocher;
	private int xFinalEventARapprocher;
	private int yFinalEventARapprocher;
	
	/**
	 * Constructeur explicite
	 * @param idEventARapprocher Event qui doit se rapprocher d'un autre
	 * @param idEventCible Event vers lequel on se rapproche
	 */
	public SeRapprocher(final Integer idEventARapprocher, final Integer idEventCible) {
		//le -1 est bidon, il sera remplacé par la direction de l'Event lors de la vérification
		super(-1, 1);
		
		this.idEventARapprocher = idEventARapprocher;
		this.idEventCible = idEventCible;
		this.initialisation = false;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public SeRapprocher(final HashMap<String, Object> parametres) {
		this(
				parametres.containsKey("idEventARapprocher") ? (int) parametres.get("idEventARapprocher") : 0, //par défaut, le Héros
				parametres.containsKey("idEventCible") ? (int) parametres.get("idEventCible") : null //par défaut, cet Event
		);
	}

	@Override
	public final boolean mouvementPossible() {
		if (!initialisation) {
			// Initialisation
			this.eventARapprocher = this.deplacement.page.event.map.eventsHash.get(this.idEventARapprocher);
			this.eventCible = this.deplacement.page.event.map.eventsHash.get(this.idEventCible);
			
			// Coordonnées de départ
			this.xInitialEventARapprocher = this.eventARapprocher.x;
			this.yInitialEventARapprocher = this.eventARapprocher.y;
			this.directionDurantLeMouvement = this.eventARapprocher.direction;
			
			// Calcul des coordonnées d'arrivée
			final int xmin1 = this.eventARapprocher.x;
			final int xmax1 = xmin1 + this.eventARapprocher.largeurHitbox;
			final int ymin1 = this.eventARapprocher.y;
			final int ymax1 = ymin1 + this.eventARapprocher.hauteurHitbox;
			final int xmin2 = this.eventCible.x;
			final int xmax2 = xmin2 + this.eventCible.largeurHitbox;
			final int ymin2 = this.eventCible.y;
			final int ymax2 = ymin2 + this.eventCible.hauteurHitbox;
			// Où se situe-t-on par rapport à l'Event cible ?
			if (xmax1 <= xmin2) {
				//on est à gauche
				this.xFinalEventARapprocher = xmin2 - eventARapprocher.largeurHitbox;
				this.yFinalEventARapprocher = (ymin2+ymax2 - eventARapprocher.hauteurHitbox)/2;
				
			} else if (xmin1 >= xmax2) {
				//on est à droite
				this.xFinalEventARapprocher = xmax2;
				this.yFinalEventARapprocher = (ymin2+ymax2 - eventARapprocher.hauteurHitbox)/2;
				
			} else if (ymax1 <= ymin2) {
				//on est en haut
				this.xFinalEventARapprocher = (xmin2+xmax2 - eventARapprocher.largeurHitbox)/2;
				this.yFinalEventARapprocher = ymin2 - eventARapprocher.hauteurHitbox;
				
			} else if (ymin1 >= ymax2) {
				//on est en bas
				this.xFinalEventARapprocher = (xmin2+xmax2 - this.eventARapprocher.largeurHitbox)/2;
				this.yFinalEventARapprocher = ymax2;
				
			} else {
				//cas théoriquement impossible
				LOG.warn("Positions relatives anormales des events "+this.idEventARapprocher+" et "+this.idEventCible);
				return false;
			}
			
			// Nombre d'étapes ?
			final int trajetX = this.xFinalEventARapprocher - this.xInitialEventARapprocher;
			final int trajetY = this.yFinalEventARapprocher - this.yInitialEventARapprocher;
			this.etapes = Maths.max(1,
					Math.abs(trajetX/this.eventARapprocher.vitesseActuelle),
					Math.abs(trajetY/this.eventARapprocher.vitesseActuelle));
		
			// Est-ce possible ?
			final int nouveauX = this.eventARapprocher.x + Maths.min(trajetX, this.eventARapprocher.vitesseActuelle);
			final int nouveauY = this.eventARapprocher.y + Maths.min(trajetY, this.eventARapprocher.vitesseActuelle);
			return this.eventARapprocher.map.calculerSiLaPlaceEstLibre(nouveauX, 
					nouveauY, 
					this.eventARapprocher.largeurHitbox, 
					this.eventARapprocher.hauteurHitbox, 
					this.eventARapprocher.id);
		}
		
		return true;
		
	}
	
	@Override
	public final void calculDuMouvement(final Event event) {
		event.avance = true;
		
		//TODO
		
		this.ceQuiAEteFait++;
	}
	
	@Override
	public final int getDirectionImposee() {
		return this.directionDurantLeMouvement;
	}
}
