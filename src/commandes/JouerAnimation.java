package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.Commande;
import main.Fenetre;

/**
 * Une Animation peut cibler un Event, ou bien être positionnée en des coordonnées spécifiques.
 */
public class JouerAnimation extends Commande implements CommandeEvent {
	protected static final Logger LOG = LogManager.getLogger(JouerAnimation.class);
	
	public final int idAnimation;
	public final Integer idEvent;
	public int xEcran, yEcran;
	
	public int frameActuelle;
	
	/**
	 * Constructeur explicite
	 * @param idAnimation identifiant de l'Animation à jouer
	 * @param idEvent identifiant de l'Event sur lequel on affiche l'Animation ; "null" vaut pour "cet Event"
	 * @param xEcran position x de la Map où on affiche l'animation (facultatif si Event)
	 * @param yEcran position y de la Map où on affiche l'animation (facultatif si Event)
	 */
	private JouerAnimation(final int idAnimation, final Integer idEvent, final int xEcran, final int yEcran) {
		this.idAnimation = idAnimation;
		this.idEvent = idEvent;
		this.xEcran = xEcran;
		this.yEcran = yEcran;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public JouerAnimation(final HashMap<String, Object> parametres) {
		this( 	(int) parametres.get("idAnimation"),
				parametres.containsKey("idEvent") ? (int) parametres.get("idEvent") : null, //soit on utilise l'idEvent
				parametres.containsKey("xEcran") ? (int) parametres.get("xEcran") : -1, //soit on utilise des coordonnées
				parametres.containsKey("yEcran") ? (int) parametres.get("yEcran") : -1
		);
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		this.frameActuelle = 0;
		Fenetre.getPartieActuelle().animations.add(this);
		LOG.info("Animation "+this.idAnimation+" ajoutée à la file");
		return curseurActuel+1;
	}

}
