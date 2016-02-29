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
import commandes.Message;
import main.Fenetre;
import main.Lecteur;
import menu.LecteurMenu;
import menu.Menu;
import utilitaire.GestionClavier;
import utilitaire.GestionClavier.ToucheRole;

/**
 * Le Lecteur de map affiche la Map et les Events.
 * Il reçoit les ordres du clavier pour les transcrire en actions.
 */
public class LecteurMap extends Lecteur {
	//constantes
	private static final int X_AFFICHAGE_ARME = 563;
	private static final int Y_AFFICHAGE_ARME = 4;
	private static final int X_AFFICHAGE_OBJET = 612;
	private static final int Y_AFFICHAGE_OBJET = 4;
	private static final int X_AFFICHAGE_MESSAGE = 76;
	private static final int Y_AFFICHAGE_MESSAGE = 320;
	
	public Map map;
	public Tileset tilesetActuel = null;
	
	/** permet de trier les events selon leur coordonnée y pour l'affichage */
	public Comparator<Event> comparateur;
	
	/** si true, les évènements n'avancent plus naturellement (seuls mouvements forcés autorisés) */
	public boolean stopEvent = false; 
	
	/** message à afficher dans la boîte de dialogue */
	public Message messageActuel = null;
	
	/**  */
	public static final BufferedImage HUD_TOUCHES = chargerImageHudTouches();
	
	/** frame où le joueur a appuyé sur la touche action */
	public int frameDAppuiDeLaToucheAction;
	
	/**
	 * Constructeur explicite
	 * @param fenetre dont ce Lecteur assure l'affichage
	 */
	public LecteurMap(final Fenetre fenetre) {
		this.fenetre = fenetre;
		this.comparateur = new Comparator<Event>() {
	        public int compare(final Event e1, final Event e2) {
	            return e1.compareTo(e2);
	        }
	    };
	}
	
	/**
	 * A chaque frame, calcule l'écran à afficher, avec le décor et les Events dessus.
	 * @warning Ne pas oublier de récupérer le résultat de cette méthode.
	 * @return écran représentant la Map
	 */
	public final BufferedImage calculerAffichage() {
		BufferedImage ecran = ecranNoir();
		
		//ouverture du tileset
		try {
			if (tilesetActuel == null) {
				tilesetActuel = this.map!=null && this.map.tileset!=null ? this.map.tileset : new Tileset(map.tileset.nom);
			}
		} catch (Exception e) {
			System.out.println("Erreur lors de l'ouverture du tileset :");
			e.printStackTrace();
		}
		
		//on dessine la map
		final int xCamera = calculerXCamera();
		final int yCamera = calculerYCamera();
		ecran = superposerImages(ecran, map.imageCoucheSousHeros, -xCamera, -yCamera);

		//lecture des commandes event
		continuerLaLectureDesPagesDeCommandesEvent();

		//déplacements des évènements
		deplacerLesEvents();
		
		
		//animation des évènements
		animerLesEvents();

		//DEBUG pour voir la hitbox de l'attaque du héros
		//ecran = dessinerLaHitboxDuHeros(ecran, xCamera, yCamera);
		
		//on dessine les évènements
		ecran = dessinerLesEvents(ecran, xCamera, yCamera);
		
		//ajouter imageCoucheSurHeros à l'écran
		ecran = superposerImages(ecran, map.imageCoucheSurHeros, -xCamera, -yCamera);
		
		//ajouter les jauges
		ecran = dessinerLesJauges(ecran);
		
		//on affiche le message
		if (messageActuel!=null) {
			ecran = superposerImages(ecran, messageActuel.image, X_AFFICHAGE_MESSAGE, Y_AFFICHAGE_MESSAGE);
		}
		
		//supprimer events dont l'attribut "supprimé" est à true
		supprimerLesEventsASupprimer();
		
		return ecran;
	}

	/**
	 * Dessiner à l'écran les jauges et informations extradiégétiques à destination du joueur
	 * @param ecran sur lequel on dessine les jauges
	 * @return écran avec les jauges dessinées
	 */
	private BufferedImage dessinerLesJauges(BufferedImage ecran) {
		//touches
		ecran = superposerImages(ecran, HUD_TOUCHES, 0, 0);
		
		//icone de l'arme equipée
		try {
			ecran = superposerImages(ecran, Fenetre.getPartieActuelle().getArmeEquipee().icone, X_AFFICHAGE_ARME, Y_AFFICHAGE_ARME);
		} catch (NullPointerException e) {
			//pas d'arme équipée
		}
		
		//TODO icone de l'objet équipé
		/*
		try
			X_AFFICHAGE_OBJET
			Y_AFFICHAGE_OBJETn, 
		*/
		return ecran;
	}

	/**
	 * Pour le debug, on peut souhaiter afficher la HitBox du Héros à l'écran.
	 * @param ecran sur lequel on dessine la Htibox du Héros
	 * @param xCamera position x de la caméra
	 * @param yCamera position y de la caméra
	 * @return écran avec la Hitbox du Héros dessinée dessus
	 */
	@SuppressWarnings("unused")
	private BufferedImage dessinerLaHitboxDuHeros(BufferedImage ecran, final int xCamera, final int yCamera) {
		try {
			final int[] coord = Hitbox.calculerCoordonneesAbsolues(this.map.heros);
			final int xminHitbox = coord[0];
			final int xmaxHitbox = coord[1];
			final int yminHitbox = coord[2];
			final int ymaxHitbox = coord[3];
			final Graphics2D graphics = ecran.createGraphics();
			graphics.setPaint(Color.magenta);
			graphics.drawLine(xminHitbox-xCamera, yminHitbox-yCamera, xmaxHitbox-xCamera, yminHitbox-yCamera);
			graphics.drawLine(xminHitbox-xCamera, ymaxHitbox-yCamera, xmaxHitbox-xCamera, ymaxHitbox-yCamera);
			graphics.drawLine(xminHitbox-xCamera, yminHitbox-yCamera, xminHitbox-xCamera, ymaxHitbox-yCamera);
			graphics.drawLine(xmaxHitbox-xCamera, yminHitbox-yCamera, xmaxHitbox-xCamera, ymaxHitbox-yCamera);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ecran;
	}

	/**
	 * Activer une page si l'event n'a aucune page activée.
	 * Sinon, continuer de lire la page de commandes active de l'event.
	 */
	private void continuerLaLectureDesPagesDeCommandesEvent() {
		for (Event event : this.map.events) {
			if (!event.equals(this.map.heros)) { //le Héros est calculé en dernier
				if (!event.supprime) {
					if (event.pageActive == null || event.pageActive.commandes==null) {
						event.activerUnePage();
					}
					if (event.pageActive != null) {
						event.pageActive.executer();
					}
				}
			}
		}
		//le Héros est calculé en dernier pour éviter les problèmes d'épée
		final Event event = this.map.heros;
		if (!event.supprime) {
			if (event.pageActive == null || event.pageActive.commandes==null) {
				event.activerUnePage();
			}
			if (event.pageActive != null) {
				event.pageActive.executer();
			}
		}
	}

	/**
	 * Calculer le nouvel écran, avec les Events dessinés dessus.
	 * Ne pas oublier de récupérer le résultat de cette méthode.
	 * @param ecran sur lequel on dessine les Events
	 * @param xCamera position x de la caméra
	 * @param yCamera position y de la caméra
	 * @return écran avec les Events dessinés dessus
	 */
	private BufferedImage dessinerLesEvents(BufferedImage ecran, final int xCamera, final int yCamera) {
		try {
			Collections.sort(this.map.events, this.comparateur); //on trie les events du plus derrière au plus devant
			for (Event event : this.map.events) {
				if (!event.supprime) {
					ecran = dessinerEvent(ecran, event, xCamera, yCamera);
				}
			}
		} catch (Exception e) {
			System.out.println("Erreur lors du dessin des évènements :");
			e.printStackTrace();
		}
		return ecran;
	}

	/**
	 * Donne la bonne valeur à l'attribut "animation" avant d'envoyer l'event à l'affichage.
	 */
	private void animerLesEvents() {
		if (this.stopEvent) {
			return;
		}
		try {
			//TODO if( event.saute )
			for (Event event : this.map.events) {
				final boolean passerALAnimationSuivante = (this.map.lecteur.frameActuelle%event.frequenceActuelle==0);
				//cas où l'Event est animé à l'arrêt
				if (!event.avance && event.animeALArretActuel && passerALAnimationSuivante) {
					event.animation = (event.animation+1) % Event.NOMBRE_DE_VIGNETTES_PAR_IMAGE;
				}
				//cas où l'Event est vraiment en mouvement
				if ((event.avance||event.avancaitALaFramePrecedente) && event.animeEnMouvementActuel && passerALAnimationSuivante) {
					event.animation = (event.animation+1) % Event.NOMBRE_DE_VIGNETTES_PAR_IMAGE;
				}
				event.avancaitALaFramePrecedente = event.avance;
			}
		} catch (Exception e) {
			System.out.println("erreur lors de l'animation des évènements dans la boucle d'affichage de la map :");
			e.printStackTrace();
		}
	}
	
	/**
	 * Donne la bonne valeur aux positions x et y avant d'envoyer l'Event à l'affichage.
	 */
	private void deplacerLesEvents() {
		try {
			//animer la marche du Héros si touche pressée
			final ArrayList<Integer> touchesPressees = this.fenetre.touchesPressees;
			if ( touchesPressees.contains(ToucheRole.HAUT)
			|| touchesPressees.contains(ToucheRole.GAUCHE) 
			|| touchesPressees.contains(ToucheRole.BAS) 
			|| touchesPressees.contains(ToucheRole.DROITE) ) {
				map.heros.avance = true;
			}
			
			//déplacer chaque Event
			for (Event event : this.map.events) {
				if (!event.supprime) {
					event.deplacer(); //on effectue le déplacement si possible (pas d'obstacles rencontrés)
				}
			}
		} catch (Exception e) {
			System.err.println("Erreur lors du déplacement des évènements :");
			e.printStackTrace();
		}
	}

	/**
	 * Certains Events ont été marqués "à supprimer" durant la lecture des Commandes.
	 * On les élimine maintenant, une fois que la lecture des Commandes est terminée.
	 * En effet on ne peut pas supprimer des Events lorsqu'on est encore dans la boucle qui parcourt la liste des Events.
	 */
	private void supprimerLesEventsASupprimer() {
		int nombreDEvents = this.map.events.size();
		for (int i = 0; i<nombreDEvents; i++) {
			if (this.map.events.get(i).supprime) {
				this.map.events.remove(i);
				nombreDEvents--;
				i--;
			}
		}
	}

	/**
	 * Faire une capture d'écran des collisions
	 */
	public final void photographierCollision() {
		final BufferedImage img = new BufferedImage(Fenetre.LARGEUR_ECRAN, Fenetre.HAUTEUR_ECRAN, Lecteur.TYPE_DES_IMAGES);
		final Graphics2D graphics = img.createGraphics();
		graphics.setPaint(Color.white);
		graphics.fillRect(0, 0, Fenetre.LARGEUR_ECRAN, Fenetre.HAUTEUR_ECRAN);
		
		img.setRGB(map.heros.x, map.heros.y, Color.red.getRGB());
		img.setRGB(map.events.get(1).x, map.events.get(1).y, Color.blue.getRGB());
		sauvegarderImage(img);
	}

	/**
	 * Dessine l'Event sur l'écran.
	 * @warning Ne pas oublier de récupérer le résultat de cette méthode.
	 * @param ecran sur lequel on dessine
	 * @param event à dessiner
	 * @param xCamera position x de la caméra
	 * @param yCamera position y de la caméra
	 * @return écran sur lequel on a dessiné l'Event demandé
	 */
	private BufferedImage dessinerEvent(final BufferedImage ecran, final Event event, final int xCamera, final int yCamera) {
		final BufferedImage eventImage = event.imageActuelle;
		if (eventImage!=null) { 
			//l'apparence de l'event est une des 16 parties de l'image de l'event (suivant la direction et l'animation)
			final int largeur = eventImage.getWidth() / 4;
			final int hauteur = eventImage.getHeight() / 4;
			int direction;
			final int animation;
			int positionX;
			int positionY;
			if (event.saute) {
				//l'Event est en train de sauter
				direction = event.directionLorsDuSaut;
				animation = 0;
				positionX = event.coordonneeApparenteXLorsDuSaut;
				positionY = event.coordonneeApparenteYLorsDuSaut;
			} else {
				//l'Event ne Saute pas
				direction = event.direction;
				animation = event.animation;
				positionX = event.x;
				positionY = event.y;
			}
			//ajustement selon la taille de l'image
			positionX += event.largeurHitbox/2 - largeur/2;
			positionY += event.hauteurHitbox - hauteur + event.offsetY;
			
			final BufferedImage apparence = eventImage.getSubimage(animation*largeur, direction*hauteur, largeur, hauteur);
			return superposerImages(ecran, apparence, positionX-xCamera, positionY-yCamera);
			/*
			//DEBUG pour visualiser les collisions
			Graphics2D graphics = ecran.createGraphics();
			graphics.setPaint(Color.blue);
			graphics.fillRect(event.x-xCamera, event.y-yCamera, event.largeurHitbox, event.hauteurHitbox);
			return ecran;
			*/
		} else {
			//l'event n'a pas d'image
			return ecran;
		}
	}
	
	/**
	 * Dessine à l'écran un carreau du Tileset aux coordonnées (xEcran;yEcran).
	 * @warning Ne pas oublier de récupérer le résultat de cette méthode.
	 * @param ecran sur lequel on doit dessiner un carreau
	 * @param xEcran position x où dessiner le carreau à l'écran
	 * @param yEcran position y où dessiner le carreau à l'écran
	 * @param numeroCarreau numéro du carreau à dessiner
	 * @param tilesetUtilise Tileset utilisé pour interpréter le décor de la Map
	 * @return écran sur lequel on a dessiné le carreau demandé
	 */
	public final BufferedImage dessinerCarreau(final BufferedImage ecran, final int xEcran, final int yEcran, final int numeroCarreau, final Tileset tilesetUtilise) {
		final BufferedImage dessinCarreau = tilesetUtilise.carreaux[numeroCarreau];
		return superposerImages(ecran, dessinCarreau, xEcran*Fenetre.TAILLE_D_UN_CARREAU, yEcran*Fenetre.TAILLE_D_UN_CARREAU);
	}

	@Override
	public final void keyPressed(final Integer keycode) {
		switch(keycode) {
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
	 * Ouvre une autre Map (dans un nouveau LecteurMap).
	 * @warning cette méthode ne doit être appelée que par le nouveau Lecteur !
	 * @param nouvelleMap sur laquelle le Héros voyage
	 */
	public final void changerMap(final Map nouvelleMap) {
		Fenetre.getFenetre().futurLecteur = this;
		Fenetre.getFenetre().lecteur.allume = false;
		
		//on détruit le Tileset actuel si le prochain n'est pas le même
		if (tilesetActuel!=null && !tilesetActuel.nom.equals(nouvelleMap.tileset.nom)) {
			this.tilesetActuel = null;
		}
	}
	
	/**
	 * Ouvrir le Menu du jeu.
	 * On quitte la Map temporairement (elle est mémorisée) pour parcourir le Menu.
	 */
	public final void ouvrirLeMenu() {
		final Menu menuPause = new MenuPause();
		final LecteurMenu lecteurMenu = new LecteurMenu(this.fenetre, menuPause, this);
		
		this.fenetre.futurLecteur = lecteurMenu;
		lecteurMenu.menu = menuPause;
		this.allume = false;
	}

	@Override
	public final void keyReleased(final Integer keycode) {
		remettreAZeroLAnimationDuHeros(); //s'il s'est arrêté
		mettreHerosDansLaBonneDirection();
	}
	
	/**
	 * Lorsque le Héros s'arrête de marcher, on arrête son animation.
	 */
	public final void remettreAZeroLAnimationDuHeros() {
		final ArrayList<Integer> touchesPressees = fenetre.touchesPressees;
		final Event heros = map.heros;
		if (!touchesPressees.contains(GestionClavier.ToucheRole.BAS) 
		&& !touchesPressees.contains(GestionClavier.ToucheRole.HAUT) 
		&& !touchesPressees.contains(GestionClavier.ToucheRole.GAUCHE) 
		&& !touchesPressees.contains(GestionClavier.ToucheRole.DROITE)) {
			heros.avance = false;
			heros.animation = 0;
		}
	}
	
	/**
	 * Tourner le Héros dans la bonne Direction selon l'entrée clavier.
	 */
	public final void mettreHerosDansLaBonneDirection() {
		final Heros heros = map.heros;
		if (!stopEvent && heros.animationAttaque<=0) {
			final ArrayList<Integer> touchesPressees = fenetre.touchesPressees;
			if ( touchesPressees.contains(GestionClavier.ToucheRole.GAUCHE) ) {
				heros.direction = Event.Direction.GAUCHE;
			} else if ( touchesPressees.contains(GestionClavier.ToucheRole.DROITE) ) {
				heros.direction = Event.Direction.DROITE;
			} else if ( touchesPressees.contains(GestionClavier.ToucheRole.BAS) ) {
				heros.direction = Event.Direction.BAS;
			} else if ( touchesPressees.contains(GestionClavier.ToucheRole.HAUT) ) {
				heros.direction = Event.Direction.HAUT;
			}
		}
	}
	
	/**
	 * Calculer la position x de la caméra
	 * @return position x de la caméra
	 */
	private int calculerXCamera() {
		final int largeurMap = map.largeur;
		if ( !this.map.defilementCameraX ) {
			//map très petite, défilement inutile
			return 0;
		} else {
			//grande map, défilement possible
			final int xCamera = map.heros.x - Fenetre.LARGEUR_ECRAN/2;
			if (xCamera<0) { //caméra ne déborde pas de la map à gauche
				return 0;
			} else if (xCamera+Fenetre.LARGEUR_ECRAN > largeurMap*Fenetre.TAILLE_D_UN_CARREAU) { //caméra ne déborde pas de la map à droite
				return largeurMap*Fenetre.TAILLE_D_UN_CARREAU - Fenetre.LARGEUR_ECRAN;
			} else {
				return xCamera;
			}
		}
	}
	
	/**
	 * Calculer la position y de la caméra
	 * @return position y de la caméra
	 */
	private int calculerYCamera() {
		final int hauteurMap = map.hauteur;
		if ( !this.map.defilementCameraY ) { 
			//map très petite, défilement inutile
			return 0;
		} else {
			//grande map, défilement possible
			final int yCamera = map.heros.y - Fenetre.HAUTEUR_ECRAN/2;
			if (yCamera<0) { //caméra ne déborde pas de la map en haut
				return 0;
			} else if (yCamera+Fenetre.HAUTEUR_ECRAN > hauteurMap*Fenetre.TAILLE_D_UN_CARREAU) { //caméra ne déborde pas de la map en bas
				return hauteurMap*Fenetre.TAILLE_D_UN_CARREAU - Fenetre.HAUTEUR_ECRAN;
			} else {
				return yCamera;
			}
		}
	}
	
	/**
	 * Le Héros arrête son animation pour écouter un Message.
	 */
	public final void normaliserApparenceDuHerosAvantMessage() {
		this.map.heros.animation = 0;
		this.map.heros.animationAttaque = 0;
	}
	
	/**
	 * Déplacer le Héros vers le haut
	 */
	public final void haut() {
		this.mettreHerosDansLaBonneDirection();
		this.map.heros.avance = true;
	}

	/**
	 * Déplacer le Héros vers la gauche
	 */
	public final void gauche() {
		this.mettreHerosDansLaBonneDirection();
		this.map.heros.avance = true;
	}

	/**
	 * Déplacer le Héros vers le bas
	 */
	public final void bas() {
		this.mettreHerosDansLaBonneDirection();
		this.map.heros.avance = true;
	}

	/**
	 * Déplacer le Héros vers la droite
	 */
	public final void droite() {
		this.mettreHerosDansLaBonneDirection();
		this.map.heros.avance = true;
	}
	
	/**
	 * Attaquer ou parler (suivant si gentil ou méchant)
	 */
	public final void action() {
		this.frameDAppuiDeLaToucheAction = this.frameActuelle;
	}

	/**
	 * Transmettre à la Partie le changement d'Arme ordonné à la Fenêtre
	 */
	public final void equiperArmeSuivante() {
		if (!this.stopEvent) { //on ne change pas d'Arme lorsqu'on lit un Message
			Fenetre.getPartieActuelle().equiperArmeSuivante();
		}
	}
	
	/**
	 * Transmettre à la Partie le changement d'Arme ordonné à la Fenêtre
	 */
	public final void equiperArmePrecedente() {
		if (!this.stopEvent) { //on ne change pas d'Arme lorsqu'on lit un Message
			Fenetre.getPartieActuelle().equiperArmePrecedente();
		}
	}

	/**
	 * Utiliser l'objet secondaire.
	 */
	public void objet() {
		//TODO
	}
	
	/**
	 * Charge le petit carré blanc qui entoure l'Arme dans le HUD à l'écran.
	 * @return image constitutive du HUD
	 */
	public static BufferedImage chargerImageHudTouches() {
		try {
			return ImageIO.read(new File(".\\ressources\\Graphics\\Pictures\\carre arme kujira.png"));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
