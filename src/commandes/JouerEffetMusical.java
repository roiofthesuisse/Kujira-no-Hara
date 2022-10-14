package commandes;

import java.util.HashMap;
import java.util.List;

import main.Commande;
import utilitaire.InterpreteurDeJson;
import utilitaire.Maths;
import utilitaire.son.LecteurAudio;
import utilitaire.son.Musique;

/**
 * Jouer une fanfare. La fanfare interromp la musique.
 */
public class JouerEffetMusical extends Commande implements CommandeEvent, CommandeMenu {
	private final String nomFichierSonore;
	private final float volume;

	/**
	 * Constructeur explicite
	 * 
	 * @param nomFichierSonore nom du fichier de la fanfare a jouer
	 * @param volume           sonore (entre 0.0f et 1.0f)
	 */
	public JouerEffetMusical(final String nomFichierSonore, final float volume) {
		this.nomFichierSonore = nomFichierSonore.replaceAll(InterpreteurDeJson.CARACTERES_INTERDITS, "_");
		this.volume = volume;
	}

	/**
	 * Constructeur generique
	 * 
	 * @param parametres liste de parametres issus de JSON
	 */
	public JouerEffetMusical(final HashMap<String, Object> parametres) {
		this((String) parametres.get("nomFichierSonore"),
				parametres.containsKey("volume") ? Maths.toFloat(parametres.get("volume")) : Musique.VOLUME_MAXIMAL);
	}

	@Override
	public final int executer(final int curseurActuel, final List<Commande> commandes) {
		LecteurAudio.playMe(nomFichierSonore, volume);

		return curseurActuel + 1;
	}

}
