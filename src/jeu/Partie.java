package jeu;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.json.JSONObject;

import map.Map;
import utilitaire.InterpreteurDeJson;
import utilitaire.Maths;

/**
 * Une Partie est l'ensemble des informations liées à l'avancée du joueur dans le jeu.
 */
public final class Partie {
	public int numeroMap;
	public Map map;
	public int xHeros;
	public int yHeros;
	public int directionHeros;
	public int vie;
	public int vieMax;
	
	public final boolean[] interrupteurs;
	public final ArrayList<String> interrupteursLocaux;
	public final int[] variables;
	
	/** combien possède-t-on d'Objet numéro i ? */
	public int[] objetsPossedes;
	/** la Quête numéro i a-t-elle été faite ? */
	public boolean[] quetesFaites;
	/** possède-t-on l'Arme numéro i ? */
	public boolean[] armesPossedees;
	public int nombreDArmesPossedees;
	
	private int idArmeEquipee;
	
	
	/**
	 * Constructeur d'une nouvelle Partie vierge
	 * @throws FileNotFoundException le JSON de paramétrage d'une nouvelle Partie n'a pas été trouvé
	 */
	private Partie() throws FileNotFoundException {
		final JSONObject jsonNouvellePartie = InterpreteurDeJson.ouvrirJsonNouvellePartie();
		
		this.numeroMap = jsonNouvellePartie.getInt("numeroMap");
		this.xHeros = jsonNouvellePartie.getInt("xHeros");
		this.yHeros = jsonNouvellePartie.getInt("yHeros");
		this.directionHeros = jsonNouvellePartie.getInt("directionHeros");
		this.vie = jsonNouvellePartie.getInt("vie");
		this.vieMax = jsonNouvellePartie.getInt("vieMax");
		
		this.interrupteurs = new boolean[ jsonNouvellePartie.getInt("nombreDInterrupteurs") ];
		this.interrupteursLocaux = new ArrayList<String>();
		this.variables = new int[ jsonNouvellePartie.getInt("nombreDeVariables") ];
		
		this.quetesFaites = new boolean[ Quete.chargerLesQuetesDuJeu() ];
		this.objetsPossedes = new int[ Objet.chargerLesObjetsDuJeu() ];
		this.armesPossedees = new boolean[ Arme.chargerLesArmesDuJeu() ];
		this.nombreDArmesPossedees = 0;
		
		this.idArmeEquipee = -1;
		System.out.println("Partie chargée.");
	}
	
	/**
	 * Constructeur explicite
	 * @param numeroMap numéro de la Map où se trouve le Héros en reprenant la Partie
	 * @param xHeros coordonnée x du Héros en reprenant la Partie
	 * @param yHeros coordonnée y du Héros en reprenant la Partie
	 * @param directionHeros direction dans laquelle se trouve le Heros en reprenant la Partie
	 * @param vie niveau d'énergie vitale du Héros en reprenant la Partie
	 * @param vieMax niveau maximal possible d'énergie vitale du Héros en reprenant la Partie
	 * ----------------------------------------------------------------------------------------
	 * @param objetsPossedes combien possède-t-on d'Objet numéro i ?
	 * @param quetesFaites la Quête numéro i a-t-elle été faite ?
	 * @param armesPossedees possède-t-on l'Arme numéro i ?
	 * @param nombreDArmesPossedees combien a-t-on d'Armes ?
	 * ---------------------------------------------------------------------------------------- 
	 * @param idArmeEquipee identifiant de l'Arme actuelle équipée
	 * @throws FileNotFoundException le JSON de paramétrage d'une nouvelle Partie n'a pas été trouvé
	 */
	private Partie(final int numeroMap, final int xHeros, final int yHeros, final int directionHeros, final int vie, final int vieMax, final int idArmeEquipee, final int[] objetsPossedes, final boolean[] quetesFaites, final boolean[] armesPossedees, final int nombreDArmesPossedees) throws FileNotFoundException {
		this();
		this.numeroMap = numeroMap;
		this.xHeros = xHeros;
		this.yHeros = yHeros;
		this.directionHeros = directionHeros;
		this.vie = vie;
		this.vieMax = vieMax;
		
		this.objetsPossedes = objetsPossedes;
		this.quetesFaites = quetesFaites;
		this.armesPossedees = armesPossedees;
		this.nombreDArmesPossedees = nombreDArmesPossedees;
		
		this.idArmeEquipee = idArmeEquipee;
	}
	
	/**
	 * Génère une nouvelle Partie vierge.
	 * @return une nouvelle partie
	 * @throws FileNotFoundException le JSON de paramétrage d'une nouvelle Partie n'a pas été trouvé
	 */
	public static Partie creerNouvellePartie() throws FileNotFoundException {
		return new Partie();
	}
	
	/**
	 * Charger une partie à l'aide d'un fichier de sauvegarde.
	 * @param numeroSauvegarde numéro de la partie sauvegardée
	 * @return une partie sauvegardée
	 */
	public static Partie chargerPartie(final int numeroSauvegarde) {
		//TODO créer un objet Partie à partir d'un fichier de sauvegarde
		return null;
	}
	
	/**
	 * Connaitre l'Arme actuellement équipée
	 * @return Arme équipée
	 */
	public Arme getArmeEquipee() {
		return Arme.getArme(this.idArmeEquipee);
	}
	
	/**
	 * Equiper une Arme au Heros
	 * @param idArme identifiant de l'Arme à équiper
	 */
	public void equiperArme(final int idArme) {
		if (this.armesPossedees[idArme]) {
			this.idArmeEquipee = idArme;
		}
	}
	
	/**
	 * Equiper l'Arme suivante dans la liste des Armes possédées par le Héros
	 */
	public void equiperArmeSuivante() {
		//pas d'Armes possédées
		if (nombreDArmesPossedees<=0) {
			return;
		}
		//si pas d'Arme équipée, on équipe la dernière possédée
		if (idArmeEquipee<0) {
			idArmeEquipee += nombreDArmesPossedees-1;
			return;
		}
		//on équipe l'Arme suivante
		this.idArmeEquipee = Maths.modulo(this.idArmeEquipee + 1, nombreDArmesPossedees);
		//affichage console
		if (this.getArmeEquipee()!=null) {
			System.out.println("arme suivante : "+this.getArmeEquipee().nom);
		}
	}
	
	/**
	 * Equiper l'Arme précédente dans la liste des Armes possédées par le Héros
	 */
	public void equiperArmePrecedente() {		
		//pas d'Armes possédées
		if (nombreDArmesPossedees<=0) {
			return;
		}
		//si pas d'Arme équipée, on équipe la dernière possédée
		if (idArmeEquipee<0) {
			idArmeEquipee += nombreDArmesPossedees-1;
			return;
		}
		//on équipe l'Arme précédente
		this.idArmeEquipee = Maths.modulo(this.idArmeEquipee - 1, nombreDArmesPossedees);
		//affichage console
		System.out.println("arme précédente : "+ this.getArmeEquipee().nom);
	}
	
}
