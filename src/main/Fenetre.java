package main;

import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javafx.embed.swing.JFXPanel;
import main.capteurs.CapteurClavier;
import main.capteurs.CapteurFenetre;
import main.capteurs.CapteurSouris;

import javax.swing.JFrame;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import utilitaire.graphismes.Graphismes;

/**
 * La Fenêtre affiche l'écran du jeu, mais a aussi un rôle de listener pour les entrées clavier.
 */
@SuppressWarnings("serial")
public final class Fenetre extends JFrame {
	//constantes
	private static final Logger LOG = LogManager.getLogger(Fenetre.class);
	public static final int LARGEUR_ECRAN = 640;
	public static final int HAUTEUR_ECRAN = 480;
	private static int largeurPleinEcran;
	private static int hauteurPleinEcran;
	private static final String titre = "Le meilleur jeu du monde";

	private final boolean pleinEcran;
	private int margeGauche, margeHaut;
	public BufferStrategy bufferStrategy;
	private Graphics bufferStrategyGraphics;
	private GraphicsDevice device;
	
	/**
	 * Constructeur explicite
	 */
	Fenetre(final boolean pleinEcran) {
		super(titre);
		this.pleinEcran = pleinEcran;

		this.addKeyListener(new CapteurClavier(this)); //récupérer les entrées Clavier
		this.addWindowFocusListener(new CapteurFenetre(this)); //pauser le jeu si Fenetre inactive
		this.addMouseListener(new CapteurSouris(this)); //plein écran si double-clic
		this.device = this.getGraphicsConfiguration().getDevice();
		
		// Démarrer JavaFX pour pouvoir ensuite lire des fichiers MP3
		@SuppressWarnings("unused")
		final JFXPanel fxPanel = new JFXPanel();
		
		//-------------------------------------------------------------------------------
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		final Insets marges = obtenirLesMarges();
		this.margeGauche = marges.left;
		this.margeHaut = marges.top;
		this.setSize(marges.left+LARGEUR_ECRAN+marges.right, marges.top+HAUTEUR_ECRAN+marges.bottom);
		try {
			final ArrayList<Image> icones = new ArrayList<Image>();
			final BufferedImage iconePetite = Graphismes.ouvrirImage("Icons", "baleine icone.png");
			final BufferedImage iconeGrande = Graphismes.ouvrirImage("Icons", "baleine icone grand.png");
			icones.add(iconePetite);
			icones.add(iconeGrande);
			this.setIconImages(icones);
		} catch (IOException e) {
			//problème avec les icones
			LOG.error("Problème avec les icônes", e);
		}
		this.setResizable(false);
		this.setVisible(true);
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
	 * La Fenêtre confie l'affichage d'un Menu/Map à un Lecteur.
	 * Si jamais le Lecteur actuel est éteint et qu'un futur Lecteur est désigné, on effectue le remplacement.
	 * Si aucun futur Lecteur n'est désigné, la Fenêtre se ferme.
	 */
	public void demarrerAffichage() {
		// Utiliser une BufferStrategy double
		this.createBufferStrategy(2);
		this.bufferStrategy = this.getBufferStrategy();
		this.bufferStrategyGraphics = this.bufferStrategy.getDrawGraphics();
		
		while (!Main.quitterLeJeu) {
			//on lance le Lecteur
			Main.lecteur.demarrer(); //boucle tant que le Lecteur est allumé
			
			//si on est ici, c'est que le Lecteur a été éteint par une Commande Event
			//y en a-t-il un autre après ?
			if (Main.futurLecteur != null) {
				if (!Main.quitterLeJeu) {
					//on passe au Lecteur suivant
					Main.lecteur = Main.futurLecteur;
					Main.futurLecteur = null;
				}
			} else {
				//pas de Lecteur à suivre
				//on éteint le jeu
				Main.quitterLeJeu = true;
			}
		}
		//le jeu a été éteint ou bien il n'y a plus de Lecteur à suivre
		
		// On ferme la Fenêtre
		this.bufferStrategyGraphics.dispose();
		this.bufferStrategy.dispose();
		this.dispose();
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
	 * Entrer ou quitter le mode plein écran.
	 */
	public void pleinEcran() {
		//TODO fermer la Fenêtre et en utiliser une nouvelle
		if (this.device.isFullScreenSupported()) {
			// Est-on déjà en mode plein écran ?
			if (this.pleinEcran) {
				// On quitte le mode plein écran
				this.device.setFullScreenWindow(null);
				this.setUndecorated(false);
			} else {
				// On entre en mode plein écran
				this.device.setFullScreenWindow(this);
				this.setUndecorated(true);
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
