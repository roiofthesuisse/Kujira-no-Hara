package utilitaire.graphismes;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.CompositeContext;
import java.awt.RenderingHints;
import java.awt.image.ColorModel;

/**
 * Un Composite est utilisé par le Graphics2D d'une image pour effectuer les superpositions d'images.
 * Il contient les deux informations nécessaires à la superposition : le mode de fusion et l'opacité.
 */
public final class MonComposite implements Composite {
	public final ModeDeFusion modeDeFusion;
	public final float opacite;
	
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
	 * Constructeur explicite
	 * @param modeDeFusion façon dont on superpose les deux images
	 * @param opacite de l'image superposée à l'image support
	 */
	private MonComposite(final ModeDeFusion modeDeFusion, final float opacite) {
		this.modeDeFusion = modeDeFusion;
		this.opacite = opacite;
	}

	@Override
	public CompositeContext createContext(final ColorModel srcColorModel, final ColorModel dstColorModel, final RenderingHints hints) {
		return new ContexteDeComposite(this);
	}

}
