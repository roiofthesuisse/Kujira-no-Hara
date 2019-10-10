package commandes;

import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.Commande;
import utilitaire.son.LecteurAudio;
import utilitaire.son.Musique;

/**
 * Arrêter la musique.
 */
public class ArreterLesEffetsSonores extends Commande implements CommandeEvent, CommandeMenu {
	private static final Logger LOG = LogManager.getLogger(ArreterLesEffetsSonores.class);

	/** Durée totale de l'arrêt en fondu */
	private final static int DUREE_FONDU = 10;
	/** Compteur de frames de l'arrêt en fondu */
	private int frame;

	/**
	 * Constructeur explicite
	 */
	private ArreterLesEffetsSonores() {
		this.frame = 0;
	}

	/**
	 * Constructeur générique
	 * 
	 * @param parametres liste de paramètres issus de JSON
	 */
	public ArreterLesEffetsSonores(final HashMap<String, Object> parametres) {
		this();
	}

	@Override
	public final int executer(final int curseurActuel, final List<Commande> commandes) {
		for (int i = 0; i < LecteurAudio.seEnCours.size(); i++) {
			final Musique se = LecteurAudio.seEnCours.get(i);
			se.arreter();
			i--;
		}
		LOG.info("Arrêt des effets sonores.");
		return curseurActuel + 1;
	}

}
