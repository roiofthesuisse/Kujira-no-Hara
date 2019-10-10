package commandes;

import java.util.HashMap;
import java.util.List;

import main.Commande;

/**
 * Retirer la dernière lettre du mot de passe.
 */
public class RetirerUneLettreAuMot extends Commande implements CommandeMenu {
	final int numeroMot;

	/**
	 * Constructeur explicite
	 * 
	 * @param numeroMot numéro du mot auquel il faut retirer une lettre
	 */
	public RetirerUneLettreAuMot(final int numeroMot) {
		this.numeroMot = numeroMot;
	}

	/**
	 * Constructeur générique
	 * 
	 * @param parametres liste de paramètres issus de JSON
	 */
	public RetirerUneLettreAuMot(final HashMap<String, Object> parametres) {
		this((int) parametres.get("numeroMot"));
	}

	@Override
	public final int executer(final int curseurActuel, final List<Commande> commandes) {
		final String mot = getPartieActuelle().mots[this.numeroMot];
		if (mot != null && mot.length() > 0) {
			getPartieActuelle().mots[this.numeroMot] = mot.substring(0, mot.length() - 1);
			this.element.menu.reactualiserTousLesTextes();
		}
		return curseurActuel + 1;
	}

}
