package commandes;

import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.Commande;
import main.Main;
import map.Heros;
import map.LecteurMap;
import map.Map;
import map.Transition;
import map.positionInitiale.PositionInitiale;
import map.positionInitiale.PositionInitialeExhaustive;

/**
 * Le Heros est t�l�port� sur une autre Map.
 */
public class ChangerDeMap extends Commande implements CommandeEvent {
	private static final Logger LOG = LogManager.getLogger(ChangerDeMap.class);

	private final boolean definiParDesVariables;
	private final int numeroNouvelleMap;
	private final int xDebutHeros;
	private final int yDebutHeros;
	private int directionDebutHeros;
	private Transition transition;

	/**
	 * Constructeur explicite
	 * 
	 * @param variable            le lieu d'arriv�e est d�fini par des variables
	 * @param numeroNouvelleMap   Numero de la nouvelle Map
	 * @param xDebutHeros         Coordonnee x du Heros (en carreaux) a son arriv�e
	 *                            sur la Map
	 * @param yDebutHeros         Coordonnee y du Heros (en carreaux) a son arriv�e
	 *                            sur la Map
	 * @param directionDebutHeros direction du Heros a son arriv�e sur la Map
	 * @param transition          visuelle pour passer d'une Map a l'autre (si null,
	 *                            choix automatique)
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
	 * Constructeur generique
	 * 
	 * @param parametres liste de parametres issus de JSON
	 */
	public ChangerDeMap(final HashMap<String, Object> parametres) {
		this(parametres.containsKey("variable") && (boolean) parametres.get("variable"),
				(int) parametres.get("numeroNouvelleMap"), (int) parametres.get("xDebutHeros"),
				(int) parametres.get("yDebutHeros"),
				parametres.containsKey("directionDebutHeros") ? (int) parametres.get("directionDebutHeros") : -1,
				parametres.containsKey("transition") ? Transition.parNom((String) parametres.get("transition")) : null);
	}

	@Override
	public final int executer(final int curseurActuel, final List<Commande> commandes) {
		final Map ancienneMap = this.page.event.map;
		final Heros ancienHeros = ancienneMap.heros;

		if (this.directionDebutHeros == -1) {
			// aucune direction n'a ete impos�e pour le Heros, on garde l'ancienne
			this.directionDebutHeros = ancienHeros.direction;
		}

		// Si la Transition n'a pas ete sp�cifi�e par le code RM,
		// on choisit la mieux adapt�e automatiquement
		if (this.transition == null) {
			if (ancienneMap.leHerosEntreParUnePorte(ancienHeros.x / Main.TAILLE_D_UN_CARREAU,
					ancienHeros.y / Main.TAILLE_D_UN_CARREAU)) {
				// Transition en cas de franchissement de porte
				this.transition = Transition.ROND;
			} else {
				// Transition par defaut
				this.transition = Transition.DEFILEMENT;
			}
		}

		final LecteurMap nouveauLecteur = new LecteurMap();
		nouveauLecteur.transition = this.transition; // Transition qui introduira la nouvelle Map
		try {
			final int numeroNouvelleMapInterprete;
			final int xDebutHerosInterprete;
			final int yDebutHerosInterprete;
			if (this.definiParDesVariables) {
				// donnees a chercher dans les variables
				final int[] variables = getPartieActuelle().variables;
				numeroNouvelleMapInterprete = variables[numeroNouvelleMap];
				xDebutHerosInterprete = variables[xDebutHeros];
				yDebutHerosInterprete = variables[yDebutHeros];
			} else {
				// donnees brutes
				numeroNouvelleMapInterprete = numeroNouvelleMap;
				xDebutHerosInterprete = xDebutHeros;
				yDebutHerosInterprete = yDebutHeros;
			}

			final PositionInitiale positionInitiale = new PositionInitialeExhaustive(this.directionDebutHeros,
					ancienHeros.x, ancienHeros.y, xDebutHerosInterprete, yDebutHerosInterprete);
			final Map nouvelleMap = new Map(numeroNouvelleMapInterprete, nouveauLecteur, ancienHeros, null, // pas de
																											// Brouillard
																											// forc�
					positionInitiale);

			nouveauLecteur.devenirLeNouveauLecteurMap(nouvelleMap);
		} catch (Exception e) {
			LOG.error("Impossible de charger la map numero " + numeroNouvelleMap, e);
		}
		return curseurActuel + 1;
	}

}
