package map;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import commandes.ModifierInterrupteurLocal;
import main.Fenetre;
import map.Event.Direction;
import utilitaire.InterpreteurDeJson;
import utilitaire.graphismes.Graphismes;

/**
 * Une Map est un décor rectangulaire constitué de briques issues du Tileset.
 * Le Heros et les Events se déplacent dedans.
 */
public class Map {
	//constantes
	private static final Logger LOG = LogManager.getLogger(Map.class);
	/** Chaque carreau du Tileset possède une altitude intrinsèque */
	private static final int NOMBRE_ALTITUDES = 6;
	/** Certaines altitudes sont affichées sous le Héros */
	private static final int NOMBRE_ALTITUDES_SOUS_HEROS = 2;
	/** Certaines altitudes sont affichées au dessus du Héros */
	private static final int NOMBRE_ALTITUDES_SUR_HEROS = NOMBRE_ALTITUDES - NOMBRE_ALTITUDES_SOUS_HEROS;
	/** Le décor est constitué de 3 couches, afin de pouvoir superposer plusieurs carreaux au même endroit de la Map */
	private static final int NOMBRE_LAYERS = 3;
	/** La position intiale du Héros sur cette Map est décrite par 3 paramètres */
	private static final int POSITION_INITIALE_PAR_X_Y_ET_DIRECTION = 3;
	/** La position intiale du Héros sur cette Map est décrite par 2 paramètres */
	private static final int POSITION_INITIALE_PAR_DECALAGE_ET_DIRECTION = 2;
	
	public final int numero;
	public LecteurMap lecteur;
	public String nom;
	public String nomBGM; //musique
	public String nomBGS; //fond sonore
	public String nomTileset;
	public Tileset tileset; //image contenant les decors
	public BufferedImage panoramaActuel;
	public int parallaxeActuelle;
	/** largeur de la Map (en nombre de cases) */
	public final int largeur;
	/** hauteur de la Map (en nombre de cases) */
	public final int hauteur;
	public final int[][] layer0; //couche du sol
	public final int[][] layer1; //couche de decor 1
	public final int[][] layer2; //couche de decor 2
	public final int[][][] layers;
	/** en cas d'Autotile animé */
	private BufferedImage[] imagesCoucheSousHeros = new BufferedImage[Autotile.NOMBRE_VIGNETTES_AUTOTILE_ANIME]; 
	/** en cas d'Autotile animé */
	private BufferedImage[] imagesCoucheSurHeros = new BufferedImage[Autotile.NOMBRE_VIGNETTES_AUTOTILE_ANIME]; 
	public boolean contientDesAutotilesAnimes;
	public Brouillard brouillard;
	/** liste des Events rangés par coordonnée y */
	public ArrayList<Event> events;
	/** hashmap des Events rangés par id, 0 pour le Héros */
	public HashMap<Integer, Event> eventsHash;
	/** liste des Events à ajouter au tour suivant */
	public ArrayList<Event> eventsAAjouter = new ArrayList<Event>();
	public Heros heros;
	/** coordonnée x (en pixels) */
	public int xDebutHeros;
	/** coordonnée y (en pixels) */
	public int yDebutHeros;
	public int directionDebutHeros;
	public boolean[][] casePassable;
	public final boolean defilementCameraX;
	public final boolean defilementCameraY;
	
	/** Maps adjacentes à celle-ci */
	public HashMap<Integer, Adjacence> adjacences;
	
	/**
	 * Constructeur explicite
	 * @param numero de la Map, c'est-à-dire numéro du fichier map (au format JSON) à charger
	 * @param lecteur de la Map
	 * @param ancienHeros heros de la Map précédente
	 * @param positionInitialeDuHeros [xDebutHeros, yDebutHerosArg, directionDebutHeros] ou bien [xAncienneMapHeros, yAncienneMapHeros, decalageDebutHeros, directionDebutHeros]
	 * @throws Exception Impossible de charger la Map
	 */
	public Map(final int numero, final LecteurMap lecteur, final Heros ancienHeros, final int...positionInitialeDuHeros) throws Exception {
		this.numero = numero;
		this.lecteur = lecteur;
		lecteur.map = this; //on prévient le Lecteur qu'il a une Map
		
		//la map est un fichier JSON
		final JSONObject jsonMap = InterpreteurDeJson.ouvrirJsonMap(numero);
		
		this.nomBGM = jsonMap.getString("bgm");
		this.nomBGS = jsonMap.getString("bgs");
		this.nomTileset = jsonMap.getString("tileset");
		this.largeur = jsonMap.getInt("largeur");
		this.hauteur = jsonMap.getInt("hauteur");
		this.defilementCameraX = largeur>(Fenetre.LARGEUR_ECRAN/Fenetre.TAILLE_D_UN_CARREAU);
		this.defilementCameraY = hauteur>(Fenetre.HAUTEUR_ECRAN/Fenetre.TAILLE_D_UN_CARREAU);
		this.layer0 = recupererCouche(jsonMap, 0);
		this.layer1 = recupererCouche(jsonMap, 1);
		this.layer2 = recupererCouche(jsonMap, 2);
		this.layers = new int[][][] {this.layer0, this.layer1, this.layer2};
		
		//position initiale du Héros
		if (positionInitialeDuHeros.length == POSITION_INITIALE_PAR_X_Y_ET_DIRECTION) {
			// Coordonnées initiales spécifiées
			this.xDebutHeros = positionInitialeDuHeros[0]; //position x (en pixels) initiale du Héros
			this.yDebutHeros = positionInitialeDuHeros[1]; //position y (en pixels) initiale du Héros
			this.directionDebutHeros = positionInitialeDuHeros[2]; //direction initiale du Héros
			
		} else if (positionInitialeDuHeros.length == POSITION_INITIALE_PAR_DECALAGE_ET_DIRECTION) {
			// Coordonnées initiales non-spécifiées
			final int decalageDebutHeros = positionInitialeDuHeros[0]; //décalage (en nombre de cases) du Héros par rapport à l'ancienne Map
			this.directionDebutHeros = positionInitialeDuHeros[1]; //direction initiale du Héros
			
			switch (this.directionDebutHeros) {
			case Direction.HAUT:
				this.xDebutHeros = ancienHeros.x + decalageDebutHeros*Fenetre.TAILLE_D_UN_CARREAU;
				this.yDebutHeros = (this.hauteur-1)*Fenetre.TAILLE_D_UN_CARREAU;
				break;
			case Direction.BAS:
				this.xDebutHeros = ancienHeros.x + decalageDebutHeros*Fenetre.TAILLE_D_UN_CARREAU;
				this.yDebutHeros = 0;
				break;
			case Direction.GAUCHE:
				this.xDebutHeros = (this.largeur-1)*Fenetre.TAILLE_D_UN_CARREAU;
				this.yDebutHeros = ancienHeros.y + decalageDebutHeros*Fenetre.TAILLE_D_UN_CARREAU;
				break;
			case Direction.DROITE:
				this.xDebutHeros = 0;
				this.yDebutHeros = ancienHeros.y + decalageDebutHeros*Fenetre.TAILLE_D_UN_CARREAU;
				break;
			default:
				LOG.error("Direction inconnue !");
				break;
			}
			LOG.debug("Coordonnées initiales du héros calculées automatiquement : "+ this.xDebutHeros +";"+ this.yDebutHeros);
		} else {
			throw new Exception("Nombre incorrect de paramètres pour la position initiale du héros : "+positionInitialeDuHeros.length);
		}
		
		//maps adjacentes à celle-ci
		if (jsonMap.has("adjacences")) {
			this.adjacences = new HashMap<Integer, Adjacence>();
			final JSONArray adjacencesJsonArray = jsonMap.getJSONArray("adjacences");
			for (Object o : adjacencesJsonArray) {
				final JSONObject jsonAdjacence = (JSONObject) o;
				
				final Integer direction = new Integer(jsonAdjacence.getInt("direction"));
				final int decalage = jsonAdjacence.has("decalage") ? jsonAdjacence.getInt("decalage") : 0;
				final Transition transition = jsonAdjacence.has("transition") ? Transition.parNom(jsonAdjacence.getString("transition")) : Transition.parDefaut();
				
				final Adjacence adjacence = new Adjacence(jsonAdjacence.getInt("numeroMap"), direction, decalage, transition);
				this.adjacences.put(direction, adjacence);
				LOG.debug("Cette map a une adjacence dans la direction " + direction);
			}
		}
		
		//informations sur la transition
		final Transition transition = lecteur.transition;
		if (Transition.DEFILEMENT.equals(transition)) {
			// direction du défilement
			transition.direction = Transition.calculerDirectionDefilement(ancienHeros, ancienHeros.map, this);
		} else if (Transition.ROND.equals(transition)) {
			// centre du rond
			transition.xHerosAvant = ancienHeros.x + Heros.LARGEUR_HITBOX_PAR_DEFAUT/2;
			transition.yHerosAvant = ancienHeros.y + Heros.HAUTEUR_HITBOX_PAR_DEFAUT/2;
			transition.xHerosApres = xDebutHeros + Heros.LARGEUR_HITBOX_PAR_DEFAUT/2;
			transition.yHerosApres = yDebutHeros + Heros.HAUTEUR_HITBOX_PAR_DEFAUT/2;
		}
		
		//chargement du tileset
		try {
			//si jamais le Tileset est le même, pas la peine de le recréer
			final Tileset tilesetActuel = ((LecteurMap) Fenetre.getFenetre().lecteur).tilesetActuel;
			if (this.nomTileset.equals(tilesetActuel.nom)) {
				this.tileset = tilesetActuel;
				LOG.info("Le Tileset n'a pas changé, on garde le même.");
			} else {
				throw new Exception("Le Tileset a changé.");
			}
		} catch (Exception e1) {
			//impossible de convertir le Lecteur en LecteurMap car c'est un LecteurMenu
			//ou bien
			//le Lecteur actuel est null
			//ou bien
			//le Tileset a changé
			try {
				LOG.info("Le Tileset a changé, il faut le recharger.");
				this.tileset = new Tileset(this.nomTileset);
			} catch (IOException e2) {
				LOG.error("Erreur lors de la création du Tileset :", e2);
			}
		}
		
		//on dessine la couche de décor inférieure, qui sera sous le héros et les évènements
			creerImageDuDecorEnDessousDuHeros();
		
		//on dessine la couche de décor supérieure, qui sera au dessus du héros et des évènements
			creerImageDuDecorAuDessusDuHeros();
		
		//importer les events
			importerLesEvenements(jsonMap);
			
		//création de la liste des cases passables
			creerListeDesCasesPassables();
		
		//panorama
			this.panoramaActuel = this.tileset.imagePanorama;
			this.parallaxeActuelle = this.tileset.parallaxe;
			
		//création du Brouillard
			this.brouillard = Brouillard.creerBrouillardAPartirDeJson(jsonMap);
			if (this.brouillard == null) {
				this.brouillard = this.tileset.brouillard;
			}

	}

	/**
	 * L'affichage est un sandwich : une partie du décor est en fond, et les Events sont affichés par dessus.
	 * On affiche en premier le décor arrière.
	 */
	private void creerImageDuDecorEnDessousDuHeros() {
		//on initialise les images des couches
		final BufferedImage[] couches = new BufferedImage[NOMBRE_ALTITUDES_SOUS_HEROS];
		final BufferedImage[][] couchesAutotile = new BufferedImage[NOMBRE_ALTITUDES_SOUS_HEROS][Autotile.NOMBRE_VIGNETTES_AUTOTILE_ANIME];
		final int largeurPixel = largeur*Fenetre.TAILLE_D_UN_CARREAU;
		final int hauteurPixel = hauteur*Fenetre.TAILLE_D_UN_CARREAU;
		for (int i = 0; i<NOMBRE_ALTITUDES_SOUS_HEROS; i++) {
			couches[i] = Graphismes.imageVide(largeurPixel, hauteurPixel);
			for (int j = 0; j<Autotile.NOMBRE_VIGNETTES_AUTOTILE_ANIME; j++) {
				couchesAutotile[i][j] = Graphismes.imageVide(largeurPixel, hauteurPixel);
			}
		}
		
		//on dessine les carreaux sur les couches
		int numeroCarreau;
		int altitudeCarreau;
		BufferedImage couche;
		BufferedImage[] coucheAnimee;
		for (int i = 0; i<largeur; i++) {
			for (int j = 0; j<hauteur; j++) {
				for (int k = 0; k<NOMBRE_LAYERS; k++) {
					final int[][] layer = layers[k];
					try {
						numeroCarreau = layer[i][j];
						altitudeCarreau = this.tileset.altitudeDeLaCase(numeroCarreau);
						if (altitudeCarreau<NOMBRE_ALTITUDES_SOUS_HEROS) {
							if (numeroCarreau >= 0) { 
								//case normale
								couche = couches[altitudeCarreau];
								dessinerCarreau(couche, i, j, numeroCarreau, tileset);
							} else if (numeroCarreau < -1) { 
								//autotile
								couche = couches[altitudeCarreau];
								coucheAnimee = couchesAutotile[altitudeCarreau];
								dessinerAutotile(couche, coucheAnimee, i, j, numeroCarreau, tileset, layer);
							}
						}
					} catch (ArrayIndexOutOfBoundsException e) {
						//case vide
						LOG.trace("case vide");
					}
				}
			}
		}
		
		//assemblage des différentes altitudes
		for (int j = 0; j<Autotile.NOMBRE_VIGNETTES_AUTOTILE_ANIME; j++) {
			if (this.imagesCoucheSousHeros[j] == null) {
				this.imagesCoucheSousHeros[j] = Graphismes.clonerUneImage(couches[0]);
			}
			for (int i = 0; i<NOMBRE_ALTITUDES_SOUS_HEROS; i++) {
				this.imagesCoucheSousHeros[j] = Graphismes.superposerImages(this.imagesCoucheSousHeros[j], couches[i], 0, 0);
				this.imagesCoucheSousHeros[j] = Graphismes.superposerImages(this.imagesCoucheSousHeros[j], couchesAutotile[i][j], 0, 0);
			}
		}
	}

	/**
	 * L'affichage est un sandwich : une partie du décor est affichée par dessus les Events.
	 * On affiche en dernier le décor supérieur.
	 */
	private void creerImageDuDecorAuDessusDuHeros() {
		//on initialise les images des couches
		final BufferedImage[] couches = new BufferedImage[NOMBRE_ALTITUDES_SUR_HEROS];
		final BufferedImage[][] couchesAutotile = new BufferedImage[NOMBRE_ALTITUDES_SUR_HEROS][Autotile.NOMBRE_VIGNETTES_AUTOTILE_ANIME];
		final int largeurPixel = largeur*Fenetre.TAILLE_D_UN_CARREAU;
		final int hauteurPixel = hauteur*Fenetre.TAILLE_D_UN_CARREAU;
		for (int i = 0; i<NOMBRE_ALTITUDES_SUR_HEROS; i++) {
			couches[i] = Graphismes.imageVide(largeurPixel, hauteurPixel);
			for (int j = 0; j<Autotile.NOMBRE_VIGNETTES_AUTOTILE_ANIME; j++) {
				couchesAutotile[i][j] = Graphismes.imageVide(largeurPixel, hauteurPixel);
			}
		}
		
		//on dessine les carreaux sur les couches
		int numeroCarreau;
		int altitudeCarreau;
		BufferedImage couche;
		BufferedImage[] coucheAnimee;
		for (int i = 0; i<largeur; i++) {
			for (int j = 0; j<hauteur; j++) {
				for (int k = 0; k<NOMBRE_LAYERS; k++) {
					final int[][] layer = layers[k];
					try {
						numeroCarreau = layer[i][j];
						altitudeCarreau = this.tileset.altitudeDeLaCase(numeroCarreau);
						if (altitudeCarreau >= NOMBRE_ALTITUDES_SOUS_HEROS) {
							if (numeroCarreau >= 0) { 
								//case normale
								couche = couches[altitudeCarreau-NOMBRE_ALTITUDES_SOUS_HEROS];
								dessinerCarreau(couche, i, j, numeroCarreau, tileset);
							} else if (numeroCarreau < -1) { 
								//autotile
								coucheAnimee = couchesAutotile[altitudeCarreau-NOMBRE_ALTITUDES_SOUS_HEROS];
								couche = couches[altitudeCarreau-NOMBRE_ALTITUDES_SOUS_HEROS];
								dessinerAutotile(couche, coucheAnimee, i, j, numeroCarreau, tileset, layer);
							}
						}
					} catch (ArrayIndexOutOfBoundsException e) {
						//case vide
						LOG.trace("case vide");
					}
				}
			}
		}
		
		//assemblage des différentes altitudes
		for (int j = 0; j<Autotile.NOMBRE_VIGNETTES_AUTOTILE_ANIME; j++) {
			if (this.imagesCoucheSurHeros[j] == null) {
				this.imagesCoucheSurHeros[j] = Graphismes.clonerUneImage(couches[0]);
			}
			for (int i = 1; i<NOMBRE_ALTITUDES_SUR_HEROS; i++) {
				this.imagesCoucheSurHeros[j] = Graphismes.superposerImages(this.imagesCoucheSurHeros[j], couches[i], 0, 0);
				this.imagesCoucheSurHeros[j] = Graphismes.superposerImages(this.imagesCoucheSurHeros[j], couchesAutotile[i][j], 0, 0);
			}
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
	 */
	public final void dessinerCarreau(final BufferedImage ecran, final int xEcran, final int yEcran, final int numeroCarreau, final Tileset tilesetUtilise) {
		final BufferedImage dessinCarreau = tilesetUtilise.carreaux[numeroCarreau];
		Graphismes.superposerImages(ecran, dessinCarreau, xEcran*Fenetre.TAILLE_D_UN_CARREAU, yEcran*Fenetre.TAILLE_D_UN_CARREAU);
	}
	
	/**
	 * Dessiner à l'écran un carreau issu d'un autotile.
	 * @warning Ne pas oublier de récupérer le résultat de cette méthode.
	 * @param decor supérieur ou inférieur de la Map, sur lequel on doit dessiner un carreau
	 * @param decorAnime partie animée du décor (à peindre dans le cas d'un Autotile animé)
	 * @param x coordonnée x du carreau sur la Map
	 * @param y coordonnée y du carreau sur la Map
	 * @param numeroCarreau numéro de l'autotile (numéro négatif)
	 * @param tilesetUtilise Tileset utilisé pour interpréter le décor de la Map
	 * @param layer couche de décor à laquelle appartient le carreau
	 */
	public final void dessinerAutotile(final BufferedImage decor, final BufferedImage[] decorAnime, final int x, final int y,
			final int numeroCarreau, final Tileset tilesetUtilise, final int[][] layer) {
		final Autotile autotile = tilesetUtilise.autotiles.get(numeroCarreau);
		
		//on prévient la Map qu'elle aura un décor animé
		if (autotile.anime) {
			this.contientDesAutotilesAnimes = true;
		}
		
		//on calcule l'apparence du carreau Autotile
		final BufferedImage[] dessinCarreau = autotile.calculerAutotile(x, y, this.largeur, this.hauteur, numeroCarreau, layer);
		
		if (autotile.anime) {
			//décor animé : on dessine les 4 étapes de l'animation du décor
			for (int i = 0; i<Autotile.NOMBRE_VIGNETTES_AUTOTILE_ANIME; i++) {
				Graphismes.superposerImages(decorAnime[i], dessinCarreau[i], x*Fenetre.TAILLE_D_UN_CARREAU, y*Fenetre.TAILLE_D_UN_CARREAU);
			}
		} else {
			//décor fixe
			Graphismes.superposerImages(decor, dessinCarreau[0], x*Fenetre.TAILLE_D_UN_CARREAU, y*Fenetre.TAILLE_D_UN_CARREAU);
		}
	}

	/**
	 * Constitue la liste des Events de la Map en allant lire dans le fichier JSON décrivant la Map.
	 * @param jsonMap objet JSON décrivant la Map
	 */
	private void importerLesEvenements(final JSONObject jsonMap) {
		try {
			this.events = new ArrayList<Event>();
			this.eventsHash = new HashMap<Integer, Event>();
			//d'abord le héros
			this.heros = new Heros(this.xDebutHeros, this.yDebutHeros, this.directionDebutHeros, this);
			this.events.add(heros);
			//this.eventsHash.put(0, heros);
			//puis les autres
			final JSONArray jsonEvents = jsonMap.getJSONArray("events");
			Event.recupererLesEvents(this.events, jsonEvents, this);
		} catch (Exception e3) {
			LOG.error("Erreur lors de la constitution de la liste des events :", e3);
		}
		
		// Numérotation des Events
		final int nombreDEvents = this.events.size();
		Event event;
		for (int i = 0; i<nombreDEvents; i++) {
			event = this.events.get(i);
			event.map = this;
			event.numero = i;
			if (this.eventsHash.containsKey(event.id)) { //la numérotation des Events comporte un doublon !
				LOG.error("Un autre event porte déjà le numéro : " + event.id);
			}
			this.eventsHash.put(event.id, event);
		}
		
		// Réinitialisation des interrupteurs locaux (si besoin)
		for (Event eventAReinitialiser : this.events) {
			if (eventAReinitialiser.reinitialiser) {
				ModifierInterrupteurLocal.reinitialiserEvent(eventAReinitialiser);
			}
		}
	}

	/**
	 * Création d'un tableau pour connaitre les passabilités de la Map plus rapidement par la suite.
	 */
	private void creerListeDesCasesPassables() {
		this.casePassable = new boolean[this.largeur][this.hauteur];
		int[][] couche;
		int numeroDeLaCaseDansLeTileset = -1;
		for (int i = 0; i<this.largeur; i++) {
			for (int j = 0; j<this.hauteur; j++) {
				this.casePassable[i][j] = true;
				
				int altitudeDeCetteCouche = -1;
				final boolean[] obstacleALAltitude = new boolean[NOMBRE_ALTITUDES];
				// Pour chaque altitude, la couche la plus haute (hors -1) donne la passabilité
				for (int k = 0; k<NOMBRE_LAYERS; k++) {
					couche = layers[k]; //couche de décor
					numeroDeLaCaseDansLeTileset = couche[i][j];

					if (numeroDeLaCaseDansLeTileset != -1) {
					altitudeDeCetteCouche = this.tileset.altitudeDeLaCase(numeroDeLaCaseDansLeTileset);
					// Y a-t-il un obstacle à cette altitude ?
					obstacleALAltitude[altitudeDeCetteCouche] = this.tileset.laCaseEstUnObstacle(numeroDeLaCaseDansLeTileset);
					}
				}
				// Si au moins une des altitudes est bloquante (dernière affectation), la case est bloquante
				for (int a = 0; a<NOMBRE_ALTITUDES; a++) {	
					if (obstacleALAltitude[a]) {
						this.casePassable[i][j] = false;
					}
				}
			}
		}
	}

	/**
	 * Va chercher une couche de décor en particulier dans le fichier JSON qui représente la Map.
	 * @param jsonMap objet JSON représentant la map
	 * @param numeroCouche numéro de la couche à récuperer
	 * @return un tableau bidimentionnel contenant le décor situé sur cette couche
	 */
	private int[][] recupererCouche(final JSONObject jsonMap, final int numeroCouche) {
		final int[][] couche = new int[largeur][hauteur];
		final JSONArray array = jsonMap.getJSONArray("couche"+numeroCouche);
		JSONArray ligne;
		for (int j = 0; j<hauteur; j++) {
			ligne = (JSONArray) array.get(j);
			for (int i = 0; i<largeur; i++) {
				int numeroCarreau;
				try {
					numeroCarreau = (int) ligne.get(i);
				} catch (ClassCastException e) {
					numeroCarreau = Integer.parseInt((String) ligne.get(i));
				}
				couche[i][j] = numeroCarreau;
			}
		}
		return couche;
	}

	/**
	 * Inscrire l'Event dans la liste des Events en attente de suppression.
	 * L'Event sera supprimé à la fin de la boucle d'affichage.
	 * @param numeroEventASupprimer numéro de l'Event qu'il faut inscrire à la suppression
	 * @return booléen pour savoir si l'Event à supprimer a bien été trouvé dans la liste des évènements
	 */
	public final boolean supprimerEvenement(final int numeroEventASupprimer) {
		for (Event event : this.events) {
			if (event.numero == numeroEventASupprimer) {
				event.supprime = true;
				return true;
			}
		}
		LOG.warn("L'évènement à supprimer numéro "+numeroEventASupprimer+" n'a pas été trouvé dans la liste.");
		return false;
	}

	/**
	 * Obtenir le décor à afficher par dessus le Héros.
	 * Ce décor est composé du décor fixe et d'éventuels autotiles animés.
	 * @param vignetteAutotileActuelle vignette de l'Autotile à afficher
	 * @return décor supérieur, avec l'autotile dépendant de la frame
	 */
	public final BufferedImage getImageCoucheSurHeros(final int vignetteAutotileActuelle) {
		return this.imagesCoucheSurHeros[vignetteAutotileActuelle];
	}

	/**
	 * Obtenir le décor à afficher en dessous du Héros.
	 * Ce décor est composé du décor fixe et d'éventuels autotiles animés.
	 * @param vignetteAutotileActuelle frame actuelle du Lecteur
	 * @return décor inférieur, avec l'autotile dépendant de la frame
	 */
	public final BufferedImage getImageCoucheSousHeros(final int vignetteAutotileActuelle) {
		return this.imagesCoucheSousHeros[vignetteAutotileActuelle];
	}
	
	/**
	 * Calcule si on peut poser un Event sur la Map à cet endroit-là.
	 * Ne pas utiliser cette méthode si l'Event à poser est traversable, car la réponse est forcément oui.
	 * @param xmin position x (en pixels) de l'Event qu'on veut poser
	 * @param ymin position y (en pixels) de l'Event qu'on veut poser
	 * @param largeurHitbox largeur de l'Event à poser
	 * @param hauteurHitbox hauteur de l'Event à poser
	 * @param numeroEvent numéro de l'Event à poser
	 * @return true si on peut poser un nouvel Event ici, false sinon
	 */
	public final boolean calculerSiLaPlaceEstLibre(final int xmin, final int ymin, final int largeurHitbox, final int hauteurHitbox, final int numeroEvent) {
		final int xmax = xmin + largeurHitbox;
		final int ymax = ymin + hauteurHitbox;
		
		try {
			//aucun des 4 coins de l'Event ne doivent être sur une case non passable
			if (!this.casePassable[xmin/Fenetre.TAILLE_D_UN_CARREAU][ymin/Fenetre.TAILLE_D_UN_CARREAU]) {
				return false;
			}
			if (!this.casePassable[(xmax-1)/Fenetre.TAILLE_D_UN_CARREAU][ymin/Fenetre.TAILLE_D_UN_CARREAU]) {
				return false;
			}
			if (!this.casePassable[xmin/Fenetre.TAILLE_D_UN_CARREAU][(ymax-1)/Fenetre.TAILLE_D_UN_CARREAU]) {
				return false;
			}
			if (!this.casePassable[(xmax-1)/Fenetre.TAILLE_D_UN_CARREAU][(ymax-1)/Fenetre.TAILLE_D_UN_CARREAU]) {
				return false;
			}
		} catch (Exception e) {
			//l'Event sort de la Map !
			final Event event = this.eventsHash.get((Integer) numeroEvent);
			if (!event.sortiDeLaMap) { //on n'affiche le message d'erreur qu'une fois
				LOG.warn("L'event "+event.numero+" ("+event.nom+") est sorti de la map !"); //TODO ni le numéro, ni le nom ne semblent correspondre à l'Event qui sort
				LOG.trace(e);
			}
			event.sortiDeLaMap = true;
		}
			
		//si rencontre avec un autre Event non traversable -> false
		int xmin2;
		int xmax2;
		int ymin2;
		int ymax2;
		for (Event autreEvent : this.events) {
			xmin2 = autreEvent.x;
			xmax2 = autreEvent.x + autreEvent.largeurHitbox;
			ymin2 = autreEvent.y;
			ymax2 = autreEvent.y + autreEvent.hauteurHitbox;
			if (numeroEvent != autreEvent.numero 
				&& !autreEvent.traversableActuel
				&& Hitbox.lesDeuxRectanglesSeChevauchent(xmin, xmax, ymin, ymax, 
						xmin2, xmax2, ymin2, ymax2, 
						largeurHitbox, hauteurHitbox, 
						autreEvent.largeurHitbox, autreEvent.hauteurHitbox) 
			) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Inventer un id pour un nouvel Event de eventHash.
	 * @return id inédit
	 */
	public final int calculerNouvelIdPourEventsHash() {
		//on invente un tout nouvel id
		boolean lIdEstDejaPris;
		final int nombreDEvents = this.events.size();
		for (int nouvelId = 0;; nouvelId++) {
			lIdEstDejaPris = false;
			for (int i = 0; i<nombreDEvents && !lIdEstDejaPris; i++) {
				if (this.events.get(i).id == nouvelId) {
					lIdEstDejaPris = true;
				}
			}
			if (!lIdEstDejaPris) {
				LOG.debug("Le nouvel id d'event choisi est "+nouvelId);
				return nouvelId;
			}
		}
	}

	/**
	 * Si le Héros sort de la Map, il va vers une éventuelle Map adjacente automatiquement.
	 */
	public void sortirVersLaMapAdjacente() {
		if (this.adjacences != null && !this.adjacences.isEmpty()) {
			Adjacence adjacence = null;
			if (heros.x < 0) {
				// Sortie par la gauche
				adjacence = this.adjacences.get(new Integer(Direction.GAUCHE));
			} else if (heros.x > this.largeur*Fenetre.TAILLE_D_UN_CARREAU - Heros.LARGEUR_HITBOX_PAR_DEFAUT) {
				// Sortie par la droite
				adjacence = this.adjacences.get(new Integer(Direction.DROITE));
			} else if (heros.y < 0) {
				// Sortie par le haut
				adjacence = this.adjacences.get(new Integer(Direction.HAUT));
			} else if (heros.y > this.hauteur*Fenetre.TAILLE_D_UN_CARREAU - Heros.HAUTEUR_HITBOX_PAR_DEFAUT) {
				// Sortie par le bas
				adjacence = this.adjacences.get(new Integer(Direction.BAS));
			} else {
				// Le Héros est encore dans la Map
				return;
			}
			
			if (adjacence != null) {
				adjacence.allerALaMapAdjacente(this.heros);
			}
		}
	}
	
}
