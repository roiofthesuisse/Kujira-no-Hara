package map;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import commandes.Deplacement;
import conditions.Condition;
import main.Commande;
import main.Lecteur;
import main.Main;
import map.PageEvent.Traversabilite;
import mouvements.Mouvement;
import utilitaire.InterpreteurDeJson;
import utilitaire.Maths;
import utilitaire.graphismes.Graphismes;
import utilitaire.graphismes.ModeDeFusion;

/**
 * Un Event est un élément actif du décor, voire interactif.
 * Ses éventuels comportements sont représentés par une liste de Pages.
 */
public class Event implements Comparable<Event> {
	//constantes
	private static final Logger LOG = LogManager.getLogger(Event.class);
	public static final int VITESSE_PAR_DEFAUT = 4;
	public static final int FREQUENCE_PAR_DEFAUT = 4;
	public static final int DIRECTION_PAR_DEFAUT = Event.Direction.BAS;
	public static final int LARGEUR_HITBOX_PAR_DEFAUT = Main.TAILLE_D_UN_CARREAU;
	public static final int HAUTEUR_HITBOX_PAR_DEFAUT = Main.TAILLE_D_UN_CARREAU;
	public static final int NOMBRE_DE_VIGNETTES_PAR_IMAGE = 4;
	public static final boolean ANIME_A_L_ARRET_PAR_DEFAUT = false;
	public static final boolean ANIME_EN_MOUVEMENT_PAR_DEFAUT = true;
	public static final Traversabilite TRAVERSABLE_PAR_DEFAUT = Traversabilite.OBSTACLE; //si une Page est active
	public static final Traversabilite TRAVERSABLE_SI_VIDE = Traversabilite.TRAVERSABLE; //si aucune Page active
	public static final boolean DIRECTION_FIXE_PAR_DEFAUT = false;
	public static final boolean AU_DESSUS_DE_TOUT_PAR_DEFAUT = false;
	public static final boolean REPETER_LE_DEPLACEMENT_PAR_DEFAUT = false;
	public static final boolean IGNORER_LES_MOUVEMENTS_IMPOSSIBLES_PAR_DEFAUT = true;
	public static final boolean ATTENDRE_LA_FIN_DU_DEPLACEMENT_PAR_DEFAUT = true;
	public static final boolean PLAT_PAR_DEFAUT = false;
	public static final boolean PLAT_SI_VIDE = true;
	public static final String MARQUEUR_DE_REINITIALISATION = "RESET";
	
	/** Map à laquelle cet Event appartient */
	public Map map;
	/** nom de l'Event */
	public String nom;
	/** identifiant de l'Event pour les Mouvements, tels qu'écrits dans les fichiers JSON */
	public Integer id;
	/** distance (en pixels) entre le bord gauche de l'écran et le corps de l'Event */
	public int x;
	/** distance (en pixels) entre le bord haut de l'écran et le corps de l'Event */
	public int y;
	/** Faut-il réinitialiser les interrupteurs locaux de cet event à chaque changement de Map ? */
	public final boolean reinitialiser;
	
	/** de combien de pixels avance l'Event à chaque pas ? */
	public int vitesseActuelle = VITESSE_PAR_DEFAUT; //1:trèsLent 2:lent 4:normal 8:rapide 16:trèsrapide 32:trèstrèsRapide
	/** toutes les combien de frames l'Event change-t-il d'animation ? */
	public int frequenceActuelle = FREQUENCE_PAR_DEFAUT; //1:trèsAgité 2:agité 4:normal 8:mou 16:trèsMou
	
	/** un Event peut être déplacé par une Commande Event externe à son déplacement naturel nominal */
	public Deplacement deplacementForce;
	
	public BufferedImage imageActuelle = null;
	public boolean apparenceActuelleEstUnTile;
	public int opaciteActuelle = Graphismes.OPACITE_MAXIMALE;
	public ModeDeFusion modeDeFusionActuel = ModeDeFusion.NORMAL;
	/** par défaut, si l'image est plus petite que 32px, l'Event est considéré comme plat (au sol) */
	public boolean platActuel = PLAT_PAR_DEFAUT;
	public int direction;
	public int animation;
	
	/** l'Event est-il en train d'avancer en ce moment même ? (utile pour l'animation) */
	public boolean avance = false;
	/** L'Event avançait-il à la frame précédente ? (utile pour l'animation) */
	public boolean avancaitALaFramePrecedente = false;
	/** l'Event est-il en train de sauter en ce moment même ? (utile pour l'animation) */
	public boolean saute = false;
	public int coordonneeApparenteXLorsDuSaut; //en pixels
	public int coordonneeApparenteYLorsDuSaut; //en pixels
	
	/**  L'Event est-il au contact du Héros ? (utile pour la Condition ArriveeAuContact */
	public boolean estAuContactDuHerosMaintenant = false;
	public boolean estAuContactDuHerosAvant = false;
	public int frameDuContact;
	
	/** 
	 * Ces paramètres sont remplis automatiquement au chargement de la page.
	 */
	public boolean animeALArretActuel = ANIME_A_L_ARRET_PAR_DEFAUT;
	public boolean animeEnMouvementActuel = ANIME_EN_MOUVEMENT_PAR_DEFAUT;
	public Traversabilite traversableActuel = TRAVERSABLE_PAR_DEFAUT;
	public boolean directionFixeActuelle = DIRECTION_FIXE_PAR_DEFAUT;
	public boolean auDessusDeToutActuel = AU_DESSUS_DE_TOUT_PAR_DEFAUT;
	public Deplacement deplacementNaturelActuel;
	
	public int largeurHitbox = LARGEUR_HITBOX_PAR_DEFAUT;
	public int hauteurHitbox = HAUTEUR_HITBOX_PAR_DEFAUT;
	
	/**
	 * Décale l'affichage vers le bas.
	 * En effet, décaler l'affichage dans les trois autres directions est possible en modifiant l'image.
	 */
	int offsetY = 0; 
	public ArrayList<PageEvent> pages;
	/** Page dont on va lire les Commandes Event car toutes ses Conditions sont remplies */
	public PageEvent pageActive = null;
	/** Page dont on utilise l'apparence car les Conditions non liées au contact sont remplies */
	public PageEvent pageDApparence = null;
	public int numeroDeLaDernierePageDApparence = -1;
	public boolean ilYAEuChangementDePageDApparence = true;
	
	/**
	 * Lorsque ce marqueur est à true, on considère l'event comme supprimé.
	 * Ce n'est qu'un simple marqueur : l'event n'est réellement supprimé qu'après la boucle for sur les events.
	 */
	public boolean supprime = false;
	/** Flag pour n'afficher qu'une seule fois le message d'erreur comme quoi l'Event s'est fait la malle */
	public boolean sortiDeLaMap = false;
	
	/**
	 * Tout Event regarde dans une Direction.
	 */
	public static class Direction {
		public static final int BAS = 0;
		public static final int GAUCHE = 1;
		public static final int DROITE = 2;
		public static final int HAUT = 3;
		
		/**
		 * Calcule la direction opposée.
		 * @param dir direction à inverser
		 * @return direction opposée
		 */
		public static final int directionOpposee(final int dir) {
			switch (dir) {
				case Direction.BAS:
					return Direction.HAUT;
				case Direction.HAUT:
					return Direction.BAS;
				case Direction.GAUCHE:
					return Direction.DROITE;
				case Direction.DROITE:
					return Direction.GAUCHE;
				default:
					return -1;
			}
		}
	}
	
	/**
	 * Constructeur explicite de l'Event
	 * @param x position x (en pixels) où se trouve l'Event, en abscisse, de gauche à droite
	 * @param y position x (en pixels) numero du carreau où se trouve l'Event, en ordonnée, de haut en bas
	 * @param offsetY si on veut afficher l'Event plus bas que sa case réelle
	 * @param nom de l'Event
	 * @param id identifiant numérique de l'Event
	 * @param reinitialiserLesInterupteursLocaux à chaque changement de Map ?
	 * @param pages ensemble de Pages décrivant le comportement de l'Event
	 * @param largeurHitbox largeur de la boîte de collision
	 * @param hauteurHitbox hauteur de la boîte de collision
	 * @param map de l'Event
	 */
	public Event(final Integer x, final Integer y, final int offsetY, final String nom, final Integer id, 
			final boolean reinitialiserLesInterupteursLocaux, final ArrayList<PageEvent> pages, 
			final int largeurHitbox, final int hauteurHitbox, final Map map) {
		this.x = x;
		this.y = y;
		this.offsetY = offsetY;
		this.id = id;
		this.nom = nom;
		this.reinitialiser = reinitialiserLesInterupteursLocaux;
		this.pages = pages;
		this.map = map;
		this.largeurHitbox = largeurHitbox;
		this.hauteurHitbox = hauteurHitbox;
		this.deplacementForce = new Deplacement(null, new ArrayList<Mouvement>(), true, false, false);
		initialiserLesPages();
		if (pages!=null && pages.size()>=1) {
			attribuerLesProprietesActuelles(pages.get(0)); //par défaut, propriétés de la première page
		}
	}
	
	/**
	 * Constructeur de l'Event utilisant un tableau de pages JSON
	 * @param x coordonnée x (en pixels) où se trouve l'Event, en abscisse, de gauche à droite
	 * @param y coordonnée y (en pixels) où se trouve l'Event, en ordonnée, de haut en bas
	 * @param offsetY si on veut afficher l'Event plus bas que sa case réelle
	 * @param nom de l'Event
	 * @param id identifiant numérique de l'Event
	 * @param reinitialiserLesInterupteursLocaux à chaque changement de Map ?
	 * @param tableauDesPages tableau JSON contenant les Pages de comportement
	 * @param largeurHitbox largeur de la boîte de collision
	 * @param hauteurHitbox hauteur de la boîte de collision
	 * @param map de l'Event
	 */
	public Event(final Integer x, final Integer y, final int offsetY, final String nom, final Integer id, 
			final boolean reinitialiserLesInterupteursLocaux, final JSONArray tableauDesPages, 
			final int largeurHitbox, final int hauteurHitbox, final Map map) {
		this(x, y, offsetY, nom, id, reinitialiserLesInterupteursLocaux, creerListeDesPagesViaJson(tableauDesPages, id, map), largeurHitbox, hauteurHitbox, map);
	}

	/**
	 * Prend le tableau JSON des pages et crée la liste des Pages avec.
	 * @param idEvent identifiant de l'Event
	 * @param tableauDesPages au format JSON
	 * @param map de l'event
	 * @return liste des Pages de l'Event
	 */
	protected static ArrayList<PageEvent> creerListeDesPagesViaJson(final JSONArray tableauDesPages, final Integer idEvent, final Map map) {
		final ArrayList<PageEvent> listeDesPages = new ArrayList<PageEvent>();
		int i = 0;
		for (Object pageJSON : tableauDesPages) {
			listeDesPages.add( new PageEvent(i, (JSONObject) pageJSON, idEvent, map) );
			i++;
		}
		return listeDesPages;
	}

	/**
	 * On relie les Conditions et les Commandes à leur Page.
	 */
	protected final void initialiserLesPages() {
		int numeroCondition = 0;
		try {
			for (PageEvent page : this.pages) {
				page.event = this;
				if (page!=null) {
					//numérotation des conditions et on apprend aux conditions qui est leur page
					try {
						for (Condition cond : page.conditions) {
							cond.numero = numeroCondition;
							cond.page = page;
							numeroCondition++;
						}
					} catch (NullPointerException e1) {
						//pas de conditions pour déclencher cette page
						LOG.warn("La page "+page.numero+" de l'event "+this.nom+" ("+this.id+") n'a pas besoin de conditions pour se déclencher.");
					}
					//on apprend aux commandes qui est leur page
					try {
						for (Commande comm : page.commandes) {
							comm.page = page;
						}
					} catch (NullPointerException e2) {
						//pas de commandes dans cette page
						LOG.warn("La page "+page.numero+" de l'event "+this.nom+" ("+this.id+") n'a pas de commandes.");
					}
				}
			}
		} catch (NullPointerException e3) {
			//pas de pages dans cet event
			LOG.error("L'envent "+this.nom+" ("+this.id+") n'a pas de pages.");
		}
	}

	/**
	 * Faire faire un Mouvement à l'Event.
	 * Ce Mouvement est soit issu du Déplacement naturel de l'Event, soit de son éventuel Déplacement forcé.
	 * @Warning Cette méthode est overridée pour le Héros.
	 */
	public void deplacer() {
		if (this.deplacementForce!=null && this.deplacementForce.mouvements.size()>0) {
			//il y a un Déplacement forcé
			this.deplacementForce.executerLePremierMouvement();
			
		} else {
			//pas de Déplacement forcé : on execute le Déplacement naturel
			if (deplacementNaturelActuel!=null && this.deplacementNaturelActuel.mouvements.size()>0) {
				//il y a un Déplacement naturel
				this.deplacementNaturelActuel.executerLePremierMouvement();
			} else {
				//pas de Déplacement du tout
				this.avance = false;
				this.saute = false;
				if (!this.animeALArretActuel && !this.avancaitALaFramePrecedente && !this.avance) {
					//l'event ne bouge plus depuis 2 frames, on arrête son animation
					this.animation = 0;
				}
			}
		}
	}

	@Override
	/**
	 * Permet de dire si un Event est devant ou derrière un autre en terme d'affichage.
	 */
	public final int compareTo(final Event e) {
		final int eEstDevant = -1;
		final int thisEstDevant = 1;
		if (this.auDessusDeToutActuel) {
			if (e.auDessusDeToutActuel) {
				final int thisY = this.y + this.hauteurHitbox;
				final int eY = e.y + e.hauteurHitbox;
				//les deux sont au dessus de tout, on applique la logique inversée
				if (thisY > eY) {
					return eEstDevant;
				}
				if (thisY < eY) {
					return thisEstDevant;
				}
			} else {
				return thisEstDevant;
			}
		} else {
			if (e.auDessusDeToutActuel) {
				return eEstDevant;
			} else {
				//y'en a-t-il un au sol ?
				if (this.platActuel && !e.platActuel) {
					return eEstDevant;
				} else if (e.platActuel && !this.platActuel) {
					return thisEstDevant;
				}
				
				final int thisY = this.y + this.hauteurHitbox;
				final int eY = e.y + e.hauteurHitbox;
				//aucun n'est au dessus de tout, on applique la logique normale
				if (thisY > eY) {
					return thisEstDevant;
				}
				if (thisY < eY) {
					return eEstDevant;
				}
			}
		}
		return 0;
	}

	/**
	 * Active la Page de l'Event qui vérifie toutes les Conditions de déclenchement.
	 * S'il y a plusieurs Pages valides, on prend la dernière.
	 * Les Commandes Event de la Page active choisie seront executées.
	 * L'apparence de la Page d'apparence choisie sera utilisée.
	 */
	public final void activerUnePage() {
		PageEvent pageQuOnChoisitEnRemplacement = null;
		boolean onATrouveLaPageDApparence = false;
		boolean onATrouveLaPageActive = true;
		try {
			for (int i = pages.size()-1; i>=0 && !onATrouveLaPageDApparence; i--) {
				final PageEvent page = pages.get(i);
				
				//par défaut une Page est valide, elle sera invalidée si une Condition est fausse
				onATrouveLaPageActive = true;
				boolean cettePageConvientPourLApparence = true;
				
				if (page.conditions!=null && page.conditions.size()>0) {
					//la Page a des Conditions de déclenchement, on les analyse

					//si une Condition est fausse, la Page ne convient pas
					for (int j = 0; j<page.conditions.size() && cettePageConvientPourLApparence; j++) {
						final Condition cond = page.conditions.get(j);
						if (!cond.estVerifiee()) {
							//la Condition n'est pas vérifiée
							onATrouveLaPageActive = false;
							if (!cond.estLieeAuHeros()) {
								cettePageConvientPourLApparence = false;
							}
						}
					}
				} else {
					//aucune condition nécessaire pour cette Page, donc la Page est choisie
					onATrouveLaPageActive = true;
					cettePageConvientPourLApparence = true;
				}
				
				//si une Page eligible a été trouvée, pas besoin d'essayer les autres Pages
				if (cettePageConvientPourLApparence) {
					pageQuOnChoisitEnRemplacement = page;
					onATrouveLaPageDApparence = true;
				}
			}
		} catch (NullPointerException e2) {
			//pas de Pages pour cet Event
			LOG.warn("L'event "+this.id+" ("+this.nom+") n'a pas de pages.");
			e2.printStackTrace();
		}
		if (!onATrouveLaPageDApparence) {
			//aucune Page ne convient, l'Event n'est pas affiché
			this.pageActive = null;
			this.pageDApparence = null;
			viderLesProprietesActuelles();		
			return;
		} else {
			//une Page correspond au moins pour les Conditions non liées au Héros, on donne son apparence à l'Event
			this.pageActive = null;
			this.pageDApparence = pageQuOnChoisitEnRemplacement;
			
			//il est important de noter un véritable changement de Page (non vide)
			//en effet cela implique de réinitialiser la direction initiale de l'Event
			if (this.pageDApparence.numero != this.numeroDeLaDernierePageDApparence) {
				this.ilYAEuChangementDePageDApparence = true;
				this.numeroDeLaDernierePageDApparence = this.pageDApparence.numero;
			} else {
				this.ilYAEuChangementDePageDApparence = false;
			}
			attribuerLesProprietesActuelles(this.pageDApparence);
			
			if (onATrouveLaPageActive) {
				//même les Conditions liées au Héros correspondent, on execute la Page
				this.pageActive = pageQuOnChoisitEnRemplacement;
			}
		}
	}

	/**
	 * On assigne les propriétés actuelles en utilisant celles d'une Page donnée.
	 * @param page dont on récupère les propriétés pour les donner à l'Event
	 */
	private void attribuerLesProprietesActuelles(final PageEvent page) {
		//apparence
		this.imageActuelle = page.image;
		this.apparenceActuelleEstUnTile = page.apparenceEstUnTile;
		
		if (!(this instanceof Heros) //le Héros n'est pas redirigé aux changements de Page
		&& ilYAEuChangementDePageDApparence) { //on ne réinitialise pas les propriétés sans vrai changement de Page 
			this.direction = page.directionInitiale;
		
			//propriétés
			this.vitesseActuelle = page.vitesse;
			this.frequenceActuelle = page.frequence;
			this.animeALArretActuel = page.animeALArret;
			this.auDessusDeToutActuel = page.auDessusDeTout;
			this.animeEnMouvementActuel = page.animeEnMouvement;
			this.traversableActuel = page.traversable;
			this.directionFixeActuelle = page.directionFixe;
			this.platActuel = page.plat;
			this.opaciteActuelle = page.opacite;
			this.modeDeFusionActuel = page.modeDeFusion;
		}
		//déplacement
		this.deplacementNaturelActuel = page.deplacementNaturel;
	}
	
	/**
	 * Faire disparaître l'Event à l'écran.
	 */
	private void viderLesProprietesActuelles() {
		//apparence
		this.imageActuelle = null;
		this.apparenceActuelleEstUnTile = false;
		
		if (!(this instanceof Heros) //le Héros n'est pas redirigé aux changements de Page
		&& ilYAEuChangementDePageDApparence ) { //on ne réinitialise pas les propriétés sans vrai changement de Page 
			//propriétés
			this.vitesseActuelle = Event.VITESSE_PAR_DEFAUT;
			this.frequenceActuelle = Event.FREQUENCE_PAR_DEFAUT;
			this.animeALArretActuel = Event.ANIME_A_L_ARRET_PAR_DEFAUT;
			this.auDessusDeToutActuel = Event.AU_DESSUS_DE_TOUT_PAR_DEFAUT;
			this.animeEnMouvementActuel = Event.ANIME_EN_MOUVEMENT_PAR_DEFAUT;
			this.traversableActuel = Event.TRAVERSABLE_SI_VIDE;
			this.directionFixeActuelle = Event.DIRECTION_FIXE_PAR_DEFAUT;
			this.platActuel = Event.PLAT_SI_VIDE;
			this.opaciteActuelle = Graphismes.OPACITE_MAXIMALE;
			this.modeDeFusionActuel = ModeDeFusion.NORMAL;
		}
		//déplacement
		this.deplacementNaturelActuel = null;
	}
	
	/**
	 * Où afficher l'Event ?
	 * @return position x de l'image de l'Event
	 */
	public final int xImage() {
		final int xBase;
		if (this.saute) {
			xBase = this.coordonneeApparenteXLorsDuSaut;
		} else {
			xBase = this.x;
		}
		final int largeurVignette;
		if (this.apparenceActuelleEstUnTile) {
			largeurVignette = this.imageActuelle.getWidth();
		} else {
			largeurVignette = this.imageActuelle.getWidth()/4;
		}
		return xBase + (this.largeurHitbox - largeurVignette)/2;
	}
	
	/**
	 * Où afficher l'Event ?
	 * @return position y de l'image de l'Event
	 */
	public final int yImage() {
		final int yBase;
		if (this.saute) {
			yBase = this.coordonneeApparenteYLorsDuSaut;
		} else {
			yBase = this.y;
		}
		final int hauteurVignette;
		if (this.apparenceActuelleEstUnTile) {
			hauteurVignette = this.imageActuelle.getHeight();
		} else {
			hauteurVignette = this.imageActuelle.getHeight()/4;
		}
		return yBase + this.hauteurHitbox - hauteurVignette + this.offsetY;
	}
	
	/**
	 * Traduit les Events depuis le format JSON et les range dans la liste des Events de la Map.
	 * @param events liste des Events de la Map
	 * @param eventsJSON tableau JSON contenant les Events au format JSON
	 * @param map des Events
	 */
	public static void recupererLesEvents(final ArrayList<Event> events, final JSONArray eventsJSON, final Map map) {
		// On effectue l'importation des Events en multithread : chaque Event a son thread.
		final ExecutorService executor = Executors.newFixedThreadPool(Main.NOMBRE_DE_THREADS);
		final Vector<Event> eventsVector = new Vector<Event>(); //le Vector est synchronisé, contrairement à l'ArrayList
		ThreadImporterLesEvents.initialiserParametreGlobaux(eventsVector, map);
		for (Object ev : eventsJSON) {
			executor.submit(new ThreadImporterLesEvents((JSONObject) ev)); //chaque thread modifie une ligne du dstOut
		}
		executor.shutdown();
		// On attend la fin de l'execution pour toutes les events JSON
		try {
			while (!executor.awaitTermination(Lecteur.DUREE_FRAME, TimeUnit.MILLISECONDS)) {
				LOG.warn("L'import des Events n'est pas encore terminé...");
			}
		} catch (InterruptedException e) {
			LOG.error("Impossible d'attendre la fin de l'importation des Events !", e);
		}
		
		// On transvase les Events du vector vers l'ArrayList
		for (Event event : eventsVector) {
			LOG.debug("Event importé : "+event.id+" ("+event.nom+")");
			events.add(event);
		}
	}
	
	/**
	 * Multithread pour importer les Events de la Map rapidement.
	 */
	protected static class ThreadImporterLesEvents implements Runnable {
		static Vector<Event> events;
		static Map map;
		
		final JSONObject jsonEvent;
		
		/**
		 * Ces paramètres sont communs à tou
		 * @param events liste pour récupérer les Events importés
		 * @param map de ces Events
		 */
		public static void initialiserParametreGlobaux(final Vector<Event> events, final Map map) {
			ThreadImporterLesEvents.events = events;
			ThreadImporterLesEvents.map = map;
		}
		
		/**
		 * Initialisation du thread
		 * @param ev objet JSON contenant les infos de l'Event à importer
		 */
		public ThreadImporterLesEvents(final JSONObject ev) {
			 this.jsonEvent = ev;
		}
		
		@Override
		public final void run() {
			final String nomEvent = jsonEvent.getString("nom");
			final Integer id = jsonEvent.getInt("id");
			try {
				//récupération des données dans le JSON
				
				int xEvent, yEvent; 
				try {
					// coordonnées entières
					xEvent = ((int) jsonEvent.get("x")) * Main.TAILLE_D_UN_CARREAU;
					yEvent = ((int) jsonEvent.get("y")) * Main.TAILLE_D_UN_CARREAU;
				} catch (ClassCastException e) {
					// coordonnées non entières
					xEvent = (int) (jsonEvent.getDouble("x") * Main.TAILLE_D_UN_CARREAU);
					yEvent = (int) (jsonEvent.getDouble("y") * Main.TAILLE_D_UN_CARREAU);
				}
				//instanciation de l'event
				Event event;
	
				//on essaye de le créer à partir de la bibliothèque JSON GenericEvents
				event = Event.creerEventGenerique(id, nomEvent, xEvent, yEvent, map);
				
				//si l'Event n'est pas générique, on le construit à partir de sa description dans la page JSON
				if (event == null) {
					final int largeurHitbox;
					if (jsonEvent.has("largeur")) {
						largeurHitbox = jsonEvent.getInt("largeur");
					} else {
						largeurHitbox = Event.LARGEUR_HITBOX_PAR_DEFAUT;
					}
					final int hauteurHitbox;
					if (jsonEvent.has("hauteur")) {
						hauteurHitbox = jsonEvent.getInt("hauteur");
					} else {
						hauteurHitbox = Event.HAUTEUR_HITBOX_PAR_DEFAUT;
					}
					final int offsetY;
					if (jsonEvent.has("offsetY")) {
						offsetY = jsonEvent.getInt("offsetY");
					} else {
						offsetY = 0;
					}
					final boolean reinitialiser = jsonEvent.has("reinitialiser") ? jsonEvent.getBoolean("reinitialiser") : nomEvent.contains(MARQUEUR_DE_REINITIALISATION);
	
					final JSONArray jsonPages = jsonEvent.getJSONArray("pages");
					event = new Event(xEvent, yEvent, offsetY, nomEvent, id, reinitialiser, jsonPages, largeurHitbox, hauteurHitbox, map);
				}
				events.add(event);
			} catch (Exception e) {
				LOG.error("Impossible d'importer l'event "+id+" ("+nomEvent+") !", e);
			}
		}
	}
	
	/**
	 * Calculer le terrain de l'Event.
	 * @return identifiant du terrain
	 */
	public final int calculerTerrain() {
		final int xEvent = (this.x + this.largeurHitbox/2) / Main.TAILLE_D_UN_CARREAU;
		final int yEvent = (this.y + this.hauteurHitbox/2) / Main.TAILLE_D_UN_CARREAU;
		final int carreauEvent = this.map.layer0[xEvent][yEvent];
		return this.map.tileset.terrainDeLaCase(carreauEvent);
	}
	
	/**
	 * Créer un Event générique à partir de sa description JSON.
	 * @param id de l'Event à créer
	 * @param nomFichier nom du fichier JSON de l'Event générique
	 * @param xEvent (en pixels) position x de l'Event
	 * @param yEvent (en pixels) position y de l'Event
	 * @param map de l'Event
	 * @return un Event créé
	 */
	public static Event creerEventGenerique(final int id, final String nomFichier, final int xEvent, 
			final int yEvent, final Map map) {
		final JSONObject jsonEventGenerique;
		try {
			jsonEventGenerique = InterpreteurDeJson.ouvrirJsonEventGenerique(nomFichier);
		} catch (Exception e1) {
			LOG.trace("Impossible de trouver le fichier JSON "+nomFichier+" pour contruire l'Event générique !", e1);
			return null;
		}
		final String nomEvent;
		if (jsonEventGenerique.has("nom")) {
			nomEvent = jsonEventGenerique.getString("nom");
		} else {
			nomEvent = nomFichier;
		}
		
		int largeurHitbox;
		try {
			largeurHitbox = jsonEventGenerique.getInt("largeur");
		} catch (JSONException e2) {
			largeurHitbox = Event.LARGEUR_HITBOX_PAR_DEFAUT;
		}
		int hauteurHitbox;
		try {
			hauteurHitbox = jsonEventGenerique.getInt("hauteur");
		} catch (JSONException e2) {
			hauteurHitbox = Event.HAUTEUR_HITBOX_PAR_DEFAUT;
		}
		final int offsetY;
		if (jsonEventGenerique.has("offsetY")) {
			offsetY = jsonEventGenerique.getInt("offsetY");
		} else {
			offsetY = 0;
		}
		final boolean reinitialiser = jsonEventGenerique.has("reinitialiser") ? jsonEventGenerique.getBoolean("reinitialiser") : nomEvent.contains(MARQUEUR_DE_REINITIALISATION);

		final JSONArray jsonPages = jsonEventGenerique.getJSONArray("pages");
		return new Event(xEvent, yEvent, offsetY, nomEvent, id, reinitialiser, jsonPages, largeurHitbox, hauteurHitbox, map);
	}
	
	/**
	 * Vérifier l'égalité entre deux Events.
	 */
	@Override
	public final boolean equals(final Object o) {
		if (o == null) {
			return false;
		}
		if (!(o instanceof Event)) {
			return false;
		}
		final Event event2 = (Event) o;
		if (this.hashCode() != event2.hashCode()) {
			return false;
		}
		if (this.id != event2.id) {
			return false;
		}
		if (!this.nom.equals(event2.nom)) {
			return false;
		}
		return true;
	}
	
	/**
	 * Hashcode pour rendre plus rapide la méthode equals().
	 */
	@Override
	public final int hashCode() {
		return Maths.NOMBRE_PREMIER1 + Maths.NOMBRE_PREMIER2 * this.id;
	}
	
}