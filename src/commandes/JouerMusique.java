package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.Commande;
import utilitaire.son.LecteurAudio;
import utilitaire.son.Musique;

/**
 * Jouer un musique en boucle.
 */
public class JouerMusique extends Commande implements CommandeEvent, CommandeMenu {
	private static final Logger LOG = LogManager.getLogger(JouerMusique.class);
	
	private final String nomFichierSonore;
	private final float volume;
	private final int nombreDeFrames;
	private int frame;
	private int piste;
	
	/**
	 * Constructeur explicite
	 * @param nomFichierSonore nom du fichier de la musique à jouer
	 * @param volume sonore (entre 0.0f et 1.0f)
	 * @param nombreDeFrames durée de l'entrée en fondu
	 * @param piste sur laquelle jouer
	 */
	public JouerMusique(final String nomFichierSonore, final float volume, final int nombreDeFrames, final int piste) {
		this.nomFichierSonore = nomFichierSonore;
		this.volume = volume;
		this.nombreDeFrames = nombreDeFrames;
		this.frame = 0;
		this.piste = piste;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public JouerMusique(final HashMap<String, Object> parametres) {
		this( 
				(String) parametres.get("nomFichierSonore"),
				parametres.containsKey("volume") ? (float) parametres.get("volume") : Musique.VOLUME_MAXIMAL,
				parametres.containsKey("nombreDeFrames") ? (int) parametres.get("nombreDeFrames") : 0,
				parametres.containsKey("piste") ? (int) parametres.get("piste") : 0
		);
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		if (frame == 0) {
			// Démarrage de la musique
			LecteurAudio.playBgm(nomFichierSonore, 0, piste);
			this.frame++;
			
			LOG.info("Démarrage de la musique.");
			return curseurActuel;
			
		} else if (frame < nombreDeFrames) {
			// Augmentation progressive du volume
			final float volumeProgressif = volume * (float) frame /(float) nombreDeFrames;
			LecteurAudio.bgmEnCours[piste].modifierVolume(volumeProgressif);
			this.frame++;
			
			return curseurActuel;
			
		} else {
			// Le volume final est atteint
			LecteurAudio.bgmEnCours[piste].modifierVolume(volume);
			this.frame = 0;
			
			LOG.info("La musique est démarrée.");
			return curseurActuel+1;
		}
	}

}
