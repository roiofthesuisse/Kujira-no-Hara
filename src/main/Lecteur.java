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
 * Le rôle du lecteur est d'afficher dans la fenêtre la succession des frames au cours du temps.
 */
public abstract class Lecteur {
	public BufferedImage ecranAtuel;
	public Fenetre fenetre = null;
	/**
	 * Durée minimale d'une frame (en millisecondes).
	 * Il est interdit qu'une frame dure moins longtemps, afin que l'animation soit compréhensible.
	 * La frame peut durer plus longtemps si l'ordinateur a du mal à faire tourner le bousin.
	 */
	public static long dureeFrame = 30;
	public static int imageType = BufferedImage.TYPE_INT_ARGB;
	/**
	 * Est-ce que le lecteur est allumé ?
	 * Si le lecteur est allumé, l'affichage est actualisé sans cesse.
	 * Si le lecteur est éteint, l'affichage arrête sa boucle, et la fenêtre attend un nouveau lecteur.
	 */
	public Boolean allume = true;
	public int frameActuelle = 0;

	public abstract  BufferedImage calculerAffichage();
	public abstract void keyPressed(int keycode);
	public abstract void keyReleased(Integer keycode);
	
	public BufferedImage ecranNoir(){
		int largeur = Fenetre.largeurParDefaut;
		int hauteur = Fenetre.hauteurParDefaut;
		BufferedImage image = new BufferedImage(largeur, hauteur, imageType);
		Graphics2D graphics = image.createGraphics();
		graphics.setPaint(Color.black);
		graphics.fillRect(0, 0, largeur, hauteur);
		return image;
	}
	
	public BufferedImage imageVide(int largeur, int hauteur){
		BufferedImage image = new BufferedImage(largeur, hauteur, imageType);
		Color couleur = new Color(0,0,0,0);
		Graphics2D graphics = image.createGraphics();
		graphics.setPaint(couleur);
		graphics.fillRect(0, 0, largeur, hauteur);
		return image;
	}
	
	public static void sauvegarderImage(BufferedImage image){
		try {
			File outputfile = new File("C:/Users/RoiOfTheSuisse/Pictures/saved.png");
			ImageIO.write(image, "png", outputfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public BufferedImage superposerImages(BufferedImage ecran, BufferedImage image2, int x, int y){
		BufferedImage image1 = ecran;
		int largeur = image1.getWidth();
		int hauteur = image1.getHeight();
		BufferedImage image3 = new BufferedImage (largeur, hauteur, imageType);
		Graphics2D g2d = image3.createGraphics ();
		g2d.drawImage (image1, null, 0, 0);
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
		g2d.drawImage (image2, null, x, y);
		g2d.dispose ();
		return image3;
	}
	
	public String getNomBgm(){
		if(this instanceof LecteurMap){
			return ((LecteurMap)this).map.nomBGM;
		}else if(this instanceof LecteurMenu){
			return ((LecteurMenu)this).menu.nomBGM;
		}
		return null;
	}
	
	public void demarrer(){
		allume = true;
		System.out.println("Nouveau lecteur démarré");
		LecteurAudio.playBgm(getNomBgm(), 1.0f);
		while(allume){
			Date d1 = new Date();
			ecranAtuel = calculerAffichage();
			Date d2 = new Date();
			long dureeEffectiveDeLaFrame = d2.getTime()-d1.getTime();
			if(dureeEffectiveDeLaFrame < dureeFrame){
				//si l'affichage a pris moins de temps que la durée attendue, on attend que la frame se termine
				try {
					Thread.sleep(dureeFrame-dureeEffectiveDeLaFrame);
				} catch (InterruptedException e) {
					System.out.println("La boucle de lecture du jeu dans Lecteur.demarrer() fait de la merde.");
					e.printStackTrace();
				}
			}
			fenetre.actualiserAffichage(ecranAtuel);
			frameActuelle++;
			//System.out.println("dureeEffectiveDeLaFrame : " + dureeEffectiveDeLaFrame);
		}
		System.out.println("Lecteur actuel arrêté à la frame "+frameActuelle);
	}
	
}
