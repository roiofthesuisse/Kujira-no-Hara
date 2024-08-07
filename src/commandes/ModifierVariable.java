package commandes;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jeu.Partie;
import main.Commande;
import main.Main;
import map.Event;
import map.LecteurMap;

/**
 * Modifier la valeur d'une variable
 */
public class ModifierVariable extends Commande implements CommandeEvent, CommandeMenu {
	private static final Logger LOG = LogManager.getLogger(ModifierVariable.class);

	final int numeroVariable;
	final String operationAFaire;
	final String operationAFaire2;
	final int valeurADonner;
	final int valeurADonner2;

	/**
	 * Op�ration a effectuer sur la variable
	 */
	public static final String RENDRE_EGAL_A = "rendre egal a";
	public static final String AJOUTER = "ajouter";
	public static final String RETIRER = "retirer";
	public static final String MULTIPLIER = "multiplier";
	public static final String DIVISER = "diviser";
	public static final String MODULO = "modulo";

	/**
	 * Provenance de la valeur modificatrice
	 */
	public static final String VALEUR_BRUTE = "valeur brute";
	public static final String CONTENU_DE_LA_VARIABLE = "contenu de la variable";
	public static final String NOMBRE_ALEATOIRE = "nombre aleatoire";
	public static final String OBJETS_POSSEDES = "objets possedes";
	public static final String COORDONNEE_X = "coordonnee x";
	public static final String COORDONNEE_Y = "coordonnee y";
	public static final String TERRAIN = "terrain";
	public static final String NUMERO_MAP = "numero de la map";
	public static final String ARGENT_POSSEDE = "argent possede";

	/**
	 * Constructeur explicite Utiliser les constantes situees dans la classe pour
	 * creer une telle op�ration.
	 * 
	 * @param numeroVariable   Numero de la variable a modifier
	 * @param operationAFaire  0:rendreEgalA 1:ajouter 2:retirer 3:multiplier
	 *                         4:diviser 5:modulo
	 * @param operationAFaire2 0:valeur 1:contenuDeLaVariable 2:nombreAleatoire
	 * @param valeurADonner    valeur brute, Numero de la variable, ou borne
	 *                         inf�rieure al�atoire
	 * @param valeurADonner2   �ventuelle borne superieure al�atoire
	 */
	public ModifierVariable(final int numeroVariable, final String operationAFaire, final String operationAFaire2,
			final int valeurADonner, final int valeurADonner2) {
		this.numeroVariable = numeroVariable;
		this.operationAFaire = operationAFaire;
		this.operationAFaire2 = operationAFaire2;
		this.valeurADonner = valeurADonner;
		this.valeurADonner2 = valeurADonner2;
	}

	/**
	 * Constructeur generique
	 * 
	 * @param parametres liste de parametres issus de JSON
	 */
	public ModifierVariable(final HashMap<String, Object> parametres) {
		this((int) parametres.get("numeroVariable"), (String) parametres.get("operationAFaire"),
				(String) parametres.get("operationAFaire2"),
				parametres.containsKey("valeurADonner") ? (int) parametres.get("valeurADonner") : -1,
				parametres.containsKey("valeurADonner2") ? (int) parametres.get("valeurADonner2") : -1);
	}

	@Override
	public final int executer(final int curseurActuel, final List<Commande> commandes) {
		final int valeur;
		final Partie partieActuelle = getPartieActuelle();
		final Event event;

		// operationAFaire2 donne la provenance de la valeur modificatrice
		switch (operationAFaire2) {
		case VALEUR_BRUTE:
			valeur = valeurADonner;
			break;
		case CONTENU_DE_LA_VARIABLE:
			valeur = partieActuelle.variables[valeurADonner];
			break;
		case NOMBRE_ALEATOIRE:
			valeur = (new Random()).nextInt(valeurADonner2 - valeurADonner) + valeurADonner;
			break;
		case OBJETS_POSSEDES:
			valeur = partieActuelle.objetsPossedes[valeurADonner];
			break;
		case COORDONNEE_X:
			if (valeurADonner >= 0) {
				event = ((LecteurMap) Main.lecteur).map.eventsHash.get((Integer) valeurADonner);
			} else {
				// si aucun identifiant d'Event n'est sp�cifi�, on prend l'Event de la Commande
				event = this.page.event;
			}
			valeur = (event.x + event.largeurHitbox / 2) / Main.TAILLE_D_UN_CARREAU;
			break;
		case COORDONNEE_Y:
			if (valeurADonner >= 0) {
				event = ((LecteurMap) Main.lecteur).map.eventsHash.get((Integer) valeurADonner);
			} else {
				// si aucun identifiant d'Event n'est sp�cifi�, on prend l'Event de la Commande
				event = this.page.event;
			}
			valeur = (event.y + event.hauteurHitbox / 2) / Main.TAILLE_D_UN_CARREAU;
			break;
		case TERRAIN:
			if (valeurADonner >= 0) {
				event = ((LecteurMap) Main.lecteur).map.eventsHash.get((Integer) valeurADonner);
			} else {
				// si aucun identifiant d'Event n'est sp�cifi�, on prend l'Event de la Commande
				event = this.page.event;
			}
			valeur = event.calculerTerrain();
			break;
		case NUMERO_MAP:
			valeur = ((LecteurMap) Main.lecteur).map.numero;
			break;
		case ARGENT_POSSEDE:
			valeur = partieActuelle.argent;
			break;
		default:
			LOG.error("ModifierVariable.executer() : valeur inconnue pour operationAFaire2 : " + operationAFaire2);
			valeur = 0;
			break;
		}

		// operationAFaire donne le type d'op�ration a effectuer
		switch (operationAFaire) {
		case RENDRE_EGAL_A:
			partieActuelle.variables[numeroVariable] = valeur;
			break;
		case AJOUTER:
			partieActuelle.variables[numeroVariable] += valeur;
			break;
		case RETIRER:
			partieActuelle.variables[numeroVariable] -= valeur;
			break;
		case MULTIPLIER:
			partieActuelle.variables[numeroVariable] *= valeur;
			break;
		case DIVISER:
			partieActuelle.variables[numeroVariable] /= valeur;
			break;
		case MODULO:
			partieActuelle.variables[numeroVariable] %= valeur;
			break;
		default:
			LOG.error("ModifierVariable.executer() : valeur inconnue pour operationAFaire : " + operationAFaire);
			break;
		}

		return curseurActuel + 1;
	}

}
