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
import commandes.Sauvegarder.Sauvegardable;
import main.Fenetre;
import map.Event.Direction;
import utilitaire.InterpreteurDeJson;
import utilitaire.graphismes.Graphismes;
import utilitaire.son.Musique;

/**
 * Une Map est un décor rectangulaire constitué de briques issues du Tileset.
 * Le Heros et les Events se déplacent dedans.
 */
public class Map implements Sauvegardable {
	//constantes
	private static final Logger LOG = LogManager.getLogger(Map.class);
	/** Chaque carreau du Tileset possède une altitude intrinsèque */
	private static final int NOMBRE_ALTITUDES = 6;
	/** L'altitude médiane est celle affichée avec les Events ; les autres altitudes sont soit dessous, soit dessus */
	private static final int ALTITUDE_MEDIANE = 1;
	/** Le décor est constitué de 3 couches, afin de pouvoir superposer plusieurs carreaux au même endroit de la Map */
	private static final int NOMBRE_LAYERS = 3;
	/** La position intiale du Héros sur cette Map est décrite par 5 paramètres */
	private static final int POSITION_INITIALE_PAR_X_Y_ET_DIRECTION = 5;
	/** La position intiale du Héros sur cette Map est décrite par 2 paramètres */
	private static final int POSITION_INITIALE_PAR_DECALAGE_ET_DIRECTION = 2;
	
	/** Numéro du fichier JSON de la Map */
	public final int numero;
	public String nom;
	/** Lecteur chargé de lire cette Map */
	public LecteurMap lecteur;
	/** Nom de la musique */
	public String nomBGM;
	public Float volumeBGM;
	/** Nom du bruit de fond */
	public String nomBGS;
	public Float volumeBGS;
	/** Nom du Tileset utilisé */
	public String nomTileset;
	/** Image contenant les tuiles constitutives de décor possibles */
	public Tileset tileset;
	/** Image de panorama actuel affiché derrière la Map */
	public BufferedImage panoramaActuel;
	/** Taux de parallaxe entre la Map et le Panorama */
	public int parallaxeActuelle;
	/** Dimensions de la Map (en nombre de cases) */
	public final int largeur, hauteur;
	/** Trois couches de décor pour pouvoir placer plusieurs tuiles sur la même case */
	public final int[][] layer0, layer1, layer2;
	public final int[][][] layers;
	/** en cas d'Autotile animé */
	private BufferedImage[] imagesCoucheSousHeros = new BufferedImage[Autotile.NOMBRE_VIGNETTES_AUTOTILE_ANIME];
	/** en cas d'Autotile animé */
	private BufferedImage[] imagesCoucheAvecHeros = new BufferedImage[Autotile.NOMBRE_VIGNETTES_AUTOTILE_ANIME];
	/** en cas d'Autotile animé */
	private BufferedImage[] imagesCoucheSurHeros = new BufferedImage[Autotile.NOMBRE_VIGNETTES_AUTOTILE_ANIME];
	/** Le décor contient-il des autotiles animés ? */
	public boolean contientDesAutotilesAnimes;
	/** Brouillard affiché par dessus de décor et les Events */
	public Brouillard brouillard;
	/** liste des Events rangés par coordonnée y */
	public ArrayList<Event> events;
	/** hashmap des Events rangés par id, 0 pour le Héros */
	public HashMap<Integer, Event> eventsHash;
	/** liste des Events à ajouter au tour suivant */
	public ArrayList<Event> eventsAAjouter = new ArrayList<Event>();
	/** Event numéro 0, dirigé par le joueur */
	public Heros heros;
	/** Coordonnées du héros (en pixels) à l'initialisation de la Map */
	public int xDebutHeros, yDebutHeros;
	/** Direction dans laquelle regarde le Héros à l'initialisation de la Map */
	public int directionDebutHeros;
	/** La case x;y est-elle passable ? */
	public boolean[][] casePassable;
	/** Pour faire défiler la caméra ailleurs que centrée sur le Héros */
	public final boolean defilementCameraX, defilementCameraY;
	/** Effet d'ondulation sur la Map */
	public Ondulation ondulation;
	
	/** Maps adjacentes à celle-ci */
	public HashMap<Integer, Adjacence> adjacences;
	
	/**
	 * Constructeur explicite
	 * @param numero de la Map, c'est-à-dire numéro du fichier map (au format JSON) à charger
	 * @param lecteur de la Map
	 * @param ancienHeros heros de la Map précédente
	 * @param brouillardForce brouillard imposé au chargement de partie
	 * @param positionInitialeDuHeros [xDebutHeros, yDebutHerosArg, directionDebutHeros] ou bien [xAncienneMapHeros, yAncienneMapHeros, decalageDebutHeros, directionDebutHeros]
	 * @throws Exception Impossible de charger la Map
	 */
	public Map(final int numero, final LecteurMap lecteur, final Heros ancienHeros, final Brouillard brouillardForce, 
			final int...positionInitialeDuHeros) throws Exception {
		this.numero = numero;
		this.lecteur = lecteur;
		lecteur.map = this; //on prévient le Lecteur qu'il a une Map
		
		//la map est un fichier JSON
		final JSONObject jsonMap = InterpreteurDeJson.ouvrirJsonMap(this.numero);
		
		this.nomBGM = "";
		this.volumeBGM = Musique.VOLUME_MAXIMAL;
		if (jsonMap.has("musique")) {
			final JSONObject bgmJSON = (JSONObject) jsonMap.get("musique");
			this.nomBGM = bgmJSON.getString("nomFichierSonore");
			this.volumeBGM = bgmJSON.has("volume") ? (float) bgmJSON.getDouble("volume") : Musique.VOLUME_MAXIMAL;
		}
		this.nomBGS = "";
		this.volumeBGS = Musique.VOLUME_MAXIMAL;
		if (jsonMap.has("fondSonore")) {
			final JSONObject bgsJSON = (JSONObject) jsonMap.get("fondSonore");
			this.nomBGS = bgsJSON.getString("nomFichierSonore");
			this.volumeBGS = bgsJSON.has("volume") ? (float) bgsJSON.getDouble("volume") : Musique.VOLUME_MAXIMAL;
		}
		this.nomTileset = jsonMap.getString("tileset");
		if (jsonMap.has("ondulation")) {
			final JSONObject jsonOndulation = jsonMap.getJSONObject("ondulation");
			this.ondulation = new Ondulation(jsonOndulation.getInt("nombreDeVagues"), jsonOndulation.getInt("amplitude"), jsonOndulation.getInt("lenteur"));
		} else {
			this.ondulation = null;
		}
		this.largeur = jsonMap.getInt("largeur");
		this.hauteur = jsonMap.getInt("hauteur");
		this.defilementCameraX = largeur > (Fenetre.LARGEUR_ECRAN/Fenetre.TAILLE_D_UN_CARREAU);
		this.defilementCameraY = hauteur > (Fenetre.HAUTEUR_ECRAN/Fenetre.TAILLE_D_UN_CARREAU);
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
		final int xAncienHeros = ancienHeros != null ? ancienHeros.x : 0; //pas d'ancien Héros en cas de chargement de Partie
		final int yAncienHeros = ancienHeros != null ? ancienHeros.y : 0;
		final int largeurAncienneMap = ancienHeros != null ? ancienHeros.map.largeur : 0;
		final int hauteurAncienneMap = ancienHeros != null ? ancienHeros.map.hauteur : 0;
		transition.direction = Transition.calculerDirectionDefilement(xAncienHeros, yAncienHeros, this.xDebutHeros, 
				this.yDebutHeros, largeurAncienneMap, hauteurAncienneMap, this.largeur, this.hauteur);
		if (Transition.ROND.equals(transition)) {
			// centre du rond
			transition.xHerosAvant = ancienHeros.x + Heros.LARGEUR_HITBOX_PAR_DEFAUT/2;
			transition.yHerosAvant = ancienHeros.y + Heros.HAUTEUR_HITBOX_PAR_DEFAUT/2;
			transition.xHerosApres = xDebutHeros + Heros.LARGEUR_HITBOX_PAR_DEFAUT/2;
			transition.yHerosApres = yDebutHeros + Heros.HAUTEUR_HITBOX_PAR_DEFAUT/2;
		}
		
		//correctif sur la position x y initiale du Héros
		//le Héros n'est pas forcément pile sur le coin haut-gauche du carreau téléporteur
		if (positionInitialeDuHeros.length == POSITION_INITIALE_PAR_X_Y_ET_DIRECTION) {
			final int ecartX = positionInitialeDuHeros[3];
			final int ecartY = positionInitialeDuHeros[4];
			if (transition.direction == Direction.BAS || transition.direction == Direction.HAUT) {
				this.xDebutHeros += ecartX;
			} else {
				this.yDebutHeros += ecartY;
			}
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
		//on dessine la couche de décor médiane, qui sera avec le héros et les évènements
			creerImageDuDecorAuMemeNiveauQueLeHeros();
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
			if (brouillardForce != null) {
				// chargement du précédent Brouillard sauvegardé
				this.brouillard = brouillardForce;
			} else {
				// arrivée sur la Map donc chargement du Brouillard natif
				this.brouillard = Brouillard.creerBrouillardAPartirDeJson(jsonMap);
				if (this.brouillard == null) {
					this.brouillard = this.tileset.brouillard;
				}
			}
	}

	/**
	 * L'affichage est un sandwich : une partie du décor est en fond, et les Events sont affichés par dessus.
	 * On affiche en premier le décor arrière.
	 */
	private void creerImageDuDecorEnDessousDuHeros() {
		creerCoucheDeDecor(this.imagesCoucheSurHeros, 0, ALTITUDE_MEDIANE-1);
		/*
		final long t0 = System.nanoTime();
		
		final int largeurPixel = this.largeur*Fenetre.TAILLE_D_UN_CARREAU;
		final int hauteurPixel = this.hauteur*Fenetre.TAILLE_D_UN_CARREAU;
		
		this.imagesCoucheSousHeros[0] = Graphismes.imageVide(largeurPixel, hauteurPixel);
		
		int numeroCarreau;
		int altitudeCarreau;
		int nombreDeVignettes = 1;
		for (int i = 0; i<largeur; i++) {
			for (int j = 0; j<hauteur; j++) {
				for (int altitudeActuelle = 0; altitudeActuelle<ALTITUDE_MEDIANE; altitudeActuelle++) {
					for (int k = 0; k<NOMBRE_LAYERS; k++) {
						final int[][] layer = layers[k];
						numeroCarreau = layer[i][j];
						if (numeroCarreau == -1) {
							//case vide, on ne dessine rien
						} else {
							altitudeCarreau = this.tileset.altitudeDeLaCase(numeroCarreau);
							if (altitudeCarreau == altitudeActuelle) {
								if (numeroCarreau >= 0) {
									//case normale
									for (int v = 0; v<nombreDeVignettes; v++) {
										dessinerCarreau(this.imagesCoucheSousHeros[v], i, j, numeroCarreau, tileset);
									}
								} else if (numeroCarreau < -1) { 
									//autotile
									nombreDeVignettes = dessinerAutotile(this.imagesCoucheSousHeros, i, j, numeroCarreau, tileset, layer);
								}
							}
						}
					}
				}
			}
		}
		final long t1 = System.nanoTime();
		Fenetre.getFenetre().mesuresDePerformance.add(new Long(t1 - t0).toString());
		*/
	}
	
	/**
	 * L'affichage est un sandwich : une partie du décor est affichée au meme niveau que les Events.
	 * On affiche ce décor médian intercallé en bandelettes entre les Events.
	 */
	private void creerImageDuDecorAuMemeNiveauQueLeHeros() {
		creerCoucheDeDecor(this.imagesCoucheAvecHeros, ALTITUDE_MEDIANE, ALTITUDE_MEDIANE);
	}

	/**
	 * L'affichage est un sandwich : une partie du décor est affichée par dessus les Events.
	 * On affiche en dernier le décor supérieur.
	 */
	private void creerImageDuDecorAuDessusDuHeros() {
		creerCoucheDeDecor(this.imagesCoucheSurHeros, ALTITUDE_MEDIANE+1, NOMBRE_ALTITUDES-1);
		/*
		final long t0 = System.nanoTime();
		
		final int largeurPixel = this.largeur*Fenetre.TAILLE_D_UN_CARREAU;
		final int hauteurPixel = this.hauteur*Fenetre.TAILLE_D_UN_CARREAU;
		
		this.imagesCoucheSurHeros[0] = Graphismes.imageVide(largeurPixel, hauteurPixel);
		
		int numeroCarreau;
		int altitudeCarreau;
		int nombreDeVignettes = 1;
		for (int i = 0; i<largeur; i++) {
			for (int j = 0; j<hauteur; j++) {
				for (int altitudeActuelle = ALTITUDE_MEDIANE; altitudeActuelle<NOMBRE_ALTITUDES; altitudeActuelle++) {
					for (int k = 0; k<NOMBRE_LAYERS; k++) {
						final int[][] layer = layers[k];
						numeroCarreau = layer[i][j];
						if (numeroCarreau != -1) {
							//case non-vide, il y a quelque chose à dessiner
							altitudeCarreau = this.tileset.altitudeDeLaCase(numeroCarreau);
							if (altitudeCarreau == altitudeActuelle) {
								if (numeroCarreau >= 0) {
									//case normale
									for (int v = 0; v<nombreDeVignettes; v++) {
										dessinerCarreau(this.imagesCoucheSurHeros[v], i, j, numeroCarreau, tileset);
									}
								} else if (numeroCarreau < -1) { 
									//autotile
									nombreDeVignettes = dessinerAutotile(this.imagesCoucheSurHeros, i, j, numeroCarreau, tileset, layer);
								}
							}
						}
					}
				}
			}
		}
		
		final long t1 = System.nanoTime();
		Fenetre.getFenetre().mesuresDePerformance.add(new Long(t1 - t0).toString());
		*/
	}
	
	/**
	 * L'affichage est un sandwich : une partie du décor est affichée par dessus les Events, une autre dessous, et une autre au même niveau.
	 * @param vignettesDeCetteCouche vignettes de cette couch de décor pour l'animation du décor
	 * @param altitudeMin première altitude de cette couche de décor
	 * @param altitudeMax dernière altitude (incluse) de cette couche de décor
	 */
	private void creerCoucheDeDecor(BufferedImage[] vignettesDeCetteCouche, int altitudeMin, int altitudeMax) {
		final long t0 = System.nanoTime();
		
		final int largeurPixel = this.largeur*Fenetre.TAILLE_D_UN_CARREAU;
		final int hauteurPixel = this.hauteur*Fenetre.TAILLE_D_UN_CARREAU;
		
		vignettesDeCetteCouche[0] = Graphismes.imageVide(largeurPixel, hauteurPixel);
		
		int numeroCarreau;
		int altitudeCarreau;
		int nombreDeVignettes = 1;
		for (int i = 0; i<largeur; i++) {
			for (int j = 0; j<hauteur; j++) {
				for (int altitudeActuelle = altitudeMin; altitudeActuelle<=altitudeMax; altitudeActuelle++) {
					for (int k = 0; k<NOMBRE_LAYERS; k++) {
						final int[][] layer = layers[k];
						numeroCarreau = layer[i][j];
						if (numeroCarreau != -1) {
							//case non-vide, il y a quelque chose à dessiner
							altitudeCarreau = this.tileset.altitudeDeLaCase(numeroCarreau);
							if (altitudeCarreau == altitudeActuelle) {
								if (numeroCarreau >= 0) {
									//case normale
									for (int v = 0; v<nombreDeVignettes; v++) {
										dessinerCarreau(vignettesDeCetteCouche[v], i, j, numeroCarreau, tileset);
									}
								} else if (numeroCarreau < -1) { 
									//autotile
									nombreDeVignettes = dessinerAutotile(vignettesDeCetteCouche, i, j, numeroCarreau, tileset, layer);
								}
							}
						}
					}
				}
			}
		}
		
		final long t1 = System.nanoTime();
		Fenetre.getFenetre().mesuresDePerformance.add(new Long(t1 - t0).toString());
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
	 * @param decorAnime partie animée du décor (à peindre dans le cas d'un Autotile animé)
	 * @param x coordonnée x du carreau sur la Map
	 * @param y coordonnée y du carreau sur la Map
	 * @param numeroCarreau numéro de l'autotile (numéro négatif)
	 * @param tilesetUtilise Tileset utilisé pour interpréter le décor de la Map
	 * @param layer couche de décor à laquelle appartient le carreau
	 * @return nombre de vignettes nécessaires pour constituer le décor éventuellement animé
	 */
	public final int dessinerAutotile(final BufferedImage[] decorAnime, final int x, final int y,
			final int numeroCarreau, final Tileset tilesetUtilise, final int[][] layer) {
		final Autotile autotile = tilesetUtilise.autotiles.get(numeroCarreau);
		
		//on prévient la Map qu'elle aura un décor animé
		if (autotile.anime && !this.contientDesAutotilesAnimes) {
			this.contientDesAutotilesAnimes = true;
			LOG.debug("Cette map contient des autotiles animés.");
		}
		
		// on crée les vignettes manquantes pour l'animation du décor
		if (this.contientDesAutotilesAnimes && decorAnime[Autotile.NOMBRE_VIGNETTES_AUTOTILE_ANIME - 1] == null) {
			for (int i = 1; i<Autotile.NOMBRE_VIGNETTES_AUTOTILE_ANIME; i++) {
				decorAnime[i] = Graphismes.clonerUneImage(decorAnime[0]);
			}
			LOG.debug("Création des différentes vignettes d'animation du décor.");
		}
		
		final int nombreDeVignettes = this.contientDesAutotilesAnimes ? Autotile.NOMBRE_VIGNETTES_AUTOTILE_ANIME : 1;
		
		//on calcule l'apparence du carreau Autotile
		final BufferedImage[] dessinCarreau = autotile.calculerAutotile(x, y, this.largeur, this.hauteur, numeroCarreau, layer);
		
		if (autotile.anime) {
			//décor animé : on dessine les 4 étapes de l'animation du décor
			for (int i = 0; i<nombreDeVignettes; i++) {
				Graphismes.superposerImages(decorAnime[i], dessinCarreau[i], x*Fenetre.TAILLE_D_UN_CARREAU, y*Fenetre.TAILLE_D_UN_CARREAU);
			}
		} else {
			//décor fixe
			for (int i = 0; i<nombreDeVignettes; i++) {
				Graphismes.superposerImages(decorAnime[i], dessinCarreau[0], x*Fenetre.TAILLE_D_UN_CARREAU, y*Fenetre.TAILLE_D_UN_CARREAU);
			}
		}
		
		return nombreDeVignettes;
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
		for (Event event : this.events) {
			event.map = this;
			if (this.eventsHash.containsKey(event.id)) { //la numérotation des Events comporte un doublon !
				LOG.error("CONFLIT : les events "+this.eventsHash.get(event.id).nom+" et "+event.nom+" portent le même id : "+event.id);
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
	 * @param idEventASupprimer numéro de l'Event qu'il faut inscrire à la suppression
	 * @return booléen pour savoir si l'Event à supprimer a bien été trouvé dans la liste des évènements
	 */
	public final boolean supprimerEvenement(final int idEventASupprimer) {
		for (Event event : this.events) {
			if (event.id == idEventASupprimer) {
				event.supprime = true;
				return true;
			}
		}
		LOG.warn("L'évènement à supprimer id:"+idEventASupprimer+" n'a pas été trouvé dans la liste.");
		return false;
	}

	/**
	 * Obtenir le décor à afficher par dessus le Héros.
	 * Ce décor est composé du décor fixe et d'éventuels autotiles animés.
	 * @param vignetteAutotileActuelle vignette de l'Autotile à afficher
	 * @return décor supérieur, avec l'autotile dépendant de la frame
	 */
	public final BufferedImage getImageCoucheSurHeros(final int vignetteAutotileActuelle) {
		if (this.imagesCoucheSurHeros[1] != null) {
			return this.imagesCoucheSurHeros[vignetteAutotileActuelle];
		} else {
			return this.imagesCoucheSurHeros[0];
		}
	}
	
	/**
	 * Obtenir le décor à afficher au même niveau que le Héros.
	 * Ce décor est composé du décor fixe et d'éventuels autotiles animés.
	 * Il doit être découpé en bandelettes pour s'intercaler avec les Events.
	 * @param vignetteAutotileActuelle vignette de l'Autotile à afficher
	 * @param bandelette à découper
	 * @return bandelette de décor médian, avec l'autotile dépendant de la frame
	 */
	public final BufferedImage getImageCoucheAvecHeros(final int vignetteAutotileActuelle, final int bandelette) {
		BufferedImage vignette;
		if (this.imagesCoucheSurHeros[1] != null) {
			vignette = this.imagesCoucheSurHeros[vignetteAutotileActuelle];
		} else {
			vignette = this.imagesCoucheSurHeros[0];
		}
		return vignette.getSubimage(0, bandelette*Fenetre.TAILLE_D_UN_CARREAU, vignette.getWidth(), Fenetre.TAILLE_D_UN_CARREAU);
	}

	/**
	 * Obtenir le décor à afficher en dessous du Héros.
	 * Ce décor est composé du décor fixe et d'éventuels autotiles animés.
	 * @param vignetteAutotileActuelle frame actuelle du Lecteur
	 * @return décor inférieur, avec l'autotile dépendant de la frame
	 */
	public final BufferedImage getImageCoucheSousHeros(final int vignetteAutotileActuelle) {
		if (this.imagesCoucheSousHeros[1] != null) {
			return this.imagesCoucheSousHeros[vignetteAutotileActuelle];
		} else {
			return this.imagesCoucheSousHeros[0];
		}
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
				LOG.warn("L'event "+event.id+" ("+event.nom+") est sorti de la map !"); //TODO ni le numéro, ni le nom ne semblent correspondre à l'Event qui sort
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
			if (numeroEvent != autreEvent.id 
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

	@Override
	public JSONObject sauvegarderEnJson() {
		final JSONObject jsonEtatMap = new JSONObject();
		jsonEtatMap.put("numero", this.numero);
		jsonEtatMap.put("xHeros", this.heros.x);
		jsonEtatMap.put("yHeros", this.heros.y);
		jsonEtatMap.put("directionHeros", this.heros.direction);
		
		if (this.brouillard != null) {
			final JSONObject jsonBrouillard = this.brouillard.sauvegarderEnJson();
			jsonEtatMap.put("brouillard", jsonBrouillard);
		}
		
		return jsonEtatMap;
	}
	
}
