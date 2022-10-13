package commandes;

import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.Commande;
import main.Lecteur;
import main.Main;
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
	 * 
	 * @param numeroPageCommune num�ro de la Page a appeler
	 */
	public AppelerPageCommune(final int numeroPageCommune) {
		this.numeroPageCommune = numeroPageCommune;
		
		final HashMap<Integer, PageCommune> pagesCommunes = PageCommune.PAGES_COMMUNES;
		if (pagesCommunes.containsKey(this.numeroPageCommune)) {
			this.pageCommune = pagesCommunes.get(this.numeroPageCommune);
		} else {
			LOG.warn("Page commune " + numeroPageCommune + " introuvable !");
		}
	}

	/**
	 * Constructeur g�n�rique
	 * 
	 * @param parametres liste de param�tres issus de JSON
	 */
	public AppelerPageCommune(final HashMap<String, Object> parametres) {
		this((int) parametres.get("numeroPageCommune"));
	}

	@Override
	public final int executer(final int curseurActuel, final List<Commande> commandes) {
		// La page commune est-elle introuvable ?
		if (this.pageCommune == null) {
			LOG.warn("Page commune " + this.numeroPageCommune + " introuvable !");
			return curseurActuel + 1;
		}

		// On apprend a la page commune qui est son event
		this.pageCommune.event = this.page.event;

		// Ex�cution
		LOG.info("Ex�cution de la page commune " + this.numeroPageCommune + " " + this.pageCommune.nom);
		this.pageCommune.executer();

		// La page a-t-elle �t� ex�cut�e en entier ?
		if (this.pageCommune.curseurCommandes == 0) {
			// fini
			return curseurActuel + 1;
		} else {
			// pas fini
			return curseurActuel;
		}
	}

}
