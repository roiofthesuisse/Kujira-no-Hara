package commandes;

import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.Commande;
import map.Event;
import map.Map;

/**
 * Transformer un Event de la Map courante en un Event decrit dans le fichier JSON d'une Map
 */
public class TransformerUnEvent extends Commande implements CommandeEvent {
	private static final Logger LOG = LogManager.getLogger(InvoquerUnEvent.class);

	private final int idEventCible, idMapModele, idEventModele;

	/**
	 * Constructeur explicite
*
	 * @param idEventCible id de l'Event a remplacer
	 * @param idMapModele id de la Map ou se trouve l'Event a imiter
	 * @param idEventModele id de l'Event a imiter
	 */
	public TransformerUnEvent(final int idEventCible, final int idMapModele, final int idEventModele) {
		this.idEventCible = idEventCible;
		this.idMapModele = idMapModele;
		this.idEventModele = idEventModele;
	}

	/**
	 * Constructeur generique
	 * 
	 * @param parametres liste de parametres issus de JSON
	 */
	public TransformerUnEvent(final HashMap<String, Object> parametres) {
		this((int) parametres.get("idEventCible"), (int) parametres.get("idMap"),
				(int) parametres.get("idEventModele"));
	}

	@Override
	public int executer(int curseurActuel, List<Commande> commandes) {
		
		// Initialisation
		Map mapCourante = this.page.event.map;
		Event eventCible = mapCourante.events.get(this.idEventCible);
		int x = eventCible.x;
		int y = eventCible.y;
		Commande supprimerEventCible, invoquerUnEvent;
		supprimerEventCible = new SupprimerEvent(this.idEventCible);
		supprimerEventCible.page = this.page; // on met la Commande au courant de qui est sa Page
		invoquerUnEvent = new InvoquerUnEvent(x, y, this.idMapModele, this.idEventModele);
		invoquerUnEvent.page = this.page; // on met la Commande au courant de qui est sa Page

		// Supprimer l'Event cible
		supprimerEventCible.executer(0, commandes);
		// Invoquer un autre Event a la place
		invoquerUnEvent.executer(0, commandes);

		LOG.info("Transformation de l'event " + this.idEventCible + " en l'event " + this.idEventModele + " de la map "
				+ this.idMapModele);
		return curseurActuel + 1;
	}

}
