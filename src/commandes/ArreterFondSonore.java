package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import main.Commande;
import son.LecteurAudio;

/**
 * Arrêter le fond sonore.
 */
public class ArreterFondSonore extends Commande implements CommandeEvent, CommandeMenu {
	/** Durée totale de l'arrêt en fondu */
	private final int nombreDeFrames;
	/** Compteur de frames de l'arrêt en fondu */
	private int frame;
	private float volumeInitial;
	
	/**
	 * Constructeur explicite
	 * @param nombreDeFrames durée totale de l'arrêt en fondu
	 */
	private ArreterFondSonore(final int nombreDeFrames) {
		this.nombreDeFrames = nombreDeFrames;
		this.frame = 0;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public ArreterFondSonore(final HashMap<String, Object> parametres) {
		this(parametres.containsKey("nombreDeFrames") ? (int) parametres.get("nombreDeFrames") : 0);
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		// On mémorise le volume initial
		if (frame == 0) {
			volumeInitial = LecteurAudio.bgsEnCours.volumeActuel;
		}
		
		if (frame < nombreDeFrames) {
			// Arrêt en fondu
			final float nouveauVolume = volumeInitial * (float) (nombreDeFrames-frame)/(float) nombreDeFrames;
			LecteurAudio.bgsEnCours.modifierVolume(nouveauVolume);
			frame++;
			
			return curseurActuel;
			
		} else {
			// L'arrêt en fondu est terminé
			LecteurAudio.bgsEnCours.arreter();
			frame = 0;
			
			return curseurActuel+1;
		}
	}

}
