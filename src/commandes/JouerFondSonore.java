package commandes;

import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.Commande;
import utilitaire.InterpreteurDeJson;
import utilitaire.Maths;
import utilitaire.son.LecteurAudio;
import utilitaire.son.Musique;

/**
 * Jouer un fond sonore en boucle.
 */
public class JouerFondSonore extends Commande implements CommandeEvent, CommandeMenu {
	private static final Logger LOG = LogManager.getLogger(JouerFondSonore.class);

	private final String nomFichierSonore;
	private final float volume;
	private final int nombreDeFrames;
	private int frame;
	private int piste;

	/**
	 * Constructeur explicite
	 * 
	 * @param nomFichierSonore nom du fichier de la musique a jouer
	 * @param volume           sonore (entre 0.0f et 1.0f)
	 * @param nombreDeFrames   Duree de l'entree en fondu
	 * @param piste            sur laquelle jouer
	 */
	public JouerFondSonore(final String nomFichierSonore, final float volume, final int nombreDeFrames,
			final int piste) {
		this.nomFichierSonore = nomFichierSonore.replaceAll(InterpreteurDeJson.CARACTERES_INTERDITS, "_");
		this.volume = volume;
		this.nombreDeFrames = nombreDeFrames;
		this.frame = 0;
		this.piste = piste;
	}

	/**
	 * Constructeur generique
	 * 
	 * @param parametres liste de parametres issus de JSON
	 */
	public JouerFondSonore(final HashMap<String, Object> parametres) {
		this((String) parametres.get("nomFichierSonore"),
				parametres.containsKey("volume") ? Maths.toFloat(parametres.get("volume")) : Musique.VOLUME_MAXIMAL,
				parametres.containsKey("nombreDeFrames") ? (int) parametres.get("nombreDeFrames") : 0,
				parametres.containsKey("piste") ? (int) parametres.get("piste") : 0);
	}

	@Override
	public final int executer(final int curseurActuel, final List<Commande> commandes) {
		if (frame == 0) {
			// D�marrage de la musique
			LecteurAudio.playBgs(nomFichierSonore, 0, piste);
			this.frame++;

			if (LecteurAudio.bgsEnCours[piste] == null) {
				LOG.error("Impossible de Demarrer le fond sonore \"" + nomFichierSonore + "\"");
			} else {
				LOG.info("D�marrage du fond sonore.");
			}
			return curseurActuel;

		} else if (frame < nombreDeFrames) {
			// Augmentation progressive du volume
			final float volumeProgressif = volume * (float) frame / (float) nombreDeFrames;
			LecteurAudio.bgsEnCours[piste].modifierVolume(volumeProgressif);
			this.frame++;

			return curseurActuel;

		} else {
			// Le volume final est atteint
			LecteurAudio.bgsEnCours[piste].modifierVolume(volume);
			this.frame = 0;

			return curseurActuel + 1;
		}
	}

}
