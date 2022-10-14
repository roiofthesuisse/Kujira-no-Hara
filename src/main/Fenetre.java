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

import javax.swing.JFrame;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.capteurs.CapteurClavier;
import main.capteurs.CapteurFenetre;
import main.capteurs.CapteurSouris;
import utilitaire.graphismes.Graphismes;

/**
 * La Fenetre affiche l'ecran du jeu, mais a aussi un role de listener pour les entrees clavier.
 */
@SuppressWarnings("serial")
public final class Fenetre extends JFrame {
	//constantes
	private static final Logger LOG = LogManager.getLogger(Fenetre.class);
	public static final int LARGEUR_ECRAN = 640;
	public static final int HAUTEUR_ECRAN = 480;
	private static int largeurPleinEcran;
	private static int hauteurPleinEcran;
	private static final String TITRE = "Le meilleur jeu du monde";

	private final boolean pleinEcran;
	private int margeGauche, margeHaut;
	public BufferStrategy bufferStrategy;
	private Graphics bufferStrategyGraphics;
	private GraphicsDevice device;
	
	/**
	 * Constructeur explicite
	 * @param pleinEcran faut-il ouvrir la Fenetre en plein ecran ?
	 */
	Fenetre(final boolean pleinEcran) {
		super(TITRE);
		this.pleinEcran = pleinEcran;
		
		// Capteurs d'entrees
		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		this.addKeyListener(new CapteurClavier()); //recuperer les entrees Clavier
		this.addWindowListener(new CapteurFenetre()); //pauser le jeu si Fenetre inactive
		this.addMouseListener(new CapteurSouris()); //plein ecran si double-clic
		this.device = this.getGraphicsConfiguration().getDevice();
		
		// Demarrer JavaFX pour pouvoir ensuite lire des fichiers MP3
		// TODO ne plus utiliser de fichiers MP3 !
		//@SuppressWarnings("unused")
		//final JFXPanel fxPanel = new JFXPanel();
		
		// Dimensionnement
		if (this.pleinEcran) {
			// Mode plein ecran
			this.setUndecorated(true);
			this.device.setFullScreenWindow(this);
			
		} else {
			// Mode fenetre 
			final Insets marges = obtenirLesMarges();
			this.margeGauche = marges.left;
			this.margeHaut = marges.top;
			this.setSize(marges.left+LARGEUR_ECRAN+marges.right, marges.top+HAUTEUR_ECRAN+marges.bottom);
			this.device.setFullScreenWindow(null);
			this.setUndecorated(false);
		}
		this.setResizable(false);
		
		// Icones
		try {
			final ArrayList<Image> icones = new ArrayList<Image>();
			final BufferedImage iconePetite = Graphismes.ouvrirImage("Icons", "baleine icone.png");
			final BufferedImage iconeGrande = Graphismes.ouvrirImage("Icons", "baleine icone grand.png");
			icones.add(iconePetite);
			icones.add(iconeGrande);
			this.setIconImages(icones);
		} catch (IOException e) {
			//probleme avec les icones
			LOG.error("probleme avec les ic�nes", e);
		}
		
		// On fait apparaitre la Fenetre
		this.setVisible(true);
		
		// Strategie d'affichage
		this.createBufferStrategy(1);
		this.bufferStrategy = this.getBufferStrategy();
		this.bufferStrategyGraphics = this.bufferStrategy.getDrawGraphics();
	}

	
	/**
	 * Calculer la taille des marges de la Fenetre (variables selon l'Operating System).
	 * En verite la Fenetre est un peu plus grande que l'ecran : elle a des marges tout autour.
	 * @return marges a prendre en compte
	 */
	public static Insets obtenirLesMarges() {
		final JFrame fenetreBidon = new JFrame();
		fenetreBidon.setVisible(true);
		final Insets marges = fenetreBidon.getInsets();
		fenetreBidon.dispose();
		return marges;
	}
	
	/**
	 * Changer l'image affichee dans la Fenetre de jeu.
	 * @param image nouvelle image a afficher dans la Fenetre
	 */
	public void actualiserAffichage(final BufferedImage image) {
		//do {
			// S'assurer que le contenu du tampon graphique reste coh�rent
			//do {
				// Il faut un nouveau contexte graphique a chaque tour de boucle pour valider la strategie
				//final Graphics graphics = this.bufferStrategy.getDrawGraphics();
				final BufferedImage imageAgrandie;
				if (this.device.getFullScreenWindow() != null) {
					imageAgrandie = Graphismes.redimensionner(image, getLargeurPleinEcran(), getHauteurPleinEcran());
				} else {
					imageAgrandie = image;
				}
				// Dessiner l'ecran de cette frame-ci
				bufferStrategyGraphics.drawImage(imageAgrandie, this.margeGauche, this.margeHaut, null);
				// Liberer le contexte graphique
				//graphics.dispose();
				// Repeter le rendu si jamais le contenu du tampon est restaure
			//} while (this.bufferStrategy.contentsRestored());
			// Afficher le tampon
			this.bufferStrategy.show();
			// Repeter le rendu si le tampon a ete perdu
		//} while (this.bufferStrategy.contentsLost());
		
		// On n'aura plus jamais besoin de l'image
		image.getGraphics().dispose();
	}

	/**
	 * Entrer ou quitter le mode plein ecran.
	 */
	public static void pleinEcran() {
		if (Main.fenetre.device.isFullScreenSupported()) {
			final Fenetre ancienneFenetre =  Main.fenetre;
			Main.fenetre = new Fenetre(!Main.fenetre.pleinEcran);
			ancienneFenetre.fermer();
		}
	}
	
	/**
	 * Calculer la largeur et la hauteur de l'ecran en mode plein ecran.
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
	 * Obtenir la hauteur de l'ecran en mode plein ecran.
	 * @return hauteur (en pixels)
	 */
	private int getHauteurPleinEcran() {
		return hauteurPleinEcran;
	}

	/**
	 * Fermer la Fenetre.
	 */
	public void fermer() {
		this.bufferStrategyGraphics.dispose();
		this.bufferStrategy.dispose();
		this.dispose();
	}

}
