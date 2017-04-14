package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import main.Commande;
import son.LecteurAudio;

/**
 * Arrêter la musique.
 */
public class ArreterMusique extends Commande implements CommandeEvent, CommandeMenu {
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public ArreterMusique(final HashMap<String, Object> parametres) {}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		LecteurAudio.bgmEnCours.arreter();
		
		return curseurActuel+1;
	}

}
