package mouvements;

import java.util.HashMap;

import commandes.CommandeEvent;
import map.Event;

/**
 * Attendre un certain nombre de frames avant d'executer la Commande suivante.
 */
public class Attendre extends Mouvement implements CommandeEvent {
	
	/**
	 * Constructeur explicite
	 * @param nombreDeFrames qu'il faut attendre
	 */
	public Attendre(final int nombreDeFrames) {
		this.etapes = nombreDeFrames;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public Attendre(final HashMap<String, Object> parametres) {
		this( (Integer) parametres.get("nombreDeFrames") );
	}

	@Override
	public final boolean mouvementPossible() {
		return true;
	}

	/** 
	 * Applique l'effet du Mouvement sur la Map et les Events.
	 * Puis incrémente le compteur "ceQuiAEteFait".
	 * @param event subissant le Mouvement
	 */
	@Override
	protected final void calculDuMouvement(final Event event) {
		//une frame s'écoule
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
	protected void reinitialiserSpecifique() {
		//rien
	}
	
	@Override
	public final int getDirectionImposee() {
		return -1;
	}
	
	@Override
	public final String toString() {
		return "Attendre "+this.etapes+" frames";
	}
	
}
