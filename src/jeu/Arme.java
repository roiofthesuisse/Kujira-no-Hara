package jeu;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import main.Fenetre;
import map.Hitbox;

/**
 * Le Héros peut utiliser un certain nombre d'Armes contre les Events ennemis.
 */
public class Arme {
	//constantes
	public static final int ID_EPEE = 0;
	public static final Hitbox ZONE_ATTAQUE_EPEE = new Hitbox(24, 48);
	public static final int FRAME_DEBUT_COUP_EPEE = 1;
	public static final int FRAME_FIN_COUP_EPEE = 5;
	public static final int[] FRAMES_ANIMATION_EPEE = {3, 2, 2, 1, 1, 0, 0};
	
	public static final int ID_EVENTAIL = 1;
	public static final Hitbox ZONE_ATTAQUE_EVENTAIL = new Hitbox(5*Fenetre.TAILLE_D_UN_CARREAU, 16);
	public static final int FRAME_DEBUT_COUP_EVENTAIL = 3;
	public static final int FRAME_FIN_COUP_EVENTAIL = 7;
	public static final int[] FRAMES_ANIMATION_EVENTAIL = {0, 0, 0, 1, 1, 1, 0, 0};
	
	/**
	 * Chaque arme possède un id propre. 
	 * 0 pour l'épée, 1 pour la torche etc.
	 */
	public final int id;
	public final String nom;
	public final String nomImageAttaque;
	public final String nomEffetSonoreAttaque;
	public BufferedImage icone;
	/**
	 * L'animation d'attaque est composée de plusieurs images.
	 * Pour faire rester une image plus longtemps à l'écran, l'ajouter plusieurs fois à la liste.
	 * La dernière image de la liste est affichée en premier, car l'affichage est décrémentaire.
	 */
	public int[] framesDAnimation;
	private static Arme[] armesDuJeu = new Arme[10];
	public Hitbox hitbox;
	/**
	 * A partir de cette frame d'animation, l'attaque commence à avoir un effet.
	 * L'intéraction devient possible avec un ennemi.
	 */
	public int frameDebutCoup;
	/**
	 * A partir de cette frame d'animation, l'attaque arrête d'avoir un effet.
	 * L'intéraction n'est plus possible avec l'ennemi.
	 */
	public int frameFinCoup;
	
	/**
	 * @param id chaque Arme a un identifiant
	 * @param nom chaque Arme a un nom
	 * @param nomImageAttaque nom de l'image du Héros utilisant cette l'Arme
	 * @param nomEffetSonoreAttaque nom du fichier sonore joué lors de l'utilisation
	 * @param framesDAnimation séquence des vignettes à afficher lors de l'animation d'attaque
	 * @param hitbox zone d'attaque qu'on peut atteindre
	 * @param frameDebutCoup frame de l'animation d'attaque où le coup commence réellement
	 * @param frameFinCoup frame de l'animation d'attaque où le coup est terminé
	 * @param nomIcone nom du fichier image de l'icone de l'Arme
	 */
	public Arme(final int id, final String nom, final String nomImageAttaque, final String nomEffetSonoreAttaque, 
			final int[] framesDAnimation, final Hitbox hitbox, final int frameDebutCoup, 
			final int frameFinCoup, final String nomIcone) {
		this.id = id;
		this.nom = nom;
		this.nomImageAttaque = nomImageAttaque;
		this.nomEffetSonoreAttaque = nomEffetSonoreAttaque;
		this.framesDAnimation = framesDAnimation;
		this.hitbox = hitbox;
		this.frameDebutCoup = frameDebutCoup;
		this.frameFinCoup = frameFinCoup;
		try {
			this.icone = ImageIO.read(new File(".\\ressources\\Graphics\\Icons\\"+nomIcone));
		} catch (IOException e) {
			//erreur lors du chargement de l'icone
			e.printStackTrace();
		}
		
		//on ajoute l'arme à la liste des armes
		armesDuJeu[id] = this;
	}
	
	/**
	 * @param idArme identifiant de l'arme souhaitée
	 * @return arme dont l'identifiant est idArme
	 */
	public static Arme getArme(final int idArme) {
		try {
			return armesDuJeu[idArme];
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * initialiser la liste des armes du jeu
	 */
	public static void initialiserLesArmesDuJeu() {
		//TODO importer les armes du jeu à partir d'un fichier texte plutôt qu'avec des constantes
		
		//épée
		Arme.armesDuJeu[ID_EPEE] = new Arme(ID_EPEE, "epee", "Jiyounasu AttaqueEpee character.png", "Epee.wav", FRAMES_ANIMATION_EPEE, ZONE_ATTAQUE_EPEE, FRAME_DEBUT_COUP_EPEE, FRAME_FIN_COUP_EPEE, "epee2 icon.png");
			
		//eventail
		Arme.armesDuJeu[ID_EVENTAIL] = new Arme(ID_EVENTAIL, "eventail", "Jiyounasu Eventail character.png", "Eventail.mp3", FRAMES_ANIMATION_EVENTAIL, ZONE_ATTAQUE_EVENTAIL, FRAME_DEBUT_COUP_EVENTAIL, FRAME_FIN_COUP_EVENTAIL, "eventail icon.png");
			
		//autre
			//TODO autres armes
	}
	
}
