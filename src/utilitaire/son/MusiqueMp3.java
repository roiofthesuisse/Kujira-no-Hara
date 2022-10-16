package utilitaire.son;

import java.nio.file.Paths;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * Fichier audio au format MP3.
 */
public class MusiqueMp3 extends Musique {
	
	protected MusiqueMp3(final String nom, final TypeMusique type, final float volume) {
		super(nom, type, volume);
		
		//le fichier est un MP3
		this.format = FormatAudio.MP3;
		final String uri = Paths.get(DOSSIER_AUDIO+type+"/" + nom).toUri().toString();
		final Media media = new Media(uri);
		final MediaPlayer mediaPlayer = new MediaPlayer(media);
		this.clip = mediaPlayer;
		//volume initial
		if (volume < VOLUME_MAXIMAL) {
			this.modifierVolume(volume);
		}
	}
	
	public final void modifierVolume(final float nouveauVolume) {
		((MediaPlayer) this.clip).setVolume(nouveauVolume);
		
		//mettre a jour les donnees
		this.volumeActuel = nouveauVolume;
	}
	
	public final void jouerUneSeuleFois(final Float[] volumeBgmMemorise) {
		class LancerMp3 extends LancerSon {
			private final MediaPlayer mp3Clip;
			
			private LancerMp3(MediaPlayer clip, TypeMusique type) {
				super(type, volumeBgmMemorise);
				this.mp3Clip = clip;
			}
			
			final long obtenirDuree() {
				this.mp3Clip.setOnReady(this);
				final long dureeMs = (long) this.mp3Clip.getTotalDuration().toMillis();
				if (dureeMs>0) {
					dureeMillisecondes = dureeMs;
					LOG.debug("Duree du MP3 "+nom+": "+dureeMs+" ms");
					return dureeMs;
				} else {
					LOG.warn("Duree du MP3 inacessible !");
					return Musique.DUREE_PAR_DEFAUT_ME;
				}
			}
			
			/**
			 * Jouer le fichier audio.
			 */
			public void run() {
				mp3Clip.setCycleCount(1); //jouer une seule fois
				mp3Clip.play();
				fermerALaFin();
			}
			
		}
		final LancerMp3 r3 = new LancerMp3((MediaPlayer) this.clip, this.type);
		this.thread = new Thread(r3, this.type.nom+" "+this.format.nom+" ("+this.nom+")");
		if (TypeMusique.ME.equals(type)) {
			try {
				Thread.sleep(DELAI_AVANT_ME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.thread.start();
	}
	
	public final void jouerEnBoucle() {
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
	}
	
	public final void arreterSpecifique() {
		final MediaPlayer mp3Clip = (MediaPlayer) clip;
		mp3Clip.stop();
	}

	@Override
	public void mettreEnPause() {
		final MediaPlayer mp3Clip = (MediaPlayer) clip;
		mp3Clip.pause();
	}

	@Override
	public void reprendreApresPause() {
		final MediaPlayer mp3Clip = (MediaPlayer) clip;
		mp3Clip.play();
	}
	
	
}
