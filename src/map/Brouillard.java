package map;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import commandes.Sauvegarder.Sauvegardable;
import utilitaire.graphismes.Graphismes;
import utilitaire.graphismes.ModeDeFusion;

/**
 * Le Brouillard est une image ajoutée en transparence par dessus la Map et ses Events.
 * Son intérêt est d'enrichir l'ambiance colorimétrique du décor.
 */
public final class Brouillard implements Sauvegardable {
	private static final Logger LOG = LogManager.getLogger(Brouillard.class);
	
	public final String nomImage;
	public BufferedImage image;
	public int largeur;
	public int hauteur;
	public int opacite;

	public final ModeDeFusion mode;
	
	public final int defilementX;
	public final int defilementY;
	public final double zoom;
	
	/**
	 * Constructeur explicite
	 * @param nomImage nom de l'image située dans le dossier "Graphics/Fogs/"
	 * @param opacite transparence de l'image
	 * @param mode mode de superposition de l'image avec la Map
	 * @param defilementX vitesse de déplacement du Brouillard suivant l'axe x
	 * @param defilementY vitesse de déplacement du Brouillard suivant l'axe y
	 * @param zoom taux d'aggrandissement de l'image (en pourcents)
	 * @throws IOException l'image n'a pas pu être chargée
	 */
	public Brouillard(final String nomImage, final int opacite, final ModeDeFusion mode, final int defilementX, 
			final int defilementY, final int zoom) {
		this.zoom = zoom;
		this.nomImage = nomImage;
		try {
			final double ratioZoom = (double) zoom / (double) Graphismes.PAS_D_HOMOTHETIE;
			this.image = redimensionnerImage(Graphismes.ouvrirImage("Fogs", this.nomImage), ratioZoom);
			this.largeur = this.image.getWidth();
			this.hauteur = this.image.getHeight();
		} catch (IOException e) {
			LOG.error("Impossible d'ouvrir l'image de brouillard "+nomImage, e);
			this.image = null;
		}
		this.opacite = opacite;
		this.mode = mode;
		this.defilementX = defilementX;
		this.defilementY = defilementY;
	}
	
	/**
	 * Redimensionne une image selon un ratio.
	 * @param image à redimensionner
	 * @param ratio d'aggrandissement
	 * @return image redimensionnée
	 */
	private static BufferedImage redimensionnerImage(final BufferedImage image, final double ratio) {
		if (ratio == 1) {
			//pas de redimensionnement à faire
			return image;
		}
		
	    final int ancienneLargeur  = image.getWidth();
	    final int ancienneHauteur = image.getHeight();
	    final AffineTransform scaleTransform = AffineTransform.getScaleInstance(ratio, ratio);
	    final AffineTransformOp bilinearScaleOp = new AffineTransformOp(scaleTransform, AffineTransformOp.TYPE_BILINEAR);
	    final int nouvelleLargeur = (int) Math.round(ratio*ancienneLargeur);
	    final int nouvelleHauteur = (int) Math.round(ratio*ancienneHauteur);
	    return bilinearScaleOp.filter(image, new BufferedImage(nouvelleLargeur, nouvelleHauteur, image.getType()));
	}
	
	/**
	 * Extrait le Brouillard de l'objet JSON de la Map.
	 * @param jsonMap objet JSON représentant la Map
	 * @return Brouillard de la Map
	 */
	public static Brouillard creerBrouillardAPartirDeJson(final JSONObject jsonMap) {
		try {
			final JSONObject brouillardJson = jsonMap.getJSONObject("brouillard");
			final String nomImage = brouillardJson.getString("nom");
			final int opacite = brouillardJson.getInt("opacite");
			int defilementX = 0;
			try {
				defilementX = brouillardJson.getInt("defilementX");
			} catch (JSONException e) {
				//pas de défilement x
			}
			int defilementY = 0;
			try {
				defilementY = brouillardJson.getInt("defilementY");
			} catch (JSONException e) {
				//pas de défilement y
			}
			int zoom = Graphismes.PAS_D_HOMOTHETIE;
			try {
				zoom = brouillardJson.getInt("zoom");
			} catch (JSONException e) {
				//pas de zoom
			}
			final String nomModeDeFusion = brouillardJson.has("modeDeFusion") ? brouillardJson.getString("modeDeFusion") : null;

			return new Brouillard(nomImage, opacite, ModeDeFusion.parNom(nomModeDeFusion), defilementX, defilementY, zoom);
		} catch (JSONException e) {
			//pas de brouillard
			LOG.info("Pas de Brouillard pour cette Map.");
			return null;
		}
	}
	
	/**
	 * Dessiner le Brouillard au dessus de la Map et ses Events.
	 * @param ecran sur lequel on dessine
	 * @param xCamera position x de la caméra
	 * @param yCamera position y de la caméra
	 * @param frame d'animation du Brouillard
	 * @return écran sur lequel on a dessiné le Brouillard
	 */
	public BufferedImage dessinerLeBrouillard(BufferedImage ecran, final int xCamera, final int yCamera, final int frame) {
		if (this.image == null || this.opacite <= 0) {
			//pas de Brouillard
			return ecran;
		}
		
		final int largeurEcran = ecran.getWidth();
		final int hauteurEcran = ecran.getWidth();
		final int decalageX = this.defilementX * (frame % this.largeur);
		final int decalageY = this.defilementY * (frame % this.hauteur); 
		int imin = (xCamera - decalageX) / this.largeur;
		int imax = (xCamera + largeurEcran - decalageX) / this.largeur;
		int jmin = (yCamera - decalageY) / this.hauteur;
		int jmax = (yCamera + hauteurEcran - decalageY) / this.hauteur;
		/*if (Brouillard.calculerAffichage(imin, this.largeur, decalageX, xCamera) >= 0) {
			imin--;
		}
		if (Brouillard.calculerAffichage(imax, this.largeur, decalageX, xCamera) <= largeurEcran) {
			imax++;
		}
		if (Brouillard.calculerAffichage(jmin, this.hauteur, decalageY, yCamera) >= 0) {
			jmin--;
		}
		if (Brouillard.calculerAffichage(jmax, this.largeur, decalageY, yCamera) <= hauteurEcran) {
			jmax++;
		}*/
		for (int i = imin; i<=imax; i++) {
			for (int j = jmin; j<=jmax; j++) {
				ecran = Graphismes.superposerImages(
					ecran, 
					this.image, 
					Brouillard.calculerAffichage(i, this.largeur, decalageX, xCamera), 
					Brouillard.calculerAffichage(j, this.hauteur, decalageY, yCamera),
					false,
					Graphismes.PAS_D_HOMOTHETIE, //le zoom a déjà été pris en compte
					Graphismes.PAS_D_HOMOTHETIE, //le zoom a déjà été pris en compte
					this.opacite, 
					this.mode,
					Graphismes.PAS_DE_ROTATION
				);	
			}
		}
		return ecran;
	}
	
	/**
	 * Calcule la position où dessiner l'image du Brouillard.
	 * @param numeroVignette l'écran est recouvert plusieurs fois avec l'image du Brouillard si elle est petite
	 * @param tailleBrouillard taille de l'image du Brouillard
	 * @param decalageTemporel l'image du Brouillard se déplace à l'écran
	 * @param positionCamera position de la caméra par rapport au coin haut-gauche de la Map
	 * @return position de la vignette par rapport au coin haut-gauche de l'écran
	 */
	public static int calculerAffichage(final int numeroVignette, final int tailleBrouillard, final int decalageTemporel, final int positionCamera) {
		return numeroVignette*tailleBrouillard + decalageTemporel - positionCamera;
	}

	@Override
	public JSONObject sauvegarderEnJson() {
		final JSONObject jsonBrouillard = new JSONObject();
		jsonBrouillard.put("nomImage", nomImage);
		jsonBrouillard.put("opacite", opacite);
		jsonBrouillard.put("mode", mode.nom);
		jsonBrouillard.put("defilementX", defilementX);
		jsonBrouillard.put("defilementY", defilementY);
		jsonBrouillard.put("zoom", zoom);
		return jsonBrouillard;
	}

}
