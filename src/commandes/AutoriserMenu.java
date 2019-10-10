package commandes;

import java.util.HashMap;
import java.util.List;

import main.Commande;

/**
 * Equiper le Heros avec une Arme qu'il possède
 */
public class AutoriserMenu extends Commande implements CommandeEvent {
	/** Autoriser : true, interdire : false */
	public boolean autoriser;

	/**
	 * Constructeur explicite
	 * 
	 * @param autoriser l'accès au Menu ou pas ?
	 */
	public AutoriserMenu(final boolean autoriser) {
		this.autoriser = autoriser;
	}

	/**
	 * Constructeur générique
	 * 
	 * @param parametres liste de paramètres issus de JSON
	 */
	public AutoriserMenu(final HashMap<String, Object> parametres) {
		this((boolean) parametres.get("autoriser"));
	}

	@Override
	public final int executer(final int curseurActuel, final List<Commande> commandes) {
		this.page.event.map.lecteur.autoriserMenu = this.autoriser;
		return curseurActuel + 1;
	}

}
