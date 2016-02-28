package conditions;

import java.util.HashMap;

import org.json.JSONObject;

import commandes.CommandeEvent;
import commandes.Mouvement;
import utilitaire.InterpreteurDeJson;

/**
 * Vérifie si ce Mouvement est possible.
 * L'Event considéré est mentionné dans l'objet Mouvement.
 */
public class ConditionMouvementPossible extends Condition implements CommandeEvent {
	private Mouvement mouvement;

	/**
	 * Constructeur explicite
	 * @param mouvement dont il faut vérifier la faisabilité
	 * @param numeroCondition numéro de la condition
	 */
	public ConditionMouvementPossible(final Mouvement mouvement, final int numeroCondition) {
		this.mouvement = mouvement;
		this.numero = numeroCondition;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public ConditionMouvementPossible(final HashMap<String, Object> parametres) {
		this(InterpreteurDeJson.recupererUnMouvement((JSONObject) parametres.get("mouvement"), null),
				(int) parametres.get("numero"));
	}

	@Override
	public final boolean estVerifiee() {
		this.mouvement.page = this.page; //on informe le Mouvement de sa Page
		return this.mouvement.mouvementPossible();
	}

	/**
	 * Ce n'est pas une Condition qui implique une proximité avec le Héros.
	 * @return false 
	 */
	public final boolean estLieeAuHeros() {
		return false;
	}
	
}
