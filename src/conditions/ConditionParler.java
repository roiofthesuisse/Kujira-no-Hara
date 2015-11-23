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
			if(pageActive!=cettePage){ //TODO vérifier si cette condition est utile
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
				switch(dir){
				case Event.Direction.HAUT:
					if(ymin1 != ymax2){
						return false;
					}else{
						if( heros.largeurHitbox < event.largeurHitbox ){ 
							//la longueur de contact est supérieure à la moitié de la taille du héros
							return ((xmin2<xmax1 && xmax1<=xmax2) && xmax1-xmin2>heros.largeurHitbox/2) || 
								((xmin2<=xmin1 && xmin1<xmax2) && xmax2-xmin1>heros.largeurHitbox/2);
						}else{
							//le héros englobe l'event
							return xmin1<=xmin2 && xmax2<=xmax1;
						}
					}
				case Event.Direction.GAUCHE:
					if(xmin1 != xmax2){
						return false;
					}else{
						if( heros.hauteurHitbox < event.hauteurHitbox ){ 
							//la longueur de contact est supérieure à la moitié de la taille du héros
							return ((ymin2<ymax1 && ymax1<=ymax2) && ymax1-ymin2>heros.hauteurHitbox/2) || 
								((ymin2<=ymin1 && ymin1<ymax2) && ymax2-ymin1>heros.hauteurHitbox/2);
						}else{
							//le héros englobe l'event
							return ymin1<=ymin2 && ymax2<=ymax1;
						}
					}
				case Event.Direction.DROITE:
					if(xmax1 != xmin2){
						return false;
					}else{
						if( heros.hauteurHitbox < event.hauteurHitbox ){ 
							//la longueur de contact est supérieure à la moitié de la taille du héros
							return ((ymin2<ymax1 && ymax1<=ymax2) && ymax1-ymin2>heros.hauteurHitbox/2) || 
								((ymin2<=ymin1 && ymin1<ymax2) && ymax2-ymin1>heros.hauteurHitbox/2);
						}else{
							//le héros englobe l'event
							return ymin1<=ymin2 && ymax2<=ymax1;
						}
					}
				case Event.Direction.BAS:
					if(ymax1 != ymin2){
						return false;
					}else{
						if( heros.largeurHitbox < event.largeurHitbox ){ 
							//la longueur de contact est supérieure à la moitié de la taille du héros
							return ((xmin2<xmax1 && xmax1<=xmax2) && xmax1-xmin2>heros.largeurHitbox/2) || 
								((xmin2<=xmin1 && xmin1<xmax2) && xmax2-xmin1>heros.largeurHitbox/2);
						}else{
							//le héros englobe l'event
							return xmin1<=xmin2 && xmax2<=xmax1;
						}
					}
				}
			}
		}catch(NullPointerException e){
			//pas de page actuelle pour le lecteur
		}
		return false;
	}

}
