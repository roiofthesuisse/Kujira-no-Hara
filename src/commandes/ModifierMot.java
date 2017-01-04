package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import main.Commande;
import main.Fenetre;

/**
 * Modifier le mot de passe.
 */
public class ModifierMot extends Commande implements CommandeMenu, CommandeEvent {
	private final String nouveauMot;
	
	/**
	 * Constructeur explicite
	 * @param nouveauMot nouvelle valeur du mot
	 */
	public ModifierMot(final String nouveauMot) {
		this.nouveauMot = nouveauMot;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public ModifierMot(final HashMap<String, Object> parametres) {
		this( (String) parametres.get("mot"));
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		Fenetre.getPartieActuelle().mot = this.nouveauMot;
		return curseurActuel+1;
	}

}
