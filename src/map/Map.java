package map;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

import main.Arme;
import main.Partie;

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
	/**
	 * N'est vrai que durant l'action de l'appui, se remet à false tout de suite
	 */
	public Boolean toucheActionPressee = false;
	public boolean[][] casePassable;
	
	/**
	 * @param numero de la map, c'est-à-dire numéro du fichier map à charger
	 * @param lecteur de la map
	 * @param nomTileset 
	 * @throws FileNotFoundException 
	 */
	public Map(int numero, LecteurMap lecteur, int xDebutHerosArg, int yDebutHerosArg, int directionDebutHeros) throws FileNotFoundException{
		this.numero = numero;
		this.lecteur = lecteur;
		
		//la map est un fichier JSON
		String nomFichierJson = ".\\ressources\\Data\\Maps\\"+numero+".json";
		Scanner scanner = new Scanner(new File(nomFichierJson));
		String contenuFichierJson = scanner.useDelimiter("\\Z").next();
		scanner.close();
		JSONObject jsonMap = new JSONObject(contenuFichierJson);
		
		this.nomBGM = jsonMap.getString("bgm");
		this.nomBGS = jsonMap.getString("bgs");
		this.nomTileset = jsonMap.getString("tileset");
		this.largeur = jsonMap.getInt("largeur");
		this.hauteur = jsonMap.getInt("hauteur");
		this.layer0 = recupererCouche(jsonMap,0);
		this.layer1 = recupererCouche(jsonMap,1);
		this.layer2 = recupererCouche(jsonMap,2);
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

	private void creerImageDuDecorEnDessousDuHeros() {
		// TODO prendre en compte toutes les couches 1 2 3 et les altitudes du tileset
		try{
			this.imageCoucheSousHeros = lecteur.imageVide(largeur*32,hauteur*32);
			for(int i=0; i<largeur; i++){
				for(int j=0; j<hauteur; j++){
					try{
						int carreau = layer0[i][j];
						imageCoucheSousHeros = this.lecteur.dessinerCarreau(imageCoucheSousHeros,i,j,carreau,tileset);
					}catch(NumberFormatException e){
						//case vide
					}
				}
			}
		}catch(Exception e){
			System.out.println("Erreur lors de la lecture de la map :");
			e.printStackTrace();
		}
	}

	private void creerImageDuDecorAuDessusDuHeros() {
		//TODO prendre en compte toutes les couches 1 2 3 et les altitudes du tileset
		try{
			this.imageCoucheSurHeros = lecteur.imageVide(largeur*32,hauteur*32);
			for(int i=0; i<largeur; i++){
				for(int j=0; j<hauteur; j++){
					try{
						int carreau = layer1[i][j];
						if(carreau>=0) imageCoucheSurHeros = this.lecteur.dessinerCarreau(imageCoucheSurHeros,i,j,carreau,tileset);
					}catch(NumberFormatException e){
						//case vide
					}
				}
			}
		}catch(Exception e){
			System.out.println("Erreur lors de la lecture de la map :");
			e.printStackTrace();
		}
	}

	private void importerLesEvenements(JSONObject jsonMap) {
		try{
			this.events = new ArrayList<Event>();
			//d'abord le héros
			this.heros = new Heros(this, this.xDebutHeros,this.yDebutHeros, this.directionDebutHeros);
			this.events.add(heros);
			//puis les autres
			JSONArray jsonEvents = jsonMap.getJSONArray("events");
			for(Object ev : jsonEvents){
				JSONObject jsonEvent = (JSONObject) ev;
				//récupération des données dans le JSON
				String nomEvent = jsonEvent.getString("nom");
				int xEvent = jsonEvent.getInt("x");
				int yEvent = jsonEvent.getInt("y");
				//instanciation de l'event
				Event event;
				try{
					//on essaye de le créer à partir de la bibliothèque
					Class<?> classeEvent = Class.forName("bibliothequeEvent."+nomEvent);
					Constructor<?> constructeurEvent = classeEvent.getConstructor(this.getClass(), Integer.class, Integer.class);
					event = (Event) constructeurEvent.newInstance(this, xEvent, yEvent);
				}catch(ClassNotFoundException e1){
					//l'event n'est pas dans la bibliothèque, on le construit à partir de sa description JSON
					int hauteurHitbox = jsonEvent.getInt("largeur");
					int largeurHitbox = jsonEvent.getInt("hauteur");
					int direction;
					try{
						direction = jsonEvent.getInt("direction");
					}catch(Exception e2){
						direction = Event.Direction.BAS; //direction par défaut
					}
					JSONArray jsonPages = jsonEvent.getJSONArray("pages");
					event = new Event(this, xEvent, yEvent, direction, nomEvent, jsonPages, hauteurHitbox, largeurHitbox);
				}
				this.events.add(event);
			}
			//events.add( new Panneau(this,2,7) );
			//events.add( new DarumaAleatoire(this,1,1) );
			//events.add( new Algue(this,2,8) );
			//events.add( new DarumaAleatoire(this,3,7) );
			//events.add( new DarumaAleatoire(this,3,8) );
		} catch(Exception e3) {
			System.err.println("Erreur lors de la constitution de la liste des events :");
			e3.printStackTrace();
		}
		//numérotation des events
		for(int i=0; i<this.events.size(); i++){
			this.events.get(i).numero = i;
		}
	}

	private void creerListeDesCasesPassables() {
		ArrayList<int[][]> layers = new ArrayList<int[][]>();
		layers.add(this.layer0);
		layers.add(this.layer1);
		layers.add(this.layer2);
		this.casePassable = new boolean[this.largeur][this.hauteur];
		boolean passable;
		int numeroDeLaCaseDansLeTileset;
		for(int i=0; i<this.largeur; i++){
			for(int j=0; j<this.hauteur; j++){
				passable = true;
				this.casePassable[i][j] = true;
				for(int k=0; k<3&&passable; k++){ //si on en trouve une de non passable, on ne cherche pas les autres couches
					int[][] layer = layers.get(k);
					numeroDeLaCaseDansLeTileset = layer[i][j];
					if(numeroDeLaCaseDansLeTileset!=-1 && !this.tileset.passabilite[numeroDeLaCaseDansLeTileset]){
						this.casePassable[i][j] = false;
						passable = false;
					}
				}
			}
		}
	}

	/**
	 * 
	 * @param jsonMap objet JSON représentant la map
	 * @param numeroCouche numéro de la couche à récuperer
	 * @return
	 */
	private int[][] recupererCouche(JSONObject jsonMap, int numeroCouche) {
		int[][] couche = new int[largeur][hauteur];
		JSONArray array = jsonMap.getJSONArray("couche"+numeroCouche);
		JSONArray ligne;
		for(int j=0; j<hauteur; j++){
			ligne = (JSONArray) array.get(j);
			for(int i=0; i<largeur; i++){
				try{
					couche[i][j] = Integer.parseInt((String) ligne.get(i));
				}catch(ClassCastException e){
					couche[i][j] = (Integer) ligne.get(i);
				}
			}
		}
		return couche;
	}

	/**
	 * marcher vers le haut
	 */
	public void haut() {
		lecteur.mettreHerosDansLaBonneDirection();
		this.heros.avance = true;
	}

	/**
	 * marcher vers la gauche
	 */
	public void gauche() {
		lecteur.mettreHerosDansLaBonneDirection();
		this.heros.avance = true;
	}

	/**
	 * marcher vers le bas
	 */
	public void bas() {
		lecteur.mettreHerosDansLaBonneDirection();
		this.heros.avance = true;
	}

	/**
	 * marcher vers la droite
	 */
	public void droite() {
		lecteur.mettreHerosDansLaBonneDirection();
		this.heros.avance = true;
	}
	
	/**
	 * attaquer ou parler
	 */
	public void action() {
		toucheActionPressee = true;
	}

	/**
	 * changer d'arme
	 */
	public void equiperArmeSuivante() {
		int idArmeEquipee = Partie.idArmeEquipee;
		idArmeEquipee++;
		int nombreDArmesPossedees = Partie.idArmesPossedees.size();
		if(idArmeEquipee>=nombreDArmesPossedees){
			idArmeEquipee -= nombreDArmesPossedees;
		}
		Partie.idArmeEquipee = idArmeEquipee;
		String nomArmeEquipee;
		try{
			nomArmeEquipee = Arme.getArme(idArmeEquipee).nom;
		}catch(NullPointerException e){
			nomArmeEquipee = "null";
		}
		System.out.println("arme suivante : "+nomArmeEquipee);
	}
	
	/**
	 * changer d'arme
	 */
	public void equiperArmePrecedente() {
		int idArmeEquipee = Partie.idArmeEquipee;
		idArmeEquipee--;
		int nombreDArmesPossedees = Partie.idArmesPossedees.size();
		if(nombreDArmesPossedees<=0){
			//pas d'armes possédées
			return;
		}
		if(idArmeEquipee<0){
			//si pas d'arme équipée, on équipe la dernière possédée
			idArmeEquipee += nombreDArmesPossedees;
		}
		Partie.idArmeEquipee = idArmeEquipee;
		System.out.println("arme précédente : "+Arme.getArme(idArmeEquipee).nom);
	}

	/**
	 * utiliser objet secondaire
	 */
	public void objet() {
		//TODO
	}

	/**
	 * Mettre en attente dans la liste de suppression.
	 * L'évènement sera supprimé à la fin de la boucle d'affichage
	 * @param numeroEventASupprimer
	 * @return booléen pour savoir si l'évent à supprimer a bien été trouvé dans la liste des évènements
	 */
	public boolean supprimerEvenement(int numeroEventASupprimer) {
		for(Event event : this.events){
			if(event.numero == numeroEventASupprimer){
				event.supprime = true;
				return true;
			}
		}
		System.out.println("L'évènement à supprimer numéro "+numeroEventASupprimer+" n'a pas été trouvé dans la liste.");
		return false;
	}
	
}
