package son;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Fichier audio au format OGG.
 */
public class MusiqueOgg extends Musique {
	
	protected MusiqueOgg(final String nom, final TypeMusique type, final float volume) {
		super(nom, type, volume);
		
		this.format = FormatAudio.OGG;
		try {
			File fichierOgg = new File(".\\ressources\\Audio\\"+type+"\\" + nom);
			this.stream = new FileInputStream(fichierOgg);
			this.clip = new OggClip(this.stream);
			//volume initial
			if (volume < VOLUME_MAXIMAL) {
				this.modifierVolume(volume);
			}
			//durée
			if (TypeMusique.ME.equals(type)) {
				AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(fichierOgg);
				AudioFormat format = audioInputStream.getFormat();
				long frames = audioInputStream.getFrameLength();
				this.dureeMillisecondes = (long) ((1000*frames+0.0) / format.getFrameRate());  
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			LOG.error("La musique "+this.nom+" n'est pas gérée par AudioSystem.", e);
		}
	}
	
	public final void modifierVolume(final float nouveauVolume) {
		((OggClip) this.clip).setGain(nouveauVolume);
		
		//mettre à jour les données
		this.volumeActuel = nouveauVolume;
	}
	
	public final void jouerUneSeuleFois(final Float volumeBgmMemorise) {
		class LancerOgg extends LancerSon {
			private final OggClip oggClip;
			
			LancerOgg(OggClip oggClip, TypeMusique type, long duree){
				super(type, duree, volumeBgmMemorise);
				this.oggClip = oggClip;
			}

			public void run() {
			    this.oggClip.play(); //jouer une seule fois
			    fermerALaFin();
			}
		}
		final LancerOgg r1 = new LancerOgg((OggClip) this.clip, this.type, this.dureeMillisecondes);
		this.thread = new Thread(r1, this.type.nom+" "+this.format.nom+" ("+this.nom+")");
		this.thread.start();
	}
	
	public final void jouerEnBoucle() {
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
	}
	
	public final void arreterSpecifique() {
		final OggClip oggClip = (OggClip) clip;
		oggClip.stop();
	}

	@Override
	public void mettreEnPause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reprendreApresPause() {
		// TODO Auto-generated method stub
		
	}
	
	
}
