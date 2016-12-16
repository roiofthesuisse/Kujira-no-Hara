package utilitaire.graphismes;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.Fenetre;
import main.Lecteur;

/**
 * CLasse utilitaire pour les opérations graphiques récurrentes.
 */
public abstract class Graphismes {
	//constantes
	private static final Logger LOG = LogManager.getLogger(Graphismes.class);
	/** Valeur (en pourcents) représentant l'absence d'homothétie */
	public static final int PAS_D_HOMOTHETIE = 100;
	/** Valeur (en degrés) représentant l'absence de rotation */
	public static final int PAS_DE_ROTATION = 0;
	/** Valeur (sur 255) représentant l'absence de transparence */
	public static final int OPACITE_MAXIMALE = 255;
	/** L'origine de l'image est son coin haut-gauche et non son centre */
	private static final boolean ORIGINE_HAUT_GAUCHE = false;
	public static Graphics2D graphismes;
	/** Unique configuration autorisée pour le format des images */
	private static final GraphicsConfiguration CONFIGURATION = GraphicsEnvironment.
			getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();

	/**
	 * Superposer deux images
	 * @param ecran image de fond, sur laquelle on va superposer l'autre
	 * @param image2 image du dessus, superposée sur l'écran
	 * @param x position x où on superpose l'image2
	 * @param y position y où on superpose l'image2
	 * @return écran sur lequel on a superposé l'image2
	 */
	public static final BufferedImage superposerImages(BufferedImage ecran, final BufferedImage image2, final int x, final int y) {
		return superposerImages(ecran, image2, x, y, OPACITE_MAXIMALE);
	}
	
	/**
	 * Superposer deux images
	 * @param ecran image de fond, sur laquelle on va superposer l'autre
	 * @param image2 image du dessus, superposée sur l'écran
	 * @param x position x où on superpose l'image2
	 * @param y position y où on superpose l'image2
	 * @param opacite transparence de l'image2 entre 0 et 255
	 * @return écran sur lequel on a superposé l'image2
	 */
	public static final BufferedImage superposerImages(BufferedImage ecran, final BufferedImage image2, final int x, final int y, final int opacite) {
		return superposerImages(ecran, image2, x, y, opacite, ModeDeFusion.NORMAL);
	}
	
	/**
	 * Superposer deux images
	 * @param ecran image de fond, sur laquelle on va superposer l'autre
	 * @param image2 image du dessus, superposée sur l'écran
	 * @param x position x où on superpose l'image2
	 * @param y position y où on superpose l'image2
	 * @param opacite transparence de l'image2 entre 0 et 255
	 * @param modeDeFusion façon dont on superpose les deux images
	 * @return écran sur lequel on a superposé l'image2
	 */
	public static final BufferedImage superposerImages(BufferedImage ecran, final BufferedImage image2, final int x, final int y, final int opacite, final ModeDeFusion modeDeFusion) {
		return superposerImages(ecran, image2, x, y, ORIGINE_HAUT_GAUCHE, PAS_D_HOMOTHETIE, PAS_D_HOMOTHETIE, opacite, modeDeFusion, PAS_DE_ROTATION);
	}
	
	/**
	 * Superposer deux images
	 * @param ecran image de fond, sur laquelle on va superposer l'autre
	 * @param image2 image du dessus, superposée sur l'écran
	 * @param x position x où on superpose l'image2
	 * @param y position y où on superpose l'image2
	 * @param centre l'origine de l'image est-elle son centre ?
	 * @param zoomX zoom horizontal (en pourcents)
	 * @param zoomY zoom vertical (en pourcents)
	 * @param opacite transparence de l'image2 entre 0 et 255
	 * @param modeDeFusion façon dont on superpose les deux images
	 * @param angle de rotation de l'image
	 * @return écran sur lequel on a superposé l'image2
	 */
	public static final BufferedImage superposerImages(BufferedImage ecran, BufferedImage image2, int x, int y, 
			final boolean centre, final int zoomX, final int zoomY, final int opacite, final ModeDeFusion modeDeFusion, 
			final int angle) {
		final Graphics2D g2d = (Graphics2D) ecran.createGraphics();
		
		//transparence et mode de fusion
		if (opacite < OPACITE_MAXIMALE || !ModeDeFusion.NORMAL.equals(modeDeFusion)) {
			final float alpha = (float) opacite/OPACITE_MAXIMALE;
	        final Composite comp = MonComposite.creerComposite(modeDeFusion, alpha);
			g2d.setComposite(comp);
		}
		
		//zoom
		if (zoomX != Graphismes.PAS_D_HOMOTHETIE || zoomY != Graphismes.PAS_D_HOMOTHETIE) {
			final int largeur = image2.getWidth() * zoomX / 100;
			final int hauteur = image2.getHeight() * zoomY / 100;
			image2 = Graphismes.redimensionner(image2, largeur, hauteur);
		}
		//origine
		if (centre) {
			//l'origine de l'image est son centre
			x -= image2.getWidth()/2;
			y -= image2.getHeight()/2;
		}
		
		if (angle == PAS_DE_ROTATION) {
			g2d.drawImage(image2, null, x, y);
		} else {
			//rotation de l'image
			final double angleRadians = Math.toRadians(angle);
			
			//TODO la translation avant la rotation n'est pas forcément la même après la rotation, car la taille de l'image a changé
			final double centreRotationX = image2.getWidth() / 2;
			final double centreRotationY = image2.getHeight() / 2;
			
			final AffineTransform tx = new AffineTransform();
			//TODO inutile de faire des translate car rotate(angle, x, y); fait la même chose
			tx.translate(centreRotationX, centreRotationY); 
			//TODO utiliser plutôt la méthode tx.rotate(angleRadians); et voir si l'image est tronquée
			tx.rotate(angleRadians, centreRotationX, centreRotationY); 
			//TODO inutile de faire des translate car rotate(angle, x, y); fait la même chose
			tx.translate(-centreRotationX, -centreRotationY);
			final AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
			final BufferedImage imagePivotee = op.filter(image2, null);
			final int nouvelleLargeur = imagePivotee.getWidth();
			final int nouvelleHauteur = imagePivotee.getHeight();
			final int nouveauX = x + (int) centreRotationX - nouvelleLargeur/2;
			final int nouveauY = y + (int) centreRotationY - nouvelleHauteur/2;
			g2d.drawImage(imagePivotee, nouveauX, nouveauY, null);
		}
		g2d.dispose();
		
		return ecran;
	}
	
	/**
	 * Produire un rectangle noir pour l'afficher comme écran
	 * @return un rectangle noir
	 */
	public static BufferedImage ecranNoir() {
		BufferedImage image = new BufferedImage(Fenetre.LARGEUR_ECRAN, Fenetre.HAUTEUR_ECRAN, Lecteur.TYPE_DES_IMAGES);
		Graphics2D g2d = image.createGraphics();
		g2d.setPaint(Color.black);
		g2d.fillRect(0, 0, Fenetre.LARGEUR_ECRAN, Fenetre.HAUTEUR_ECRAN);
		g2d.dispose();
		return image;
	}
	
	/**
	 * Produire un rectangle vide
	 * @param largeur du rectangle
	 * @param hauteur du rectangle
	 * @return un rectangle sans couleur
	 */
	public static BufferedImage imageVide(final int largeur, final int hauteur) {
		BufferedImage image = new BufferedImage(largeur, hauteur, Lecteur.TYPE_DES_IMAGES);
		final Color couleur = new Color(0, 0, 0, 0);
		Graphics2D g2d = image.createGraphics();
		g2d.setPaint(couleur);
		g2d.fillRect(0, 0, largeur, hauteur);
		g2d.dispose();
		return image;
	}
	
	/**
	 * Produire un rectangle vide pour l'afficher comme écran
	 * @return un rectangle vide
	 */
	public static BufferedImage ecranVide() {
		return imageVide(Fenetre.LARGEUR_ECRAN, Fenetre.HAUTEUR_ECRAN);
	}
	
	/**
	 * Cloner une image modèle.
	 * @param image à cloner
	 * @return clone de l'image
	 */
	public static BufferedImage creerUneImageVideDeMemeTaille(final BufferedImage image) {
		final BufferedImage cloneVide = new BufferedImage(
				image.getWidth(), 
				image.getWidth(), 
				image.getType()
		);
		return cloneVide;
	}
	
	/**
	 * Cloner une image modèle.
	 * @param image à cloner
	 * @return clone de l'image
	 */
	public static BufferedImage clonerUneImage(final BufferedImage image) {
		BufferedImage clone = creerUneImageVideDeMemeTaille(image);
		
		// Ajout de l'image de boîte de dialogue
		clone = Graphismes.superposerImages(clone, image, 0, 0);
		return clone;
	}
	
	/**
	 * Enregistrer une image dans l'ordinateur
	 * @param image à enregistrer
	 * @param nom de l'image enregistrée
	 */
	public static void sauvegarderImage(final BufferedImage image, final String nom) {
		try {
			final File outputfile = new File("C:/Users/RoiOfTheSuisse/Pictures/"+nom+".png");
			ImageIO.write(image, "png", outputfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Redimensionner une image.
	 * @param image à redimensionner
	 * @param largeur de la nouvelle image
	 * @param hauteur de la nouvelle image
	 * @return image redimensionnée
	 */
	public static BufferedImage redimensionner(final BufferedImage image, final int largeur, final int hauteur) {
		final Image tmp = image.getScaledInstance(largeur, hauteur, Image.SCALE_SMOOTH);
	    final BufferedImage imageRedimensionnee = new BufferedImage(largeur, hauteur, BufferedImage.TYPE_INT_ARGB);

	    final Graphics2D g2d = imageRedimensionnee.createGraphics();
	    g2d.drawImage(tmp, 0, 0, null);
	    g2d.dispose();

	    return imageRedimensionnee;
	}

    /**
     * Charer une image du dossier de ressources.
     * @param dossier sous-dossier du dossier Picture où se trouve l'image
     * @param nom de l'image
     * @return image chargée, compatible avec la configuration officielle
     * @throws IOException impossible d'ouvrir l'image
     */
    public static BufferedImage ouvrirImage(final String dossier, final String nom) throws IOException {
    	try {
			return convertirEnImageCompatible(ImageIO.read(new File(".\\ressources\\Graphics\\"+dossier+"\\"+nom)));
		} catch (IOException e) {
			if (nom!=null && !nom.equals("")) {
				LOG.error("Impossible d'ouvrir l'image : "+dossier+"/"+nom);
				e.printStackTrace();
			} else {
				LOG.warn("Pas d'image pour ce "+dossier);
			}
			throw e;
		}
    }
    
    /**
	 * Convertir une image qui n'est pas dans la bonne configuration.
	 * @param image dans une autre configuration
	 * @return image dans la configuration officielle
	 */
    private static BufferedImage convertirEnImageCompatible(final BufferedImage image) {
        if (image.getColorModel().equals(CONFIGURATION.getColorModel())) {
            return image;
        }

        final BufferedImage compatibleImage = CONFIGURATION.createCompatibleImage(
                image.getWidth(), image.getHeight(), image.getTransparency());
        final Graphics g = compatibleImage.getGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();

        return compatibleImage;
    }

}
