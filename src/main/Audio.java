package main;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/**
 * Classe chargée de lire les fichiers audio du jeu.
 */
public class Audio {
	
	public static synchronized void playSE(final String nom) {
		new Thread(new Runnable() { //the wrapper thread is unnecessary, unless it blocks on the Clip finishing; see comments.
			public void run() {
		    	try {
		    		Clip clip = AudioSystem.getClip();
		    		AudioInputStream inputStream = AudioSystem.getAudioInputStream(Fenetre.class.getResourceAsStream("/Audio/SE/" + nom));
		    		clip.open(inputStream);
		    		clip.start(); 
		    	} catch (Exception e) {
		    		e.printStackTrace();
		    	}
			}
		}).start();
	}
	
}
