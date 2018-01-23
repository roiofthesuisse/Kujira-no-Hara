package main;

import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import javafx.embed.swing.JFXPanel;
import jeu.Partie;
import main.capteurs.CapteurClavier;
import main.capteurs.CapteurFenetre;
import main.capteurs.CapteurSouris;

import javax.swing.JFrame;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import map.LecteurMap;
import map.Map;
import map.Transition;
import menu.LecteurMenu;
import menu.Menu;
import net.bull.javamelody.Parameter;
import utilitaire.EmbeddedServer;
import utilitaire.graphismes.Graphismes;

/**
 * La Fenêtre affiche l'écran du jeu, mais a aussi un rôle de listener pour les entrées clavier.
 */
@SuppressWarnings("serial")
public final class Fenetre extends JFrame {
	//constantes
	private static final Logger LOG = LogManager.getLogger(Fenetre.class);
	public static final int TAILLE_D_UN_CARREAU = 32;
	public static final int LARGEUR_ECRAN = 640;
	public static final int HAUTEUR_ECRAN = 480;
	private static int largeurPleinEcran;
	private static int hauteurPleinEcran;
	private static final int PORT_JAVAMELODY = 8080;
	public static final int NOMBRE_DE_THREADS = 5;
	
	private static Fenetre maFenetre = null;
	public static String titre = "Le meilleur jeu du monde";
	public static int langue;

	private int margeGauche, margeHaut;
	public BufferStrategy bufferStrategy;
	private Graphics bufferStrategyGraphics;
	private GraphicsDevice device;
	public Lecteur lecteur = null;
	private Partie partie = null;
	public Lecteur futurLecteur = null;
	public boolean quitterLeJeu = false;
	
	public ArrayList<String> mesuresDePerformance = new ArrayList<String>();
	
	/**
	 * Constructeur explicite
	 */
	private Fenetre() {
		super(titre);
		final Menu menuTitre = Menu.creerMenuDepuisJson("Titre", null);
		
		//la sélection initiale est ChargerPartie s'il y a déjà une sauvegarde, sinon nouvellePartie
		int selectionInitiale = 0;
		try {
			final int nombreDeFichiersDeSauvegarde = (int) Files.list(Paths.get(".\\saves")).count();
			if (nombreDeFichiersDeSauvegarde > 0) {
				selectionInitiale = 1;
			}
		} catch (IOException e) {
			LOG.error("Impossible de compter les fichiers de sauvegarde !", e);
		}
		this.lecteur = new LecteurMenu(this, menuTitre, null, selectionInitiale);

		this.addKeyListener(new CapteurClavier(this)); //récupérer les entrées Clavier
		this.addWindowFocusListener(new CapteurFenetre(this)); //pauser le jeu si Fenetre inactive
		this.addMouseListener(new CapteurSouris(this)); //plein écran si double-clic
		this.device = this.getGraphicsConfiguration().getDevice();
		
		// Démarrer JavaFX pour pouvoir ensuite lire des fichiers MP3
		@SuppressWarnings("unused")
		final JFXPanel fxPanel = new JFXPanel();
	}
	
	/**
	 * Obtenir à tout moment l'unique Fenêtre active (singleton).
	 * @return Fenêtre active
	 */
	public static Fenetre getFenetre() {
		if (maFenetre == null) {
			maFenetre = new Fenetre();
		}
		return maFenetre;
	}
	
	/**
	 * Calculer la taille des marges de la Fenêtre (variables selon l'Operating System).
	 * En vérité la Fenêtre est un peu plus grande que l'écran : elle a des marges tout autour.
	 * @return marges à prendre en compte
	 */
	public static Insets obtenirLesMarges() {
		final JFrame fenetreBidon = new JFrame();
		fenetreBidon.setVisible(true);
		final Insets marges = fenetreBidon.getInsets();
		fenetreBidon.dispose();
		return marges;
	}
	
	/**
	 * Ouvrir la Fenêtre, afficher son titre et son icône.
	 * Son contenu est encore vide pour l'instant, car l'affichage n'a pas démarré.
	 */
	public static void ouvrirFenetre() {
		final Fenetre fenetre = getFenetre();
		fenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		fenetre.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(final java.awt.event.WindowEvent windowEvent) {
		        fenetre.fermer(); //exécuter la méthode fermer() lorsqu'on ferme la Fenêtre
		    }
		});
		final Insets marges = obtenirLesMarges();
		fenetre.margeGauche = marges.left;
		fenetre.margeHaut = marges.top;
		fenetre.setSize(marges.left+LARGEUR_ECRAN+marges.right, marges.top+HAUTEUR_ECRAN+marges.bottom);
		try {
			final ArrayList<Image> icones = new ArrayList<Image>();
			final BufferedImage iconePetite = Graphismes.ouvrirImage("Icons", "baleine icone.png");
			final BufferedImage iconeGrande = Graphismes.ouvrirImage("Icons", "baleine icone grand.png");
			icones.add(iconePetite);
			icones.add(iconeGrande);
			fenetre.setIconImages(icones);
		} catch (IOException e) {
			//problème avec les icones
			LOG.error("Problème avec les icônes", e);
		}
		fenetre.setResizable(false);
		fenetre.setVisible(true);
	}
	
	/**
	 * La Fenêtre confie l'affichage d'un Menu/Map à un Lecteur.
	 * Si jamais le Lecteur actuel est éteint et qu'un futur Lecteur est désigné, on effectue le remplacement.
	 * Si aucun futur Lecteur n'est désigné, la Fenêtre se ferme.
	 */
	public void demarrerAffichage() {
		// Accélération du calcul graphique
		System.setProperty("sun.java2d.opengl", "True");
		
		// Utiliser une BufferStrategy double
		this.createBufferStrategy(2);
		this.bufferStrategy = this.getBufferStrategy();
		this.bufferStrategyGraphics = this.bufferStrategy.getDrawGraphics();
		
		while (!this.quitterLeJeu) {
			//on lance le Lecteur
			this.lecteur.demarrer(); //boucle tant que le Lecteur est allumé
			
			//si on est ici, c'est que le Lecteur a été éteint par une Commande Event
			//y en a-t-il un autre après ?
			if (this.futurLecteur!=null) {
				if (!this.quitterLeJeu) {
					//on passe au Lecteur suivant
					this.lecteur = this.futurLecteur;
					this.futurLecteur = null;
				}
			} else {
				//pas de Lecteur à suivre
				//on éteint le jeu
				this.quitterLeJeu = true;
			}
		}
		//le jeu a été éteint ou bien il n'y a plus de Lecteur à suivre
	}
	
	/**
	 * Changer l'image affichée dans la Fenêtre de jeu.
	 * @param image nouvelle image à afficher dans la fenêtre
	 */
	public void actualiserAffichage(final BufferedImage image) {
		//do {
			// S'assurer que le contenu du tampon graphique reste cohérent
			//do {
				// Il faut un nouveau contexte graphique à chaque tour de boucle pour valider la stratégie
				//final Graphics graphics = this.bufferStrategy.getDrawGraphics();
				final BufferedImage imageAgrandie;
				if (this.device.getFullScreenWindow() != null) {
					imageAgrandie = Graphismes.redimensionner(image, getLargeurPleinEcran(), getHauteurPleinEcran());
				} else {
					imageAgrandie = image;
				}
				// Dessiner l'écran de cette frame-ci
				bufferStrategyGraphics.drawImage(imageAgrandie, this.margeGauche, this.margeHaut, null);
				// Libérer le contexte graphique
				//graphics.dispose();
				// Répéter le rendu si jamais le contenu du tampon est restauré
			//} while (this.bufferStrategy.contentsRestored());
			// Afficher le tampon
			this.bufferStrategy.show();
			// Répéter le rendu si le tampon a été perdu
		//} while (this.bufferStrategy.contentsLost());
	}
	
	/**
	 * Point d'entrée du programme
	 * @param args pas besoin d'arguments
	 */
	public static void main(final String[] args) {
		ouvrirFenetre();
		lancerSupervisionJavaMelody();
		maFenetre.demarrerAffichage();
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
		// Fréquence d'échantillonnage
		parametresJavaMelody.put(Parameter.SAMPLING_SECONDS, "1.0");
		// Emplacement des rapports d'analyse
		parametresJavaMelody.put(Parameter.MONITORING_PATH, "/");
		try {
			// Démarrer le server d'affichage de l'analyse JavaMelody
			LOG.info("Démarrage de JavaMelody...");
			EmbeddedServer.start(PORT_JAVAMELODY, parametresJavaMelody);
			LOG.info("JavaMelody est démarré.");
		} catch (Exception e) {
			LOG.error("Impossible de lancer l'analyse de performance avec JavaMelody.", e);
		}
	}

	
	/**
	 * La Fenêtre a une partie sélectionnée, on l'ouvre.
	 */
	public void ouvrirLaPartie() {
		if (this.partie == null) {
			try {
				this.partie = Partie.creerNouvellePartie();
			} catch (Exception e) {
				LOG.error("Impossible de charger la partie.");
				e.printStackTrace();
			}
		}
		this.futurLecteur = new LecteurMap(this, Transition.AUCUNE);
		try {
			((LecteurMap) futurLecteur).map = new Map(
					this.partie.numeroMap, 
					(LecteurMap) this.futurLecteur, 
					null,
					this.partie.brouillardACharger,
					this.partie.xHeros, 
					this.partie.yHeros, 
					this.partie.directionHeros,
					0, // pas de décalage car ce n'est pas un changement de Map
					0  // pas de décalage car ce n'est pas un changement de Map
			);
		} catch (Exception e) {
			LOG.error("Impossible de charger la map numero "+partie.numeroMap);
			e.printStackTrace();
		}
		this.lecteur.allume = false; //TODO est-ce utile ?
	}
	
	/**
	 * Fermer la Fenêtre et quitter le jeu
	 */
	public void fermer() {
		this.bufferStrategyGraphics.dispose();
		exporterCsv();
		System.exit(0);
	}
	
	/**
	 * Exporter les mesures de performances en tant que fichier CSV.
	 */
	private void exporterCsv() {
		final Path file = Paths.get("C:/Users/Public/kujira-perf2.csv");
		try {
			Files.write(file, this.mesuresDePerformance, Charset.forName("UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Obtenir la Partie actuelle
	 * @return la Partie en cours
	 */
	public static Partie getPartieActuelle() {
		return maFenetre.partie;
	}
	
	/**
	 * Mémoriser la Partie actuelle
	 * @param partieActuelle à faire mémoriser par la Fenetre
	 */
	public void setPartieActuelle(final Partie partieActuelle) {
		this.partie = partieActuelle;
	}

	/**
	 * Entrer ou quitter le mode plein écran.
	 */
	public void pleinEcran() {
		if (this.device.isFullScreenSupported()) {
			// Est-on déjà en mode plein écran ?
			if (this.device.getFullScreenWindow() == null) {
				// On entre en mode plein écran
				this.device.setFullScreenWindow(this);
				this.setUndecorated(true);
			} else {
				// On quitte le mode plein écran
				this.device.setFullScreenWindow(null);
				this.setUndecorated(false);
			}
		}
	}
	
	/**
	 * Calculer la largeur et la hauteur de l'écran en mode plein écran.
	 * @return largeur (en pixels)
	 */
	private int getLargeurPleinEcran() {
		if (largeurPleinEcran <= 0) {
			final Rectangle bounds = getGraphicsConfiguration().getBounds();
			largeurPleinEcran = (int) (bounds.getMaxX() - bounds.getMinX());
			hauteurPleinEcran = (int) (bounds.getMaxY() - bounds.getMinY());
		}
		return largeurPleinEcran;
	}
	
	/**
	 * Obtenir la hauteur de l'écran en mode plein écran.
	 * @return hauteur (en pixels)
	 */
	private int getHauteurPleinEcran() {
		return hauteurPleinEcran;
	}

}
