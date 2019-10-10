package commandes;

import java.util.HashMap;
import java.util.List;

import main.Commande;
import main.Main;

/**
 * Eteindre le jeu.
 */
public class QuitterJeu extends Commande implements CommandeMenu {

	/**
	 * Constructeur vide
	 */
	public QuitterJeu() {

	}

	/**
	 * Constructeur générique
	 * 
	 * @param parametres liste de paramètres issus de JSON
	 */
	public QuitterJeu(final HashMap<String, Object> parametres) {
		this();
	}

	@Override
	public final int executer(final int curseurActuel, final List<Commande> commandes) {
		Main.quitterLeJeu = true;

		return curseurActuel + 1;
	}

}
