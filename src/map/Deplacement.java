package map;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import commandes.Mouvement;
import utilitaire.InterpreteurDeJson;

/**
 * Un Déplacement est un ensemble de mouvements subis par un Event.
 * Selon son utilisation, le Déplacement pourra être naturel (situé dans la description JSON de l'Event)
 * ou forcé (provoqué par des Commandes Event).
 */
public class Deplacement {
	public final ArrayList<Mouvement> mouvements;
	public boolean ignorerLesMouvementsImpossibles = false;
	public boolean repeterLeDeplacement = true;
	
	/**
	 * Constructeur explicite
	 * @param mouvements liste des Mouvements constitutifs du Déplacement
	 * @param ignorerLesMouvementsImpossibles si le Mouvement est impossible, passe-t-on au suivant ?
	 * @param repeterLeDeplacement faut-il répéter le Déplacement à partir du début une fois qu'il est terminé ?
	 */
	public Deplacement(final ArrayList<Mouvement> mouvements, final boolean ignorerLesMouvementsImpossibles, final boolean repeterLeDeplacement) {
		this.mouvements = mouvements;
		this.ignorerLesMouvementsImpossibles = ignorerLesMouvementsImpossibles;
		this.repeterLeDeplacement = repeterLeDeplacement;
	}
	
	/**
	 * Constructeur générique
	 * @param idEvent identifiant de l'Event à déplacer
	 * @param deplacementJSON fichier JSON décrivant le Déplacement
	 * @param page de l'Event qui contient le Mouvement
	 */
	public Deplacement(final Integer idEvent, final JSONObject deplacementJSON, final PageEvent page) {
		this.mouvements = new ArrayList<Mouvement>();
		for (Object actionDeplacementJSON : deplacementJSON.getJSONArray("mouvements")) {
			this.mouvements.add( InterpreteurDeJson.recupererUnMouvement((JSONObject) actionDeplacementJSON, page) );
		}
		
		try {
			this.repeterLeDeplacement = (boolean) deplacementJSON.get("repeterLeDeplacement");
		} catch (JSONException e2) {
			this.repeterLeDeplacement = Event.REPETER_LE_DEPLACEMENT_PAR_DEFAUT;
		}
		try {
			this.ignorerLesMouvementsImpossibles = (boolean) deplacementJSON.get("ignorerLesMouvementsImpossibles");
		} catch (JSONException e2) {
			this.ignorerLesMouvementsImpossibles = Event.IGNORER_LES_MOUVEMENTS_IMPOSSIBLES_PAR_DEFAUT;
		}
	}
	
	/**
	 * Executer le premier Mouvement du Déplacement.
	 */
	public final void executerLePremierMouvement() {
		((Mouvement) this.mouvements.get(0)).executerLeMouvement(this);
	}
}
