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
	private final int numeroMot;
	
	/**
	 * Constructeur explicite
	 * @param nouveauMot nouvelle valeur du mot
	 * @param numeroMot numéro du mot à modifier
	 */
	public ModifierMot(final String nouveauMot, final int numeroMot) {
		this.nouveauMot = nouveauMot;
		this.numeroMot = numeroMot;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public ModifierMot(final HashMap<String, Object> parametres) {
		this( 
				(String) parametres.get("mot"),
				(int) parametres.get("numeroMot")
		);
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		Fenetre.getPartieActuelle().mots[this.numeroMot] = this.nouveauMot;
		this.element.menu.reactualiserTousLesTextes();
		return curseurActuel+1;
	}

}
