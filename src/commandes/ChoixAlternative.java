package commandes;

import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.Commande;

/**
 * Une des differentes Alternatives du Choix.
 */
public class ChoixAlternative extends Commande implements CommandeEvent {
	private static final Logger LOG = LogManager.getLogger(ChoixAlternative.class);

	/** Num�ro du Choix */
	public int numeroChoix;
	/** Num�ro d'Alternative au sein du Choix */
	public int numeroAlternative;

	/**
	 * Constructeur explicite
	 * 
	 * @param numeroChoix       num�ro identifiant du Choix
	 * @param numeroAlternative num�ro de l'Alternative au sein du Choix
	 */
	public ChoixAlternative(final int numeroChoix, final int numeroAlternative) {
		this.numeroChoix = numeroChoix;
		this.numeroAlternative = numeroAlternative;
	}

	/**
	 * Constructeur generique
	 * 
	 * @param parametres liste de parametres issus de JSON
	 */
	public ChoixAlternative(final HashMap<String, Object> parametres) {
		this((int) parametres.get("numero"), (int) parametres.get("alternative"));
	}

	/**
	 * Les Alternatives d'un Choix permettent des sauts de curseur dans le code
	 * Event. Leur execution est instantan�e.
	 * 
	 * @param curseurActuel position du curseur avant l'execution
	 * @param commandes     liste des Commandes de la Page
	 * @return nouvelle position du curseur
	 */
	@Override
	public final int executer(final int curseurActuel, final List<Commande> commandes) {
		for (int i = 0; i < commandes.size(); i++) {
			final Commande commande = commandes.get(i);
			if (commande instanceof ChoixFin) {
				final ChoixFin finDeChoix = (ChoixFin) commande;
				if (finDeChoix.numero == this.numeroChoix) {
					// la fin de ce Choix a ete trouv�e
					return i + 1;
				}
			}
		}
		// la fin de Boucle n'a pas ete trouv�e
		LOG.error("La fin du choix num�ro " + numeroChoix + " n'a pas ete trouv�e !");
		return curseurActuel + 1;
	}

}
