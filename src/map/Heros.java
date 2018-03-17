package map;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import main.Main;
import utilitaire.InterpreteurDeJson;

/**
 * Event particulier qui est déplacé par le joueur à l'aide du clavier
 */
public class Heros extends Event {
	private static final Logger LOG = LogManager.getLogger(Heros.class);
	
	public static final Event MODELE = creerModele();
	
	/**
	 * L'animation d'attaque vaut 0 si le héros n'attaque pas.
	 * Au début d'une attaque, elle est mise au maximum (longueur de l'animation de l'attaque).
	 * A chaque frame, elle est affichée puis décrémentée.
	 */
	public int animationAttaque = 0;

	/**
	 * Constructeur explicite
	 * @param x position x (en pixels) du Héros sur la Map
	 * @param y position y (en pixels) du Héros sur la Map
	 * @param directionEnDebutDeMap directiondu Héros au début de la Map
	 * @param map courante du Héros
	 * @throws FileNotFoundException 
	 */
	public Heros(final int x, final int y, final int directionEnDebutDeMap, final Map map) throws FileNotFoundException {
		super(x, y, MODELE.offsetY, MODELE.nom, MODELE.id, MODELE.vies, MODELE.reinitialiser, MODELE.pages, MODELE.largeurHitbox, MODELE.hauteurHitbox, map);
		this.direction = directionEnDebutDeMap;
	}
	
	/**
	 * Le Héros est créé à partir d'un modèle.
	 * Ce modèle est un Event générique.
	 * @return Event modèle qui sert à la création du Héros
	 */
	private static Event creerModele() {
		JSONObject jsonEventGenerique = null;
		try {
			jsonEventGenerique = InterpreteurDeJson.ouvrirJsonEventGenerique("Heros");
		} catch (Exception e) {
			LOG.error("Impossible d'ouvrir le fichier JSON du héros !", e);
		}
		
		final int largeur = jsonEventGenerique.has("largeur") ? (int) jsonEventGenerique.getInt("largeur") : Event.LARGEUR_HITBOX_PAR_DEFAUT;
		final int hauteur = jsonEventGenerique.has("hauteur") ? (int) jsonEventGenerique.getInt("hauteur") : Event.LARGEUR_HITBOX_PAR_DEFAUT;
		final int offsetY = jsonEventGenerique.has("offsetY") ? jsonEventGenerique.getInt("offsetY") : 0;
		final boolean reinitialiserLesInterrupteursLocaux = jsonEventGenerique.has("reinitialiser") ? jsonEventGenerique.getBoolean("reinitialiser") : false; 
		final JSONArray jsonPages = jsonEventGenerique.getJSONArray("pages");
		final ArrayList<PageEvent> pages = creerListeDesPagesViaJson(jsonPages, 0, null);
		
		final Event modele = new Event(0, 0, offsetY, "heros", 0, 1, reinitialiserLesInterrupteursLocaux, pages, largeur, hauteur, null);
		return modele;
	}
	
	@Override
	public final void deplacer() {
		if (animationAttaque > 0) {
			//pas de déplacement si animation d'attaque
			this.animation = Main.getPartieActuelle().getArmeEquipee().framesDAnimation[animationAttaque-1];
			
			animationAttaque--;
		} else if (!this.map.lecteur.laTransitionEstTerminee()) {
			//pas de déplacement du Héros si la Transition n'est pas terminée
			
		} else if (this.deplacementForce!=null && this.deplacementForce.mouvements.size()>0) {
			//il y a un déplacement forcé
			this.deplacementForce.executerLePremierMouvement();
		} else if (this.deplacementNaturelActuel != null) {
			this.deplacementNaturelActuel.executerLePremierMouvement();
		}
	}
	
}
