package commandes;

import java.util.HashMap;

import map.Deplacement;
import map.Event;

/**
 * Attendre un certain nombre de frames avant d'executer la Commande suivante
 */
public class Attendre extends Mouvement implements CommandeEvent {
	
	/**
	 * Constructeur explicite
	 * @param nombreDeFrames qu'il faut attendre
	 */
	public Attendre(final Integer idEventADeplacer, final int nombreDeFrames) {
		this.idEventADeplacer = idEventADeplacer;
		this.ceQuiAEteFait = 0;
		this.etapes = nombreDeFrames;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public Attendre(final HashMap<String, Object> parametres) {
		this( null, //l'Event qui attend est toujours celui qui appelle la commande Attendre
				(Integer) parametres.get("nombreDeFrames") );
	}

	@Override
	public final boolean mouvementPossible() {
		return true;
	}

	@Override
	protected final void calculDuMouvement(final Event event) {
		//une frame s'écoule
		this.ceQuiAEteFait++;
	}

	@Override
	protected void terminerLeMouvementSpecifique(final Event event, final Deplacement deplacement) {
		//rien
	}

	@Override
	protected void ignorerLeMouvementSpecifique(final Event event, final Deplacement deplacement) {
		//rien
	}

	@Override
	protected void reinitialiserSpecifique() {
		// rien
	}
	
}
