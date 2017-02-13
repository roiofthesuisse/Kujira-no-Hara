package commandes;

import java.util.ArrayList;

import jeu.Partie;
import main.Commande;
import main.Fenetre;
import map.Event;

/**
 * Placer un Event ailleurs sur la Map
 */
public class TeleporterEvent extends Commande implements CommandeEvent {
	private Integer idEvent;
	private int nouveauX;
	private int nouveauY;
	private boolean utiliserVariables; //false:valeurs true:variables 
	
	/**
	 * Constructeur explicite
	 * @param idEvent identifiant de l'Event à téléporter
	 * @param nouveauX nouvelle coordonnée x de l'Event
	 * @param nouveauY nouvelle coordonnée y de l'Event
	 * @param utiliserVariables false si valeurs fixes, true si numéros de variables
	 */
	public TeleporterEvent(final Integer idEvent, final int nouveauX, final int nouveauY, final boolean utiliserVariables) {
		this.idEvent = idEvent;
		this.nouveauX = nouveauX;
		this.nouveauY = nouveauY;
		this.utiliserVariables = utiliserVariables;
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		final Event cetEvent;
		if (idEvent == null) {
			//si idEvent n'est pas précisé, l'Event appelant est téléporté par défaut
			cetEvent = this.page.event;
		} else {
			cetEvent = this.page.event.map.eventsHash.get((Integer) idEvent);
		}
		
		if (utiliserVariables) {
			final Partie partieActuelle = Fenetre.getPartieActuelle();
			cetEvent.x = partieActuelle.variables[nouveauX]*Fenetre.TAILLE_D_UN_CARREAU;
			cetEvent.y = partieActuelle.variables[nouveauY]*Fenetre.TAILLE_D_UN_CARREAU;
		} else {
			cetEvent.x = nouveauX*Fenetre.TAILLE_D_UN_CARREAU;
			cetEvent.y = nouveauY*Fenetre.TAILLE_D_UN_CARREAU;
		}
		return curseurActuel+1;
	}

}
