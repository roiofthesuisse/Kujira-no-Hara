package utilitaire.graphismes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.Fenetre;
import map.LecteurMap;
import utilitaire.Maths;

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
            		private static final float ECLAIRCISSEMENT = 0.4f;
            		private static final float ASSOMBRISSEMENT = 0.6f;
            		private static final double VIVIFICATION = 0.75;
            		private static final double FADIFICATION = 1.25;
            		private static final float JAUNISSEMENT = 0.06f;
            		private static final float BLEUISSEMENT = 0.2f;
            		
            		@Override
            		public void peindre(final int[] src, final int[] dst, final int[] result) {
            			final float opaciteLocale = (float) src[ALPHA] / (float) VALEUR_MAXIMALE;
            			final int luminositeFiltre = (src[ROUGE] + src[VERT] + src[BLEU])/3;
            			
            			// Le filtre impose sa luminosité à l'écran
            			int L = luminositeFiltre * 2 - VALEUR_MAXIMALE;
            			int luminositeEcran = (dst[ROUGE] + dst[VERT] + dst[BLEU])/3;
            			float coefficientLuminosite = L>=0 ? ECLAIRCISSEMENT : ASSOMBRISSEMENT;
            			
            			// Dans la lumière, les couleurs sont plus vives ; dans l'obscurité, plus fades.
            			float saturation = 2.0f*luminositeFiltre / VALEUR_MAXIMALE;
            			final double coefficientSaturation = L>=0 ? VIVIFICATION : FADIFICATION;
            			saturation = (float) Maths.pow(saturation, coefficientSaturation);
            			int deltaRouge = dst[ROUGE] - luminositeEcran;
            			int deltaVert = dst[VERT] - luminositeEcran;
            			int deltaBleu = dst[BLEU] - luminositeEcran;
            			
            			// Dans la lumière, l'image est plus jaune : dans l'obscurité, plus bleue.
            			float coefficientJaune = L>=0 ? JAUNISSEMENT : 0;
            			float coefficientBleu = L>=0 ? 0 : -BLEUISSEMENT;
            			
            			float resultatRouge = luminositeEcran + L*coefficientLuminosite + deltaRouge*saturation + L*coefficientJaune;
            			float resultatVert = luminositeEcran + L*coefficientLuminosite + deltaVert*saturation + L*coefficientJaune;
            			float resultatBleu = luminositeEcran + L*coefficientLuminosite + deltaBleu*saturation + L*coefficientBleu;
            			
                    	result[ROUGE] = seuiller(dst[ROUGE]*(1.0f-opaciteLocale) + resultatRouge*opaciteLocale);
                        result[VERT] = seuiller(dst[VERT]*(1.0f-opaciteLocale) + resultatVert*opaciteLocale);
                        result[BLEU] = seuiller(dst[BLEU]*(1.0f-opaciteLocale) + resultatBleu*opaciteLocale);
                        result[ALPHA] = dst[ALPHA];
            		}
            	};
            case TON_DE_L_ECRAN:
                return new Pinceau() {
                    @Override
                    public void peindre(final int[] srcNePasUtiliser, final int[] dst, final int[] result) {
                    	int[] ton = composite.ton;
                    	final float desaturation = (float) ton[ALPHA] / (float) VALEUR_MAXIMALE;
                    	final float saturation = 1.0f - desaturation;
                    	
                    	float mediane = VALEUR_MAXIMALE/2;
                    	float tauxRouge = (float) dst[ROUGE] / mediane - 1.0f;
                    	float tauxVert = (float) dst[VERT] / mediane - 1.0f;
                    	float tauxBleu = (float) dst[BLEU] / mediane - 1.0f;

                    	int luminosite = (dst[ROUGE] + dst[VERT] + dst[BLEU])/3;
                    	int baseRouge =  (int) (ton[ROUGE]*saturation + luminosite*desaturation);
                    	int baseVert =  (int) (ton[VERT]*saturation + luminosite*desaturation);
                    	int baseBleu =  (int) (ton[BLEU]*saturation + luminosite*desaturation);
                    	
                    	result[ROUGE] = seuiller(baseRouge + (int) ((VALEUR_MAXIMALE - baseRouge)*tauxRouge*saturation));
                        result[VERT] = seuiller(baseVert + (int) ((VALEUR_MAXIMALE - baseVert)*tauxVert*saturation));
                        result[BLEU] = seuiller(baseBleu + (int) ((VALEUR_MAXIMALE - baseBleu)*tauxBleu*saturation));
                        result[ALPHA] = dst[ALPHA];
                    }
                };

            default:
            	LOG.error("Blender non défini pour le mode de fusion : "+composite.modeDeFusion.nom);
            	return null;
        }
    }
    
    private static int seuiller(final float valeur) {
    	return Math.max(0, Math.min(VALEUR_MAXIMALE, (int) valeur));
    }
}
