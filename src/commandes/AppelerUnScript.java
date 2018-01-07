package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import main.Commande;

/**
 * Appeler un script ruby.
 */
public class AppelerUnScript extends Commande implements CommandeEvent, CommandeMenu {
	
	private final String script;
	
	/**
	 * Constructeur explicite
	 * @param script à exécuter
	 */
	public AppelerUnScript(final String script) {
		this.script = script;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public AppelerUnScript(final HashMap<String, Object> parametres) {
		this((String) parametres.get("script"));
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		// TODO Auto-generated method stub
		return curseurActuel+1;
	}

}
