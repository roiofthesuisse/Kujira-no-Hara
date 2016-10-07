package map;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import main.Fenetre;
import utilitaire.Graphismes;
import utilitaire.InterpreteurDeJson;

/**
 * Une Map est un décor rectangulaire constitué de briques issues du Tileset.
 * Le Heros et les Events se déplacent dedans.
 */
public class Map {
	//constantes
	/** Chaque carreau du Tileset possède une altitude intrinsèque */
	private static final int NOMBRE_ALTITUDES = 6;
	/** Certaines altitudes sont affichées sous le Héros */
	private static final int NOMBRE_ALTITUDES_SOUS_HEROS = 2;
	/** Certaines altitudes sont affichées au dessus du Héros */
	private static final int NOMBRE_ALTITUDES_SUR_HEROS = NOMBRE_ALTITUDES - NOMBRE_ALTITUDES_SOUS_HEROS;
	/** Le décor est constitué de 3 couches, afin de pouvoir superposer plusieurs carreaux au même endroit de la Map */
	private static final int NOMBRE_LAYERS = 3;
	
	public final int numero;
	public LecteurMap lecteur;
	public String nom;
	public String nomBGM; //musique
	public String nomBGS; //fond sonore
	public String nomTileset;
	public Tileset tileset; //image contenant les decors
	public final int largeur;
	public final int hauteur;
	public final int[][] layer0; //couche du sol
	public final int[][] layer1; //couche de decor 1
	public final int[][] layer2; //couche de decor 2
	public final int[][][] layers;
	public BufferedImage imageCoucheSousHeros;
	public BufferedImage imageCoucheSurHeros;
	public boolean contientDesAutotilesAnimes;
	public Brouillard brouillard;
	/** liste des Events rangés par coordonnée y */
	public ArrayList<Event> events;
	/** hashmap des Events rangés par id, 0 pour le Héros */
	public HashMap<Integer, Event> eventsHash;
	public Heros heros;
	public int xDebutHeros;
	public int yDebutHeros;
	public int directionDebutHeros;
	public boolean[][] casePassable;
	public final boolean defilementCameraX;
	public final boolean defilementCameraY;
	
	/**
	 * Constructeur explicite
	 * @param numero de la Map, c'est-à-dire numéro du fichier map (au format JSON) à charger
	 * @param lecteur de la Map
	 * @param xDebutHerosArg position x du Heros à son arrivée sur la Map
	 * @param yDebutHerosArg position y du Heros à son arrivée sur la Map
	 * @param directionDebutHeros direction du Heros à son arrivée sur la Map
	 * @throws FileNotFoundException 
	 */
	public Map(final int numero, final LecteurMap lecteur, final int xDebutHerosArg, final int yDebutHerosArg, final int directionDebutHeros) throws FileNotFoundException {
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
		this.xDebutHeros = xDebutHerosArg;
		this.yDebutHeros = yDebutHerosArg;
		this.directionDebutHeros = directionDebutHeros;
		
		//chargement du tileset
		try {
			//si jamais le Tileset est le même, pas la peine de le recréer
			final Tileset tilesetActuel = ((LecteurMap) Fenetre.getFenetre().lecteur).tilesetActuel;
			if (this.nomTileset.equals(tilesetActuel.nom)) {
				this.tileset = tilesetActuel;
				System.out.println("Le Tileset n'a pas changé, on garde le même.");
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
				System.out.println("Le Tileset a changé, il faut le recharger.");
				this.tileset = new Tileset(this.nomTileset);
			} catch (IOException e2) {
				System.err.println("Erreur lors de la création du Tileset :");
				e2.printStackTrace();
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
			
		//création du Brouillard
			this.brouillard = Brouillard.creerBrouillardAPartirDeJson(jsonMap);

	}

	/**
	 * L'affichage est un sandwich : une partie du décor est en fond, et les Events sont affichés par dessus.
	 * On affiche en premier le décor arrière.
	 */
	private void creerImageDuDecorEnDessousDuHeros() {
			final BufferedImage[] couches = new BufferedImage[NOMBRE_ALTITUDES_SOUS_HEROS];
			for (int i = 0; i<NOMBRE_ALTITUDES_SOUS_HEROS; i++) {
				couches[i] = lecteur.imageVide(largeur*Fenetre.TAILLE_D_UN_CARREAU, hauteur*Fenetre.TAILLE_D_UN_CARREAU);
			}
			
			int numeroCarreau;
			int altitudeCarreau;
			for (int i = 0; i<largeur; i++) {
				for (int j = 0; j<hauteur; j++) {
					for (int k = 0; k<NOMBRE_LAYERS; k++) {
						final int[][] layer = layers[k];
						try {
							numeroCarreau = layer[i][j];
							altitudeCarreau = this.tileset.altitude[numeroCarreau];
							if (altitudeCarreau<NOMBRE_ALTITUDES_SOUS_HEROS) {
								couches[altitudeCarreau] = this.lecteur.dessinerCarreau(couches[altitudeCarreau], i, j, numeroCarreau, tileset);
							}
						} catch (ArrayIndexOutOfBoundsException e) {
							//case vide
						}
					}
				}
			}
			for (int i = 1; i<NOMBRE_ALTITUDES_SOUS_HEROS; i++) {
				couches[0] = Graphismes.superposerImages(couches[0], couches[i], 0, 0);
			}
			this.imageCoucheSousHeros = couches[0];
	}

	/**
	 * L'affichage est un sandwich : une partie du décor est affichée par dessus les Events.
	 * On affiche en dernier le décor supérieur.
	 */
	private void creerImageDuDecorAuDessusDuHeros() {
		final BufferedImage[] couches = new BufferedImage[NOMBRE_ALTITUDES_SUR_HEROS];
		for (int i = 0; i<NOMBRE_ALTITUDES_SUR_HEROS; i++) {
			couches[i] = lecteur.imageVide(largeur*Fenetre.TAILLE_D_UN_CARREAU, hauteur*Fenetre.TAILLE_D_UN_CARREAU);
		}
		
		int numeroCarreau;
		int altitudeCarreau;
		for (int i = 0; i<largeur; i++) {
			for (int j = 0; j<hauteur; j++) {
				for (int k = 0; k<NOMBRE_LAYERS; k++) {
					final int[][] layer = layers[k];
					try {
						numeroCarreau = layer[i][j];
						altitudeCarreau = this.tileset.altitude[numeroCarreau];
						if (altitudeCarreau>=NOMBRE_ALTITUDES_SOUS_HEROS) {
							couches[altitudeCarreau-NOMBRE_ALTITUDES_SOUS_HEROS] = this.lecteur.dessinerCarreau(couches[altitudeCarreau-NOMBRE_ALTITUDES_SOUS_HEROS], i, j, numeroCarreau, tileset);
						}
					} catch (ArrayIndexOutOfBoundsException e) {
						//case vide
					}
				}
			}
		}
		for (int i = 1; i<NOMBRE_ALTITUDES_SUR_HEROS; i++) {
			couches[0] = Graphismes.superposerImages(couches[0], couches[i], 0, 0);
		}
		this.imageCoucheSurHeros = couches[0];
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
			this.heros = new Heros(this.xDebutHeros, this.yDebutHeros, this.directionDebutHeros);
			this.events.add(heros);
			this.eventsHash.put(0, heros);
			//puis les autres
			final JSONArray jsonEvents = jsonMap.getJSONArray("events");
			InterpreteurDeJson.recupererLesEvents(this.events, jsonEvents);
		} catch (Exception e3) {
			System.err.println("Erreur lors de la constitution de la liste des events :");
			e3.printStackTrace();
		}
		//numérotation des Events
		final int nombreDEvents = this.events.size();
		for (int i = 0; i<nombreDEvents; i++) {
			this.events.get(i).map = this;
			this.events.get(i).numero = i;
			this.eventsHash.put(this.events.get(i).id, this.events.get(i));
		}
	}

	/**
	 * Création d'un tableau pour connaitre les passabilités de la Map plus rapidement par la suite.
	 */
	private void creerListeDesCasesPassables() {
		this.casePassable = new boolean[this.largeur][this.hauteur];
		boolean passable;
		int numeroDeLaCaseDansLeTileset;
		for (int i = 0; i<this.largeur; i++) {
			for (int j = 0; j<this.hauteur; j++) {
				passable = true;
				this.casePassable[i][j] = true;
				for (int k = 0; k<NOMBRE_LAYERS&&passable; k++) { //si on en trouve une de non passable, on ne cherche pas les autres couches
					final int[][] layer = layers[k];
					numeroDeLaCaseDansLeTileset = layer[i][j];
					if (numeroDeLaCaseDansLeTileset!=-1 && !this.tileset.passabilite[numeroDeLaCaseDansLeTileset]) {
						this.casePassable[i][j] = false;
						passable = false;
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
				try {
					couche[i][j] = Integer.parseInt((String) ligne.get(i));
				} catch (ClassCastException e) {
					couche[i][j] = (int) ligne.get(i);
				}
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
		System.out.println("L'évènement à supprimer numéro "+numeroEventASupprimer+" n'a pas été trouvé dans la liste.");
		return false;
	}
	
}
