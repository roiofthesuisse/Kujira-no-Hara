package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import main.Commande;

/**
 * Une des différentes Alternatives du Choix.
 */
public class AllerVersEtiquette extends Commande implements CommandeEvent {
	/** Nom de l'Etiquette */
	public String nom;
	
	/**
	 * Constructeur explicite
	 * @param nom de l'Etiquette vers laquelle aller
	 */
	public AllerVersEtiquette(final String nom) {
		this.nom = nom;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public AllerVersEtiquette(final HashMap<String, Object> parametres) {
		this( (String) parametres.get("nom"));
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
			if (commande instanceof Etiquette) {
				final Etiquette etiquette = (Etiquette) commande;
				if (etiquette.nom == this.nom) {
					//la fin de ce Choix a été trouvée
					return i+1;
				}
			}
		}
		//la fin de Boucle n'a pas été trouvée
		System.err.println("L'étiquette "+nom+" n'a pas été trouvée !");
		return curseurActuel+1;
	}

}
