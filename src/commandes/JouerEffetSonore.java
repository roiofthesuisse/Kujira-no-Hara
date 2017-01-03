package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import main.Commande;
import son.LecteurAudio;

/**
 * Jouer un effet sonore.
 */
public class JouerEffetSonore extends Commande implements CommandeEvent, CommandeMenu {
	private final String nomFichierSonore;
	
	/**
	 * @param nomFichierSonore nom du fichier de l'effet sonore à jouer
	 */
	public JouerEffetSonore(final String nomFichierSonore) {
		this.nomFichierSonore = nomFichierSonore;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public JouerEffetSonore(final HashMap<String, Object> parametres) {
		this( (String) parametres.get("nomFichierSonore") );
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		executer();
		return curseurActuel+1;
	}

	@Override
	public final void executer() {
		LecteurAudio.playSe(nomFichierSonore);
	}

}
