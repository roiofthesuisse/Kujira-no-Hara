package map;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import commandes.Message;
import commandes.OuvrirMenu;
import jeu.Chronometre;
import main.Commande;
import main.Fenetre;
import main.Lecteur;
import map.meteo.Meteo;
import menu.Texte;
import mouvements.RegarderUnEvent;
import utilitaire.GestionClavier;
import utilitaire.GestionClavier.ToucheRole;
import utilitaire.graphismes.Graphismes;
import utilitaire.graphismes.ModeDeFusion;
import utilitaire.son.LecteurAudio;
import utilitaire.Maths;

/**
 * Le Lecteur de map affiche la Map et les Events.
 * Il reçoit les ordres du clavier pour les transcrire en actions.
 */
public class LecteurMap extends Lecteur {
	//jauges
	private static final int X_AFFICHAGE_ARME = 563;
	private static final int Y_AFFICHAGE_ARME = 4;
	private static final int X_AFFICHAGE_GADGET = 612;
	private static final int Y_AFFICHAGE_GADGET = 4;
	private static final int X_AFFICHAGE_ARGENT = 4;
	private static final int Y_AFFICHAGE_ARGENT = 450;
	private static final int ESPACEMENT_ICONES = 4;
	//icônes de jauges
	public static final BufferedImage HUD_TOUCHES = chargerImageHudTouches();
	public static final BufferedImage HUD_ARGENT = chargerImageHudArgent();
	
	public Map map;
	public Tileset tilesetActuel = null;
	/** vignette actuelle pour l'animation des Autotiles animés de la Map */
	private int vignetteAutotileActuelle = 0;
	
	/** permet de trier les events selon leur coordonnée y pour l'affichage */
	public Comparator<Event> comparateur;
	
	/** si true, les évènements n'avancent plus naturellement (seuls mouvements forcés autorisés) */
	public boolean stopEvent = false;
	public Event eventQuiALanceStopEvent;
	
	/** Message à afficher dans la boîte de dialogue */
	public Message messageActuel = null;
	
	/** Autoriser ou interdire l'accès au Menu depuis la Map ? */
	public boolean autoriserMenu = true;
	
	/** Position x de la caméra */
	private int xCamera;
	/** Position y de la caméra */
	private int yCamera;
	/** Défilement X de la caméra */
	public int defilementX;
	/** Défilement Y de la caméra */
	public int defilementY;
	/** Décalage (en pixels) de l'écran à cause du tremblement de terre */
	public int tremblementDeTerre;
	/** Transition visuelle avec la Map précédente */
	public Transition transition = Transition.AUCUNE;
	
	/**
	 * Constructeur explicite
	 * @param fenetre dont ce Lecteur assure l'affichage
	 * @param transition visuelle pour le passage d'une Map à l'autre
	 */
	public LecteurMap(final Fenetre fenetre, final Transition transition) {
		this.fenetre = fenetre;
		this.transition = transition;
		this.comparateur = new Comparator<Event>() {
	        public int compare(final Event e1, final Event e2) {
	            return e1.compareTo(e2);
	        }
	    };
	}
	
	/**
	 * A chaque frame, calcule l'écran à afficher, avec le décor et les Events dessus.
	 * @param frame dont l'écran est calculé
	 * @warning Ne pas oublier de récupérer le résultat de cette méthode.
	 * @return écran représentant la Map
	 */
	public final BufferedImage calculerAffichage(final int frame) {
		//final long t0 = System.nanoTime(); //mesure de performances
		
		//éventuelle sortie vers la Map adjacente
		this.map.sortirVersLaMapAdjacente();
		
		//ouverture du tileset
		try {
			if (tilesetActuel == null) {
				tilesetActuel = this.map!=null && this.map.tileset!=null ? this.map.tileset : new Tileset(map.tileset.nom);
			}
		} catch (Exception e) {
			LOG.error("Erreur lors de l'ouverture du tileset :", e);
		}
		
		//calcul de la position de la caméra par rapport à la Map
		this.xCamera = calculerXCamera();
		this.yCamera = calculerYCamera();
		
		//panorama
		BufferedImage ecran = dessinerPanorama(xCamera, yCamera);
		
		//on dessine le décor inférieur
		animerLesAutotiles();
		ecran = dessinerDecorInferieur(ecran, xCamera, yCamera, vignetteAutotileActuelle);
		
		//lecture des commandes event
		continuerLaLectureDesPagesDeCommandesEvent();

		//déplacements des évènements
		deplacerLesEvents();
		
		//animation des évènements
		animerLesEvents(frame);

		//TODO DEBUG pour voir la hitbox de l'attaque du héros
		ecran = dessinerLaHitboxDuHeros(ecran, xCamera, yCamera);
		
		//on dessine les évènements et la couche médiane
		ecran = dessinerLesEvents(ecran, xCamera, yCamera, true, vignetteAutotileActuelle);
		
		//on dessine les animations
		ecran = Animation.dessinerLesAnimations(ecran);
		
		//ajouter imageCoucheSurHeros à l'écran
		ecran = dessinerDecorSuperieur(ecran, xCamera, yCamera, vignetteAutotileActuelle);
		
		//météo
		ecran = dessinerMeteo(ecran, frame);
		
		//brouillard
		if (map.brouillard != null) {
			ecran = map.brouillard.dessinerLeBrouillard(ecran, xCamera, yCamera, frame);
		}
		
		//ton
		if (this.map.tileset.ton != null) {
			ecran = Graphismes.superposerImages(ecran, ecran, 0, 0, Graphismes.OPACITE_MAXIMALE, ModeDeFusion.TON_DE_L_ECRAN);
		}
		
		//effet aquatique (lol)
		if (this.map.ondulation != null) {
			ecran = Ondulation.faireOndulerLEcran(ecran, frame);
		}
		
		// Transition visuelle avec la Map précédente
		if (!this.allume) {
			// Faire une capture d'écran juste avant l'arrêt de l'ancienne Map
			final Lecteur futurLecteur0 = Fenetre.getFenetre().futurLecteur;
			if (futurLecteur0 instanceof LecteurMap) {
				final LecteurMap futurLecteur = (LecteurMap) futurLecteur0;
				if (!Transition.AUCUNE.equals(futurLecteur.transition)) {
					final boolean afficherLeHeros = Transition.ROND.equals(futurLecteur.transition);
					futurLecteur.transition.captureDeLaMapPrecedente = capturerLaMap(afficherLeHeros);
				}
			}
		} else {
			// Réutiliser cette capture d'écran au début de la nouvelle Map
			ecran = this.transition.calculer(ecran, this.map, frame);
		}
		
		//afficher les images
		ecran = Picture.dessinerLesImages(ecran);
		
		//ajouter les jauges
		ecran = dessinerLesJauges(ecran);
		
		//chronometre
		final Chronometre chronometre = Fenetre.getPartieActuelle().chronometre;
		if (chronometre!=null) {
			ecran = chronometre.dessinerChronometre(ecran);
		}
		
		//on affiche le message
		if (messageActuel!=null) {
			ecran = Graphismes.superposerImages(
					ecran, 
					this.messageActuel.image,
					Message.positionBoiteMessage.xAffichage, 
					Message.positionBoiteMessage.yAffichage);
		}
		
		//supprimer events dont l'attribut "supprimé" est à true
		supprimerLesEventsASupprimer();
		
		ajouterLesEventsAAjouter();
		
		//this.fenetre.mesuresDePerformance.add(new Long(t1 - t0).toString());

		return ecran;
	}

	/**
	 * Dessiner l'image de fond (noir ou Panorama)
	 * @param xCamera position x de la caméra
	 * @param yCamera position y d ela caméra
	 * @return image de fond
	 */
	private BufferedImage dessinerPanorama(final int xCamera, final int yCamera) {
		if (this.map.panoramaActuel != null) {
			//parallaxe
			int xPanorama = this.map.parallaxeActuelle * xCamera / Maths.POURCENTS;
			int yPanorama = this.map.parallaxeActuelle * yCamera / Maths.POURCENTS;
			final int xMax = this.map.panoramaActuel.getWidth() - Fenetre.LARGEUR_ECRAN;
			final int yMax = this.map.panoramaActuel.getHeight() - Fenetre.HAUTEUR_ECRAN;
			if (xPanorama > xMax) {
				xPanorama = xMax;
			} else if (xPanorama < 0) {
				xPanorama = 0;
			}
			if (yPanorama > yMax) {
				yPanorama = yMax;
			} else if (yPanorama < 0) {
				yPanorama = 0;
			}
			return Graphismes.clonerUneImage(this.map.panoramaActuel.getSubimage(xPanorama, yPanorama, Fenetre.LARGEUR_ECRAN, Fenetre.HAUTEUR_ECRAN));
		} else {
			return Graphismes.ecranColore(Color.BLACK);
		}
	}

	/**
	 * Les Autotiles animés ont plusieurs vignettes d'animation. De temps en temps, il faut changer de vignette.
	 */
	private void animerLesAutotiles() {
		if (this.map.contientDesAutotilesAnimes && (this.frameActuelle % Autotile.FREQUENCE_ANIMATION_AUTOTILE == 0)) {
			vignetteAutotileActuelle += 1;
			vignetteAutotileActuelle = Maths.modulo(vignetteAutotileActuelle, Autotile.NOMBRE_VIGNETTES_AUTOTILE_ANIME);
		}
	}

	/**
	 * Dessiner à l'écran le décor situé au dessus du Héros.
	 * @param ecran sur lequel dessiner le décor
	 * @param xCamera position x de la caméra
	 * @param yCamera position y de la caméra
	 * @param vignetteAutotile vignette d'animation actuelle de l'Autotile animé
	 * @return écran avec le décor supérieur peint
	 */
	private BufferedImage dessinerDecorSuperieur(BufferedImage ecran, final int xCamera, final int yCamera, final int vignetteAutotile) {
		ecran = Graphismes.superposerImages(ecran, this.map.getImageCoucheSurHeros(vignetteAutotile), -xCamera, -yCamera);
		return ecran;
	}

	/**
	 * Dessiner à l'écran le décor situé en dessous du Héros.
	 * @param ecran sur lequel dessiner le décor
	 * @param xCamera position x de la caméra
	 * @param yCamera position y de la caméra
	 * @param vignetteAutotile vignette d'animation actuelle de l'Autotile animé
	 * @return écran avec le décor inférieur peint
	 */
	private BufferedImage dessinerDecorInferieur(BufferedImage ecran, final int xCamera, final int yCamera, final int vignetteAutotile) {
		ecran = Graphismes.superposerImages(ecran, this.map.getImageCoucheSousHeros(vignetteAutotile), -xCamera, -yCamera);
		return ecran;
	}

	/**
	 * Dessiner à l'écran les effets météorologiques.
	 * @param ecran sur lequel on dessine
	 * @param frame de l'effet météorologique
	 * @return écran avec la météo
	 */
	private BufferedImage dessinerMeteo(BufferedImage ecran, final int frame) {
		final Meteo meteo = Fenetre.getPartieActuelle().meteo;
		if (meteo != null) {
			ecran = Graphismes.superposerImages(ecran, meteo.calculerImage(frame), 0, 0);
		}
		return ecran;
	}

	/**
	 * Dessiner à l'écran les jauges et informations extradiégétiques à destination du joueur
	 * @param ecran sur lequel on dessine les jauges
	 * @return écran avec les jauges dessinées
	 */
	private BufferedImage dessinerLesJauges(BufferedImage ecran) {
		//touches
		ecran = Graphismes.superposerImages(ecran, HUD_TOUCHES, 0, 0);
		
		//icone de l'Arme equipée
		try {
			ecran = Graphismes.superposerImages(ecran, Fenetre.getPartieActuelle().getArmeEquipee().icone, X_AFFICHAGE_ARME, Y_AFFICHAGE_ARME);
		} catch (NullPointerException e) {
			//pas d'Arme équipée
		}
		
		//icone du Gadget équipé
		try {
			ecran = Graphismes.superposerImages(ecran, Fenetre.getPartieActuelle().getGadgetEquipe().icone, X_AFFICHAGE_GADGET, Y_AFFICHAGE_GADGET);
		} catch (NullPointerException e) {
			//pas de Gadget équipé
		}
		
		//argent
		final int argent = Fenetre.getPartieActuelle().argent;
		if (argent > 0) {
			ecran = Graphismes.superposerImages(ecran, HUD_ARGENT, X_AFFICHAGE_ARGENT, Y_AFFICHAGE_ARGENT);
			final ArrayList<String> contenuTexte = new ArrayList<String>();
			contenuTexte.add("" + argent);
			final Texte texte = new Texte(contenuTexte, Color.white, Color.black, Texte.Taille.MOYENNE);
			final BufferedImage texteImage = texte.getImage();
			ecran = Graphismes.superposerImages(ecran, texteImage, X_AFFICHAGE_ARGENT+HUD_ARGENT.getWidth()+ESPACEMENT_ICONES, Y_AFFICHAGE_ARGENT);
		}
		
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
			if (Fenetre.getPartieActuelle().getArmeEquipee() != null) {
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
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ecran;
	}

	/**
	 * Lire la Page active de chaque Event de la Map.
	 */
	private void continuerLaLectureDesPagesDeCommandesEvent() {
		//en cas de stopEvent, seul l'Event qui a figé tout le monde est lu (Commandes)
		if (stopEvent) {
			activerUnePageEtLExecuter(this.eventQuiALanceStopEvent);
		} else {
			//lire tous les Events de la Map (sauf le Héros)
			for (Event event : this.map.events) {
				if (!event.equals(this.map.heros)) { //le Héros est calculé en dernier
					activerUnePageEtLExecuter(event);
				}
			}
			//le Héros est calculé en dernier pour éviter les problèmes d'épée
			activerUnePageEtLExecuter(this.map.heros);
			
			//lire les PagesCommunes
			lireLesPagesCommunes();
		}
	}
	
	/**
	 * Activer une Page (si aucune n'est activée) de l'Event (s'il n'est pas supprimé et l'exécuter.
	 * @param event dont il faut activer une Page et l'exécuter
	 */
	private void activerUnePageEtLExecuter(final Event event) {
		if (!event.supprime && !event.saute) {
			if (event.pageActive == null || event.pageActive.commandes==null) {
				event.activerUnePage();
			}

			if (event.pageActive != null) {
				event.pageActive.executer();
			}
		}
	}
	
	/**
	 * Lire les Pages de code commun.
	 */
	private void lireLesPagesCommunes() {
		if (this.pagesCommunes.size() > 0) {
			for (PageCommune pageCommune : this.pagesCommunes) {
				if (!pageCommune.active) {
					pageCommune.essayerDActiver();
				}
				if (pageCommune.active) {
					pageCommune.executer();
				}
			}
		}
	}

	/**
	 * Calculer le nouvel écran, avec les Events dessinés dessus.
	 * Ne pas oublier de récupérer le résultat de cette méthode.
	 * @param ecran sur lequel on dessine les Events
	 * @param xCamera position x de la caméra
	 * @param yCamera position y de la caméra
	 * @param dessinerLeHeros le Héros doit-il être visible sur l'ancienne Map ?
	 * @param vignetteAutotile vignette d'animation actuelle de l'Autotile animé
	 * @return écran avec les Events dessinés dessus
	 */
	private BufferedImage dessinerLesEvents(BufferedImage ecran, final int xCamera, final int yCamera, final boolean dessinerLeHeros, 
			final int vignetteAutotile) {
		try {
			Collections.sort(this.map.events, this.comparateur); //on trie les events du plus derrière au plus devant
			int bandeletteActuelle = 0;
			int bandeletteEvent = 0;
			for (Event event : this.map.events) {
				if (!event.supprime) {
					//dessiner la bandelette de décor médian
					bandeletteEvent = event.y / Fenetre.TAILLE_D_UN_CARREAU;
					if (bandeletteEvent > bandeletteActuelle) {
						final BufferedImage imageBandelette = this.map.getImageCoucheAvecHeros(vignetteAutotile, bandeletteActuelle, bandeletteEvent);
						ecran = Graphismes.superposerImages(ecran, imageBandelette,
								-xCamera, bandeletteActuelle*Fenetre.TAILLE_D_UN_CARREAU-yCamera);
						
						bandeletteActuelle = bandeletteEvent;
					}
					
					//dessiner l'Event
					if (dessinerLeHeros || !event.equals(map.heros)) {
						ecran = dessinerEvent(ecran, event, xCamera, yCamera);
					}
				}
			}
			
			//dernière bandelette
			final BufferedImage imageBandelette = this.map.getImageCoucheAvecHeros(vignetteAutotile, bandeletteEvent, this.map.hauteur+1);
			ecran = Graphismes.superposerImages(ecran, imageBandelette,
					-xCamera, bandeletteActuelle*Fenetre.TAILLE_D_UN_CARREAU-yCamera);
			
		} catch (Exception e) {
			LOG.error("Erreur lors du dessin des évènements :", e);
		}
		return ecran;
	}

	/**
	 * Donne la bonne valeur à l'attribut "animation" avant d'envoyer l'event à l'affichage.
	 * @param frame d'animation des Events
	 */
	private void animerLesEvents(final int frame) {
		if (this.stopEvent) {
			return; //pas d'animation en cas de stopEvent
		}
		
		try {
			for (Event event : this.map.events) {
				final boolean passerALAnimationSuivante = (frame % event.frequenceActuelle == 0) //fréquence d'animation
				|| (event.avance && !event.avancaitALaFramePrecedente); //la première frame d'animation est un pas
				
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
			LOG.error("erreur lors de l'animation des évènements dans la boucle d'affichage de la map :", e);
		}
	}
	
	/**
	 * Donne la bonne valeur aux positions x et y avant d'envoyer l'Event à l'affichage.
	 * En cas de stopEvent, seuls les Mouvements commandités par l'Event qui a figé tout sont lus.
	 */
	private void deplacerLesEvents() {
		try {
			//animer la marche du Héros si touche pressée
			if ( GestionClavier.ToucheRole.HAUT.touche.enfoncee
			  || GestionClavier.ToucheRole.GAUCHE.touche.enfoncee
			  || GestionClavier.ToucheRole.BAS.touche.enfoncee
			  || GestionClavier.ToucheRole.DROITE.touche.enfoncee ) {
				map.heros.avance = true;
			}
			
			//déplacer chaque Event
			for (Event event : this.map.events) {	
				if (!event.supprime) {
					event.deplacer(); //on effectue le déplacement si possible (pas d'obstacles rencontrés)
				}
			}
		} catch (Exception e) {
			LOG.error("Erreur lors du déplacement des évènements :", e);
		}
	}

	/**
	 * Certains Events ont été marqués "à supprimer" durant la lecture des Commandes.
	 * On les élimine maintenant, une fois que la lecture des Commandes est terminée.
	 * En effet on ne peut pas supprimer des Events lorsqu'on est encore dans la boucle qui parcourt la liste des Events.
	 */
	private void supprimerLesEventsASupprimer() {
		int nombreDEvents = this.map.events.size();
		Event eventAsupprimer;
		for (int i = 0; i<nombreDEvents; i++) {
			eventAsupprimer = this.map.events.get(i);
			if (eventAsupprimer.supprime) {
				this.map.events.remove(i);
				LOG.info("Suppression de l'event "+eventAsupprimer.nom);
				LOG.debug("Nombre d'events sur la map : "+this.map.events.size());
				nombreDEvents--;
				i--;
			}
		}
	}
	
	/**
	 * Ajouter les nouveaux Events à ajouter à la Map pour le tour suivant.
	 */
	private void ajouterLesEventsAAjouter() {
		int nombreDEvents = this.map.eventsAAjouter.size();
		Event eventAajouter;
		for (int i = 0; i<nombreDEvents; i++) {
			eventAajouter = this.map.eventsAAjouter.get(i);
			
			//on l'ajoute au hash des Events avec un numéro
			if (eventAajouter.id < 0) {
				eventAajouter.id = this.map.calculerNouvelIdPourEventsHash();
			}
			this.map.eventsHash.put(eventAajouter.id, eventAajouter);
			
			//on l'ajoute à la liste des Events
			this.map.events.add(eventAajouter);
			eventAajouter.id = this.map.events.size();
			
			LOG.info("Ajout de l'event "+eventAajouter.nom);
			LOG.debug("Nombre d'events sur la map : "+this.map.events.size());
			this.map.eventsAAjouter.remove(i);
			nombreDEvents--;
			i--;
		}
	}
	
	

	/**
	 * Faire une capture d'écran des collisions
	 */
	public final void photographierCollision() {
		final BufferedImage img = new BufferedImage(Fenetre.LARGEUR_ECRAN, Fenetre.HAUTEUR_ECRAN, Graphismes.TYPE_DES_IMAGES);
		final Graphics2D graphics = img.createGraphics();
		graphics.setPaint(Color.white);
		graphics.fillRect(0, 0, Fenetre.LARGEUR_ECRAN, Fenetre.HAUTEUR_ECRAN);
		
		img.setRGB(map.heros.x, map.heros.y, Color.red.getRGB());
		img.setRGB(map.events.get(1).x, map.events.get(1).y, Color.blue.getRGB());
		Graphismes.sauvegarderImage(img, "collision");
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
			final int positionX = event.xImage();
			final int positionY = event.yImage();
			
			final int direction = event.direction;
			final int animation;
			if (event.saute && !event.directionFixeActuelle) {
				//l'Event est en train de sauter
				animation = 0; //TODO attention : si la vignette normale de l'event n'est pas la vignette 0, l'event va changer d'apparence
				
			} else {
				//l'Event ne Saute pas
				animation = event.animation;
			}
			
			/*
			//DEBUG pour visualiser les collisions //TODO commenter
			Graphics2D graphics = ecran.createGraphics();
			graphics.setPaint(Color.blue);
			graphics.fillRect(event.x-xCamera, event.y-yCamera, event.largeurHitbox, event.hauteurHitbox);
			//voilà
			*/
			
			final BufferedImage apparence;
			try {
				apparence = eventImage.getSubimage(animation*largeur, direction*hauteur, largeur, hauteur);
			} catch (RasterFormatException rfe) {
				LOG.error("La vignette d'Event est mal découpée "
						+ "(animation:"+animation+";direction:"+direction+";"
						+ "largeur:"+largeur+";hauteur:"+hauteur+")", rfe
				);
				return ecran;
			}
			return Graphismes.superposerImages(ecran, apparence, positionX-xCamera, positionY-yCamera, event.opaciteActuelle, event.modeDeFusionActuel);
		} else {
			//l'event n'a pas d'image
			return ecran;
		}
	}

	@Override
	public final void keyPressed(final ToucheRole touchePressee) {
		// action spécifique selon la touche
		switch (touchePressee) {
			case MENU : 
				this.ouvrirLeMenu(); 
				break;
			case HAUT : 
				this.haut(); 
				break;
			case GAUCHE : 
				this.gauche(); 
				break;
			case BAS : 
				this.bas(); 
				break;
			case DROITE : this.droite(); 
				break;
			case ARME_SUIVANTE : 
				this.equiperArmeSuivante(); 
				break;
			case ACTION : 
				this.action(); 
				break;
			case ARME_PRECEDENTE : 
				this.equiperArmePrecedente(); 
				break;
			case ACTION_SECONDAIRE : 
				//rien
				break;
			case CAPTURE_D_ECRAN : 
				this.faireUneCaptureDEcran(); 
				break;
			case BREAKPOINT :
				final Fenetre fenetre = Fenetre.getFenetre();
				break;
			default : 
				break; // touche inconnue
		}
	}
	
	/**
	 * Ouvre une autre Map (dans un nouveau LecteurMap).
	 * @warning cette méthode ne doit être appelée que par le nouveau Lecteur !
	 * @param nouvelleMap sur laquelle le Héros voyage
	 */
	public final void devenirLeNouveauLecteurMap(final Map nouvelleMap) {
		Fenetre.getFenetre().futurLecteur = this;
		Fenetre.getFenetre().lecteur.allume = false;
		
		// On détruit le Tileset actuel si le prochain n'est pas le même
		if (tilesetActuel!=null && !tilesetActuel.nom.equals(nouvelleMap.tileset.nom)) {
			this.tilesetActuel = null;
		}
	}
	
	/**
	 * Prendre une capture d'écran de la Map sans le Héros ni les jauges.
	 * @param afficherLeHeros le Héros doit-il être visible sur l'ancienne Map ?
	 * @return capture de la Map
	 */
	private BufferedImage capturerLaMap(final boolean afficherLeHeros) {
		BufferedImage capture = dessinerPanorama(this.xCamera, this.yCamera);
		capture = dessinerDecorInferieur(capture, this.xCamera, this.yCamera, this.vignetteAutotileActuelle);
		capture = dessinerLesEvents(capture, this.xCamera, this.yCamera, afficherLeHeros, this.vignetteAutotileActuelle);
		capture = Animation.dessinerLesAnimations(capture);
		capture = dessinerDecorSuperieur(capture, this.xCamera, this.yCamera, this.vignetteAutotileActuelle);
		capture = dessinerMeteo(capture, this.frameActuelle);
		//brouillard
		if (this.map.brouillard != null) {
			capture = map.brouillard.dessinerLeBrouillard(capture, this.xCamera, this.yCamera, this.frameActuelle);
		}
		//ton
		if (this.map.tileset.ton != null) {
			capture = Graphismes.superposerImages(capture, capture, 0, 0, Graphismes.OPACITE_MAXIMALE, ModeDeFusion.TON_DE_L_ECRAN);
		}
		return capture;
	}

	/**
	 * Ouvrir le Menu du jeu.
	 * On quitte la Map temporairement (elle est mémorisée) pour parcourir le Menu.
	 */
	public final void ouvrirLeMenu() {
		if (!this.stopEvent && this.autoriserMenu) { //impossible d'ouvrir le Menu en cas de stopEvent ou de Menu interdit
			final Commande menuPause = new OuvrirMenu("Statut", 0);
			menuPause.executer(0, null);
		}
	}

	@Override
	public final void keyReleased(final ToucheRole toucheRelachee) {
		remettreAZeroLAnimationDuHeros(); //s'il s'est arrêté
	}
	
	/**
	 * Lorsque le Héros s'arrête de marcher, on arrête son animation.
	 */
	public final void remettreAZeroLAnimationDuHeros() {
		final Event heros = map.heros;
		if (!GestionClavier.ToucheRole.BAS.touche.enfoncee
		 && !GestionClavier.ToucheRole.HAUT.touche.enfoncee
		 && !GestionClavier.ToucheRole.GAUCHE.touche.enfoncee
		 && !GestionClavier.ToucheRole.DROITE.touche.enfoncee) {
			heros.avance = false;
			heros.animation = 0;
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
			return this.tremblementDeTerre;
		} else {
			//grande map, défilement possible
			int nouveauXCamera = map.heros.x - Fenetre.LARGEUR_ECRAN/2;
			
			if (nouveauXCamera<0) { //caméra ne déborde pas de la map à gauche
				return (this.defilementX>0 ? this.defilementX : 0) 
						+ this.tremblementDeTerre;
			} else if (nouveauXCamera+Fenetre.LARGEUR_ECRAN > largeurMap*Fenetre.TAILLE_D_UN_CARREAU) { //caméra ne déborde pas de la map à droite
				return largeurMap*Fenetre.TAILLE_D_UN_CARREAU - Fenetre.LARGEUR_ECRAN 
						+ (this.defilementX<0 ? this.defilementX : 0) 
						+ this.tremblementDeTerre;
			} else {
				return nouveauXCamera 
						+ this.defilementX 
						+ this.tremblementDeTerre;
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
			int nouveauYCamera = map.heros.y - Fenetre.HAUTEUR_ECRAN/2;
			
			if (nouveauYCamera<0) { //caméra ne déborde pas de la map en haut
				return 0 + (this.defilementY>0 ? this.defilementY : 0);
			} else if (nouveauYCamera+Fenetre.HAUTEUR_ECRAN > hauteurMap*Fenetre.TAILLE_D_UN_CARREAU) { //caméra ne déborde pas de la map en bas
				return hauteurMap*Fenetre.TAILLE_D_UN_CARREAU - Fenetre.HAUTEUR_ECRAN + (this.defilementY<0 ? this.defilementY : 0);
			} else {
				return nouveauYCamera + this.defilementY;
			}
		}
	}
	
	/**
	 * Le Héros arrête son animation pour écouter un Message.
	 * @param event avec lequel le Héros discute
	 */
	public final void normaliserApparenceDesInterlocuteursAvantMessage(final Event event) {
		// Normaliser le Héros
		final Heros heros = this.map.heros;
		// Le Héros arrête son animation
		if (!heros.animeALArretActuel) {
			heros.animation = 0;
		}
		// Le Héros arrête son attaque
		heros.animationAttaque = 0;
				
		// Normaliser l'intelocuteur
		// L'interlocuteur arrête son animation
		if (!event.animeALArretActuel && !event.directionFixeActuelle) {
			event.animation = 0; //TODO attention : si la vignette par défaut de l'event n'est pas la vignette 0, il va changer d'apparence
		}
		// L'interlocuteur se tourne vers le Héros
		if (!event.directionFixeActuelle) {
			event.direction = RegarderUnEvent.calculerDirectionDeRegard(event, heros);
		}
	}
	
	/**
	 * Déplacer le Héros vers le haut
	 */
	public final void haut() {
		if (this.messageActuel!=null) {
			//les touches directionnelles servent au Message/Choix/EntrerUnNombre
			this.messageActuel.haut();
		} else if (!this.stopEvent) {
			//les touches directionnelles servent à faire avancer le Héros
			//this.map.heros.mettreDansLaBonneDirection();
			this.map.heros.avance = true;
		}
	}

	/**
	 * Déplacer le Héros vers la gauche
	 */
	public final void gauche() {
		if (this.messageActuel!=null) {
			//les touches servent au Message/Choix/EntrerUnNombre
			this.messageActuel.gauche();
		} else if (!this.stopEvent) {
			//les touches directionnelles servent à faire avancer le Héros
			//this.map.heros.mettreDansLaBonneDirection();
			this.map.heros.avance = true;
		}
	}

	/**
	 * Déplacer le Héros vers le bas
	 */
	public final void bas() {
		if (this.messageActuel!=null) {
			//les touches servent au Message/Choix/EntrerUnNombre
			this.messageActuel.bas();
		} else if (!this.stopEvent) {
			//les touches directionnelles servent à faire avancer le Héros
			//this.map.heros.mettreDansLaBonneDirection();
			this.map.heros.avance = true;
		}
	}

	/**
	 * Déplacer le Héros vers la droite
	 */
	public final void droite() {
		if (this.messageActuel!=null) {
			//les touches servent au Message/Choix/EntrerUnNombre
			this.messageActuel.droite();
		} else if (!this.stopEvent) {
			//les touches directionnelles servent à faire avancer le Héros
			//this.map.heros.mettreDansLaBonneDirection();
			this.map.heros.avance = true;
		}
	}
	
	/**
	 * Attaquer ou parler (suivant si gentil ou méchant)
	 */
	public final void action() {
		if (this.messageActuel!=null) {
			//les touches servent au Message/Choix/EntrerUnNombre
			this.messageActuel.action();
		}
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
	 * Charge le petit carré blanc qui entoure l'Arme dans le HUD à l'écran.
	 * @return image constitutive du HUD
	 */
	public static BufferedImage chargerImageHudTouches() {
		try {
			return Graphismes.ouvrirImage("Pictures", "carre arme kujira.png");
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Charge l'icône de l'argent.
	 * @return image constitutive du HUD
	 */
	public static BufferedImage chargerImageHudArgent() {
		try {
			return Graphismes.ouvrirImage("Icons", "ecaille icon.png");
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected final String typeDeLecteur() {
		return "LecteurMap";
	}

	@Override
	public final void lireMusique() {
		final Map map = ((LecteurMap) this).map;
		if (map.volumeBGM == null) {
			LOG.warn("volumeBGM");
		}
		if (map.volumeBGS == null) {
			LOG.warn("volumeBGS");
		}
		if (map.nomBGM != null && !map.nomBGM.isEmpty()) {
			LecteurAudio.playBgm(map.nomBGM, map.volumeBGM, 0);
		}
		if (map.nomBGS != null && !map.nomBGS.isEmpty()) {
			LecteurAudio.playBgs(map.nomBGS, map.volumeBGS, 0);
		}
	}
	
	/**
	 * La Transition à partir de la Map précédente est-elle terminée ?
	 * @return true si fini
	 */
	public final boolean laTransitionEstTerminee() {
		return Transition.AUCUNE.equals(this.transition) || this.frameActuelle >= Transition.DUREE_TRANSITION;
	}
	
}
