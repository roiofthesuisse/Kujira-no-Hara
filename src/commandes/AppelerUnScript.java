package commandes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.Commande;

/**
 * Appeler un script ruby.
 */
public class AppelerUnScript extends Commande implements CommandeEvent, CommandeMenu {
	// constantes
	private static final Logger LOG = LogManager.getLogger(AppelerUnScript.class);
	private static final String ATTENDRE_CET_EVENT = "wait_for_event(@event_id)";
	private static final String ATTENDRE_HEROS = "wait_for_event(0)";

	private final String script;
	private ArrayList<Commande> commandes;
	private int curseur;

	/**
	 * Constructeur explicite
	 * 
	 * @param script à exécuter
	 */
	public AppelerUnScript(final String script) {
		this.script = script;
		this.commandes = null;
		this.curseur = 0;
	}

	/**
	 * Constructeur générique
	 * 
	 * @param parametres liste de paramètres issus de JSON
	 */
	public AppelerUnScript(final HashMap<String, Object> parametres) {
		this((String) parametres.get("script"));
	}

	@Override
	public final int executer(final int curseurActuel, final List<Commande> commandes) {
		if (this.commandes == null) {
			// On n'a pas encore parsé le script en liste de Commandes
			this.commandes = new ArrayList<>();

			// Est-ce que le script est une attente d'Event ?
			Integer idEventAAttendre = null;
			if (ATTENDRE_CET_EVENT.equals(this.script)) {
				// Attendre la fin des déplacements de cet Events
				idEventAAttendre = this.page.event.id;
			} else if (ATTENDRE_HEROS.equals(script)) {
				// Attendre la fin du Déplacement du Héros
				idEventAAttendre = this.page.event.map.heros.id;
			}
			if (idEventAAttendre != null) {
				// Attendre l'Event
				final AttendreLaFinDesDeplacements attendreEvent = new AttendreLaFinDesDeplacements(idEventAAttendre);
				attendreEvent.page = this.page; // On dit à la commande qui est son Event
				this.commandes.add(attendreEvent);
			}

			// TODO restent à parser :
			// $game_map.events[@event_id].x == $game_variables[181] &&
			// $game_map.events[@event_id].y == $game_variables[182]

			// invoquer($game_map.events[1].x, $game_map.events[1].y, 21, 54)

			// $game_map.events[@event_id].transform(511, 6)

			// event = invoquer(15, 13, 457, 7)\nevent.jump($game_player.x-event.x,
			// $game_player.y-event.y)

			// $game_map.events[@event_id].life -= 1

			// $game_temp.animations.push([23, true, $game_map.events[@event_id].x,
			// $game_map.events[@event_id].y])

			// x = y = 0\ncase $game_player.direction\nwhen 2; y =
			// $game_map.events[@event_id].y - $game_player.y - 1\nwhen 4; x =
			// $game_map.events[@event_id].x - $game_player.x + 1\nwhen 6; x =
			// $game_map.events[@event_id].x - $game_player.x - 1\nwhen 8; y =
			// $game_map.events[@event_id].y - $game_player.y + 1\nend\n$game_player.jump(x,
			// y)
			this.curseur = 0;
		}
		if (this.commandes != null) {
			// On déjà parsé le script en liste de Commandes
			// On continue d'executer les Commandes de la liste
			if (this.curseur < this.commandes.size()) {
				// Il y a encore des Commandes à lire
				this.curseur = this.commandes.get(curseur).executer(this.curseur, this.commandes);
				return curseurActuel;
			} else {
				// Il n'y a plus de Commandes à lire
				this.curseur = 0;
				return curseurActuel + 1;
			}
		} else {
			// Impossible de parser le script !
			LOG.error("L'appel de ce script n'est pas encore implémenté !");
			return curseurActuel + 1;
		}
	}

	@Override
	public final String toString() {
		return "AppelerUnScript : " + script;
	}

}
