package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.Commande;
import utilitaire.son.LecteurAudio;

/**
 * Arrêter la musique.
 */
public class ArreterMusique extends Commande implements CommandeEvent, CommandeMenu {
	private static final Logger LOG = LogManager.getLogger(ArreterMusique.class);
	
	/** Durée totale de l'arrêt en fondu */
	private final int nombreDeFrames;
	/** Compteur de frames de l'arrêt en fondu */
	private int frame;
	private float volumeInitial;
	private int piste;
	
	/**
	 * Constructeur explicite
	 * @param nombreDeFrames durée totale de l'arrêt en fondu
	 * @param piste à arrêter
	 */
	private ArreterMusique(final int nombreDeFrames, final int piste) {
		this.nombreDeFrames = nombreDeFrames;
		this.frame = 0;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public ArreterMusique(final HashMap<String, Object> parametres) {
		this(
				parametres.containsKey("nombreDeFrames") ? (int) parametres.get("nombreDeFrames") : 0,
				parametres.containsKey("piste") ? (int) parametres.get("piste") : 0
		);
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		// On ne fait rien si la musique est déjà arrêtée
		if (LecteurAudio.bgmEnCours == null || LecteurAudio.bgmEnCours[piste] == null) {
			LOG.warn("La musique est déjà arrêtée.");
			return curseurActuel+1;
		}
		
		// On mémorise le volume initial
		if (frame == 0) {
			volumeInitial = LecteurAudio.bgmEnCours[piste].volumeActuel;
		}
		
		if (frame < nombreDeFrames) {
			// Arrêt en fondu
			final float nouveauVolume = volumeInitial * (float) (nombreDeFrames-frame)/(float) nombreDeFrames;
			LecteurAudio.bgmEnCours[piste].modifierVolume(nouveauVolume);
			frame++;
			
			return curseurActuel;
			
		} else {
			// L'arrêt en fondu est terminé
			LecteurAudio.stopBgm(piste);
			frame = 0;
			
			LOG.info("Arrêt total de la musique.");
			return curseurActuel+1;
		}
	}

}
