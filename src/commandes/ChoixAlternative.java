package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import main.Commande;

/**
 * Une des différentes Alternatives du Choix.
 */
public class ChoixAlternative extends Commande implements CommandeEvent {
	/** Numéro du Choix */
	public int numeroChoix;
	/** Numéro d'Alternative au sein du Choix */
	public int numeroAlternative;
	
	/**
	 * Constructeur explicite
	 * @param numeroChoix numéro identifiant du Choix
	 * @param numeroAlternative numéro de l'Alternative au sein du Choix
	 */
	public ChoixAlternative(final int numeroChoix, final int numeroAlternative) {
		this.numeroChoix = numeroChoix;
		this.numeroAlternative = numeroAlternative;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public ChoixAlternative(final HashMap<String, Object> parametres) {
		this( (int) parametres.get("numero"),
				(int) parametres.get("alternative"));
	}

	/**
	 * Les Alternatives d'un Choix permettent des sauts de curseur dans le code Event.
	 * Leur execution est instantanée.
	 * @param curseurActuel position du curseur avant l'execution
	 * @param commandes liste des Commandes de la Page
	 * @return nouvelle position du curseur
	 */
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		for (int i = 0; i < commandes.size(); i++) {
			final Commande commande = commandes.get(i);
			if (commande instanceof ChoixFin) {
				final ChoixFin finDeChoix = (ChoixFin) commande;
				if (finDeChoix.numero == this.numeroChoix) {
					//la fin de ce Choix a été trouvée
					return i+1;
				}
			}
		}
		//la fin de Boucle n'a pas été trouvée
		System.err.println("La fin du choix numéro "+numeroChoix+" n'a pas été trouvée !");
		return curseurActuel+1;
	}

}
