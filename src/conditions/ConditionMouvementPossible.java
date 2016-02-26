package conditions;

import java.util.HashMap;

import org.json.JSONObject;

import commandes.CommandeEvent;
import commandes.Mouvement;
import map.PageEvent;
import utilitaire.InterpreteurDeJson;

/**
 * Vérifie si ce Mouvement est possible.
 * L'Event considéré est mentionné dans l'objet Mouvement.
 */
public class ConditionMouvementPossible extends Condition implements CommandeEvent {
	private PageEvent page;
	
	private Mouvement mouvement;

	/**
	 * Constructeur explicite
	 * @param mouvement dont il faut vérifier la faisabilité
	 */
	public ConditionMouvementPossible(final Mouvement mouvement) {
		this.mouvement = mouvement;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public ConditionMouvementPossible(final HashMap<String, Object> parametres) {
		this(InterpreteurDeJson.recupererUnMouvement((JSONObject) parametres.get("mouvement")));
	}

	@Override
	public final boolean estVerifiee() {
		return mouvement.mouvementPossible();
	}

	/**
	 * Ce n'est pas une Condition qui implique une proximité avec le Héros.
	 * @return false 
	 */
	public final boolean estLieeAuHeros() {
		return false;
	}

	@Override
	public final PageEvent getPage() {
		return page;
	}

	@Override
	public final void setPage(final PageEvent page) {
		this.page = page;
	}

}
