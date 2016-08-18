package mouvements;

import java.util.HashMap;

import map.Event;

/**
 * Rendre l'Event plat (au sol).
 */
public class RendrePlat extends Mouvement {
	private boolean nouveauPlat;
	
	/**
	 * Constructeur explicite
	 * @param nouveauPlat à donner à l'Event
	 */
	public RendrePlat(final boolean nouveauPlat) {
		this.nouveauPlat = nouveauPlat;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public RendrePlat(final HashMap<String, Object> parametres) {
		this( parametres.containsKey("plat") ? (boolean) parametres.get("plat") : true );
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
		event.platActuel = this.nouveauPlat;
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
		return "plat : "+this.nouveauPlat;
	}

	@Override
	public final int getDirectionImposee() {
		return -1;
	}

}