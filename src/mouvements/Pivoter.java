package mouvements;

import java.util.HashMap;

import map.Event;
import map.Event.Direction;

/**
 * Faire pivoter un Event d'un certain angle.
 */
public class Pivoter extends Mouvement {
	//constantes
	private static final int ANGLE_DROIT_HORAIRE = 90;
	private static final int ANGLE_DROIT_ANTIHORAIRE = -90;
	private static final int ANGLE_DEMI_TOUR = 180;
	
	private int angle;
	private int direction;
	
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
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public Pivoter(final HashMap<String, Object> parametres) {
		this((int) parametres.get("angle"));
	}

	@Override
	protected void reinitialiserSpecifique() {
		// Mouvement non spécifique
	}

	@Override
	public final boolean mouvementPossible() {
		return true;
	}

	@Override
	protected final void calculDuMouvement(final Event event) {
		// rien car le travail est fait par la méthode getDirectionImposee()
		this.ceQuiAEteFait++;
	}

	@Override
	protected void terminerLeMouvementSpecifique(final Event event) {
		// Mouvement non spécifique

	}

	@Override
	protected void ignorerLeMouvementSpecifique(final Event event) {
		// Mouvement non spécifique

	}

	@Override
	public final String toString() {
		return "Pivoter d'un angle de " + angle + " degrés";
	}

	@Override
	public final int getDirectionImposee() {
		final Event event = this.deplacement.getEventADeplacer();
		this.direction = event.direction;
		switch (this.angle) {
		case (ANGLE_DROIT_HORAIRE):
			pivoter90degres();
			break;
		case(ANGLE_DEMI_TOUR):
			prendreDirectionOpposee();
			break;
		case(ANGLE_DROIT_ANTIHORAIRE):
			pivoterMoins90degres();
			break;
		default:
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
	 * Donne la direction à +90°
	 */
	private void pivoter90degres() {
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
	 * Donne la direction à -90°
	 */
	private void pivoterMoins90degres() {
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
