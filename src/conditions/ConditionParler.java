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
				int ymax1 = heros.y+heros.largeurHitbox;
				int xmin2 = event.x;
				int xmax2 = event.x+event.largeurHitbox;
				int ymin2 = event.y;
				int ymax2 = event.y+event.largeurHitbox;
				int deltaX = event.x-heros.x;
				int deltaY = event.y-heros.y;
				int distance = deltaX*deltaX + deltaY*deltaY;
				int rayon = event.largeurHitbox+heros.largeurHitbox;
				int dir = heros.direction;
				//il faut être collé à l'évènement et regarder vers lui
				if( ( (ymin1==ymax2&&dir==3) || (xmin1==xmax2&&dir==1) || (xmax1==xmin2&&dir==2) || (ymax1==ymin2&&dir==0) )
					&& distance<=rayon*rayon/2
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
