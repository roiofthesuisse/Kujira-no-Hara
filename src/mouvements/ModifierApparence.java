package mouvements;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;

import javax.imageio.ImageIO;

import map.Event;

/**
 * <p>Modifier l'apparence d'un Event.</p>
 * <ul>
 * <li>Image non précisée : pas de changement</li>
 * <li>Image inexistante : l'Event devient invisible</li>
 * <li>Mauvaise direction ou animation : pas de changement</li>
 * </ul>
 */
public class ModifierApparence extends Mouvement {
	private String nouvelleImage = null;
	private int nouvelleDirection = -1;
	private int nouvelleAnimation = -1;
	
	/**
	 * Constructeur explicite
	 * @param nomImage à donner comme apparence à l'Event
	 * @param nouvelleDirection à donner comme apparence à l'Event
	 * @param nouvelleAnimation à donner comme apparence à l'Event
	 */
	public ModifierApparence(final String nomImage, final int nouvelleDirection, final int nouvelleAnimation) {
		this.nouvelleImage = nomImage;
		this.nouvelleDirection = nouvelleDirection;
		this.nouvelleAnimation = nouvelleAnimation;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public ModifierApparence(final HashMap<String, Object> parametres) {
		this( parametres.containsKey("image") ? (String) parametres.get("image") : null,
			  parametres.containsKey("direction") ? (int) parametres.get("direction") : -1,
			  parametres.containsKey("animation") ? (int) parametres.get("animation") : -1 
		);
	}
	
	@Override
	protected void reinitialiserSpecifique() {
		//rien
	}

	@Override
	public final boolean mouvementPossible() {
		//toujours possible
		return true;
	}

	@Override
	protected final void calculDuMouvement(final Event event) {
		if (this.nouvelleImage != null) {
			try {
				final BufferedImage image = ImageIO.read(new File(".\\ressources\\Graphics\\Characters\\"+this.nouvelleImage));
				event.imageActuelle = image;
			} catch (Exception e) {
				System.err.println("Impossible d'ouvrir l'image d'apparence : "+this.nouvelleImage);
				event.imageActuelle = null;
			}
		}
		if (this.nouvelleDirection >= 0 && this.nouvelleDirection < Event.NOMBRE_DE_VIGNETTES_PAR_IMAGE) {
			event.direction = this.nouvelleDirection;
		}
		if (this.nouvelleAnimation >= 0 && this.nouvelleAnimation < Event.NOMBRE_DE_VIGNETTES_PAR_IMAGE) {
			event.animation = this.nouvelleAnimation;
		}
	}

	@Override
	protected void terminerLeMouvementSpecifique(final Event event) {
		//rien
	}

	@Override
	protected void ignorerLeMouvementSpecifique(final Event event) {
		//rien
	}

	@Override
	public final String toString() {
		return "nouvelle apparence : "+this.nouvelleImage;
	}

	@Override
	public final int getDirectionImposee() {
		return -1;
	}

}