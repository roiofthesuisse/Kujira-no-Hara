package conditions;

import java.util.HashMap;

import commandes.CommandeEvent;
import commandes.CommandeMenu;
import jeu.Partie;

/**
 * verifie si le Heros poss�de ce Gadget.
 */
public class ConditionGadgetPossede extends Condition implements CommandeEvent, CommandeMenu {
	public int idGadget;

	/**
	 * Constructeur explicite
	 * @param numero de la Condition
	 * @param idGadget identifiant du Gadget a verifier
	 */
	public ConditionGadgetPossede(final int numero, final int idGadget) {
		this.numero = numero;
		this.idGadget = idGadget;
	}
	
	/**
	 * Constructeur generique
	 * @param parametres liste de parametres issus de JSON
	 */
	public ConditionGadgetPossede(final HashMap<String, Object> parametres) {
		this( parametres.containsKey("numero") ? (int) parametres.get("numero") : -1,
			(int) parametres.get("idGadget") 
		);
	}
	
	@Override
	public final boolean estVerifiee() {
		final Partie partieActuelle = getPartieActuelle();
		return partieActuelle.gadgetsPossedes[this.idGadget];
	}
	
	/**
	 * Ce n'est pas une Condition qui implique une proximite avec le Heros.
	 * @return false 
	 */
	public final boolean estLieeAuHeros() {
		return false;
	}

}