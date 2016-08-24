package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import main.Commande;
import main.Lecteur;

/**
 * Afficher ou masquer la boite des Messages.
 */
public class AfficherBoiteMessage extends Commande implements CommandeEvent {
	private boolean afficher;
	
	/**
	 * Constructeur explicite
	 * @param afficher la boîte des Messages ?
	 */
	public AfficherBoiteMessage(final Boolean afficher) {
		this.afficher = afficher;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public AfficherBoiteMessage(final HashMap<String, Object> parametres) {
		this( (Boolean) parametres.get("afficher") );
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		Lecteur.afficherBoiteMessage = afficher;
		if (afficher) {
			Message.imageBoiteMessage = Message.IMAGE_BOITE_MESSAGE_PLEINE;
		} else {
			Message.imageBoiteMessage = Message.IMAGE_BOITE_MESSAGE_VIDE;
		}
		return curseurActuel+1;
	}

}
