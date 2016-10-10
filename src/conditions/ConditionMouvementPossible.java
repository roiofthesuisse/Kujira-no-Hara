package conditions;

import java.util.HashMap;

import org.json.JSONObject;

import commandes.CommandeEvent;
import commandes.Deplacement;
import utilitaire.InterpreteurDeJson;

/**
 * Vérifie si ce Mouvement est possible.
 * L'Event considéré est mentionné dans l'objet Mouvement.
 */
public class ConditionMouvementPossible extends Condition implements CommandeEvent {
	private Deplacement deplacement;

	/**
	 * Constructeur explicite
	 * @param numero de la condition
	 * @param deplacement qui ne contient qu'un Mouvement, ainsi que l'id de l'Event à déplacer
	 */
	public ConditionMouvementPossible(final int numero, final Deplacement deplacement) {
		this.numero = numero;
		this.deplacement = deplacement;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public ConditionMouvementPossible(final HashMap<String, Object> parametres) {
		this( parametres.get("numero") != null ? (int) parametres.get("numero") : -1,
			(Deplacement) InterpreteurDeJson.recupererUneCommande( (JSONObject) parametres.get("deplacement") )
		);
	}

	@Override
	public final boolean estVerifiee() {
		this.deplacement.page = this.page; //on apprend au Déplacement quelle est sa Page
		final boolean resultat = this.deplacement.mouvements.get(0).mouvementPossible();
		return resultat;
	}

	/**
	 * Ce n'est pas une Condition qui implique une proximité avec le Héros.
	 * @return false 
	 */
	public final boolean estLieeAuHeros() {
		return false;
	}
	
}
