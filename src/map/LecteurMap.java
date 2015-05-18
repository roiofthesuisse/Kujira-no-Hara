package map;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import comportementEvent.Message;
import main.Fenetre;
import main.GestionClavier;
import main.Lecteur;

public class LecteurMap extends Lecteur{
	public Map map;
	public Tileset tilesetActuel = null;
	public Comparator<Event> comparateur; //permet de trier les events selon leur coordonnée y pour l'affichage
	public Boolean stopEvent = false; //si true, les évènements n'avacent plus naturellement (seuls mouvements forcés autorisés)
	
	//éventuel message à afficher à l'écran
	public Message messageActuel = null;
	
	public LecteurMap(Fenetre fenetre){
		this.fenetre = fenetre;
		this.comparateur = new Comparator<Event>(){
	        public int compare(Event e1, Event e2){
	            return e1.compareTo(e2);
	        }
	    };
	}
	
	public BufferedImage calculerAffichage(){
		BufferedImage ecran = ecranNoir();
		
		//ouverture du tileset
		try {
			if(tilesetActuel == null){
				tilesetActuel = new Tileset(map.tileset.nom);
			}
		}catch(Exception e){
			System.out.println("Erreur lors de l'ouverture du tileset :");
			e.printStackTrace();
		}
		
		//on dessine la map
		int xCamera = calculerXCamera();
		int yCamera = calculerYCamera();
		ecran = superposerImages(ecran, map.imageCoucheSousHeros, -xCamera, -yCamera);
		
		//lecture des commandes event
		for (Event event : map.events){
			if(!event.supprime){
				if(event.pageActive == null || event.pageActive.commandes==null){
					event.activerUnePage();
				}
				if(event.pageActive != null){
					event.pageActive.executer();
				}
			}
		}
		
		//déplacements des évènements
		if(!stopEvent){
			try{
				ArrayList<Integer> touchesPressees = this.fenetre.touchesPressees;
				if( touchesPressees.contains(90) || touchesPressees.contains(81) || touchesPressees.contains(83) || touchesPressees.contains(68) ){
					map.heros.avance = true;
				}
				for(Event event : map.events){
					if(!event.supprime){
						event.deplacer(); //on effectue le déplacement si possible (en fonction des obstacles rencontrés)
					}
				}
			}catch(Exception e){
				System.out.println("Erreur lors du déplacement des évènements :");
				e.printStackTrace();
			}
		}
		
		//animation des évènements
		if(!stopEvent){
			try{
				for(Event event : map.events){
					Boolean passerALAnimationSuivante = (map.lecteur.frameActuelle%event.frequenceActuelle==0);
					if(!event.avance && event.animeALArretActuel && passerALAnimationSuivante) event.animation = (event.animation+1)%4;
					if(event.avance && event.animeEnMouvementActuel && passerALAnimationSuivante) event.animation = (event.animation+1)%4;
				}
			}catch(Exception e){
				System.out.println("erreur lors de l'animation des évènements dans la boucle d'affichage de la map :");
				e.printStackTrace();
			}
		}
		
		//on dessine les évènements
		try{
			Collections.sort(map.events, comparateur); //on trie les events du plus derrière au plus devant
			for(Event event : map.events){
				if(!event.supprime){
				ecran = dessinerEvent(ecran,event,xCamera,yCamera);
				}
			}
		}catch(Exception e){
			System.out.println("Erreur lors du dessin des évènements :");
			e.printStackTrace();
		}
		
		//ajouter imageCoucheSurHeros à l'écran
		ecran = superposerImages(ecran, map.imageCoucheSurHeros, -xCamera, -yCamera);
		
		//on affiche le message
		if(messageActuel!=null){
			ecran = superposerImages(ecran, messageActuel.image, 76, 320);
		}
		
		//supprimer events dont le variable "supprimé" est à true
		//TODO
		
		return ecran;
	}

	public void photographierCollision(){
		BufferedImage img = new BufferedImage(640,480,imageType);
		for(int i=00; i<640; i++){
			for(int j=0; j<480; j++){
				img.setRGB(i, j, Color.white.getRGB());
			}
		}
		img.setRGB(map.heros.x, map.heros.y, Color.red.getRGB());
		img.setRGB(map.events.get(1).x, map.events.get(1).y, Color.blue.getRGB());
		sauvegarderImage(img);
	}

	private BufferedImage dessinerEvent(BufferedImage ecran, Event event, int xCamera, int yCamera) {
		//on calcule l'apparence de l'event
		//l'apparence de l'event est une des 16 parties de l'image de l'event
		BufferedImage eventImage = event.imageActuelle;
		if(eventImage==null) System.out.println("image nulle pour l'event "+event.nom+" "+event.numero);
		int largeur = eventImage.getWidth() / 4;
		int hauteur = eventImage.getHeight() / 4;
		int animation = event.animation;
		int direction = event.direction;
		BufferedImage apparence = new BufferedImage(largeur,hauteur,imageType);
		for(int i=0; i<largeur; i++){
			for(int j=0; j<hauteur; j++){
				apparence.setRGB(i, j, eventImage.getRGB(animation*largeur+i, direction*hauteur+j));
			}
		}
		int positionX = event.x + event.largeurHitbox/2 - largeur/2;
		int positionY = event.y + event.largeurHitbox - hauteur + event.offsetY;
		return superposerImages(ecran, apparence, positionX-xCamera, positionY-yCamera);
	}
	
	/**
	 * dessine à l'écran aux coordonnées (xEcran;yEcran) un certain carreau du tileset
	 * @param ecran
	 * @param xEcran
	 * @param yEcran
	 * @param carreau
	 * @return 
	 */
	public BufferedImage dessinerCarreau(BufferedImage ecran, int xEcran, int yEcran, int carreau, Tileset tilesetUtilise) throws Exception{
		int xTileset = carreau%8;
		int yTileset = carreau/8;
		BufferedImage dessinCarreau = new BufferedImage(32,32,imageType);
		for(int i=0; i<32; i++){
			for(int j=0; j<32; j++){
				dessinCarreau.setRGB(i, j, tilesetUtilise.image.getRGB(xTileset*32+i,yTileset*32+j));
			}
		}
		return superposerImages(ecran, dessinCarreau, xEcran*32, yEcran*32);
	}

	@Override
	public void keyPressed(int keycode) {
		switch(keycode){
			case 10 : map.menu(); break; //entrée
			case 90 : map.haut(); break; //z
			case 81 : map.gauche(); break; //q
			case 83 : map.bas(); break; //s
			case 68 : map.droite(); break; //d
			case 79 : map.armeSuivante(); break; //o
			case 75 : map.action(); break; //k
			case 76 : map.armePrecedente(); break; //l
			case 77 : map.objet(); break; //km
			/*case 38 : map.haut(); break;
			case 37 : map.gauche(); break;
			case 40 : map.bas(); break;
			case 39 : map.droite(); break;*/
			default : break;
		}
	}
	
	public void changerMap(Map nouvelleMap){
		this.map = nouvelleMap;
		nouvelleMap.lecteur = this;
		Fenetre.futurLecteur = this;
		this.allume = false;
		this.tilesetActuel = null;
	}

	@Override
	public void keyReleased(Integer keycode) {
		remettreAZeroLAnimationDuHeros(); //s'il s'est arrêté
		mettreHerosDansLaBonneDirection();
	}
	
	public void remettreAZeroLAnimationDuHeros(){
		int toucheBas = GestionClavier.codeToucheBas();
		int toucheHaut = GestionClavier.codeToucheHaut();
		int toucheGauche = GestionClavier.codeToucheGauche();
		int toucheDroite = GestionClavier.codeToucheDroite();
		ArrayList<Integer> touchesPressees = fenetre.touchesPressees;
		Event heros = map.heros;
		if( !touchesPressees.contains(toucheBas) && !touchesPressees.contains(toucheHaut) 
		&& !touchesPressees.contains(toucheGauche) && !touchesPressees.contains(toucheDroite) ){
			heros.avance = false;
			heros.animation = 0;
		}
	}
	
	public void mettreHerosDansLaBonneDirection(){
		Heros heros = map.heros;
		if(!stopEvent && heros.animationAttaque<=0){
			int toucheBas = GestionClavier.codeToucheBas();
			int toucheHaut = GestionClavier.codeToucheHaut();
			int toucheGauche = GestionClavier.codeToucheGauche();
			int toucheDroite = GestionClavier.codeToucheDroite();
			ArrayList<Integer> touchesPressees = fenetre.touchesPressees;
			if( touchesPressees.contains(toucheGauche) ){
				heros.direction = 1;
			}else if( touchesPressees.contains(toucheDroite) ){
				heros.direction = 2;
			}else if( touchesPressees.contains(toucheBas) ){
				heros.direction = 0;
			}else if( touchesPressees.contains(toucheHaut) ){
				heros.direction = 3;
			}
		}
	}
	
	private int calculerXCamera(){
		int largeurMap = map.layer0.get(0).length;
		if(largeurMap<20){
			//map très petite, défilement inutile
			return 0;
		}else{
			//grande map, défilement possible
			int xCamera = map.heros.x - Fenetre.largeurParDefaut/2;
			if(xCamera<0){ //caméra ne déborde pas de la map à gauche
				return 0;
			}else if(xCamera+Fenetre.largeurParDefaut>largeurMap*32){ //caméra ne déborde pas de la map à droite
				return largeurMap*32 - Fenetre.largeurParDefaut;
			}else{
				return xCamera;
			}
		}
	}
	
	private int calculerYCamera(){
		int hauteurMap = map.layer0.size();
		if(hauteurMap<15){ 
			//map très petite, défilement inutile
			return 0;
		}else{
			//grande map, défilement possible
			int yCamera = map.heros.y - Fenetre.hauteurParDefaut/2;
			if(yCamera<0){ //caméra ne déborde pas de la map en haut
				return 0;
			}else if(yCamera+Fenetre.hauteurParDefaut>hauteurMap*32){ //caméra ne déborde pas de la map en bas
				return hauteurMap*32 - Fenetre.hauteurParDefaut;
			}else{
				return yCamera;
			}
		}
	}
	
	public void normaliserApparenceDuHerosAvantMessage(){
		this.map.heros.animation = 0;
		this.map.heros.animationAttaque = 0;
	}
	
}
