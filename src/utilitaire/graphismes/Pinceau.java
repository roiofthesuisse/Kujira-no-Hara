package utilitaire.graphismes;

import java.awt.Color;

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
	private static final int TEINTE = 0;
	private static final int SATURATION = 1;
	private static final int LUMINOSITE = 2;
	
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
                    	final float opaciteLocale = (float) src[ALPHA] / (float) VALEUR_MAXIMALE;
                    	result[ROUGE] = Math.min(VALEUR_MAXIMALE, (int) (src[ROUGE]*opaciteLocale) + dst[ROUGE]);
                        result[VERT] = Math.min(VALEUR_MAXIMALE, (int) (src[VERT]*opaciteLocale) + dst[VERT]);
                        result[BLEU] = Math.min(VALEUR_MAXIMALE, (int) (src[BLEU]*opaciteLocale) + dst[BLEU]);
                        result[ALPHA] = dst[ALPHA];
                    }
                };
            case ADDITION_NEGATIF:
                return new Pinceau() {
                    @Override
                    public void peindre(final int[] src, final int[] dst, final int[] result) {
                    	final float opaciteLocale = (float) src[ALPHA] / VALEUR_MAXIMALE;
                    	result[ROUGE] = Math.min(VALEUR_MAXIMALE, (int) ((VALEUR_MAXIMALE-src[ROUGE])*opaciteLocale) + dst[ROUGE]);
                        result[VERT] = Math.min(VALEUR_MAXIMALE, (int) ((VALEUR_MAXIMALE-src[VERT])*opaciteLocale) + dst[VERT]);
                        result[BLEU] = Math.min(VALEUR_MAXIMALE, (int) ((VALEUR_MAXIMALE-src[BLEU])*opaciteLocale) + dst[BLEU]);
                        result[ALPHA] = dst[ALPHA];
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
                        result[ALPHA] = dst[ALPHA];
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
                        result[ALPHA] = dst[ALPHA];
                    }
                };
            case TOPKEK:
            	return new Pinceau() {
            		private static final float TEINTE_CENTRALE = 0.5f;
            		private static final float DECALAGE_JAUNE = 120f/360f;
            		private static final float DECALAGE_BLEU = 60f/360f;
            		private static final float ANTI_JAUNISSEMENT = 4;
            		private static final float ANTI_BLEUISSEMENT = 3;
            		private static final float ANTI_VIVIFICATION = 16;
            		private static final float ANTI_MORNIFICATION = 4;
            		private static final float ANTI_ECLAIRCISSEMENT = 16;
            		private static final float ANTI_ASSOMBRISSEMENT = 1.2f;
            		@Override
            		public void peindre(final int[] src, final int[] dst, final int[] result) {
            			final float opaciteLocale = (float) src[ALPHA] / (float) VALEUR_MAXIMALE;
            			final float[] hsbSrc = new float[3];
            			final float[] hsbDst = new float[3];
            			final float[] hsbResult = new float[3];
            			// On passe dans l'espace HSB pour faire varier la luminosité sans déformer la saturation
            			Color.RGBtoHSB(src[ROUGE], src[VERT], src[BLEU], hsbSrc); //les coordonnées HSB vont de 0 à 1
            			Color.RGBtoHSB(dst[ROUGE], dst[VERT], dst[BLEU], hsbDst);
            			
            			final float luminositeSrc = 2*hsbSrc[LUMINOSITE]-1;
            			if (luminositeSrc > 0) {
            				// Effet sur la teinte : jaunissement 
            				final float coefficientJaune = luminositeSrc/ANTI_JAUNISSEMENT;
            				final float coefficientNormal = 1f - coefficientJaune;
            				float teinteDstDecalee = hsbDst[TEINTE] + DECALAGE_JAUNE;
            				if (teinteDstDecalee > 1f) {
            					teinteDstDecalee--;
            				}
            				final float moyenneDecalee = teinteDstDecalee*coefficientNormal + TEINTE_CENTRALE*coefficientJaune;
            				hsbResult[TEINTE] = moyenneDecalee - DECALAGE_JAUNE;
            				
            				// Effet sur la saturation : vivification
            				hsbResult[SATURATION] = Math.max(0f, Math.min(1f, hsbDst[SATURATION] + luminositeSrc*opaciteLocale/ANTI_VIVIFICATION));
            				
            				// Effet sur la luminosité : éclaircissement
            				hsbResult[LUMINOSITE] = Math.max(0f, Math.min(1f, hsbDst[LUMINOSITE] + luminositeSrc*opaciteLocale/ANTI_ECLAIRCISSEMENT));
            			} else {
            				// Effet sur la teinte : bleuissement
            				final float coefficientBleu = -luminositeSrc/ANTI_BLEUISSEMENT;
            				final float coefficientNormal = 1f - coefficientBleu;
            				float teinteDstDecalee = hsbDst[TEINTE] - DECALAGE_BLEU;
            				if (teinteDstDecalee < 0f) {
            					teinteDstDecalee++;
            				}
            				final float moyenneDecalee = teinteDstDecalee*coefficientNormal + TEINTE_CENTRALE*coefficientBleu;
            				hsbResult[TEINTE] = moyenneDecalee + DECALAGE_BLEU;
            				
            				// Effet sur la saturation : mornification
            				hsbResult[SATURATION] = Math.max(0f, Math.min(1f, hsbDst[SATURATION] + luminositeSrc*opaciteLocale/ANTI_MORNIFICATION));
            				
            				// Effet sur la luminosité : assombrissement
            				hsbResult[LUMINOSITE] = Math.max(0f, Math.min(1f, hsbDst[LUMINOSITE] + luminositeSrc*opaciteLocale/ANTI_ASSOMBRISSEMENT));
            			}
            			
            			final int res = Color.HSBtoRGB(hsbResult[TEINTE], hsbResult[SATURATION], hsbResult[LUMINOSITE]);
            			result[ALPHA] = dst[ALPHA];
                        result[ROUGE] = (res >> 16) & 0xFF;
                        result[VERT]  = (res >>  8) & 0xFF;
                        result[BLEU]  = (res      ) & 0xFF;
            		}
            	};
            //TODO negatif
            default:
            	LOG.error("Blender non défini pour le mode de fusion : "+composite.modeDeFusion.nom);
            	return null;
        }
    }
}
