package commandes;

import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.Commande;
import map.Picture;

/**
 * Figer une Image en supprimant son Déplacement.
 */
public class InterrompreDeplacementImage extends Commande implements CommandeEvent {
	protected static final Logger LOG = LogManager.getLogger(InterrompreDeplacementImage.class);

	/** numéro de l'image à déplacer */
	private Integer numero; // Integer car utilisé comme clé d'une HashMap

	/**
	 * Constructeur explicite
	 * 
	 * @param numero de l'image à stopper
	 */
	private InterrompreDeplacementImage(final int numero) {
		this.numero = numero;
	}

	/**
	 * Constructeur générique
	 * 
	 * @param parametres liste de paramètres issus de JSON
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
