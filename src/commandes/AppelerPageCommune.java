package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.Commande;
import main.Fenetre;
import map.PageCommune;

/**
 * Appeler une Page de code Commun.
 */
public class AppelerPageCommune extends Commande implements CommandeEvent, CommandeMenu {
	private static final Logger LOG = LogManager.getLogger(AppelerPageCommune.class);
	
	private final Integer numeroPageCommune;
	private PageCommune pageCommune = null;
	
	/**
	 * Constructeur explicite
	 * @param numeroPageCommune numéro de la Page à appeler
	 */
	public AppelerPageCommune(final int numeroPageCommune) {
		this.numeroPageCommune = numeroPageCommune;
		final HashMap<Integer, PageCommune> pagesCommunes = Fenetre.getFenetre().lecteur.pagesCommunes;
		if (pagesCommunes.containsKey(this.numeroPageCommune)) {
			this.pageCommune = pagesCommunes.get(this.numeroPageCommune);
		} else {
			LOG.warn("Page commune "+numeroPageCommune+" introuvable !");
		}
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public AppelerPageCommune(final HashMap<String, Object> parametres) {
		this(
				(int) parametres.get("numeroPageCommune")
		);
	}

	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		// La page commune est-elle introuvable ?
		if (this.pageCommune == null) {
			LOG.warn("Page commune "+this.numeroPageCommune+" introuvable !");
			return curseurActuel+1;
		}
		
		// On apprend à la page commune qui est son event
		this.pageCommune.event = this.page.event;
		
		// Exécution
		LOG.info("Exécution de la page commune "+this.numeroPageCommune+" "+this.pageCommune.nom);
		this.pageCommune.executer();
		
		// La page a-t-elle été exécutée en entier ?
		if (this.pageCommune.curseurCommandes == 0) {
			//fini
			return curseurActuel+1;
		} else {
			//pas fini
			return curseurActuel;
		}
	}

}
