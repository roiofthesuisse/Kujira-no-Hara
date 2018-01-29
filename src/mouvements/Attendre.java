package mouvements;

import java.util.HashMap;

import map.Event;

/**
 * Attendre au cours d'un Déplacement.
 */
public class Attendre extends Mouvement {
	
	/**
	 * Constructeur explicite
	 * @param nombreDeFrames à Attendre avant le prochain Mouvement
	 */
	public Attendre(final int nombreDeFrames) {
		this.etapes = nombreDeFrames;
		this.ceQuiAEteFait = 0;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public Attendre(final HashMap<String, Object> parametres) {
		this((int) parametres.get("nombreDeFrames"));
	}
	
	@Override
	protected void reinitialiserSpecifique() {
		//rien
	}

	@Override
	public final boolean mouvementPossible() {
		return true;
	}

	@Override
	protected final void calculDuMouvement(final Event event) {
		this.ceQuiAEteFait++;
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
		return "Attendre "+this.etapes+" frames dans le Déplacement de "+this.deplacement.getEventADeplacer().nom;
	}

	@Override
	public final int getDirectionImposee() {
		return -1;
	}

}
