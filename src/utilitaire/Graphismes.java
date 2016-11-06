package utilitaire;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import main.Fenetre;
import main.Lecteur;

/**
 * CLasse utilitaire pour les opérations graphiques récurrentes.
 */
public abstract class Graphismes {
	public static final int OPACITE_MAXIMALE = 255;
	public static Graphics2D graphismes;
	
	/**
	 * Façon dont les images sont superposées.
	 */
	public enum ModeDeFusion {
		NORMAL("normal"), ADDITION("addition"), SOUSTRACTION("soustraction");
		
		public String nom;
		
		/**
		 * Constructeur explicite
		 * @param nom du mode de fusion
		 */
		ModeDeFusion(final String nom) {
			this.nom = nom;
		}
		
		/**
		 * Obtenir le mode de fusion à partir de son nom
		 * @param nom du mode de fusion
		 * @return mode de fusion qui porte ce nom
		 */
		public static ModeDeFusion parNom(final Object nom) {
			for (ModeDeFusion mode : ModeDeFusion.values()) {
				if (mode.nom.equals(nom)) {
					return mode;
				}
			}
			return ModeDeFusion.NORMAL;
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
	public static final BufferedImage superposerImages(BufferedImage ecran, final BufferedImage image2, final int x, final int y) {
		return superposerImages(ecran, image2, x, y, OPACITE_MAXIMALE);
	}
	
	/**
	 * Superposer deux images
	 * @param ecran image de fond, sur laquelle on va superposer l'autre
	 * @param image2 image du dessus, superposée sur l'écran
	 * @param x position x où on superpose l'image2
	 * @param y position y où on superpose l'image2
	 * @param opacite transparence de l'image2 entre 0 et 255
	 * @return écran sur lequel on a superposé l'image2
	 */
	public static final BufferedImage superposerImages(BufferedImage ecran, final BufferedImage image2, final int x, final int y, final int opacite) {	
		final Graphics2D g2d = (Graphics2D) ecran.createGraphics();
		//TODO final ModeDeSuperposition mode
		//s'inspirer de http://www.java2s.com/Code/Java/2D-Graphics-GUI/BlendCompositeDemo.htm
		
		//transparence
		if (opacite < OPACITE_MAXIMALE) {
			final int rule = AlphaComposite.SRC_OVER;
			final float alpha = (float) opacite/OPACITE_MAXIMALE;
	        final Composite comp = AlphaComposite.getInstance(rule, alpha);
			g2d.setComposite(comp);
		}
		
		g2d.drawImage(image2, null, x, y);
		g2d.dispose();
		
		return ecran;
	}
	
	/**
	 * Produire un rectangle noir pour l'afficher comme écran
	 * @return un rectangle noir
	 */
	public static BufferedImage ecranNoir() {
		BufferedImage image = new BufferedImage(Fenetre.LARGEUR_ECRAN, Fenetre.HAUTEUR_ECRAN, Lecteur.TYPE_DES_IMAGES);
		Graphics2D g2d = image.createGraphics();
		g2d.setPaint(Color.black);
		g2d.fillRect(0, 0, Fenetre.LARGEUR_ECRAN, Fenetre.HAUTEUR_ECRAN);
		g2d.dispose();
		return image;
	}
	
	/**
	 * Produire un rectangle vide
	 * @param largeur du rectangle
	 * @param hauteur du rectangle
	 * @return un rectangle sans couleur
	 */
	public static BufferedImage imageVide(final int largeur, final int hauteur) {
		BufferedImage image = new BufferedImage(largeur, hauteur, Lecteur.TYPE_DES_IMAGES);
		final Color couleur = new Color(0, 0, 0, 0);
		Graphics2D g2d = image.createGraphics();
		g2d.setPaint(couleur);
		g2d.fillRect(0, 0, largeur, hauteur);
		g2d.dispose();
		return image;
	}
	
	/**
	 * Produire un rectangle vide pour l'afficher comme écran
	 * @return un rectangle vide
	 */
	public static BufferedImage ecranVide() {
		return imageVide(Fenetre.LARGEUR_ECRAN, Fenetre.HAUTEUR_ECRAN);
	}
	
	/**
	 * Cloner une image modèle.
	 * @param image à cloner
	 * @return clone de l'image
	 */
	public static BufferedImage creerUneImageVideDeMemeTaille(final BufferedImage image) {
		final BufferedImage cloneVide = new BufferedImage(
				image.getWidth(), 
				image.getWidth(), 
				image.getType()
		);
		return cloneVide;
	}
	
	/**
	 * Cloner une image modèle.
	 * @param image à cloner
	 * @return clone de l'image
	 */
	public static BufferedImage clonerUneImage(final BufferedImage image) {
		BufferedImage clone = creerUneImageVideDeMemeTaille(image);
		
		// Ajout de l'image de boîte de dialogue
		clone = Graphismes.superposerImages(clone, image, 0, 0);
		return clone;
	}
	
	/**
	 * Enregistrer une image dans l'ordinateur
	 * @param image à enregistrer
	 * @param nom de l'image enregistrée
	 */
	public static void sauvegarderImage(final BufferedImage image, final String nom) {
		try {
			final File outputfile = new File("C:/Users/RoiOfTheSuisse/Pictures/"+nom+".png");
			ImageIO.write(image, "png", outputfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Redimensionner une image.
	 * @param image à redimensionner
	 * @param largeur de la nouvelle image
	 * @param hauteur de la nouvelle image
	 * @return image redimensionnée
	 */
	public static BufferedImage redimensionner(final BufferedImage image, final int largeur, final int hauteur) {
		final Image tmp = image.getScaledInstance(largeur, hauteur, Image.SCALE_SMOOTH);
	    final BufferedImage imageRedimensionnee = new BufferedImage(largeur, hauteur, BufferedImage.TYPE_INT_ARGB);

	    final Graphics2D g2d = imageRedimensionnee.createGraphics();
	    g2d.drawImage(tmp, 0, 0, null);
	    g2d.dispose();

	    return imageRedimensionnee;
	}  

}
