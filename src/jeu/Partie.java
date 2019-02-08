package jeu;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import commandes.ChargerPartie;
import commandes.DeplacerImage;
import commandes.JouerAnimation;
import commandes.Sauvegarder;
import commandes.Sauvegarder.Sauvegardable;
import conditions.Condition;
import jeu.Quete.AvancementQuete;
import jeu.courrier.LettreAEnvoyer;
import jeu.courrier.LettreRecue;
import main.Commande;
import main.Main;
import map.Animation;
import map.Brouillard;
import map.Map;
import map.Picture;
import map.meteo.Meteo;
import menu.Listable;
import menu.Texte;
import utilitaire.InterpreteurDeJson;
import utilitaire.Maths;
import utilitaire.graphismes.Graphismes;
import utilitaire.graphismes.ModeDeFusion;

/**
 * Une Partie est l'ensemble des informations liées à l'avancée du joueur dans le jeu.
 */
public final class Partie implements Listable, Sauvegardable {
	//constantes
	private static final Logger LOG = LogManager.getLogger(Partie.class);
	private static final int NOMBRE_D_INTERRUPTEURS = 100;
	private static final int NOMBRE_DE_VARIABLES = 200;
	private static final int NOMBRE_D_IMAGES = 50;
	private static final int NOMBRE_DE_MOTS = 50;
	/** Numéro du mot correpsondant au nom du heros */
	public static final int NOM_DU_HEROS = 0;
	/** Marge (en pixels) de la vignette de Partie dans le Menu */
	private static final int MARGE = 16;
	/** Numero de la variable dans laquelle est stocke le numero de l'image d'avancement de la partie */
	public static final int NUMERO_VARIABLE_AVANCEMENT_PARTIE = 126;
	
	public int id;
	public int numeroMap;
	public Map map;
	/** Brouillard à afficher sur la Map lorsqu'on charge une Partie sauvegardée */
	public Brouillard brouillardACharger;
	
	/** coordonnées x (en pixels) */
	public int xHeros, yHeros;
	public int directionHeros;
	public int vie;
	public int vieMax;
	public int argent;
	
	public final boolean[] interrupteurs = new boolean[NOMBRE_D_INTERRUPTEURS];
	public final ArrayList<String> interrupteursLocaux = new ArrayList<String>();
	public final int[] variables = new int[NOMBRE_DE_VARIABLES];
	
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
	/** Lettres a envoyer a des personnages du jeu */
	public ArrayList<LettreAEnvoyer> lettresAEnvoyer;
	/** Lettres recues de personnages du jeu */
	public ArrayList<LettreRecue> lettresRecues;
	
	public int idArmeEquipee;
	public int idGadgetEquipe;
	
	/** effet météorologique en cours */
	public Meteo meteo = null;
	/** Images à afficher par dessus l'écran */
	public Picture[] images = new Picture[NOMBRE_D_IMAGES];
	/** Animations à afficher sur la Map */
	public ArrayList<JouerAnimation> animations = new ArrayList<>();
	
	/** Mot de passe à saisir lettre par lettre via un Menu */
	public final int tailleMaximaleDuMot = 10;
	public String[] mots = new String[NOMBRE_DE_MOTS];
	/** Chronomètre a afficher à l'écran */
	public Chronometre chronometre;
	
	/**
	 * Constructeur d'une nouvelle Partie vierge
	 * @throws Exception le JSON de paramétrage d'une nouvelle Partie n'a pas été trouvé
	 */
	private Partie() throws Exception {
		final JSONObject jsonNouvellePartie = InterpreteurDeJson.ouvrirJsonNouvellePartie();
		// Position du Héros
		this.numeroMap = jsonNouvellePartie.getInt("numeroMap");
		this.brouillardACharger = null;
		this.xHeros = jsonNouvellePartie.getInt("xHeros") * Main.TAILLE_D_UN_CARREAU;
		this.yHeros = jsonNouvellePartie.getInt("yHeros") * Main.TAILLE_D_UN_CARREAU;
		this.directionHeros = jsonNouvellePartie.getInt("directionHeros");
		// Vie
		this.vie = jsonNouvellePartie.getInt("vie");
		this.vieMax = jsonNouvellePartie.getInt("vieMax");
		// Quêtes
		this.avancementDesQuetes = new AvancementQuete[ Quete.chargerLesQuetesDuJeu() ];
		for (int i = 0; i<avancementDesQuetes.length; i++) {
			avancementDesQuetes[i] = AvancementQuete.INCONNUE;
		}
		// Objets
		this.objetsPossedes = new int[Objet.objetsDuJeu.length];
		// Armes
		this.armesPossedees = new boolean[Arme.ARMES_DU_JEU.length];
		this.nombreDArmesPossedees = 0;
		this.idArmeEquipee = -1;
		// Gadgets
		this.gadgetsPossedes = new boolean[Gadget.GADGETS_DU_JEU.length];
		this.nombreDeGadgetsPossedes = 0;
		this.idGadgetEquipe = -1;
		// Courrier
		this.lettresAEnvoyer = new ArrayList<>();
		this.lettresRecues = new ArrayList<>();

		LOG.info("Partie chargée.");
	}
	
	/**
	 * Constructeur explicite
	 * @param id du fichier de sauvegarde
	 * @param numeroMap numéro de la Map où se trouve le Héros en reprenant la Partie
	 * @param xHeros coordonnée x du Héros (en pixels) en reprenant la Partie
	 * @param yHeros coordonnée y du Héros (en pixels) en reprenant la Partie
	 * @param directionHeros direction dans laquelle se trouve le Heros en reprenant la Partie
	 * @param jsonBrouillard de la Map
	 * @param vie niveau d'énergie vitale du Héros en reprenant la Partie
	 * @param vieMax niveau maximal possible d'énergie vitale du Héros en reprenant la Partie
	 * @param argent possédé
	 * ----------------------------------------------------------------------------------------
	 * @param objetsPossedes combien possède-t-on d'Objet numéro i ?
	 * @param avancementDesQuetes la Quête numéro i a-t-elle été faite ?
	 * @param armesPossedees possède-t-on l'Arme numéro i ?
	 * @param gadgetsPossedes possède-t-on le Gadget numéro i ?
	 * @param interrupteurs état des interrupteurs du jeu
	 * @param variables état des variables locaux du jeu
	 * @param interrupteursLocaux état des interrupteurs locaux du jeu
	 * @param mots état des mots du jeu
	 * @param chronometre état du chronomètre éventuel
	 * @param images affichées à l'écran
	 * ---------------------------------------------------------------------------------------- 
	 * @param idArmeEquipee identifiant de l'Arme actuelle équipée
	 * @param idGadgetEquipe identifiant du Gadget actuel équipé
	 * @throws Exception le JSON de paramétrage d'une nouvelle Partie n'a pas été trouvé
	 */
	public Partie(final int id, final int numeroMap, final int xHeros, final int yHeros, final int directionHeros, 
			final JSONObject jsonBrouillard, final int vie, final int vieMax, final int argent, final int idArmeEquipee, 
			final int idGadgetEquipe, final JSONArray objetsPossedes, final JSONArray avancementDesQuetes, 
			final JSONArray armesPossedees, final JSONArray gadgetsPossedes, final JSONArray interrupteurs, 
			final JSONArray variables, final JSONArray interrupteursLocaux, final JSONArray mots, 
			final JSONObject chronometre, final JSONArray images)
	throws Exception {
		this();
		this.id = id;
		this.numeroMap = numeroMap;
		this.brouillardACharger = Brouillard.creerBrouillardAPartirDeJson(jsonBrouillard);
		this.xHeros = xHeros;
		this.yHeros = yHeros;
		this.directionHeros = directionHeros;
		this.vie = vie;
		this.vieMax = vieMax;
		this.argent = argent;
		
		//objets
		final int[] tableauObjetsPossedes = new int[Objet.objetsDuJeu.length];
		for (Object o : objetsPossedes) {
			final JSONObject objetPossede = (JSONObject) o;
			tableauObjetsPossedes[objetPossede.getInt("numero")] = objetPossede.getInt("quantite");
		}
		this.objetsPossedes = tableauObjetsPossedes;
		
		//quêtes
		final AvancementQuete[] tableauAvancementDesQuetes = new AvancementQuete[Quete.quetesDuJeu.length];
		for (Object o : avancementDesQuetes) {
			final JSONObject avancementQuete = (JSONObject) o;
			tableauAvancementDesQuetes[avancementQuete.getInt("numero")] = AvancementQuete.getEtat(avancementQuete.getString("avancement"));
		}
		this.avancementDesQuetes = tableauAvancementDesQuetes;
		
		//armes
		final boolean[] tableauArmesPossedees = new boolean[Arme.ARMES_DU_JEU.length];
		int nombreDArmesPossedees = 0;
		for (Object o : armesPossedees) {
			final int armePossedee = (Integer) o;
			tableauArmesPossedees[armePossedee] = true;
			nombreDArmesPossedees++;
		}
		this.armesPossedees = tableauArmesPossedees; 
		this.nombreDArmesPossedees = nombreDArmesPossedees;
		
		//gadgets
		final boolean[] tableauDesGadgetsPossedes = new boolean[Gadget.GADGETS_DU_JEU.length];
		int nombreDeGadgetsPossedes = 0;
		for (Object o : gadgetsPossedes) {
			final int gadgetPossede = (Integer) o;
			tableauDesGadgetsPossedes[gadgetPossede] = true;
			nombreDeGadgetsPossedes++;
		}
		this.gadgetsPossedes = tableauDesGadgetsPossedes;
		this.nombreDeGadgetsPossedes = nombreDeGadgetsPossedes;
		
		//lettres a envoyer
		final ArrayList<LettreAEnvoyer> lettresAEnvoyer = new ArrayList<>();
		//TODO parse JSON
		this.lettresAEnvoyer = lettresAEnvoyer;
		
		//lettres recues
		final ArrayList<LettreRecue> lettresRecues = new ArrayList<>();
		//TODO parse JSON
		this.lettresRecues = lettresRecues;
		
		//interrupteurs
		for (Object o : interrupteurs) {
			final int interrupteurActif = (Integer) o;
			this.interrupteurs[interrupteurActif] = true;
		}
		//variables
		for (Object o : variables) {
			final JSONObject variable = (JSONObject) o;
			this.variables[variable.getInt("numero")] = variable.getInt("valeur");
		}
		//interrupteurs locaux
		for (Object o : interrupteursLocaux) {
			final String code = (String) o;
			this.interrupteursLocaux.add(code);
		}
		//mots
		for (int i = 0; i<mots.length(); i++) {
		    final Object o = mots.get(i);
		    if (!JSONObject.NULL.equals(o)) {
		        this.mots[i] = (String) o;
		    }
		}
		//chronometre
		if (chronometre!=null) {
			this.chronometre = new Chronometre(chronometre.getBoolean("croissant"), chronometre.getInt("secondes"));
		}
		//images
		for (Object o : images) {
			final JSONObject descripteur = (JSONObject) o;
			final Integer numero = descripteur.getInt("numero");
			final String nomImage = descripteur.getString("nomImage");
			final BufferedImage image = Graphismes.ouvrirImage("Pictures", nomImage);
			final int x = descripteur.getInt("x");
			final int y = descripteur.getInt("y");
			final boolean centre = descripteur.getBoolean("centre");
			final int zoomX = descripteur.getInt("zoomX");
			final int zoomY = descripteur.getInt("zoomY");
			final int opacite = descripteur.getInt("opacite");
			final String nomModeDeFusion = descripteur.getString("nomImage");
			final ModeDeFusion modeDeFusion = ModeDeFusion.parNom(nomModeDeFusion);
			final int angle = descripteur.getInt("angle");
			final Picture picture = new Picture(image, nomImage, numero, x, y, centre, zoomX, zoomY, opacite, modeDeFusion, angle);
			if (descripteur.has("deplacementActuel")) {
				// L'image est en mouvement
				final JSONObject deplacementImage = descripteur.getJSONObject("deplacementActuel");
				final int nombreDeFrames = deplacementImage.getInt("nombreDeFrames");
				final int dejaFait = deplacementImage.getInt("dejaFait");
				final int xDebut = deplacementImage.getInt("xDebut");
				final int yDebut = deplacementImage.getInt("yDebut");
				final int zoomXDebut = deplacementImage.getInt("zoomXDebut");
				final int zoomYDebut = deplacementImage.getInt("zoomYDebut");
				final int angleDebut = deplacementImage.getInt("angleDebut");
				final int opaciteDebut = deplacementImage.getInt("opaciteDebut");
				final Boolean centreFin = deplacementImage.has("centre") ? deplacementImage.getBoolean("centre") : null;
				final Integer xVar = deplacementImage.has("x") ? deplacementImage.getInt("x") : null;
				final Integer yVar = deplacementImage.has("y") ? deplacementImage.getInt("y") : null;
				final boolean variablesFin = deplacementImage.getBoolean("variables");
				final int xFin = deplacementImage.getInt("xFin");
				final int yFin = deplacementImage.getInt("yFin");
				final Integer zoomXFin = deplacementImage.has("zoomXFin") ? deplacementImage.getInt("zoomXFin") : null;
				final Integer zoomYFin = deplacementImage.has("zoomYFin") ? deplacementImage.getInt("zoomYFin") : null;
				final Integer opaciteFin = deplacementImage.has("opaciteFin") ? deplacementImage.getInt("opaciteFin") : null;
				final ModeDeFusion modeDeFusionFin = deplacementImage.has("modeDeFusion") ? ModeDeFusion.parNom(deplacementImage.getString("modeDeFusion")) : null;
				final Integer angleFin = deplacementImage.has("angleFin") ? deplacementImage.getInt("angleFin") : null;
				final boolean repeterLeDeplacement = deplacementImage.getBoolean("repeterLeDeplacement");
				final boolean attendreLaFinDuDeplacement = deplacementImage.getBoolean("attendreLaFinDuDeplacement");
				final DeplacerImage deplacerImage = new DeplacerImage(numero, nombreDeFrames, centreFin, variablesFin,
						xVar, yVar, zoomXFin, zoomYFin, opaciteFin, modeDeFusionFin, angleFin, 
						repeterLeDeplacement, attendreLaFinDuDeplacement);
				deplacerImage.configurerEnCoursDeRoute(dejaFait, xDebut, yDebut, zoomXDebut, zoomYDebut, angleDebut, opaciteDebut, xFin, yFin);
				picture.deplacementActuel = deplacerImage;
			}
			this.images[numero] = picture;
		}
		// Animations
		Animation.chargerLesAnimationsDuJeu();
				
		this.idArmeEquipee = idArmeEquipee;
		this.idGadgetEquipe = idGadgetEquipe;
	}
	
	/**
	 * Génère une nouvelle Partie vierge.
	 * @return une nouvelle partie
	 * @throws Exception le JSON de paramétrage d'une nouvelle Partie n'a pas été trouvé
	 */
	public static Partie creerNouvellePartie() throws Exception {
		return new Partie();
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

	/**
	 * Enumerer les Parties du jeu.
	 * @param possedes non-applicable
	 * @return association entre numero et Partie
	 */
	public static java.util.Map<Integer, Listable> obtenirTousLesListables(final Boolean possedes) {
		final HashMap<Integer, Listable> parties = new HashMap<Integer, Listable>();
		
		final File dossier = new File(Sauvegarder.NOM_DOSSIER_SAUVEGARDES);
		final File[] fichiers = dossier.listFiles();
		for (int i = 0; i < fichiers.length; i++) {
			if (fichiers[i].isFile()) {
				final String nomPartie = fichiers[i].getName();
				final int numeroPartie = Integer.parseInt(nomPartie.replaceAll("[^0-9]", ""));
				try {
					final Partie partie = ChargerPartie.chargerPartie(numeroPartie);
					parties.put(numeroPartie, partie);
				} catch (Exception e) {
					LOG.error("Impossible de charger la partie numéro "+numeroPartie, e);
				}
			}
		}
		return parties;
	}

	@Override
	public BufferedImage construireImagePourListe(final int largeur, final int hauteur) {
		final BufferedImage vignettePartie = new BufferedImage(largeur, hauteur, Graphismes.TYPE_DES_IMAGES);
		
		// Texte d'avancement
		final ArrayList<String> blabla = new ArrayList<String>();
		final StringBuilder francais = new StringBuilder();
		francais.append(this.mots[0]); //nom du héros
		blabla.add(francais.toString());
		final StringBuilder anglais = new StringBuilder();
		anglais.append(this.mots[0]); //nom du héros
		blabla.add(anglais.toString());
		final BufferedImage nomPartie = new Texte(blabla).getImage();
		Graphismes.superposerImages(vignettePartie, nomPartie, MARGE, MARGE);
		
		// Image d'avancement
		try {
			final BufferedImage avancement = Graphismes.ouvrirImage("Pictures/Avancement", Integer.toString(this.variables[NUMERO_VARIABLE_AVANCEMENT_PARTIE]));
			final int xVignette = largeur-avancement.getWidth()-MARGE;
			final int yVignette = MARGE;
			Graphismes.superposerImages(vignettePartie, avancement, xVignette, yVignette);
		} catch (IOException e) {
			LOG.error("Impossible d'ouvrir l'image d'avancement "+avancement());
		}
		
		
		return vignettePartie;
	}
	
	/**
	 * Code correspondant à l'avancement de la Partie
	 * @return code
	 */
	public int avancement() {
		return this.variables[NUMERO_VARIABLE_AVANCEMENT_PARTIE];
	}
	
	@Override
	public ArrayList<Condition> getConditions() {
		// Afficher toutes les parties dans le Menu
		return null;
	}

	@Override
	public ArrayList<Commande> getComportementSelection() {
		// Rien ne se passe quand le curseur arrive au dessus de la Partie
		return null;
	}

	@Override
	public ArrayList<Commande> getComportementConfirmation() {
		// On charge la Partie
		final ArrayList<Commande> comportementConfirmation = new ArrayList<Commande>();
		comportementConfirmation.add(new ChargerPartie(this.id));
		return comportementConfirmation;
	}

	@Override
	public JSONObject sauvegarderEnJson() {
		final JSONObject jsonPartie = new JSONObject();
		
		jsonPartie.put("vie", this.vie);
		jsonPartie.put("vieMax", this.vieMax);
		jsonPartie.put("argent", this.argent);
		
		//armes
		jsonPartie.put("idArmeEquipee", this.idArmeEquipee);
		final JSONArray armesPossedees = new JSONArray();
		for (int i = 0; i<this.armesPossedees.length; i++) {
			if (this.armesPossedees[i]) {
				armesPossedees.put(i);
			}
		}
		jsonPartie.put("armesPossedees", armesPossedees);
		
		//gadgets
		jsonPartie.put("idGadgetEquipe", this.idGadgetEquipe);
		final JSONArray gadgetsPossedes = new JSONArray();
		for (int i = 0; i<this.gadgetsPossedes.length; i++) {
			if (this.gadgetsPossedes[i]) {
				gadgetsPossedes.put(i);
			}
		}
		jsonPartie.put("gadgetsPossedes", gadgetsPossedes);
		
		//lettres a envoyer
		final JSONArray lettresAEnvoyerJson = new JSONArray();
		//TODO convertir en JSON
		jsonPartie.put("lettresAEnvoyer", lettresAEnvoyerJson);
		
		//lettres recues
		final JSONArray lettresRecuesJson = new JSONArray();
		//TODO convertir en JSON
		jsonPartie.put("lettresRecues", lettresRecuesJson);
		
		//interrupteurs
		final JSONArray interrupteurs = new JSONArray();
		for (int i = 0; i<this.interrupteurs.length; i++) {
			if (this.interrupteurs[i]) {
				interrupteurs.put(i);
			}
		}
		jsonPartie.put("interrupteurs", interrupteurs);
		
		//variables
		final JSONArray variables = new JSONArray();
		for (int i = 0; i<this.variables.length; i++) {
			if (this.variables[i] != 0) {
				final JSONObject variable = new JSONObject();
				variable.put("numero", i);
				variable.put("valeur", this.variables[i]);
				variables.put(variable);
			}
		}
		jsonPartie.put("variables", variables);
		
		//interrupteurs locaux
		final JSONArray interrupteursLocaux = new JSONArray();
		for (int i = 0; i<this.interrupteursLocaux.size(); i++) {
			final String code = this.interrupteursLocaux.get(i); // code de la forme mXXXeXXXiXXX (map, event, interrupteur)
			interrupteursLocaux.put(code);
		}
		jsonPartie.put("interrupteursLocaux", interrupteursLocaux);
		
		//mots
		final JSONArray mots = new JSONArray();
		for (int i = 0; i<this.mots.length; i++) {
			mots.put(this.mots[i]);
		}
		jsonPartie.put("mots", mots);
		
		//chronometre
		if (this.chronometre != null) {
			final JSONObject jsonChronometre = new JSONObject();
			jsonChronometre.put("secondes", this.chronometre.secondes);
			jsonChronometre.put("croissant", this.chronometre.croissant);
			jsonPartie.put("chronometre", jsonChronometre);
		}
		
		//objets
		final JSONArray objetsPossedes = new JSONArray();
		for (int i = 0; i<this.objetsPossedes.length; i++) {
			if (this.objetsPossedes[i] != 0) {
				final JSONObject objet = new JSONObject();
				objet.put("numero", i);
				objet.put("quantite", this.objetsPossedes[i]);
				objetsPossedes.put(objet);
			}
		}
		jsonPartie.put("objetsPossedes", objetsPossedes);
		
		//quêtes
		final JSONArray avancementQuetes = new JSONArray();
		for (int i = 0; i<this.avancementDesQuetes.length; i++) {
			if (this.avancementDesQuetes[i]!= null 
					&& !AvancementQuete.INCONNUE.equals(this.avancementDesQuetes[i])) {
				final JSONObject quete = new JSONObject();
				quete.put("numero", i);
				quete.put("avancement", this.avancementDesQuetes[i].nom);
				avancementQuetes.put(quete);
			}
		}
		jsonPartie.put("avancementQuetes", avancementQuetes);
		
		//images
		final JSONArray jsonImages = new JSONArray();
		for (Picture picture : this.images) {
			//toutes les places de la liste ne sont pas forcement occupees
			if (picture != null) {
				jsonImages.put(picture.sauvegarderEnJson());
			}
		}
		jsonPartie.put("images", jsonImages);
		
		return jsonPartie;
	}

}
