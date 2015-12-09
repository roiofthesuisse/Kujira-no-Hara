package main;

import java.util.ArrayList;

import map.Event;
import map.Map;

public class Partie {
	int numeroMap;
	Map map;
	int xHeros;
	int yHeros;
	int directionHeros;
	int vie;
	int vieMax;
	public boolean[] interrupteurs;
	public int[] variables;
	public boolean[] quetesFaites;
	public static int idArmeEquipee = -1;
	public static ArrayList<Integer> idArmesPossedees = new ArrayList<Integer>();
	
	/**
	 * le héros commence en map numéro 0
	 * coordonnées (5;5)
	 * 6 vies (sur 6 vies maximum)
	 */
	private Partie(){
		this.numeroMap = 0;
		this.xHeros = 5;
		this.yHeros = 5;
		this.directionHeros = Event.Direction.BAS;
		this.vie = 6;
		this.vieMax = 6;
		this.interrupteurs = new boolean[100];
		this.variables = new int[100];
		this.quetesFaites = new boolean[100];
		Arme.initialiserLesArmesDuJeu();
	}
	
	/**
	 * @param numeroMap numéro de la map où se trouve le héros
	 * @param xHeros coordonnée x du héros
	 * @param yHeros coordonnée y du héros
	 * @param vie niveau d'énergie vitale du héros
	 * @param vieMax niveau maximal possible d'énergie vitale du héros
	 */
	private Partie(int numeroMap, int xHeros, int yHeros, int directionHeros, int vie, int vieMax){
		this.numeroMap = numeroMap;
		this.xHeros = xHeros;
		this.yHeros = yHeros;
		this.directionHeros = directionHeros;
		this.vie = vie;
		this.vieMax = vieMax;
	}
	
	/**
	 * @return une nouvelle partie
	 */
	public static Partie creerNouvellePartie(){
		return new Partie();
	}
	
	/**
	 * Charger une partie à l'aide d'un fichier de sauvegarde.
	 * @param numeroSauvegarde numéro de la partie sauvegardée
	 * @return une partie sauvegardée
	 */
	public static Partie chargerPartie(int numeroSauvegarde){
		//TODO créer un objet Partie à partir d'un fichier de sauvegarde
		return null;
	}
	
}
