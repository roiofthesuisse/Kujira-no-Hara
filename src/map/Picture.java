package map;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Image à afficher à l'écran et ses paramètres.
 */
public class Picture {
	private String nomImage;
	private BufferedImage image;
	public Integer numero;
	/** coordonnée x d'affichage par rapport au coin de l'écran */
	private int x;
	/** coordonnée y d'affichage par rapport au coin de l'écran */
	private int y;
	private int opacite;
	
	/**
	 * Constructeur explicite
	 * @param nomImage nom du fichier image
	 * @param numero de l'image pour le LecteurMap
	 * @param x coordonnée x d'affichage à l'écran
	 * @param y coordonnée y d'affichage à l'écran
	 * @param opacite d'affichage
	 */
	public Picture(final String nomImage, final int numero, final int x, final int y, final int opacite) {
		this.nomImage = nomImage;
		try {
			this.image = ImageIO.read(new File(".\\ressources\\Graphics\\Picture\\"+this.nomImage));
		} catch (IOException e) {
			System.err.println("Impossible d'ouvrir l'image "+nomImage);
			e.printStackTrace();
		}
		this.numero = numero;
		this.x = x;
		this.y = y;
		this.opacite = opacite;
	}
	
}
