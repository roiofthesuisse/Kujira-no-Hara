package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import main.Commande;
import utilitaire.son.LecteurAudio;
import utilitaire.son.Musique;

/**
 * Jouer un effet sonore.
 */
public class JouerEffetSonore extends Commande implements CommandeEvent, CommandeMenu {
	private final String nomFichierSonore;
	private final float volume;
	
	/**
	 * Constructeur explicite
	 * @param nomFichierSonore nom du fichier de l'effet sonore à jouer
	 * @param volume sonore (entre 0.0f et 1.0f)
	 */
	public JouerEffetSonore(final String nomFichierSonore, final float volume) {
		this.nomFichierSonore = nomFichierSonore;
		this.volume = volume;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public JouerEffetSonore(final HashMap<String, Object> parametres) {
		this( (String) parametres.get("nomFichierSonore"),
				parametres.containsKey("volume") ? (float) parametres.get("volume") : Musique.VOLUME_MAXIMAL
		);
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		LecteurAudio.playSe(nomFichierSonore, volume);
		
		return curseurActuel+1;
	}

}
