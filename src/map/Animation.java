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
import utilitaire.InterpreteurDeJson;
import utilitaire.graphismes.Graphismes;
import utilitaire.graphismes.ModeDeFusion;
import utilitaire.son.LecteurAudio;
import utilitaire.son.Musique;

/**
 * Jouer une Animation sur la Map.
 * Une Animation est une succession de vignettes et de sons.
 */
public class Animation {
	//constantes
	protected static final Logger LOG = LogManager.getLogger(Animation.class);
	private static final int NOMBRE_VIGNETTES_PAR_LIGNE = 4;
	private static final int TAILLE_VIGNETTE = 192;
	public static final Animation[] ANIMATIONS_DU_JEU = chargerLesAnimationsDuJeu();
	
	/** Image dans laquelle on découpe les vignettes de l'Animation */
	private BufferedImage image;
	/** Les différentes étapes successives de l'animation */
	public final ArrayList<Frame> frames;
	
	/**
	 * La chronologie de l'Animation est décrite par Frame.
	 * Chaque Frame liste les vignettes et sons à jouer à un moment précis.
	 */
	public class Frame {
		ArrayList<Picture> vignettes;
		ArrayList<Son> sons;
		
		/**
		 * Son à jouer lors de la Frame d'Animation.
		 */
		private class Son {
			public String nom;
			public float volume;
			/** 
			 * Constructeur explicite
			 * @param nom du fichier sonore à jouer
			 * @param volume sonore entre 0.0 et 1.0
			 */
			Son(final String nom, final float volume) {
				this.nom = nom;
				this.volume = volume;
			}
		}
		
		/**
		 * Constructeur explicite
		 * @param image contenant toutes les vignettes utilisées dans l'Animation
		 * @param jsonFrame objet JSON représentant la Frame
		 */
		public Frame(final BufferedImage image, final JSONObject jsonFrame) {
			//vignettes
			this.vignettes = new ArrayList<Picture>();
			final JSONArray jsonVignettes = jsonFrame.getJSONArray("vignettes");
			for (Object o : jsonVignettes) {
				final JSONObject jsonVignette = (JSONObject) o;
	
				// La vignette est une portion de l'image d'animation
				final int numeroVignette = jsonVignette.getInt("numeroVignette");
				final int xRecadrage = (numeroVignette % NOMBRE_VIGNETTES_PAR_LIGNE) * TAILLE_VIGNETTE;
				final int yRecadrage = (numeroVignette / NOMBRE_VIGNETTES_PAR_LIGNE) * TAILLE_VIGNETTE;
				final BufferedImage imageVignette = image.getSubimage(xRecadrage, yRecadrage, TAILLE_VIGNETTE, TAILLE_VIGNETTE);

				//position x de la vignette par rapport au centre de l'animation
				final int xVignette = jsonVignette.getInt("xVignette");
				//position y de la vignette par rapport au centre de l'animation
				final int yVignette = jsonVignette.getInt("yVignette");
				
				final Picture vignette = new Picture(imageVignette,
						null, //les animations ne sont pas des Pictures sauvegardée
						-1, //Picture non référencée dans le LecteurMap
						xVignette,
						yVignette,
						true, //centré
						jsonVignette.has("zoomX") ? jsonVignette.getInt("zoomX") : Graphismes.PAS_D_HOMOTHETIE,
						jsonVignette.has("zoomY") ? (int) jsonVignette.getInt("zoomY") : Graphismes.PAS_D_HOMOTHETIE, 
						jsonVignette.has("opacite") ? (int) jsonVignette.getInt("opacite") : Graphismes.OPACITE_MAXIMALE, 
						ModeDeFusion.parNom(jsonVignette.has("modeDeFusion") ? jsonVignette.getString("modeDeFusion") : ModeDeFusion.NORMAL), 
						jsonVignette.has("angle") ? (int) jsonVignette.getInt("angle") : Graphismes.PAS_DE_ROTATION);
				
				this.vignettes.add(vignette);
			}
			
			//sons
			if (jsonFrame.has("sons")) {
				this.sons = new ArrayList<Son>();
				final JSONArray jsonSons = jsonFrame.getJSONArray("sons");
				for (Object o : jsonSons) {
					final JSONObject jsonSon = (JSONObject) o;
					final String nomSon = jsonSon.getString("nom");
					final float volumeSon = jsonSon.has("volume") ? (float) jsonSon.getDouble("volume") : Musique.VOLUME_MAXIMAL;
					this.sons.add( new Son(nomSon, volumeSon) );
				}
			} else {
				this.sons = null;
			}
		}
	}
	
	/**
	 * Constructeur explicite
	 * @param jsonAnimation objet JSON représentant l'Animation
	 */
	public Animation(final JSONObject jsonAnimation) {
		final String nomImage = jsonAnimation.getString("nomImage");
		try {
			this.image = Graphismes.ouvrirImage("Animations", nomImage);
		} catch (IOException e) {
			LOG.error(e);
		}
		
		this.frames = new ArrayList<Frame>();
		final JSONArray jsonFrames = jsonAnimation.getJSONArray("frames");
		for (Object o : jsonFrames) {
			this.frames.add( new Frame(this.image, (JSONObject) o) );
		}
	}
	
	/**
	 * Charger toutes les Animations du jeu.
	 * @return tableau des Animations modèles
	 */
	public static Animation[] chargerLesAnimationsDuJeu() {
		final JSONArray jsonAnimations;
		try {
			jsonAnimations = InterpreteurDeJson.ouvrirJsonAnimations();
		} catch (Exception e) {
			LOG.error("Impossible d'interpréter la liste des animations du jeu.", e);
			return null;
		}
		
		final ArrayList<Animation> animations = new ArrayList<Animation>();
		for (Object animationObject : jsonAnimations) {
			animations.add(new Animation((JSONObject) animationObject));
		}
		final Animation[] tableauDesAnimations = new Animation[animations.size()];
		return animations.toArray(tableauDesAnimations);
		
	}

	/**
	 * Dessiner les Animations en cours sur l'écran
	 * @param ecran sur lequel on dessine
	 * @return écran avec les Animations dessinées dessus
	 */
	public static BufferedImage dessinerLesAnimations(BufferedImage ecran) {
		final Partie partie = Fenetre.getPartieActuelle();
		JouerAnimation animationEnCours;
		int nombreDAnimationsEnCours = partie.animations.size();
		for (int i = 0; i<nombreDAnimationsEnCours; i++) {
			animationEnCours = partie.animations.get(i);
			
			// Détermination de la cible de l'animation
			final int xCible, yCible, xCentrage, yCentrage;
			if (animationEnCours.xEcran >= 0 && animationEnCours.yEcran >= 0) {
				// La cible est un point fixe de la Map
				xCible = animationEnCours.xEcran;
				yCible = animationEnCours.yEcran;
				xCentrage = 0;
				yCentrage = 0;
			} else {
				// La cible est un Event
				final Event eventCible;
				if (animationEnCours.idEvent != null) {
					// La cible est un Event spécifié
					eventCible = animationEnCours.page.event.map.eventsHash.get(animationEnCours.idEvent);
				} else {
					// La cible est cet Event
					eventCible = animationEnCours.page.event;
				}
				xCible = eventCible.x;
				yCible = eventCible.y;
				xCentrage = eventCible.largeurHitbox/2;
				yCentrage = eventCible.hauteurHitbox;
			}

			try {
				final Frame frameActuelle = Animation.ANIMATIONS_DU_JEU[animationEnCours.idAnimation].frames.get(animationEnCours.frameActuelle);
				// Afficher les vignettes de cette frame
				for (Picture picture : frameActuelle.vignettes) {
					ecran = Graphismes.superposerImages(
							ecran,
							picture.image,
							picture.x + xCible + xCentrage,
							picture.y + yCible + yCentrage,
							picture.centre,
							picture.zoomX,
							picture.zoomY,
							picture.opacite,
							picture.modeDeFusion,
							picture.angle
					);
				}
				// Jouer les sons de cette frame
				if (frameActuelle.sons != null) {
					for (Frame.Son son : frameActuelle.sons) {
						LecteurAudio.playSe(son.nom, son.volume);
					}
				}
				// La prochaine fois, on jouera la frame suivante
				LOG.trace("Frame n°"+animationEnCours.frameActuelle+" de l'animation");
				animationEnCours.frameActuelle++;
			} catch (IndexOutOfBoundsException e) {
				LOG.debug("Fin de l'animation n°"+animationEnCours.frameActuelle);
				// L'animation est terminée, on la retire de la file
				partie.animations.remove(i);
				i--;
				nombreDAnimationsEnCours--;
			}
		}
		return ecran;
	}
	
}
