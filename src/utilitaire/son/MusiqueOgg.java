package utilitaire.son;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

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
				this.dureeMillisecondes = calculerDuree(fichierOgg);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private final long calculerDuree(File fichier) {
		//TODO
		//look for last "OggS" of file
		//read a byte (should be 0)
		//read a byte (only bit 3 should be set)
		//then read a 64-bit little endian value
		return 1000;
	}
	
	public final void modifierVolume(final float nouveauVolume) {
		((OggClip) this.clip).setGain(nouveauVolume);
		
		//mettre à jour les données
		this.volumeActuel = nouveauVolume;
	}
	
	public final void jouerUneSeuleFois(final Float[] volumeBgmMemorise) {
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
		final OggClip oggClip = (OggClip) clip;
		oggClip.pause();
	}

	@Override
	public void reprendreApresPause() {
		final OggClip oggClip = (OggClip) clip;
		oggClip.resume();
	}
	
	
}
