package conditions;

import java.util.HashMap;

import commandes.CommandeEvent;
import commandes.CommandeMenu;
import main.Fenetre;

/**
 * Vérifier la valeur du mot de passe.
 */
public class ConditionMot extends Condition implements CommandeEvent, CommandeMenu {
	private final String motAttendu;
	
	/**
	 * Constructeur explicite
	 * @param numero de la Condition
	 * @param motAttendu pour la valeur du mot de passe saisi par le joueur
	 */
	public ConditionMot(final int numero, final String motAttendu) {
		this.numero = numero;
		this.motAttendu = motAttendu;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public ConditionMot(final HashMap<String, Object> parametres) {
		this( 
				(int) parametres.get("numero"),
				(String) parametres.get("mot")
		);
	}
	
	@Override
	public final boolean estVerifiee() {
		return this.motAttendu.equals(Fenetre.getPartieActuelle().mot);
	}

	@Override
	public final boolean estLieeAuHeros() {
		return false;
	}

}
