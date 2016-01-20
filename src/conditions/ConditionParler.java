package conditions;

import map.Event;
import map.Heros;

/**
 * Le Héros colle l'event et regarde vers lui.
 */
public class ConditionParler extends Condition {
	public boolean lieeAuHeros = true;
	public static int distanceMaxParole = 4; //au dela de cette distance en pixels, le dialogue ne se déclenche pas
	
	@Override
	public final boolean estVerifiee() {
		//1) pour Parler, premièrement, il faut appuyer sur la touche action :
		final ConditionToucheAction conditionToucheAction = new ConditionToucheAction();
		conditionToucheAction.page = this.page;
		if (!conditionToucheAction.estVerifiee()) {
			return false;
		}
		
		//2) deuxièmement, il faut être situé face à son interlocuteur :
		
		int pageActive;
		try {
			pageActive = this.page.event.pageActive.numero;
		} catch (NullPointerException e) {
			//pas de page actuelle pour le lecteur
			pageActive = -1;
		}
		int cettePage = this.page.numero;
		//il faut d'abord que la page ne soit pas ouverte
		if (pageActive!=cettePage) { //TODO vérifier si cette condition est utile
			Event event = page.event;
			Heros heros = event.map.heros;
			int xmin1 = heros.x;
			int xmax1 = heros.x+heros.largeurHitbox;
			int ymin1 = heros.y;
			int ymax1 = heros.y+heros.hauteurHitbox;
			int xmin2 = event.x;
			int xmax2 = event.x+event.largeurHitbox;
			int ymin2 = event.y;
			int ymax2 = event.y+event.hauteurHitbox;
			int dir = heros.direction;
			//il faut être collé à l'évènement et regarder vers lui
			switch(dir) {
				case Event.Direction.HAUT:
					if ( Math.abs(ymin1-ymax2)>distanceMaxParole ) {
						return false;
					} else {
						if ( heros.largeurHitbox < event.largeurHitbox ) { 
							//la longueur de contact est supérieure à la moitié de la taille du héros
							boolean grandeSurfaceDeContact =
								((xmin2<xmax1 && xmax1<=xmax2) && xmax1-xmin2>heros.largeurHitbox/2) 
								|| ((xmin2<=xmin1 && xmin1<xmax2) && xmax2-xmin1>heros.largeurHitbox/2);
							return grandeSurfaceDeContact;
						} else {
							//le héros englobe l'event
							boolean surfaceDeContactMaximale =
							 (xmin1<=xmin2 && xmax2<=xmax1);
							return surfaceDeContactMaximale;
						}
					}
				case Event.Direction.GAUCHE:
					if ( Math.abs(xmin1-xmax2)>distanceMaxParole ) {
						return false;
					} else {
						if ( heros.hauteurHitbox < event.hauteurHitbox ) { 
							//la longueur de contact est supérieure à la moitié de la taille du héros
							boolean grandeSurfaceDeContact =
								((ymin2<ymax1 && ymax1<=ymax2) && ymax1-ymin2>heros.hauteurHitbox/2) 
								|| ((ymin2<=ymin1 && ymin1<ymax2) && ymax2-ymin1>heros.hauteurHitbox/2);
							return grandeSurfaceDeContact;
						} else {
							//le héros englobe l'event
							boolean surfaceDeContactMaximale =
								(ymin1<=ymin2 && ymax2<=ymax1);
							return surfaceDeContactMaximale;
						}
					}
				case Event.Direction.DROITE:
					if ( Math.abs(xmax1-xmin2)>distanceMaxParole ) {
						return false;
					} else {
						if ( heros.hauteurHitbox < event.hauteurHitbox ) { 
							//la longueur de contact est supérieure à la moitié de la taille du héros
							boolean grandeSurfaceDeContact =
								((ymin2<ymax1 && ymax1<=ymax2) && ymax1-ymin2>heros.hauteurHitbox/2) 
								|| ((ymin2<=ymin1 && ymin1<ymax2) && ymax2-ymin1>heros.hauteurHitbox/2);
							return grandeSurfaceDeContact;
						} else {
							//le héros englobe l'event
							boolean surfaceDeContactMaximale =
								(ymin1<=ymin2 && ymax2<=ymax1);
							return surfaceDeContactMaximale;
						}
					}
				default: //HAUT
					if ( Math.abs(ymax1-ymin2)>distanceMaxParole ) {
						return false;
					} else {
						if ( heros.largeurHitbox < event.largeurHitbox ) {
							//la longueur de contact est supérieure à la moitié de la taille du héros
							boolean grandeSurfaceDeContact =
								((xmin2<xmax1 && xmax1<=xmax2) && xmax1-xmin2>heros.largeurHitbox/2) 
								|| ((xmin2<=xmin1 && xmin1<xmax2) && xmax2-xmin1>heros.largeurHitbox/2);
							return grandeSurfaceDeContact;
						} else {
							//le héros englobe l'event
							boolean surfaceDeContactMaximale =
								(xmin1<=xmin2 && xmax2<=xmax1);
							return surfaceDeContactMaximale;
						}
					}
			}
		}
		System.out.println("ConditionParler.estVerifiee() n'a pas trouvé de cas correspondant.");
		return false;
	}
	
	public final boolean estLieeAuHeros() {
		return true;
	}

}
