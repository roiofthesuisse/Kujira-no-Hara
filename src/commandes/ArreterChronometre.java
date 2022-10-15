package commandes;

import java.util.HashMap;
import java.util.List;

import main.Commande;

/**
 * arreter le Chronometre.
 */
public class ArreterChronometre extends Commande implements CommandeEvent {

	/**
	 * Constructeur vide
	 */
	public ArreterChronometre() {
	}

	/**
	 * Constructeur generique
	 * 
	 * @param parametres liste de parametres issus de JSON
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
