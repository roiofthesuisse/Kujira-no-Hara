package commandes;

import java.util.HashMap;
import java.util.List;

import main.Commande;

/**
 * Fin du Choix, pour d�limiter la derni�re Alternative.
 */
public class ChoixFin extends Commande implements CommandeEvent {
	/** Num�ro du Choix */
	public int numero;

	/**
	 * Constructeur explicite
	 * 
	 * @param numero identifiant du Choix
	 */
	public ChoixFin(final int numero) {
		this.numero = numero;
	}

	/**
	 * Constructeur generique
	 * 
	 * @param parametres liste de parametres issus de JSON
	 */
	public ChoixFin(final HashMap<String, Object> parametres) {
		this((int) parametres.get("numero"));
	}

	/**
	 * La fin d'un Choix rencontr�e, c'est la sortie de la derni�re Alternative. Son
	 * execution est instantan�e.
	 * 
	 * @param curseurActuel position du curseur avant l'execution
	 * @param commandes     liste des Commandes de la Page
	 * @return nouvelle position du curseur
	 */
	@Override
	public final int executer(final int curseurActuel, final List<Commande> commandes) {
		return curseurActuel + 1;
	}

}
