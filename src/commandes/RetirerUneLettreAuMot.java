package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import main.Commande;
import main.Fenetre;

/**
 * Ajouter une lettre à la fin du mot de passe.
 */
public class RetirerUneLettreAuMot extends Commande implements CommandeMenu {
	
	/**
	 * Constructeur explicite
	 */
	public RetirerUneLettreAuMot() {
		
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public RetirerUneLettreAuMot(final HashMap<String, Object> parametres) {
		this();
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		final String mot = Fenetre.getPartieActuelle().mot;
		if (mot.length()>0) {
			Fenetre.getPartieActuelle().mot += mot.substring(0, mot.length()-1);
		}
		return curseurActuel+1;
	}

}
