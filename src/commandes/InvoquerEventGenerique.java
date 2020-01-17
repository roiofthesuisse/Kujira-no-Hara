package commandes;

import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.Commande;
import main.Main;
import map.Event;
import map.Map;

/**
 * Invoque un Event générique
 */
public class InvoquerEventGenerique extends Commande implements CommandeEvent {
	private static final Logger LOG = LogManager.getLogger(InvoquerEventGenerique.class);

	private final int x;
	private final int y;
	private final String nomEventInvoque;

	/**
	 * Constructeur explicite
	 * 
	 * @param x               position x de l'Event générique invoqué
	 * @param y               position y de l'Event générique invoqué
	 * @param nomEventInvoque nom de l'Event générique invoqué
	 */
	public InvoquerEventGenerique(final int x, final int y, final String nomEventInvoque) {
		this.x = x;
		this.y = y;
		this.nomEventInvoque = nomEventInvoque;
	}

	/**
	 * Constructeur générique
	 * 
	 * @param parametres liste de paramètres issus de JSON
	 */
	public InvoquerEventGenerique(final HashMap<String, Object> parametres) {
		this((int) parametres.get("x"), (int) parametres.get("y"), (String) parametres.get("nomEventInvoque"));
	}

	@Override
	public final int executer(final int curseurActuel, final List<Commande> commandes) {
		final Map map = this.page.event.map;
		final int xPixel = this.x * Main.TAILLE_D_UN_CARREAU;
		final int yPixel = this.y * Main.TAILLE_D_UN_CARREAU;
		final int idEventInvoque = -1;
		final Event eventInvoque = Event.creerEventGenerique(idEventInvoque, this.nomEventInvoque, xPixel, yPixel, map);
		if (map.calculerSiLaPlaceEstLibre(xPixel, yPixel, eventInvoque.largeurHitbox, eventInvoque.hauteurHitbox,
				idEventInvoque)) {
			map.eventsAAjouter.add(eventInvoque);
		} else {
			LOG.error("Impossible d'invoquer l'event générique \"" + nomEventInvoque + "\" aux coordonnées (" + xPixel
					+ ";" + yPixel + ") car la place est prise !");
		}
		return curseurActuel + 1;
	}

}
