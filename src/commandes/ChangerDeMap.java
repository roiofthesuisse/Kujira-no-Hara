package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.Commande;
import main.Fenetre;
import map.Heros;
import map.LecteurMap;
import map.Map;
import map.Transition;

/**
 * Le Heros est téléporté sur une autre Map.
 */
public class ChangerDeMap extends Commande implements CommandeEvent {
	private static final Logger LOG = LogManager.getLogger(ChangerDeMap.class);
	
	private final int numeroNouvelleMap;
	private final int xDebutHeros;
	private final int yDebutHeros;
	private final Transition transition;
	
	/**
	 * Constructeur explicite
	 * @param numeroNouvelleMap numéro de la nouvelle Map
	 * @param xDebutHeros coordonnée x du Héros (en pixels) à son arrivée sur la Map
	 * @param yDebutHeros coordonnée y du Héros (en pixels) à son arrivée sur la Map
	 * @param transition visuelle pour passer d'une Map à l'autre
	 */
	public ChangerDeMap(final int numeroNouvelleMap, final int xDebutHeros, final int yDebutHeros, final Transition transition) {
		this.numeroNouvelleMap = numeroNouvelleMap;
		this.xDebutHeros = xDebutHeros;
		this.yDebutHeros = yDebutHeros;
		this.transition = transition;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public ChangerDeMap(final HashMap<String, Object> parametres) {
		this( (int) parametres.get("numeroNouvelleMap"), 
			(int) parametres.get("xDebutHeros") * Fenetre.TAILLE_D_UN_CARREAU,
			(int) parametres.get("yDebutHeros") * Fenetre.TAILLE_D_UN_CARREAU,
			parametres.containsKey("transition") ? Transition.parNom((String) parametres.get("transition")) : Transition.parDefaut()
		);
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		final Map ancienneMap = this.page.event.map;
		final Heros ancienHeros = ancienneMap.heros;
		final int directionHeros = ancienHeros.direction;
		
		final LecteurMap nouveauLecteur = new LecteurMap(Fenetre.getFenetre(), this.transition);
		try {
			final Map nouvelleMap = new Map(
					numeroNouvelleMap, 
					nouveauLecteur, 
					ancienHeros, 
					null, //pas de Brouillard forcé
					xDebutHeros, 
					yDebutHeros, 
					directionHeros
			);			
			nouveauLecteur.devenirLeNouveauLecteurMap(nouvelleMap);
		} catch (Exception e) {
			LOG.error("Impossible de charger la map numero "+numeroNouvelleMap, e);
		}
		return curseurActuel+1;
	}
	
	

}
