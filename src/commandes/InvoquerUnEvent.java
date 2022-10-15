package commandes;

import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import main.Commande;
import main.Main;
import map.Event;
import map.Map;
import utilitaire.InterpreteurDeJson;

/**
 * Invoquer un Event sur la Map courante a partir du fichier JSON d'une Map
 */
public class InvoquerUnEvent extends Commande implements CommandeEvent {
	private static final Logger LOG = LogManager.getLogger(InvoquerUnEvent.class);

	private final int idMap, idEvent, x, y;

	/**
	 * Constructeur explicite
	 * 
	 * @param x       coordonnee (case) ou placer la copie
	 * @param y       coordonnee (case) ou placer la copie
	 * @param idMap   id de la Map ou se trouve l'Event a imiter
	 * @param idEvent id de l'Event a imiter
	 */
	public InvoquerUnEvent(final int x, final int y, final int idMap, final int idEvent) {
		this.idMap = idMap;
		this.idEvent = idEvent;
		this.x = x;
		this.y = y;
	}

	/**
	 * Constructeur generique
	 * 
	 * @param parametres liste de parametres issus de JSON
	 */
	public InvoquerUnEvent(final HashMap<String, Object> parametres) {
		this((int) parametres.get("x"), (int) parametres.get("y"), (int) parametres.get("idMap"),
				(int) parametres.get("idEvent"));
	}

	@Override
	public int executer(int curseurActuel, List<Commande> commandes) {
		Map mapCourante = this.page.event.map;
		// Cr�er juste un Event a partir du JSON de la Map
		// (pas la peine de cr�er toute la Map)
		try {
			// Ouvrir le fichier JSON de la Map Ou se trouve l'Event a invoquer
			JSONObject jsonMap = InterpreteurDeJson.ouvrirJsonMap(this.idMap);
			// Chercher l'Event dans le fichier JSON de la Map
			final JSONArray jsonEvents = jsonMap.getJSONArray("events");
			Event eventInvoque = null;
			onChercheLEventNumerote: for (Object ev : jsonEvents) {
				JSONObject jsonEvent = (JSONObject) ev;
				if (jsonEvent.getInt("id") == this.idEvent) {
					// Instancier l'Event a invoquer
					eventInvoque = Event.recupererUnEvent(jsonEvent, mapCourante);
					break onChercheLEventNumerote;
				}
			}
			if (eventInvoque != null) {
				// L'Event a ete instanci� avec succ�s

				// Le placer sur la Map courante
				eventInvoque.x = this.x * Main.TAILLE_D_UN_CARREAU;
				eventInvoque.y = this.y * Main.TAILLE_D_UN_CARREAU;
				// Il sera ajout� a la Map courante a la prochaine frame
				mapCourante.eventsAAjouter.add(eventInvoque);
				LOG.info("Invocation de l'event " + this.idEvent + " originaire de la map " + this.idMap);

			} else {
				// L'event n'a pas ete trouv�
				LOG.error("Impossible d'invoquer l'event " + this.idEvent
						+ ", il n'a pas ete trouv� dans le JSON de la map " + this.idMap);
			}
		} catch (Exception e) {
			LOG.error("Impossible d'ouvrir le fichier JSON de la map " + this.idMap, e);
		}
		return curseurActuel + 1;
	}

}
