package utilitaire.son;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Fichier audio au format OGG.
 */
public class MusiqueOgg extends Musique {
	protected MusiqueOgg(final String nom, final TypeMusique type, final float volume) {
		super(nom, type, volume);
		
		this.format = FormatAudio.OGG;
		try {
			final File fichierOgg = new File(DOSSIER_AUDIO+type+"/" + nom);
			this.stream = new FileInputStream(fichierOgg);
			this.clip = new OggClip(this.stream);
			//volume initial
			if (volume < VOLUME_MAXIMAL) {
				this.modifierVolume(volume);
			}
			//Duree
			if (TypeMusique.ME.equals(type)) {
				this.dureeMillisecondes = (long) calculerDuree(fichierOgg);
			}
		} catch (FileNotFoundException e) {
			LOG.error(e);
		} catch (IOException e) {
			LOG.error(e);
		}
	}
	
	/**
	 * Calculer la Duree de la musique Ogg.
	 * @param fichierOgg a mesurer
	 * @return Duree (en millisecondes)
	 */
	private double calculerDuree(final File fichierOgg) {
		int debit = -1;
		int longueur = -1;
		
		final int taille = (int) fichierOgg.length();
		final byte[] t = new byte[taille];
		try {
			this.stream.read(t);
			
			//4 octets pour OggS, 2 octets inutiles, 8 octets pour la longueur
			for (int i = taille-1-8-2-4; i>=0; i--) { 
				// On cherche la longueur
				if (
						longueur<0
						&& t[i]==(byte)'O'
						&& t[i+1]==(byte)'g'
						&& t[i+2]==(byte)'g'
						&& t[i+3]==(byte)'S'
						//&& t[i+4]==0
						//&& t[i+5]==4
				) {
					final byte[] byteArray = new byte[]{
							t[i+6],t[i+7],t[i+8],t[i+9],
							t[i+10],t[i+11],t[i+12],t[i+13]
					};
					final ByteBuffer bb = ByteBuffer.wrap(byteArray);
					bb.order(ByteOrder.LITTLE_ENDIAN);
					longueur = bb.getInt(0);
					LOG.debug("OggS "+longueur);
				}
			}
			for (int i = 0; i<taille-8-2-4; i++) {
				// On cherche le d�bit
				if (debit<0
						&& t[i]==(byte)'v'
						&& t[i+1]==(byte)'o'
						&& t[i+2]==(byte)'r'
						&& t[i+3]==(byte)'b'
						&& t[i+4]==(byte)'i'
						&& t[i+5]==(byte)'s'
				) {
					final byte[] byteArray = new byte[]{t[i+11],t[i+12],t[i+13],t[i+14]};
					final ByteBuffer bb = ByteBuffer.wrap(byteArray);
					bb.order(ByteOrder.LITTLE_ENDIAN);
					debit = bb.getInt(0);
					LOG.debug("vorbis "+debit);
				}
				
			}
		} catch (IOException e) {
			LOG.error("Erreur a la lecture du OGG.", e);
		}
		
		if (debit<0 || longueur<0) {
			LOG.error("Impossible de trouver la Duree de ce OGG." + (debit<0 ? " Ce n'est pas un vorbis." : ""));
			return DUREE_PAR_DEFAUT_ME; //valeur bidon
		} else {
			final double duree = (double) (longueur*1000) / (double) debit;
			LOG.debug("Duree du OGG : "+duree+" ms");
			return duree;
		}
	}
	
	@Override
	public final void modifierVolume(final float nouveauVolume) {
		((OggClip) this.clip).setGain(nouveauVolume);
		
		//mettre a jour les donn�es
		this.volumeActuel = nouveauVolume;
	}
	
	@Override
	public final void jouerUneSeuleFois(final Float[] volumeBgmMemorise) {
		class LancerOgg extends LancerSon {
			private final OggClip oggClip;
			
			LancerOgg(OggClip oggClip, TypeMusique type){
				super(type, volumeBgmMemorise);
				this.oggClip = oggClip;
			}

			public void run() {
			    this.oggClip.play(); //jouer une seule fois
			    fermerALaFin();
			}

			@Override
			long obtenirDuree() {
				return dureeMillisecondes;
			}
		}
		final LancerOgg r1 = new LancerOgg((OggClip) this.clip, this.type);
		this.thread = new Thread(r1, this.type.nom+" "+this.format.nom+" ("+this.nom+")");
		if (TypeMusique.ME.equals(type)) {
			try {
				Thread.sleep(DELAI_AVANT_ME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.thread.start();
	}
	
	@Override
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
	
	@Override
	public final void arreterSpecifique() {
		final OggClip oggClip = (OggClip) clip;
		oggClip.stop();
	}

	@Override
	public final void mettreEnPause() {
		final OggClip oggClip = (OggClip) clip;
		oggClip.pause();
	}

	@Override
	public final void reprendreApresPause() {
		final OggClip oggClip = (OggClip) clip;
		oggClip.resume();
	}
	
	
}
