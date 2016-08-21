package son;

/**
 * Classe utilitaire chargée de lire les fichiers audio du jeu.
 */
public abstract class LecteurAudio {
	public static Musique bgmEnCours = null;
	//TODO bgs, pareil que bgm
	//TODO me, pareil que se, sauf qu'il mute le bgm
	
	/**
	 * Jouer un effet sonore.
	 * @param nom du fichier audio
	 */
	public static synchronized void playSe(final String nom) {
		Musique musique = new Musique(nom, Musique.TypeMusique.SE);
		musique.demarrerSe();
	}
	
	/**
	 * Jouer une musique au format OGG.
	 * @param nom du fichier audio
	 * @param volume sonore auquel il faut jouer la musique
	 */
	/*
	import java.io.File;
	import java.io.FileInputStream;
	import java.io.IOException;
	public static synchronized void playOggBgm(final String nom, final float volume) {
		try {
			OggClip ogg = new OggClip(new FileInputStream(new File(".\\ressources\\Audio\\BGM\\" + nom)));
			ogg.loop();
			ogg.setGain(volume);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	*/
	
	/**
	 * Jouer une musique
	 * @param nom du fichier audio
	 * @param volume  sonore auquel il faut jouer la musique
	 */
	public static synchronized void playBgm(final String nom, final float volume) {
		//si le nom est vide, on ignore
			if (nom==null || nom.equals("")) {
				return;
			}
		//si on est déjà en train de jouer le bon morceau, on ne fait rien
		if (LecteurAudio.bgmEnCours==null || !nom.equals(LecteurAudio.bgmEnCours.nom)) {
			//on éteint la musique actuelle
				stopBgm();

			//on lance la nouvelle
				Musique musique = new Musique(nom, Musique.TypeMusique.BGM);
				musique.demarrerBgm();
				
			//on met à jour les données
				LecteurAudio.bgmEnCours = musique;
		}
	}
	
	/**
	 * Arrêter la musique actuellement jouée.
	 */
	public static synchronized void stopBgm() {
		if (LecteurAudio.bgmEnCours!=null) {
			LecteurAudio.bgmEnCours.arreter();
		}
	}
	
}
