package commandes;

import java.util.HashMap;
import java.util.List;

import main.Commande;

/**
 * Arrêter le Chronometre.
 */
public class ArreterChronometre extends Commande implements CommandeEvent {

	/**
	 * Constructeur vide
	 */
	public ArreterChronometre() {
	}

	/**
	 * Constructeur générique
	 * 
	 * @param parametres liste de paramètres issus de JSON
	 */
	public ArreterChronometre(final HashMap<String, Object> parametres) {
		this();
	}

	@Override
	public int executer(final int curseurActuel, final List<Commande> commandes) {
		getPartieActuelle().chronometre = null;
		return curseurActuel + 1;
	}

}
