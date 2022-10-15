package commandes;

import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.Commande;
import map.Event;

public class ModifierOffsetY extends Commande implements CommandeEvent {

	private static final Logger LOG = LogManager.getLogger(ModifierOffsetY.class);

	private final int idEvent;
	private final int nouvelOffset;

	/**
	 * Constructeur generique
	 * 
	 * @param parametres liste de parametres issus de JSON
	 */
	public ModifierOffsetY(final HashMap<String, Object> parametres) {
		this((int) parametres.get("idEvent"), (int) parametres.get("nouvelOffset"));
	}

	/**
	 * Constructeur explicite
	 * 
	 * @param idEvent
	 * @param nouvelOffset
	 */
	public ModifierOffsetY(int idEvent, int nouvelOffset) {
		this.idEvent = idEvent;
		this.nouvelOffset = nouvelOffset;
	}

	@Override
	public int executer(int curseurActuel, List<Commande> commandes) {
		Event cible = this.page.event.map.eventsHash.get(this.idEvent);
		cible.offsetY = this.nouvelOffset;
		LOG.trace("ModifierOffsetY de l'Event " + this.idEvent + " : " + this.nouvelOffset);
		return curseurActuel + 1;
	}

}
