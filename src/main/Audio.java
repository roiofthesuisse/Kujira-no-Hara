package main;

import java.io.InputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/**
 * Classe chargée de lire les fichiers audio du jeu.
 */
public class Audio {
	public static Clip bgmJoueActuellement = null;
	public static String nomBgmJoueActuellemnt = null;
	
	/**
	 * Jouer un effet sonore.
	 */
	public static synchronized void playSe (final String nom) {
		new Thread(new Runnable() {
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
	
	/**
	 * Jouer une musique.
	 */
	public static synchronized void playBgm (final String nom) {
		//si le nom est vide, on ignore
		if(nom==null || nom.equals("")){
			return;
		}
		
		//si on est déjà en train de jouer le bon morceau, on ne fait rien
		if(!nom.equals(nomBgmJoueActuellemnt)){
			//on éteint la musique actuelle
				if(bgmJoueActuellement!=null) stopBgm();
			//on lance la nouvelle
				class RunBGM implements Runnable{
					private Clip clip;
					public void run() {
				    	try {
				    		clip = AudioSystem.getClip();
				    		try{
				    			InputStream stream = Fenetre.class.getResourceAsStream("/Audio/BGM/"+nom);
				    			if(stream==null){
				    				System.err.println("Le stream est null.");
				    			}
					    		AudioInputStream inputStream = AudioSystem.getAudioInputStream(stream);
					    		clip.open(inputStream);
					    		clip.loop(Clip.LOOP_CONTINUOUSLY); 
				    		}catch(NullPointerException e){
				    			System.err.println("Impossible de trouver le fichier "+nom);
				    			throw e;
				    		}
				    	} catch (Exception e) {
				    		e.printStackTrace();
				    	}
					}	
					
					public Clip getClip(){
						return clip;
					}
				}
				RunBGM r = new RunBGM();
				Thread t = new Thread(r);
				t.start();
			//on met à jour les données
				bgmJoueActuellement = r.getClip();
				nomBgmJoueActuellemnt = nom;
		}
	}
	
	/**
	 * Arrêter une musique.
	 */
	public static synchronized void stopBgm(){
		bgmJoueActuellement.close();
	}
	
}
