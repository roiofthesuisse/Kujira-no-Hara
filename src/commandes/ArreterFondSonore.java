package commandes;

import java.util.HashMap;
import java.util.List;

import main.Commande;
import utilitaire.son.LecteurAudio;

/**
 * arreter le fond sonore.
 */
public class ArreterFondSonore extends Commande implements CommandeEvent, CommandeMenu {
	/** Duree totale de l'arret en fondu */
	private final int nombreDeFrames;
	/** Compteur de frames de l'arret en fondu */
	private int frame;
	private float volumeInitial;
	private int piste;

	/**
	 * Constructeur explicite
	 * 
	 * @param nombreDeFrames Duree totale de l'arret en fondu
	 * @param piste          a arreter
	 */
	private ArreterFondSonore(final int nombreDeFrames, final int piste) {
		this.nombreDeFrames = nombreDeFrames;
		this.frame = 0;
	}

	/**
	 * Constructeur generique
	 * 
	 * @param parametres liste de parametres issus de JSON
	 */
	public ArreterFondSonore(final HashMap<String, Object> parametres) {
		this(parametres.containsKey("nombreDeFrames") ? (int) parametres.get("nombreDeFrames") : 0,
				parametres.containsKey("piste") ? (int) parametres.get("piste") : 0);
	}

	@Override
	public final int executer(final int curseurActuel, final List<Commande> commandes) {
		// On m�morise le volume initial
		if (frame == 0) {
			volumeInitial = LecteurAudio.bgsEnCours[piste].volumeActuel;
		}

		if (frame < nombreDeFrames) {
			// arret en fondu
			final float nouveauVolume = volumeInitial * (float) (nombreDeFrames - frame) / (float) nombreDeFrames;
			LecteurAudio.bgsEnCours[piste].modifierVolume(nouveauVolume);
			frame++;

			return curseurActuel;

		} else {
			// L'arret en fondu est termine
			LecteurAudio.stopBgs(piste);
			frame = 0;

			return curseurActuel + 1;
		}
	}

}
