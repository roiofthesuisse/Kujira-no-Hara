package commandes;

import java.util.HashMap;
import java.util.List;

import jeu.Partie;
import main.Commande;

/**
 * Retirer un Gadget au joueur.
 */
public class RetirerGadget extends Commande implements CommandeEvent, CommandeMenu {
	private int idGadget;

	/**
	 * Constructeur explicite
	 * 
	 * @param idGadget identifiant du Gadget à retirer (numéro)
	 */
	public RetirerGadget(final int idGadget) {
		this.idGadget = idGadget;
	}

	/**
	 * Constructeur générique
	 * 
	 * @param parametres liste de paramètres issus de JSON
	 */
	public RetirerGadget(final HashMap<String, Object> parametres) {
		this((int) parametres.get("idGadget"));
	}

	@Override
	public final int executer(final int curseurActuel, final List<Commande> commandes) {
		// on procède à la suppression
		final Partie partieActuelle = getPartieActuelle();
		final boolean[] gadgetsPossedes = partieActuelle.gadgetsPossedes;
		if (gadgetsPossedes[idGadget]) {
			gadgetsPossedes[this.idGadget] = false;
			partieActuelle.nombreDeGadgetsPossedes--;
			partieActuelle.idGadgetEquipe = -1; // -1 pour signifier qu'aucun Gadget n'est équipé
		}
		return curseurActuel + 1;
	}

}
