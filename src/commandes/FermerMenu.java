package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import main.Commande;

/**
 * Fermer le Menu actif.
 * Fermer le Menu sur la Map est donc IMPOSSIBLE.
 * Dans un Menu, on sort du sous-Menu ou bien on revient sur la Map (s'il n'y a pas de sur-Menu), ou encore on quitte le jeu s'il n'y a pas de Map non plus.
 */
public class FermerMenu extends Commande implements CommandeMenu {
	
	/**
	 * Constructeur explicite
	 */
	public FermerMenu() {
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public FermerMenu(final HashMap<String, Object> parametres) {
		this();
	}

	@Override
	public final void executer() {
		this.executer(0, null);
		
	}

	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		this.element.menu.lecteur.allerAuMenuParentOuRevenirAuJeu();
		return 0;
	}

}
