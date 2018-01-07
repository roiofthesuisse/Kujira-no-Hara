package mouvements;

import java.util.HashMap;

import map.Event;
import map.PageEvent.Traversabilite;

/**
 * Rendre un Event traversable.
 */
public class RendreTraversable extends Mouvement {
	private Traversabilite nouveauTraversable;
	
	/**
	 * Constructeur explicite
	 * @param nouveauTraversable à donner à l'Event
	 */
	public RendreTraversable(final boolean nouveauTraversable) {
		if (nouveauTraversable) {
			this.nouveauTraversable = Traversabilite.TRAVERSABLE;
		} else {
			this.nouveauTraversable = Traversabilite.OBSTACLE;
		}
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public RendreTraversable(final HashMap<String, Object> parametres) {
		this( parametres.containsKey("traversable") ? (boolean) parametres.get("traversable") : true );
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
		event.traversableActuel = this.nouveauTraversable;
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
		return "traversable : "+this.nouveauTraversable;
	}

	@Override
	public final int getDirectionImposee() {
		return -1;
	}

}