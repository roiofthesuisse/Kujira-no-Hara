package son;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Classe chargée de lire les fichiers audio du jeu.
 */
public class LecteurAudio {
	public static Musique bgmEnCours = null;
	//TODO bgs
	
	/**
	 * Jouer un effet sonore.
	 */
	public static synchronized void playSe (final String nom) {
		Musique musique = new Musique(nom, Musique.TypeMusique.SE);
		musique.demarrerSe();
	}
	
	/**
	 * Jouer une musique au format OGG.
	 */
	public static synchronized void playOggBgm(final String nom, float volume){
		try {
			OggClip ogg = new OggClip(new FileInputStream(new File(".\\ressources\\Audio\\BGM\\" + nom)));
			ogg.loop();
			ogg.setGain(volume);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * @param nom
	 * @param volume
	 * @throws Exception 
	 */
	public static synchronized void playBgm (final String nom, float volume) {
		//si le nom est vide, on ignore
			if(nom==null || nom.equals("")){
				return;
			}
		//si on est déjà en train de jouer le bon morceau, on ne fait rien
		if(bgmEnCours==null || !nom.equals(bgmEnCours.nom)){
			//on éteint la musique actuelle
				stopBgm();

			//on lance la nouvelle
				Musique musique = new Musique(nom, Musique.TypeMusique.BGM);
				musique.demarrerBgm();
				
			//on met à jour les données
				bgmEnCours = musique;
		}
	}
	
	/**
	 * Arrêter une musique.
	 */
	public static synchronized void stopBgm(){
		if(bgmEnCours!=null){
			bgmEnCours.arreter();
		}
	}
	
}
