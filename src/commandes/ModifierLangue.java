package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import main.Commande;
import main.Fenetre;

public class ModifierLangue extends Commande implements CommandeEvent, CommandeMenu {
	
	private final int langue;
	
	public ModifierLangue(final int langue) {
		this.langue = langue;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public ModifierLangue(final HashMap<String, Object> parametres) {
		this((int) parametres.get("langue"));
	}
	
	@Override
	public int executer(int curseurActuel, ArrayList<Commande> commandes) {
		Fenetre.getPartieActuelle().langue = this.langue;
		return curseurActuel+1;
	}

}
