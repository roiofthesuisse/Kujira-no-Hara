package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import main.Commande;
import main.Fenetre;

/**
 * Ajouter un certain nombre d'Objets au joueur.
 */
public class AjouterObjet extends Commande implements CommandeEvent, CommandeMenu {
	private int idObjet;
	private final int quantite;
	
	/**
	 * Constructeur explicite
	 * @param identifiantObjet identifiant de l'Objet à ajouter : soit son nom, soit son numéro
	 * @param quantite à ajouter pour cet Objet
	 */
	public AjouterObjet(final int idObjet, final int quantite) {
		this.idObjet = idObjet;
		this.quantite = quantite;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public AjouterObjet(final HashMap<String, Object> parametres) {
		this( (int) parametres.get("numeroObjet"),
			parametres.containsKey("quantite") ? (int) parametres.get("quantite") : 1 //ajouter 1 par défaut
		);
	}

	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {	
		//on procède à l'ajout
		final int[] objetsPossedes = Fenetre.getPartieActuelle().objetsPossedes;
		objetsPossedes[this.idObjet] += quantite;
				
		return curseurActuel+1;
	}

}
