package map;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import commandes.Message;
import commandes.OuvrirMenu;
import jeu.Chronometre;
import main.Commande;
import main.Fenetre;
import main.Lecteur;
import main.Main;
import map.meteo.Meteo;
import mouvements.Mouvement;
import mouvements.RegarderUnEvent;
import mouvements.Sauter;
import utilitaire.GestionClavier;
import utilitaire.GestionClavier.ToucheRole;
import utilitaire.Maths;
import utilitaire.graphismes.Graphismes;
import utilitaire.son.LecteurAudio;

/**
 * Le Lecteur de map affiche la Map et les Events. Il re�oit les ordres du
 * clavier pour les transcrire en actions.
 */
public class LecteurMap extends Lecteur {

	public Map map;
	/** vignette actuelle pour l'animation des Autotiles anim�s de la Map */
	private int vignetteAutotileActuelle = 0;

	/**
	 * si true, les evenements n'avancent plus naturellement (seuls mouvements
	 * forc�s autoris�s)
	 */
	public boolean stopEvent = false;
	/**
	 * si true, le Heros n'avance plus naturellement (seuls mouvements forc�s
	 * autoris�s)
	 */
	public boolean stopHeros = false;
	public Event eventQuiALanceStopEvent;

	/** Message a afficher dans la boite de dialogue */
	public Message messageActuel = null;
	public Message messagePrecedent = null;

	/** Autoriser ou interdire l'acc�s au Menu depuis la Map ? */
	public boolean autoriserMenu = true;

	/** Position x de la cam�ra */
	private int xCamera;
	/** Position y de la cam�ra */
	private int yCamera;
	/** D�filement X de la cam�ra */
	public int defilementX;
	/** D�filement Y de la cam�ra */
	public int defilementY;
	/** Decalage (en pixels) de l'ecran a cause du tremblement de terre */
	public int tremblementDeTerre;
	/** Transition visuelle avec la Map pr�c�dente */
	public Transition transition = Transition.AUCUNE;
	public int[] tonActuel = null;

	/** Notifier le joueur a l'ecran concernant les nouvelles quetes */
	public List<NotificationQuete> notificationsQuetes = new ArrayList<>();

	/**
	 * Constructeur vide
	 */
	public LecteurMap() {
	}

	/**
	 * A chaque frame, calcule l'ecran a afficher, avec le decor et les Events
	 * dessus.
	 * 
	 * @param frame dont l'ecran est calcul�
	 * @warning Ne pas oublier de recuperer le r�sultat de cette Methode.
	 * @return ecran repr�sentant la Map
	 */
	public final BufferedImage calculerAffichage(final int frame) {
		// final long t0 = System.nanoTime(); //mesure de performances

		// �ventuelle sortie vers la Map adjacente
		this.map.sortirVersLaMapAdjacente();

		// calcul de la position de la cam�ra par rapport a la Map
		this.xCamera = calculerXCamera();
		this.yCamera = calculerYCamera();

		// ton
		if (this.tonActuel == null) {
			this.tonActuel = this.map.tileset.ton;
		}

		// panorama
		BufferedImage ecran = dessinerPanorama(xCamera, yCamera);

		// on dessine le decor inf�rieur
		animerLesAutotiles();
		ecran = dessinerDecorInferieur(ecran, xCamera, yCamera, vignetteAutotileActuelle);

		// lecture des commandes event
		continuerLaLectureDesPagesDeCommandesEvent();

		// deplacements des evenements
		deplacerLesEvents();

		// animation des evenements
		animerLesEvents(frame);

		// TODO DEBUG pour voir la hitbox de l'attaque du Heros
		// ecran = dessinerLaHitboxDuHeros(ecran, xCamera, yCamera);

		// on dessine les evenements et la couche m�diane
		ecran = dessinerLesEvents(ecran, xCamera, yCamera, true, vignetteAutotileActuelle);

		// ajouter imageCoucheSurHeros a l'ecran
		ecran = dessinerDecorSuperieur(ecran, xCamera, yCamera, vignetteAutotileActuelle);

		// on dessine les evenements au dessus de tout et la couche m�diane
		ecran = dessinerLesEventsAuDessusDeTout(ecran, xCamera, yCamera, true);

		// on dessine les animations
		ecran = Animation.dessinerLesAnimations(ecran, xCamera, yCamera);

		// m�t�o
		ecran = dessinerMeteo(ecran, frame);

		// brouillard
		if (map.brouillard != null) {
			ecran = map.brouillard.dessinerLeBrouillard(ecran, xCamera, yCamera, frame);
		}

		// ton
		if (this.tonActuel != null) {
			ecran = Graphismes.appliquerTon(ecran, this.tonActuel);
		}

		// effet aquatique (lol)
		if (this.map.ondulation != null) {
			ecran = this.map.ondulation.faireOndulerLEcran(ecran, frame);
		}

		// Transition visuelle avec la Map pr�c�dente
		if (!this.allume) {
			// Faire une capture d'ecran juste avant l'arret de l'ancienne Map
			final Lecteur futurLecteur0 = Main.futurLecteur;
			if (futurLecteur0 instanceof LecteurMap) {
				final LecteurMap futurLecteur = (LecteurMap) futurLecteur0;
				if (!Transition.AUCUNE.equals(futurLecteur.transition)) {
					final boolean afficherLeHeros = Transition.ROND.equals(futurLecteur.transition);
					futurLecteur.transition.captureDeLaMapPrecedente = capturerLaMap(afficherLeHeros);
				}
			}
		} else {
			// R�utiliser cette capture d'ecran au d�but de la nouvelle Map
			ecran = this.transition.calculer(ecran, this.map, frame);
		}

		// afficher les images basses
		ecran = Picture.dessinerLesImages(ecran, true);

		// ajouter les jauges
		// TODO ecran = Jauges.dessinerLesJauges(ecran);

		// afficher les images hautes
		ecran = Picture.dessinerLesImages(ecran, false);

		// chronometre
		final Chronometre chronometre = Main.getPartieActuelle().chronometre;
		if (chronometre != null) {
			ecran = chronometre.dessinerChronometre(ecran);
		}

		// notifications de quetes
		ecran = NotificationQuete.dessinerLesNotificationsDeQuetes(ecran, this.notificationsQuetes);

		// on affiche le message
		if (messageActuel != null) {
			ecran = Graphismes.superposerImages(ecran, this.messageActuel.image,
					Message.positionBoiteMessage.xAffichage, Message.positionBoiteMessage.yAffichage);
		}

		// supprimer events dont l'attribut "supprime" est a true
		supprimerLesEventsASupprimer();

		ajouterLesEventsAAjouter();

		// this.fenetre.mesuresDePerformance.add(new Long(t1 - t0).toString());

		return ecran;
	}

	/**
	 * Dessiner l'image de fond (noir ou Panorama)
	 * 
	 * @param xCamera position x de la cam�ra
	 * @param yCamera position y d ela cam�ra
	 * @return image de fond
	 */
	private BufferedImage dessinerPanorama(final int xCamera, final int yCamera) {
		if (this.map.panoramaActuel != null) {
			// parallaxe
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
			return Graphismes.clonerUneImage(this.map.panoramaActuel.getSubimage(xPanorama, yPanorama,
					Fenetre.LARGEUR_ECRAN, Fenetre.HAUTEUR_ECRAN));
		} else {
			return Graphismes.ecranColore(Color.BLACK);
		}
	}

	/**
	 * Les Autotiles anim�s ont plusieurs vignettes d'animation. De temps en temps,
	 * il faut changer de vignette.
	 */
	private void animerLesAutotiles() {
		if (this.map.contientDesAutotilesAnimes && (this.frameActuelle % Autotile.FREQUENCE_ANIMATION_AUTOTILE == 0)) {
			vignetteAutotileActuelle += 1;
			vignetteAutotileActuelle = Maths.modulo(vignetteAutotileActuelle, Autotile.NOMBRE_VIGNETTES_AUTOTILE_ANIME);
		}
	}

	/**
	 * Dessiner a l'ecran le decor situe au dessus du Heros.
	 * 
	 * @param ecran            sur lequel dessiner le decor
	 * @param xCamera          position x de la cam�ra
	 * @param yCamera          position y de la cam�ra
	 * @param vignetteAutotile vignette d'animation actuelle de l'Autotile anim�
	 * @return ecran avec le decor superieur peint
	 */
	private BufferedImage dessinerDecorSuperieur(BufferedImage ecran, final int xCamera, final int yCamera,
			final int vignetteAutotile) {
		ecran = Graphismes.superposerImages(ecran, this.map.getImageCoucheDecorSuperieur(vignetteAutotile), -xCamera,
				-yCamera);
		return ecran;
	}

	/**
	 * Dessiner a l'ecran le decor situe en dessous du Heros.
	 * 
	 * @param ecran            sur lequel dessiner le decor
	 * @param xCamera          position x de la cam�ra
	 * @param yCamera          position y de la cam�ra
	 * @param vignetteAutotile vignette d'animation actuelle de l'Autotile anim�
	 * @return ecran avec le decor inf�rieur peint
	 */
	private BufferedImage dessinerDecorInferieur(BufferedImage ecran, final int xCamera, final int yCamera,
			final int vignetteAutotile) {
		ecran = Graphismes.superposerImages(ecran, this.map.getImageCoucheDecorInferieur(vignetteAutotile), -xCamera,
				-yCamera);
		return ecran;
	}

	/**
	 * Dessiner a l'ecran les effets m�t�orologiques.
	 * 
	 * @param ecran sur lequel on dessine
	 * @param frame de l'effet m�t�orologique
	 * @return ecran avec la m�t�o
	 */
	private BufferedImage dessinerMeteo(BufferedImage ecran, final int frame) {
		final Meteo meteo = Main.getPartieActuelle().meteo;
		if (meteo != null) {
			ecran = Graphismes.superposerImages(ecran, meteo.calculerImage(frame), 0, 0);
		}
		return ecran;
	}

	/**
	 * Pour le debug, on peut souhaiter afficher la HitBox du Heros a l'ecran.
	 * 
	 * @param ecran   sur lequel on dessine la Htibox du Heros
	 * @param xCamera position x de la cam�ra
	 * @param yCamera position y de la cam�ra
	 * @return ecran avec la Hitbox du Heros dessin�e dessus
	 */
	@SuppressWarnings("unused")
	private BufferedImage dessinerLaHitboxDuHeros(BufferedImage ecran, final int xCamera, final int yCamera) {
		try {
			if (Main.getPartieActuelle().getArmeEquipee() != null) {
				final Hitbox zoneDAttaqueDuHeros = Main.getPartieActuelle().getArmeEquipee().hitbox;
				final int[] coord = zoneDAttaqueDuHeros.calculerCoordonneesAbsolues(this.map.heros);
				final int xminHitbox = coord[0];
				final int xmaxHitbox = coord[1];
				final int yminHitbox = coord[2];
				final int ymaxHitbox = coord[3];
				final Graphics2D graphics = (Graphics2D) ecran.getGraphics();
				graphics.setPaint(Color.magenta);
				graphics.drawLine(xminHitbox - xCamera, yminHitbox - yCamera, xmaxHitbox - xCamera,
						yminHitbox - yCamera);
				graphics.drawLine(xminHitbox - xCamera, ymaxHitbox - yCamera, xmaxHitbox - xCamera,
						ymaxHitbox - yCamera);
				graphics.drawLine(xminHitbox - xCamera, yminHitbox - yCamera, xminHitbox - xCamera,
						ymaxHitbox - yCamera);
				graphics.drawLine(xmaxHitbox - xCamera, yminHitbox - yCamera, xmaxHitbox - xCamera,
						ymaxHitbox - yCamera);
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
		// en cas de stopEvent, seul l'Event qui a fig� tout le monde est lu (Commandes)
		if (stopEvent) {
			activerUnePageEtLExecuter(this.eventQuiALanceStopEvent);
		} else {
			// lire tous les Events de la Map (sauf le Heros)
			for (Event event : this.map.events) {
				if (!event.equals(this.map.heros)) { // le Heros est calcula en dernier
					activerUnePageEtLExecuter(event);
				}
			}
			// le Heros est calcula en dernier pour �viter les problemes d'�p�e
			if (!this.stopHeros) {
				activerUnePageEtLExecuter(this.map.heros);
			}

			// lire les PagesCommunes
			lireLesPagesCommunes();
		}
	}

	/**
	 * Activer une Page (si aucune n'est activ�e) de l'Event (s'il n'est pas
	 * supprime et l'executer.
	 * 
	 * @param event dont il faut activer une Page et l'executer
	 */
	private void activerUnePageEtLExecuter(final Event event) {
		if (!event.supprime && !event.saute) {
			if (event.pageActive == null || event.pageActive.commandes == null) {
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
		if (PageCommune.PAGES_COMMUNES.size() > 0) {
			for (PageCommune pageCommune : PageCommune.PAGES_COMMUNES.values()) {
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
	 * Calculer le nouvel ecran, avec les Events dessin�s dessus. Ne pas oublier de
	 * recuperer le r�sultat de cette Methode.
	 * 
	 * @param ecran            sur lequel on dessine les Events
	 * @param xCamera          position x de la cam�ra
	 * @param yCamera          position y de la cam�ra
	 * @param dessinerLeHeros  le Heros doit-il etre visible sur l'ancienne Map ?
	 * @param vignetteAutotile vignette d'animation actuelle de l'Autotile anim�
	 * @return ecran avec les Events dessin�s dessus
	 */
	private BufferedImage dessinerLesEvents(BufferedImage ecran, final int xCamera, final int yCamera,
			final boolean dessinerLeHeros, final int vignetteAutotile) {
		try {
			Collections.sort(this.map.events); // on trie les events du plus derri�re au plus devant

			// On dessine les Events plats d'abord
			for (Event event : this.map.events) {
				if (!event.supprime && event.platActuel) {
					if (dessinerLeHeros || !event.equals(map.heros)) {
						ecran = dessinerEvent(ecran, event, xCamera, yCamera);
					}
				}
			}

			// On dessine les Events normaux ensuite
			int bandeletteActuelle = 0;
			int bandeletteEvent = 0;
			for (Event event : this.map.events) {
				if (!event.supprime && !event.platActuel) {
					if (!event.auDessusDeToutActuel) {
						// dessiner la bandelette de decor m�dian
						bandeletteEvent = (event.y + event.hauteurHitbox - 1) / Main.TAILLE_D_UN_CARREAU;
						if (bandeletteEvent > bandeletteActuelle) {
							try {
								final BufferedImage imageBandelette = this.map.getImageCoucheDecorMedian(
										vignetteAutotile, bandeletteActuelle, bandeletteEvent);
								if (imageBandelette != null) {
									ecran = Graphismes.superposerImages(ecran, imageBandelette, -xCamera,
											bandeletteActuelle * Main.TAILLE_D_UN_CARREAU - yCamera);
								}
							} catch (RasterFormatException e) {
								LOG.warn("Bandelette de decor m�dian impossible a d�couper !");
							}
							bandeletteActuelle = bandeletteEvent;
						}
					}

					// dessiner l'Event
					if (dessinerLeHeros || !event.equals(map.heros)) {
						ecran = dessinerEvent(ecran, event, xCamera, yCamera);
					}
				}
			}
			// derni�re bandelette
			final BufferedImage imageBandelette = this.map.getImageCoucheDecorMedian(vignetteAutotile, bandeletteEvent,
					this.map.hauteur);
			ecran = Graphismes.superposerImages(ecran, imageBandelette, -xCamera,
					bandeletteActuelle * Main.TAILLE_D_UN_CARREAU - yCamera);

			// Les Events au dessus de tout seront dessin�s apres le decor superieur

		} catch (Exception e) {
			LOG.error("Erreur lors du dessin des evenements :", e);
		}
		return ecran;
	}

	/**
	 * Calculer le nouvel ecran, avec les Events dessin�s dessus. Ne pas oublier de
	 * recuperer le r�sultat de cette Methode.
	 * 
	 * @param ecran           sur lequel on dessine les Events
	 * @param xCamera         position x de la cam�ra
	 * @param yCamera         position y de la cam�ra
	 * @param dessinerLeHeros le Heros doit-il etre visible sur l'ancienne Map ?
	 * @return ecran avec les Events dessin�s dessus
	 */
	private BufferedImage dessinerLesEventsAuDessusDeTout(BufferedImage ecran, final int xCamera, final int yCamera,
			final boolean dessinerLeHeros) {
		try {
			for (Event event : this.map.events) {
				if (!event.supprime && event.auDessusDeToutActuel) {
					if (dessinerLeHeros || !event.equals(map.heros)) {
						ecran = dessinerEvent(ecran, event, xCamera, yCamera);
					}
				}
			}
		} catch (Exception e) {
			LOG.error("Erreur lors du dessin des evenements :", e);
		}
		return ecran;
	}

	/**
	 * Donne la bonne valeur a l'attribut "animation" avant d'envoyer l'event �
	 * l'affichage.
	 * 
	 * @param frame d'animation des Events
	 */
	private void animerLesEvents(final int frame) {
		try {
			for (Event event : this.map.events) {
				if (event.frequenceActuelle != null) {

					final boolean passerALAnimationSuivante = (frame % event.frequenceActuelle.valeur == 0) // fr�quence
																											// d'animation
							|| (event.avance && !event.avancaitALaFramePrecedente); // la premiere frame d'animation est
																					// un pas

					// cas Ou l'Event est anim� a l'arret
					if (!event.avance && event.animeALArretActuel && passerALAnimationSuivante) {
						event.animation = (event.animation + 1) % Event.NOMBRE_DE_VIGNETTES_PAR_IMAGE;
					}
					// cas Ou l'Event est vraiment en mouvement
					final boolean eventAvance = event.avance || event.avancaitALaFramePrecedente;
					// l'animation des Events est-elle figee ?
					final boolean animerLesEvents = !this.stopEvent
							|| (event.deplacementForce != null && event.deplacementForce.mouvements.size() > 0
									&& event.deplacementForce.idEventCommanditaire == this.eventQuiALanceStopEvent.id);
					// l'animation du Heros est-elle figee ?
					final boolean animerLeHeros = !this.stopHeros || !(event instanceof Heros); // si pas le Heros,
																								// alors pas concern�
					if (eventAvance && event.animeEnMouvementActuel && passerALAnimationSuivante && animerLesEvents
							&& animerLeHeros // si pas le Heros, alors pas concern�
					) {
						// on poursuit l'animation de l'Event
						event.animation = (event.animation + 1) % Event.NOMBRE_DE_VIGNETTES_PAR_IMAGE;
					}
					event.avancaitALaFramePrecedente = event.avance;

				} else {
					// Erreur : frequence nulle
					final String nomEvent = (event.nom == null ? "null" : event.nom);
					final int idEvent = (event.id == null ? -1 : event.id);
					LOG.error("L'event " + idEvent + " '" + nomEvent + "' a une fr�quence nulle !");
				}
			}
		} catch (Exception e) {
			LOG.error("erreur lors de l'animation des evenements dans la boucle d'affichage de la map :", e);
		}
	}

	/**
	 * Donne la bonne valeur aux positions x et y avant d'envoyer l'Event �
	 * l'affichage. En cas de stopEvent, seuls les Mouvements commandit�s par
	 * l'Event qui a fig� tout sont lus.
	 */
	private void deplacerLesEvents() {
		try {
			// animer la marche du Heros si touche de deplacement pressee
			if (GestionClavier.ToucheRole.HAUT.enfoncee() || GestionClavier.ToucheRole.GAUCHE.enfoncee()
					|| GestionClavier.ToucheRole.BAS.enfoncee() || GestionClavier.ToucheRole.DROITE.enfoncee()) {
				map.heros.avance = true;
			}

			// deplacer chaque Event
			for (Event event : this.map.events) {
				if (!event.supprime) {
					event.deplacer(); // on effectue le deplacement si possible (pas d'obstacles rencontres)
				}
			}
		} catch (Exception e) {
			LOG.error("Erreur lors du deplacement des evenements :", e);
		}
	}

	/**
	 * Certains Events ont ete marqu�s "� supprimer" durant la lecture des
	 * Commandes. On les �limine maintenant, une fois que la lecture des Commandes
	 * est terminee. En effet on ne peut pas supprimer des Events lorsqu'on est
	 * encore dans la boucle qui parcourt la liste des Events.
	 */
	private void supprimerLesEventsASupprimer() {
		int nombreDEvents = this.map.events.size();
		Event eventAsupprimer;
		for (int i = 0; i < nombreDEvents; i++) {
			eventAsupprimer = this.map.events.get(i);
			if (eventAsupprimer.supprime) {
				this.map.events.remove(i);
				LOG.info("Suppression de l'event " + eventAsupprimer.nom);
				LOG.debug("Nombre d'events sur la map : " + this.map.events.size());
				nombreDEvents--;
				i--;
			}
		}
	}

	/**
	 * Ajouter les nouveaux Events a ajouter a la Map pour le tour suivant.
	 */
	private void ajouterLesEventsAAjouter() {
		int nombreDEvents = this.map.eventsAAjouter.size();
		Event eventAajouter;
		for (int i = 0; i < nombreDEvents; i++) {
			eventAajouter = this.map.eventsAAjouter.get(i);

			// on l'ajoute au hash des Events avec un Numero
			if (eventAajouter.id < 0) {
				eventAajouter.id = this.map.calculerNouvelIdPourEventsHash();
			}
			this.map.eventsHash.put(eventAajouter.id, eventAajouter);

			// on l'ajoute a la liste des Events
			this.map.events.add(eventAajouter);
			eventAajouter.id = this.map.events.size();

			LOG.info("Ajout de l'event " + eventAajouter.nom);
			LOG.debug("Nombre d'events sur la map : " + this.map.events.size());
			this.map.eventsAAjouter.remove(i);
			nombreDEvents--;
			i--;
		}
	}

	/**
	 * Faire une capture d'ecran des collisions
	 */
	public final void photographierCollision() {
		final BufferedImage img = new BufferedImage(Fenetre.LARGEUR_ECRAN, Fenetre.HAUTEUR_ECRAN,
				Graphismes.TYPE_DES_IMAGES);
		final Graphics2D graphics = img.createGraphics();
		graphics.setPaint(Color.white);
		graphics.fillRect(0, 0, Fenetre.LARGEUR_ECRAN, Fenetre.HAUTEUR_ECRAN);

		img.setRGB(map.heros.x, map.heros.y, Color.red.getRGB());
		img.setRGB(map.events.get(1).x, map.events.get(1).y, Color.blue.getRGB());
		Graphismes.sauvegarderImage(img, "collision");

		// On n'aura plus jamais besoin de cette image
		graphics.dispose();
	}

	/**
	 * Dessine l'Event sur l'ecran.
	 * 
	 * @warning Ne pas oublier de recuperer le r�sultat de cette Methode.
	 * @param ecran   sur lequel on dessine
	 * @param event   a dessiner
	 * @param xCamera position x de la cam�ra
	 * @param yCamera position y de la cam�ra
	 * @return ecran sur lequel on a dessin� l'Event demand�
	 */
	private BufferedImage dessinerEvent(final BufferedImage ecran, final Event event, final int xCamera,
			final int yCamera) {
		final BufferedImage eventImage = event.imageActuelle;

		// DEBUG pour visualiser les collisions //TODO commenter
		/*
		 * Graphics2D graphics = ecran.createGraphics(); graphics.setPaint(Color.blue);
		 * graphics.fillRect(event.x-xCamera, event.y-yCamera, event.largeurHitbox,
		 * event.hauteurHitbox);
		 */
		// voil�

		if (eventImage != null) {
			int largeur = eventImage.getWidth();
			int hauteur = eventImage.getHeight();
			// l'apparence de l'event est une des 16 parties de l'image de l'event (suivant
			// la direction et l'animation)
			if (!event.apparenceActuelleEstUnTile) {
				largeur /= 4;
				hauteur /= 4;
			}
			final int positionX = event.xImage();
			final int positionY = event.yImage();

			final int direction = event.direction;
			final int animation;
			if (event.saute && !event.directionFixeActuelle) {
				// l'Event est en train de sauter
				animation = 0; // TODO attention : si la vignette normale de l'event n'est pas la vignette 0,
								// l'event va changer d'apparence

			} else {
				// l'Event ne Saute pas
				animation = event.animation;
			}

			final BufferedImage apparence;
			try {
				apparence = eventImage.getSubimage(animation * largeur, direction * hauteur, largeur, hauteur);
			} catch (RasterFormatException rfe) {
				LOG.error("La vignette d'Event est mal d�coup�e " + "(animation:" + animation + ";direction:"
						+ direction + ";" + "largeur:" + largeur + ";hauteur:" + hauteur + ")", rfe);
				return ecran;
			}
			return Graphismes.superposerImages(ecran, apparence, positionX - xCamera, positionY - yCamera,
					event.opaciteActuelle, event.modeDeFusionActuel);
		} else {
			// l'event n'a pas d'image
			return ecran;
		}
	}

	@Override
	public final void keyPressed(final ToucheRole touchePressee) {
		// action sp�cifique selon la touche
		switch (touchePressee) {
		case MENU:
			this.ouvrirLeMenu();
			break;
		case HAUT:
			this.haut();
			break;
		case GAUCHE:
			this.gauche();
			break;
		case BAS:
			this.bas();
			break;
		case DROITE:
			this.droite();
			break;
		case ARME_SUIVANTE:
			this.equiperArmeSuivante();
			break;
		case ACTION:
			this.action();
			break;
		case ARME_PRECEDENTE:
			this.equiperArmePrecedente();
			break;
		case ACTION_SECONDAIRE:
			// rien
			break;
		case CAPTURE_D_ECRAN:
			this.faireUneCaptureDEcran();
			break;
		case DEBUG:
			LOG.debug("Map : \"" + this.map.nom + "\" (" + this.map.largeur + "x" + this.map.hauteur + ")");
			LOG.debug("Events :");
			for (Event e : this.map.events) {
				LOG.debug("\t" + e.id + " : " + e.nom);
				if (e.deplacementForce != null && e.deplacementForce.mouvements != null
						&& e.deplacementForce.mouvements.size() > 0) {
					StringBuilder mouvementsForces = new StringBuilder();
					for (Mouvement m : e.deplacementForce.mouvements) {
						mouvementsForces.append(m.getClass().getSimpleName()).append(" ; ");
					}
					LOG.debug("\t\tDeplacement forc� : " + mouvementsForces.toString());
				}
				if (e.deplacementNaturelActuel != null && e.deplacementNaturelActuel.mouvements != null
						&& e.deplacementNaturelActuel.mouvements.size() > 0) {
					StringBuilder mouvementsNaturels = new StringBuilder();
					for (Mouvement m : e.deplacementNaturelActuel.mouvements) {
						mouvementsNaturels.append(m.getClass().getSimpleName()).append(" ; ");
					}
					LOG.debug("\t\tDeplacement naturel : " + mouvementsNaturels.toString());
				}
			}
			break;
		default:
			break; // touche inconnue
		}
	}

	/**
	 * Ouvre une autre Map (dans un nouveau LecteurMap).
	 * 
	 * @warning cette Methode ne doit etre appel�e que par le nouveau Lecteur !
	 * @param nouvelleMap sur laquelle le Heros voyage
	 */
	public final void devenirLeNouveauLecteurMap(final Map nouvelleMap) {
		Main.futurLecteur = this;
		Main.lecteur.allume = false;

		// On d�truit le Tileset actuel si le prochain n'est pas le meme
		if (this.map.tileset != null && !this.map.tileset.nom.equals(nouvelleMap.tileset.nom)) {
			this.map.tileset = null;
		}
	}

	/**
	 * Prendre une capture d'ecran de la Map sans le Heros ni les jauges.
	 * 
	 * @param afficherLeHeros le Heros doit-il etre visible sur l'ancienne Map ?
	 * @return capture de la Map
	 */
	private BufferedImage capturerLaMap(final boolean afficherLeHeros) {
		BufferedImage capture = dessinerPanorama(this.xCamera, this.yCamera);
		capture = dessinerDecorInferieur(capture, this.xCamera, this.yCamera, this.vignetteAutotileActuelle);
		capture = dessinerLesEvents(capture, this.xCamera, this.yCamera, afficherLeHeros,
				this.vignetteAutotileActuelle);
		capture = Animation.dessinerLesAnimations(capture, this.xCamera, this.yCamera);
		capture = dessinerDecorSuperieur(capture, this.xCamera, this.yCamera, this.vignetteAutotileActuelle);
		capture = dessinerMeteo(capture, this.frameActuelle);
		// brouillard
		if (this.map.brouillard != null) {
			capture = map.brouillard.dessinerLeBrouillard(capture, this.xCamera, this.yCamera, this.frameActuelle);
		}
		// ton
		if (this.tonActuel != null) {
			// capture = Graphismes.superposerImages(capture, capture, 0, 0,
			// Graphismes.OPACITE_MAXIMALE, ModeDeFusion.TON_DE_L_ECRAN);
			capture = Graphismes.appliquerTon(capture, this.tonActuel);
		}
		return capture;
	}

	/**
	 * Ouvrir le Menu du jeu. On quitte la Map temporairement (elle est m�moris�e)
	 * pour parcourir le Menu.
	 */
	public final void ouvrirLeMenu() {
		// impossible d'ouvrir le Menu en cas de stopEvent, stopHeros ou de Menu
		// interdit
		if (!this.stopEvent && !this.stopHeros && this.autoriserMenu) {
			final Commande menuPause = new OuvrirMenu("Statut", 0);
			menuPause.executer(0, null);
		}
	}

	@Override
	public final void keyReleased(final ToucheRole toucheRelachee) {
		remettreAZeroLAnimationDuHeros(); // s'il s'est arrete
	}

	/**
	 * Lorsque le Heros s'arrete de marcher, on arrete son animation.
	 */
	public final void remettreAZeroLAnimationDuHeros() {
		final Event heros = map.heros;
		if (!GestionClavier.ToucheRole.BAS.enfoncee() && !GestionClavier.ToucheRole.HAUT.enfoncee()
				&& !GestionClavier.ToucheRole.GAUCHE.enfoncee() && !GestionClavier.ToucheRole.DROITE.enfoncee()) {
			heros.avance = false;
			heros.animation = 0;
		}
	}

	/**
	 * Calculer la position x de la cam�ra
	 * 
	 * @return position x de la cam�ra
	 */
	private int calculerXCamera() {
		final int largeurMap = map.largeur;
		if (!this.map.defilementCameraX) {
			// map tres petite, d�filement inutile
			return this.tremblementDeTerre;
		} else {
			// grande map, d�filement possible

			// Si l'event saute, la cam�ra est decorr�l�e de la position du Heros
			final int xHeros;
			if (this.map.heros.saute && this.map.heros.deplacementForce != null
					&& this.map.heros.deplacementForce.mouvements != null
					&& this.map.heros.deplacementForce.mouvements.size() > 0
					&& this.map.heros.deplacementForce.mouvements.get(0) instanceof Sauter) {
				// Le Heros est en train de sauter
				final Sauter saut = (Sauter) this.map.heros.deplacementForce.mouvements.get(0);
				xHeros = saut.xPourCamera();
			} else {
				// Le Heros ne saute pas
				xHeros = map.heros.x;
			}
			final int nouveauXCamera = xHeros - Fenetre.LARGEUR_ECRAN / 2;

			if (nouveauXCamera < 0) { // cam�ra ne d�borde pas de la map a gauche
				return (this.defilementX > 0 ? this.defilementX : 0) + this.tremblementDeTerre;
			} else if (nouveauXCamera + Fenetre.LARGEUR_ECRAN > largeurMap * Main.TAILLE_D_UN_CARREAU) { // cam�ra ne
																											// d�borde
																											// pas de la
																											// map �
																											// droite
				return largeurMap * Main.TAILLE_D_UN_CARREAU - Fenetre.LARGEUR_ECRAN
						+ (this.defilementX < 0 ? this.defilementX : 0) + this.tremblementDeTerre;
			} else {
				return nouveauXCamera + this.defilementX + this.tremblementDeTerre;
			}
		}
	}

	/**
	 * Calculer la position y de la cam�ra
	 * 
	 * @return position y de la cam�ra
	 */
	private int calculerYCamera() {
		final int hauteurMap = map.hauteur;
		if (!this.map.defilementCameraY) {
			// map tres petite, d�filement inutile
			return 0;
		} else {
			// grande map, d�filement possible

			// Si l'event saute, la cam�ra est decorr�l�e de la position du Heros
			final int yHeros;
			if (this.map.heros.saute && this.map.heros.deplacementForce != null
					&& this.map.heros.deplacementForce.mouvements != null
					&& this.map.heros.deplacementForce.mouvements.size() > 0
					&& this.map.heros.deplacementForce.mouvements.get(0) instanceof Sauter) {
				// Le Heros est en train de sauter
				final Sauter saut = (Sauter) this.map.heros.deplacementForce.mouvements.get(0);
				yHeros = saut.yPourCamera();
			} else {
				// Le Heros ne saute pas
				yHeros = map.heros.y;
			}
			int nouveauYCamera = yHeros - Fenetre.HAUTEUR_ECRAN / 2;

			if (nouveauYCamera < 0) { // cam�ra ne d�borde pas de la map en haut
				return 0 + (this.defilementY > 0 ? this.defilementY : 0);
			} else if (nouveauYCamera + Fenetre.HAUTEUR_ECRAN > hauteurMap * Main.TAILLE_D_UN_CARREAU) { // cam�ra ne
																											// d�borde
																											// pas de la
																											// map en
																											// bas
				return hauteurMap * Main.TAILLE_D_UN_CARREAU - Fenetre.HAUTEUR_ECRAN
						+ (this.defilementY < 0 ? this.defilementY : 0);
			} else {
				return nouveauYCamera + this.defilementY;
			}
		}
	}

	/**
	 * Actualiser et recuperer la cam�ra de la Map.
	 * 
	 * @return Coordonnees x et y de la cam�ra sur la Map
	 */
	public int[] recupererCamera() {
		final int[] camera = new int[2];
		camera[0] = this.xCamera = calculerXCamera();
		camera[1] = this.yCamera = calculerYCamera();
		return camera;
	}

	/**
	 * Le Heros arrete son animation pour ecouter un Message.
	 * 
	 * @param event avec lequel le Heros discute
	 */
	public final void normaliserApparenceDesInterlocuteursAvantMessage(final Event event) {
		// Normaliser le Heros
		final Heros heros = this.map.heros;
		// Le Heros arrete son animation
		if (!heros.animeALArretActuel) {
			heros.animation = 0;
		}
		// Le Heros arrete son attaque
		heros.animationAttaque = 0;

		// Normaliser l'intelocuteur
		// L'interlocuteur arrete son animation
		if (!event.animeALArretActuel && !event.directionFixeActuelle) {
			event.animation = 0; // TODO attention : si la vignette par defaut de l'event n'est pas la vignette
									// 0, il va changer d'apparence
		}
		// L'interlocuteur se tourne vers le Heros
		if (!event.directionFixeActuelle) {
			event.direction = RegarderUnEvent.calculerDirectionDeRegard(event, heros);
		}
	}

	/**
	 * Deplacer le Heros vers le haut
	 */
	public final void haut() {
		if (this.messageActuel != null) {
			// les touches directionnelles servent au Message/Choix/EntrerUnNombre
			this.messageActuel.haut();
		} else if (!this.stopEvent && !this.stopHeros) {
			// les touches directionnelles servent a faire avancer le Heros
			// this.map.heros.mettreDansLaBonneDirection();
			this.map.heros.avance = true;
		}
	}

	/**
	 * Deplacer le Heros vers la gauche
	 */
	public final void gauche() {
		if (this.messageActuel != null) {
			// les touches servent au Message/Choix/EntrerUnNombre
			this.messageActuel.gauche();
		} else if (!this.stopEvent && !this.stopHeros) {
			// les touches directionnelles servent a faire avancer le Heros
			// this.map.heros.mettreDansLaBonneDirection();
			this.map.heros.avance = true;
		}
	}

	/**
	 * Deplacer le Heros vers le bas
	 */
	public final void bas() {
		if (this.messageActuel != null) {
			// les touches servent au Message/Choix/EntrerUnNombre
			this.messageActuel.bas();
		} else if (!this.stopEvent && !this.stopHeros) {
			// les touches directionnelles servent a faire avancer le Heros
			// this.map.heros.mettreDansLaBonneDirection();
			this.map.heros.avance = true;
		}
	}

	/**
	 * Deplacer le Heros vers la droite
	 */
	public final void droite() {
		if (this.messageActuel != null) {
			// les touches servent au Message/Choix/EntrerUnNombre
			this.messageActuel.droite();
		} else if (!this.stopEvent && !this.stopHeros) {
			// les touches directionnelles servent a faire avancer le Heros
			// this.map.heros.mettreDansLaBonneDirection();
			this.map.heros.avance = true;
		}
	}

	/**
	 * Attaquer ou parler (suivant si gentil ou m�chant)
	 */
	public final void action() {
		if (this.messageActuel != null) {
			// les touches servent au Message/Choix/EntrerUnNombre
			this.messageActuel.action();
		}
	}

	/**
	 * Transmettre a la Partie le changement d'Arme ordonne a la Fenetre
	 */
	public final void equiperArmeSuivante() {
		if (!this.stopEvent) { // on ne change pas d'Arme lorsqu'on lit un Message
			Main.getPartieActuelle().equiperArmeSuivante();
		}
	}

	/**
	 * Transmettre a la Partie le changement d'Arme ordonne a la Fenetre
	 */
	public final void equiperArmePrecedente() {
		if (!this.stopEvent) { // on ne change pas d'Arme lorsqu'on lit un Message
			Main.getPartieActuelle().equiperArmePrecedente();
		}
	}

	@Override
	protected final String typeDeLecteur() {
		return "LecteurMap";
	}

	@Override
	public final void lireMusique() {
		final Map map = this.map;
		if (map.volumeBGM == null) {
			LOG.warn("volumeBGM est null");
		}
		if (map.volumeBGS == null) {
			LOG.warn("volumeBGS est null");
		}
		if (map.nomBGM != null && !map.nomBGM.isEmpty()) {
			LecteurAudio.playBgm(map.nomBGM, map.volumeBGM, 0);
		}
		if (map.nomBGS != null && !map.nomBGS.isEmpty()) {
			LecteurAudio.playBgs(map.nomBGS, map.volumeBGS, 0);
		}
	}

	/**
	 * La Transition a partir de la Map pr�c�dente est-elle terminee ?
	 * 
	 * @return true si fini
	 */
	public final boolean laTransitionEstTerminee() {
		return Transition.AUCUNE.equals(this.transition) || this.frameActuelle >= Transition.DUREE_TRANSITION;
	}

}
