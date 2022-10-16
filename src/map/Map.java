package map;

import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
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
import main.Main;
import map.Event.Direction;
import map.positionInitiale.PositionInitiale;
import utilitaire.InterpreteurDeJson;
import utilitaire.Maths;
import utilitaire.graphismes.Graphismes;
import utilitaire.son.Musique;

/**
 * Une Map est un decor rectangulaire constitu� de briques issues du Tileset.
 * Le Heros et les Events se deplacent dedans.
 */
public class Map implements Sauvegardable {
	//constantes
	private static final Logger LOG = LogManager.getLogger(Map.class);
	/** Chaque carreau du Tileset poss�de une altitude intrins�que */
	private static final int NOMBRE_ALTITUDES = 6;
	/** L'altitude m�diane est celle affichee avec les Events ; les autres altitudes sont soit dessous, soit dessus */
	private static final int ALTITUDE_MEDIANE = 1;
	/** Le decor est constitu� de 3 couches, afin de pouvoir superposer plusieurs carreaux au meme endroit de la Map */
	private static final int NOMBRE_LAYERS = 3;
	/** La position intiale du Heros sur cette Map est decrite par 5 parametres */
	private static final int POSITION_INITIALE_PAR_X_Y_ET_DIRECTION = 5;
	/** La position intiale du Heros sur cette Map est decrite par 2 parametres */
	private static final int POSITION_INITIALE_PAR_DECALAGE_ET_DIRECTION = 2;
	
	/** Numero du fichier JSON de la Map */
	public final int numero;
	public String nom;
	/** Lecteur charg� de lire cette Map */
	public LecteurMap lecteur;
	/** Nom de la musique */
	public String nomBGM;
	public Float volumeBGM;
	/** Nom du bruit de fond */
	public String nomBGS;
	public Float volumeBGS;
	/** Nom du Tileset utilis� */
	public String nomTileset;
	/** Image contenant les tuiles constitutives de decor possibles */
	public Tileset tileset;
	/** Image de panorama actuel affiche derri�re la Map */
	public BufferedImage panoramaActuel;
	/** Taux de parallaxe entre la Map et le Panorama */
	public int parallaxeActuelle;
	/** Dimensions de la Map (en nombre de cases) */
	public final int largeur, hauteur;
	/** Trois couches de decor pour pouvoir placer plusieurs tuiles sur la meme case */
	public final int[][] layer0, layer1, layer2;
	public final int[][][] layers;
	/** en cas d'Autotile anim� */
	private BufferedImage[] imagesCoucheSousHeros;
	/** en cas d'Autotile anim� */
	private BufferedImage[] imagesCoucheAvecHeros;
	/** en cas d'Autotile anim� */
	private BufferedImage[] imagesCoucheSurHeros;
	/** Le decor contient-il des autotiles anim�s ? */
	public boolean contientDesAutotilesAnimes;
	/** Brouillard affiche par dessus de decor et les Events */
	public Brouillard brouillard;
	/** liste des Events rang�s par Coordonnee y */
	public ArrayList<Event> events;
	/** hashmap des Events rang�s par id, 0 pour le Heros */
	public HashMap<Integer, Event> eventsHash;
	/** liste des Events a ajouter au tour suivant */
	public ArrayList<Event> eventsAAjouter = new ArrayList<Event>();
	/** Event Numero 0, dirige par le joueur */
	public Heros heros;
	/** Coordonnees du Heros (en pixels) a l'initialisation de la Map */
	public int xDebutHeros, yDebutHeros;
	/** Direction dans laquelle regarde le Heros a l'initialisation de la Map */
	public int directionDebutHeros;
	/** La case x;y est-elle passable ? */
	public Passabilite[][] casePassable;
	/** Pour faire d�filer la cam�ra ailleurs que centree sur le Heros */
	public final boolean defilementCameraX, defilementCameraY;
	/** Effet d'ondulation sur la Map */
	public Ondulation ondulation;
	
	/** Maps adjacentes a celle-ci */
	public HashMap<Integer, Adjacence> adjacences;
	
	/**
	 * Constructeur explicite
	 * @param numero de la Map, c'est-�-dire Numero du fichier map (au format JSON) a charger
	 * @param lecteur de la Map
	 * @param ancienHeros heros de la Map pr�c�dente
	 * @param brouillardForce brouillard impos� au chargement de partie
	 * @param positionInitiale [xDebutHeros, yDebutHerosArg, directionDebutHeros] ou bien [xAncienneMapHeros, yAncienneMapHeros, decalageDebutHeros, directionDebutHeros]
	 * @throws Exception Impossible de charger la Map
	 */
	public Map(final int numero, final LecteurMap lecteur, final Heros ancienHeros, final Brouillard brouillardForce, 
			final PositionInitiale positionInitiale) throws Exception {
		this.numero = numero;
		this.lecteur = lecteur;
		lecteur.map = this; //on pr�vient le Lecteur qu'il a une Map
		
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
		this.defilementCameraX = largeur > (Fenetre.LARGEUR_ECRAN/Main.TAILLE_D_UN_CARREAU);
		this.defilementCameraY = hauteur > (Fenetre.HAUTEUR_ECRAN/Main.TAILLE_D_UN_CARREAU);
		this.layer0 = recupererCouche(jsonMap, 0, this.largeur, this.hauteur);
		this.layer1 = recupererCouche(jsonMap, 1, this.largeur, this.hauteur);
		this.layer2 = recupererCouche(jsonMap, 2, this.largeur, this.hauteur);
		this.layers = new int[][][] {this.layer0, this.layer1, this.layer2};

		// Transition qui introduit cette Map
		final Transition transition = lecteur.transition;
		
		// Position initiale du Heros
		final int[] positionInitialeDuHeros = positionInitiale.calculer(this.largeur, this.hauteur, transition);
		this.xDebutHeros = positionInitialeDuHeros[0]; //position x (en pixels) initiale du Heros
		this.yDebutHeros = positionInitialeDuHeros[1]; //position y (en pixels) initiale du Heros
		this.directionDebutHeros = positionInitialeDuHeros[2]; //direction initiale du Heros
		
		// Maps adjacentes a celle-ci
		if (jsonMap.has("adjacences")) {
			this.adjacences = new HashMap<Integer, Adjacence>();
			final JSONArray adjacencesJsonArray = jsonMap.getJSONArray("adjacences");
			for (Object o : adjacencesJsonArray) {
				final JSONObject jsonAdjacence = (JSONObject) o;
				
				final Integer direction = Integer.valueOf(jsonAdjacence.getInt("direction"));
				final int decalage = jsonAdjacence.has("decalage") ? jsonAdjacence.getInt("decalage") : 0;
				final Transition transitionVersLAdjacence = jsonAdjacence.has("transition") 
						? Transition.parNom(jsonAdjacence.getString("transition")) 
						: Transition.DEFILEMENT;
				final Adjacence adjacence = new Adjacence(jsonAdjacence.getInt("numeroMap"), direction, decalage, transitionVersLAdjacence);
				this.adjacences.put(direction, adjacence);
				LOG.debug("Cette map a une adjacence dans la direction " + direction);
			}
		}
		
		long t0 = System.nanoTime();
		//chargement du tileset
		try {
			//si jamais le Tileset est le meme, pas la peine de le recreer
			final Tileset tilesetActuel = ((LecteurMap) Main.lecteur).map.tileset;
			if (this.nomTileset.equals(tilesetActuel.nom)) {
				this.tileset = tilesetActuel;
				LOG.info("Le Tileset n'a pas chang�, on garde le meme.");
			} else {
				throw new Exception("Le Tileset a chang�.");
			}
		} catch (Exception e1) {
			//impossible de convertir le Lecteur en LecteurMap car c'est un LecteurMenu
			//ou bien
			//le Lecteur actuel est null
			//ou bien
			//le Tileset a chang�
			try {
				LOG.info("Le Tileset a chang�, il faut le recharger.");
				this.tileset = new Tileset(this.nomTileset);
			} catch (IOException e2) {
				LOG.error("Erreur lors de la cr�ation du Tileset :", e2);
			}
		}
		long t1 = System.nanoTime();
			
		//on dessine la couche de decor inf�rieure, qui sera sous le Heros et les evenements
			this.imagesCoucheSousHeros = creerCoucheDeDecor(0, ALTITUDE_MEDIANE-1);
		//on dessine la couche de decor m�diane, qui sera avec le Heros et les evenements
			this.imagesCoucheAvecHeros = creerCoucheDeDecor(ALTITUDE_MEDIANE, ALTITUDE_MEDIANE);
		//on dessine la couche de decor superieure, qui sera au dessus du Heros et des evenements
			this.imagesCoucheSurHeros = creerCoucheDeDecor(ALTITUDE_MEDIANE+1, NOMBRE_ALTITUDES-1);
			long t2 = System.nanoTime();
			Main.mesuresDePerformance.add((t1-t0)+";"+(t2-t1));
			
		//importer les events
			importerLesEvenements(jsonMap, this.xDebutHeros, this.yDebutHeros, this.directionDebutHeros);
			
		//informations sur la Transition
			final int xAncienHeros = ancienHeros != null ? ancienHeros.x : 0; //pas d'ancien Heros en cas de chargement de Partie
			final int yAncienHeros = ancienHeros != null ? ancienHeros.y : 0;
			final int largeurAncienneMap = ancienHeros != null ? ancienHeros.map.largeur : 0;
			final int hauteurAncienneMap = ancienHeros != null ? ancienHeros.map.hauteur : 0;
			//TODO faire de calculerDirectionDefilement une Methode propre au type DEFILEMENT
			transition.direction = Transition.calculerDirectionDefilement(xAncienHeros, yAncienHeros, this.xDebutHeros, 
					this.yDebutHeros, largeurAncienneMap, hauteurAncienneMap, this.largeur, this.hauteur);
			//TODO faire une Methode calculerTransition() qui fait ceci pour ROND
			if (Transition.ROND.equals(transition)) {
				// Calcul du centre du rond
				final int[] ancienneCamera = ancienHeros.map.lecteur.recupererCamera();
				transition.xHerosAvant = ancienHeros.x + Heros.LARGEUR_HITBOX_PAR_DEFAUT/2 - ancienneCamera[0];
				transition.yHerosAvant = ancienHeros.y + Heros.HAUTEUR_HITBOX_PAR_DEFAUT/2 - ancienneCamera[1];
				final int[] nouvelleCamera = this.lecteur.recupererCamera();
				transition.xHerosApres = xDebutHeros + Heros.LARGEUR_HITBOX_PAR_DEFAUT/2 - nouvelleCamera[0];
				transition.yHerosApres = yDebutHeros + Heros.HAUTEUR_HITBOX_PAR_DEFAUT/2 - nouvelleCamera[1];
			}
			
		// TODO ce correctif sur la position initiale du Heros aurait sa place dans la Methode importerLesEvenements()
		//correctif sur la position x y initiale du Heros
		//le Heros n'est pas forc�ment pile sur le coin haut-gauche du carreau t�l�porteur
			if (Transition.ROND.equals(transition)) {
				// Si c'est une Transition ronde, on recentre le Heros sur la case
				this.heros.x = this.heros.x - (this.heros.x % Main.TAILLE_D_UN_CARREAU) + Main.TAILLE_D_UN_CARREAU/2 - this.heros.largeurHitbox/2;
				this.heros.y = this.heros.y - (this.heros.y % Main.TAILLE_D_UN_CARREAU) + Main.TAILLE_D_UN_CARREAU/2 - this.heros.hauteurHitbox/2;
			} else if (positionInitialeDuHeros.length == POSITION_INITIALE_PAR_X_Y_ET_DIRECTION) {
				// Si c'est un d�filement, on reporte le decalage de la Map pr�c�dente
				final int ecartX = positionInitialeDuHeros[3];
				final int ecartY = positionInitialeDuHeros[4];
				if (transition.direction == Direction.BAS || transition.direction == Direction.HAUT) {
					this.heros.x += ecartX;
				} else {
					this.heros.y += ecartY;
				}
			}
			
		//cr�ation de la liste des cases passables
			creerListeDesCasesPassables();
		
		//panorama
			this.panoramaActuel = this.tileset.imagePanorama;
			this.parallaxeActuelle = this.tileset.parallaxe;
			
		//cr�ation du Brouillard
			if (brouillardForce != null) {
				// chargement du pr�c�dent Brouillard sauvegard�
				this.brouillard = brouillardForce;
			} else {
				// arriv�e sur la Map donc chargement du Brouillard natif
				this.brouillard = Brouillard.creerBrouillardAPartirDeJson(jsonMap);
				if (this.brouillard == null) {
					this.brouillard = this.tileset.brouillard;
				}
			}
	}
	
	/**
	 * L'affichage est un sandwich : une partie du decor est affichee par dessus les Events, une autre dessous, et une autre au meme niveau.
	 * @param altitudeMin premiere altitude de cette couche de decor
	 * @param altitudeMax derni�re altitude (incluse) de cette couche de decor
	 * @return vignettes de la couche de decor
	 */
	private BufferedImage[] creerCoucheDeDecor(final int altitudeMin, final int altitudeMax) {
		final BufferedImage[] vignettesDeCetteCouche = new BufferedImage[Autotile.NOMBRE_VIGNETTES_AUTOTILE_ANIME];
		
		final long t0 = System.nanoTime();
		
		final int largeurPixel = this.largeur*Main.TAILLE_D_UN_CARREAU;
		final int hauteurPixel = this.hauteur*Main.TAILLE_D_UN_CARREAU;
		
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
							//case non-vide, il y a quelque chose a dessiner
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
		Main.mesuresDePerformance.add(Long.valueOf(t1 - t0).toString());
		return vignettesDeCetteCouche;
	}
	
	/**
	 * Dessine a l'ecran un carreau du Tileset aux Coordonnees (xEcran;yEcran).
	 * @warning Ne pas oublier de recuperer le r�sultat de cette Methode.
	 * @param ecran sur lequel on doit dessiner un carreau
	 * @param xEcran position x Ou dessiner le carreau a l'ecran
	 * @param yEcran position y Ou dessiner le carreau a l'ecran
	 * @param numeroCarreau Numero du carreau a dessiner
	 * @param tilesetUtilise Tileset utilis� pour interpreter le decor de la Map
	 */
	public final void dessinerCarreau(final BufferedImage ecran, final int xEcran, final int yEcran, final int numeroCarreau, final Tileset tilesetUtilise) {
		final BufferedImage dessinCarreau = tilesetUtilise.carreaux[numeroCarreau];
		Graphismes.superposerImages(ecran, dessinCarreau, xEcran*Main.TAILLE_D_UN_CARREAU, yEcran*Main.TAILLE_D_UN_CARREAU);
		//TODO essayer la Methode superposerPortion, plus rapide ?
		/*final int xTile = (numeroCarreau%8) * Main.TAILLE_D_UN_CARREAU;
		final int yTile = (numeroCarreau/8) * Main.TAILLE_D_UN_CARREAU;
		Graphismes.superposerPortionDImage(ecran, tilesetUtilise.image, xEcran, yEcran, xTile, yTile);*/
	}
	
	/**
	 * Dessiner a l'ecran un carreau issu d'un autotile.
	 * @warning Ne pas oublier de recuperer le r�sultat de cette Methode.
	 * @param decorAnime partie anim�e du decor (� peindre dans le cas d'un Autotile anim�)
	 * @param x Coordonnee x du carreau sur la Map
	 * @param y Coordonnee y du carreau sur la Map
	 * @param numeroCarreau Numero de l'autotile (Numero n�gatif)
	 * @param tilesetUtilise Tileset utilis� pour interpreter le decor de la Map
	 * @param layer couche de decor a laquelle appartient le carreau
	 * @return nombre de vignettes n�cessaires pour constituer le decor �ventuellement anim�
	 */
	public final int dessinerAutotile(final BufferedImage[] decorAnime, final int x, final int y,
			final int numeroCarreau, final Tileset tilesetUtilise, final int[][] layer) {
		final Autotile autotile = tilesetUtilise.autotiles.get(numeroCarreau);
		
		//on pr�vient la Map qu'elle aura un decor anim�
		if (autotile.anime && !this.contientDesAutotilesAnimes) {
			this.contientDesAutotilesAnimes = true;
			LOG.debug("Cette map contient des autotiles anim�s.");
		}
		
		// on cree les vignettes manquantes pour l'animation du decor
		if (this.contientDesAutotilesAnimes && decorAnime[Autotile.NOMBRE_VIGNETTES_AUTOTILE_ANIME - 1] == null) {
			for (int i = 1; i<Autotile.NOMBRE_VIGNETTES_AUTOTILE_ANIME; i++) {
				decorAnime[i] = Graphismes.clonerUneImage(decorAnime[0]);
			}
			LOG.debug("Cr�ation des differentes vignettes d'animation du decor.");
		}
		
		final int nombreDeVignettes = this.contientDesAutotilesAnimes ? Autotile.NOMBRE_VIGNETTES_AUTOTILE_ANIME : 1;
		
		//on calcule l'apparence du carreau Autotile
		final BufferedImage[] dessinCarreau = autotile.calculerAutotile(x, y, this.largeur, this.hauteur, numeroCarreau, layer);
		
		if (autotile.anime) {
			//decor anim� : on dessine les 4 �tapes de l'animation du decor
			for (int i = 0; i<nombreDeVignettes; i++) {
				Graphismes.superposerImages(decorAnime[i], dessinCarreau[i], x*Main.TAILLE_D_UN_CARREAU, y*Main.TAILLE_D_UN_CARREAU);
			}
		} else {
			//decor fixe
			for (int i = 0; i<nombreDeVignettes; i++) {
				Graphismes.superposerImages(decorAnime[i], dessinCarreau[0], x*Main.TAILLE_D_UN_CARREAU, y*Main.TAILLE_D_UN_CARREAU);
			}
		}
		
		return nombreDeVignettes;
	}

	/**
	 * Constitue la liste des Events de la Map en allant lire dans le fichier JSON decrivant la Map.
	 * @param jsonMap objet JSON decrivant la Map
	 * @param xDebutHeros position initiale (en pixels) du Heros sur la Map
	 * @param yDebutHeros position initiale (en pixels) du Heros sur la Map
	 * @param directionDebutHeros direction initiale du Heros sur la Map
	 */
	private void importerLesEvenements(final JSONObject jsonMap, final int xDebutHeros, final int yDebutHeros, final int directionDebutHeros) {
		try {
			this.events = new ArrayList<Event>();
			this.eventsHash = new HashMap<Integer, Event>();
			//d'abord le Heros
			this.heros = new Heros(this.xDebutHeros, this.yDebutHeros, this.directionDebutHeros, this);
			this.events.add(heros);
			//this.eventsHash.put(0, heros);
			//puis les autres
			final JSONArray jsonEvents = jsonMap.getJSONArray("events");
			Event.recupererLesEvents(this.events, jsonEvents, this);
		} catch (Exception e3) {
			LOG.error("Erreur lors de la constitution de la liste des events :", e3);
		}
		
		// Numerotation des Events
		for (Event event : this.events) {
			event.map = this;
			if (this.eventsHash.containsKey(event.id)) { //la Numerotation des Events comporte un doublon !
				LOG.error("CONFLIT : les events "+this.eventsHash.get(event.id).nom+" et "+event.nom+" portent le meme id : "+event.id);
			}
			this.eventsHash.put(event.id, event);
		}
		
		// reinitialisation des interrupteurs locaux (si besoin)
		for (Event eventAReinitialiser : this.events) {
			if (eventAReinitialiser.reinitialiser) {
				ModifierInterrupteurLocal.reinitialiserEvent(eventAReinitialiser);
			}
		}
	}

	/**
	 * Cr�ation d'un tableau pour connaitre les passabilites de la Map plus rapidement par la suite.
	 */
	private void creerListeDesCasesPassables() {
		this.casePassable = new Passabilite[this.largeur][this.hauteur];
		int[][] couche;
		int numeroDeLaCaseDansLeTileset = -1;
		for (int i = 0; i<this.largeur; i++) {
			for (int j = 0; j<this.hauteur; j++) {
				
				// On cherche la plus haute couche dont le tile a une altitude de 0
				int coucheDeBase = -1;
				for (int k = 0; k<NOMBRE_LAYERS; k++) {
					couche = layers[k]; //couche de decor
					numeroDeLaCaseDansLeTileset = couche[i][j];
					if (this.tileset.altitudeDeLaCase(numeroDeLaCaseDansLeTileset) == 0) {
						coucheDeBase = k;
					}
				}
				// Ce tile servira de base au calcul de la passabilite
				final Passabilite passabiliteDeBase;
				if (coucheDeBase == -1) {
					passabiliteDeBase = Passabilite.PASSABLE;
				} else {
					final int tuileDeBase = layers[coucheDeBase][i][j];
					passabiliteDeBase = this.tileset.passabiliteDeLaCase(tuileDeBase);
				}
				this.casePassable[i][j] = passabiliteDeBase;
				
				// De cette passabilite de base, on va soustraire les obstacles
				int tuileSuivante;
				Passabilite passabiliteSuivante;
				for (int k = coucheDeBase+1; k<NOMBRE_LAYERS; k++) {
					couche = layers[k];
					if (this.tileset.altitudeDeLaCase(numeroDeLaCaseDansLeTileset) > 0) {
						tuileSuivante = couche[i][j];
						passabiliteSuivante = this.tileset.passabiliteDeLaCase(tuileSuivante);
						this.casePassable[i][j] = Passabilite.intersection(this.casePassable[i][j], passabiliteSuivante);
					}
				}
				// Voil�
			}
		}
	}

	/**
	 * Va chercher une couche de decor en particulier dans le fichier JSON qui repr�sente la Map.
	 * @param jsonMap objet JSON repr�sentant la map
	 * @param numeroCouche Numero de la couche a r�cuperer
	 * @param largeur de la map
	 * @param hauteur de la map
	 * @return un tableau bidimentionnel contenant le decor situe sur cette couche
	 */
	public static int[][] recupererCouche(final JSONObject jsonMap, final int numeroCouche, final int largeur, final int hauteur) {
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
	 * L'Event sera supprime a la fin de la boucle d'affichage.
	 * @param idEventASupprimer Numero de l'Event qu'il faut inscrire a la suppression
	 * @return bool�en pour savoir si l'Event a supprimer a bien ete trouv� dans la liste des evenements
	 */
	public final boolean supprimerEvenement(final int idEventASupprimer) {
		for (Event event : this.events) {
			if (event.id == idEventASupprimer) {
				event.supprime = true;
				return true;
			}
		}
		LOG.warn("L'evenement a supprimer id:"+idEventASupprimer+" n'a pas ete trouv� dans la liste.");
		return false;
	}

	/**
	 * Obtenir le decor a afficher par dessus les Events.
	 * Ce decor est compos� du decor fixe et d'�ventuels autotiles anim�s.
	 * @param vignetteAutotileActuelle vignette de l'Autotile a afficher
	 * @return decor superieur, avec l'autotile d�pendant de la frame
	 */
	public final BufferedImage getImageCoucheDecorSuperieur(final int vignetteAutotileActuelle) {
		if (this.imagesCoucheSurHeros[1] != null) {
			return this.imagesCoucheSurHeros[vignetteAutotileActuelle];
		} else {
			return this.imagesCoucheSurHeros[0];
		}
	}
	
	/**
	 * Obtenir le decor a afficher au meme niveau que les Events.
	 * Ce decor est compos� du decor fixe et d'�ventuels autotiles anim�s.
	 * Il doit etre d�coup� en bandelettes pour s'intercaler avec les Events.
	 * @param vignetteAutotileActuelle vignette de l'Autotile a afficher
	 * @param debutBandelette a d�couper
	 * @param finBandelette a d�couper
	 * @return bandelette de decor m�dian, avec l'autotile d�pendant de la frame
	 */
	public final BufferedImage getImageCoucheDecorMedian(final int vignetteAutotileActuelle, final int debutBandelette, final int finBandelette) {
		final BufferedImage vignette;
		if (this.imagesCoucheAvecHeros[1] != null) {
			vignette = this.imagesCoucheAvecHeros[vignetteAutotileActuelle];
		} else {
			vignette = this.imagesCoucheAvecHeros[0];
		}
		// Quelle est la hauteur de la bandelette a decouper ?
		final int debutDecoupageY = debutBandelette*Main.TAILLE_D_UN_CARREAU;
		int hauteurBandelette = (finBandelette-debutBandelette)*Main.TAILLE_D_UN_CARREAU;
		// TODO avant il y avait aussi un "-1" ci-dessous 
		// mais je l'ai retir� 
		// parce qu'il produisait une ligne de pixels manquante tout en bas de l'ecran
		// a voir si dans le futur si ca casse quelque chose d'autre
		final int hauteurPossibleDeDecouper = vignette.getHeight() - debutDecoupageY; 
		hauteurBandelette = Maths.min(hauteurBandelette, hauteurPossibleDeDecouper);
		hauteurBandelette = Maths.max(hauteurBandelette, 0); // pas de bandelette de hauteur n�gative
		// D�couper la bandelette de decor m�dian
		try {
			return vignette.getSubimage(0, debutDecoupageY, vignette.getWidth(), hauteurBandelette);
		} catch (RasterFormatException e) {
			/*LOG.error("La bandelette de decor m�dian a d�couper d�passe de la vignette source !"
					+ " hauteur de la vignette ("+vignette.getHeight()
					+ ") < d�but du d�coupage ("+debutDecoupageY
					+ ") + hauteur de la bandelette ("+hauteurBandelette+")", e);
			throw e;*/
			return null;
		}
	}

	/**
	 * Obtenir le decor a afficher en dessous des Events.
	 * Ce decor est compos� du decor fixe et d'�ventuels autotiles anim�s.
	 * @param vignetteAutotileActuelle frame actuelle du Lecteur
	 * @return decor inf�rieur, avec l'autotile d�pendant de la frame
	 */
	public final BufferedImage getImageCoucheDecorInferieur(final int vignetteAutotileActuelle) {
		if (this.imagesCoucheSousHeros[1] != null) {
			return this.imagesCoucheSousHeros[vignetteAutotileActuelle];
		} else {
			return this.imagesCoucheSousHeros[0];
		}
	}
	
	/**
	 * Calcule si on peut poser un Event sur la Map a cet endroit-l�.
	 * Ne pas utiliser cette Methode si l'Event a poser est traversable, car la r�ponse est forc�ment oui.
	 * @param xmin position x (en pixels) de l'Event qu'on veut poser
	 * @param ymin position y (en pixels) de l'Event qu'on veut poser
	 * @param largeurHitbox largeur de l'Event a poser
	 * @param hauteurHitbox hauteur de l'Event a poser
	 * @param numeroEvent Numero de l'Event a poser
	 * @return true si on peut poser un nouvel Event ici, false sinon
	 */
	public final boolean calculerSiLaPlaceEstLibre(final int xmin, final int ymin, final int largeurHitbox, final int hauteurHitbox, final int numeroEvent) {
		final Event event = this.eventsHash.get((Integer) numeroEvent);
		final int xmax = xmin + largeurHitbox;
		final int ymax = ymin + hauteurHitbox;
		
		// Est-ce que le decor permet de passer ici ?
		if (!lEventEstSurUnDecorTraversable(event, xmin, ymin, xmax, ymax)) {
			// Le decor n'est pas traversable ici !
			// La place n'est pas libre !
			return false;
		}
		
		// Est-ce que les autres Events permettent de passer ici ?
		if (!lEventNEmpietePasSurUnAutreEvent(event, xmin, ymin, xmax, ymax, largeurHitbox, hauteurHitbox, numeroEvent)) {
			// L'Event empi�te sur un autre Event !
			// La place n'est pas libre !
			return false;
		}
		
		// La place est libre a cet endroit
		return true;
	}
	
	/**
	 * Est-ce qu'on peut poser l'Event ici malgre le decor ?
	 * @param event qu'on voudrait placer ici
	 * @param xmin Coordonnee (en pixels) de la ou on veut placer l'Event
	 * @param ymin Coordonnee (en pixels) de la ou on veut placer l'Event
	 * @param xmax Coordonnee (en pixels) de la ou on veut placer l'Event
	 * @param ymax Coordonnee (en pixels) de la ou on veut placer l'Event
	 * @return true si le decor est traversable ici, false sinon.
	 */
	public final boolean lEventEstSurUnDecorTraversable(final Event event, final int xmin, final int ymin, final int xmax, final int ymax) {
		
		/** Coordonnees (en nombre de cases) des cases ou se situent les coins de l'Event */
		final int xCaseMin = xmin/Main.TAILLE_D_UN_CARREAU;
		final int yCaseMin = ymin/Main.TAILLE_D_UN_CARREAU;
		final int xCaseMax = (xmax-1)/Main.TAILLE_D_UN_CARREAU;
		final int yCaseMax = (ymax-1)/Main.TAILLE_D_UN_CARREAU;
		try {
			// On considerera l'Event comme solide durant le calcul
			// Si tel n'est pas le cas, on affiche une alerte
			if (Passabilite.estMultilateral(event.traversableActuel)) {
				LOG.warn("L'event a une passabilite multilat�rale ! "
						+ "Impossible de calculer ses collisions avec le decor ! "
						+ "Il sera trait� comme solide.");
			}
			
			// Remarque : ces 4 premiers cas purs sont des cas particuliers des 4 cas de chevauchement
			//aucun des 4 coins de l'Event ne doivent etre sur une case non passable
			if (this.casePassable[xCaseMin][yCaseMin] == Passabilite.OBSTACLE) {
				return false;
			}
			if (this.casePassable[xCaseMax][yCaseMin] == Passabilite.OBSTACLE) {
				return false;
			}
			if (this.casePassable[xCaseMin][yCaseMax] == Passabilite.OBSTACLE) {
				return false;
			}
			if (this.casePassable[xCaseMax][yCaseMax] == Passabilite.OBSTACLE) {
				return false;
			}
			
			//chevauchement entre deux cases et passabilites directionnelles
			/** Coordonnees (en nombre de cases) de la case Ou se situe le centre de l'Event */
			final int xCaseCentre = ((xmin+xmax)/2) / Main.TAILLE_D_UN_CARREAU;
			final int yCaseCentre = ((ymin+ymax)/2) / Main.TAILLE_D_UN_CARREAU;
			/** l'Event chevauche-t-il deux cases ? */
			final boolean chevauchementAGauche = (xCaseCentre != xCaseMin);
			final boolean chevauchementADroite = (xCaseCentre != xCaseMax);
			final boolean chevauchementEnHaut = (yCaseCentre != yCaseMin);
			final boolean chevauchementEnBas = (yCaseCentre != yCaseMax);
			if (chevauchementAGauche 
					&& (!this.casePassable[xCaseCentre][yCaseCentre].passableAGauche 
					|| !this.casePassable[xCaseMin][yCaseCentre].passableADroite)) {
				return false;
			}
			if (chevauchementADroite 
					&& (!this.casePassable[xCaseCentre][yCaseCentre].passableADroite 
					|| !this.casePassable[xCaseMax][yCaseCentre].passableAGauche)) {
				return false;
			}
			if (chevauchementEnHaut
					&& (!this.casePassable[xCaseCentre][yCaseCentre].passableEnHaut 
					|| !this.casePassable[xCaseCentre][yCaseMin].passableEnBas)) {
				return false;
			}
			if (chevauchementEnBas 
					&& (!this.casePassable[xCaseCentre][yCaseCentre].passableEnBas 
					|| !this.casePassable[xCaseCentre][yCaseMax].passableEnHaut)) {
				return false;
			}
		} catch (Exception e) {
			//l'Event sort de la Map !
			
			if (!event.sortiDeLaMap) { //on n'affiche le message d'erreur qu'une fois
				LOG.warn("L'event "+event.id+" ("+event.nom+") est sorti de la map !"); //TODO ni le Numero, ni le nom ne semblent correspondre a l'Event qui sort
				LOG.trace(e);
			}
			event.sortiDeLaMap = true;
		}
		
		return true;
	}
	
	/**
	 * Est-ce qu'on peut poser l'Event ici malgre les autres Events ?
	 * @param event qu'on voudrait placer ici
	 * @param xmin Coordonnee (en pixel) de la Ou on veut placer l'Event
	 * @param ymin Coordonnee (en pixel) de la Ou on veut placer l'Event
	 * @param xmax Coordonnee (en pixel) de la Ou on veut placer l'Event
	 * @param ymax Coordonnee (en pixel) de la Ou on veut placer l'Event
	 * @param largeurHitbox largeur (en pixels) de l'Event
	 * @param hauteurHitbox largeur (en pixels) de l'Event
	 * @param numeroEvent Numero de l'Event
	 * @return true si les Events alentours font suffisamment de place pour cet Event, false sinon.
	 */
	private boolean lEventNEmpietePasSurUnAutreEvent(final Event event, final int xmin, final int ymin, 
			final int xmax, final int ymax, final int largeurHitbox, final int hauteurHitbox, final int numeroEvent) {
		//si rencontre avec un autre Event non traversable -> false
		int xmin2;
		int xmax2;
		int ymin2;
		int ymax2;
		for (Event autreEvent : this.events) {
			
			// Il ne faut pas qu'un Event interf�re avec lui-meme
			if (numeroEvent != autreEvent.id) {
				// Ce n'est pas le meme Event
				
				// Y a-t-il un choc physique ?
				final boolean modeTraversable = 
						event.traversableActuel == Passabilite.PASSABLE
						|| autreEvent.traversableActuel == Passabilite.PASSABLE 
						// si l'un des deux est le Heros
						|| (event.id == 0 && autreEvent.traversableParLeHerosActuel)
						|| (autreEvent.id == 0 && event.traversableParLeHerosActuel);
				
				if (!modeTraversable) {
					// Il y a potentiellement un choc physique
					
					xmin2 = autreEvent.x;
					xmax2 = autreEvent.x + autreEvent.largeurHitbox;
					ymin2 = autreEvent.y;
					ymax2 = autreEvent.y + autreEvent.hauteurHitbox;
					
					if (event.traversableActuel == Passabilite.OBSTACLE 
							&& autreEvent.traversableActuel == Passabilite.OBSTACLE) {
						// Les deux Events sont solides
						
						// Les deux Events se chevauchent-ils ?
						if (Hitbox.lesDeuxRectanglesSeChevauchent(xmin, xmax, ymin, ymax, 
							xmin2, xmax2, ymin2, ymax2, 
							largeurHitbox, hauteurHitbox, 
							autreEvent.largeurHitbox, autreEvent.hauteurHitbox) 
						) {
							return false;
						}
						
					} else if (Passabilite.estMultilateral(event.traversableActuel)
							|| Passabilite.estMultilateral(autreEvent.traversableActuel)) {
						// passabilites multilat�rales
						if (Hitbox.lesDeuxRectanglesSeChevauchentMultilateralement(
								xmin, xmax, ymin, ymax, xmin2, xmax2, ymin2, ymax2,
								event.traversableActuel.passableAGauche, event.traversableActuel.passableADroite, 
								event.traversableActuel.passableEnBas, event.traversableActuel.passableEnHaut, 
								autreEvent.traversableActuel.passableAGauche, autreEvent.traversableActuel.passableADroite, 
								autreEvent.traversableActuel.passableEnBas, autreEvent.traversableActuel.passableEnHaut, 
								event.largeurHitbox, autreEvent.largeurHitbox, event.hauteurHitbox, autreEvent.hauteurHitbox)) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}
	
	/**
	 * Inventer un id pour un nouvel Event de eventHash.
	 * @return id in�dit
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
	 * Si le Heros sort de la Map, il va vers une �ventuelle Map adjacente automatiquement.
	 */
	public void sortirVersLaMapAdjacente() {
		if (this.adjacences != null && !this.adjacences.isEmpty()) {
			Adjacence adjacence = null;
			if (heros.x < 0) {
				// Sortie par la gauche
				adjacence = this.adjacences.get((Integer) Direction.GAUCHE);
			} else if (heros.x > this.largeur*Main.TAILLE_D_UN_CARREAU - Heros.LARGEUR_HITBOX_PAR_DEFAUT) {
				// Sortie par la droite
				adjacence = this.adjacences.get((Integer) Direction.DROITE);
			} else if (heros.y < 0) {
				// Sortie par le haut
				adjacence = this.adjacences.get((Integer) Direction.HAUT);
			} else if (heros.y > this.hauteur*Main.TAILLE_D_UN_CARREAU - Heros.HAUTEUR_HITBOX_PAR_DEFAUT) {
				// Sortie par le bas
				adjacence = this.adjacences.get((Integer) Direction.BAS);
			} else {
				// Le Heros est encore dans la Map
				return;
			}
			
			if (adjacence != null) {
				adjacence.allerALaMapAdjacente(this.heros);
			}
		}
	}
	
	/**
	 * Le Heros est-il en train d'entrer par une porte ?
	 * @param xHerosMapPrecedente Coordonnee x (en carreaux) du Heros sur l'ancienne Map
	 * @param yHerosMapPrecedente Coordonnee y (en carreaux) du Heros sur l'ancienne Map
	 * @return true si le Heros passe par une porte, false sinon
	 */
	public boolean leHerosEntreParUnePorte(final int xHerosMapPrecedente, final int yHerosMapPrecedente) {
		boolean onATrouveUnePorte = false;
		rechercheDePorteAutour: for (int i = -1; i<=1; i++) {
			for (int j = -1; j<=1; j++) {
				try {
					final Integer carreauDuHeros0 = this.layer0[xHerosMapPrecedente + i][yHerosMapPrecedente + j];
					if (this.tileset.portes.contains(carreauDuHeros0)) {
						// On a trouv� une porte a proximite !
						onATrouveUnePorte = true;
						break rechercheDePorteAutour;
					}
					final Integer carreauDuHeros1 = this.layer1[xHerosMapPrecedente + i][yHerosMapPrecedente + j];
					if (this.tileset.portes.contains(carreauDuHeros1)) {
						// On a trouv� une porte a proximite !
						onATrouveUnePorte = true;
						break rechercheDePorteAutour;
					}
					final Integer carreauDuHeros2 = this.layer2[xHerosMapPrecedente + i][yHerosMapPrecedente + j];
					if (this.tileset.portes.contains(carreauDuHeros2)) {
						// On a trouv� une porte a proximite !
						onATrouveUnePorte = true;
						break rechercheDePorteAutour;
					}
				} catch (ArrayIndexOutOfBoundsException e) {
					// on sort de la map
				}
			}
		}
		LOG.info(onATrouveUnePorte ? "Le Heros est entr� par une porte." : "Le Heros n'est pas entr� par une porte.");
		return onATrouveUnePorte;
	}

	@Override
	public final JSONObject sauvegarderEnJson() {
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
