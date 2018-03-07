package utilitaire.graphismes;

import java.awt.CompositeContext;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.Lecteur;
import main.Main;

/**
 * Tout Composite possède un Contexte.
 * Celui-ci applique le Pinceau pour peindre chaque pixel de l'image superposée sur l'image support.
 */
public class ContexteDeComposite implements CompositeContext {
	protected static final Logger LOG = LogManager.getLogger(ContexteDeComposite.class);
	
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
		
		// changer le ton de l'écran ?
		final int[] ton = ModeDeFusion.TON_DE_L_ECRAN.nom.equals(composite.modeDeFusion.nom) ? composite.ton : null;
		
		//buffers
		final DataBuffer srcDataBuffer = src.getDataBuffer();
		final DataBuffer dstDataBuffer = dstIn.getDataBuffer();
		
		// On effectue l'opération graphique en multithread : chaque ligne de pixel a son thread.
		final ExecutorService executor = Executors.newFixedThreadPool(Main.NOMBRE_DE_THREADS);
		ThreadOperationGraphique.initialiserParametreGlobaux(srcDataBuffer, dstDataBuffer, largeur, 
				src, dstIn, dstOut, pinceau, opacite, ton);
		for (int y = 0; y < hauteur; y++) {
			executor.submit(new ThreadOperationGraphique(y)); //chaque thread modifie une ligne du dstOut
		}
		executor.shutdown();
		// On attend la fin de l'execution pour toutes les lignes de pixels de l'image
		try {
			while (!executor.awaitTermination(Lecteur.DUREE_FRAME, TimeUnit.MILLISECONDS)) {
				LOG.warn("L'opération graphique n'est pas encore terminée...");
			}
		} catch (InterruptedException e) {
			LOG.error("Impossible d'attendre la fin de l'opération graphique !", e);
		}
		
		// Le dstOut contient à présent le mélange entre le src et le dstIn.
	}


	@Override
	public void dispose() {
		// rien
	}
	
	/**
	 * L'opération graphique est réalisée en multithread pour aller plus vite.
	 * Chaque ligne de pixels de l'image correspond à un thread.
	 */
	protected static class ThreadOperationGraphique implements Runnable {
		private final int y;
		private static DataBuffer srcDataBuffer;
		private static DataBuffer dstDataBuffer;
		private static int largeur;
		private static Raster src; 
		private static Raster dstIn; 
		private static volatile WritableRaster dstOut;
		private static Pinceau pinceau;
		private static float opacite;
		private static int[] ton;
		
		/**
		 * Ces paramètres sont communs à toutes les lignes de pixels de l'image.
		 * @param srcDataBuffer tampon de l'image apposée
		 * @param dstDataBuffer tampon de l'image cible
		 * @param largeur de l'image
		 * @param src raster de l'image apposée
		 * @param dstIn raster de l'image apposée
		 * @param dstOut raster du résultat du mélange
		 * @param pinceau qui effectue le mélange sur chaque pixel
		 * @param opacite de l'opération graphique
		 * @param ton null dans le cas normal, non null dans le cas d'un ton d'écran
		 */
		public static void initialiserParametreGlobaux(final DataBuffer srcDataBuffer, final DataBuffer dstDataBuffer, 
				final int largeur, final Raster src, final Raster dstIn, final WritableRaster dstOut, 
				final Pinceau pinceau, final float opacite, final int[] ton
		) {
			ThreadOperationGraphique.srcDataBuffer = srcDataBuffer;
			ThreadOperationGraphique.dstDataBuffer = dstDataBuffer;
			ThreadOperationGraphique.largeur = largeur;
			ThreadOperationGraphique.src = src;
			ThreadOperationGraphique.dstIn = dstIn;
			ThreadOperationGraphique.dstOut = dstOut;
			ThreadOperationGraphique.pinceau = pinceau;
			ThreadOperationGraphique.opacite = opacite;
			ThreadOperationGraphique.ton = ton;
		}
		
		/**
		 * Initialisation du thread
		 * @param y numéro de la ligne de pixels
		 */
		public ThreadOperationGraphique(final int y) {
			this.y = y;
		}
		
		@Override
		public final void run() {
	        final int[] result = new int[4];
	        final int[] srcPixel;
	        final int[] dstPixel = new int[4];
	        final int[] srcPixels = new int[largeur];
	        final byte[] srcPixelsBytes = new byte[largeur*4]; //4 bytes pour faire un int
	        final int[] dstPixels = new int[largeur];      
	        final byte[] dstPixelsBytes = new byte[largeur*4]; //4 bytes pour faire un int
			
			//récupérer le tableau de pixels
	        if (ton == null) {
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
	        	// le pixel de l'image source sera utilisé comme source
	        	srcPixel  = new int[4];
	        } else {
	        	// ton de l'écran utilisé comme source
	        	srcPixel = ton;
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
            	int pixel;
            	if (ton == null) {
            		pixel = srcPixels[x];
            		srcPixel[0] = (pixel >> 24) & 0xFF;
                    srcPixel[1] = (pixel >> 16) & 0xFF;
                    srcPixel[2] = (pixel >>  8) & 0xFF;
                    srcPixel[3] = (pixel      ) & 0xFF;
            	}
                
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

}
