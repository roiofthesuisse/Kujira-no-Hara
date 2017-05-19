package utilitaire.son;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Classe utilitaire chargée de lire les fichiers audio du jeu.
 */
public abstract class LecteurAudio {
	private static final Logger LOG = LogManager.getLogger(LecteurAudio.class);
	static final int NOMBRE_DE_PISTES = 10;
	
	public static Musique[] bgmEnCours = new Musique[NOMBRE_DE_PISTES];
	public static Musique[] bgsEnCours = new Musique[NOMBRE_DE_PISTES];
	
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
		final Float[] volumeBgmMemorise = new Float[NOMBRE_DE_PISTES];
		for (int i = 0; i<NOMBRE_DE_PISTES; i++) {
			final Musique bgm = LecteurAudio.bgmEnCours[i];
			volumeBgmMemorise[i] = LecteurAudio.bgmEnCours[i].volumeActuel;
			bgm.modifierVolume(0);
		}
		musique.jouerUneSeuleFois(volumeBgmMemorise);
	}
	
	/**
	 * Jouer une musique
	 * @param nom du fichier audio
	 * @param volume  sonore auquel il faut jouer la musique
	 * @param piste sur laquelle jouer la musique
	 */
	public static synchronized void playBgm(final String nom, final float volume, final int piste) {
		// Si le nom est vide, on ignore
		if (nom == null || nom.equals("")) {
			LOG.debug("Nom de musique vide : on ne fait rien.");
			return;
		}
		
		if (LecteurAudio.bgmEnCours[piste] != null && nom.equals(LecteurAudio.bgmEnCours[piste].nom)) {
			// Même morceau que le précédent
			if (bgmEnCours[piste].volumeActuel != volume) {
				// Modification du volume uniquement
				bgmEnCours[piste].modifierVolume(volume);
			}
		} else {
			// Morceau différent du précédent
			
			// On éteint la musique actuelle
			stopBgm(piste);
			
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
			LecteurAudio.bgmEnCours[piste] = musique;
		}
		
	}
	
	/**
	 * Arrêter la musique actuellement jouée.
	 * @param piste à arrêter
	 */
	public static synchronized void stopBgm(final int piste) {
		if (LecteurAudio.bgmEnCours[piste] != null) {
			LecteurAudio.bgmEnCours[piste].arreter();
			LecteurAudio.bgmEnCours[piste] = null;
		}
	}

	/**
	 * Jouer un fond sonore.
	 * @param nom du fichier audio
	 * @param volume  sonore auquel il faut jouer la musique
	 * @param piste sur laquelle jouer
	 */
	public static synchronized void playBgs(final String nom, final float volume, final int piste) {
		// Si le nom est vide, on ignore
		if (nom == null || nom.equals("")) {
			return;
		}
			
		// Si on est déjà en train de jouer le bon fond sonore, on ne fait rien
		if (LecteurAudio.bgsEnCours[piste] == null || !nom.equals(LecteurAudio.bgsEnCours[piste].nom)) {
			// On éteint le fond sonore actuel
			stopBgs(piste);

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
			LecteurAudio.bgsEnCours[piste] = musique;
			
		// Modification du volume uniquement
		} else if (bgsEnCours[piste].volumeActuel != volume) {
			bgsEnCours[piste].modifierVolume(volume);
		}
	}
	
	/**
	 * Arrêter le fond sonore actuellement joué.
	 * @param piste à arrêter
	 */
	public static synchronized void stopBgs(final int piste) {
		if (LecteurAudio.bgsEnCours[piste] != null) {
			LecteurAudio.bgsEnCours[piste].arreter();
			LecteurAudio.bgsEnCours[piste] = null;
		}
	}
	
}
