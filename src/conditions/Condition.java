package conditions;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import commandes.CommandeEvent;
import commandes.CommandeMenu;
import main.Commande;

/**
 * Une Condition peut servir a d�finir le moment de declenchement d'une Page, ou
 * faire partie du code Event.
 */
public abstract class Condition extends Commande {
	private static final Logger LOG = LogManager.getLogger(Condition.class);

	public int numero = -1; // le Numero de condition est le meme que le Numero de fin de condition qui
							// correspond

	/**
	 * La Condition est elle verifi�e ?
	 * 
	 * @return true si verifi�e, false si non verifi�e
	 */
	public abstract boolean estVerifiee();

	/**
	 * Une Condition est une Commande Event, elle peut etre execut�e pour faire des
	 * sauts de curseur. Son execution est instantan�e.
	 * 
	 * @param curseurActuel position du curseur avant l'execution
	 * @param commandes     liste des Commandes de la Page
	 * @return nouvelle position du curseur
	 */
	public final int executer(final int curseurActuel, final List<Commande> commandes) {
		// une Condition doit avoir un Numero pour etre execut�e comme Commande Event
		if (this.numero == -1) {
			LOG.error("La condition " + this.getClass().getName() + " n'a pas de Numero !");
		}

		if (estVerifiee()) {
			return curseurActuel + 1;
		} else {
			int nouveauCurseur = curseurActuel;
			boolean onATrouveLaFinDeSi = false;
			while (!onATrouveLaFinDeSi) {
				nouveauCurseur++;
				try {
					// la fin de si a le meme numero que la condition
					if (((Condition) commandes.get(nouveauCurseur)).numero == numero) {
						onATrouveLaFinDeSi = true;
					}
				} catch (IndexOutOfBoundsException e) {
					if (this instanceof CommandeEvent) {
						LOG.error("L'evenement n�" + this.page.event.id + " n'a pas trouv� sa fin de condition "
								+ this.numero + " :", e);
					}
					if (this instanceof CommandeMenu) {
						LOG.error("L'element de menu n�" + this.element.id + " n'a pas trouv� sa fin de condition "
								+ this.numero + " :", e);
					}
				} catch (Exception e) {
					// pas une condition
				}
			}
			return nouveauCurseur + 1;
		}
	}

	/**
	 * Est-ce que la Condition demande un mouvement particulier du Heros ? Contact,
	 * Arriv�eAuContact, Parler...
	 * 
	 * @return false si la Condition est a considerer pour l'apparence d'un Event,
	 *         false sinon
	 */
	public abstract boolean estLieeAuHeros();

	/**
	 * Les Commandes de Menu sont instantann�es et donc n'utilisent pas de curseur.
	 * Cette Methode, exig�e par CommandeMenu, est la meme pour toutes les
	 * Conditions.
	 */
	public void executer() {
		// rien
	}

	/**
	 * Traduit les Conditions depuis le format JSON et les range dans la liste des
	 * Conditions de la Page.
	 * 
	 * @param conditions     liste des Conditions de la Page
	 * @param conditionsJSON tableau JSON contenant les Conditions au format JSON
	 */
	public static void recupererLesConditions(final ArrayList<Condition> conditions, final JSONArray conditionsJSON) {
		JSONObject conditionJSON2 = null;
		for (Object conditionJSON : conditionsJSON) {
			try {
				conditionJSON2 = (JSONObject) conditionJSON;
				final Class<?> classeCondition = Class.forName("conditions.Condition" + conditionJSON2.get("nom"));
				try {
					// cas d'une Condition sans parametres

					final Constructor<?> constructeurCondition = classeCondition.getConstructor();
					final Condition condition = (Condition) constructeurCondition.newInstance();
					conditions.add(condition);

				} catch (NoSuchMethodException e0) {
					// cas d'une Condition utilisant des parametres

					final Iterator<String> parametresNoms = ((JSONObject) conditionJSON).keys();
					String parametreNom; // nom du parametre pour instancier la Condition
					Object parametreValeur; // valeur du parametre pour instancier la Condition
					final HashMap<String, Object> parametres = new HashMap<String, Object>();
					while (parametresNoms.hasNext()) {
						parametreNom = parametresNoms.next();
						if (!parametreNom.equals("nom")) { // le nom servait a trouver la classe, ici on ne s'int�resse
															// qu'aux parametres
							parametreValeur = ((JSONObject) conditionJSON).get(parametreNom);
							parametres.put(parametreNom, parametreValeur);
						}
					}
					final Constructor<?> constructeurCondition = classeCondition.getConstructor(parametres.getClass());
					final Condition condition = (Condition) constructeurCondition.newInstance(parametres);
					conditions.add(condition);
				}

			} catch (Exception e1) {
				LOG.error("Erreur lors de l'instanciation de la Condition : " + conditionJSON2, e1);
			}
		}
	}

}
