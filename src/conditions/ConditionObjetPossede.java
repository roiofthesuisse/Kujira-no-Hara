package conditions;

import java.util.HashMap;

import commandes.CommandeEvent;
import commandes.CommandeMenu;

/**
 * Le joueur possède-t-il l'Objet ?
 */
public class ConditionObjetPossede extends Condition implements CommandeEvent, CommandeMenu {
	private int idObjet;
	
	/**
	 * Constructeur partiel
	 * Réservé aux Conditions de Pages
	 * @param idObjet identifiant de l'Objet
	 */
	public ConditionObjetPossede(final int idObjet) {
		this(-1, idObjet);
	}
	
	/**
	 * Constructeur explicite
	 * @param numeroCondition numéro de la Condition
	 * @param idObjet identifiant de l'Objet
	 */
	public ConditionObjetPossede(final int numeroCondition, final int idObjet) {
		this.numero = numeroCondition;
		this.idObjet = idObjet;
	}
		
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public ConditionObjetPossede(final HashMap<String, Object> parametres) {
		this( parametres.get("numero") != null ? (int) parametres.get("numero") : -1,
			(int) parametres.get("idObjet")
		);
	}
	
	@Override
	public final boolean estVerifiee() {
		return getPartieActuelle().objetsPossedes[idObjet] >= 1;
	}

	@Override
	public final boolean estLieeAuHeros() {
		return false;
	}

}
