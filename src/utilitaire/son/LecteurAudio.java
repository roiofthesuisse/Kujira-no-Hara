package utilitaire.son;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Classe utilitaire chargée de lire les fichiers audio du jeu.
 */
public abstract class LecteurAudio {
	private static final Logger LOG = LogManager.getLogger(LecteurAudio.class);
	
	public static Musique bgmEnCours = null;
	public static Musique bgsEnCours = null;
	
	/**
	 * Jouer un effet sonore.
	 * Volume sonore maximal par défaut.
	 * @param nom du fichier audio
	 */
	public static synchronized void playSe(final String nom) {
		playSe(nom, Musique.VOLUME_MAXIMAL);
	}
	
	/**
	 * Jouer un effet sonore.
	 * @param nom du fichier audio
	 * @param volume sonore
	 */
	public static synchronized void playSe(final String nom, final float volume) {
		Musique musique;
		if (nom.endsWith(".ogg")) {
			musique = new MusiqueOgg(nom, Musique.TypeMusique.SE, volume);
		} else if(nom.endsWith(".wav")) {
			musique = new MusiqueWav(nom, Musique.TypeMusique.SE, volume);
		} else if(nom.endsWith(".mp3")) {
			musique = new MusiqueMp3(nom, Musique.TypeMusique.SE, volume);
		} else {
			LOG.error("Format audio inconnu : "+nom);
			return;
		}
		musique.jouerUneSeuleFois(null);
	}
	
	/**
	 * Jouer une fanfare musicale.
	 * @param nom du fichier audio
	 * @param volume sonore
	 */
	public static synchronized void playMe(final String nom, final float volume) {
		final Musique musique;
		if (nom.endsWith(".ogg")) {
			musique = new MusiqueOgg(nom, Musique.TypeMusique.ME, volume);
		} else if(nom.endsWith(".wav")) {
			musique = new MusiqueWav(nom, Musique.TypeMusique.ME, volume);
		} else if(nom.endsWith(".mp3")) {
			musique = new MusiqueMp3(nom, Musique.TypeMusique.ME, volume);
		} else {
			LOG.error("Format audio inconnu : "+nom);
			return;
		}
		float volumeBgmMemorise = LecteurAudio.bgmEnCours.volumeActuel;
		LecteurAudio.bgmEnCours.modifierVolume(0);
		musique.jouerUneSeuleFois(volumeBgmMemorise);
	}
	
	/**
	 * Jouer une musique.
	 * Volume maximal par défaut.
	 * @param nom du fichier audio
	 */
	public static synchronized void playBgm(final String nom) {
		playBgm(nom, Musique.VOLUME_MAXIMAL);
	}
	
	/**
	 * Jouer une musique
	 * @param nom du fichier audio
	 * @param volume  sonore auquel il faut jouer la musique
	 */
	public static synchronized void playBgm(final String nom, final float volume) {
		// Si le nom est vide, on ignore
		if (nom == null || nom.equals("")) {
			LOG.debug("Nom de musique vide : on ne fait rien.");
			return;
		}
		
		if (LecteurAudio.bgmEnCours != null && nom.equals(LecteurAudio.bgmEnCours.nom)) {
			// Même morceau que le précédent
			if (bgmEnCours.volumeActuel != volume) {
				// Modification du volume uniquement
				bgmEnCours.modifierVolume(volume);
			}
		} else {
			// Morceau différent du précédent
			
			// On éteint la musique actuelle
			stopBgm();
			
			// On lance la nouvelle
			final Musique musique;
			if (nom.endsWith(".ogg")) {
				musique = new MusiqueOgg(nom, Musique.TypeMusique.BGM, volume);
			} else if(nom.endsWith(".wav")) {
				musique = new MusiqueWav(nom, Musique.TypeMusique.BGM, volume);
			} else if(nom.endsWith(".mp3")) {
				musique = new MusiqueMp3(nom, Musique.TypeMusique.BGM, volume);
			} else {
				LOG.error("Format audio inconnu : "+nom);
				return;
			}
			musique.jouerEnBoucle();
				
			// On met à jour les données
			LecteurAudio.bgmEnCours = musique;
		}
		
	}
	
	/**
	 * Arrêter la musique actuellement jouée.
	 */
	public static synchronized void stopBgm() {
		if (LecteurAudio.bgmEnCours != null) {
			LecteurAudio.bgmEnCours.arreter();
			LecteurAudio.bgmEnCours = null;
		}
	}
	
	/**
	 * Jouer un fond sonore.
	 * Volume maximal par défaut.
	 * @param nom du fichier audio
	 */
	public static synchronized void playBgs(final String nom) {
		playBgs(nom, Musique.VOLUME_MAXIMAL);
	}
	
	/**
	 * Jouer un fond sonore.
	 * @param nom du fichier audio
	 * @param volume  sonore auquel il faut jouer la musique
	 */
	public static synchronized void playBgs(final String nom, final float volume) {
		// Si le nom est vide, on ignore
		if (nom == null || nom.equals("")) {
			return;
		}
			
		// Si on est déjà en train de jouer le bon fond sonore, on ne fait rien
		if (LecteurAudio.bgsEnCours == null || !nom.equals(LecteurAudio.bgsEnCours.nom)) {
			// On éteint le fond sonore actuel
			stopBgs();

			// On lance le nouveau
			final Musique musique;
			if (nom.endsWith(".ogg")) {
				musique = new MusiqueOgg(nom, Musique.TypeMusique.BGS, volume);
			} else if(nom.endsWith(".wav")) {
				musique = new MusiqueWav(nom, Musique.TypeMusique.BGS, volume);
			} else if(nom.endsWith(".mp3")) {
				musique = new MusiqueMp3(nom, Musique.TypeMusique.BGS, volume);
			} else {
				LOG.error("Format audio inconnu : "+nom);
				return;
			}
			musique.jouerEnBoucle();
				
			// On met à jour les données
			LecteurAudio.bgsEnCours = musique;
			
		// Modification du volume uniquement
		} else if (bgsEnCours.volumeActuel != volume) {
			bgsEnCours.modifierVolume(volume);
		}
	}
	
	/**
	 * Arrêter le fond sonore actuellement joué.
	 */
	public static synchronized void stopBgs() {
		if (LecteurAudio.bgsEnCours != null) {
			LecteurAudio.bgsEnCours.arreter();
			LecteurAudio.bgsEnCours = null;
		}
	}
	
}
