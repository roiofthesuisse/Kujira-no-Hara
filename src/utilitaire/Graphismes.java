package utilitaire;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Graphismes {
	public static final int OPACITE_MAXIMALE = 255;
	
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
	public static final BufferedImage superposerImages(BufferedImage ecran, final BufferedImage image2, final int x, final int y, final int opacite){
		//TODO final ModeDeSuperposition mode
		//s'inspirer de http://www.java2s.com/Code/Java/2D-Graphics-GUI/BlendCompositeDemo.htm
		final Graphics2D g2d = (Graphics2D) ecran.createGraphics();
		
		//transparence
		if (opacite < OPACITE_MAXIMALE) {
			final int rule = AlphaComposite.SRC_OVER;
			final float alpha = (float) opacite/OPACITE_MAXIMALE;
	        final Composite comp = AlphaComposite.getInstance(rule, alpha);
			g2d.setComposite(comp);
		}
		
		g2d.drawImage(image2, null, x, y);
		return ecran;
	}

}
