package jeu;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.imageio.ImageIO;

import org.json.JSONArray;
import org.json.JSONObject;

import map.Hitbox;
import utilitaire.InterpreteurDeJson;

/**
 * Le Héros peut utiliser un certain nombre d'Armes contre les Events ennemis.
 */
public class Arme {
	//constantes
	private static Arme[] armesDuJeu;
	public static HashMap<String, Arme> armesDuJeuHash = new HashMap<String, Arme>();
	
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
	public Integer[] framesDAnimation;
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
	private Arme(final int id, final String nom, final String nomImageAttaque, final String nomEffetSonoreAttaque, 
			final Integer[] framesDAnimation, final Hitbox hitbox, final int frameDebutCoup, 
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
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public Arme(final HashMap<String, Object> parametres) {
		this( (int) parametres.get("numero"), 
			(String) parametres.get("nom"),
			(String) parametres.get("nomImageAttaque"),
			(String) parametres.get("nomEffetSonoreAttaque"),
			(Integer[]) parametres.get("framesDAnimation"),
			new Hitbox((int) parametres.get("portee"), (int) parametres.get("etendue")),
			(int) parametres.get("frameDebutCoup"),
			(int) parametres.get("frameFinCoup"),
			(String) parametres.get("nomIcone")
		);
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
	 * Charger les Armes du jeu via JSON.
	 * @return nombre de Armes dans le jeu
	 */
	public static int chargerLesArmesDuJeu() {
		try {
			final JSONArray jsonArmes = InterpreteurDeJson.ouvrirJsonArmes();
			final ArrayList<Arme> armes = new ArrayList<Arme>();
			int i = 0;
			for (Object objectArme : jsonArmes) {
				final JSONObject jsonArme = (JSONObject) objectArme;
				
				final HashMap<String, Object> parametres = new HashMap<String, Object>();
				parametres.put("numero", i);
				
				final Iterator<String> jsonParametres = jsonArme.keys();
				while (jsonParametres.hasNext()) {
					final String parametre = jsonParametres.next();
					
					if ("framesDAnimation".equals(parametre)) {
						//paramètre : framesDAnimation
						final JSONArray jsonArrayframesDAnimation = jsonArme.getJSONArray("framesDAnimation");
						final ArrayList<Integer> framesDAnimationListe = new ArrayList<Integer>();
						for (Object frameObject : jsonArrayframesDAnimation) {
							framesDAnimationListe.add((Integer) frameObject);
						}
						
						final Integer[] framesDAnimation = new Integer[framesDAnimationListe.size()];
						framesDAnimationListe.toArray(framesDAnimation);
						parametres.put("framesDAnimation", framesDAnimation);
					} else {
						//autres paramètres
						parametres.put(parametre, jsonArme.get(parametre));
					}
				}
				
				final Arme arme = new Arme(parametres);
				armes.add(arme);
				armesDuJeuHash.put(arme.nom, arme);
				i++;
			}
			
			armesDuJeu = new Arme[armes.size()];
			armes.toArray(armesDuJeu);
			return armesDuJeu.length;
			
		} catch (FileNotFoundException e) {
			//problème lors de l'ouverture du fichier JSON
			System.err.println("Impossible de charger les quêtes du jeu.");
			e.printStackTrace();
			armesDuJeu = null;
			return 0;
		}
	}
	
}
