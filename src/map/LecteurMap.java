package map;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.imageio.ImageIO;

import bibliothequeMenu.MenuPause;
import comportementEvent.Message;
import main.Fenetre;
import main.GestionClavier;
import main.Lecteur;
import main.Partie;
import menu.LecteurMenu;
import menu.Menu;

/**
 * Le Lecteur de map affiche la Map et les Events.
 * Il reçoit les ordres du clavier pour les transcrire en actions.
 */
public class LecteurMap extends Lecteur {
	public Map map;
	public Tileset tilesetActuel = null;
	private static BufferedImage hudTouches;
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
	
	/** n'est vrai que durant l'action de l'appui, se remet à false tout de suite */
	public Boolean toucheActionPressee = false;
	
	public LecteurMap(Fenetre fenetre){
		this.fenetre = fenetre;
		this.comparateur = new Comparator<Event>(){
	        public int compare(Event e1, Event e2){
	            return e1.compareTo(e2);
	        }
	    };
	    //images pour le HUD
	    if(hudTouches==null){
	    	try {
				hudTouches = ImageIO.read(new File(".\\ressources\\Graphics\\Pictures\\touches.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
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
		
		//DEBUG pour voir la hitbox de l'attaque du héros
		//ecran = dessinerLaHitboxDuHeros(ecran,xCamera,yCamera);
		
		//on dessine les évènements
		ecran = dessinerLesEvents(ecran,xCamera,yCamera);
		
		//ajouter imageCoucheSurHeros à l'écran
		ecran = superposerImages(ecran, map.imageCoucheSurHeros, -xCamera, -yCamera);
		
		//ajouter les jauges
		ecran = dessinerLesJauges(ecran);
		
		//on affiche le message
		if(messageActuel!=null){
			ecran = superposerImages(ecran, messageActuel.image, 76, 320);
		}
		
		//supprimer events dont la variable "supprimé" est à true
		supprimerLesEventsASupprimer();
		
		return ecran;
	}

	private BufferedImage dessinerLesJauges(BufferedImage ecran) {
		//touches
		ecran = superposerImages(ecran, hudTouches, 496, 0);
		//icone de l'arme equipée
		try{
			ecran = superposerImages(ecran, Partie.getArmeEquipee().icone, 511, 56);
		}catch(NullPointerException e){
			//pas d'arme équipée
		}
		return ecran;
	}

	@SuppressWarnings("unused")
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
			if(!event.equals(this.map.heros)){ //le héros est calculé en dernier
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
		//le héros est calculé en dernier pour éviter les problèmes d'épée
		Event event = this.map.heros;
		if(!event.supprime){
			if(event.pageActive == null || event.pageActive.commandes==null){
				event.activerUnePage();
			}
			if(event.pageActive != null){
				event.pageActive.executer();
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
			int largeur = eventImage.getWidth() / 4;
			int hauteur = eventImage.getHeight() / 4;
			int animation = event.animation;
			int direction = event.direction;
			BufferedImage apparence = eventImage.getSubimage(animation*largeur, direction*hauteur, largeur, hauteur);
			int positionX = event.x + event.largeurHitbox/2 - largeur/2;
			int positionY = event.y + event.hauteurHitbox - hauteur + event.offsetY;
			return superposerImages(ecran, apparence, positionX-xCamera, positionY-yCamera);
			/*
			//DEBUG pour visualiser les collisions
			Graphics2D graphics = ecran.createGraphics();
			graphics.setPaint(Color.blue);
			graphics.fillRect(event.x-xCamera, event.y-yCamera, event.largeurHitbox, event.hauteurHitbox);
			return ecran;
			*/
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
			case GestionClavier.ToucheRole.MENU : this.ouvrirLeMenu(); break;
			case GestionClavier.ToucheRole.HAUT : this.haut(); break;
			case GestionClavier.ToucheRole.GAUCHE : this.gauche(); break;
			case GestionClavier.ToucheRole.BAS : this.bas(); break;
			case GestionClavier.ToucheRole.DROITE : this.droite(); break;
			case GestionClavier.ToucheRole.ARME_SUIVANTE : this.equiperArmeSuivante(); break;
			case GestionClavier.ToucheRole.ACTION : this.action(); break;
			case GestionClavier.ToucheRole.ARME_PRECEDENTE : this.equiperArmePrecedente(); break;
			case GestionClavier.ToucheRole.ACTION_SECONDAIRE : this.objet(); break;
			default : break;
		}
	}
	
	/**
	 * Ouvre une autre map (dans un nouveau LecteurMap).
	 */
	public void changerMap(Map nouvelleMap){
		this.map = nouvelleMap;
		nouvelleMap.lecteur = this;
		this.fenetre.futurLecteur = this;
		this.allume = false;
		if(tilesetActuel!=null && !tilesetActuel.nom.equals(nouvelleMap.tileset.nom)) this.tilesetActuel = null;
	}
	
	/**
	 * Ouvrir le menu.
	 */
	public void ouvrirLeMenu() {
		LecteurMenu lecteurMenu = new LecteurMenu(this.fenetre, this);
		Menu menu = new MenuPause(lecteurMenu);
		this.fenetre.futurLecteur = lecteurMenu;
		lecteurMenu.menu = menu;
		this.allume = false;
	}

	@Override
	public void keyReleased(Integer keycode) {
		remettreAZeroLAnimationDuHeros(); //s'il s'est arrêté
		mettreHerosDansLaBonneDirection();
	}
	
	public void remettreAZeroLAnimationDuHeros(){
		ArrayList<Integer> touchesPressees = fenetre.touchesPressees;
		Event heros = map.heros;
		if(!touchesPressees.contains(GestionClavier.ToucheRole.BAS) 
		&& !touchesPressees.contains(GestionClavier.ToucheRole.HAUT) 
		&& !touchesPressees.contains(GestionClavier.ToucheRole.GAUCHE) 
		&& !touchesPressees.contains(GestionClavier.ToucheRole.DROITE)){
			heros.avance = false;
			heros.animation = 0;
		}
	}
	
	public void mettreHerosDansLaBonneDirection(){
		Heros heros = map.heros;
		if(!stopEvent && heros.animationAttaque<=0){
			ArrayList<Integer> touchesPressees = fenetre.touchesPressees;
			if( touchesPressees.contains(GestionClavier.ToucheRole.GAUCHE) ){
				heros.direction = Event.Direction.GAUCHE;
			}else if( touchesPressees.contains(GestionClavier.ToucheRole.DROITE) ){
				heros.direction = Event.Direction.DROITE;
			}else if( touchesPressees.contains(GestionClavier.ToucheRole.BAS) ){
				heros.direction = Event.Direction.BAS;
			}else if( touchesPressees.contains(GestionClavier.ToucheRole.HAUT) ){
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
	
	/**
	 * marcher vers le haut
	 */
	public void haut() {
		this.mettreHerosDansLaBonneDirection();
		this.map.heros.avance = true;
	}

	/**
	 * marcher vers la gauche
	 */
	public void gauche() {
		this.mettreHerosDansLaBonneDirection();
		this.map.heros.avance = true;
	}

	/**
	 * marcher vers le bas
	 */
	public void bas() {
		this.mettreHerosDansLaBonneDirection();
		this.map.heros.avance = true;
	}

	/**
	 * marcher vers la droite
	 */
	public void droite() {
		this.mettreHerosDansLaBonneDirection();
		this.map.heros.avance = true;
	}
	
	/**
	 * attaquer ou parler
	 */
	public void action() {
		this.toucheActionPressee = true;
	}

	/**
	 * changer d'arme
	 */
	public void equiperArmeSuivante() {
		Partie.equiperArmeSuivante();
	}
	
	/**
	 * changer d'arme
	 */
	public void equiperArmePrecedente() {
		Partie.equiperArmePrecedente();
	}

	/**
	 * utiliser objet secondaire
	 */
	public void objet() {
		//TODO
	}
	
}
