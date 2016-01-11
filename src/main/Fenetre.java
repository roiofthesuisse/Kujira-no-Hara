package main;

import java.awt.Image;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javafx.embed.swing.JFXPanel;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import bibliothequeMenu.MenuTitre;
import map.LecteurMap;
import map.Map;
import menu.LecteurMenu;
import utilitaire.GestionClavier;

/**
 * La Fenêtre affiche l'écran du jeu, mais a aussi un rôle de listener pour les entrées clavier.
 */
@SuppressWarnings("serial")
public final class Fenetre extends JFrame implements KeyListener {
	//constantes
	public static final int TAILLE_D_UN_CARREAU = 32;
	public static final int LARGEUR_ECRAN = 640;
	public static final int HAUTEUR_ECRAN = 480;
	
	private static Fenetre maFenetre = null;
	public static String titre = "Le meilleur jeu du monde";

	public JLabel labelEcran = null;
	public Lecteur lecteur = null;
	private Partie partie = null;
	public Lecteur futurLecteur = null;
	public ArrayList<Integer> touchesPressees = null;
	public boolean quitterLeJeu = false;
	
	/**
	 * Constructeur explicite
	 */
	private Fenetre() {
		super(titre);
		this.labelEcran = new JLabel();
		this.lecteur = new LecteurMenu(this, null);
		final MenuTitre menuTitre = new MenuTitre((LecteurMenu) this.lecteur);
		((LecteurMenu) this.lecteur).menu = menuTitre;
		this.touchesPressees = new ArrayList<Integer>();
		this.addKeyListener(this);
		
		//démarrer JavaFX pour pouvoir ensuite lire des fichiers MP3
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
		final Insets marges = obtenirLesMarges();
		final int margeHorizontale = marges.left+marges.right;
		final int margeVerticale = marges.top+marges.bottom;
		fenetre.setSize(LARGEUR_ECRAN+margeHorizontale, HAUTEUR_ECRAN+margeVerticale);
		try {
			final ArrayList<Image> icones = new ArrayList<Image>();
			final BufferedImage iconePetite = ImageIO.read(new File(".\\ressources\\Graphics\\Icons\\baleine icone.png"));
			final BufferedImage iconeGrande = ImageIO.read(new File(".\\ressources\\Graphics\\Icons\\baleine icone grand.png"));
			icones.add(iconePetite);
			icones.add(iconeGrande);
			fenetre.setIconImages(icones);
		} catch (IOException e) {
			//problème avec les icones
			e.printStackTrace();
		}
		fenetre.setResizable(false);
		fenetre.setVisible(true);
	}
	
	/**
	 * La Fenêtre confie l'affichage d'un Menu/Map à un Lecteur.
	 * Si jamais le Lecteur actuel est éteint et qu'un futur Lecteur est désigné, on effectue le remplacement.
	 * Si aucun futur Lecteur n'est désigné, la Fenêtre se ferme.
	 */
	public static void demarrerAffichage() {
		while (!maFenetre.quitterLeJeu) {
			//on lance le Lecteur
			maFenetre.lecteur.demarrer(); //boucle tant que le Lecteur est allumé
			
			//si on est ici, c'est que le Lecteur a été éteint par une Commande Event
			//y en a-t-il un autre après ?
			if (maFenetre.futurLecteur!=null) {
				if (!maFenetre.quitterLeJeu) {
					//on passe au Lecteur suivant
					maFenetre.lecteur = maFenetre.futurLecteur;
					maFenetre.futurLecteur = null;
				}
			} else {
				//pas de Lecteur à suivre
				//on éteint le jeu
				maFenetre.quitterLeJeu = true;
			}
		}
		//le jeu a été éteint ou bien il n'y a plus de Lecteur à suivre
	}
	
	/**
	 * Changer l'image affichée dans la Fenêtre de jeu.
	 * @param image nouvelle image à afficher dans la fenêtre
	 */
	public void actualiserAffichage(final Image image) {
		this.invalidate();
		this.remove(this.labelEcran);
		this.labelEcran = new JLabel(new ImageIcon(image));
		this.add(this.labelEcran);
		this.revalidate();
	}
	
	/**
	 * Point d'entrée du programme
	 * @param args pas besoin d'arguments
	 */
	public static void main(final String[] args) {
		ouvrirFenetre();
		demarrerAffichage();
	}

	
	@Override
	public void keyPressed(final KeyEvent event) {
		final Integer keyCode = event.getKeyCode();
		if (!this.touchesPressees.contains(keyCode)) {
			this.touchesPressees.add(keyCode);
			GestionClavier.keyPressed(keyCode, this.lecteur);
		}
	}

	@Override
	public void keyReleased(final KeyEvent event) {
		final Integer keyCode = event.getKeyCode();
		this.touchesPressees.remove(keyCode);
		GestionClavier.keyReleased(keyCode, this.lecteur);
	}

	@Override
	public void keyTyped(final KeyEvent event) {
		//rien
	}
	
	/**
	 * La Fenêtre a une partie sélectionnée, on l'ouvre.
	 */
	public void ouvrirLaPartie() {
		if (this.partie == null) {
			this.partie = Partie.creerNouvellePartie();
		}
		this.futurLecteur = new LecteurMap(this);
		try {
			((LecteurMap) futurLecteur).map = new Map(this.partie.numeroMap, (LecteurMap) this.futurLecteur, this.partie.xHeros, this.partie.yHeros, this.partie.directionHeros);
		} catch (FileNotFoundException e) {
			System.err.println("Impossible de charger la map numero "+partie.numeroMap);
			e.printStackTrace();
		}
		this.lecteur.allume = false; //TODO est-ce utile ?
	}
	
	/**
	 * Fermer la Fenêtre et quitter le jeu
	 */
	public void fermer() {
		System.exit(0);
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
}
