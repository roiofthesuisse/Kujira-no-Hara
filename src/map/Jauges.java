package map;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import main.Main;
import menu.Texte;
import utilitaire.graphismes.Graphismes;

/**
 * Classe utilitaire pour afficher les jauges sur l'ecran de jeu
 */
public abstract class Jauges {
	// arme
	private static final int X_AFFICHAGE_ARME = 563;
	private static final int Y_AFFICHAGE_ARME = 4;
	public static final BufferedImage HUD_TOUCHES = chargerImageHudTouches();
	
	// gadget
	private static final int X_AFFICHAGE_GADGET = 612;
	private static final int Y_AFFICHAGE_GADGET = 4;
	
	// argent
	private static final int X_AFFICHAGE_ARGENT = 4;
	private static final int Y_AFFICHAGE_ARGENT = 450;
	public static final BufferedImage HUD_ARGENT = chargerImageHudArgent();
	
	private static final int ESPACEMENT_ICONES = 4;
	
	// vie
	private static final int VARIABLE_VIE_HEROS = 4;
	private static final int PORTIONS_VIE = 2;
	private static final BufferedImage[] ICONE_VIE = chargerLesIconesDeVie();
	
	/**
	 * Dessiner a l'ecran les jauges et informations extradi�g�tiques a destination du joueur
	 * @param ecran sur lequel on dessine les jauges
	 * @return ecran avec les jauges dessinees
	 */
	public static BufferedImage dessinerLesJauges(BufferedImage ecran) {
		
		// vie du heros
		ecran = dessinerVies(ecran);
		
		// touches
		ecran = Graphismes.superposerImages(ecran, HUD_TOUCHES, 0, 0);
		
		// Arme equip�e
		try {
			ecran = Graphismes.superposerImages(ecran, Main.getPartieActuelle().getArmeEquipee().icone, X_AFFICHAGE_ARME, Y_AFFICHAGE_ARME);
		} catch (NullPointerException e) {
			//pas d'Arme �quipee
		}
		
		// Gadget equipe
		try {
			ecran = Graphismes.superposerImages(ecran, Main.getPartieActuelle().getGadgetEquipe().icone, X_AFFICHAGE_GADGET, Y_AFFICHAGE_GADGET);
		} catch (NullPointerException e) {
			//pas de Gadget equipe
		}
		
		// argent
		ecran = dessinerArgent(ecran);
		
		return ecran;
	}
	
	/**
	 * Charge le petit carr� blanc qui entoure l'Arme dans le HUD a l'ecran.
	 * @return image constitutive du HUD
	 */
	public static BufferedImage chargerImageHudTouches() {
		try {
			return Graphismes.ouvrirImage("Pictures", "carre arme kujira.png");
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Charge l'ic�ne de l'argent.
	 * @return image constitutive du HUD
	 */
	public static BufferedImage chargerImageHudArgent() {
		try {
			return Graphismes.ouvrirImage("Icons", "ecaille icon.png");
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Charge les icones de la barre de vie.
	 * @return image constitutive du HUD
	 */
	public static BufferedImage[] chargerLesIconesDeVie() {
		try {
			final BufferedImage[] iconesVie = new BufferedImage[PORTIONS_VIE];
			iconesVie[0] = Graphismes.ouvrirImage("Icons", "vie jaune icon.png");
			iconesVie[1] = Graphismes.ouvrirImage("Icons", "vie verte icon.png");
			return iconesVie;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	
	/**
	 * Dessiner la jauge de vie a l'ecran
	 * @param ecran sur lequel on dessine les jauges
	 * @return ecran avec les jauges dessinees
	 */
	private static BufferedImage dessinerVies(BufferedImage ecran) {
		final int vieDuHeros = Main.getPartieActuelle().variables[VARIABLE_VIE_HEROS];
		if (vieDuHeros <= 0) {
			// pas de vie a afficher
			return ecran;
		}
		
		final int tailleBarreDeVie = vieDuHeros/2;
		final BufferedImage iconeViePleine = ICONE_VIE[PORTIONS_VIE-1];
		int xIcone;
		final int yIcone = ESPACEMENT_ICONES;
		for (int i = 0; i<tailleBarreDeVie; i++) {
			// icones de vie pleine
			xIcone = ESPACEMENT_ICONES + i*(iconeViePleine.getWidth() + ESPACEMENT_ICONES);
			ecran = Graphismes.superposerImages(ecran, iconeViePleine, xIcone, yIcone);
		}
		if (vieDuHeros % PORTIONS_VIE != 0) {
			// derniere icone (vie partielle)
			final BufferedImage iconeViePartielle = ICONE_VIE[vieDuHeros%PORTIONS_VIE-1];
			xIcone = ESPACEMENT_ICONES + tailleBarreDeVie*(iconeViePleine.getWidth() + ESPACEMENT_ICONES);
			ecran = Graphismes.superposerImages(ecran, iconeViePartielle, xIcone, yIcone);
		}
		
		return ecran;
	}
	
	/**
	 * Dessiner la jauge d'argent a l'ecran
	 * @param ecran sur lequel on dessine les jauges
	 * @return ecran avec les jauges dessinees
	 */
	private static BufferedImage dessinerArgent(BufferedImage ecran) {
		final int argent = Main.getPartieActuelle().argent;
		if (argent > 0) {
			ecran = Graphismes.superposerImages(ecran, HUD_ARGENT, X_AFFICHAGE_ARGENT, Y_AFFICHAGE_ARGENT);
			final ArrayList<String> contenuTexte = new ArrayList<String>();
			contenuTexte.add("" + argent);
			final Texte texte = new Texte(contenuTexte, Color.white, Color.black, Texte.Taille.MOYENNE);
			final BufferedImage texteImage = texte.getImage();
			ecran = Graphismes.superposerImages(ecran, texteImage, X_AFFICHAGE_ARGENT+HUD_ARGENT.getWidth()+ESPACEMENT_ICONES, Y_AFFICHAGE_ARGENT);
		}
		return ecran;
	}
	
}
