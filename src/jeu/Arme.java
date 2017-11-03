package jeu;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import commandes.EquiperArme;
import commandes.ModifierTexte;
import conditions.Condition;
import conditions.ConditionArmePossedee;
import main.Commande;
import main.Fenetre;
import map.Hitbox;
import menu.Listable;
import menu.Texte;
import utilitaire.InterpreteurDeJson;
import utilitaire.graphismes.Graphismes;
import utilitaire.graphismes.ModeDeFusion;

/**
 * Le Héros peut utiliser un certain nombre d'Armes contre les Events ennemis.
 */
public class Arme implements Listable {
	//constantes
	private static final Logger LOG = LogManager.getLogger(Arme.class);
	public static final Arme[] ARMES_DU_JEU = chargerLesArmesDuJeu();
	
	/**
	 * Chaque Arme possède un id propre. 
	 * 0 pour l'épée, 1 pour la torche etc.
	 */
	public final int id;
	public ArrayList<String> nom;
	private final ArrayList<String> description;
	public String nomEffetSonoreAttaque;
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
	 * @param nom de l'Arme (dans plusieurs langues)
	 * @param description à afficher dans les Menus (dans plusieurs langues)
	 * @param nomEffetSonoreAttaque nom du fichier sonore joué lors de l'utilisation
	 * @param framesDAnimation séquence des vignettes à afficher lors de l'animation d'attaque
	 * @param hitbox zone d'attaque qu'on peut atteindre
	 * @param frameDebutCoup frame de l'animation d'attaque où le coup commence réellement
	 * @param frameFinCoup frame de l'animation d'attaque où le coup est terminé
	 * @param nomIcone nom du fichier image de l'icone de l'Arme
	 */
	private Arme(final int id, final ArrayList<String> nom, final ArrayList<String> description, final String nomEffetSonoreAttaque, 
			final Integer[] framesDAnimation, final Hitbox hitbox, final int frameDebutCoup, 
			final int frameFinCoup, final String nomIcone) {
		this.id = id;
		this.nom = nom;
		this.description = description;
		this.nomEffetSonoreAttaque = nomEffetSonoreAttaque;
		this.framesDAnimation = framesDAnimation;
		this.hitbox = hitbox;
		this.frameDebutCoup = frameDebutCoup;
		this.frameFinCoup = frameFinCoup;
		try {
			this.icone = Graphismes.ouvrirImage("Icons", nomIcone);
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
			Texte.construireTexteMultilingue(parametres.get("nom")),
			Texte.construireTexteMultilingue(parametres.get("description")),
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
			return ARMES_DU_JEU[idArme];
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Charger les Armes du jeu via JSON.
	 * @return nombre de Armes dans le jeu
	 */
	public static Arme[] chargerLesArmesDuJeu() {
		final JSONArray jsonArmes;
		try {
			jsonArmes = InterpreteurDeJson.ouvrirJsonArmes();
		} catch (Exception e) {
			//problème lors de l'ouverture du fichier JSON
			LOG.error("Impossible de charger les armes du jeu.", e);
			return null;
		}
		
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
			i++;
		}
		
		final Arme[] armesDuJeu = new Arme[armes.size()];
		armes.toArray(armesDuJeu);
		return armesDuJeu;
	}

	/**
	 * Enumerer les Armes du jeu.
	 * @param possedes filtrer ou non sur les Armes possédées
	 * @return association entre numero et Arme
	 */
	public static final Map<Integer, Listable> obtenirTousLesListables(final Boolean possedes) {
		final Map<Integer, Listable> listablesPossedes = new HashMap<Integer, Listable>();
		if (possedes) {
			// seulement les Armes possédées
			final boolean[] armesPossedees = Fenetre.getPartieActuelle().armesPossedees;
			for (int i = 0; i < armesPossedees.length; i++) {
				if (armesPossedees[i]) {
					listablesPossedes.put((Integer) i, ARMES_DU_JEU[i]);
				}
			}
		} else {
			// toutes les Armes
			for (Arme arme : ARMES_DU_JEU) {
				listablesPossedes.put((Integer) arme.id, arme);
			}
		}
		return listablesPossedes;
	}

	@Override
	public final BufferedImage construireImagePourListe(final int largeurMinimale, final int hauteurMinimale) {
		final Texte texte = new Texte(this.nom);
		final BufferedImage imageTexte = texte.texteToImage();
		
		int largeur = imageTexte.getWidth() + Texte.MARGE_A_DROITE + this.icone.getWidth();
		if (largeur < largeurMinimale) {
			largeur = largeurMinimale;
		}
		int hauteur = Math.max(imageTexte.getHeight(), this.icone.getHeight());
		if (hauteur < hauteurMinimale) {
			hauteur = hauteurMinimale;
		}
		
		BufferedImage image = new BufferedImage(largeur, hauteur, Graphismes.TYPE_DES_IMAGES);
		image = Graphismes.superposerImages(image, this.icone, 0, 0, false, 
				Graphismes.PAS_D_HOMOTHETIE, Graphismes.PAS_D_HOMOTHETIE, Graphismes.OPACITE_MAXIMALE, 
				ModeDeFusion.NORMAL, Graphismes.PAS_DE_ROTATION);
		image = Graphismes.superposerImages(image, imageTexte, this.icone.getWidth()+Texte.MARGE_A_DROITE, 0, 
				false, Graphismes.PAS_D_HOMOTHETIE, Graphismes.PAS_D_HOMOTHETIE, Graphismes.OPACITE_MAXIMALE, 
				ModeDeFusion.NORMAL, Graphismes.PAS_DE_ROTATION);
		return image;
	}
	
	@Override
	public final ArrayList<Condition> getConditions() {
		final ArrayList<Condition> conditions = new ArrayList<Condition>();
		conditions.add(new ConditionArmePossedee(1, this.id));
		return conditions;
	}
	
	@Override
	public final ArrayList<Commande> getComportementSelection() {
		final ArrayList<Commande> comportementSelection = new ArrayList<Commande>();
		comportementSelection.add(new ModifierTexte(this.description));
		return comportementSelection;
	}

	@Override
	public final ArrayList<Commande> getComportementConfirmation() {
		final ArrayList<Commande> comportementConfirmation = new ArrayList<Commande>();
		comportementConfirmation.add(new EquiperArme(this.id));
		return comportementConfirmation;
	}
	
}
