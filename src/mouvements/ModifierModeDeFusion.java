package mouvements;

import java.util.HashMap;

import map.Event;
import utilitaire.graphismes.ModeDeFusion;

/**
 * Modifier le mode de fusion d'un Event.
 */
public class ModifierModeDeFusion extends Mouvement {
	private ModeDeFusion nouveauModeDeFusion;
	
	/**
	 * Constructeur explicite
	 * @param nouveauModeDeFusion a donner a l'Event
	 */
	public ModifierModeDeFusion(final ModeDeFusion nouveauModeDeFusion) {
		this.nouveauModeDeFusion = nouveauModeDeFusion;
	}
	
	/**
	 * Constructeur generique
	 * @param parametres liste de parametres issus de JSON
	 */
	public ModifierModeDeFusion(final HashMap<String, Object> parametres) {
		this( ModeDeFusion.parNom((String) parametres.get("modeDeFusion")) );
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
		event.modeDeFusionActuel = this.nouveauModeDeFusion;
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
		return "nouveau mode de fusion : " + this.nouveauModeDeFusion;
	}

	@Override
	public final int getDirectionImposee() {
		return -1;
	}

}