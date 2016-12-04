package utilitaire.graphismes;

import java.awt.CompositeContext;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

/**
 * Tout Composite possède un Contexte.
 * Celui-ci applique le Pinceau pour peindre chaque pixel de l'image superposée sur l'image support.
 */
public class ContexteDeComposite implements CompositeContext {
	private final Pinceau pinceau;
    private final MonComposite composite;

    /**
     * Constructeur explicite
     * @param composite définissant la nature de la superposition d'images
     */
    public ContexteDeComposite(final MonComposite composite) {
        this.composite = composite;
        this.pinceau = Pinceau.obtenirPinceauPour(composite);
    }
    
	@Override
	public final void compose(final Raster src, final Raster dstIn, final WritableRaster dstOut) {
		final int largeur = Math.min(src.getWidth(), dstIn.getWidth());
        final int hauteur = Math.min(src.getHeight(), dstIn.getHeight());

        final float opacite = composite.opacite;

        final int[] result = new int[4];
        final int[] srcPixel = new int[4];
        final int[] dstPixel = new int[4];
        final int[] srcPixels = new int[largeur];
        final byte[] srcPixelsBytes = new byte[largeur*4]; //4 bytes pour faire un int
        final int[] dstPixels = new int[largeur];      
        final byte[] dstPixelsBytes = new byte[largeur*4]; //4 bytes pour faire un int
        
        //buffers
        final DataBuffer srcDataBuffer = src.getDataBuffer();
        final DataBuffer dstDataBuffer = dstIn.getDataBuffer();
        
        for (int y = 0; y < hauteur; y++) {
        	//récupérer le tableau de pixels
        	if (srcDataBuffer.getDataType() == DataBuffer.TYPE_INT) {
	            src.getDataElements(0, y, largeur, 1, srcPixels);
        	} else {
        		//l'image source n'a pas des pixels de type int, il faut les convertir
        		src.getDataElements(0, y, largeur, 1, srcPixelsBytes);

        		final IntBuffer intBuffer =
        				   ByteBuffer.wrap(srcPixelsBytes)
        				     .order(ByteOrder.LITTLE_ENDIAN)
        				     .asIntBuffer();
        		intBuffer.get(srcPixels);
        	}
        	if (dstDataBuffer.getDataType() == DataBuffer.TYPE_INT) {
        		dstIn.getDataElements(0, y, largeur, 1, dstPixels);
        	} else {
        		//l'image de destination n'a pas des pixels de type int, il faut les convertir
        		dstIn.getDataElements(0, y, largeur, 1, dstPixelsBytes);

        		final IntBuffer intBuffer =
     				   ByteBuffer.wrap(dstPixelsBytes)
     				     .order(ByteOrder.LITTLE_ENDIAN)
     				     .asIntBuffer();
        		intBuffer.get(dstPixels);
        	}
        	
            for (int x = 0; x < largeur; x++) {
                //les pixels sont des INT_ARGB
                int pixel = srcPixels[x];
                srcPixel[0] = (pixel >> 24) & 0xFF;
                srcPixel[1] = (pixel >> 16) & 0xFF;
                srcPixel[2] = (pixel >>  8) & 0xFF;
                srcPixel[3] = (pixel      ) & 0xFF;
                
                pixel = dstPixels[x];
                dstPixel[0] = (pixel >> 24) & 0xFF;
                dstPixel[1] = (pixel >> 16) & 0xFF;
                dstPixel[2] = (pixel >>  8) & 0xFF;
                dstPixel[3] = (pixel      ) & 0xFF;

                //calcul du résultat
                pinceau.peindre(srcPixel, dstPixel, result);

                //pondérer le résultat selon l'opacité globale
                dstPixels[x] = ((int) (dstPixel[0] + (result[0] - dstPixel[0]) * opacite) & 0xFF) << 24 |
		                       ((int) (dstPixel[1] + (result[1] - dstPixel[1]) * opacite) & 0xFF) << 16 |
		                       ((int) (dstPixel[2] + (result[2] - dstPixel[2]) * opacite) & 0xFF) <<  8 |
		                        (int) (dstPixel[3] + (result[3] - dstPixel[3]) * opacite) & 0xFF;
            }
            dstOut.setDataElements(0, y, largeur, 1, dstPixels);
        }
    }


	@Override
	public void dispose() {
		// rien
	}

}
