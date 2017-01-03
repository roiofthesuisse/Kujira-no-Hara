package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import main.Commande;
import main.Fenetre;

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
	 * @param parametres liste de paramètres issus de JSON
	 */
	public CaptureDEcran(final HashMap<String, Object> parametres) {
		this();
	}

	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		Fenetre.getFenetre().lecteur.faireUneCaptureDEcran();
		
		return curseurActuel+1;
	}

}
