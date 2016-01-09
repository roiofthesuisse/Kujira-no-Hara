package main;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.imageio.ImageIO;

import son.LecteurAudio;
import map.LecteurMap;
import menu.LecteurMenu;

/**
 * Le lecteur peut être un lecteur de menu ou un lecteur de map.
 * Le rôle du lecteur est d'afficher dans la fenêtre la succession des écrans au cours du temps.
 */
public abstract class Lecteur {
	public BufferedImage ecranAtuel;
	public Fenetre fenetre = null;
	/**
	 * Durée minimale d'une frame (en millisecondes).
	 * Il est interdit qu'une frame dure moins longtemps, afin que l'animation soit compréhensible.
	 * La frame peut durer plus longtemps si l'ordinateur a du mal à faire tourner le bousin.
	 */
	private static final long DUREE_FRAME = 30;
	public static final int TYPE_DES_IMAGES = BufferedImage.TYPE_INT_ARGB;
	/**
	 * Est-ce que le lecteur est allumé ?
	 * Si le lecteur est allumé, l'affichage est actualisé sans cesse.
	 * Si le lecteur est éteint, l'affichage arrête sa boucle, et la fenêtre attend un nouveau lecteur.
	 */
	public boolean allume = true;
	public int frameActuelle = 0;

	/**
	 * Le rôle d'un Lecteur est de calculer l'écran à afficher dans la Fenêtre.
	 * @return écran à afficher maintenant
	 */
	public abstract  BufferedImage calculerAffichage();
	
	/**
	 * Prévenir le Lecteur qu'une touche a été pressée, pour qu'il en déduise une action à faire.
	 * @param keycode numéro de la touche pressée
	 */
	public abstract void keyPressed(Integer keycode);
	
	/**
	 * Prévenir le Lecteur qu'une touche a été relachée, pour qu'il en déduise une action à faire.
	 * @param keycode numéro de la touche relachée
	 */
	public abstract void keyReleased(Integer keycode);
	
	/**
	 * Produire un rectangle noir pour l'afficher comme écran
	 * @return un rectangle noir
	 */
	public final BufferedImage ecranNoir() {
		int largeur = Fenetre.LARGEUR_ECRAN;
		int hauteur = Fenetre.HAUTEUR_ECRAN;
		BufferedImage image = new BufferedImage(largeur, hauteur, Lecteur.TYPE_DES_IMAGES);
		Graphics2D graphics = image.createGraphics();
		graphics.setPaint(Color.black);
		graphics.fillRect(0, 0, largeur, hauteur);
		return image;
	}
	
	/**
	 * Produire un rectangle vide
	 * @param largeur du rectangle
	 * @param hauteur du rectangle
	 * @return un rectangle sans couleur
	 */
	public final BufferedImage imageVide(final int largeur, final int hauteur) {
		BufferedImage image = new BufferedImage(largeur, hauteur, Lecteur.TYPE_DES_IMAGES);
		Color couleur = new Color(0, 0, 0, 0);
		Graphics2D graphics = image.createGraphics();
		graphics.setPaint(couleur);
		graphics.fillRect(0, 0, largeur, hauteur);
		return image;
	}
	
	/**
	 * Enregistrer une image dans l'ordinateur
	 * @param image à enregistrer
	 */
	public static void sauvegarderImage(final BufferedImage image) {
		try {
			File outputfile = new File("C:/Users/RoiOfTheSuisse/Pictures/saved.png");
			ImageIO.write(image, "png", outputfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Superposer deux images
	 * @param ecran image de fond, sur laquelle on va superposer l'autre
	 * @param image2 image du dessus, superposée sur l'écran
	 * @param x position x où on superpose l'image2
	 * @param y position y où on superpose l'image2
	 * @return écran sur lequel on a superposé l'image2
	 */
	public final BufferedImage superposerImages(BufferedImage ecran, final BufferedImage image2, final int x, final int y) {
		Graphics2D g2d = (Graphics2D) ecran.createGraphics();
		g2d.drawImage(image2, null, x, y);
		return ecran;
	}
	
	/***
	 * Récupérer le nom du BGM qu'il faut jouer pour accompagner le Manu ou la Map
	 * @return nom du BGM à jouer
	 */
	public final String getNomBgm() {
		if (this instanceof LecteurMap) {
			return ((LecteurMap) this).map.nomBGM;
		} else if (this instanceof LecteurMenu) {
			return ((LecteurMenu) this).menu.nomBGM;
		}
		return null;
	}
	
	/**
	 * Démarrer le Lecteur.
	 * Le Lecteur est allumé, la musique est lue, un écran est affiché à chaque frame.
	 */
	public final void demarrer() {
		this.allume = true;
		String typeLecteur = this.getClass().getName().equals(LecteurMap.class.getName()) ? "LecteurMap" : "LecteurMenu";
		System.out.println("Un nouveau "+typeLecteur+" vient d'être démarré.");
		LecteurAudio.playBgm(getNomBgm(), 1.0f);
		while (this.allume) {
			Date d1 = new Date();
			this.ecranAtuel = calculerAffichage();
			Date d2 = new Date();
			long dureeEffectiveDeLaFrame = d2.getTime()-d1.getTime();
			if (dureeEffectiveDeLaFrame < Lecteur.DUREE_FRAME) {
				//si l'affichage a pris moins de temps que la durée attendue, on attend que la frame se termine
				try {
					Thread.sleep(Lecteur.DUREE_FRAME-dureeEffectiveDeLaFrame);
				} catch (InterruptedException e) {
					System.out.println("La boucle de lecture du jeu dans Lecteur.demarrer() fait de la merde.");
					e.printStackTrace();
				}
			}
			this.fenetre.actualiserAffichage(this.ecranAtuel);
			this.frameActuelle++;
			//System.out.println("dureeEffectiveDeLaFrame : " + dureeEffectiveDeLaFrame);
		}
		System.out.println("Le "+typeLecteur+" actuel vient d'être arrêté à la frame "+this.frameActuelle);
	}
	
}
