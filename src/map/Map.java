package map;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

import main.Arme;
import main.Partie;
import bibliothequeEvent.Algue;
import bibliothequeEvent.DarumaAleatoire;

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
	public Boolean toucheActionPressee = false;
	
	/**
	 * @param numero de la map, c'est-à-dire numéro du fichier map à charger
	 * @param lecteur de la map
	 * @param nomTileset 
	 * @throws FileNotFoundException 
	 */
	public Map(int numero, LecteurMap lecteur, int xDebutHeros, int yDebutHeros) throws FileNotFoundException{
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
		
		//chargement du tileset
		this.tileset = new Tileset(nomTileset);
		
		//on dessine la couche de décor inférieure, qui sera sous le héros et les évènements
		//TODO prendre en compte les layer1 et 2
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
		
		//on dessine la couche de décor supérieure, qui sera au dessus du héros et des évènements
		//TODO prendre en compte les layer0 et 2
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
		
		//importer les events
			//d'abord le héros :
				try{
					events = new ArrayList<Event>();
					this.heros = new Heros(this, xDebutHeros,yDebutHeros,0);
					events.add(heros);
					
					JSONArray jsonEvents = jsonMap.getJSONArray("events");
					for(Object jsonEvent : jsonEvents){
						//récupération des données dans le JSON
						String nomEvent = ((JSONObject)jsonEvent).getString("nom");
						int xEvent = ((JSONObject)jsonEvent).getInt("x");
						int yEvent = ((JSONObject)jsonEvent).getInt("y");
						//instanciation de l'event
						Class<?> classeEvent = Class.forName("bibliothequeEvent."+nomEvent);
						Constructor<?> constructeurEvent = classeEvent.getConstructor(this.getClass(), Integer.class, Integer.class);
						Event event = (Event) constructeurEvent.newInstance(this, xEvent, yEvent);
						events.add(event);
					}
					//events.add( new Panneau(this,2,7) );
					//events.add( new DarumaAleatoire(this,1,1) );
					//events.add( new Algue(this,2,8) );
					//events.add( new DarumaAleatoire(this,3,7) );
					//events.add( new DarumaAleatoire(this,3,8) );
				} catch(Exception e) {
					System.out.println("Erreur lors de la constitution de la liste des events :");
					e.printStackTrace();
				}
			//ajouter les autres events à partir du CSV liste des events de cette map
				//TODO
			//numérotation des events
				for(int i=0; i<events.size(); i++){
					events.get(i).numero = i;
				}
		//fin de l'importation des évènements
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
	 * ouvrir le menu
	 */
	public void menu() {
		// TODO
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
	 * changer d'arme
	 */
	public void armeSuivante() {
		int idArmeEquipee = Partie.idArmeEquipee;
		idArmeEquipee++;
		int nombreDArmesPossedees = Partie.idArmesPossedees.size();
		if(idArmeEquipee>=nombreDArmesPossedees){
			idArmeEquipee -= nombreDArmesPossedees;
		}
		Partie.idArmeEquipee = idArmeEquipee;
		System.out.println("arme suivante : "+Arme.getArme(idArmeEquipee).nom);
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
	public void armePrecedente() {
		int idArmeEquipee = Partie.idArmeEquipee;
		idArmeEquipee--;
		int nombreDArmesPossedees = Partie.idArmesPossedees.size();
		if(idArmeEquipee<0){
			idArmeEquipee += nombreDArmesPossedees;
		}
		Partie.idArmeEquipee = idArmeEquipee;
		System.out.println("arme précédente : "+Arme.getArme(idArmeEquipee).nom);
	}

	/**
	 * utiliser objet secondaire
	 */
	public void objet() {
		// TODO
	}

	/**
	 * Mettre en attente dans la liste de suppression.
	 * L'évènement sera supprimé à la fin de la boucle d'affichage
	 * @param numeroEventASupprimer
	 * @return booléen pour savoir si l'évent à supprimer a bien été trouvé dans la liste des évènements
	 */
	public boolean supprimerEvenement(int numeroEventASupprimer) {
		for(Event event : events){
			if(event.numero == numeroEventASupprimer){
				event.supprime = true;
				return true;
			}
		}
		System.out.println("L'évènement à supprimer numéro "+numeroEventASupprimer+" n'a pas été trouvé dans la liste.");
		return false;
	}

	public static String getNomBgm(int numeroNouvelleMap) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
