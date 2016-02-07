package commandes;

import java.util.ArrayList;

import jeu.Partie;
import main.Commande;
import main.Fenetre;
import map.Event;
import map.PageEvent;

/**
 * Placer un Event ailleurs sur la Map
 */
public class TeleporterEvent implements CommandeEvent {
	private PageEvent page;
	
	private int nouveauX;
	private int nouveauY;
	private boolean utiliserVariables; //false:valeurs true:variables 
	
	/**
	 * Constructeur explicite
	 * @param nouveauX nouvelle coordonnée x de l'Event
	 * @param nouveauY nouvelle coordonnée y de l'Event
	 * @param utiliserVariables false si valeurs fixes, true si numéros de variables
	 */
	public TeleporterEvent(final int nouveauX, final int nouveauY, final boolean utiliserVariables) {
		this.nouveauX = nouveauX;
		this.nouveauY = nouveauY;
		this.utiliserVariables = utiliserVariables;
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<? extends Commande> commandes) {
		final Event cetEvent = ((CommandeEvent) commandes.get(0)).getPage().event; //TODO pourquoi pas tout simplement "his" ?
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
	
	@Override
	public final PageEvent getPage() {
		return this.page;
	}

	@Override
	public final void setPage(final PageEvent page) {
		this.page = page;
	}

}
