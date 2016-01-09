package son;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * Permet d'uniformiser le contrôle de la musique (présente sous différents formats audio).
 */
public class Musique {
	//constantes
	private static final long DUREE_MAXIMALE_SE = 5000; //en millisecondes
	
	/**
	 * Le clip peut être un Clip javax ou bien un OggClip.
	 */
	private Object clip;
	private InputStream stream;
	public String nom;
	public FormatAudio format;
	public TypeMusique type;
	private Thread thread;
	
	
	
	/**
	 * Différents formats de fichiers audio possibles
	 */
	public enum FormatAudio {
		WAV, OGG, MP3
	}
	
	/**
	 * Différents types de fichiers audio possibles
	 */
	public enum TypeMusique {
		BGM, BGE, ME, SE
	}
	
	/**
	 * Constructeur explicite
	 * @param nom du fichier audio
	 * @param type BGM, BGS, ME, SE
	 */
	public Musique(final String nom, final TypeMusique type) {
		this.nom = nom;
		this.type = type;
		
		if (nom.contains(".ogg")) {
			//le fichier est un OGG
			this.format = FormatAudio.OGG;
			try {
				this.stream = new FileInputStream(new File(".\\ressources\\Audio\\"+type+"\\" + nom));
				this.clip = new OggClip(this.stream);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (nom.contains(".wav")) {
			//le fichier est un WAV
			this.format = FormatAudio.WAV;
			try {
				this.clip = AudioSystem.getClip();
				this.stream = AudioSystem.getAudioInputStream(new File(".\\ressources\\Audio\\"+type+"\\" + nom));
				((Clip) clip).open((AudioInputStream) this.stream);
			} catch (LineUnavailableException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (UnsupportedAudioFileException e) {
				e.printStackTrace();
			}
		} else if (nom.contains(".mp3")) {
			//le fichier est un MP3
			this.format = FormatAudio.MP3;
			String uri = Paths.get("ressources/Audio/"+type+"/" + nom).toUri().toString();
			Media media = new Media(uri);
			MediaPlayer mediaPlayer = new MediaPlayer(media);
			this.clip = mediaPlayer;
		} else {
			new Exception("Format audio inconnu : "+nom).printStackTrace();
		}
	}
	
	/**
	 * Jouer un effet sonore.
	 */
	public final void demarrerSe() {
		switch(format) {
		case OGG : //le fichier est un OGG
			/**
			 * Thread parallèle qui s'occupera de la lecteur du fichier audio
			 */
			class RunOggBgm implements Runnable {
				/**
				 * Jouer le fichier audio.
				 */
				public void run() {
				    OggClip oggClip = (OggClip) clip;
				    oggClip.play(); //jouer une seule fois
				    try {
						Thread.sleep(DUREE_MAXIMALE_SE);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				    arreter();
				}
			}
			RunOggBgm r1 = new RunOggBgm();
			this.thread = new Thread(r1, "SE OGG ("+this.nom+")");
			this.thread.start();
			break;
			
		case WAV : //le fichier est un WAV
			/**
			 * Thread parallèle qui s'occupera de la lecteur du fichier audio
			 */
			class RunWavBgm implements Runnable {
				/**
				 * Jouer le fichier audio.
				 */
				public void run() {
				    Clip wavClip = (Clip) clip;
				    wavClip.start(); //jouer une seule fois
				    try {
						Thread.sleep(DUREE_MAXIMALE_SE);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				    arreter();
				}
			}
			RunWavBgm r2 = new RunWavBgm();
			this.thread = new Thread(r2, "SE WAV ("+this.nom+")");
			this.thread.start();
			break;
			
		case MP3 : //le fichier est un MP3
			/**
			 * Thread parallèle qui s'occupera de la lecteur du fichier audio
			 */
			class RunMp3Bgm implements Runnable {
				/**
				 * Jouer le fichier audio.
				 */
				public void run() {
					MediaPlayer mp3Clip = (MediaPlayer) clip;
					mp3Clip.setCycleCount(1); //jouer une seule fois
					mp3Clip.play();
					try {
						Thread.sleep(DUREE_MAXIMALE_SE);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					arreter();
				}
			}
			RunMp3Bgm r3 = new RunMp3Bgm();
			this.thread = new Thread(r3, "SE MP3 ("+this.nom+")");
			this.thread.start();
			break;
			
		default: //type de fichier inconnu
			new Exception("Type de fichier inconnu").printStackTrace();
			break;
		}
	}
	
	/**
	 * Jouer musique d'accompagnement.
	 */
	public final void demarrerBgm() {
		switch(format){
		case OGG : //le fichier est un OGG
			/**
			 * Thread parallèle qui s'occupera de la lecteur du fichier audio
			 */
			class RunOggBgm implements Runnable {
				/**
				 * Jouer le fichier audio.
				 */
				public void run() {
				    OggClip oggClip = (OggClip) clip;
				    oggClip.loop(); //jouer en boucle
				}
			}
			RunOggBgm r1 = new RunOggBgm();
			this.thread = new Thread(r1, "BGM OGG ("+this.nom+")");
			this.thread.start();
			break;
			
		case WAV : //le fichier est un WAV
			/**
			 * Thread parallèle qui s'occupera de la lecteur du fichier audio
			 */
			class RunWavBgm implements Runnable {
				/**
				 * Jouer le fichier audio.
				 */
				public void run() {
					Clip wavClip = (Clip) clip;
					wavClip.loop(Clip.LOOP_CONTINUOUSLY); //jouer en boucle
				}
			}
			RunWavBgm r2 = new RunWavBgm();
			this.thread = new Thread(r2, "BGM WAV ("+this.nom+")");
			this.thread.start();
			break;
			
		case MP3 : //le fichier est un MP3
			/**
			 * Thread parallèle qui s'occupera de la lecteur du fichier audio
			 */
			class RunMp3Bgm implements Runnable {
				/**
				 * Jouer le fichier audio.
				 */
				public void run() {
					MediaPlayer mp3Clip = ((MediaPlayer) clip);
					mp3Clip.setCycleCount(MediaPlayer.INDEFINITE); //jouer en boucle
					mp3Clip.play();
				}
			}
			RunMp3Bgm r3 = new RunMp3Bgm();
			this.thread = new Thread(r3, "BGM MP3 ("+this.nom+")");
			this.thread.start();
			break;
			
		default: //type de fichier inconnu
			new Exception("Type de fichier inconnu").printStackTrace();
			break;
		}
	}

	/**
	 * Arrêter cette Musique.
	 * Il y a potentiellement deux threads à fermer : le Clip et l'InputStream.
	 */
	public final void arreter() {
		//on ferme le clip
		switch(format) {
			case OGG : 
				OggClip oggClip = (OggClip) clip;
				oggClip.stop();
				break;
				
			case WAV : 
				Clip wavClip = (Clip) clip;
				wavClip.close();
				break;
			
			case MP3 :
				MediaPlayer mp3Clip = (MediaPlayer) clip;
				mp3Clip.stop();
				break;
				
			default :
				break;
		}
		//on ferme l'InputStream
		if (this.stream!=null) {
			try {
				this.stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
