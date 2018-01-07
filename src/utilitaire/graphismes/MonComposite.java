package utilitaire.graphismes;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.CompositeContext;
import java.awt.RenderingHints;
import java.awt.image.ColorModel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Un Composite est utilisé par le Graphics2D d'une image pour effectuer les superpositions d'images.
 * Il contient les deux informations nécessaires à la superposition : le mode de fusion et l'opacité.
 */
public final class MonComposite implements Composite {
	private static final Logger LOG = LogManager.getLogger(MonComposite.class);
	
	public final ModeDeFusion modeDeFusion;
	public final float opacite;
	public int[] ton = null;
	
	/**
	 * Générer un Composite qui effectuera la superposition telle que voulue
	 * @param modeDeFusion façon dont on superpose les deux images
	 * @param opacite de l'image à superposer (valeur réelle entre 0 et 1)
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
	 * Générer un Composite tonal qui effectuera la superposition telle que voulue
	 * @param ton déformant les couleurs de l'image
	 * @return composite qui effectuera la superposition telle que voulue
	 */
	public static Composite creerComposite(final int[] ton) {
		final MonComposite composite = new MonComposite(ModeDeFusion.TON_DE_L_ECRAN, 1f);
		composite.ton = ton;
		return composite;
	}

	/**
	 * Constructeur explicite
	 * @param modeDeFusion façon dont on superpose les deux images
	 * @param opacite de l'image superposée à l'image support
	 */
	private MonComposite(final ModeDeFusion modeDeFusion, final float opacite) {
		this.modeDeFusion = modeDeFusion;
		this.opacite = opacite;
	}

	@Override
	public CompositeContext createContext(ColorModel srcColorModel, final ColorModel dstColorModel, final RenderingHints hints) {
		if (!srcColorModel.equals(dstColorModel)) {
			LOG.warn("Attention : les modèles de couleurs sont différents pour les deux images !\n"
					+ "Chargez les images avec Graphismes.ouvrirImage() afin d'assurer la compatibilité.");
		}
		return new ContexteDeComposite(this);
	}

}
