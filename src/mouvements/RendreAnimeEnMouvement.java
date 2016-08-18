package mouvements;

import java.util.HashMap;

import map.Event;

/**
 * Rendre un Event animé en Mouvement.
 */
public class RendreAnimeEnMouvement extends Mouvement {
	private boolean nouveauAnimeEnMouvement;
	
	/**
	 * Constructeur explicite
	 * @param nouveauAnimeEnMouvement à donner à l'Event
	 */
	public RendreAnimeEnMouvement(final boolean nouveauAnimeEnMouvement) {
		this.nouveauAnimeEnMouvement = nouveauAnimeEnMouvement;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public RendreAnimeEnMouvement(final HashMap<String, Object> parametres) {
		this( parametres.containsKey("animeEnMouvement") ? (boolean) parametres.get("animeEnMouvement") : true );
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
		event.animeEnMouvementActuel = this.nouveauAnimeEnMouvement;
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
		return "animé en mouvement : "+this.nouveauAnimeEnMouvement;
	}

	@Override
	public final int getDirectionImposee() {
		return -1;
	}

}
