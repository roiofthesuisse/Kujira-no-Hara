package utilitaire.son;

import java.io.IOException;
import java.io.InputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Permet d'uniformiser le controle de la Musique malgr� les differents formats audio.
 */
public abstract class Musique {
	//constantes
	protected static final Logger LOG = LogManager.getLogger(Musique.class);
	public static final float VOLUME_MAXIMAL = 1.0f;
	/** Un SE trop long sera tronqu� */
	private static final long DUREE_MAXIMALE_SE = 20000; //en millisecondes
	/** Quand il est impossible de calculer la Duree du ME */
	protected static final long DUREE_PAR_DEFAUT_ME = 20000; //en millisecondes
	protected static final long DELAI_AVANT_ME = 500; //en millisecondes
	protected static final String DOSSIER_AUDIO = "./ressources/Audio/";
	
	/**
	 * Le clip peut etre un Clip javax ou bien un OggClip.
	 */
	protected Object clip;
	protected InputStream stream;
	public String nom;
	public FormatAudio format;
	public long dureeMillisecondes;
	public TypeMusique type;
	protected Thread thread;
	public float volumeActuel;
	
	/**
	 * differents formats de fichiers audio possibles
	 */
	public enum FormatAudio {
		WAV("WAV"), OGG("OGG"), MP3("MP3");
		
		public final String nom;
		
		/**
		 * @param nom du format audio
		 */
		FormatAudio(final String nom) {
			this.nom = nom;
		}
	}
	
	/**
	 * differents types de fichiers audio possibles
	 */
	public enum TypeMusique {
		BGM("BGM"), BGS("BGS"), ME("ME"), SE("SE");

		public final String nom;
		
		/**
		 * @param nom du type de musique
		 */
		TypeMusique(final String nom) {
			this.nom = nom;
		}
	}
	
	/**
	 * Thread parall�le qui joue la Musique.
	 */
	abstract class LancerSon implements Runnable {
		private final TypeMusique type;
		private final Float[] volumeBgmMemorise;
		
		protected LancerSon(TypeMusique type, Float[] volumeBgmMemorise) {
			super();
			this.type = type;
			this.volumeBgmMemorise = volumeBgmMemorise;
		}
		
		abstract long obtenirDuree();
		
		/**
		 * Refermer le clip a la fin de son execution.
		 * Si c'est un ME, reDemarrer le BGM mis en silence.
		 */
		protected void fermerALaFin() {
			if (TypeMusique.SE.equals(this.type)) {
		    	// interrompre un SE trop long
		    	try {
		    		Thread.sleep(DUREE_MAXIMALE_SE);
				} catch (InterruptedException e) {
					LOG.error("Impossible d'attendre la fin de la musique "+nom, e);
				}
		    	
		    } else if (TypeMusique.ME.equals(this.type)) {
		    	// attendre la fin du ME
		    	try {
		    		Thread.sleep(DELAI_AVANT_ME+this.obtenirDuree());
				} catch (InterruptedException e) {
					LOG.error("Impossible d'attendre la fin de la musique "+nom, e);
				}
		    	
		    	// reDemarrer le BGM apres la fin du ME
		    	for (int i = 0; i<LecteurAudio.NOMBRE_DE_PISTES; i++) {
		    		final Musique bgm = LecteurAudio.bgmEnCours[i];
			    	if (bgm != null) {
			    		Float ancienVolume = this.volumeBgmMemorise[i];
			    		if (ancienVolume == null) {
			    			LOG.warn("Le ME est arrete sans volume BGM a restituer.");
			    			ancienVolume = Musique.VOLUME_MAXIMAL;
			    		}
			    		bgm.modifierVolume(ancienVolume);
			    	}
		    	}
		    }
		    arreter();
		}
	}
	
	/**
	 * Constructeur explicite
	 * @param nom du fichier audio
	 * @param type BGM, BGS, ME, SE
	 * @param volume entre 0.0 et 1.0
	 */
	protected Musique(final String nom, final TypeMusique type, final float volume) {
		this.nom = nom;
		this.type = type;
		this.volumeActuel = volume;
	}
	
	/**
	 * Modifier le volume de la Musique.
	 * @param nouveauVolume a appliquer
	 */
	public abstract void modifierVolume(float nouveauVolume);
	
	/**
	 * Jouer un fichier sonore qui s'arretera tout seul arriv� a la fin.
	 * @param volumeBgmMemorise on a m�moris� les volumes des BGM avant de lancer un ME
	 */
	public abstract void jouerUneSeuleFois(Float[] volumeBgmMemorise);
	
	/**
	 * Jouer une fichier sonore qui tourne en boucle sans s'arreter.
	 */
	public abstract void jouerEnBoucle();

	/**
	 * arreter cette Musique.
	 * Il y a potentiellement deux threads a fermer : le Clip et l'InputStream.
	 * Ne pas utiliser autrement que via LecteurAudio.arreterBgm().
	 */
	public final void arreter() {
		//on ferme le clip
		arreterSpecifique();
		//on retire le SE de la liste
		if (TypeMusique.SE.equals(this.type)) {
			LecteurAudio.seEnCours.remove(this);
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
	
	/**
	 * arreter la Musique en fonction du format audio.
	 */
	public abstract void arreterSpecifique();
	
	/**
	 * Mettre la Musique en pause.
	 */
	public abstract void mettreEnPause();
	
	/**
	 * Continuer la lecture de la Musique apres la pause.
	 */
	public abstract void reprendreApresPause();

}
