package jeu;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import commandes.Sauvegarder;
import jeu.Quete.AvancementQuete;
import map.Map;
import map.Picture;
import map.meteo.Meteo;
import utilitaire.InterpreteurDeJson;
import utilitaire.Maths;

/**
 * Une Partie est l'ensemble des informations liées à l'avancée du joueur dans le jeu.
 */
public final class Partie {
	private static final Logger LOG = LogManager.getLogger(Partie.class);
	
	public int numeroMap;
	public Map map;
	public int xHeros;
	public int yHeros;
	public int directionHeros;
	public int vie;
	public int vieMax;
	public int argent;
	
	public final boolean[] interrupteurs;
	public final ArrayList<String> interrupteursLocaux;
	public final int[] variables;
	
	/** combien possède-t-on d'Objet numéro i ? */
	public int[] objetsPossedes;
	/** la Quête numéro i a-t-elle été faite ? */
	public AvancementQuete[] avancementDesQuetes;
	/** possède-t-on l'Arme numéro i ? */
	public boolean[] armesPossedees;
	public int nombreDArmesPossedees;
	/** possède-t-on le gadget numéro i ? */
	public boolean[] gadgetsPossedes;
	public int nombreDeGadgetsPossedes;
	
	public int idArmeEquipee;
	public int idGadgetEquipe;
	
	/** effet météorologique en cours */
	public Meteo meteo = null;
	/** Images à afficher par dessus l'écran */
	public HashMap<Integer, Picture> images = new HashMap<Integer, Picture>();
	
	
	/**
	 * Constructeur d'une nouvelle Partie vierge
	 * @throws FileNotFoundException le JSON de paramétrage d'une nouvelle Partie n'a pas été trouvé
	 */
	private Partie() throws FileNotFoundException {
		final JSONObject jsonNouvellePartie = InterpreteurDeJson.ouvrirJsonNouvellePartie();
		// Position du Héros
		this.numeroMap = jsonNouvellePartie.getInt("numeroMap");
		this.xHeros = jsonNouvellePartie.getInt("xHeros");
		this.yHeros = jsonNouvellePartie.getInt("yHeros");
		this.directionHeros = jsonNouvellePartie.getInt("directionHeros");
		// Vie
		this.vie = jsonNouvellePartie.getInt("vie");
		this.vieMax = jsonNouvellePartie.getInt("vieMax");
		// Interrupteurs et Variables
		this.interrupteurs = new boolean[ jsonNouvellePartie.getInt("nombreDInterrupteurs") ];
		this.interrupteursLocaux = new ArrayList<String>();
		this.variables = new int[ jsonNouvellePartie.getInt("nombreDeVariables") ];
		// Quêtes
		this.avancementDesQuetes = new AvancementQuete[ Quete.chargerLesQuetesDuJeu() ];
		for (int i = 0; i<avancementDesQuetes.length; i++) {
			avancementDesQuetes[i] = AvancementQuete.INCONNUE;
		}
		// Objets
		this.objetsPossedes = new int[ Objet.chargerLesObjetsDuJeu() ];
		// Armes
		this.armesPossedees = new boolean[ Arme.chargerLesArmesDuJeu() ];
		this.nombreDArmesPossedees = 0;
		this.idArmeEquipee = -1;
		// Gadgets
		this.gadgetsPossedes = new boolean[ Gadget.chargerLesGadgetsDuJeu() ];
		this.nombreDeGadgetsPossedes = 0;
		this.idGadgetEquipe = -1;
		
		LOG.info("Partie chargée.");
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
	 * @param avancementDesQuetes la Quête numéro i a-t-elle été faite ?
	 * @param armesPossedees possède-t-on l'Arme numéro i ?
	 * @param nombreDArmesPossedees combien a-t-on d'Armes ?
	 * ---------------------------------------------------------------------------------------- 
	 * @param idArmeEquipee identifiant de l'Arme actuelle équipée
	 * @param idGadgetEquipe identifiant du Gadget actuel équipé
	 * @throws FileNotFoundException le JSON de paramétrage d'une nouvelle Partie n'a pas été trouvé
	 */
	private Partie(final int numeroMap, final int xHeros, final int yHeros, final int directionHeros, final int vie, 
			final int vieMax, final int idArmeEquipee, final int idGadgetEquipe, final int[] objetsPossedes, 
			final AvancementQuete[] avancementDesQuetes, final boolean[] armesPossedees, final int nombreDArmesPossedees) 
	throws FileNotFoundException {
		this();
		this.numeroMap = numeroMap;
		this.xHeros = xHeros;
		this.yHeros = yHeros;
		this.directionHeros = directionHeros;
		this.vie = vie;
		this.vieMax = vieMax;
		
		this.objetsPossedes = objetsPossedes;
		this.avancementDesQuetes = avancementDesQuetes;
		this.armesPossedees = armesPossedees;
		this.nombreDArmesPossedees = nombreDArmesPossedees;
		
		this.idArmeEquipee = idArmeEquipee;
		this.idGadgetEquipe = idGadgetEquipe;
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
	 * @param jsonSauvegarde partie au format json
	 * @return une partie sauvegardée
	 */
	public static Partie chargerPartie(final JSONObject jsonSauvegarde) {
		final JSONObject jsonEtatMap = (JSONObject) jsonSauvegarde.get("etatMap");
		final JSONObject jsonAvancement = (JSONObject) jsonSauvegarde.get("partie");
		final Partie partie = new Partie(
				jsonEtatMap.getInt("numero"),
				jsonEtatMap.getInt("xHeros"),
				jsonEtatMap.getInt("yHeros"),
				jsonEtatMap.getInt("directionHeros"),
				jsonAvancement.getInt("vie")
				
				
		);
		return partie;
	}
	

	
	/**
	 * Connaitre le Gadget actuellement équipé
	 * @return Gadget équipé
	 */
	public Gadget getGadgetEquipe() {
		return Gadget.getGadget(this.idGadgetEquipe);
	}
	
	/**
	 * Equiper un Gadget au Heros
	 * @param idGadget identifiant du Gadget à équiper
	 */
	public void equiperGadget(final int idGadget) {
		if (this.gadgetsPossedes[idGadget]) {
			this.idGadgetEquipe = idGadget;
		}
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
			LOG.info("arme suivante : "+this.getArmeEquipee().nom);
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
		LOG.info("arme précédente : "+ this.getArmeEquipee().nom);
	}
	
}
