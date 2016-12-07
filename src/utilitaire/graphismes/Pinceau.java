package utilitaire.graphismes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Le Pinceau va peindre un pixel de l'image à superposer sur l'image support.
 * La façon dont le pixel sera peint dépend du Composite utilisé.
 */
public abstract class Pinceau {
	private static final Logger LOG = LogManager.getLogger(Pinceau.class);
	public static final int VALEUR_MAXIMALE = 255;
	private static final int ALPHA = 0;
	private static final int ROUGE = 1;
	private static final int VERT = 2;
	private static final int BLEU = 3;
	
	/**
	 * Peindre un pixel de l'image à superposer sur l'image support.
	 * @param src pixel à peindre
	 * @param dst pixel sur lequel on peint
	 * @param resultat mélange entre les deux pixels
	 */
	public abstract void peindre(int[] src, int[] dst, int[] resultat);

	/**
	 * Obtenir le pinceau correspondant à un Composite.
	 * @param composite façon de peindre qu'on attend du pinceau
	 * @return pinceau qui peint tel que décrit par le Composite
	 */
    public static Pinceau obtenirPinceauPour(final MonComposite composite) {
        switch (composite.modeDeFusion) {
            case ADDITION:
                return new Pinceau() {
                    @Override
                    public void peindre(final int[] src, final int[] dst, final int[] result) {
                    	final float opaciteLocale = src[ALPHA] / VALEUR_MAXIMALE;
                    	result[ROUGE] = Math.min(VALEUR_MAXIMALE, (int) (src[ROUGE]*opaciteLocale) + dst[ROUGE]);
                        result[VERT] = Math.min(VALEUR_MAXIMALE, (int) (src[VERT]*opaciteLocale) + dst[VERT]);
                        result[BLEU] = Math.min(VALEUR_MAXIMALE, (int) (src[BLEU]*opaciteLocale) + dst[BLEU]);
                        result[ALPHA] = Math.min(VALEUR_MAXIMALE, dst[ALPHA]);
                    }
                };
            case ADDITION_NEGATIF:
                return new Pinceau() {
                    @Override
                    public void peindre(final int[] src, final int[] dst, final int[] result) {
                    	final float opaciteLocale = src[ALPHA] / VALEUR_MAXIMALE;
                    	result[ROUGE] = Math.min(VALEUR_MAXIMALE, (int) ((VALEUR_MAXIMALE-src[ROUGE])*opaciteLocale) + dst[ROUGE]);
                        result[VERT] = Math.min(VALEUR_MAXIMALE, (int) ((VALEUR_MAXIMALE-src[VERT])*opaciteLocale) + dst[VERT]);
                        result[BLEU] = Math.min(VALEUR_MAXIMALE, (int) ((VALEUR_MAXIMALE-src[BLEU])*opaciteLocale) + dst[BLEU]);
                        result[ALPHA] = Math.min(VALEUR_MAXIMALE, dst[ALPHA]);
                    }
                };
            case SOUSTRACTION:
                return new Pinceau() {
                    @Override
                    public void peindre(final int[] src, final int[] dst, final int[] result) {
                    	final float opaciteLocale = (float) src[ALPHA] / (float) VALEUR_MAXIMALE;
                    	result[ROUGE] = Math.max(0, (int) (dst[ROUGE] - src[ROUGE]*opaciteLocale));
                        result[VERT] = Math.max(0, (int) (dst[VERT] - src[VERT]*opaciteLocale));
                        result[BLEU] = Math.max(0, (int) (dst[BLEU] - src[BLEU]*opaciteLocale));
                        result[ALPHA] = Math.min(VALEUR_MAXIMALE, dst[ALPHA]);
                    }
                };
            case SOUSTRACTION_NEGATIF:
                return new Pinceau() {
                    @Override
                    public void peindre(final int[] src, final int[] dst, final int[] result) {
                    	final float opaciteLocale = (float) src[ALPHA] / (float) VALEUR_MAXIMALE;
                    	result[ROUGE] = Math.max(0, (int) (dst[ROUGE] - (VALEUR_MAXIMALE-src[ROUGE])*opaciteLocale));
                        result[VERT] = Math.max(0, (int) (dst[VERT] - (VALEUR_MAXIMALE-src[VERT])*opaciteLocale));
                        result[BLEU] = Math.max(0, (int) (dst[BLEU] - (VALEUR_MAXIMALE-src[BLEU])*opaciteLocale));
                        result[ALPHA] = Math.min(VALEUR_MAXIMALE, dst[ALPHA]);
                    }
                };
            //TODO negatif
            default:
            	LOG.error("Blender non défini pour le mode de fusion : "+composite.modeDeFusion.nom);
            	return null;
        }
    }
}
