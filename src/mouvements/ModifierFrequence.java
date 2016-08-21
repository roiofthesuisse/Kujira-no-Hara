package mouvements;

import java.util.HashMap;

import map.Event;

/**
 * Modifier la frequence actuelle d'un Event.
 */
public class ModifierFrequence extends Mouvement {
	private int nouvelleFrequence;
	
	/**
	 * Constructeur explicite
	 * @param nouvelleFrequence à donner à l'Event
	 */
	public ModifierFrequence(final int nouvelleFrequence) {
		this.nouvelleFrequence = nouvelleFrequence;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public ModifierFrequence(final HashMap<String, Object> parametres) {
		this( (int) parametres.get("frequence") );
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
		event.frequenceActuelle = this.nouvelleFrequence;
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
		return "nouvelle frequence : "+this.nouvelleFrequence;
	}

	@Override
	public final int getDirectionImposee() {
		return -1;
	}

}
