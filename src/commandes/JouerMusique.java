package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import main.Commande;
import son.LecteurAudio;
import son.Musique;

/**
 * Jouer un musique en boucle.
 */
public class JouerMusique extends Commande implements CommandeEvent, CommandeMenu {
	private final String nomFichierSonore;
	private final float volume;
	
	/**
	 * Constructeur explicite
	 * @param nomFichierSonore nom du fichier de la musique à jouer
	 * @param volume sonore (entre 0.0f et 1.0f)
	 */
	public JouerMusique(final String nomFichierSonore, final float volume) {
		this.nomFichierSonore = nomFichierSonore;
		this.volume = volume;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public JouerMusique(final HashMap<String, Object> parametres) {
		this( (String) parametres.get("nomFichierSonore"),
				parametres.containsKey("volume") ? (float) parametres.get("volume") : Musique.VOLUME_MAXIMAL
		);
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		LecteurAudio.playBgm(nomFichierSonore, volume);
		
		return curseurActuel+1;
	}

}
