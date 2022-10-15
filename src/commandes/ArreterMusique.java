package commandes;

import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.Commande;
import utilitaire.son.LecteurAudio;

/**
 * Arr�ter la musique.
 */
public class ArreterMusique extends Commande implements CommandeEvent, CommandeMenu {
	private static final Logger LOG = LogManager.getLogger(ArreterMusique.class);

	/** Dur�e totale de l'arr�t en fondu */
	private final int nombreDeFrames;
	/** Compteur de frames de l'arr�t en fondu */
	private int frame;
	private float volumeInitial;
	private int piste;

	/**
	 * Constructeur explicite
	 * 
	 * @param nombreDeFrames dur�e totale de l'arr�t en fondu
	 * @param piste          a arr�ter
	 */
	private ArreterMusique(final int nombreDeFrames, final int piste) {
		this.nombreDeFrames = nombreDeFrames;
		this.frame = 0;
	}

	/**
	 * Constructeur generique
	 * 
	 * @param parametres liste de parametres issus de JSON
	 */
	public ArreterMusique(final HashMap<String, Object> parametres) {
		this(parametres.containsKey("nombreDeFrames") ? (int) parametres.get("nombreDeFrames") : 0,
				parametres.containsKey("piste") ? (int) parametres.get("piste") : 0);
	}

	@Override
	public final int executer(final int curseurActuel, final List<Commande> commandes) {
		// On ne fait rien si la musique est deja arr�t�e
		if (LecteurAudio.bgmEnCours == null || LecteurAudio.bgmEnCours[piste] == null) {
			LOG.warn("La musique est deja arr�t�e.");
			return curseurActuel + 1;
		}

		// On m�morise le volume initial
		if (frame == 0) {
			volumeInitial = LecteurAudio.bgmEnCours[piste].volumeActuel;
		}

		if (frame < nombreDeFrames) {
			// Arr�t en fondu
			final float nouveauVolume = volumeInitial * (float) (nombreDeFrames - frame) / (float) nombreDeFrames;
			LecteurAudio.bgmEnCours[piste].modifierVolume(nouveauVolume);
			frame++;

			return curseurActuel;

		} else {
			// L'arr�t en fondu est termine
			LecteurAudio.stopBgm(piste);
			frame = 0;

			LOG.info("Arr�t total de la musique.");
			return curseurActuel + 1;
		}
	}

}
