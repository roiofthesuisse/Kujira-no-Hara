package map;

import java.awt.image.BufferedImage;

import main.Fenetre;
import utilitaire.Graphismes;
import utilitaire.Graphismes.ModeDeFusion;

/**
 * Image à afficher à l'écran et ses paramètres.
 */
public class Picture {
	public BufferedImage image;
	public Integer numero;
	/** coordonnée x d'affichage par rapport au coin de l'écran */
	public int x;
	/** coordonnée y d'affichage par rapport au coin de l'écran */
	public int y;
	private int opacite;
	private ModeDeFusion modeDeFusion;
	/** angle de rotation de l'image */
	private int angle;
	
	/**
	 * Constructeur explicite
	 * @param image nom du fichier image
	 * @param numero de l'image pour le LecteurMap
	 * @param x coordonnée x d'affichage à l'écran (en pixels)
	 * @param y coordonnée y d'affichage à l'écran (en pixels)
	 * @param opacite de l'image (sur 255)
	 * @param modeDeFusion de la superposition d'images
	 * @param angle de rotation de l'image (en degrés)
	 */
	public Picture(final BufferedImage image, final int numero, final int x, final int y, final int opacite, final ModeDeFusion modeDeFusion, final int angle) {
		this.image = image;
		this.numero = numero;
		this.x = x;
		this.y = y;
		this.opacite = opacite;
		this.modeDeFusion = modeDeFusion;
		this.angle = angle;
	}
	
	/**
	 * Dessiner les images.
	 * @param ecran sur lequel on affiche les images
	 * @return écran avec les images dessinées
	 */
	public static BufferedImage dessinerLesImages(BufferedImage ecran) {
		for (Picture picture : Fenetre.getPartieActuelle().images.values()) {
			ecran = Graphismes.superposerImages(ecran, picture.image, picture.x, picture.y, picture.opacite, picture.angle);
		}
		return ecran;
	}
	
}
