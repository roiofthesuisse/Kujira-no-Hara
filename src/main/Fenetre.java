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

@SuppressWarnings("serial")
public class Fenetre extends JFrame implements KeyListener{
	private static Fenetre maFenetre = null;
	public static String titre = "Le meilleur jeu du monde";
	public static int largeurParDefaut = 640;
	public static int hauteurParDefaut = 480;
	public JLabel labelEcran = null;
	public Lecteur lecteur = null;
	public Partie partie = null;
	public static Lecteur futurLecteur = null;
	public ArrayList<Integer> touchesPressees = null;
	
	/**
	 * La fenêtre affiche l'écran du jeu, mais a aussi un rôle de listener pour les entrées clavier.
	 */
	private Fenetre(){
		super(titre);
		this.labelEcran = new JLabel();
		this.lecteur = new LecteurMenu(this);
		MenuTitre menuTitre = new MenuTitre((LecteurMenu)this.lecteur);
		((LecteurMenu)this.lecteur).menu = menuTitre;
		this.touchesPressees = new ArrayList<Integer>();
		this.addKeyListener(this);
		
		//démarrer JavaFX pour pouvoir ensuite lire des fichiers MP3
		@SuppressWarnings("unused")
		JFXPanel fxPanel = new JFXPanel();
	}
	
	public static Fenetre getFenetre(){
		if(maFenetre == null){
			maFenetre = new Fenetre();
		}
		return maFenetre;
	}
	
	public static Insets obtenirLesMarges(){
		JFrame fenetreBidon = new JFrame();
		fenetreBidon.setVisible(true);
		Insets marges = fenetreBidon.getInsets();
		fenetreBidon.dispose();
		return marges;
	}
	
	public static void ouvrirFenetre(){
		Fenetre fenetre = getFenetre();
		fenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Insets marges = obtenirLesMarges();
		int margeHorizontale = marges.left+marges.right;
		int margeVerticale = marges.top+marges.bottom;
		fenetre.setSize(largeurParDefaut+margeHorizontale, hauteurParDefaut+margeVerticale);
		try {
			ArrayList<Image> icones = new ArrayList<Image>();
			BufferedImage iconePetite = ImageIO.read(new File(".\\ressources\\Graphics\\Icons\\baleine icone.png"));
			BufferedImage iconeGrande = ImageIO.read(new File(".\\ressources\\Graphics\\Icons\\baleine icone grand.png"));
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
	 * La fenêtre confie l'affichage d'un menu/map à un lecteur de menu/map.
	 * Si jamais un futur lecteur est désigné, on effectue le remplacement.
	 */
	public static void demarrerAffichage(){
		while(true){
			maFenetre.lecteur.demarrer();
			while(futurLecteur == null){}
			maFenetre.lecteur = futurLecteur;
		}
	}
	
	//TODO
	/*
	public void changerLecteur(Lecteur nouveauLecteur){
		System.out.println("Changement de lecteur");
		this.lecteur.allume = false;
		this.lecteur = nouveauLecteur;
		demarrerAffichage();
	}
	*/
	
	/**
	 * Changer l'image affichée dans la fenêtre de jeu.
	 * @param image nouvelle image à afficher dans la fenêtre
	 */
	public void actualiserAffichage(Image image){
		this.invalidate();
		this.remove(labelEcran);
		labelEcran = new JLabel(new ImageIcon(image));
		this.add(labelEcran);
		this.revalidate();
	}
	
	public static void main(String[] args){
		ouvrirFenetre();
		demarrerAffichage();
	}

	
	@Override
	public void keyPressed(KeyEvent event) {
		Integer keyCode = event.getKeyCode();
		if(! touchesPressees.contains(keyCode)){
			touchesPressees.add(keyCode);
			GestionClavier.keyPressed(keyCode,this);
		}
	}

	@Override
	public void keyReleased(KeyEvent event) {
		Integer keyCode = event.getKeyCode();
		touchesPressees.remove(keyCode);
		GestionClavier.keyReleased(keyCode,this);
	}

	@Override
	public void keyTyped(KeyEvent event) {
		//rien
	}
	
	/**
	 * La fenêtre a une partie sélectionnée, on l'ouvre.
	 */
	public void ouvrirLaPartie() {
		if(this.partie == null){
			partie = Partie.creerNouvellePartie();
		}
		futurLecteur = new LecteurMap(this);
		try {
			((LecteurMap)futurLecteur).map = new Map(partie.numeroMap, (LecteurMap)futurLecteur, partie.xHeros, partie.yHeros, partie.directionHeros);
		} catch (FileNotFoundException e) {
			System.err.println("Impossible de charger la map numero "+partie.numeroMap);
			e.printStackTrace();
		}
		this.lecteur.allume = false;
	}
}
