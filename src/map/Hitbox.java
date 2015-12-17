package map;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import main.Partie;

/**
 * Une hitbox peut être assignée à une arme afin de calculer sa portée et son étendue.
 */
public class Hitbox {
	public final int portee;
	public final int etendue;
	
	/**
	 * @param portee : profondeur de la zone d'attaque
	 * @param etendue : largeur de la zone d'attaque
	 */
	public Hitbox(int portee, int etendue){
		this.portee = portee;
		this.etendue = etendue;
	}
	
	public static Boolean estDansZoneDAttaque(Event e, Heros h){
		Boolean estCeQueLeHerosAUneArme = (Partie.idArmesPossedees.size() > 0) && Partie.getArmeEquipee()!=null;
		if(estCeQueLeHerosAUneArme){
			//on calcule les bords de la zone d'attaque en fonction de l'orientation du héros
			int[] coord = calculerCoordonneesAbsolues(h);
			int xminHitbox = coord[0];
			int xmaxHitbox = coord[1];
			int yminHitbox = coord[2];
			int ymaxHitbox = coord[3];
			
			int xminEvent = e.x;
			int xmaxEvent = e.x + e.largeurHitbox;
			int yminEvent = e.y;
			int ymaxEvent = e.y + e.hauteurHitbox;
			//calcul du croisement entre la bodybox de l'event et la hitbox de l'arme
			return lesDeuxRectanglesSeChevauchent(xminHitbox, xmaxHitbox, yminHitbox, ymaxHitbox, xminEvent, xmaxEvent, yminEvent, ymaxEvent, 1, 2, 3, 4); //1,2,3,4 étant différents, tous les types de croisements seront testés
		}
		return false;
	}
	
	public static int[] calculerCoordonneesAbsolues(Heros h) {
		int[] coordonneesAbsolues = new int[4];
		Hitbox b = Partie.getArmeEquipee().hitbox;
		int xminHitbox;
		int xmaxHitbox;
		int yminHitbox;
		int ymaxHitbox;
		switch(h.direction){
			case Event.Direction.BAS : {
				xminHitbox = (h.x+h.largeurHitbox/2) - b.etendue/2;
				xmaxHitbox = (h.x+h.largeurHitbox/2) + b.etendue/2;
				yminHitbox = h.y+h.hauteurHitbox;
				ymaxHitbox = h.y+h.hauteurHitbox + b.portee;
				break;
			}
			case Event.Direction.GAUCHE : {
				xminHitbox = h.x - b.portee;
				xmaxHitbox = h.x;
				yminHitbox = (h.y+h.hauteurHitbox/2) - b.etendue/2;
				ymaxHitbox = (h.y+h.hauteurHitbox/2) + b.etendue/2;
				break;
			}
			case Event.Direction.DROITE : {
				xminHitbox = h.x+h.largeurHitbox;
				xmaxHitbox = h.x+h.largeurHitbox + b.portee;
				yminHitbox = (h.y+h.hauteurHitbox/2) - b.etendue/2;
				ymaxHitbox = (h.y+h.hauteurHitbox/2) + b.etendue/2;
				break;
			}
			default: { //HAUT
				xminHitbox = (h.x+h.largeurHitbox/2) - b.etendue/2;
				xmaxHitbox = (h.x+h.largeurHitbox/2) + b.etendue/2;
				yminHitbox = h.y - b.portee;
				ymaxHitbox = h.y;
				break;
			}
		}
		coordonneesAbsolues[0] = xminHitbox;
		coordonneesAbsolues[1] = xmaxHitbox;
		coordonneesAbsolues[2] = yminHitbox;
		coordonneesAbsolues[3] = ymaxHitbox;
		return coordonneesAbsolues;
	}

	public static void printCroisement(int x1min, int x1max, int y1min, int y1max, int x2min, int x2max, int y2min, int y2max){
		//on part d'une image blanche
		BufferedImage img = new BufferedImage(640,480,LecteurMap.imageType);
		Graphics2D graphics = img.createGraphics();
		graphics.setPaint(Color.white);
		graphics.fillRect(0, 0, 640, 480);
		
		//on dessine le rouge
		graphics.setPaint(Color.red);
		graphics.fillRect(x1min, y1min, x1max-x1min, y1max-y1min);

		//on dessine le bleu
		graphics.setPaint(Color.blue);
		graphics.fillRect(x2min, y2min, x2max-x2min, y2max-y2min);
		
		//on enregistre
		LecteurMap.sauvegarderImage(img);
	}
	
	public static Boolean lesDeuxRectanglesSeChevauchent(int x1min, int x1max, int y1min, int y1max, int x2min, int x2max, int y2min, int y2max, int largHitbox, int hautHitbox, int largHitboxAutre, int hautHitboxAutre){
		//premier cas : deux coins se chevauchent
		Boolean deuxCoinsSeChevauchent = ((x1min<=x2min && x2min<x1max && x1max<=x2max)
										 	||(x2min<=x1min && x1min<x2max && x2max<=x1max))
									  && ((y1min<=y2min && y2min<y1max && y1max<=y2max)
											||(y2min<=y1min && y1min<y2max && y2max<=y1max));
		if(deuxCoinsSeChevauchent){
			return true; 
		}
		
		if(largHitbox==largHitboxAutre && hautHitbox==hautHitboxAutre){
			//si deux events ont la même taille, ils ne peuvent se chevaucher que par le coin
			//(pour être plus exact : le cas où deux events de même taille se chevauchent par l'arête est un cas particulier de la formule du chevauchement par coin)
			return false;
		}
		
		if(largHitbox!=largHitboxAutre){ //si deux events n'ont pas la même largeur, ils peuvent se chevaucher par arête horizontale
			//deuxième cas : deux cotés de chevauchent
			Boolean deuxCotesSeChevauchent = ((x1min<=x2min && x2max<=x1max)&&((y2min<=y1min && y1min<y2max && y2max<=y1max)||(y1min<=y2min && y2min<y1max && y1max<=y2max)))
										  || ((x2min<=x1min && x1max<=x2max)&&((y1min<=y2min && y2min<y1max && y1max<=y2max)||(y2min<=y1min && y1min<y2max && y2max<=y1max)));
											//autre méthode pour résultat identique :
										   /*(((x2min<x1min && x1min<x2max)||(x2min<x1max && x1max<x2max)) 
											&& ((y2min<y1min && y1min<y2max)||(y2min<y1max && y1max<y2max)))
										   ||(((x1min<x2min && x2min<x1max)||(x1min<x2max && x2max<x1max)) 
											&& ((y1min<y2min && y2min<y1max)||(y1min<y2max && y2max<y1max)));*/
			if(deuxCotesSeChevauchent){
				return true;
			}
		}
		if(hautHitbox!=hautHitboxAutre){ //si deux events n'ont pas la même hauteur, ils peuvent se chevaucher par arête verticale
			//deuxième cas : deux cotés de chevauchent
			Boolean deuxCotesSeChevauchent = ((y1min<=y2min && y2max<=y1max)&&((x2min<=x1min && x1min<x2max && x2max<=x1max)||(x1min<=x2min && x2min<x1max && x1max<=x2max)))
										  || ((y2min<=y1min && y1max<=y2max)&&((x1min<=x2min && x2min<x1max && x1max<=x2max)||(x2min<=x1min && x1min<x2max && x2max<=x1max)));
			if(deuxCotesSeChevauchent){
				return true;
			}
		}
		
		//troisième cas : une hitbox incluse dans l'autre (pfff faut vraiment le faire exprès lol)
		Boolean unInclusDansLAutre = ((x1min<=x2min && x2max<=x1max)&&(y1min<=y2min && y2max<=y1max))
								   ||((x2min<=x1min && x1max<=x2max)&&(y2min<=y1min && y1max<=y2max));
		return unInclusDansLAutre;
	}
	
}
