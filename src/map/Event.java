package map;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import mouvements.Mouvement;
import mouvements.Sauter;
import utilitaire.InterpreteurDeJson;
import utilitaire.Maths;
import utilitaire.graphismes.Graphismes;
import utilitaire.graphismes.ModeDeFusion;

/**
 * Un Event est un �l�ment actif du d�cor, voire interactif. Ses �ventuels
 * comportements sont repr�sent�s par une liste de Pages.
 */
public class Event implements Comparable<Event> {
	// constantes
	private static final Logger LOG = LogManager.getLogger(Event.class);
	public static final Vitesse VITESSE_PAR_DEFAUT = Vitesse.MODEREE;
	public static final Frequence FREQUENCE_PAR_DEFAUT = Frequence.BASSE;
	public static final int DIRECTION_PAR_DEFAUT = Event.Direction.BAS;
	public static final int LARGEUR_HITBOX_PAR_DEFAUT = Main.TAILLE_D_UN_CARREAU;
	public static final int HAUTEUR_HITBOX_PAR_DEFAUT = Main.TAILLE_D_UN_CARREAU;
	public static final int NOMBRE_DE_VIGNETTES_PAR_IMAGE = 4;
	public static final boolean ANIME_A_L_ARRET_PAR_DEFAUT = false;
	public static final boolean ANIME_EN_MOUVEMENT_PAR_DEFAUT = true;
	public static final Passabilite TRAVERSABLE_PAR_DEFAUT = Passabilite.OBSTACLE; // si une Page est active
	public static final boolean TRAVERSABLE_PAR_LE_HEROS_PAR_DEFAUT = false;
	public static final Passabilite TRAVERSABLE_SI_VIDE = Passabilite.PASSABLE; // si aucune Page active
	public static final boolean DIRECTION_FIXE_PAR_DEFAUT = false;
	public static final boolean AU_DESSUS_DE_TOUT_PAR_DEFAUT = false;
	public static final boolean REPETER_LE_DEPLACEMENT_PAR_DEFAUT = false;
	public static final boolean IGNORER_LES_MOUVEMENTS_IMPOSSIBLES_PAR_DEFAUT = true;
	public static final boolean ATTENDRE_LA_FIN_DU_DEPLACEMENT_PAR_DEFAUT = true;
	public static final boolean PLAT_PAR_DEFAUT = false;
	public static final boolean PLAT_SI_VIDE = true;
	public static final String MARQUEUR_DE_REINITIALISATION = "RESET";

	/** Map a laquelle cet Event appartient */
	public Map map;
	/** nom de l'Event */
	public String nom;
	/**
	 * identifiant de l'Event pour les Mouvements, tels qu'�crits dans les fichiers
	 * JSON
	 */
	public Integer id;
	/**
	 * distance (en pixels) entre le bord gauche de l'ecran et le corps de l'Event
	 */
	public int x;
	/** distance (en pixels) entre le bord haut de l'ecran et le corps de l'Event */
	public int y;
	/** vies a d�cr�menter pour qu'il disparaisse */
	public int vies;
	/**
	 * Faut-il r�initialiser les interrupteurs locaux de cet event a chaque
	 * changement de Map ?
	 */
	public final boolean reinitialiser;

	/** de combien de pixels avance l'Event a chaque pas ? */
	public Vitesse vitesseActuelle = VITESSE_PAR_DEFAUT;
	/** toutes les combien de frames l'Event change-t-il d'animation ? */
	public Frequence frequenceActuelle = FREQUENCE_PAR_DEFAUT;

	/**
	 * un Event peut �tre d�plac� par une Commande Event externe a son d�placement
	 * naturel nominal
	 */
	public Deplacement deplacementForce;

	public BufferedImage imageActuelle = null;
	public boolean apparenceActuelleEstUnTile;
	public int opaciteActuelle = Graphismes.OPACITE_MAXIMALE;
	public ModeDeFusion modeDeFusionActuel = ModeDeFusion.NORMAL;
	/**
	 * par d�faut, si l'image est plus petite que 32px, l'Event est consid�r� comme
	 * plat (au sol)
	 */
	public boolean platActuel = PLAT_PAR_DEFAUT;
	public int direction;
	public int animation;

	/**
	 * l'Event est-il en train d'avancer en ce moment m�me ? (utile pour
	 * l'animation)
	 */
	public boolean avance = false;
	/** L'Event avan�ait-il a la frame pr�c�dente ? (utile pour l'animation) */
	public boolean avancaitALaFramePrecedente = false;
	/**
	 * l'Event est-il en train de sauter en ce moment m�me ? (utile pour
	 * l'animation)
	 */
	public boolean saute = false;
	public int coordonneeApparenteXLorsDuSaut; // en pixels
	public int coordonneeApparenteYLorsDuSaut; // en pixels

	/**
	 * L'Event est-il au contact du H�ros ? (utile pour la Condition
	 * ArriveeAuContact
	 */
	public boolean estAuContactDuHerosMaintenant = false;
	public boolean estAuContactDuHerosAvant = false;
	public int frameDuContact;

	/**
	 * Ces parametres sont remplis automatiquement au chargement de la page.
	 */
	public boolean animeALArretActuel = ANIME_A_L_ARRET_PAR_DEFAUT;
	public boolean animeEnMouvementActuel = ANIME_EN_MOUVEMENT_PAR_DEFAUT;
	public Passabilite traversableActuel = TRAVERSABLE_PAR_DEFAUT;
	public boolean traversableParLeHerosActuel = TRAVERSABLE_PAR_LE_HEROS_PAR_DEFAUT;
	public boolean directionFixeActuelle = DIRECTION_FIXE_PAR_DEFAUT;
	public boolean auDessusDeToutActuel = AU_DESSUS_DE_TOUT_PAR_DEFAUT;
	public Deplacement deplacementNaturelActuel;

	public int largeurHitbox = LARGEUR_HITBOX_PAR_DEFAUT;
	public int hauteurHitbox = HAUTEUR_HITBOX_PAR_DEFAUT;

	/**
	 * D�cale l'affichage vers le bas. En effet, d�caler l'affichage dans les trois
	 * autres directions est possible en modifiant l'image.
	 */
	int offsetY = 0;
	public ArrayList<PageEvent> pages;
	/**
	 * Page dont on va lire les Commandes Event car toutes ses Conditions sont
	 * remplies
	 */
	public PageEvent pageActive = null;
	/**
	 * Page dont on utilise l'apparence car les Conditions non li�es au contact sont
	 * remplies
	 */
	public PageEvent pageDApparence = null;
	public int numeroDeLaDernierePageDApparence = -1;
	public boolean ilYAEuChangementDePageDApparence = true;

	/**
	 * Lorsque ce marqueur est a true, on consid�re l'event comme supprim�. Ce n'est
	 * qu'un simple marqueur : l'event n'est r�ellement supprim� qu'apres la boucle
	 * for sur les events.
	 */
	public boolean supprime = false;
	/**
	 * Flag pour n'afficher qu'une seule fois le message d'erreur comme quoi l'Event
	 * s'est fait la malle
	 */
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
		 * Calcule la direction oppos�e.
		 * 
		 * @param dir direction a inverser
		 * @return direction oppos�e
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
	 * 
	 * @param x                                  position x (en pixels) o� se trouve
	 *                                           l'Event, en abscisse, de gauche �
	 *                                           droite
	 * @param y                                  position x (en pixels) numero du
	 *                                           carreau o� se trouve l'Event, en
	 *                                           ordonn�e, de haut en bas
	 * @param offsetY                            si on veut afficher l'Event plus
	 *                                           bas que sa case r�elle
	 * @param nom                                de l'Event
	 * @param id                                 identifiant num�rique de l'Event
	 * @param vies                               de l'Event
	 * @param reinitialiserLesInterupteursLocaux a chaque changement de Map ?
	 * @param pages                              ensemble de Pages d�crivant le
	 *                                           comportement de l'Event
	 * @param largeurHitbox                      largeur de la bo�te de collision
	 * @param hauteurHitbox                      hauteur de la bo�te de collision
	 * @param map                                de l'Event
	 */
	public Event(final Integer x, final Integer y, final int offsetY, final String nom, final Integer id,
			final int vies, final boolean reinitialiserLesInterupteursLocaux, final ArrayList<PageEvent> pages,
			final int largeurHitbox, final int hauteurHitbox, final Map map) {
		this.x = x;
		this.y = y;
		this.offsetY = offsetY;
		this.id = id;
		this.nom = nom;
		this.vies = vies;
		this.reinitialiser = reinitialiserLesInterupteursLocaux;
		this.pages = pages;
		this.map = map;
		this.largeurHitbox = largeurHitbox;
		this.hauteurHitbox = hauteurHitbox;
		this.deplacementForce = new Deplacement(null, new ArrayList<Mouvement>(), true, false, false);
		initialiserLesPages();
		if (pages != null && pages.size() >= 1) {
			attribuerLesProprietesActuelles(pages.get(0)); // par d�faut, propri�t�s de la premi�re page
		}
	}

	/**
	 * Constructeur de l'Event utilisant un tableau de pages JSON
	 * 
	 * @param x                                  coordonn�e x (en pixels) o� se
	 *                                           trouve l'Event, en abscisse, de
	 *                                           gauche a droite
	 * @param y                                  coordonn�e y (en pixels) o� se
	 *                                           trouve l'Event, en ordonn�e, de
	 *                                           haut en bas
	 * @param offsetY                            si on veut afficher l'Event plus
	 *                                           bas que sa case r�elle
	 * @param nom                                de l'Event
	 * @param id                                 identifiant num�rique de l'Event
	 * @param vies                               de l'Event
	 * @param reinitialiserLesInterupteursLocaux a chaque changement de Map ?
	 * @param tableauDesPages                    tableau JSON contenant les Pages de
	 *                                           comportement
	 * @param largeurHitbox                      largeur de la bo�te de collision
	 * @param hauteurHitbox                      hauteur de la bo�te de collision
	 * @param map                                de l'Event
	 */
	public Event(final Integer x, final Integer y, final int offsetY, final String nom, final Integer id,
			final int vies, final boolean reinitialiserLesInterupteursLocaux, final JSONArray tableauDesPages,
			final int largeurHitbox, final int hauteurHitbox, final Map map) {
		this(x, y, offsetY, nom, id, vies, reinitialiserLesInterupteursLocaux,
				creerListeDesPagesViaJson(tableauDesPages, id, map), largeurHitbox, hauteurHitbox, map);
	}

	/**
	 * Prend le tableau JSON des pages et cr�e la liste des Pages avec.
	 * 
	 * @param idEvent         identifiant de l'Event
	 * @param tableauDesPages au format JSON
	 * @param map             de l'event
	 * @return liste des Pages de l'Event
	 */
	protected static ArrayList<PageEvent> creerListeDesPagesViaJson(final JSONArray tableauDesPages,
			final Integer idEvent, final Map map) {
		final ArrayList<PageEvent> listeDesPages = new ArrayList<PageEvent>();
		int i = 0;
		for (Object pageJSON : tableauDesPages) {
			listeDesPages.add(new PageEvent(i, (JSONObject) pageJSON, idEvent, map));
			i++;
		}
		return listeDesPages;
	}

	/**
	 * On relie les Conditions et les Commandes a leur Page.
	 */
	protected final void initialiserLesPages() {
		int numeroCondition = 0;
		try {
			for (PageEvent page : this.pages) {
				page.event = this;
				if (page != null) {
					// num�rotation des conditions et on apprend aux conditions qui est leur page
					try {
						for (Condition cond : page.conditions) {
							cond.numero = numeroCondition;
							cond.page = page;
							numeroCondition++;
						}
					} catch (NullPointerException e1) {
						// pas de conditions pour d�clencher cette page
						LOG.warn("La page " + page.numero + " de l'event " + this.nom + " (" + this.id
								+ ") n'a pas besoin de conditions pour se d�clencher.");
					}
					// on apprend aux commandes qui est leur page
					try {
						for (Commande comm : page.commandes) {
							comm.page = page;
						}
					} catch (NullPointerException e2) {
						// pas de commandes dans cette page
						LOG.warn("La page " + page.numero + " de l'event " + this.nom + " (" + this.id
								+ ") n'a pas de commandes.");
					}
				}
			}
		} catch (NullPointerException e3) {
			// pas de pages dans cet event
			LOG.error("L'envent " + this.nom + " (" + this.id + ") n'a pas de pages.");
		}
	}

	/**
	 * Faire faire un Mouvement a l'Event. Ce Mouvement est soit issu du D�placement
	 * naturel de l'Event, soit de son �ventuel D�placement forc�.
	 * 
	 * @Warning Cette Methode est overrid�e pour le H�ros.
	 */
	public void deplacer() {
		if (this.deplacementForce != null && this.deplacementForce.mouvements.size() > 0) {
			// il y a un D�placement forc�
			this.deplacementForce.executerLePremierMouvement();

		} else {
			// pas de D�placement forc� : on execute le D�placement naturel
			if (deplacementNaturelActuel != null && this.deplacementNaturelActuel.mouvements.size() > 0) {
				// il y a un D�placement naturel
				this.deplacementNaturelActuel.executerLePremierMouvement();
			} else {
				// pas de D�placement du tout
				this.avance = false;
				this.saute = false;
				if (!this.animeALArretActuel && !this.avancaitALaFramePrecedente && !this.avance) {
					// l'event ne bouge plus depuis 2 frames, on arr�te son animation
					this.animation = 0;
				}
			}
		}
	}

	/**
	 * Active la Page de l'Event qui v�rifie toutes les Conditions de d�clenchement.
	 * S'il y a plusieurs Pages valides, on prend la derni�re. Les Commandes Event
	 * de la Page active choisie seront execut�es. L'apparence de la Page
	 * d'apparence choisie sera utilis�e.
	 */
	public final void activerUnePage() {
		PageEvent pageQuOnChoisitEnRemplacement = null;
		boolean onATrouveLaPageDApparence = false;
		boolean onATrouveLaPageActive = true;
		try {
			for (int i = pages.size() - 1; i >= 0 && !onATrouveLaPageDApparence; i--) {
				final PageEvent page = pages.get(i);

				// par d�faut une Page est valide, elle sera invalid�e si une Condition est
				// fausse
				onATrouveLaPageActive = true;
				boolean cettePageConvientPourLApparence = true;

				if (page.conditions != null && page.conditions.size() > 0) {
					// la Page a des Conditions de d�clenchement, on les analyse

					// si une Condition est fausse, la Page ne convient pas
					for (int j = 0; j < page.conditions.size() && cettePageConvientPourLApparence; j++) {
						final Condition cond = page.conditions.get(j);
						if (!cond.estVerifiee()) {
							// la Condition n'est pas v�rifi�e
							onATrouveLaPageActive = false;
							if (!cond.estLieeAuHeros()) {
								cettePageConvientPourLApparence = false;
							}
						}
					}
				} else {
					// aucune condition n�cessaire pour cette Page, donc la Page est choisie
					onATrouveLaPageActive = true;
					cettePageConvientPourLApparence = true;
				}

				// si une Page eligible a ete trouv�e, pas besoin d'essayer les autres Pages
				if (cettePageConvientPourLApparence) {
					pageQuOnChoisitEnRemplacement = page;
					onATrouveLaPageDApparence = true;
				}
			}
		} catch (NullPointerException e2) {
			// pas de Pages pour cet Event
			LOG.warn("L'event " + this.id + " (" + this.nom + ") n'a pas de pages.");
			e2.printStackTrace();
		}
		if (!onATrouveLaPageDApparence) {
			// aucune Page ne convient, l'Event n'est pas affiche
			this.pageActive = null;
			this.pageDApparence = null;
			viderLesProprietesActuelles();
			return;
		} else {
			// une Page correspond au moins pour les Conditions non li�es au H�ros, on donne
			// son apparence a l'Event
			this.pageActive = null;
			this.pageDApparence = pageQuOnChoisitEnRemplacement;

			// il est important de noter un v�ritable changement de Page (non vide)
			// en effet cela implique de r�initialiser la direction initiale de l'Event
			if (this.pageDApparence.numero != this.numeroDeLaDernierePageDApparence) {
				this.ilYAEuChangementDePageDApparence = true;
				this.numeroDeLaDernierePageDApparence = this.pageDApparence.numero;
			} else {
				this.ilYAEuChangementDePageDApparence = false;
			}
			attribuerLesProprietesActuelles(this.pageDApparence);

			if (onATrouveLaPageActive) {
				// m�me les Conditions li�es au H�ros correspondent, on execute la Page
				this.pageActive = pageQuOnChoisitEnRemplacement;
			}
		}
	}

	/**
	 * On assigne les propri�t�s actuelles en utilisant celles d'une Page donn�e.
	 * 
	 * @param page dont on r�cup�re les propri�t�s pour les donner a l'Event
	 */
	private void attribuerLesProprietesActuelles(final PageEvent page) {
		// apparence
		this.imageActuelle = page.image;
		this.apparenceActuelleEstUnTile = page.apparenceEstUnTile;

		if (!(this instanceof Heros) // le H�ros n'est pas redirig� aux changements de Page
				&& ilYAEuChangementDePageDApparence) { // on ne r�initialise pas les propri�t�s sans vrai changement de
														// Page
			this.direction = page.directionInitiale;

			// propri�t�s
			this.vitesseActuelle = page.vitesse;
			this.frequenceActuelle = page.frequence;
			this.animeALArretActuel = page.animeALArret;
			this.auDessusDeToutActuel = page.auDessusDeTout;
			this.animeEnMouvementActuel = page.animeEnMouvement;
			this.traversableActuel = page.traversable;
			this.traversableParLeHerosActuel = page.traversableParLeHeros;
			this.directionFixeActuelle = page.directionFixe;
			this.platActuel = page.plat;
			this.opaciteActuelle = page.opacite;
			this.modeDeFusionActuel = page.modeDeFusion;
		}
		// d�placement
		this.deplacementNaturelActuel = page.deplacementNaturel;
	}

	/**
	 * Faire dispara�tre l'Event a l'ecran.
	 */
	private void viderLesProprietesActuelles() {
		// apparence
		this.imageActuelle = null;
		this.apparenceActuelleEstUnTile = false;

		if (!(this instanceof Heros) // le H�ros n'est pas redirig� aux changements de Page
				&& ilYAEuChangementDePageDApparence) { // on ne r�initialise pas les propri�t�s sans vrai changement de
														// Page
			// propri�t�s
			this.vitesseActuelle = Event.VITESSE_PAR_DEFAUT;
			this.frequenceActuelle = Event.FREQUENCE_PAR_DEFAUT;
			this.animeALArretActuel = Event.ANIME_A_L_ARRET_PAR_DEFAUT;
			this.auDessusDeToutActuel = Event.AU_DESSUS_DE_TOUT_PAR_DEFAUT;
			this.animeEnMouvementActuel = Event.ANIME_EN_MOUVEMENT_PAR_DEFAUT;
			this.traversableActuel = Event.TRAVERSABLE_SI_VIDE;
			this.traversableParLeHerosActuel = Event.TRAVERSABLE_PAR_LE_HEROS_PAR_DEFAUT;
			this.directionFixeActuelle = Event.DIRECTION_FIXE_PAR_DEFAUT;
			this.platActuel = Event.PLAT_SI_VIDE;
			this.opaciteActuelle = Graphismes.OPACITE_MAXIMALE;
			this.modeDeFusionActuel = ModeDeFusion.NORMAL;
		}
		// d�placement
		this.deplacementNaturelActuel = null;
	}

	/**
	 * O� afficher l'Event ?
	 * 
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
			largeurVignette = this.imageActuelle.getWidth() / 4;
		}
		return xBase + (this.largeurHitbox - largeurVignette) / 2;
	}

	/**
	 * O� afficher l'Event ?
	 * 
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
			hauteurVignette = this.imageActuelle.getHeight() / 4;
		}
		return yBase + this.hauteurHitbox - hauteurVignette + this.offsetY;
	}

	/**
	 * Traduit les Events depuis le format JSON et les range dans la liste des
	 * Events de la Map.
	 * 
	 * @param events     liste des Events de la Map
	 * @param eventsJSON tableau JSON contenant les Events au format JSON
	 * @param map        des Events
	 */
	public static void recupererLesEvents(final ArrayList<Event> events, final JSONArray eventsJSON, final Map map) {
		// On effectue l'importation des Events en multithread : chaque Event a son
		// thread.
		final ExecutorService executor = Executors.newFixedThreadPool(Main.NOMBRE_DE_THREADS);
		final Vector<Event> eventsVector = new Vector<Event>(); // le Vector est synchronis�, contrairement �
																// l'ArrayList
		ThreadImporterLesEvents.initialiserParametreGlobaux(eventsVector, map);
		for (Object ev : eventsJSON) {
			executor.submit(new ThreadImporterLesEvents((JSONObject) ev)); // chaque thread modifie une ligne du dstOut
		}
		executor.shutdown();
		// On attend la fin de l'execution pour toutes les events JSON
		try {
			while (!executor.awaitTermination(Lecteur.DUREE_FRAME, TimeUnit.MILLISECONDS)) {
				LOG.warn("L'import des Events n'est pas encore termin�...");
			}
		} catch (InterruptedException e) {
			LOG.error("Impossible d'attendre la fin de l'importation des Events !", e);
		}

		// On transvase les Events du vector vers l'ArrayList
		for (Event event : eventsVector) {
			LOG.debug("Event import� : " + event.id + " (" + event.nom + ")");
			events.add(event);
		}
	}

	/**
	 * recuperer un seul event dand le fichier JSON de la Map
	 * 
	 * @param ev  JSON de l'Event qu'on invoque
	 * @param map dans laquelle on invoque l'Event
	 * @return Event instanci�
	 */
	public static Event recupererUnEvent(final JSONObject ev, final Map map) {
		final Vector<Event> eventsVector = new Vector<Event>();
		ThreadImporterLesEvents thread = new ThreadImporterLesEvents((JSONObject) ev);
		ThreadImporterLesEvents.initialiserParametreGlobaux(eventsVector, map);
		thread.run();
		return eventsVector.get(0);
	}

	/**
	 * Multithread pour importer les Events de la Map rapidement.
	 */
	protected static class ThreadImporterLesEvents implements Runnable {
		static Vector<Event> events;
		static Map map;

		final JSONObject jsonEvent;

		/**
		 * Ces parametres sont communs a tou
		 * 
		 * @param events liste pour recuperer les Events import�s
		 * @param map    de ces Events
		 */
		public static void initialiserParametreGlobaux(final Vector<Event> events, final Map map) {
			ThreadImporterLesEvents.events = events;
			ThreadImporterLesEvents.map = map;
		}

		/**
		 * Initialisation du thread
		 * 
		 * @param ev objet JSON contenant les infos de l'Event a importer
		 */
		public ThreadImporterLesEvents(final JSONObject ev) {
			this.jsonEvent = ev;
		}

		@Override
		public final void run() {
			final String nomEvent = jsonEvent.getString("nom");
			final Integer id = jsonEvent.getInt("id");
			try {
				// r�cup�ration des donn�es dans le JSON

				int xEvent, yEvent;
				try {
					// coordonn�es enti�res
					xEvent = ((int) jsonEvent.get("x")) * Main.TAILLE_D_UN_CARREAU;
					yEvent = ((int) jsonEvent.get("y")) * Main.TAILLE_D_UN_CARREAU;
				} catch (ClassCastException e) {
					// coordonn�es non enti�res
					xEvent = (int) (jsonEvent.getDouble("x") * Main.TAILLE_D_UN_CARREAU);
					yEvent = (int) (jsonEvent.getDouble("y") * Main.TAILLE_D_UN_CARREAU);
				}
				// instanciation de l'event
				Event event;

				// on essaye de le cr�er a partir de la biblioth�que JSON GenericEvents
				event = Event.creerEventGenerique(id, nomEvent, xEvent, yEvent, map);

				// si l'Event n'est pas generique, on le construit a partir de sa description
				// dans la page JSON
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
					final boolean reinitialiser = jsonEvent.has("reinitialiser") ? jsonEvent.getBoolean("reinitialiser")
							: nomEvent.contains(MARQUEUR_DE_REINITIALISATION);
					final int vies = jsonEvent.has("vies") ? jsonEvent.getInt("vies") : interpreterVies(nomEvent);

					final JSONArray jsonPages = jsonEvent.getJSONArray("pages");
					event = new Event(xEvent, yEvent, offsetY, nomEvent, id, vies, reinitialiser, jsonPages,
							largeurHitbox, hauteurHitbox, map);
				}
				events.add(event);
			} catch (Exception e) {
				LOG.error("Impossible d'importer l'event " + id + " (" + nomEvent + ") !", e);
			}
		}
	}

	/**
	 * Interpr�ter le nom de l'Event pour connaitre son nombre de vies
	 * 
	 * @param nomEvent nom de l'Event
	 * @return nombre de vies de l'Event
	 */
	private static int interpreterVies(final String nomEvent) {
		final Pattern p = Pattern.compile("HP\\[[0-9]+\\]");
		final Matcher m = p.matcher(nomEvent);
		if (m.find()) {
			final Pattern p2 = Pattern.compile("[0-9]+");
			final Matcher m2 = p2.matcher(m.group(0));
			m2.find();
			final String nombreExtrait = m2.group(0);
			return Integer.parseInt(nombreExtrait);
		}
		return 0;
	}

	/**
	 * Calculer le terrain de l'Event.
	 * 
	 * @return identifiant du terrain
	 */
	public final int calculerTerrain() {
		final int xEvent = (this.x + this.largeurHitbox / 2) / Main.TAILLE_D_UN_CARREAU;
		final int yEvent = (this.y + this.hauteurHitbox / 2) / Main.TAILLE_D_UN_CARREAU;
		try {
			// la couche la plus haute donne son terrain
			int carreauEvent = this.map.layer2[xEvent][yEvent];
			int terrainEvent = this.map.tileset.terrainDeLaCase(carreauEvent);
			if (terrainEvent == 0) {
				// si la couche la plus haute n'a pas de terrain, on prend le terrain de la
				// couche m�diane
				carreauEvent = this.map.layer1[xEvent][yEvent];
				terrainEvent = this.map.tileset.terrainDeLaCase(carreauEvent);
				if (terrainEvent == 0) {
					// si la couche m�diane n'a pas de terrain non plus, on prend le terrain de la
					// couche basse
					carreauEvent = this.map.layer0[xEvent][yEvent];
					terrainEvent = this.map.tileset.terrainDeLaCase(carreauEvent);
					// et tant pis si la couche basse n'a pas de terrain non plus (0)
				}
			}
			return terrainEvent;

		} catch (ArrayIndexOutOfBoundsException e) {
			LOG.warn("Impossible de calculer le terrain, car l'event \"" + this.nom + "\" est sorti de la map ("
					+ xEvent + ";" + yEvent + ")");
			return 0;
		}
	}

	/**
	 * Cr�er un Event generique a partir de sa description JSON.
	 * 
	 * @param id         de l'Event a cr�er
	 * @param nomFichier nom du fichier JSON de l'Event generique
	 * @param xEvent     (en pixels) position x de l'Event
	 * @param yEvent     (en pixels) position y de l'Event
	 * @param map        de l'Event
	 * @return un Event cr��
	 */
	public static Event creerEventGenerique(final int id, final String nomFichier, final int xEvent, final int yEvent,
			final Map map) {
		JSONObject jsonEventGenerique;
		try {
			try {
				// Essayer de trouver un Event generique artisanal
				jsonEventGenerique = InterpreteurDeJson.ouvrirJsonEventGenerique(nomFichier, true);
			} catch (Exception e0) {
				// L'Event generique artisanal n'existe pas
				// Essayer de trouver un Event generique export� automatiquement
				jsonEventGenerique = InterpreteurDeJson.ouvrirJsonEventGenerique(nomFichier, false);
			}
		} catch (Exception e1) {
			// L'Event generique n'existe pas
			// Ni en artisanal, ni en export� automatiquement
			LOG.trace("Impossible de trouver le fichier JSON " + nomFichier + " pour contruire l'Event generique !",
					e1);
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
		final boolean reinitialiser = jsonEventGenerique.has("reinitialiser")
				? jsonEventGenerique.getBoolean("reinitialiser")
				: nomEvent.contains(MARQUEUR_DE_REINITIALISATION);
		final int vies = jsonEventGenerique.has("vies") ? jsonEventGenerique.getInt("vies") : interpreterVies(nomEvent);

		final JSONArray jsonPages = jsonEventGenerique.getJSONArray("pages");
		return new Event(xEvent, yEvent, offsetY, nomEvent, id, vies, reinitialiser, jsonPages, largeurHitbox,
				hauteurHitbox, map);
	}

	/**
	 * V�rifier l'�galit� entre deux Events.
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
	 * Hashcode pour rendre plus rapide la Methode equals().
	 */
	@Override
	public final int hashCode() {
		return Maths.NOMBRE_PREMIER1 + Maths.NOMBRE_PREMIER2 * this.id;
	}

	/**
	 * Permet de dire si un Event est devant ou derri�re un autre en terme
	 * d'affichage.
	 */
	@Override
	public final int compareTo(final Event e) {
		final int eEstDevant = -1;
		final int thisEstDevant = 1;
		if (this.auDessusDeToutActuel) {
			if (e.auDessusDeToutActuel) {
				final int thisY = this.y + this.hauteurHitbox;
				final int eY = e.y + e.hauteurHitbox;
				// les deux sont au dessus de tout, on applique la logique invers�e
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
				// y'en a-t-il un au sol ?
				if (this.platActuel && !e.platActuel) {
					return eEstDevant;
				} else if (e.platActuel && !this.platActuel) {
					return thisEstDevant;
				}

				// si un event saute, on considere sa position au sol
				final int thisY = this.positionAuSol() + this.hauteurHitbox;
				final int eY = e.positionAuSol() + e.hauteurHitbox;

				// aucun n'est au dessus de tout, on applique la logique normale
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
	 * Position au sol de l'Event. Si l'Event ne saute pas, c'est tout simplement sa
	 * coordonnee y.
	 * 
	 * @return coordonnee y, sauf si l'Event saute
	 */
	private int positionAuSol() {
		if (this.saute) {
			Sauter saut = null;
			if (this.deplacementForce != null && this.deplacementForce.mouvements.size() > 0
					&& this.deplacementForce.mouvements.get(0) instanceof Sauter) {
				saut = (Sauter) this.deplacementForce.mouvements.get(0);
			} else if (this.deplacementNaturelActuel != null && this.deplacementNaturelActuel.mouvements.size() > 0
					&& this.deplacementNaturelActuel.mouvements.get(0) instanceof Sauter) {
				saut = (Sauter) this.deplacementNaturelActuel.mouvements.get(0);
			}
			if (saut != null) {
				return saut.yPourCamera();
			}
		}
		return this.y;
	}

}