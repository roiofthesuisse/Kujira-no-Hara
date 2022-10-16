package commandes;

import java.util.HashMap;
import java.util.List;

import main.Commande;

/**
 * Modifier les options de la boite de Messages.
 */
public class ModifierOptionsMessages extends Commande implements CommandeEvent {
	private final boolean masquer;
	private final String position;

	/**
	 * 
	 * @param masquer  la boite de Messages ?
	 * @param position de la boite de Messages a l'ecran
	 */
	public ModifierOptionsMessages(final boolean masquer, final String position) {
		this.masquer = masquer;
		this.position = position;
	}

	/**
	 * Constructeur generique
	 * 
	 * @param parametres liste de parametres issus de JSON
	 */
	public ModifierOptionsMessages(final HashMap<String, Object> parametres) {
		this(parametres.containsKey("masquer") ? (boolean) parametres.get("masquer") : false,
				parametres.containsKey("position") ? (String) parametres.get("position") : Message.Position.BAS.nom);
	}

	@Override
	public final int executer(final int curseurActuel, final List<Commande> commandes) {
		// masquer ?
		Message.masquerBoiteMessage = this.masquer;
		// position
		Message.positionBoiteMessage = Message.Position.parNom(this.position);
		return curseurActuel + 1;
	}

}
