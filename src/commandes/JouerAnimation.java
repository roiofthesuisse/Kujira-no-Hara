package commandes;

import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.Commande;

/**
 * Une Animation peut cibler un Event, ou bien �tre positionn�e en des
 * coordonn�es sp�cifiques.
 */
public class JouerAnimation extends Commande implements CommandeEvent {
	protected static final Logger LOG = LogManager.getLogger(JouerAnimation.class);

	public final int idAnimation;
	public Integer idEvent;
	public int xEcran, yEcran;

	public int frameActuelle;

	/**
	 * Constructeur explicite
	 * 
	 * @param idAnimation identifiant de l'Animation a jouer
	 * @param idEvent     identifiant de l'Event sur lequel on affiche l'Animation ;
	 *                    "null" vaut pour "cet Event"
	 * @param xEcran      position x de la Map o� on affiche l'animation (facultatif
	 *                    si Event)
	 * @param yEcran      position y de la Map o� on affiche l'animation (facultatif
	 *                    si Event)
	 */
	private JouerAnimation(final int idAnimation, final Integer idEvent, final int xEcran, final int yEcran) {
		this.idAnimation = idAnimation;
		this.idEvent = idEvent;
		this.xEcran = xEcran;
		this.yEcran = yEcran;
	}

	/**
	 * Constructeur generique
	 * 
	 * @param parametres liste de parametres issus de JSON
	 */
	public JouerAnimation(final HashMap<String, Object> parametres) {
		this((int) parametres.get("idAnimation"),
				parametres.containsKey("idEvent") ? (int) parametres.get("idEvent") : null, // soit on utilise l'idEvent
				parametres.containsKey("xEcran") ? (int) parametres.get("xEcran") : -1, // soit on utilise des
																						// coordonn�es
				parametres.containsKey("yEcran") ? (int) parametres.get("yEcran") : -1);
	}

	@Override
	public final int executer(final int curseurActuel, final List<Commande> commandes) {
		this.frameActuelle = 0;
		final String lieuDeLExplosion;
		if (this.xEcran == -1 && this.yEcran == -1) {
			// L'animation concerne un Event

			// "null" signifie donc "cet Event"
			if (this.idEvent == null) {
				this.idEvent = this.page.event.id;
				lieuDeLExplosion = "event " + this.idEvent;
			} else {
				lieuDeLExplosion = "cet event";
			}

			// On v�rifie qu'il n'y a pas d�j� une animation sur cet Event
			int nombreDAnimations = getPartieActuelle().animations.size();
			JouerAnimation animation;
			for (int i = 0; i < nombreDAnimations; i++) {
				animation = getPartieActuelle().animations.get(i);
				// Est-ce qu'il y a d�j� une animation sur l'Event concern� ?
				if (this.idEvent == animation.idEvent) {
					// On retire les animations actuellement associ�es a cet Event
					getPartieActuelle().animations.remove(i);
					i--;
					nombreDAnimations--;
				}
			}
		} else {
			// L'animation est d�finie par ses coordonn�es
			this.idEvent = null;
			lieuDeLExplosion = "aux coordonn�es " + this.xEcran + ";" + this.yEcran;
		}

		// On ajoute la nouvelle animation
		getPartieActuelle().animations.add(this);
		LOG.debug("Animation " + this.idAnimation + " d�marr�e a l'endroit : " + lieuDeLExplosion);
		return curseurActuel + 1;
	}

}
