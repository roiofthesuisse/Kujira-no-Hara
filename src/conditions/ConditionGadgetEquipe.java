package conditions;

import java.util.HashMap;

import commandes.CommandeEvent;
import commandes.CommandeMenu;
import jeu.Partie;
import main.Fenetre;

/**
 * Vérifie si le Héros a équipé ce Gadget.
 */
public class ConditionGadgetEquipe extends Condition implements CommandeEvent, CommandeMenu {
	public int idGadget;
	
	/**
	 * Constructeur explicite
	 * @param numero de la Condition
	 * @param idGadget identifiant du Gadget à vérifier
	 */
	public ConditionGadgetEquipe(final int numero, final int idGadget) {
		this.numero = numero;
		this.idGadget = idGadget;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public ConditionGadgetEquipe(final HashMap<String, Object> parametres) {
		this( parametres.containsKey("numero") ? (int) parametres.get("numero") : -1,
			  (int) parametres.get("idGadget") );
	}
	
	@Override
	public final boolean estVerifiee() {
		final Partie partieActuelle = Fenetre.getPartieActuelle();
		if (partieActuelle.nombreDeGadgetsPossedes > 0) {
			return partieActuelle.idGadgetEquipe == this.idGadget;
		}
		return false; //aucun Gadget possédé
	}
	
	/**
	 * Ce n'est pas une Condition qui implique une proximité avec le Héros.
	 * @return false 
	 */
	public final boolean estLieeAuHeros() {
		return false;
	}

}