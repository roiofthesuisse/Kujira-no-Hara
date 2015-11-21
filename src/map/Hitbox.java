package map;

import java.awt.Color;
import java.awt.image.BufferedImage;

import main.Partie;

/**
 * Une hitbox peut être assignée à une arme afin de calculer sa portée et son étendue.
 */
public class Hitbox {
	int portee;
	int etendue;
	
	/**
	 * @param portee : profondeur de la zone d'attaque
	 * @param etendue : largeur de la zone d'attaque
	 */
	public Hitbox(int portee, int etendue){
		this.portee = portee;
		this.etendue = etendue;
	}
	
	public Boolean estDansZoneDAttaque(Event e, Heros h){
		Boolean estCeQueLeHerosAUneArme = (Partie.idArmesPossedees.size() > 0);
		if(estCeQueLeHerosAUneArme){
			int xminHitbox = h.x;
			int xmaxHitbox = h.x;
			int yminHitbox = h.y;
			int ymaxHitbox = h.y;
			//on calcule les bords de la zone d'attaque en fonction de l'orientation du héros
			switch(h.direction){
				case 0 : {
					xminHitbox -= etendue/2;
					xmaxHitbox += etendue/2;
					ymaxHitbox += portee;
					break;
				}
				case 1 : {
					yminHitbox -= etendue/2;
					ymaxHitbox += etendue/2;
					xminHitbox -= portee/2;
					break;
				}
				case 2 : {
					yminHitbox -= etendue/2;
					ymaxHitbox += etendue/2;
					xmaxHitbox += portee/2;
					break;
				}
				default : {
					xminHitbox -= etendue/2;
					xmaxHitbox += etendue/2;
					yminHitbox -= portee;
					break;
				}
			}
			int xminEvent = e.x - e.largeurHitbox/2;
			int xmaxEvent = e.x + e.largeurHitbox/2;
			int yminEvent = e.y - e.largeurHitbox/2;
			int ymaxEvent = e.y + e.largeurHitbox/2;
			//calcul du croisement entre la bodybox de l'event et la hitbox de l'arme
			//printCroisement(xminHitbox, xmaxHitbox, yminHitbox, ymaxHitbox, xminEvent, xmaxEvent, yminEvent, ymaxEvent);
			return lesDeuxRectanglesSeChevauchent(xminHitbox, xmaxHitbox, yminHitbox, ymaxHitbox, xminEvent, xmaxEvent, yminEvent, ymaxEvent, 1, 2); //1 et 2 étant différents, tous les types de croisements seront testés
		}
		return false;
	}
	
	public void printCroisement(int x1min, int x1max, int y1min, int y1max, int x2min, int x2max, int y2min, int y2max){
		BufferedImage img = new BufferedImage(640,480,LecteurMap.imageType);
		for(int i=00; i<640; i++){
			for(int j=0; j<480; j++){
				img.setRGB(i, j, Color.white.getRGB());
			}
		}
		for(int i=x1min; i<x1max; i++){
			for(int j=y1min; j<y1max; j++){
				img.setRGB(i, j, Color.red.getRGB());
			}
		}
		for(int i=x2min; i<x2max; i++){
			for(int j=y2min; j<y2max; j++){
				img.setRGB(i, j, Color.blue.getRGB());
			}
		}
		LecteurMap.sauvegarderImage(img);
	}
	
	public static Boolean lesDeuxRectanglesSeChevauchent(int x1min, int x1max, int y1min, int y1max, int x2min, int x2max, int y2min, int y2max, int tailleHitbox, int tailleHitboxAutre){
		//premier cas : deux coins se chevauchent
		Boolean deuxCoinsSeChevauchent = ((x1min<=x2min && x2min<x1max && x1max<=x2max)
										 	||(x2min<=x1min && x1min<x2max && x2max<=x1max))
									  && ((y1min<=y2min && y2min<y1max && y1max<=y2max)
											||(y2min<=y1min && y1min<y2max && y2max<=y1max));
		if(deuxCoinsSeChevauchent){
			return true; 
		}
		if(tailleHitbox!=tailleHitboxAutre){
			//deuxième cas : deux cotés de chevauchent
			Boolean deuxCotesSeChevauchent = (((x1min<=x2min && x2max<=x1max)&&((y2min<=y1min && y1min<y2max && y2max<=y1max)||(y1min<=y2min && y2min<y1max && y1max<=y2max)))
											|| ((y1min<=y2min && y2max<=y1max)&&((x2min<=x1min && x1min<x2max && x2max<=x1max)||(x1min<=x2min && x2min<x1max && x1max<=x2max))))
										  || (((x2min<=x1min && x1max<=x2max)&&((y1min<=y2min && y2min<y1max && y1max<=y2max)||(y2min<=y1min && y1min<y2max && y2max<=y1max)))
											|| ((y2min<=y1min && y1max<=y2max)&&((x1min<=x2min && x2min<x1max && x1max<=x2max)||(x2min<=x1min && x1min<x2max && x2max<=x1max))));
											//autre méthode pour résultat identique :
										   /*(((x2min<x1min && x1min<x2max)||(x2min<x1max && x1max<x2max)) 
											&& ((y2min<y1min && y1min<y2max)||(y2min<y1max && y1max<y2max)))
										   ||(((x1min<x2min && x2min<x1max)||(x1min<x2max && x2max<x1max)) 
											&& ((y1min<y2min && y2min<y1max)||(y1min<y2max && y2max<y1max)));*/
			
			if(deuxCotesSeChevauchent){
				return true;
			}
			//troisième cas : une hitbox incluse dans l'autre (pfff faut vraiment le faire exprès lol)
			Boolean unInclusDansLAutre = ((x1min<=x2min && x2max<=x1max)&&(y1min<=y2min && y2max<=y1max))
									   ||((x2min<=x1min && x1max<=x2max)&&(y2min<=y1min && y1max<=y2max));
			return unInclusDansLAutre;
			//TODO eventuellement un quatrième cas "en croix", mais très rare
		}else{
			//si les deux events ont la même taille ils ne peuvent que se croiser par coin
			return false;
		}
	}
	
}
