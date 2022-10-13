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
import main.Main;
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
	private static final int NOMBRE_VIGNETTES_PAR_LIGNE = 5;
	private static final int TAILLE_VIGNETTE = 192;
	public static final Animation[] ANIMATIONS_DU_JEU = chargerLesAnimationsDuJeu();
	
	/** Identifiant de l'animation */
	private final int id;
	/** Image dans laquelle on d�coupe les vignettes de l'Animation */
	private BufferedImage image;
	/** Les diff�rentes �tapes successives de l'animation */
	public final ArrayList<Frame> frames;
	
	/**
	 * La chronologie de l'Animation est d�crite par Frame.
	 * Chaque Frame liste les vignettes et sons a jouer a un moment pr�cis.
	 */
	public class Frame {
		ArrayList<Picture> vignettes;
		ArrayList<Son> sons;
		
		/**
		 * Son a jouer lors de la Frame d'Animation.
		 */
		private class Son {
			public String nom;
			public float volume;
			/** 
			 * Constructeur explicite
			 * @param nom du fichier sonore a jouer
			 * @param volume sonore entre 0.0 et 1.0
			 */
			Son(final String nom, final float volume) {
				this.nom = nom;
				this.volume = volume;
			}
		}
		
		/**
		 * Constructeur explicite
		 * @param image contenant toutes les vignettes utilis�es dans l'Animation
		 * @param jsonFrame objet JSON repr�sentant la Frame
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
						null, //les animations ne sont pas des Pictures sauvegard�e
						-1, //Picture non r�f�renc�e dans le LecteurMap
						xVignette,
						yVignette,
						true, //centr�
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
	 * @param jsonAnimation objet JSON repr�sentant l'Animation
	 */
	public Animation(final JSONObject jsonAnimation) {
		this.id = jsonAnimation.getInt("id");
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
	 * @return tableau des Animations mod�les
	 */
	public static Animation[] chargerLesAnimationsDuJeu() {
		final JSONArray jsonAnimations;
		try {
			jsonAnimations = InterpreteurDeJson.ouvrirJsonAnimations();
		} catch (Exception e) {
			LOG.error("Impossible d'interpr�ter la liste des animations du jeu.", e);
			return null;
		}
		
		final ArrayList<Animation> animations = new ArrayList<Animation>();
		int plusGrandId = 0;
		for (Object animationObject : jsonAnimations) {
			Animation animation = new Animation((JSONObject) animationObject);
			animations.add(animation);
			if (animation.id > plusGrandId) {
				plusGrandId = animation.id;
			}
		}
		final Animation[] tableauDesAnimations = new Animation[plusGrandId+1];
		for (Animation animation : animations) {
			tableauDesAnimations[animation.id] = animation;
		}
		return tableauDesAnimations;
		
	}

	/**
	 * Dessiner les Animations en cours sur l'ecran
	 * @param ecran sur lequel on dessine
	 * @param xCamera position (en pixels) de la cam�ra par rapport au bord haut-gauche de la Map
	 * @param yCamera position (en pixels) de la cam�ra par rapport au bord haut-gauche de la Map
	 * @return ecran avec les Animations dessin�es dessus
	 */
	public static BufferedImage dessinerLesAnimations(BufferedImage ecran, final int xCamera, final int yCamera) {
		final Partie partie = Main.getPartieActuelle();
		JouerAnimation animationEnCours;
		int nombreDAnimationsEnCours = partie.animations.size();
		for (int i = 0; i<nombreDAnimationsEnCours; i++) {
			animationEnCours = partie.animations.get(i);
			
			// D�termination de la cible de l'animation
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
					// La cible est un Event sp�cifi�
					eventCible = animationEnCours.page.event.map.eventsHash.get(animationEnCours.idEvent);
				} else {
					// La cible est cet Event
					eventCible = animationEnCours.page.event;
				}
				xCible = eventCible.x;
				yCible = eventCible.y;
				xCentrage = eventCible.largeurHitbox/2;
				yCentrage = eventCible.hauteurHitbox/2;
			}

			try {
				final Frame frameActuelle = Animation.ANIMATIONS_DU_JEU[animationEnCours.idAnimation].frames.get(animationEnCours.frameActuelle);
				// Afficher les vignettes de cette frame
				for (Picture picture : frameActuelle.vignettes) {
					ecran = Graphismes.superposerImages(
							ecran,
							picture.image,
							picture.x + xCible + xCentrage - xCamera,
							picture.y + yCible + yCentrage - yCamera,
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
				LOG.trace("Frame n�"+animationEnCours.frameActuelle+" de l'animation");
				animationEnCours.frameActuelle++;
			} catch (NullPointerException e0) {
				LOG.error("Impossible de trouver l'animation "+animationEnCours.idAnimation);
				// Animation introuvable, on la retire de la file
				partie.animations.remove(i);
				i--;
				nombreDAnimationsEnCours--;
			} catch (IndexOutOfBoundsException e1) {
				LOG.debug("Fin de l'animation n�"+animationEnCours.frameActuelle);
				// L'animation est termin�e, on la retire de la file
				partie.animations.remove(i);
				i--;
				nombreDAnimationsEnCours--;
			}
		}
		return ecran;
	}
	
}
