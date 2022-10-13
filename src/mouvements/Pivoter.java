package mouvements;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import map.Event;
import map.Event.Direction;
import utilitaire.Maths;

/**
 * Faire pivoter un Event d'un certain angle.
 */
public class Pivoter extends Mouvement {
	//constantes
	private static final Logger LOG = LogManager.getLogger(Pivoter.class);
	
	private int angle;
	protected int direction;
	
	/**
	 * Constructeur explicite
	 * @param angle du pivot
	 */
	public Pivoter(final int angle) {
		this.direction = -1;
		this.angle = angle;
		
		this.etapes = 1;
		this.ceQuiAEteFait = 0;
	}

	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public Pivoter(final HashMap<String, Object> parametres) {
		this((int) parametres.get("angle"));
	}

	@Override
	protected void reinitialiserSpecifique() {
		// Mouvement non sp�cifique
	}

	@Override
	public final boolean mouvementPossible() {
		return true;
	}

	@Override
	protected final void calculDuMouvement(final Event event) {
		// rien car le travail est fait par la Methode getDirectionImposee()
		this.ceQuiAEteFait++;
	}

	@Override
	protected void terminerLeMouvementSpecifique(final Event event) {
		// Mouvement non sp�cifique

	}

	@Override
	protected void ignorerLeMouvementSpecifique(final Event event) {
		// Mouvement non sp�cifique

	}

	@Override
	public final String toString() {
		return "Pivoter d'un angle de " + angle + " degr�s";
	}

	@Override
	public int getDirectionImposee() {
		final Event event = this.deplacement.getEventADeplacer();
		this.direction = event.direction;
		switch (this.angle) {
		case (Maths.ANGLE_DROIT_HORAIRE):
			pivoter90degres();
			break;
		case(Maths.ANGLE_DEMI_TOUR):
			prendreDirectionOpposee();
			break;
		case(Maths.ANGLE_DROIT_ANTIHORAIRE):
			pivoterMoins90degres();
			break;
		default:
			LOG.error("Angle interdit : "+this.angle);
			break;
		}
		return this.direction;
	}

	/**
	 * Inverse la direction du Mouvement
	 */
	private void prendreDirectionOpposee() {
		switch (this.direction) {
		case (Direction.BAS):
			this.direction = Direction.HAUT;
			break;
		case (Direction.HAUT):
			this.direction = Direction.BAS;
			break;
		case (Direction.GAUCHE):
			this.direction = Direction.DROITE;
			break;
		case (Direction.DROITE):
			this.direction = Direction.GAUCHE;
			break;
		default:
			break;
		}
	}

	/**
	 * Donne la direction a +90�
	 */
	protected void pivoter90degres() {
		switch (this.direction) {
		case (Direction.BAS):
			this.direction = Direction.GAUCHE;
			break;
		case (Direction.HAUT):
			this.direction = Direction.DROITE;
			break;
		case (Direction.GAUCHE):
			this.direction = Direction.HAUT;
			break;
		case (Direction.DROITE):
			this.direction = Direction.BAS;
			break;
		default:
			break;
		}
	}

	/**
	 * Donne la direction a -90�
	 */
	protected void pivoterMoins90degres() {
		switch (this.direction) {
		case (Direction.BAS):
			this.direction = Direction.DROITE;
			break;
		case (Direction.HAUT):
			this.direction = Direction.GAUCHE;
			break;
		case (Direction.GAUCHE):
			this.direction = Direction.BAS;
			break;
		case (Direction.DROITE):
			this.direction = Direction.HAUT;
			break;
		default:
			break;
		}
	}

}
