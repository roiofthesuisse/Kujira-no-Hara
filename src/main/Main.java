package main;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jeu.Partie;
import map.LecteurMap;
import map.Map;
import map.Transition;
import map.positionInitiale.PositionInitialeBrute;
import map.positionInitiale.PositionInitiale;
import menu.LecteurMenu;
import menu.Menu;
import net.bull.javamelody.Parameter;
import utilitaire.EmbeddedServer;

/**
 * Point d'entree du programme.
 */
public class Main {
	//constantes
	private static final Logger LOG = LogManager.getLogger(Main.class);
	private static final int PORT_JAVAMELODY = 8080;
	public static final int NOMBRE_DE_THREADS = 5;
	public static final int TAILLE_D_UN_CARREAU = 32;
	
	public static Fenetre fenetre = null;
	public static Lecteur lecteur = null;
	public static Lecteur futurLecteur = null;
	public static boolean quitterLeJeu = false;
	public static int langue;
	private static Partie partie = null;

	
	
	public static ArrayList<String> mesuresDePerformance = new ArrayList<String>();
	
	/**
	 * Point d'entree du programme
	 * @param args pas besoin d'arguments
	 */
	public static void main(final String[] args) {
		// La premiere Fenetre n'est pas en plein ecran
		fenetre = new Fenetre(false);
		
		// Le premier Lecteur est celui du Menu titre
		final Menu menuTitre = Menu.creerMenuDepuisJson("Titre", null);
		//la selection initiale est "chargerPartie" s'il y a deja une sauvegarde, sinon "nouvellePartie"
		int selectionInitiale = 0;
		try {
			final int nombreDeFichiersDeSauvegarde = (int) Files.list(Paths.get("./saves")).count();
			if (nombreDeFichiersDeSauvegarde > 0) {
				selectionInitiale = 1;
			}
		} catch (IOException e) {
			LOG.error("Impossible de compter les fichiers de sauvegarde !", e);
		}
		lecteur = new LecteurMenu(menuTitre, null, selectionInitiale);
		
		// Mesurer les performances
		//lancerSupervisionJavaMelody();
		
		// Acceleration du calcul graphique
		System.setProperty("sun.java2d.opengl", "True");
		
		// On demarre le lecteur
		while (!Main.quitterLeJeu) {
			//on lance le Lecteur
			Main.lecteur.demarrer(); //boucle tant que le Lecteur est allume
			
			//si on est ici, c'est que le Lecteur a ete eteint par une Commande Event
			//y en a-t-il un autre apres ?
			if (Main.futurLecteur != null) {
				if (!Main.quitterLeJeu) {
					//on passe au Lecteur suivant
					Main.lecteur = Main.futurLecteur;
					Main.futurLecteur = null;
				}
			} else {
				//pas de Lecteur a suivre
				//on eteint le jeu
				Main.quitterLeJeu = true;
			}
		}
		// Il n'y a plus de Lecteur a suivre : le jeu est eteint
		
		// Export CSV
		exporterCsv();
		
		// On ferme la Fenetre
		fenetre.fermer();
		
		LOG.info("Arret total du programme");
		System.exit(0);
	}
	
	/**
	 * La Fenetre a une partie selectionnee, on l'ouvre.
	 */
	public static void ouvrirLaPartie() {
		if (partie == null) {
			try {
				partie = Partie.creerNouvellePartie();
			} catch (Exception e) {
				LOG.error("Impossible de charger la partie.", e);
			}
		}
		futurLecteur = new LecteurMap();
		((LecteurMap) futurLecteur).transition = Transition.AUCUNE; // aucune Transition pour le premier Lecteur
		final PositionInitiale positionInitiale = new PositionInitialeBrute(partie.xHeros, partie.yHeros, 
				partie.directionHeros);
		try {
			((LecteurMap) futurLecteur).map = new Map(
					partie.numeroMap, 
					(LecteurMap) futurLecteur, 
					null,
					partie.brouillardACharger, 
					positionInitiale
			);
		} catch (Exception e) {
			LOG.error("Impossible de charger la map numero "+partie.numeroMap, e);
		}
		lecteur.allume = false; //TODO est-ce utile ?
	}
	
	/**
	 * Obtenir la Partie actuelle
	 * @return la Partie en cours
	 */
	public static Partie getPartieActuelle() {
		return partie;
	}
	
	/**
	 * Memoriser la Partie actuelle
	 * @param partieActuelle a faire memoriser par la Fenetre
	 */
	public static void setPartieActuelle(final Partie partieActuelle) {
		partie = partieActuelle;
	}

	/**
	 * Superviser les performances avec JavaMelody.
	 */
	private static void lancerSupervisionJavaMelody() {
		final HashMap<Parameter, String> parametresJavaMelody = new HashMap<>();
		// Authentification
		parametresJavaMelody.put(Parameter.AUTHORIZED_USERS, "admin:password");
		// Dossier d'enregistrement
		parametresJavaMelody.put(Parameter.STORAGE_DIRECTORY, "C://Users/Public/tmp/javamelody");
		// Frequence d'echantillonnage
		parametresJavaMelody.put(Parameter.SAMPLING_SECONDS, "1.0");
		// Emplacement des rapports d'analyse
		parametresJavaMelody.put(Parameter.MONITORING_PATH, "/");
		try {
			// Demarrer le server d'affichage de l'analyse JavaMelody
			LOG.info("Demarrage de JavaMelody...");
			EmbeddedServer.start(PORT_JAVAMELODY, parametresJavaMelody);
			LOG.info("JavaMelody est demarre.");
		} catch (Exception e) {
			LOG.error("Impossible de lancer l'analyse de performance avec JavaMelody.", e);
		}
	}
	
	/**
	 * Exporter les mesures de performances en tant que fichier CSV.
	 */
	private static void exporterCsv() {
		final Path file = Paths.get("C:/Users/Public/kujira-perf3.csv");
		try {
			Files.write(file, mesuresDePerformance, Charset.forName("UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
