package commandes;

import java.util.HashMap;
import java.util.List;

import main.Commande;

/**
 * Une Etiquette vers laquelle on peut déplacer le curseur.
 */
public class Etiquette extends Commande implements CommandeEvent {
	/** Nom de l'Etiquette */
	public String nomEtiquette;

	/**
	 * Constructeur explicite
	 * 
	 * @param nom identifiant de l'Etiquette
	 */
	public Etiquette(final String nomEtiquette) {
		this.nomEtiquette = nomEtiquette;
	}

	/**
	 * Constructeur générique
	 * 
	 * @param parametres liste de paramètres issus de JSON
	 */
	public Etiquette(final HashMap<String, Object> parametres) {
		this((String) parametres.get("nomEtiquette"));
	}

	/**
	 * L'Etiquette en soi n'a aucun effet, ce n'est qu'un point de repère. Son
	 * execution est instantanée.
	 * 
	 * @param curseurActuel position du curseur avant l'execution
	 * @param commandes     liste des Commandes de la Page
	 * @return nouvelle position du curseur
	 */
	public final int executer(final int curseurActuel, final List<Commande> commandes) {
		return curseurActuel + 1;
	}

}
