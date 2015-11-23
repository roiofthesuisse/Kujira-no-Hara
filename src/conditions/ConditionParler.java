package conditions;

import map.Event;
import map.Heros;

public class ConditionParler extends Condition {
	public static int frameDeLaDerniereFermetureDUnePageQuiACetteCondition = 0;
	
	public ConditionParler(){}
	
	@Override
	public Boolean estVerifiee() {
		//il faut que la dernière page s'ouvrant par parole se soit fermée il y a longtemps
		if(this.page.event.map.lecteur.frameActuelle - frameDeLaDerniereFermetureDUnePageQuiACetteCondition < 5){
			return false;
		}
		try{
			int pageActive = this.page.event.pageActive.numero;
			int cettePage = this.page.numero;
			//il faut d'abord que la page ne soit pas ouverte
			if(pageActive!=cettePage){ //mais cette condition est surement inutile...
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
				int deltaX = (event.x+event.largeurHitbox/2)-(heros.x+heros.largeurHitbox/2);
				int deltaY = (event.y+event.hauteurHitbox/2)-(heros.y+heros.hauteurHitbox/2);
				double distance = deltaX*deltaX + deltaY*deltaY;
				double rayonAuCarre = ((double)(event.largeurHitbox*event.largeurHitbox+event.hauteurHitbox*event.hauteurHitbox))/4.0
								 	+ ((double)(heros.largeurHitbox*heros.largeurHitbox+heros.hauteurHitbox*heros.hauteurHitbox))/4.0;
				int dir = heros.direction;
				//il faut être collé à l'évènement et regarder vers lui
				//TODO ne marche pas bien
				if( ( (ymin1>=ymax2 && dir==Event.Direction.HAUT) || 
					  (xmin1>=xmax2 && dir==Event.Direction.GAUCHE) || 
					  (xmax1<=xmin2 && dir==Event.Direction.DROITE) || 
					  (ymax1<=ymin2 && dir==Event.Direction.BAS) )
					&& distance<=rayonAuCarre
				){
					return true;
				}
			}
		}catch(NullPointerException e){
			//pas de page actuelle pour le lecteur
		}
		return false;
	}

}
