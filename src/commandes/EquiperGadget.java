package commandes;

import java.util.HashMap;
import java.util.List;

import main.Commande;

/**
 * Equiper le Heros avec un Gadget qu'il possède
 */
public class EquiperGadget extends Commande implements CommandeEvent {
	int idGadget;

	/**
	 * Constructeur explicite
	 * 
	 * @param idGadget identifiant du gadget à équiper
	 */
	public EquiperGadget(final int idGadget) {
		this.idGadget = idGadget;
	}

	/**
	 * Constructeur générique
	 * 
	 * @param parametres liste de paramètres issus de JSON
	 */
	public EquiperGadget(final HashMap<String, Object> parametres) {
		this((int) parametres.get("idGadget"));
	}

	@Override
	public final int executer(final int curseurActuel, final List<Commande> commandes) {
		getPartieActuelle().equiperGadget(this.idGadget);
		return curseurActuel + 1;
	}

}
