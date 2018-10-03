package mouvements;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import map.Event;
import map.Frequence;

/**
 * Modifier la frequence actuelle d'un Event.
 */
public class ModifierFrequence extends Mouvement {
	private static final Logger LOG = LogManager.getLogger(ModifierFrequence.class);
	
	private String nomNouvelleFrequence;
	private Frequence nouvelleFrequence = null;
	
	/**
	 * Constructeur explicite
	 * @param nomNouvelleFrequence nom de la nouvelle Fréquence à donner à l'Event
	 */
	public ModifierFrequence(final String nomNouvelleFrequence) {
		this.nomNouvelleFrequence = nomNouvelleFrequence;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public ModifierFrequence(final HashMap<String, Object> parametres) {
		this( (String) parametres.get("frequence") );
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
		if (this.nouvelleFrequence == null) {
			// La Fréquence n'a pas encore été interprétée
			
			// On l'interprète
			this.nouvelleFrequence = Frequence.parNom(this.nomNouvelleFrequence);
			
			if (this.nouvelleFrequence == null) {
				// La Fréquence n'a pas pu être interprétée !
				LOG.error("Nom de fréquence inconnu : "+this.nomNouvelleFrequence);
				// On ne fera rien
				this.nouvelleFrequence = event.frequenceActuelle;
			}
		}
		// La nouvelle Fréquence a été interprétée
		
		// On assigne la nouvelle Fréquence
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
		return "nouvelle frequence : "+this.nomNouvelleFrequence;
	}

	@Override
	public final int getDirectionImposee() {
		return -1;
	}

}
