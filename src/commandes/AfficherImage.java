package commandes;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jeu.Partie;
import main.Commande;
import main.Main;
import map.Picture;
import utilitaire.graphismes.Graphismes;
import utilitaire.graphismes.ModeDeFusion;

/**
 * Afficher une image par dessus l'�cran.
 */
public class AfficherImage extends Commande implements CommandeEvent {
	private static final Logger LOG = LogManager.getLogger(AfficherImage.class);

	private String nomImage;
	public BufferedImage image;
	/** num�ro de l'image � d�placer */
	private Integer numero; // Integer car utilis� comme cl� d'une HashMap

	/** la nouvelle origine est-elle le centre de l'image ? */
	private boolean centre;
	/** les coordonn�es sont-elles stock�es dans des variables ? */
	private boolean variables;
	private int x;
	private int y;
	private int zoomX;
	private int zoomY;
	private int opacite;
	private ModeDeFusion modeDeFusion;
	private int angle;

	/**
	 * Constructeur explicite
	 * 
	 * @param nomImage     nom de l'image � afficher
	 * @param numero       de l'image � afficher
	 * @param centre       son origine est-elle son centre ? sinon, son origine est
	 *                     son coin haut-gauche
	 * @param variables    les coordonn�es sont stock�es dans des variables
	 * @param x            coordonn�e d'affichage � l'�cran (en pixels)
	 * @param y            coordonn�e d'affichage � l'�cran (en pixels)
	 * @param zoomX        �tirement horizontal (en pourcents)
	 * @param zoomY        �tirement vertical (en pourcents)
	 * @param opacite      de l'image (sur 255)
	 * @param modeDeFusion de la superposition d'images
	 * @param angle        de rotation de l'image (en degr�s)
	 */
	public AfficherImage(final String nomImage, final int numero, final boolean centre, final boolean variables,
			final int x, final int y, final int zoomX, final int zoomY, final int opacite,
			final ModeDeFusion modeDeFusion, final int angle) {
		this.nomImage = nomImage;
		this.numero = numero;
		this.centre = centre;
		this.variables = variables;
		this.x = x;
		this.y = y;
		this.zoomX = zoomX;
		this.zoomY = zoomY;
		this.opacite = opacite;
		this.modeDeFusion = modeDeFusion;
		this.angle = angle;
	}

	/**
	 * Constructeur g�n�rique
	 * 
	 * @param parametres liste de param�tres issus de JSON
	 */
	public AfficherImage(final HashMap<String, Object> parametres) {
		this((String) parametres.get("nomImage"), (int) parametres.get("numero"),
				parametres.containsKey("centre") ? (boolean) parametres.get("centre") : false,
				parametres.containsKey("variables") ? (boolean) parametres.get("variables") : false,
				parametres.containsKey("x") ? (int) parametres.get("x") : 0,
				parametres.containsKey("y") ? (int) parametres.get("y") : 0,
				parametres.containsKey("zoomX") ? (int) parametres.get("zoomX") : Graphismes.PAS_D_HOMOTHETIE,
				parametres.containsKey("zoomY") ? (int) parametres.get("zoomY") : Graphismes.PAS_D_HOMOTHETIE,
				parametres.containsKey("opacite") ? (int) parametres.get("opacite") : Graphismes.OPACITE_MAXIMALE,
				ModeDeFusion.parNom(parametres.get("modeDeFusion")),
				parametres.containsKey("angle") ? (int) parametres.get("angle") : Graphismes.PAS_DE_ROTATION);
	}

	@Override
	public final int executer(final int curseurActuel, final List<Commande> commandes) {
		// On v�rifie si �a n'a pas d�j� �t� fait
		if (this.dejaALEcran()) {
			// L'image est d�j� � l'�cran
			// LOG.warn("Image "+this.numero+" \""+this.nomImage+"\" d�j� pr�sente � l'�cran
			// !");
			return curseurActuel + 1;
		}
		// L'image n'est pas encore � l'�cran

		try {
			this.image = Graphismes.ouvrirImage("Pictures", this.nomImage);
		} catch (IOException e) {
			LOG.error("Impossible d'ouvrir l'image " + nomImage, e);
			return curseurActuel + 1;
		}

		// coordonn�es
		final int xAffichage, yAffichage;
		if (this.variables) {
			// valeurs stock�es dans des variables
			xAffichage = getPartieActuelle().variables[this.x];
			yAffichage = getPartieActuelle().variables[this.y];
		} else {
			// valeurs brutes
			xAffichage = this.x;
			yAffichage = this.y;
		}

		final Picture picture = new Picture(this.image, this.nomImage, this.numero, xAffichage, yAffichage, centre,
				zoomX, zoomY, this.opacite, this.modeDeFusion, this.angle);
		getPartieActuelle().images[picture.numero] = picture;

		return curseurActuel + 1;
	}

	/**
	 * Est-ce que l'image � afficher est d�j� � l'�cran ?
	 * 
	 * @return true s'il n'y a rien � faire, false sinon
	 */
	private boolean dejaALEcran() {
		final Partie partieActuelle = Main.getPartieActuelle();
		final Picture pictureActuelle = partieActuelle.images[this.numero];
		return pictureActuelle != null && pictureActuelle.nomImage.equals(this.nomImage)
				&& (this.variables
						? pictureActuelle.x == getPartieActuelle().variables[this.x]
								&& pictureActuelle.y == getPartieActuelle().variables[this.y]
						: pictureActuelle.x == this.x && pictureActuelle.y == this.y)
				&& pictureActuelle.centre == this.centre && pictureActuelle.zoomX == this.zoomX
				&& pictureActuelle.zoomY == this.zoomY && pictureActuelle.angle == this.angle
				&& pictureActuelle.opacite == this.opacite && pictureActuelle.modeDeFusion.equals(this.modeDeFusion);
	}

}
