package commandes;

import java.util.HashMap;
import java.util.List;

import main.Commande;
import utilitaire.InterpreteurDeJson;
import utilitaire.Maths;
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
	 * 
	 * @param nomFichierSonore nom du fichier de l'effet sonore a jouer
	 * @param volume           sonore (entre 0.0f et 1.0f)
	 */
	public JouerEffetSonore(final String nomFichierSonore, final float volume) {
		this.nomFichierSonore = nomFichierSonore.replaceAll(InterpreteurDeJson.CARACTERES_INTERDITS, "_");
		this.volume = volume;
	}

	/**
	 * Constructeur g�n�rique
	 * 
	 * @param parametres liste de param�tres issus de JSON
	 */
	public JouerEffetSonore(final HashMap<String, Object> parametres) {
		this((String) parametres.get("nomFichierSonore"),
				parametres.containsKey("volume") ? Maths.toFloat(parametres.get("volume")) : Musique.VOLUME_MAXIMAL);
	}

	@Override
	public final int executer(final int curseurActuel, final List<Commande> commandes) {
		LecteurAudio.playSe(nomFichierSonore, volume);

		return curseurActuel + 1;
	}

}
