package commandes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.Commande;
import map.Event;
import utilitaire.graphismes.Graphismes;

/**
 * Changer l'apparence d'un Event
 */
public class ModifierApparence extends Commande implements CommandeEvent {
	private static final Logger LOG = LogManager.getLogger(ModifierApparence.class);

	int eventId;
	String nomNouvelleImage;

	/**
	 * Constructeur implicite (cet Event)
	 * 
	 * @param nomNouvelleImage nom de l'image de la nouvelle apparence
	 */
	public ModifierApparence(final String nomNouvelleImage) {
		this(nomNouvelleImage, -1); // c'est l'evenement qui donne l'ordre qui change d'apparence
	}

	/**
	 * Constructeur explicite
	 * 
	 * @param nomNouvelleImage nom de l'image de la nouvelle apparence
	 * @param eventId          Numero de l'Event a modifier
	 */
	public ModifierApparence(final String nomNouvelleImage, final int eventId) {
		this.nomNouvelleImage = nomNouvelleImage;
		this.eventId = eventId;
	}

	@Override
	public final int executer(final int curseurActuel, final List<Commande> commandes) {
		final ArrayList<Event> events = this.page.event.map.events;
		if (this.eventId == -1) {
			this.eventId = this.page.event.id; // c'est l'evenement qui donne l'ordre qui change d'apparence
		}
		for (Event e : events) {
			if (e.id == this.eventId) {
				try {
					e.imageActuelle = Graphismes.ouvrirImage("Characters", nomNouvelleImage);
					e.apparenceActuelleEstUnTile = false;
				} catch (IOException err) {
					LOG.error("Erreur lors de l'ouverture de l'image durant modification d'apparence d'event :", err);
				}
			}
		}
		return curseurActuel + 1;
	}

}
