package map;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import commandes.JouerAnimation;
import jeu.Partie;
import main.Fenetre;
import son.LecteurAudio;
import utilitaire.graphismes.Graphismes;
import utilitaire.graphismes.ModeDeFusion;

public class Animation {
	//constantes
	protected static final Logger LOG = LogManager.getLogger(Animation.class);
	private static final int NOMBRE_VIGNETTES_PAR_LIGNE = 4;
	private static final int TAILLE_VIGNETTE = 400;
	public static final Animation[] animationsDuJeu = chargerLesAnimationsDuJeu();
	
	/** Image dans laquelle on découpe les vignettes de l'Animation */
	private BufferedImage image;
	/** Les différentes étapes successives de l'animation */
	public final ArrayList<Frame> frames;
	
	public class Frame {
		ArrayList<Picture> vignettes;
		ArrayList<String> sons;
		
		public Frame(final BufferedImage image, final JSONObject jsonFrame) {
			this.vignettes = new ArrayList<Picture>();
			JSONArray jsonVignettes = jsonFrame.getJSONArray("vignettes");
			for (Object o : jsonVignettes) {
				JSONObject jsonVignette = (JSONObject) o;
	
				// La vignette est une portion de l'image d'animation
				int numeroVignette = jsonVignette.getInt("numeroVignette");
				int xRecadrage = (numeroVignette % NOMBRE_VIGNETTES_PAR_LIGNE) * TAILLE_VIGNETTE;
				int yRecadrage = (numeroVignette / NOMBRE_VIGNETTES_PAR_LIGNE) * TAILLE_VIGNETTE;
				BufferedImage imageVignette = image.getSubimage(xRecadrage, yRecadrage, TAILLE_VIGNETTE, TAILLE_VIGNETTE);

				//position x de la vignette par rapport au centre de l'animation
				int xVignette = jsonVignette.getInt("xVignette");
				//position y de la vignette par rapport au centre de l'animation
				int yVignette = jsonVignette.getInt("yVignette");
				
				Picture vignette = new Picture(imageVignette, 
						-1, //Picture non référencée dans le LecteurMap
						xVignette,
						yVignette,
						true, //centré
						jsonVignette.has("zoomX") ? jsonVignette.getInt("zoomX") : Graphismes.PAS_D_HOMOTHETIE,
						jsonVignette.has("zoomY") ? (int) jsonVignette.getInt("zoomY") : Graphismes.PAS_D_HOMOTHETIE, 
						jsonVignette.has("opacite") ? (int) jsonVignette.getInt("opacite") : Graphismes.OPACITE_MAXIMALE, 
						ModeDeFusion.parNom(jsonVignette.getString("modeDeFusion")), 
						jsonVignette.has("angle") ? (int) jsonVignette.getInt("angle") : Graphismes.PAS_DE_ROTATION);
				
				this.vignettes.add(vignette);
			}
			
			this.sons = new ArrayList<String>();
			JSONArray jsonSons = jsonFrame.getJSONArray("sons");
			for (Object o : jsonSons) {
				this.sons.add( (String) o );
			}
		}
	}
	
	public Animation(JSONObject jsonAnimation) {
		String nomImage = jsonAnimation.getString("nomImage");
		try {
			this.image = Graphismes.ouvrirImage("Animations", nomImage);
		} catch (IOException e) {
			LOG.error(e);
		}
		
		this.frames = new ArrayList<Frame>();
		JSONArray jsonFrames = jsonAnimation.getJSONArray("frames");
		for (Object o : jsonFrames) {
			this.frames.add( new Frame(this.image, (JSONObject) o) );
		}
	}
	
	public static Animation[] chargerLesAnimationsDuJeu() {
		//TODO
		return null;
	}

	/**
	 * Dessiner les Animations en cours sur l'écran
	 * @param ecran sur lequel on dessine
	 * @return écran avec les Animations dessinées
	 */
	public static BufferedImage dessinerLesAnimations(BufferedImage ecran) {
		Partie partie = Fenetre.getPartieActuelle();
		Frame frameActuelle;
		for (JouerAnimation animationEnCours : partie.animations) {
			frameActuelle = Animation.animationsDuJeu[animationEnCours.numeroAnimation].frames.get(animationEnCours.frameActuelle);
			// Afficher les vignettes de cette frame
			for (Picture picture : frameActuelle.vignettes){
				ecran = Graphismes.superposerImages(
						ecran,
						picture.image,
						picture.x + animationEnCours.xEcran, //x relatif au centre de la vignette et x à l'écran
						picture.y + animationEnCours.yEcran, //y relatif au centre de la vignette et y à l'écran
						picture.centre,
						picture.zoomX,
						picture.zoomY,
						picture.opacite,
						picture.modeDeFusion,
						picture.angle
				);
			}
			// Jouer les sons de cette frame
			for (String nomSon : frameActuelle.sons) {
				LecteurAudio.playSe(nomSon);
			}
			// La prochaine fois on jouera la frame suivante
			animationEnCours.frameActuelle++;
		}
		return ecran;
	}
	
}
