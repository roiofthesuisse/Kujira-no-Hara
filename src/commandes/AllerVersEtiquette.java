package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.Commande;

/**
 * Une des différentes Alternatives du Choix.
 */
public class AllerVersEtiquette extends Commande implements CommandeEvent {
	private static final Logger LOG = LogManager.getLogger(AllerVersEtiquette.class);
	
	/** Nom de l'Etiquette */
	public String nomEtiquette;
	
	/**
	 * Constructeur explicite
	 * @param nom de l'Etiquette vers laquelle aller
	 */
	public AllerVersEtiquette(final String nomEtiquette) {
		this.nomEtiquette = nomEtiquette;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public AllerVersEtiquette(final HashMap<String, Object> parametres) {
		this( (String) parametres.get("nomEtiquette"));
	}

	/**
	 * Les Alternatives d'un Choix permettent des sauts de curseur dans le code Event.
	 * Leur execution est instantanée.
	 * @param curseurActuel position du curseur avant l'execution
	 * @param commandes liste des Commandes de la Page
	 * @return nouvelle position du curseur
	 */
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		for (int i = 0; i < commandes.size(); i++) {
			final Commande commande = commandes.get(i);
			if (commande instanceof Etiquette) {
				final Etiquette etiquette = (Etiquette) commande;
				if (etiquette.nomEtiquette == this.nomEtiquette) {
					//la fin de ce Choix a été trouvée
					return i+1;
				}
			}
		}
		//la fin de Boucle n'a pas été trouvée
		final String nomEvent = commandes.size()>0 
				&& commandes.get(0).page != null 
				&& commandes.get(0).page.event != null 
				? commandes.get(0).page.event.nom
				: "";
		LOG.error("L'étiquette '"+this.nomEtiquette+"' de l'event '"+nomEvent+"' n'a pas été trouvée !");
		return curseurActuel+1;
	}

}
