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
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * Permet d'uniformiser le contrôle de la Musique malgré les différents formats audio.
 */
public class Musique {
	//constantes
	private static final Logger LOG = LogManager.getLogger(Musique.class);
	public static final float VOLUME_MAXIMAL = 1.0f;
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
		WAV("WAV"), OGG("OGG"), MP3("MP3");
		
		public final String nom;
		
		FormatAudio(final String nom) {
			this.nom = nom;
		}
	}
	
	/**
	 * Différents types de fichiers audio possibles
	 */
	public enum TypeMusique {
		BGM("BGM"), BGS("BGS"), ME("ME"), SE("SE");

		public final String nom;
		
		TypeMusique(final String nom) {
			this.nom = nom;
		}
	}
	
	/**
	 * Constructeur explicite
	 * @param nom du fichier audio
	 * @param type BGM, BGS, ME, SE
	 * @param volume entre 0.0 et 1.0
	 */
	public Musique(final String nom, final TypeMusique type, final float volume) {
		this.nom = nom;
		this.type = type;
		
		if (nom.endsWith(".ogg")) {
			//le fichier est un OGG
			this.format = FormatAudio.OGG;
			try {
				this.stream = new FileInputStream(new File(".\\ressources\\Audio\\"+type+"\\" + nom));
				this.clip = new OggClip(this.stream);
				//volume initial
				if (volume < VOLUME_MAXIMAL) {
					this.modifierVolume(FormatAudio.OGG, volume);
				}
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} else if (nom.endsWith(".wav")) {
			//le fichier est un WAV
			this.format = FormatAudio.WAV;
			try {
				this.clip = AudioSystem.getClip();
				this.stream = AudioSystem.getAudioInputStream(new File(".\\ressources\\Audio\\"+type+"\\" + nom));
				((Clip) clip).open((AudioInputStream) this.stream);
				//volume initial
				if (volume < VOLUME_MAXIMAL) {
					this.modifierVolume(FormatAudio.WAV, volume);
				}
				
			} catch (LineUnavailableException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (UnsupportedAudioFileException e) {
				e.printStackTrace();
			}
			
		} else if (nom.endsWith(".mp3")) {
			//le fichier est un MP3
			this.format = FormatAudio.MP3;
			final String uri = Paths.get("ressources/Audio/"+type+"/" + nom).toUri().toString();
			final Media media = new Media(uri);
			final MediaPlayer mediaPlayer = new MediaPlayer(media);
			this.clip = mediaPlayer;
			//volume initial
			if (volume < VOLUME_MAXIMAL) {
				this.modifierVolume(FormatAudio.MP3, volume);
			}
			
		} else {
			LOG.error("Format audio inconnu : "+nom);
		}
		
	}
	
	/**
	 * Modifier le volume de la Musique.
	 * @param format audio de la Musique
	 * @param nouveauVolume à appliquer
	 */
	public final void modifierVolume(final FormatAudio format, final float nouveauVolume) {
		switch (format) {
			case OGG:
				((OggClip) this.clip).setGain(nouveauVolume);
				break;
				
			case WAV:
				final FloatControl gainControl = (FloatControl) ((Clip) this.clip).getControl(FloatControl.Type.MASTER_GAIN);
				final float gain = (1f-nouveauVolume)*Float.MIN_VALUE;
				gainControl.setValue(gain);
				break;
				
			case MP3:
				((MediaPlayer) this.clip).setVolume(nouveauVolume);
				break;
				
			default:
				LOG.error("Format audio inconnu : "+this.nom);
				break;
		}
	}
	
	/**
	 * Jouer un fichier sonore qui s'arrêtera tout seul arrivé à la fin.
	 */
	public final void jouerUneSeuleFois() {
		switch (format) {
		case OGG : //le fichier est un OGG
			/**
			 * Thread parallèle qui s'occupera de la lecteur du fichier audio
			 */
			class RunOggBgm implements Runnable {
				/**
				 * Jouer le fichier audio.
				 */
				public void run() {
					final OggClip oggClip = (OggClip) clip;
				    oggClip.play(); //jouer une seule fois
				    try {
						Thread.sleep(DUREE_MAXIMALE_SE);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				    arreter();
				}
			}
			final RunOggBgm r1 = new RunOggBgm();
			this.thread = new Thread(r1, this.type.nom+" "+this.format.nom+" ("+this.nom+")");
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
					final Clip wavClip = (Clip) clip;
				    wavClip.start(); //jouer une seule fois
				    try {
						Thread.sleep(DUREE_MAXIMALE_SE);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				    arreter();
				}
			}
			final RunWavBgm r2 = new RunWavBgm();
			this.thread = new Thread(r2, this.type.nom+" "+this.format.nom+" ("+this.nom+")");
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
					final MediaPlayer mp3Clip = (MediaPlayer) clip;
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
			final RunMp3Bgm r3 = new RunMp3Bgm();
			this.thread = new Thread(r3, this.type.nom+" "+this.format.nom+" ("+this.nom+")");
			this.thread.start();
			break;
			
		default: //type de fichier inconnu
			LOG.error("Type de fichier inconnu : "+format);
			break;
		}
	}
	
	/**
	 * Jouer une fichier sonore qui tourne en boucle sans s'arrêter.
	 */
	public final void jouerEnBoucle() {
		switch (format) {
		case OGG : //le fichier est un OGG
			/**
			 * Thread parallèle qui s'occupera de la lecteur du fichier audio
			 */
			class RunOggBgm implements Runnable {
				/**
				 * Jouer le fichier audio.
				 */
				public void run() {
					final OggClip oggClip = (OggClip) clip;
				    oggClip.loop(); //jouer en boucle
				}
			}
			final RunOggBgm r1 = new RunOggBgm();
			this.thread = new Thread(r1, this.type.nom+" "+this.format.nom+" ("+this.nom+")");
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
					final Clip wavClip = (Clip) clip;
					wavClip.loop(Clip.LOOP_CONTINUOUSLY); //jouer en boucle
				}
			}
			final RunWavBgm r2 = new RunWavBgm();
			this.thread = new Thread(r2, this.type.nom+" "+this.format.nom+" ("+this.nom+")");
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
					final MediaPlayer mp3Clip = ((MediaPlayer) clip);
					mp3Clip.setCycleCount(MediaPlayer.INDEFINITE); //jouer en boucle
					mp3Clip.play();
				}
			}
			final RunMp3Bgm r3 = new RunMp3Bgm();
			this.thread = new Thread(r3, this.type.nom+" "+this.format.nom+" ("+this.nom+")");
			this.thread.start();
			break;
			
		default: //type de fichier inconnu
			LOG.error("Type de fichier inconnu : "+format);
			break;
		}
	}

	/**
	 * Arrêter cette Musique.
	 * Il y a potentiellement deux threads à fermer : le Clip et l'InputStream.
	 */
	public final void arreter() {
		//on ferme le clip
		switch (format) {
			case OGG : 
				final OggClip oggClip = (OggClip) clip;
				oggClip.stop();
				break;
				
			case WAV : 
				final Clip wavClip = (Clip) clip;
				wavClip.close();
				break;
			
			case MP3 :
				final MediaPlayer mp3Clip = (MediaPlayer) clip;
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
				LOG.warn("Impossible de fermer le stream audio "+this.nom, e);
			}
		}
	}
	
}
