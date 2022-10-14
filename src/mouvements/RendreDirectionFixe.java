package mouvements;

import java.util.HashMap;

import map.Event;

/**
 * Figer un Event dans la direction qu'il regarde.
 */
public class RendreDirectionFixe extends Mouvement {
	private boolean nouvelleDirectionFixe;
	
	/**
	 * Constructeur explicite
	 * @param nouvelleDirectionFixe a donner a l'Event
	 */
	public RendreDirectionFixe(final boolean nouvelleDirectionFixe) {
		this.nouvelleDirectionFixe = nouvelleDirectionFixe;
	}
	
	/**
	 * Constructeur generique
	 * @param parametres liste de parametres issus de JSON
	 */
	public RendreDirectionFixe(final HashMap<String, Object> parametres) {
		this( parametres.containsKey("directionFixe") ? (boolean) parametres.get("directionFixe") : true );
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
		event.directionFixeActuelle = this.nouvelleDirectionFixe;
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
		return "direction fixe : "+this.nouvelleDirectionFixe;
	}

	@Override
	public final int getDirectionImposee() {
		return -1;
	}

}