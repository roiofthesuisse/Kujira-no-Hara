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
	private int nouveauX;
	private int nouveauY;
	private boolean utiliserVariables; //false:valeurs true:variables 
	
	/**
	 * Constructeur explicite
	 * @param nouveauX nouvelle coordonn�e x de l'Event
	 * @param nouveauY nouvelle coordonn�e y de l'Event
	 * @param utiliserVariables false si valeurs fixes, true si num�ros de variables
	 */
	public TeleporterEvent(final int nouveauX, final int nouveauY, final boolean utiliserVariables) {
		this.nouveauX = nouveauX;
		this.nouveauY = nouveauY;
		this.utiliserVariables = utiliserVariables;
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		final Event cetEvent = commandes.get(0).page.event; //TODO pourquoi pas tout simplement "this" ?
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