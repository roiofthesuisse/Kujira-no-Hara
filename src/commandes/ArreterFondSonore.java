package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import main.Commande;
import son.LecteurAudio;

/**
 * Arrêter le fond sonore.
 */
public class ArreterFondSonore extends Commande implements CommandeEvent, CommandeMenu {
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public ArreterFondSonore(final HashMap<String, Object> parametres) {}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		LecteurAudio.bgsEnCours.arreter();
		
		return curseurActuel+1;
	}

}
