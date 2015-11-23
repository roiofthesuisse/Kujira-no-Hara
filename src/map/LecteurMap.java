package map;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import comportementEvent.Message;
import main.Arme;
import main.Fenetre;
import main.GestionClavier;
import main.Lecteur;
import main.Partie;

/**
 * Le lecteur de map affiche la map et les évènements.
 */
public class LecteurMap extends Lecteur{
	public Map map;
	public Tileset tilesetActuel = null;
	/**
	 * Permet de trier les events selon leur coordonnée y pour l'affichage.
	 */
	public Comparator<Event> comparateur;
	/**
	 * Si true, les évènements n'avancent plus naturellement (seuls mouvements forcés autorisés).
	 */
	public Boolean stopEvent = false; 
	
	/**
	 * Message à afficher dans la boîte de dialogue.
	 */
	public Message messageActuel = null;
	
	public LecteurMap(Fenetre fenetre){
		this.fenetre = fenetre;
		this.comparateur = new Comparator<Event>(){
	        public int compare(Event e1, Event e2){
	            return e1.compareTo(e2);
	        }
	    };
	}
	
	/**
	 * A chaque frame, calcule l'écran à afficher, avec le décor et les events dessus.
	 * Ne pas oublier de récupérer le résultat de cette méthode.
	 */
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
		continuerLaLectureDesPagesDeCommandesEvent();
		
		//déplacements des évènements
		if(!this.stopEvent){
			déplacerLesEvents();
		}
		
		//animation des évènements
		if(!this.stopEvent){
			animerLesEvents();
		}
		
		//TODO test à retirer
		ecran = dessinerLaHitboxDuHeros(ecran,xCamera,yCamera);
		
		//on dessine les évènements
		ecran = dessinerLesEvents(ecran,xCamera,yCamera);
		
		//ajouter imageCoucheSurHeros à l'écran
		ecran = superposerImages(ecran, map.imageCoucheSurHeros, -xCamera, -yCamera);
		
		//on affiche le message
		if(messageActuel!=null){
			ecran = superposerImages(ecran, messageActuel.image, 76, 320);
		}
		
		//supprimer events dont la variable "supprimé" est à true
		supprimerLesEventsASupprimer();
		
		return ecran;
	}

	private BufferedImage dessinerLaHitboxDuHeros(BufferedImage ecran, int xCamera, int yCamera) {
		try{
			int[] coord = Hitbox.calculerCoordonneesAbsolues(this.map.heros);
			int xminHitbox = coord[0];
			int xmaxHitbox = coord[1];
			int yminHitbox = coord[2];
			int ymaxHitbox = coord[3];
			Graphics2D graphics = ecran.createGraphics();
			graphics.setPaint(Color.magenta);
			graphics.drawLine(xminHitbox-xCamera, yminHitbox-yCamera, xmaxHitbox-xCamera, yminHitbox-yCamera);
			graphics.drawLine(xminHitbox-xCamera, ymaxHitbox-yCamera, xmaxHitbox-xCamera, ymaxHitbox-yCamera);
			graphics.drawLine(xminHitbox-xCamera, yminHitbox-yCamera, xminHitbox-xCamera, ymaxHitbox-yCamera);
			graphics.drawLine(xmaxHitbox-xCamera, yminHitbox-yCamera, xmaxHitbox-xCamera, ymaxHitbox-yCamera);
		}catch(Exception e){}
		return ecran;
	}

	/**
	 * Activer une page si l'event n'a aucune page activée.
	 * Sinon, continuer de lire la page de commandes active de l'event.
	 */
	private void continuerLaLectureDesPagesDeCommandesEvent() {
		for (Event event : this.map.events){
			if(!event.supprime){
				if(event.pageActive == null || event.pageActive.commandes==null){
					event.activerUnePage();
				}
				if(event.pageActive != null){
					event.pageActive.executer();
				}
			}
		}
	}

	/**
	 * Calculer le nouvel écran, avec les events dessinés dessus.
	 * Ne pas oublier de récupérer le résultat de cette méthode.
	 */
	private BufferedImage dessinerLesEvents(BufferedImage ecran, int xCamera, int yCamera) {
		try{
			Collections.sort(this.map.events, this.comparateur); //on trie les events du plus derrière au plus devant
			for(Event event : this.map.events){
				if(!event.supprime){
					ecran = dessinerEvent(ecran,event,xCamera,yCamera);
				}
			}
		}catch(Exception e){
			System.out.println("Erreur lors du dessin des évènements :");
			e.printStackTrace();
		}
		return ecran;
	}

	/**
	 * Donne la bonne valeur à l'attribut "animation" avant d'envoyer l'event à l'affichage.
	 */
	private void animerLesEvents() {
		try{
			for(Event event : this.map.events){
				Boolean passerALAnimationSuivante = (this.map.lecteur.frameActuelle%event.frequenceActuelle==0);
				if(!event.avance && event.animeALArretActuel && passerALAnimationSuivante) event.animation = (event.animation+1)%4;
				if(event.avance && event.animeEnMouvementActuel && passerALAnimationSuivante) event.animation = (event.animation+1)%4;
			}
		}catch(Exception e){
			System.out.println("erreur lors de l'animation des évènements dans la boucle d'affichage de la map :");
			e.printStackTrace();
		}
	}
	
	/**
	 * Donne la bonne valeur aux positions x et y avant d'envoyer l'event à l'affichage.
	 */
	private void déplacerLesEvents() {
		try{
			ArrayList<Integer> touchesPressees = this.fenetre.touchesPressees;
			if( touchesPressees.contains(90) || touchesPressees.contains(81) || touchesPressees.contains(83) || touchesPressees.contains(68) ){
				map.heros.avance = true;
			}
			for(Event event : this.map.events){
				if(!event.supprime){
					event.deplacer(); //on effectue le déplacement si possible (pas d'obstacles rencontrés)
				}
			}
		}catch(Exception e){
			System.out.println("Erreur lors du déplacement des évènements :");
			e.printStackTrace();
		}
	}

	private void supprimerLesEventsASupprimer() {
		int nombreDEvents = this.map.events.size();
		for(int i=0; i<nombreDEvents; i++){
			if(this.map.events.get(i).supprime){
				this.map.events.remove(i);
				nombreDEvents--;
				i--;
			}
		}
	}

	public void photographierCollision(){
		BufferedImage img = new BufferedImage(640,480,imageType);
		/*
		//vieille façon de faire :
		for(int i=00; i<640; i++){
			for(int j=0; j<480; j++){
				img.setRGB(i, j, Color.white.getRGB());
			}
		}
		*/
		//nouvelle façon de faire (plus rapide) :
		Graphics2D graphics = img.createGraphics();
		graphics.setPaint(Color.white);
		graphics.fillRect(0, 0, 640, 480);
		
		img.setRGB(map.heros.x, map.heros.y, Color.red.getRGB());
		img.setRGB(map.events.get(1).x, map.events.get(1).y, Color.blue.getRGB());
		sauvegarderImage(img);
	}

	/**
	 * Dessine l'event (tourné dans une certaine direction et dans une certaine étape d'animation).
	 * Ne pas oublier de récupérer le résultat de cette méthode.
	 */
	private BufferedImage dessinerEvent(BufferedImage ecran, Event event, int xCamera, int yCamera) {
		BufferedImage eventImage = event.imageActuelle;
		if(eventImage!=null){ 
			//l'apparence de l'event est une des 16 parties de l'image de l'event (suivant la direction et l'animation)
			/*
			int largeur = eventImage.getWidth() / 4;
			int hauteur = eventImage.getHeight() / 4;
			int animation = event.animation;
			int direction = event.direction;
			BufferedImage apparence = eventImage.getSubimage(animation*largeur, direction*hauteur, largeur, hauteur);
			int positionX = event.x + event.largeurHitbox/2 - largeur/2;
			int positionY = event.y + event.hauteurHitbox - hauteur + event.offsetY;
			return superposerImages(ecran, apparence, positionX-xCamera, positionY-yCamera);
			*/
			//TODO test (pour visualiser les collisions)
			Graphics2D graphics = ecran.createGraphics();
			graphics.setPaint(Color.blue);
			graphics.fillRect(event.x-xCamera, event.y-yCamera, event.largeurHitbox, event.hauteurHitbox);
			return ecran;
			
		}else{
			//l'event n'a pas d'image
			return ecran;
		}
	}
	
	/**
	 * Dessine à l'écran aux coordonnées (xEcran;yEcran) un certain carreau du tileset.
	 * Ne pas oublier de récupérer le résultat de cette méthode.
	 */
	public BufferedImage dessinerCarreau(BufferedImage ecran, int xEcran, int yEcran, int carreau, Tileset tilesetUtilise) throws Exception{
		int xTileset = carreau%8;
		int yTileset = carreau/8;
		BufferedImage dessinCarreau = tilesetUtilise.image.getSubimage(xTileset*32, yTileset*32, 32, 32);
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
			case 79 : map.equiperArmeSuivante(); break; //o
			case 75 : map.action(); break; //k
			case 76 : map.equiperArmePrecedente(); break; //l
			case 77 : map.objet(); break; //m
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
		if(tilesetActuel!=null && !tilesetActuel.nom.equals(nouvelleMap.tileset.nom)) this.tilesetActuel = null;
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
				heros.direction = Event.Direction.GAUCHE;
			}else if( touchesPressees.contains(toucheDroite) ){
				heros.direction = Event.Direction.DROITE;
			}else if( touchesPressees.contains(toucheBas) ){
				heros.direction = Event.Direction.BAS;
			}else if( touchesPressees.contains(toucheHaut) ){
				heros.direction = Event.Direction.HAUT;
			}
		}
	}
	
	private int calculerXCamera(){
		int largeurMap = map.largeur;
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
		int hauteurMap = map.hauteur;
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
