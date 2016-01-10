package map;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import main.Fenetre;
import utilitaire.InterpreteurDeJson;

/**
 * Une Map est un décor rectangulaire constitué de briques issues du Tileset.
 * Le Heros et les Events se déplacent dedans.
 */
public class Map {
	public final int numero;
	public LecteurMap lecteur;
	public String nom;
	public String nomBGM; //musique
	public String nomBGS; //fond sonore
	public String nomTileset;
	public Tileset tileset; //image contenant les decors
	public final int largeur;
	public final int hauteur;
	public int[][] layer0; //couche du sol
	public int[][] layer1; //couche de decor 1
	public int[][] layer2; //couche de decor 2
	public BufferedImage imageCoucheSousHeros;
	public BufferedImage imageCoucheSurHeros;
	public ArrayList<Event> events;
	public Heros heros;
	public int xDebutHeros;
	public int yDebutHeros;
	public int directionDebutHeros;
	public boolean[][] casePassable;
	
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
		
		//la map est un fichier JSON
		JSONObject jsonMap = InterpreteurDeJson.ouvrirJsonMap(numero);
		
		this.nomBGM = jsonMap.getString("bgm");
		this.nomBGS = jsonMap.getString("bgs");
		this.nomTileset = jsonMap.getString("tileset");
		this.largeur = jsonMap.getInt("largeur");
		this.hauteur = jsonMap.getInt("hauteur");
		this.layer0 = recupererCouche(jsonMap, 0);
		this.layer1 = recupererCouche(jsonMap, 1);
		this.layer2 = recupererCouche(jsonMap, 2);
		this.xDebutHeros = xDebutHerosArg;
		this.yDebutHeros = yDebutHerosArg;
		this.directionDebutHeros = directionDebutHeros;
		
		//chargement du tileset
			this.tileset = new Tileset(nomTileset);
		
		//on dessine la couche de décor inférieure, qui sera sous le héros et les évènements
			creerImageDuDecorEnDessousDuHeros();
		
		//on dessine la couche de décor supérieure, qui sera au dessus du héros et des évènements
			creerImageDuDecorAuDessusDuHeros();
		
		//importer les events
			importerLesEvenements(jsonMap);
			
		//création de la liste des cases passables
			creerListeDesCasesPassables();
	}

	/**
	 * L'affichage est un sandwich : une partie du décor est en fond, et les Events sont affichés par dessus.
	 * On affiche en premier le décor arrière.
	 */
	private void creerImageDuDecorEnDessousDuHeros() {
		// TODO prendre en compte toutes les couches 1 2 3 et les altitudes du tileset
		try {
			this.imageCoucheSousHeros = lecteur.imageVide(largeur*Fenetre.TAILLE_D_UN_CARREAU, hauteur*Fenetre.TAILLE_D_UN_CARREAU);
			for (int i = 0; i<largeur; i++) {
				for (int j = 0; j<hauteur; j++) {
					try {
						int numeroCarreau = layer0[i][j];
						imageCoucheSousHeros = this.lecteur.dessinerCarreau(imageCoucheSousHeros, i, j, numeroCarreau, tileset);
					} catch (NumberFormatException e) {
						//case vide
					}
				}
			}
		} catch (Exception e) {
			System.out.println("Erreur lors de la lecture de la map :");
			e.printStackTrace();
		}
	}

	/**
	 * L'affichage est un sandwich : une partie du décor est affichée par dessus les Events.
	 * On affiche en dernier le décor supérieur.
	 */
	private void creerImageDuDecorAuDessusDuHeros() {
		//TODO prendre en compte toutes les couches 1 2 3 et les altitudes du tileset
		try {
			this.imageCoucheSurHeros = lecteur.imageVide(largeur*Fenetre.TAILLE_D_UN_CARREAU, hauteur*Fenetre.TAILLE_D_UN_CARREAU);
			for (int i = 0; i<largeur; i++) {
				for (int j = 0; j<hauteur; j++) {
					try {
						int numeroCarreau = layer1[i][j];
						if (numeroCarreau>=0) {
							imageCoucheSurHeros = this.lecteur.dessinerCarreau(imageCoucheSurHeros, i, j, numeroCarreau, tileset);
						}
					} catch (NumberFormatException e) {
						//case vide
					}
				}
			}
		} catch (Exception e) {
			System.out.println("Erreur lors de la lecture de la map :");
			e.printStackTrace();
		}
	}

	/**
	 * Constitue la liste des Events de la Map en allant lire dans le fichier JSON décrivant la Map.
	 * @param jsonMap objet JSON décrivant la Map
	 */
	private void importerLesEvenements(final JSONObject jsonMap) {
		try {
			this.events = new ArrayList<Event>();
			//d'abord le héros
			this.heros = new Heros(this, this.xDebutHeros, this.yDebutHeros, this.directionDebutHeros);
			this.events.add(heros);
			//puis les autres
			JSONArray jsonEvents = jsonMap.getJSONArray("events");
			for (Object ev : jsonEvents) {
				JSONObject jsonEvent = (JSONObject) ev;
				//récupération des données dans le JSON
				String nomEvent = jsonEvent.getString("nom");
				int xEvent = jsonEvent.getInt("x");
				int yEvent = jsonEvent.getInt("y");
				//instanciation de l'event
				Event event;
				try {
					/*
					try{
					*/
					//on essaye de le créer à partir de la bibliothèque JSON GenericEvents
					JSONObject jsonEventGenerique = InterpreteurDeJson.ouvrirJsonEventGenerique(nomEvent);
					int largeurHitbox = jsonEventGenerique.getInt("largeur");
					int hauteurHitbox = jsonEventGenerique.getInt("hauteur");
					int direction = Event.Direction.obtenirDirectionViaJson(jsonEventGenerique);
					JSONArray jsonPages = jsonEventGenerique.getJSONArray("pages");
					event = new Event(this, xEvent, yEvent, direction, nomEvent, jsonPages, largeurHitbox, hauteurHitbox);
					/*
					}catch(Exception e2){
						e2.printStackTrace();
						//on essaye de le créer à partir de la bibliothèque Java
						Class<?> classeEvent = Class.forName("bibliothequeEvent."+nomEvent);
						Constructor<?> constructeurEvent = classeEvent.getConstructor(this.getClass(), Integer.class, Integer.class);
						event = (Event) constructeurEvent.newInstance(this, xEvent, yEvent);
					}
					*/
				} catch (Exception e3) {
					//l'event n'est pas générique, on le construit à partir de sa description dans la page JSON
					int largeurHitbox = jsonEvent.getInt("largeur");
					int hauteurHitbox = jsonEvent.getInt("hauteur");
					int direction = Event.Direction.obtenirDirectionViaJson(jsonEvent);
					JSONArray jsonPages = jsonEvent.getJSONArray("pages");
					event = new Event(this, xEvent, yEvent, direction, nomEvent, jsonPages, largeurHitbox, hauteurHitbox);
				}
				this.events.add(event);
			}
			//events.add( new Panneau(this,2,7) );
			//events.add( new DarumaAleatoire(this,1,1) );
			//events.add( new Algue(this,2,8) );
			//events.add( new DarumaAleatoire(this,3,7) );
			//events.add( new DarumaAleatoire(this,3,8) );
		} catch (Exception e3) {
			System.err.println("Erreur lors de la constitution de la liste des events :");
			e3.printStackTrace();
		}
		//numérotation des events
		for (int i = 0; i<this.events.size(); i++) {
			this.events.get(i).numero = i;
		}
	}

	/**
	 * Création d'un tableau pour connaitre les passabilités de la Map plus rapidement par la suite.
	 */
	private void creerListeDesCasesPassables() {
		ArrayList<int[][]> layers = new ArrayList<int[][]>();
		layers.add(this.layer0);
		layers.add(this.layer1);
		layers.add(this.layer2);
		this.casePassable = new boolean[this.largeur][this.hauteur];
		boolean passable;
		int numeroDeLaCaseDansLeTileset;
		for (int i = 0; i<this.largeur; i++) {
			for (int j = 0; j<this.hauteur; j++) {
				passable = true;
				this.casePassable[i][j] = true;
				for (int k = 0; k<3&&passable; k++) { //si on en trouve une de non passable, on ne cherche pas les autres couches
					int[][] layer = layers.get(k);
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
		int[][] couche = new int[largeur][hauteur];
		JSONArray array = jsonMap.getJSONArray("couche"+numeroCouche);
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
	 * Inscrire l'Event dans la liste des Events en attente de suppression
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
