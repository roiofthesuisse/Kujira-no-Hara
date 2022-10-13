package commandes;

import java.util.HashMap;
import java.util.List;

import jeu.Partie;
import main.Commande;
import main.Main;
import map.Event;

/**
 * Placer un Event ailleurs sur la Map
 */
public class TeleporterEvent extends Commande implements CommandeEvent {
	private Integer idEvent;
	private int nouveauX;
	private int nouveauY;
	private boolean utiliserVariables; // false:valeurs true:variables

	/**
	 * Constructeur explicite
	 * 
	 * @param idEvent           identifiant de l'Event a t�l�porter
	 * @param nouveauX          nouvelle coordonn�e x de l'Event
	 * @param nouveauY          nouvelle coordonn�e y de l'Event
	 * @param utiliserVariables false si valeurs fixes, true si num�ros de variables
	 */
	public TeleporterEvent(final Integer idEvent, final int nouveauX, final int nouveauY,
			final boolean utiliserVariables) {
		this.idEvent = idEvent;
		this.nouveauX = nouveauX;
		this.nouveauY = nouveauY;
		this.utiliserVariables = utiliserVariables;
	}

	/**
	 * Constructeur g�n�rique
	 * 
	 * @param parametres liste de param�tres issus de JSON
	 */
	public TeleporterEvent(final HashMap<String, Object> parametres) {
		this(parametres.containsKey("idEvent") ? (int) parametres.get("idEvent") : null,
				(int) parametres.get("nouveauX"), (int) parametres.get("nouveauY"),
				parametres.containsKey("variable") && (boolean) parametres.get("variable"));
	}

	@Override
	public final int executer(final int curseurActuel, final List<Commande> commandes) {
		final Event cetEvent;
		if (idEvent == null) {
			// si idEvent n'est pas pr�cis�, l'Event appelant est t�l�port� par d�faut
			cetEvent = this.page.event;
		} else {
			cetEvent = this.page.event.map.eventsHash.get((Integer) idEvent);
		}

		if (utiliserVariables) {
			final Partie partieActuelle = getPartieActuelle();
			cetEvent.x = partieActuelle.variables[nouveauX] * Main.TAILLE_D_UN_CARREAU;
			cetEvent.y = partieActuelle.variables[nouveauY] * Main.TAILLE_D_UN_CARREAU;
		} else {
			cetEvent.x = nouveauX * Main.TAILLE_D_UN_CARREAU;
			cetEvent.y = nouveauY * Main.TAILLE_D_UN_CARREAU;
		}

		// On interromp l'inertie
		cetEvent.avancaitALaFramePrecedente = false;
		cetEvent.avance = false;

		return curseurActuel + 1;
	}

}
