package map;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import main.Fenetre;
import utilitaire.Graphismes;

/**
 * Image à afficher à l'écran et ses paramètres.
 */
public class Picture {
	private String nomImage;
	public BufferedImage image;
	public Integer numero;
	private boolean centre;
	/** coordonnée x d'affichage par rapport au coin de l'écran */
	public int x;
	/** coordonnée y d'affichage par rapport au coin de l'écran */
	public int y;
	private int zoomX;
	private int zoomY;
	private int opacite;
	
	/**
	 * Constructeur explicite
	 * @param nomImage nom du fichier image
	 * @param numero de l'image pour le LecteurMap
	 * @param centre l'origine de l'image est elle le centre de l'image ou bien son coin haut-gauche ?
	 * @param x coordonnée x d'affichage à l'écran (en pixels)
	 * @param y coordonnée y d'affichage à l'écran (en pixels)
	 * @param zoomX étirement horizontal (en pourcents)
	 * @param zoomY étirement vertical (en pourcents)
	 * @param opacite de l'image (sur 255)
	 * @throws IOException impossible d'ouvrir l'image
	 */
	public Picture(final String nomImage, final int numero, final boolean centre, final int x, final int y, final int zoomX, final int zoomY, final int opacite) throws IOException {
		this.nomImage = nomImage;
		this.image = ImageIO.read(new File(".\\ressources\\Graphics\\Pictures\\"+this.nomImage));
		this.numero = numero;
		this.centre = centre;
		this.x = x;
		this.y = y;
		this.zoomX = zoomX;
		this.zoomY = zoomY;
		this.opacite = opacite;
	}
	
	/**
	 * Dessiner les images.
	 * @param ecran sur lequel on affiche les images
	 * @return écran avec les images dessinées
	 */
	public static BufferedImage dessinerLesImages(BufferedImage ecran) {
		for (Picture picture : Fenetre.getPartieActuelle().images.values()) {
			//TODO utiliser les autres paramètres de picture
			
			int xAffichage = picture.x;
			int yAffichage = picture.y;
			if (picture.centre) {
				xAffichage -= picture.image.getWidth()/2;
				yAffichage -= picture.image.getHeight()/2;
			}
			ecran = Graphismes.superposerImages(ecran, picture.image, xAffichage, yAffichage, picture.opacite);
		}
		return ecran;
	}
	
}
