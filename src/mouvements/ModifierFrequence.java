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
	 * @param nomNouvelleFrequence nom de la nouvelle Fr�quence a donner a l'Event
	 */
	public ModifierFrequence(final String nomNouvelleFrequence) {
		this.nomNouvelleFrequence = nomNouvelleFrequence;
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
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
			// La Fr�quence n'a pas encore �t� interpr�t�e
			
			// On l'interpr�te
			this.nouvelleFrequence = Frequence.parNom(this.nomNouvelleFrequence);
			
			if (this.nouvelleFrequence == null) {
				// La Fr�quence n'a pas pu �tre interpr�t�e !
				LOG.error("Nom de fr�quence inconnu : "+this.nomNouvelleFrequence);
				// On ne fera rien
				this.nouvelleFrequence = event.frequenceActuelle;
			}
		}
		// La nouvelle Fr�quence a �t� interpr�t�e
		
		// On assigne la nouvelle Fr�quence
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
