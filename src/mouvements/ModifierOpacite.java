package mouvements;

import java.util.HashMap;
import map.Event;

/**
 * Modifier l'opacit� d'un Event.
 */
public class ModifierOpacite extends Mouvement {
	private int nouvelleOpacite;
	
	/**
	 * Constructeur explicite
	 * @param nouvelleOpacite a donner a l'Event
	 */
	public ModifierOpacite(final int nouvelleOpacite) {
		this.nouvelleOpacite = nouvelleOpacite;
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public ModifierOpacite(final HashMap<String, Object> parametres) {
		this( (int) parametres.get("opacite") );
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
		event.opaciteActuelle = this.nouvelleOpacite;
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
		return "nouvelle opacit� : " + this.nouvelleOpacite;
	}

	@Override
	public final int getDirectionImposee() {
		return -1;
	}

}