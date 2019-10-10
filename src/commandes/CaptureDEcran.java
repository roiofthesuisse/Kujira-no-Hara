package commandes;

import java.util.HashMap;
import java.util.List;

import main.Commande;
import main.Main;

/**
 * Prendre une capture d'écran du jeu.
 */
public class CaptureDEcran extends Commande implements CommandeEvent, CommandeMenu {

	/**
	 * Constructeur explicite
	 */
	public CaptureDEcran() {
	}

	/**
	 * Constructeur générique
	 * 
	 * @param parametres liste de paramètres issus de JSON
	 */
	public CaptureDEcran(final HashMap<String, Object> parametres) {
		this();
	}

	@Override
	public final int executer(final int curseurActuel, final List<Commande> commandes) {
		Main.lecteur.faireUneCaptureDEcran();

		return curseurActuel + 1;
	}

}
