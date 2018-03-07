package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.Commande;
import main.Main;
import map.Heros;
import map.LecteurMap;
import map.Map;
import map.Transition;

/**
 * Le Heros est téléporté sur une autre Map.
 */
public class ChangerDeMap extends Commande implements CommandeEvent {
	private static final Logger LOG = LogManager.getLogger(ChangerDeMap.class);
	
	private final boolean definiParDesVariables;
	private final int numeroNouvelleMap;
	private final int xDebutHeros;
	private final int yDebutHeros;
	private int directionDebutHeros;
	private final Transition transition;
	
	/**
	 * Constructeur explicite
	 * @param variable le lieu d'arrivée est défini par des variables
	 * @param numeroNouvelleMap numéro de la nouvelle Map
	 * @param xDebutHeros coordonnée x du Héros (en carreaux) à son arrivée sur la Map
	 * @param yDebutHeros coordonnée y du Héros (en carreaux) à son arrivée sur la Map
	 * @param directionDebutHeros direction du Héros à son arrivée sur la Map
	 * @param transition visuelle pour passer d'une Map à l'autre
	 */
	public ChangerDeMap(final boolean variable, final int numeroNouvelleMap, final int xDebutHeros, 
			final int yDebutHeros, final int directionDebutHeros, final Transition transition) {
		this.definiParDesVariables = variable;
		this.numeroNouvelleMap = numeroNouvelleMap;
		this.xDebutHeros = xDebutHeros;
		this.yDebutHeros = yDebutHeros;
		this.directionDebutHeros = directionDebutHeros;
		this.transition = transition;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public ChangerDeMap(final HashMap<String, Object> parametres) {
		this( 
			parametres.containsKey("variable") && (boolean) parametres.get("variable"),
			(int) parametres.get("numeroNouvelleMap"), 
			(int) parametres.get("xDebutHeros"),
			(int) parametres.get("yDebutHeros"),
			parametres.containsKey("directionDebutHeros") ? (int) parametres.get("directionDebutHeros") : -1,
			parametres.containsKey("transition") ? Transition.parNom((String) parametres.get("transition")) : Transition.parDefaut()
		);
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		final Map ancienneMap = this.page.event.map;
		final Heros ancienHeros = ancienneMap.heros;

		if (this.directionDebutHeros == -1) {
			// aucune direction n'a été imposée pour le Héros, on garde l'ancienne
			this.directionDebutHeros = ancienHeros.direction;
		}
		
		// Le héros n'est pas forcément pile sur le coin haut-gauche du carreau
		final int ecartX = ancienHeros.x - this.page.event.x;
		final int ecartY = ancienHeros.y - this.page.event.y;
		
		final LecteurMap nouveauLecteur = new LecteurMap(this.transition);
		try {
			final int numeroNouvelleMapInterprete;
			final int xDebutHerosInterprete;
			final int yDebutHerosInterprete;
			if (this.definiParDesVariables) {
				//données à chercher dans les variables
				final int[] variables = getPartieActuelle().variables;
				numeroNouvelleMapInterprete = variables[numeroNouvelleMap];
				xDebutHerosInterprete = variables[xDebutHeros];
				yDebutHerosInterprete = variables[yDebutHeros];
			} else {
				//données brutes
				numeroNouvelleMapInterprete = numeroNouvelleMap;
				xDebutHerosInterprete = xDebutHeros;
				yDebutHerosInterprete = yDebutHeros;
			}
			
			final Map nouvelleMap = new Map(
					numeroNouvelleMapInterprete, 
					nouveauLecteur, 
					ancienHeros, 
					null, //pas de Brouillard forcé
					xDebutHerosInterprete * Main.TAILLE_D_UN_CARREAU, 
					yDebutHerosInterprete * Main.TAILLE_D_UN_CARREAU, 
					this.directionDebutHeros,
					ecartX, //décalage
					ecartY //décalage
			);
			
			nouveauLecteur.devenirLeNouveauLecteurMap(nouvelleMap);
		} catch (Exception e) {
			LOG.error("Impossible de charger la map numero "+numeroNouvelleMap, e);
		}
		return curseurActuel+1;
	}
	
	

}
