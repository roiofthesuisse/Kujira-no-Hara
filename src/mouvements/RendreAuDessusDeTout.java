package mouvements;

import java.util.HashMap;

import map.Event;

/**
 * Mettre un Event au dessus de tout ou non.
 */
public class RendreAuDessusDeTout extends Mouvement {
	private boolean nouveauAuDessusDeTout;
	
	/**
	 * Constructeur explicite
	 * @param nouveauAuDessusDeTout a donner a l'Event
	 */
	public RendreAuDessusDeTout(final boolean nouveauAuDessusDeTout) {
		this.nouveauAuDessusDeTout = nouveauAuDessusDeTout;
	}
	
	/**
	 * Constructeur generique
	 * @param parametres liste de parametres issus de JSON
	 */
	public RendreAuDessusDeTout(final HashMap<String, Object> parametres) {
		this( parametres.containsKey("auDessusDeTout") ? (boolean) parametres.get("auDessusDeTout") : true );
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
		event.auDessusDeToutActuel = this.nouveauAuDessusDeTout;
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
		return "au dessus de tout : "+this.nouveauAuDessusDeTout;
	}

	@Override
	public final int getDirectionImposee() {
		return -1;
	}

}
