package utilitaire.graphismes;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.CompositeContext;
import java.awt.RenderingHints;
import java.awt.image.ColorModel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Un Composite est utilis� par le Graphics2D d'une image pour effectuer les superpositions d'images.
 * Il contient les deux informations n�cessaires a la superposition : le mode de fusion et l'opacit�.
 */
public final class MonComposite implements Composite {
	private static final Logger LOG = LogManager.getLogger(MonComposite.class);
	
	public final ModeDeFusion modeDeFusion;
	public final float opacite;
	public int[] ton = null;
	
	/**
	 * G�n�rer un Composite qui effectuera la superposition telle que voulue
	 * @param modeDeFusion fa�on dont on superpose les deux images
	 * @param opacite de l'image a superposer (valeur r�elle entre 0 et 1)
	 * @return composite qui effectuera la superposition telle que voulue
	 */
	public static Composite creerComposite(final ModeDeFusion modeDeFusion, final float opacite) {
		if (ModeDeFusion.NORMAL.equals(modeDeFusion)) {
			return AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacite);
		} else {
			return new MonComposite(modeDeFusion, opacite);
		}
	}
	
	/**
	 * G�n�rer un Composite tonal qui effectuera la superposition telle que voulue
	 * @param ton d�formant les couleurs de l'image
	 * @return composite qui effectuera la superposition telle que voulue
	 */
	public static Composite creerComposite(final int[] ton) {
		final MonComposite composite = new MonComposite(ModeDeFusion.TON_DE_L_ECRAN, 1f);
		composite.ton = ton;
		return composite;
	}

	/**
	 * Constructeur explicite
	 * @param modeDeFusion fa�on dont on superpose les deux images
	 * @param opacite de l'image superpos�e a l'image support
	 */
	private MonComposite(final ModeDeFusion modeDeFusion, final float opacite) {
		this.modeDeFusion = modeDeFusion;
		this.opacite = opacite;
	}

	@Override
	public CompositeContext createContext(ColorModel srcColorModel, final ColorModel dstColorModel, final RenderingHints hints) {
		if (!srcColorModel.equals(dstColorModel)) {
			LOG.warn("Attention : les mod�les de couleurs sont differents pour les deux images !\n"
					+ "Chargez les images avec Graphismes.ouvrirImage() afin d'assurer la compatibilit�.");
		}
		return new ContexteDeComposite(this);
	}

}
