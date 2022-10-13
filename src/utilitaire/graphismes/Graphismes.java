package utilitaire.graphismes;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.Fenetre;
import main.Main;
import utilitaire.Maths;

/**
 * CLasse utilitaire pour les op�rations graphiques r�currentes.
 */
public abstract class Graphismes {
	// constantes
	private static final Logger LOG = LogManager.getLogger(Graphismes.class);
	/** Mod�le de couleur unique pour toutes les images du jeu */
	public static final ColorModel COLORMODEL = new BufferedImage(1, 1, Graphismes.TYPE_DES_IMAGES).getColorModel();
	/** Type unique pour toutes les images du jeu */
	public static final int TYPE_DES_IMAGES = BufferedImage.TYPE_INT_ARGB;

	/** Valeur (en pourcents) repr�sentant l'absence d'homoth�tie */
	public static final int PAS_D_HOMOTHETIE = 100;
	/** Valeur (en degr�s) repr�sentant l'absence de rotation */
	public static final int PAS_DE_ROTATION = 0;
	/** Valeur (sur 255) repr�sentant l'absence de transparence */
	public static final int OPACITE_MAXIMALE = 255;
	/** Ton par d�faut des images */
	public static final int[] TON_PAR_DEFAUT = new int[] { 0, 127, 127, 127 };
	/** L'origine de l'image est son coin haut-gauche et non son centre */
	private static final boolean ORIGINE_HAUT_GAUCHE = false;
	private static final Color COULEUR_TRANSPARENTE = new Color(0, 0, 0, 0);
	public static Graphics2D graphismes;

	/**
	 * Superposer deux images
	 * 
	 * @param ecran  image de fond, sur laquelle on va superposer l'autre
	 * @param image2 image du dessus, superpos�e sur l'ecran
	 * @param x      position x o� on superpose l'image2
	 * @param y      position y o� on superpose l'image2
	 * @return ecran sur lequel on a superpos� l'image2
	 */
	public static final BufferedImage superposerImages(BufferedImage ecran, final BufferedImage image2, final int x,
			final int y) {
		return superposerImages(ecran, image2, x, y, OPACITE_MAXIMALE);
	}

	/**
	 * Superposer deux images
	 * 
	 * @param ecran   image de fond, sur laquelle on va superposer l'autre
	 * @param image2  image du dessus, superpos�e sur l'ecran
	 * @param x       position x o� on superpose l'image2
	 * @param y       position y o� on superpose l'image2
	 * @param opacite transparence de l'image2 entre 0 et 255
	 * @return ecran sur lequel on a superpos� l'image2
	 */
	public static final BufferedImage superposerImages(BufferedImage ecran, final BufferedImage image2, final int x,
			final int y, final int opacite) {
		return superposerImages(ecran, image2, x, y, opacite, ModeDeFusion.NORMAL);
	}

	/**
	 * Superposer deux images
	 * 
	 * @param ecran        image de fond, sur laquelle on va superposer l'autre
	 * @param image2       image du dessus, superpos�e sur l'ecran
	 * @param x            position x o� on superpose l'image2
	 * @param y            position y o� on superpose l'image2
	 * @param opacite      transparence de l'image2 entre 0 et 255
	 * @param modeDeFusion fa�on dont on superpose les deux images
	 * @return ecran sur lequel on a superpos� l'image2
	 */
	public static final BufferedImage superposerImages(BufferedImage ecran, final BufferedImage image2, final int x,
			final int y, final int opacite, final ModeDeFusion modeDeFusion) {
		return superposerImages(ecran, image2, x, y, ORIGINE_HAUT_GAUCHE, PAS_D_HOMOTHETIE, PAS_D_HOMOTHETIE, opacite,
				modeDeFusion, PAS_DE_ROTATION);
	}

	/**
	 * Superposer deux images
	 * 
	 * @param ecran        image de fond, sur laquelle on va superposer l'autre
	 * @param image2       image du dessus, superpos�e sur l'ecran
	 * @param x            position x o� on superpose l'image2
	 * @param y            position y o� on superpose l'image2
	 * @param centre       l'origine de l'image est-elle son centre ?
	 * @param zoomX        zoom horizontal (en pourcents)
	 * @param zoomY        zoom vertical (en pourcents)
	 * @param opacite      transparence de l'image2 entre 0 et 255
	 * @param modeDeFusion fa�on dont on superpose les deux images
	 * @param angle        de rotation de l'image
	 * @return ecran sur lequel on a superpos� l'image2
	 */
	public static final BufferedImage superposerImages(BufferedImage ecran, BufferedImage image2, int x, int y,
			final boolean centre, final int zoomX, final int zoomY, final int opacite, final ModeDeFusion modeDeFusion,
			final int angle) {
		final Graphics2D g2d = (Graphics2D) ecran.getGraphics();

		if (opacite <= 0) {
			return ecran;
		}

		// transparence et mode de fusion
		if (opacite < OPACITE_MAXIMALE || !ModeDeFusion.NORMAL.equals(modeDeFusion)) {
			if (ModeDeFusion.TON_DE_L_ECRAN.equals(modeDeFusion)) {
				LOG.warn("Pour modifier le ton de l'ecran, utiliser plut�t la Methode Graphismes.appliquerTon().");
			}
			final float alpha = (float) opacite / OPACITE_MAXIMALE;
			final Composite comp = MonComposite.creerComposite(modeDeFusion, alpha);
			g2d.setComposite(comp);
		}

		// zoom
		if (zoomX != Graphismes.PAS_D_HOMOTHETIE || zoomY != Graphismes.PAS_D_HOMOTHETIE) {
			final int largeur = image2.getWidth() * zoomX / 100;
			final int hauteur = image2.getHeight() * zoomY / 100;
			image2 = Graphismes.redimensionner(image2, largeur, hauteur);
		}
		// origine
		if (centre) {
			// l'origine de l'image est son centre
			x -= image2.getWidth() / 2;
			y -= image2.getHeight() / 2;
		}

		if (angle == PAS_DE_ROTATION) {
			g2d.drawImage(image2, null, x, y);
		} else {
			// rotation de l'image
			final double angleRadians = Math.toRadians(angle);

			// La translation avant la rotation n'est pas forc�ment la m�me apres la
			// rotation, car la taille de l'image a chang�
			final double preTranslationX = image2.getWidth() / 2;
			final double preTranslationY = image2.getHeight() / 2;

			// On pivote les coins de l'image pour conna�tre la nouvelle largeur/hauteur
			final int[][] coins = new int[][] { { x, y }, { x + image2.getWidth(), y }, { x, y + image2.getHeight() },
					{ x + image2.getWidth(), y + image2.getHeight() } };
			final int[][] coinsPivotes = new int[4][2];
			for (int i = 0; i < 4; i++) {
				coinsPivotes[i][0] = (int) (coins[i][0] * Math.cos(angleRadians)
						- coins[i][1] * Math.sin(angleRadians));
				coinsPivotes[i][1] = (int) (coins[i][0] * Math.sin(angleRadians)
						- coins[i][1] * Math.cos(angleRadians));
			}
			final int nouvelleLargeur = Maths.max(coinsPivotes[0][0], coinsPivotes[1][0], coinsPivotes[2][0],
					coinsPivotes[3][0])
					- Maths.min(coinsPivotes[0][0], coinsPivotes[1][0], coinsPivotes[2][0], coinsPivotes[3][0]);
			final int nouvelleHauteur = Maths.max(coinsPivotes[0][1], coinsPivotes[1][1], coinsPivotes[2][1],
					coinsPivotes[3][1])
					- Maths.min(coinsPivotes[0][1], coinsPivotes[1][1], coinsPivotes[2][1], coinsPivotes[3][1]);
			final int postTranslationX = nouvelleLargeur / 2;
			final int postTranslationY = nouvelleHauteur / 2;

			// Rotation de l'image
			final AffineTransform tx = new AffineTransform();
			tx.translate(postTranslationX, postTranslationY); // �tape 3 : on la red�cale sur son coin
			tx.rotate(angleRadians); // �tape 2 : on la tourne
			tx.translate(-preTranslationX, -preTranslationY); // �tape 1 : on d�cale l'image sur son centre
			final AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
			final BufferedImage imagePivotee = op.filter(image2, null);
			final int nouveauX = x + (int) preTranslationX - postTranslationX;
			final int nouveauY = y + (int) preTranslationY - postTranslationY;
			g2d.drawImage(imagePivotee, nouveauX, nouveauY, null);
		}

		return ecran;
	}

	/**
	 * Op�ration graphique plus rapide adapt�e aux Tilesets.
	 * 
	 * @param dst  image sur laquelle on dessine
	 * @param src  image de laquelle on prend le carreau a dessiner
	 * @param xDst coordonn�e (en pixels) o� on dessine
	 * @param yDst coordonn�e (en pixels) o� on dessine
	 * @param xSrc coordonn�e (en pixels) o� on prend le carreau a dessiner
	 * @param ySrc coordonn�e (en pixels) o� on prend le carreau a dessiner
	 * @return image avec le carreau dessin�
	 */
	public static BufferedImage superposerPortionDImage(BufferedImage dst, BufferedImage src, int xDst, int yDst,
			int xSrc, int ySrc) {
		final Graphics2D g2d = (Graphics2D) dst.getGraphics();
		g2d.drawImage(src, xDst, yDst, xDst + Main.TAILLE_D_UN_CARREAU, yDst + Main.TAILLE_D_UN_CARREAU, xSrc, ySrc,
				xSrc + Main.TAILLE_D_UN_CARREAU, ySrc + Main.TAILLE_D_UN_CARREAU, COULEUR_TRANSPARENTE, null);
		return dst;
	}

	/**
	 * Produire un rectangle de couleur pour l'afficher comme ecran
	 * 
	 * @return un rectangle de couleur
	 */
	public static BufferedImage ecranColore(Color couleur) {
		BufferedImage image = new BufferedImage(Fenetre.LARGEUR_ECRAN, Fenetre.HAUTEUR_ECRAN,
				Graphismes.TYPE_DES_IMAGES);
		Graphics2D g2d = image.createGraphics();
		g2d.setPaint(couleur);
		g2d.fillRect(0, 0, Fenetre.LARGEUR_ECRAN, Fenetre.HAUTEUR_ECRAN);
		return image;
	}

	/**
	 * Produire un rectangle vide
	 * 
	 * @param largeur du rectangle
	 * @param hauteur du rectangle
	 * @return un rectangle sans couleur
	 */
	public static BufferedImage imageVide(final int largeur, final int hauteur) {
		BufferedImage image = new BufferedImage(largeur, hauteur, Graphismes.TYPE_DES_IMAGES);
		final Color couleur = new Color(0, 0, 0, 0);
		Graphics2D g2d = image.createGraphics();
		g2d.setPaint(couleur);
		g2d.fillRect(0, 0, largeur, hauteur);
		return image;
	}

	/**
	 * Produire un rectangle vide pour l'afficher comme ecran
	 * 
	 * @return un rectangle vide
	 */
	public static BufferedImage ecranVide() {
		return imageVide(Fenetre.LARGEUR_ECRAN, Fenetre.HAUTEUR_ECRAN);
	}

	/**
	 * Cloner une image mod�le.
	 * 
	 * @param image a cloner
	 * @return clone de l'image
	 */
	public static BufferedImage creerUneImageVideDeMemeTaille(final BufferedImage image) {
		final BufferedImage cloneVide = new BufferedImage(image.getWidth(), image.getHeight(),
				Graphismes.TYPE_DES_IMAGES);
		return cloneVide;
	}

	/**
	 * Cloner une image mod�le.
	 * 
	 * @param image a cloner
	 * @return clone de l'image
	 */
	public static BufferedImage clonerUneImage(final BufferedImage image) {
		final boolean isAlphaPremultiplied = COLORMODEL.isAlphaPremultiplied();
		WritableRaster raster = image.copyData(image.getRaster().createCompatibleWritableRaster());
		return new BufferedImage(COLORMODEL, raster, isAlphaPremultiplied, null);
	}

	/**
	 * Enregistrer une image dans l'ordinateur
	 * 
	 * @param image a enregistrer
	 * @param nom   de l'image enregistr�e
	 */
	public static void sauvegarderImage(final BufferedImage image, final String nom) {
		final File directory = new File(String.valueOf("C:/Users/Public/kujira"));
		if (!directory.exists()) {
			directory.mkdir();
		}

		try {
			final File outputfile = new File("C:/Users/Public/kujira/" + nom + ".png");
			ImageIO.write(image, "png", outputfile);
		} catch (IOException e) {
			LOG.error(e);
		}
	}

	/**
	 * Redimensionner une image.
	 * 
	 * @param image   a redimensionner
	 * @param largeur de la nouvelle image
	 * @param hauteur de la nouvelle image
	 * @return image redimensionn�e
	 */
	public static BufferedImage redimensionner(final BufferedImage image, final int largeur, final int hauteur) {
		final Image tmp = image.getScaledInstance(largeur, hauteur, Image.SCALE_FAST);
		final BufferedImage imageRedimensionnee = new BufferedImage(largeur, hauteur, BufferedImage.TYPE_INT_ARGB);

		final Graphics2D g2d = imageRedimensionnee.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		g2d.drawImage(tmp, 0, 0, null);

		return imageRedimensionnee;
	}

	/**
	 * Charger une image du dossier de ressources.
	 * 
	 * @param dossier sous-dossier du dossier Picture o� se trouve l'image
	 * @param nom     de l'image
	 * @return image charg�e, compatible avec la configuration officielle
	 * @throws IOException impossible d'ouvrir l'image
	 */
	public static BufferedImage ouvrirImage(final String dossier, final String nom) throws IOException {
		String dossierSlashNom = dossier + "/" + nom.replaceAll("[^a-zA-Z0-9éèàç _\\.-]", "");
		if (!nom.endsWith(".png")) {
			dossierSlashNom += ".png";
		}
		try {
			return convertirEnImageCompatible(ImageIO.read(new File("./ressources/Graphics/" + dossierSlashNom)),
					dossierSlashNom);
		} catch (IOException e) {
			if (nom != null && !nom.equals("")) {
				LOG.error("Impossible d'ouvrir l'image : " + dossierSlashNom, e);
			} else {
				LOG.warn("Pas d'image pour ce " + dossier);
			}
			throw e;
		}
	}

	/**
	 * Convertir une image qui n'est pas dans la bonne configuration.
	 * 
	 * @param image    dans une autre configuration
	 * @param nomImage a loguer en cas d'erreur
	 * @return image dans la configuration officielle
	 */
	private static BufferedImage convertirEnImageCompatible(final BufferedImage image, final String nomImage) {
		// Si l'image a d�j� le bon ColorModel, tout va bien
		if (image.getColorModel().equals(COLORMODEL)) {
			return image;
		}
		// TODO On sauvegarde l'image actuelle
		// sauvegarderImage(image, "/ressources/Graphics/"+nomImage+".old");
		// On convertit l'image dans le bon ColorModel
		LOG.debug("Conversion de l'image \"" + nomImage + "\" car elle n'a pas le ColorModel standard.");
		final BufferedImage compatibleImage = new BufferedImage(image.getWidth(), image.getHeight(), TYPE_DES_IMAGES);
		final Graphics2D g = compatibleImage.createGraphics();
		g.drawImage(image, 0, 0, null);
		// TODO On enregistre la nouvelle image
		// sauvegarderImage(compatibleImage, "/ressources/Graphics/"+nomImage);
		return compatibleImage;
	}

	/**
	 * Comparer deux tons.
	 * 
	 * @param ton  premier ton
	 * @param ton2 second ton
	 * @return true si les tons sont identiques, false sinon
	 */
	@Deprecated
	public static boolean memeTon(final int[] ton, final int[] ton2) {
		final int taille = Math.max(ton.length, ton2.length);
		for (int i = 0; i < taille; i++) {
			if (ton[i] != ton2[i]) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Appliquer un ton a une image.
	 * 
	 * @param image sur laquelle appliquer le ton
	 * @param ton   a appliquer (gris, rouge, vert, bleu)
	 * @return image modifi�e
	 */
	public static BufferedImage appliquerTon(final BufferedImage image, final int[] ton) {
		if (Graphismes.memeTon(ton, Graphismes.TON_PAR_DEFAUT)) {
			// pas de ton
			return image;
		}
		BufferedImage image2 = clonerUneImage(image);

		// composite tonal
		Graphics2D g2d = (Graphics2D) image2.createGraphics();
		final Composite comp = MonComposite.creerComposite(ton);
		g2d.setComposite(comp);

		// dessiner l'image
		g2d.drawImage(image, null, 0, 0); // ici peu importe l'image en argument, car la source sera le ton

		return image2;
	}

	/**
	 * Comparer deux images. Sont-elles identiques ?
	 * 
	 * @param image1 premi�re image
	 * @param image2 seconde image
	 * @return true si les images sont identiques
	 */
	public static boolean memeImage(BufferedImage image1, BufferedImage image2) {
		// Comparaison des dimensions
		if (image1.getWidth() != image2.getWidth() || image1.getHeight() != image2.getHeight()) {
			return false;
		}

		// Comparaison pixel a pixel
		for (int i = 0; i < image1.getWidth(); i++) {
			for (int j = 0; j < image2.getWidth(); j++) {
				if (image1.getRGB(i, j) != image2.getRGB(i, j)) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Dessiner un rectangle colore sur l'ecran
	 * 
	 * @param ecran   sur lequel on dessine
	 * @param x       coordonnee (en pixels)
	 * @param y       coordonnee (en pixels)
	 * @param largeur (en pixels)
	 * @param hauteur (en pixels)
	 * @param couleur contenant l'opacite souhaitee
	 */
	public static BufferedImage dessinerRectangle(BufferedImage ecran, int x, int y, int largeur, int hauteur,
			Color couleur) {
		final Graphics2D g2d = (Graphics2D) ecran.getGraphics();
		g2d.setColor(couleur);
		g2d.fillRect(x, y, largeur, hauteur);
		return ecran;
	}

}
