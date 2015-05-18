package main;

import java.util.ArrayList;
import map.Map;

public class Partie {
	int numeroMap;
	Map map;
	int xHeros;
	int yHeros;
	int vie;
	int vieMax;
	public boolean[] interrupteurs;
	public int[] variables;
	public boolean[] quetesFaites;
	public static int idArmeEquipee;
	public static ArrayList<Integer> idArmesPossedees = new ArrayList<Integer>();
	
	/**
	 * le héros commence en map numéro 0
	 * coordonnées (5;5)
	 * 6 vies (sur 6 vies maximum)
	 */
	private Partie(){
		numeroMap = 0;
		xHeros = 5;
		yHeros = 5;
		vie = 6;
		vieMax = 6;
		interrupteurs = new boolean[100];
		variables = new int[100];
		quetesFaites = new boolean[100];
		Arme.initialiserLesArmesDuJeu();
	}
	
	/**
	 * @param numeroMap numéro de la map où se trouve le héros
	 * @param xHeros coordonnée x du héros
	 * @param yHeros coordonnée y du héros
	 * @param vie niveau d'énergie vitale du héros
	 * @param vieMax niveau maximal possible d'énergie vitale du héros
	 */
	private Partie(int numeroMap, int xHeros, int yHeros, int vie, int vieMax){
		this.numeroMap = numeroMap;
		this.xHeros = xHeros;
		this.yHeros = yHeros;
		this.vie = vie;
		this.vieMax = vieMax;
	}
	
	/**
	 * @return une nouvelle partie
	 */
	public static Partie nouvellePartie(){
		return new Partie();
	}
	
	/**
	 * @param numeroSauvegarde numéro de la partie sauvegardée
	 * @return une partie sauvegardée
	 */
	public static Partie chargerPartie(int numeroSauvegarde){
		//TODO
		return null;
	}
	
}
