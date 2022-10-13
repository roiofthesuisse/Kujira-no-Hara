package map;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.json.JSONObject;

import commandes.DeplacerImage;
import commandes.Sauvegarder.Sauvegardable;
import main.Main;
import utilitaire.graphismes.Graphismes;
import utilitaire.graphismes.ModeDeFusion;

/**
 * Image a afficher a l'ecran et ses param�tres.
 */
public class Picture implements Sauvegardable {
	//constantes
	/** Au dela du separateur, les images seront affichees au dessus du HUD */
	public static final int SEPARATEUR_IMAGES_BASSES = 5;
	
	public String nomImage;
	public BufferedImage image;
	public Integer numero; //Integer car utilis� comme cl� d'une HashMap
	/** coordonn�e x d'affichage par rapport au coin de l'ecran */
	public int x;
	/** coordonn�e y d'affichage par rapport au coin de l'ecran */
	public int y;
	/** la nouvelle origine est-elle le centre de l'image ? */
	public boolean centre;
	/** zoom horizontal (en pourcents)*/
	public int zoomX;
	/** zoom vertical (en pourcents)*/
	public int zoomY;
	public int opacite;
	public ModeDeFusion modeDeFusion;
	/** angle de rotation de l'image */
	public int angle;
	/** L'image bouge-t-elle d'elle-m�me ? */
	public DeplacerImage deplacementActuel;
	
	/**
	 * Constructeur explicite
	 * @param image nom du fichier image
	 * @param nomImage (pour la sauvegarde uniquement)
	 * @param numero de l'image pour le LecteurMap
	 * @param x coordonn�e x d'affichage a l'ecran (en pixels)
	 * @param y coordonn�e y d'affichage a l'ecran (en pixels)
	 * @param centre l'origine de l'image est-elle son centre ?
	 * @param zoomX zoom horizontal (en pourcents)
	 * @param zoomY zoom vertical (en pourcents)
	 * @param opacite de l'image (sur 255)
	 * @param modeDeFusion de la superposition d'images
	 * @param angle de rotation de l'image (en degr�s)
	 */
	public Picture(final BufferedImage image, final String nomImage, final int numero, final int x, final int y, final boolean centre, 
			final int zoomX, final int zoomY, final int opacite, final ModeDeFusion modeDeFusion, final int angle) {
		this.image = image;
		this.nomImage = nomImage;
		this.numero = numero;
		this.x = x;
		this.y = y;
		this.centre = centre;
		this.zoomX = zoomX;
		this.zoomY = zoomY;
		this.opacite = opacite;
		this.modeDeFusion = modeDeFusion;
		this.angle = angle;
	}
	
	/**
	 * Dessiner les images.
	 * Une image basse est une image sous le HUD ; une image haute est au dessus du HUD.
	 * @param ecran sur lequel on affiche les images
	 * @param imagesBasses true pour les images basses, false pour les images hautes
	 * @return ecran avec les images dessin�es
	 */
	public static BufferedImage dessinerLesImages(BufferedImage ecran, final boolean imagesBasses) {
		final Picture[] imagesADessiner = Main.getPartieActuelle().images;
		
		final int premiere, derniere;
		// On affiche soit les images basses, soit les images hautes
		if (imagesBasses) {
			// images basses (en dessous du HUD)
			premiere = 0;
			derniere = SEPARATEUR_IMAGES_BASSES;
		} else {
			// images hautes (au dessus du HUD)
			premiere = SEPARATEUR_IMAGES_BASSES + 1;
			derniere = imagesADessiner.length - 1;
		}
		
		Picture picture;
		for (int i = premiere; i<=derniere; i++) {
			picture = imagesADessiner[i];
			// Toutes les places de la liste ne sont pas forcement occupees
			if (picture != null) {
				
				// D�placer les images (dont le d�placement a �t� d�l�gu� au LecteurMap par une Commande)
				if (picture.deplacementActuel != null) {
					picture.deplacementActuel.executerCommeUnDeplacementPropre(picture);
				}
				
				// Afficher les images
				ecran = Graphismes.superposerImages(ecran, picture.image, picture.x, picture.y, picture.centre, picture.zoomX, picture.zoomY, picture.opacite, picture.modeDeFusion, picture.angle);
			}
		}
		return ecran;
	}
	
	/**
	 * G�n�rer un JSON de la Picture (et son �ventuel d�placement actuel) pour la Sauvegarde.
	 */
	@Override
	public JSONObject sauvegarderEnJson() {
		final JSONObject jsonImage = new JSONObject();
		jsonImage.put("nomImage", this.nomImage);
		jsonImage.put("numero", this.numero);
		jsonImage.put("x", this.x);
		jsonImage.put("y", this.y);
		jsonImage.put("centre", this.centre);
		jsonImage.put("zoomX", this.zoomX);
		jsonImage.put("zoomY", this.zoomY);
		jsonImage.put("opacite", this.opacite);
		jsonImage.put("modeDeFusion", this.modeDeFusion.nom);
		jsonImage.put("angle", this.angle);
		
		if (this.deplacementActuel != null) {
			final JSONObject jsonDeplacementActuel = this.deplacementActuel.sauvegarderEnJson();
			jsonImage.put("deplacementActuel", jsonDeplacementActuel);
		}
		
		return jsonImage;
	}
	
}
