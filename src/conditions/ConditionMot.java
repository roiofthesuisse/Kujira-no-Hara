package conditions;

import java.util.HashMap;

import commandes.CommandeEvent;
import commandes.CommandeMenu;

/**
 * Vérifier la valeur du mot de passe.
 */
public class ConditionMot extends Condition implements CommandeEvent, CommandeMenu {
	private final Object motAttendu;
	private final int numeroMot;
	
	/**
	 * Constructeur explicite
	 * @param numero de la Condition
	 * @param motAttendu valeur attendue pour le mot de passe saisi par le joueur (ou bien numéro du mot)
	 * @param numeroMot numéro du mot à comparer avec la valeur attendue
	 */
	public ConditionMot(final int numero, final Object motAttendu, final int numeroMot) {
		this.numero = numero;
		this.motAttendu = motAttendu;
		this.numeroMot = numeroMot;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public ConditionMot(final HashMap<String, Object> parametres) {
		this( 
				(int) parametres.get("numero"),
				parametres.get("mot"),
				(int) parametres.get("numeroMot")
		);
	}
	
	@Override
	public final boolean estVerifiee() {
		final String aComparer;
		if (motAttendu instanceof String) {
			aComparer = (String) this.motAttendu;
		} else {
			aComparer = getPartieActuelle().mots[(Integer) this.motAttendu];
		}
		return aComparer.equals(getPartieActuelle().mots[this.numeroMot]);
	}

	@Override
	public final boolean estLieeAuHeros() {
		return false;
	}

}
