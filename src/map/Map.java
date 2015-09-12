package map;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import main.Arme;
import main.Partie;
import bibliothequeEvent.Algue;
import bibliothequeEvent.DarumaAleatoire;

public class Map {
	int numero;
	public LecteurMap lecteur;
	public String nomBGM;
	public String nomTileset;
	public Tileset tileset;
	ArrayList<String[]> layer0; //couche du sol, importée par fichier CSV depuis RM
	ArrayList<String[]> layer1; //couche d'objets 1, importée par fichier CSV depuis RM
	ArrayList<String[]> layer2; //couche d'objets 2, importée par fichier CSV depuis RM
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
	 */
	public Map(int numero, LecteurMap lecteur, int xDebutHeros, int yDebutHeros){
		this.numero = numero;
		this.lecteur = lecteur;
		this.nomBGM = getNomBgmFromData(numero);
		this.nomTileset = getNomTilesetFromData(numero);

		//importation de la couche 0 du décor, transformation en un tableau
		BufferedReader buff;
		this.layer0 = new ArrayList<String[]>();
		try {
			buff = new BufferedReader(new FileReader(".\\ressources\\Data\\Maps\\"+numero+"\\layer0.csv"));
			String ligne;
			while ((ligne = buff.readLine()) != null) {
				this.layer0.add(ligne.split(";",-1)); //le -1 de split signifie qu'il gère les cases vides
			}
			buff.close();
		} catch (Exception e) {
			System.out.println("Erreur lors de l'ouverture de la couche 0 de la map :");
			e.printStackTrace();
		}
		
		//importation de la couche 1 du décor, transformation en un tableau
		this.layer1 = new ArrayList<String[]>();
		try {
			buff = new BufferedReader(new FileReader(".\\ressources\\Data\\Maps\\"+numero+"\\layer1.csv"));
			String ligne;
			while ((ligne = buff.readLine()) != null) {
				this.layer1.add(ligne.split(";",-1)); //le -1 de split signifie qu'il gère les cases vides
			}
			buff.close();
		} catch (Exception e) {
			System.out.println("Erreur lors de l'ouverture de la couche 1 de la map :");
			e.printStackTrace();
		}
		
		//chargement du tileset
		this.tileset = new Tileset(nomTileset);
		if(tileset == null){
		}
		
		//on dessine la couche de décor inférieure, qui sera sous le héros et les évènements
		//TODO prendre en compte les layer1 et 2
		try{
			int largeur = layer0.get(0).length;
			int hauteur = layer0.size();
			this.imageCoucheSousHeros = lecteur.imageVide(largeur*32,hauteur*32);
			for(int i=0; i<largeur; i++){
				for(int j=0; j<hauteur; j++){
					try{
						int carreau = Integer.parseInt( layer0.get(j)[i] );
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
			int largeur = layer1.get(0).length;
			int hauteur = layer1.size();
			this.imageCoucheSurHeros = lecteur.imageVide(largeur*32,hauteur*32);
			for(int i=0; i<largeur; i++){
				for(int j=0; j<hauteur; j++){
					try{
						int carreau = Integer.parseInt( layer1.get(j)[i] );
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
		
		//importation des évènements grâce à un fichier CSV
			//d'abord le héros :
				try{
					events = new ArrayList<Event>();
					this.heros = new Heros(this, xDebutHeros,yDebutHeros,0);
					events.add(heros);
					
					//events.add( new Panneau(this,2,7) );
					events.add( new DarumaAleatoire(this,1,1) );
					events.add( new Algue(this,2,8) );
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

	private String getNomBgmFromData(int numero2) {
		// TODO aller chercher le nom du BGM grâce au numéro de la map dans les fichiers du jeu
		return "Enfermement.ogg";
	}

	private static String getNomTilesetFromData(int numeroMap) {
		// TODO aller chercher le nom du tileset grâce au numéro de la map dans les fichiers du jeu
		return "Maison Tileset.png";
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
