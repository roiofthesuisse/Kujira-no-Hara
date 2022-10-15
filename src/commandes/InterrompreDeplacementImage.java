package commandes;

import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.Commande;
import map.Picture;

/**
 * Figer une Image en supprimant son Deplacement.
 */
public class InterrompreDeplacementImage extends Commande implements CommandeEvent {
	protected static final Logger LOG = LogManager.getLogger(InterrompreDeplacementImage.class);

	/** num�ro de l'image a d�placer */
	private Integer numero; // Integer car utilis� comme cl� d'une HashMap

	/**
	 * Constructeur explicite
	 * 
	 * @param numero de l'image a stopper
	 */
	private InterrompreDeplacementImage(final int numero) {
		this.numero = numero;
	}

	/**
	 * Constructeur generique
	 * 
	 * @param parametres liste de parametres issus de JSON
	 */
	public InterrompreDeplacementImage(final HashMap<String, Object> parametres) {
		this((int) parametres.get("numero"));
	}

	@Override
	public final int executer(final int curseurActuel, final List<Commande> commandes) {
		final Picture picture = getPartieActuelle().images[this.numero];
		if (picture != null) {
			// On interrompt le deplacement de la Picture
			picture.deplacementActuel = null;
		} else {
			LOG.warn("On interrompt le deplacement d'une Picture qui n'existe pas ! Picture numero " + this.numero);
		}
		return curseurActuel + 1;
	}

}
