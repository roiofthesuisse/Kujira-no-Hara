package conditions;

import java.util.HashMap;

import commandes.CommandeEvent;
import commandes.CommandeMenu;
import jeu.Partie;

/**
 * verifie si le Heros a �quip� ce Gadget.
 */
public class ConditionGadgetEquipe extends Condition implements CommandeEvent, CommandeMenu {
	public int idGadget;
	
	/**
	 * Constructeur explicite
	 * @param numero de la Condition
	 * @param idGadget identifiant du Gadget a verifier
	 */
	public ConditionGadgetEquipe(final int numero, final int idGadget) {
		this.numero = numero;
		this.idGadget = idGadget;
	}
	
	/**
	 * Constructeur generique
	 * @param parametres liste de parametres issus de JSON
	 */
	public ConditionGadgetEquipe(final HashMap<String, Object> parametres) {
		this( parametres.containsKey("numero") ? (int) parametres.get("numero") : -1,
			  (int) parametres.get("idGadget") );
	}
	
	@Override
	public final boolean estVerifiee() {
		final Partie partieActuelle = getPartieActuelle();
		if (partieActuelle.nombreDeGadgetsPossedes > 0) {
			return partieActuelle.idGadgetEquipe == this.idGadget;
		}
		return false; //aucun Gadget poss�d�
	}
	
	/**
	 * Ce n'est pas une Condition qui implique une proximite avec le Heros.
	 * @return false 
	 */
	public final boolean estLieeAuHeros() {
		return false;
	}

}