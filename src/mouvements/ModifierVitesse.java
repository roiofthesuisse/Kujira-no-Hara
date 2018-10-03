package mouvements;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import map.Event;
import map.Vitesse;

/**
 * Modifier la vitesse actuelle d'un Event.
 */
public class ModifierVitesse extends Mouvement {
	private static final Logger LOG = LogManager.getLogger(ModifierVitesse.class);
	
	private String nomNouvelleVitesse;
	private Vitesse nouvelleVitesse = null;
	
	/**
	 * Constructeur explicite
	 * @param nomNouvelleVitesse nom de la nouvelle vitesse à donner à l'Event
	 */
	public ModifierVitesse(final String nomNouvelleVitesse) {
		this.nomNouvelleVitesse = nomNouvelleVitesse;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public ModifierVitesse(final HashMap<String, Object> parametres) {
		this( (String) parametres.get("vitesse") );
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
		if (this.nouvelleVitesse == null) {
			// La Vitesse n'a pas encore été interprétée
			
			// On l'interprète
			this.nouvelleVitesse = Vitesse.parNom(this.nomNouvelleVitesse);
			
			if (this.nouvelleVitesse == null) {
				// La Vitesse n'a pas pu être interprétée !
				LOG.error("Nom de vitesse inconnu : "+this.nomNouvelleVitesse);
				// On ne fera rien
				this.nouvelleVitesse = event.vitesseActuelle;
			}
		}
		// La nouvelle Vitesse a été interprétée
		
		// On assigne la nouvelle Vitesse
		event.vitesseActuelle = this.nouvelleVitesse;
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
		return "nouvelle vitesse : " + this.nomNouvelleVitesse;
	}

	@Override
	public final int getDirectionImposee() {
		return -1;
	}

}
