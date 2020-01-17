package commandes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.Commande;

/**
 * Appeler un script ruby.
 */
public class AppelerUnScript extends Commande implements CommandeEvent, CommandeMenu {
	// constantes
	private static final Logger LOG = LogManager.getLogger(AppelerUnScript.class);
	private static final String ESPACE = "(\\s)*";
	private static final String EVENT_ID = "@event_id";
	private static final String ATTENDRE_CET_EVENT = "wait_for_event(@event_id)";
	private static final String ATTENDRE_HEROS = "wait_for_event(0)";
	private static final String POSITIF = "[0-9]+";
	private static final String ATTENDRE_UN_EVENT = "wait_for_event\\(" + POSITIF + "\\)";
	private static final String INVOQUER = "invoquer\\(" + ESPACE + POSITIF + ESPACE + "," + ESPACE + POSITIF + ESPACE
			+ "," + ESPACE + POSITIF + ESPACE + "," + ESPACE + POSITIF + ESPACE + "\\)";
	private static final String COORD_X_EVENT = "$game_map\\.events\\[" + POSITIF + "\\]\\.x";
	private static final String COORD_Y_EVENT = "$game_map\\.events\\[" + POSITIF + "\\]\\.y";

	private final String script;
	private ArrayList<Commande> commandes;
	private int curseur;
	public boolean interpretationImplementee;

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
	public final int executer(final int curseurActuel, final List<Commande> commandesEvent) {
		// Initialisation
		if (this.commandes == null) {
			// On n'a pas encore parsé le script en liste de Commandes
			this.commandes = new ArrayList<>();

			// Convertir toutes les expressions du script en Commandes
			String[] expressions = this.script.split("/n");
			for (String expression : expressions) {
				Commande commande = traiter(expression);
				if (commande != null) {
					this.commandes.add(commande);
				}
			}

			this.curseur = 0;
		}

		// Executer les Commandes equivalentes au script ruby
		if (this.commandes != null && this.commandes.size() > 0) {
			// On déjà parsé le script en liste de Commandes
			// On continue d'executer les Commandes de la liste
			if (this.curseur < this.commandes.size()) {
				// Il y a encore des Commandes à lire
				this.curseur = this.commandes.get(curseur).executer(this.curseur, this.commandes);
				return curseurActuel;
			} else {
				// Il n'y a plus de Commandes à lire
				this.curseur = 0;
				this.interpretationImplementee = true;
				return curseurActuel + 1;
			}
		} else {
			// Impossible de parser le script !
			LOG.error("L'appel de ce script n'est pas encore implémenté !");
			this.interpretationImplementee = false;
			return curseurActuel + 1;
		}
	}

	/**
	 * Interpréter un script ruby.
	 * 
	 * @param expression (en ruby)
	 * @return une chaine de caractère qui est un nombre lorsque l'interprétation
	 *         est terminée.
	 */
	private Commande traiter(String expression) {

		// ------------- //
		// Remplacements //
		// ------------- //

		// Trim
		if (expression.startsWith(" ") || expression.endsWith(" ")) {
			System.out.println("trim");
			return traiter(expression.trim());
		}

		// Coordonnée x et y du Heros
		if (expression.contains("$game_player.y") || expression.contains("$game_player.x")) {
			System.out.println("coordonnée x et y du Heros");
			Integer xHeros = this.page.event.map.heros.x;
			Integer yHeros = this.page.event.map.heros.y;
			return traiter(expression.replace("$game_player.y", yHeros.toString()).replace("$game_player.x",
					xHeros.toString()));
		}

		// Id event
		if (expression.contains(EVENT_ID)) {
			String eventId = Integer.toString(this.page.event.id);
			return traiter(expression.replace(EVENT_ID, eventId));
		}

		// Coord x y event
		// TODO

		// Coord x y heros
		// TODO

		Pattern p;
		Matcher m;

		// -----------//
		// Commandes //
		// -----------//
		// Attendre cet Event / le Heros
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
			return attendreEvent;
		}

		// Attendre un Event
		p = Pattern.compile(ATTENDRE_UN_EVENT);
		m = p.matcher(expression);
		if (m.find()) {
			System.out.println(ATTENDRE_UN_EVENT);
			int idEvent = extraireLeNombre(m.group(0));
			return new AttendreLaFinDesDeplacements(idEvent);
		}

		// Invoquer un Event
		p = Pattern.compile(INVOQUER);
		m = p.matcher(expression);
		if (m.find()) {
			System.out.println(ATTENDRE_UN_EVENT);
			ArrayList<Integer> nombres = extraireLesNombres(m.group(0));
			return new InvoquerUnEvent(nombres.get(0), nombres.get(1), nombres.get(2), nombres.get(3));
		}

		// Autre
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
		// $game_map.events[@event_id].y - $game_player.y +
		// 1\nend\n$game_player.jump(x,y)

		// Pas encore implemente
		LOG.error("Script impossible à interpréter : " + expression);
		this.interpretationImplementee = false;
		return null;
	}

	/**
	 * Trouver le nombre situé dans une chaine de caractères.
	 * 
	 * @param nombreBrut chaine de caractères contenant un nombre
	 * @return nombre contenu
	 */
	private static int extraireLeNombre(final String nombreBrut) {
		final Pattern p = Pattern.compile(POSITIF);
		final Matcher m = p.matcher(nombreBrut);
		m.find();
		final String nombreExtrait = m.group(0);
		return (int) Integer.parseInt(nombreExtrait);
	}

	/**
	 * Trouver les nombres situés dans une chaine de caractères.
	 * 
	 * @param brut             chaine de caractères contenant des nombres
	 * @param relatifOuPositif les nombres attendus sont-ils positifs ou relatifs ?
	 * @return nombres contenus
	 */
	private static ArrayList<Integer> extraireLesNombres(final String brut) {
		final Pattern p = Pattern.compile(POSITIF);
		final Matcher m = p.matcher(brut);
		final ArrayList<Integer> nombres = new ArrayList<>();
		while (m.find()) {
			final String nombre = m.group();
			if ("nil".equals(nombre)) {
				nombres.add(null);
			} else {
				nombres.add(Integer.parseInt(nombre));
			}
		}
		return nombres;
	}

	@Override
	public final String toString() {
		return "AppelerUnScript : " + script;
	}

}
