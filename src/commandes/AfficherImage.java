package commandes;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

import main.Commande;
import main.Fenetre;
import map.Picture;
import utilitaire.graphismes.Graphismes;
import utilitaire.graphismes.ModeDeFusion;

/**
 * Afficher une image par dessus l'écran.
 */
public class AfficherImage extends Commande implements CommandeEvent {
	private String nomImage;
	public BufferedImage image;
	/** numéro de l'image à déplacer */
	private Integer numero; //Integer car utilisé comme clé d'une HashMap
	
	/** la nouvelle origine est-elle le centre de l'image ? */
	private boolean centre;
	/** les coordonnées sont-elles stockées dans des variables ? */
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
	 * @param nomImage nom de l'image à afficher
	 * @param numero de l'image à afficher
	 * @param centre son origine est-elle son centre ? sinon, son origine est son coin haut-gauche
	 * @param variables les coordonnées sont stockées dans des variables 
	 * @param x coordonnée d'affichage à l'écran (en pixels)
	 * @param y coordonnée d'affichage à l'écran (en pixels)
	 * @param zoomX étirement horizontal (en pourcents)
	 * @param zoomY étirement vertical (en pourcents)
	 * @param opacite de l'image (sur 255)
	 * @param modeDeFusion de la superposition d'images
	 * @param angle de rotation de l'image (en degrés)
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
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public AfficherImage(final HashMap<String, Object> parametres) {
		this( (String) parametres.get("nomImage"),
				(int) parametres.get("numero"),
				parametres.containsKey("centre") ? (boolean) parametres.get("centre") : false,
				parametres.containsKey("variables") ? (boolean) parametres.get("variables") : false,
				parametres.containsKey("x") ? (int) parametres.get("x") : 0,
				parametres.containsKey("y") ? (int) parametres.get("y") : 0,
				parametres.containsKey("zoomX") ? (int) parametres.get("zoomX") : Graphismes.PAS_D_HOMOTHETIE,
				parametres.containsKey("zoomY") ? (int) parametres.get("zoomY") : Graphismes.PAS_D_HOMOTHETIE,
				parametres.containsKey("opacite") ? (int) parametres.get("opacite") : Graphismes.OPACITE_MAXIMALE,
				ModeDeFusion.parNom(parametres.get("modeDeFusion")),
				parametres.containsKey("angle") ? (int) parametres.get("angle") : Graphismes.PAS_DE_ROTATION
		);
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		try {
			this.image = ImageIO.read(new File(".\\ressources\\Graphics\\Pictures\\"+this.nomImage));
		} catch (IOException e) {
			System.err.println("Impossible d'ouvrir l'image "+nomImage);
			e.printStackTrace();
			return curseurActuel+1;
		}
		
		//coordonnées
		int xAffichage;
		int yAffichage;
		if (this.variables) {
			//valeurs stockées dans des variables
			xAffichage = Fenetre.getPartieActuelle().variables[this.x];
			yAffichage = Fenetre.getPartieActuelle().variables[this.y];
		} else {
			//valeurs brutes
			xAffichage = this.x;
			yAffichage = this.y;
		}
		
		final Picture picture = new Picture(this.image, this.numero, xAffichage, yAffichage, centre, zoomX, zoomY, this.opacite, this.modeDeFusion, this.angle);
		Fenetre.getPartieActuelle().images.put(picture.numero, picture);
		
		return curseurActuel+1;
	}

}
