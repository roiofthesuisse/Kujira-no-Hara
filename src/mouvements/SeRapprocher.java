package mouvements;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import map.Event;
import map.Vitesse;
import utilitaire.Maths;

/**
 * Rapprocher un Event aupr�s d'un autre, jusqu'� ce qu'ils soient face a face.
 */
public class SeRapprocher extends Avancer {
	protected static final Logger LOG = LogManager.getLogger(SeRapprocher.class);
	private static final int VITESSE_DE_RAPPROCHEMENT = Vitesse.LENTE.valeur;
	
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
		//le -1 est bidon, il sera remplac� par la direction de l'Event lors de la v�rification
		super(-1, 1);
		
		this.idEventARapprocher = idEventARapprocher;
		this.idEventCible = idEventCible;
		this.directionDurantLeMouvement = -1;
		this.initialisation = false;
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public SeRapprocher(final HashMap<String, Object> parametres) {
		this(
				parametres.containsKey("idEventARapprocher") ? (int) parametres.get("idEventARapprocher") : 0, //par d�faut, le H�ros
				parametres.containsKey("idEventCible") ? (int) parametres.get("idEventCible") : null //par d�faut, cet Event
		);
	}

	@Override
	public final boolean mouvementPossible() {
		if (!this.initialisation) {
			// Initialisation
			this.eventARapprocher = this.deplacement.page.event.map.eventsHash.get(this.idEventARapprocher);
			this.eventCible = this.deplacement.page.event.map.eventsHash.get(this.idEventCible);
			
			// Coordonn�es de d�part
			this.xInitialEventARapprocher = this.eventARapprocher.x;
			this.yInitialEventARapprocher = this.eventARapprocher.y;
			
			// Calcul des coordonn�es d'arriv�e
			final int xmin1 = this.eventARapprocher.x;
			final int xmax1 = xmin1 + this.eventARapprocher.largeurHitbox;
			final int ymin1 = this.eventARapprocher.y;
			final int ymax1 = ymin1 + this.eventARapprocher.hauteurHitbox;
			final int xmin2 = this.eventCible.x;
			final int xmax2 = xmin2 + this.eventCible.largeurHitbox;
			final int ymin2 = this.eventCible.y;
			final int ymax2 = ymin2 + this.eventCible.hauteurHitbox;
			// O� se situe-t-on par rapport a l'Event cible ?
			if (xmax1 <= xmin2) {
				//on est a gauche
				this.xFinalEventARapprocher = xmin2 - this.eventARapprocher.largeurHitbox;
				this.yFinalEventARapprocher = (ymin2+ymax2 - this.eventARapprocher.hauteurHitbox)/2;
				this.directionDurantLeMouvement = Event.Direction.DROITE;
				
			} else if (xmin1 >= xmax2) {
				//on est a droite
				this.xFinalEventARapprocher = xmax2;
				this.yFinalEventARapprocher = (ymin2+ymax2 - this.eventARapprocher.hauteurHitbox)/2;
				this.directionDurantLeMouvement = Event.Direction.GAUCHE;
				
			} else if (ymax1 <= ymin2) {
				//on est en haut
				this.xFinalEventARapprocher = (xmin2+xmax2 - this.eventARapprocher.largeurHitbox)/2;
				this.yFinalEventARapprocher = ymin2 - this.eventARapprocher.hauteurHitbox;
				this.directionDurantLeMouvement = Event.Direction.BAS;
				
			} else if (ymin1 >= ymax2) {
				//on est en bas
				this.xFinalEventARapprocher = (xmin2+xmax2 - this.eventARapprocher.largeurHitbox)/2;
				this.yFinalEventARapprocher = ymax2;
				this.directionDurantLeMouvement = Event.Direction.HAUT;
				
			} else {
				//cas impossible si l'event est solide
				LOG.warn("Positions relatives anormales des events "+this.idEventARapprocher+" et "+this.idEventCible);
				return false;
			}
			
			// Nombre d'�tapes ?
			final int trajetX = this.xFinalEventARapprocher - this.xInitialEventARapprocher;
			final int trajetY = this.yFinalEventARapprocher - this.yInitialEventARapprocher;
			this.etapes = Maths.max(
					(int) Math.abs(Math.ceil(trajetX/VITESSE_DE_RAPPROCHEMENT)),
					(int) Math.abs(Math.ceil(trajetY/VITESSE_DE_RAPPROCHEMENT)));
			
			this.initialisation = true;
		}
		
		if (this.etapes == 0) {
			// Il n'y a rien a faire : on est deja en face de l'interlocuteur
			return true;
		} else {
		
			// Est-ce possible ?
			final int nouveauX = (this.xInitialEventARapprocher*(this.etapes-this.ceQuiAEteFait) 
					+ this.xFinalEventARapprocher*this.ceQuiAEteFait)/this.etapes;
			final int nouveauY = (this.yInitialEventARapprocher*(this.etapes-this.ceQuiAEteFait) 
					+ this.yFinalEventARapprocher*this.ceQuiAEteFait)/this.etapes;
			final boolean mouvementPossible = this.eventARapprocher.map.calculerSiLaPlaceEstLibre(nouveauX, 
					nouveauY, 
					this.eventARapprocher.largeurHitbox, 
					this.eventARapprocher.hauteurHitbox, 
					this.eventARapprocher.id);
	
			if (mouvementPossible) {
				return true;
			} else {
				LOG.error("Impossible de se rapprocher de l'interlocuteur !");
				this.initialisation = false;
				this.directionDurantLeMouvement = -1;
				return false;
			}
		}
	}
	
	@Override
	public final void calculDuMouvement(final Event event) {
		// On se rapproche de l'interlocuteur si besoin
		if (this.etapes > 0) {
			event.avance = true;
			this.ceQuiAEteFait++; // on incremente tout de suite, sinon la premiere etape ne fait rien
			LOG.debug("On est en train de se rapprocher de l'interlocuteur... (etape "+this.ceQuiAEteFait+"/"+this.etapes+")");
			
			event.x = (this.xInitialEventARapprocher*(this.etapes-this.ceQuiAEteFait) + this.xFinalEventARapprocher*this.ceQuiAEteFait)/this.etapes;
			event.y = (this.yInitialEventARapprocher*(this.etapes-this.ceQuiAEteFait) + this.yFinalEventARapprocher*this.ceQuiAEteFait)/this.etapes;
			LOG.debug("Rapprochement de "+this.xInitialEventARapprocher+";"+this.yInitialEventARapprocher
					+" vers "+event.x+";"+event.y);
		}
		
		// Il faudra r�initialiser le mouvement la prochaine fois
		if (this.ceQuiAEteFait >= this.etapes) {
			LOG.debug("On s'est rapproch� de l'interlocuteur.");
			this.initialisation = false;
			this.directionDurantLeMouvement = -1;
		}
	}
	
	@Override
	public final int getDirectionImposee() {
		return this.directionDurantLeMouvement;
	}
}
