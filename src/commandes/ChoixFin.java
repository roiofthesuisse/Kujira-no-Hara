package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import main.Commande;

/**
 * Fin du Choix, pour délimiter la dernière Alternative.
 */
public class ChoixFin  extends Commande implements CommandeEvent {
	/** Numéro du Choix */
	public int numero;
	
	/**
	 * Constructeur explicite
	 * @param numero identifiant du Choix
	 */
	public ChoixFin(final int numero) {
		this.numero = numero;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public ChoixFin(final HashMap<String, Object> parametres) {
		this( (int) parametres.get("numero") );
	}
	
	/**
	 * La fin d'un Choix rencontrée, c'est la sortie de la dernière Alternative.
	 * Son execution est instantanée.
	 * @param curseurActuel position du curseur avant l'execution
	 * @param commandes liste des Commandes de la Page
	 * @return nouvelle position du curseur
	 */
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		return curseurActuel+1;
	}

}
