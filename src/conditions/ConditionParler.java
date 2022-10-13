package conditions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import commandes.CommandeEvent;
import map.Event;
import map.Heros;
import utilitaire.GestionClavier;

/**
 * Le H�ros colle l'event et regarde vers lui.
 */
public class ConditionParler extends Condition implements CommandeEvent {
	//constantes
	private static final Logger LOG = LogManager.getLogger(ConditionParler.class);
	public static final int DISTANCE_MAX_PAROLE = 4; //au dela de cette distance en pixels, le dialogue ne se d�clenche pas
	
	@Override
	public final boolean estVerifiee() {
		//1) pour Parler, premi�rement, il faut appuyer sur la touche action :
		final ConditionTouche conditionToucheAction = new ConditionTouche(-1, GestionClavier.ToucheRole.ACTION.nom, false);
		conditionToucheAction.page = this.page;
		if (!conditionToucheAction.estVerifiee()) {
			return false;
		}
		
		//2) deuxi�mement, il faut �tre situ� face a son interlocuteur :
		
		int pageActive;
		try {
			pageActive = this.page.event.pageActive.numero;
		} catch (NullPointerException e) {
			//pas de page actuelle pour le lecteur
			pageActive = -1;
		}
		final int cettePage = this.page.numero;
		//il faut d'abord que la page ne soit pas ouverte
		if (pageActive!=cettePage) { //TODO v�rifier si cette condition est utile
			final Event event = this.page.event;
			final Heros heros = event.map.heros;
			final int xmin1 = heros.x;
			final int xmax1 = heros.x+heros.largeurHitbox;
			final int ymin1 = heros.y;
			final int ymax1 = heros.y+heros.hauteurHitbox;
			final int xmin2 = event.x;
			final int xmax2 = event.x+event.largeurHitbox;
			final int ymin2 = event.y;
			final int ymax2 = event.y+event.hauteurHitbox;
			final int dir = heros.direction;
			//il faut �tre coll� a l'�v�nement et regarder vers lui
			switch(dir) {
				case Event.Direction.HAUT:
					if ( Math.abs(ymin1-ymax2) > DISTANCE_MAX_PAROLE ) {
						return false;
					} else {
						if ( heros.largeurHitbox <= event.largeurHitbox ) { 
							//la longueur de contact est sup�rieure a la moiti� de la taille du h�ros
							final boolean grandeSurfaceDeContact =
								((xmin2<xmax1 && xmax1<=xmax2) && xmax1-xmin2>heros.largeurHitbox/2) 
								|| ((xmin2<=xmin1 && xmin1<xmax2) && xmax2-xmin1>heros.largeurHitbox/2);
							return grandeSurfaceDeContact;
						} else {
							//le h�ros englobe l'event
							final boolean surfaceDeContactMaximale =
							 (xmin1<=xmin2 && xmax2<=xmax1);
							return surfaceDeContactMaximale;
						}
					}
				case Event.Direction.GAUCHE:
					if ( Math.abs(xmin1-xmax2) > DISTANCE_MAX_PAROLE ) {
						return false;
					} else {
						if ( heros.hauteurHitbox <= event.hauteurHitbox ) { 
							//la longueur de contact est sup�rieure a la moiti� de la taille du h�ros
							final boolean grandeSurfaceDeContact =
								((ymin2<ymax1 && ymax1<=ymax2) && ymax1-ymin2>heros.hauteurHitbox/2) 
								|| ((ymin2<=ymin1 && ymin1<ymax2) && ymax2-ymin1>heros.hauteurHitbox/2);
							return grandeSurfaceDeContact;
						} else {
							//le h�ros englobe l'event
							final boolean surfaceDeContactMaximale =
								(ymin1<=ymin2 && ymax2<=ymax1);
							return surfaceDeContactMaximale;
						}
					}
				case Event.Direction.DROITE:
					if ( Math.abs(xmax1-xmin2) > DISTANCE_MAX_PAROLE ) {
						return false;
					} else {
						if ( heros.hauteurHitbox <= event.hauteurHitbox ) { 
							//la longueur de contact est sup�rieure a la moiti� de la taille du h�ros
							final boolean grandeSurfaceDeContact =
								((ymin2<ymax1 && ymax1<=ymax2) && ymax1-ymin2>heros.hauteurHitbox/2) 
								|| ((ymin2<=ymin1 && ymin1<ymax2) && ymax2-ymin1>heros.hauteurHitbox/2);
							return grandeSurfaceDeContact;
						} else {
							//le h�ros englobe l'event
							final boolean surfaceDeContactMaximale =
								(ymin1<=ymin2 && ymax2<=ymax1);
							return surfaceDeContactMaximale;
						}
					}
				default: //HAUT
					if ( Math.abs(ymax1-ymin2) > DISTANCE_MAX_PAROLE ) {
						return false;
					} else {
						if ( heros.largeurHitbox <= event.largeurHitbox ) {
							//la longueur de contact est sup�rieure a la moiti� de la taille du h�ros
							final boolean grandeSurfaceDeContact =
								((xmin2<xmax1 && xmax1<=xmax2) && xmax1-xmin2>heros.largeurHitbox/2) 
								|| ((xmin2<=xmin1 && xmin1<xmax2) && xmax2-xmin1>heros.largeurHitbox/2);
							return grandeSurfaceDeContact;
						} else {
							//le h�ros englobe l'event
							final boolean surfaceDeContactMaximale =
								(xmin1<=xmin2 && xmax2<=xmax1);
							return surfaceDeContactMaximale;
						}
					}
			}
		}
		LOG.debug("ConditionParler.estVerifiee() n'a pas trouv� de cas correspondant.");
		return false;
	}
	
	/**
	 * C'est une Condition qui implique une proximit� avec le H�ros.
	 * @return false 
	 */
	public final boolean estLieeAuHeros() {
		return true;
	}
	
	public final void sePlacerBienEnFaceDeLInterlocuteur() {
		//TODO a faire avant chaque discussion
	}

}
