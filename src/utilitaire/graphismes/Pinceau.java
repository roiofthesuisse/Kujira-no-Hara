package utilitaire.graphismes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import utilitaire.Maths;

/**
 * Le Pinceau va peindre un pixel de l'image a superposer sur l'image support.
 * La fa�on dont le pixel sera peint d�pend du Composite utilis�.
 */
public enum Pinceau {
	PINCEAU_ADDITION {
		@Override
        public void peindre(final int[] src, final int[] dst, final int[] result) {
        	final float opaciteLocale = (float) src[ALPHA] / (float) VALEUR_MAXIMALE;
        	result[ROUGE] = Math.min(VALEUR_MAXIMALE, (int) (src[ROUGE]*opaciteLocale) + dst[ROUGE]);
            result[VERT] = Math.min(VALEUR_MAXIMALE, (int) (src[VERT]*opaciteLocale) + dst[VERT]);
            result[BLEU] = Math.min(VALEUR_MAXIMALE, (int) (src[BLEU]*opaciteLocale) + dst[BLEU]);
            result[ALPHA] = dst[ALPHA];
        }
	},
	PINCEAU_ADDITION_NEGATIF {
		@Override
        public void peindre(final int[] src, final int[] dst, final int[] result) {
        	final float opaciteLocale = (float) src[ALPHA] / VALEUR_MAXIMALE;
        	result[ROUGE] = Math.min(VALEUR_MAXIMALE, (int) ((VALEUR_MAXIMALE-src[ROUGE])*opaciteLocale) + dst[ROUGE]);
            result[VERT] = Math.min(VALEUR_MAXIMALE, (int) ((VALEUR_MAXIMALE-src[VERT])*opaciteLocale) + dst[VERT]);
            result[BLEU] = Math.min(VALEUR_MAXIMALE, (int) ((VALEUR_MAXIMALE-src[BLEU])*opaciteLocale) + dst[BLEU]);
            result[ALPHA] = dst[ALPHA];
        }
	},
	PINCEAU_SOUSTRACTION {
		@Override
        public void peindre(final int[] src, final int[] dst, final int[] result) {
        	final float opaciteLocale = (float) src[ALPHA] / (float) VALEUR_MAXIMALE;
        	result[ROUGE] = Math.max(0, (int) (dst[ROUGE] - src[ROUGE]*opaciteLocale));
            result[VERT] = Math.max(0, (int) (dst[VERT] - src[VERT]*opaciteLocale));
            result[BLEU] = Math.max(0, (int) (dst[BLEU] - src[BLEU]*opaciteLocale));
            result[ALPHA] = dst[ALPHA];
        }
	},
	PINCEAU_SOUSTRACTION_NEGATIF {
		@Override
        public void peindre(final int[] src, final int[] dst, final int[] result) {
        	final float opaciteLocale = (float) src[ALPHA] / (float) VALEUR_MAXIMALE;
        	result[ROUGE] = Math.max(0, (int) (dst[ROUGE] - (VALEUR_MAXIMALE-src[ROUGE])*opaciteLocale));
            result[VERT] = Math.max(0, (int) (dst[VERT] - (VALEUR_MAXIMALE-src[VERT])*opaciteLocale));
            result[BLEU] = Math.max(0, (int) (dst[BLEU] - (VALEUR_MAXIMALE-src[BLEU])*opaciteLocale));
            result[ALPHA] = dst[ALPHA];
        }
	},
	PINCEAU_TOPKEK {
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
			
			// Le filtre impose sa luminosit� a l'ecran
			final int l = luminositeFiltre * 2 - VALEUR_MAXIMALE;
			final int luminositeEcran = (dst[ROUGE] + dst[VERT] + dst[BLEU])/3;
			final float coefficientLuminosite = l>=0 ? ECLAIRCISSEMENT : ASSOMBRISSEMENT;
			
			// Dans la lumi�re, les couleurs sont plus vives ; dans l'obscurit�, plus fades.
			float saturation = 2.0f*luminositeFiltre / VALEUR_MAXIMALE;
			final double coefficientSaturation = l>=0 ? VIVIFICATION : FADIFICATION;
			saturation = (float) Maths.pow(saturation, coefficientSaturation);
			final int deltaRouge = dst[ROUGE] - luminositeEcran;
			final int deltaVert = dst[VERT] - luminositeEcran;
			final int deltaBleu = dst[BLEU] - luminositeEcran;
			
			// Dans la lumi�re, l'image est plus jaune : dans l'obscurit�, plus bleue.
			final float coefficientJaune = l>=0 ? JAUNISSEMENT : 0;
			final float coefficientBleu = l>=0 ? 0 : -BLEUISSEMENT;
			
			final float resultatRouge = luminositeEcran + l*coefficientLuminosite + deltaRouge*saturation + l*coefficientJaune;
			final float resultatVert = luminositeEcran + l*coefficientLuminosite + deltaVert*saturation + l*coefficientJaune;
			final float resultatBleu = luminositeEcran + l*coefficientLuminosite + deltaBleu*saturation + l*coefficientBleu;
			
        	result[ROUGE] = seuiller(dst[ROUGE]*(1.0f-opaciteLocale) + resultatRouge*opaciteLocale);
            result[VERT] = seuiller(dst[VERT]*(1.0f-opaciteLocale) + resultatVert*opaciteLocale);
            result[BLEU] = seuiller(dst[BLEU]*(1.0f-opaciteLocale) + resultatBleu*opaciteLocale);
            result[ALPHA] = dst[ALPHA];
		}
	},
	PINCEAU_TON_DE_L_ECRAN {
		@Override
        public void peindre(final int[] ton, final int[] dst, final int[] result) {
        	final float desaturation = (float) ton[ALPHA] / (float) VALEUR_MAXIMALE;
        	final float saturation = 1.0f - desaturation;
        	
        	final float mediane = VALEUR_MAXIMALE/2;
        	final float tauxRouge = (float) dst[ROUGE] / mediane - 1.0f;
        	final float tauxVert = (float) dst[VERT] / mediane - 1.0f;
        	final float tauxBleu = (float) dst[BLEU] / mediane - 1.0f;

        	final int luminosite = (dst[ROUGE] + dst[VERT] + dst[BLEU])/3;
        	final int baseRouge =  (int) (ton[ROUGE]*saturation + luminosite*desaturation);
        	final int baseVert =  (int) (ton[VERT]*saturation + luminosite*desaturation);
        	final int baseBleu =  (int) (ton[BLEU]*saturation + luminosite*desaturation);
        	
        	result[ROUGE] = seuiller(baseRouge + (int) ((VALEUR_MAXIMALE - baseRouge)*tauxRouge*saturation));
            result[VERT] = seuiller(baseVert + (int) ((VALEUR_MAXIMALE - baseVert)*tauxVert*saturation));
            result[BLEU] = seuiller(baseBleu + (int) ((VALEUR_MAXIMALE - baseBleu)*tauxBleu*saturation));
            result[ALPHA] = dst[ALPHA];
        }
	};
	
	
	private static final Logger LOG = LogManager.getLogger(Pinceau.class);
	public static final int VALEUR_MAXIMALE = 255;
	private static final int ALPHA = 0;
	private static final int ROUGE = 1;
	private static final int VERT = 2;
	private static final int BLEU = 3;
	
	/**
	 * Peindre un pixel de l'image a superposer sur l'image support.
	 * @param src pixel a peindre
	 * @param dst pixel sur lequel on peint
	 * @param resultat m�lange entre les deux pixels
	 */
	public abstract void peindre(int[] src, int[] dst, int[] resultat);

	/**
	 * Obtenir le pinceau correspondant a un Composite.
	 * @param composite fa�on de peindre qu'on attend du pinceau
	 * @return pinceau qui peint tel que decrit par le Composite
	 */
    public static Pinceau obtenirPinceauPour(final MonComposite composite) {
        switch (composite.modeDeFusion) {
            case ADDITION:
                return Pinceau.PINCEAU_ADDITION;
            case ADDITION_NEGATIF:
                return Pinceau.PINCEAU_ADDITION_NEGATIF;
            case SOUSTRACTION:
                return Pinceau.PINCEAU_SOUSTRACTION;
            case SOUSTRACTION_NEGATIF:
                return Pinceau.PINCEAU_SOUSTRACTION_NEGATIF;
            case TOPKEK:
            	return Pinceau.PINCEAU_TOPKEK;
            case TON_DE_L_ECRAN:
                return Pinceau.PINCEAU_TON_DE_L_ECRAN;
            default:
            	LOG.error("Blender non d�fini pour le mode de fusion : "+composite.modeDeFusion.nom);
            	return null;
        }
    }
    
    /**
     * Ne pas d�passer les valeurs limites d'un couleur.
     * @param valeur a tronquer
     * @return troncature
     */
    private static int seuiller(final float valeur) {
    	return Math.max(0, Math.min(VALEUR_MAXIMALE, (int) valeur));
    }
}
