package commandes;

import java.util.HashMap;
import java.util.List;

import main.Commande;

/**
 * Equiper le Heros avec une Arme qu'il poss�de
 */
public class AutoriserMenu extends Commande implements CommandeEvent {
	/** Autoriser : true, interdire : false */
	public boolean autoriser;

	/**
	 * Constructeur explicite
	 * 
	 * @param autoriser l'acc�s au Menu ou pas ?
	 */
	public AutoriserMenu(final boolean autoriser) {
		this.autoriser = autoriser;
	}

	/**
	 * Constructeur generique
	 * 
	 * @param parametres liste de parametres issus de JSON
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
