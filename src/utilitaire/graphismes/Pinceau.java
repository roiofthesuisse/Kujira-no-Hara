package utilitaire.graphismes;

/**
 * Le Pinceau va peindre un pixel de l'image à superposer sur l'image support.
 * La façon dont le pixel sera peint dépend du Composite utilisé.
 */
public abstract class Pinceau {
	public static final int VALEUR_MAXIMALE = 255;
	private static final int ROUGE = 0;
	private static final int VERT = 1;
	private static final int BLEU = 2;
	private static final int ALPHA = 3;
	
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
                        result[ROUGE] = Math.min(VALEUR_MAXIMALE, src[ROUGE] + dst[ROUGE]);
                        result[VERT] = Math.min(VALEUR_MAXIMALE, src[VERT] + dst[VERT]);
                        result[BLEU] = Math.min(VALEUR_MAXIMALE, src[BLEU] + dst[BLEU]);
                        result[ALPHA] = Math.min(VALEUR_MAXIMALE, src[ALPHA] + dst[ALPHA]);
                    }
                };
            case SOUSTRACTION:
                return new Pinceau() {
                    @Override
                    public void peindre(final int[] src, final int[] dst, final int[] result) {
                    	result[ROUGE] = Math.max(0, dst[ROUGE] - src[ROUGE]);
                        result[VERT] = Math.max(0, dst[VERT] - src[VERT]);
                        result[BLEU] = Math.max(0, dst[BLEU] - src[BLEU]);
                        result[ALPHA] = Math.min(VALEUR_MAXIMALE, src[ALPHA] + dst[ALPHA]);
                    }
                };
            default:
            	System.err.println("Blender non défini pour le mode de fusion : "+composite.modeDeFusion.nom);
            	return null;
        }
    }
}
