package conditions;

import java.util.HashMap;

import commandes.CommandeEvent;
import commandes.CommandeMenu;

/**
 * V�rifier la valeur du mot de passe.
 */
public class ConditionMot extends Condition implements CommandeEvent, CommandeMenu {
	private final Object motAttendu;
	private final int numeroMot;
	
	/**
	 * Constructeur explicite
	 * @param numero de la Condition
	 * @param motAttendu valeur attendue pour le mot de passe saisi par le joueur (ou bien num�ro du mot)
	 * @param numeroMot num�ro du mot � comparer avec la valeur attendue
	 */
	public ConditionMot(final int numero, final Object motAttendu, final int numeroMot) {
		this.numero = numero;
		this.motAttendu = motAttendu;
		this.numeroMot = numeroMot;
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
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
