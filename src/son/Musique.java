package son;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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
	/**
	 * Le clip peut être un Clip javax ou bien un OggClip.
	 */
	private Object clip;
	public String nom;
	public FormatAudio format;
	public TypeMusique type;
	
	public enum FormatAudio{
		WAV, OGG, MP3
	}
	
	public enum TypeMusique{
		BGM, BGE, ME, SE
	}
	
	public Musique(String nom, TypeMusique type) {
		this.nom = nom;
		this.type = type;
		
		if(nom.contains(".ogg")){
			//le fichier est un OGG
			this.format = FormatAudio.OGG;
			try {
				this.clip = new OggClip(new FileInputStream(new File(".\\ressources\\Audio\\"+type+"\\" + nom)));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else if(nom.contains(".wav")){
			//le fichier est un WAV
			this.format = FormatAudio.WAV;
			try {
				this.clip = AudioSystem.getClip();
				AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File(".\\ressources\\Audio\\"+type+"\\" + nom));
				((Clip)clip).open(inputStream);
			} catch (LineUnavailableException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (UnsupportedAudioFileException e) {
				e.printStackTrace();
			}
		}else if(nom.contains(".mp3")){
			//le fichier est un MP3
			this.format = FormatAudio.MP3;
			String uri = Paths.get("ressources/Audio/"+type+"/" + nom).toUri().toString();
			Media media = new Media(uri);
			MediaPlayer mediaPlayer = new MediaPlayer(media);
			this.clip = mediaPlayer;
		}else{
			new Exception("Format audio inconnu : "+nom).printStackTrace();
		}
	}
	
	public void demarrerSe(){
		switch(format){
		case OGG : //le fichier est un OGG
			class RunOggBgm implements Runnable{
				public void run() {
				    OggClip oggClip = (OggClip) clip;
				    oggClip.play(); //jouer une seule fois
				}
			}
			RunOggBgm r1 = new RunOggBgm();
			Thread t1 = new Thread(r1);
			t1.start();
			break;
			
		case WAV : //le fichier est un WAV
			class RunWavBgm implements Runnable{
				public void run() {
				    Clip wavClip = (Clip) clip;
				    wavClip.start(); //jouer une seule fois
				}
			}
			RunWavBgm r2 = new RunWavBgm();
			Thread t2 = new Thread(r2);
			t2.start();
			break;
			
		case MP3 : //le fichier est un MP3
			class RunMp3Bgm implements Runnable{
				public void run() {
					MediaPlayer mp3Clip = (MediaPlayer) clip;
					mp3Clip.setCycleCount(1); //jouer une seule fois
					mp3Clip.play();
				}
			}
			RunMp3Bgm r3 = new RunMp3Bgm();
			Thread t3 = new Thread(r3);
			t3.start();
			break;
			
		default: //type de fichier inconnu
			new Exception("Type de fichier inconnu").printStackTrace();
			break;
		}
	}
	
	public void demarrerBgm(){
		switch(format){
		case OGG : //le fichier est un OGG
			class RunOggBgm implements Runnable{
				public void run() {
				    OggClip oggClip = (OggClip) clip;
				    oggClip.loop(); //jouer en boucle
				}
			}
			RunOggBgm r1 = new RunOggBgm();
			Thread t1 = new Thread(r1);
			t1.start();
			break;
			
		case WAV : //le fichier est un WAV
			class RunWavBgm implements Runnable{
				public void run() {
					Clip wavClip = (Clip) clip;
					wavClip.loop(Clip.LOOP_CONTINUOUSLY); //jouer en boucle
				}
			}
			RunWavBgm r2 = new RunWavBgm();
			Thread t2 = new Thread(r2);
			t2.start();
			break;
			
		case MP3 : //le fichier est un MP3
			class RunMp3Bgm implements Runnable{
				public void run() {
					MediaPlayer mp3Clip = ((MediaPlayer) clip);
					mp3Clip.setCycleCount(MediaPlayer.INDEFINITE); //jouer en boucle
					mp3Clip.play();
				}
			}
			RunMp3Bgm r3 = new RunMp3Bgm();
			Thread t3 = new Thread(r3);
			t3.start();
			break;
			
		default: //type de fichier inconnu
			new Exception("Type de fichier inconnu").printStackTrace();
			break;
		}
	}

	public void arreter() {
		switch(format){
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
		
	}
}
