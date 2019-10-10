package commandes;

import java.util.HashMap;
import java.util.List;

import main.Commande;

/**
 * Modifier le mot de passe.
 */
public class ModifierMot extends Commande implements CommandeMenu, CommandeEvent {
	private final String nouveauMot;
	private final int numeroMot;

	/**
	 * Constructeur explicite
	 * 
	 * @param nouveauMot nouvelle valeur du mot
	 * @param numeroMot  numéro du mot à modifier
	 */
	public ModifierMot(final String nouveauMot, final int numeroMot) {
		this.nouveauMot = nouveauMot;
		this.numeroMot = numeroMot;
	}

	/**
	 * Constructeur générique
	 * 
	 * @param parametres liste de paramètres issus de JSON
	 */
	public ModifierMot(final HashMap<String, Object> parametres) {
		this((String) parametres.get("mot"), (int) parametres.get("numeroMot"));
	}

	@Override
	public final int executer(final int curseurActuel, final List<Commande> commandes) {
		getPartieActuelle().mots[this.numeroMot] = this.nouveauMot;
		this.element.menu.reactualiserTousLesTextes();
		return curseurActuel + 1;
	}

}
