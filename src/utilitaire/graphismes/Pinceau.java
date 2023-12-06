package utilitaire.graphismes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Le Pinceau va peindre un pixel de l'image a superposer sur l'image support.
 * La fa�on dont le pixel sera peint d�pend du Composite utilis�.
 */
public enum Pinceau {
	PINCEAU_ADDITION {
		@Override
        public void peindre(final int[] src, final int[] dst, final int[] result) {
			result[ROUGE] = Math.min(VALEUR_MAXIMALE, diviserPar256(src[ROUGE] * src[ALPHA]) + dst[ROUGE]);
			result[VERT] = Math.min(VALEUR_MAXIMALE, diviserPar256(src[VERT] * src[ALPHA]) + dst[VERT]);
			result[BLEU] = Math.min(VALEUR_MAXIMALE, diviserPar256(src[BLEU] * src[ALPHA]) + dst[BLEU]);
            result[ALPHA] = dst[ALPHA];
        }
	},
	PINCEAU_ADDITION_NEGATIF {
		@Override
        public void peindre(final int[] src, final int[] dst, final int[] result) {
			result[ROUGE] = Math.min(VALEUR_MAXIMALE, diviserPar256((VALEUR_MAXIMALE - src[ROUGE]) * src[ALPHA]) + dst[ROUGE]);
			result[VERT] = Math.min(VALEUR_MAXIMALE, diviserPar256((VALEUR_MAXIMALE - src[VERT]) * src[ALPHA]) + dst[VERT]);
			result[BLEU] = Math.min(VALEUR_MAXIMALE, diviserPar256((VALEUR_MAXIMALE - src[BLEU]) * src[ALPHA]) + dst[BLEU]);
            result[ALPHA] = dst[ALPHA];
        }
	},
	PINCEAU_SOUSTRACTION {
		@Override
        public void peindre(final int[] src, final int[] dst, final int[] result) {
			result[ROUGE] = Math.max(0, dst[ROUGE] - diviserPar256(src[ROUGE] * src[ALPHA]));
			result[VERT] = Math.max(0, dst[VERT] - diviserPar256(src[VERT] * src[ALPHA]));
			result[BLEU] = Math.max(0, dst[BLEU] - diviserPar256(src[BLEU] * src[ALPHA]));
            result[ALPHA] = dst[ALPHA];
        }
	},
	PINCEAU_SOUSTRACTION_NEGATIF {
		@Override
        public void peindre(final int[] src, final int[] dst, final int[] result) {
			result[ROUGE] = Math.max(0, dst[ROUGE] - diviserPar256((VALEUR_MAXIMALE - src[ROUGE]) * src[ALPHA]));
			result[VERT] = Math.max(0, dst[VERT] - diviserPar256((VALEUR_MAXIMALE - src[VERT]) * src[ALPHA]));
			result[BLEU] = Math.max(0, dst[BLEU] - diviserPar256((VALEUR_MAXIMALE - src[BLEU]) * src[ALPHA]));
            result[ALPHA] = dst[ALPHA];
        }
	},
	PINCEAU_TOPKEK {
		private static final float ECLAIRCISSEMENT = 0.4f;
		private static final float ASSOMBRISSEMENT = 0.6f;
		private static final float JAUNISSEMENT = 0.06f;
		private static final float BLEUISSEMENT = 0.2f;
		
		@Override
		public void peindre(final int[] src, final int[] dst, final int[] result) {
			final int luminositeFiltre = (src[ROUGE] + src[VERT] + src[BLEU])/3;
			
			// Le filtre impose sa luminosit� a l'ecran
			final int l = luminositeFiltre * 2 - VALEUR_MAXIMALE;
			final int luminositeEcran = (dst[ROUGE] + dst[VERT] + dst[BLEU])/3;
			final float coefficientLuminosite = l>=0 ? ECLAIRCISSEMENT : ASSOMBRISSEMENT;
			
			// Dans la lumi�re, les couleurs sont plus vives ; dans l'obscurit�, plus fades.
			int saturationInt = 2 * luminositeFiltre;
			final int deltaRouge = dst[ROUGE] - luminositeEcran;
			final int deltaVert = dst[VERT] - luminositeEcran;
			final int deltaBleu = dst[BLEU] - luminositeEcran;
			
			// Dans la lumi�re, l'image est plus jaune : dans l'obscurit�, plus bleue.
			final float coefficientJaune = l>=0 ? JAUNISSEMENT : 0;
			final float coefficientBleu = l>=0 ? 0 : -BLEUISSEMENT;
			
			// C'est peut-etre plus performant si on n'utilise pas de floats dans le calcul
			int coefficientLuminositeInt = (int) (coefficientLuminosite * VALEUR_MAXIMALE);
			int coefficientJauneInt = (int) (coefficientJaune * VALEUR_MAXIMALE);
			int coefficientBleuInt = (int) (coefficientBleu * VALEUR_MAXIMALE);
			final int resultatRouge = luminositeEcran + diviserPar256(l * coefficientLuminositeInt + deltaRouge * saturationInt + l * coefficientJauneInt);
			final int resultatVert = luminositeEcran + diviserPar256(l * coefficientLuminositeInt + deltaVert * saturationInt + l * coefficientJauneInt);
			final int resultatBleu = luminositeEcran + diviserPar256(l * coefficientLuminositeInt + deltaBleu * saturationInt + l * coefficientBleuInt);
			
			result[ROUGE] = seuiller(diviserPar256(dst[ROUGE] * (VALEUR_MAXIMALE - src[ALPHA]) + resultatRouge * src[ALPHA]));
			result[VERT] = seuiller(diviserPar256(dst[VERT] * (VALEUR_MAXIMALE - src[ALPHA]) + resultatVert * src[ALPHA]));
			result[BLEU] = seuiller(diviserPar256(dst[BLEU] * (VALEUR_MAXIMALE - src[ALPHA]) + resultatBleu * src[ALPHA]));
            result[ALPHA] = dst[ALPHA];
		}
	},
	PINCEAU_TON_DE_L_ECRAN {
		@Override
        public void peindre(final int[] ton, final int[] dst, final int[] result) {
			final int desaturationInt = ton[ALPHA];
			final int saturationInt = VALEUR_MAXIMALE - desaturationInt;
        	
			final int tauxRouge = 2 * dst[ROUGE] - VALEUR_MAXIMALE; // entre -256 et +256
			final int tauxVert = 2 * dst[VERT] - VALEUR_MAXIMALE; // entre -256 et +256
			final int tauxBleu = 2 * dst[BLEU] - VALEUR_MAXIMALE; // entre -256 et +256

        	final int luminosite = (dst[ROUGE] + dst[VERT] + dst[BLEU])/3;
			final int baseRouge = diviserPar256(ton[ROUGE] * saturationInt + luminosite * desaturationInt);
			final int baseVert = diviserPar256(ton[VERT] * saturationInt + luminosite * desaturationInt);
			final int baseBleu = diviserPar256(ton[BLEU] * saturationInt + luminosite * desaturationInt);
        	
			// tauxRouge/256 est entre -1 et +1
			// (VALEUR_MAXIMALE - baseRouge)*saturationInt /256 est entre 0 et 256
			// donc il faut diviser deux fois par 256
			result[ROUGE] = seuiller(baseRouge + diviserPar256(diviserPar256((VALEUR_MAXIMALE - baseRouge) * tauxRouge * saturationInt)));
			result[VERT] = seuiller(baseVert + diviserPar256(diviserPar256((VALEUR_MAXIMALE - baseVert) * tauxVert * saturationInt)));
			result[BLEU] = seuiller(baseBleu + diviserPar256(diviserPar256((VALEUR_MAXIMALE - baseBleu) * tauxBleu * saturationInt)));
            result[ALPHA] = dst[ALPHA];
        }
	};
	
	
	private static final Logger LOG = LogManager.getLogger(Pinceau.class);
	/** veritable valeur maximale des couleurs */
	private static final int VALEUR_MAXIMALE = 255; 
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
	 * Ne pas depasser les valeurs limites d'un couleur.
	 * 
	 * @param valeur a tronquer
	 * @return troncature
	 */
	private static int seuiller(final int valeur) {
		return Math.max(0, Math.min(VALEUR_MAXIMALE, valeur));
	}

	private static int diviserPar256(final int valeur) {
		return valeur / 2 / 2 / 2 / 2 / 2 / 2 / 2 / 2;
    }
}
