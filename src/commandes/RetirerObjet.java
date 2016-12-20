package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import jeu.Objet;
import main.Commande;
import main.Fenetre;

/**
 * Retirer un certain nombre d'Objets au joueur.
 */
public class RetirerObjet extends Commande implements CommandeMenu, CommandeEvent {
	private Object identifiantObjet;
	private int numeroObjet;
	private final int quantite;
	
	/**
	 * Constructeur explicite
	 * @param identifiantObjet identifiant de l'Objet à retirer : soit son nom, soit son numéro
	 * @param quantite à retirer pour cet Objet
	 */
	public RetirerObjet(final Object identifiantObjet, final int quantite) {
		this.identifiantObjet = identifiantObjet;
		this.quantite = quantite;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public RetirerObjet(final HashMap<String, Object> parametres) {
		this( (Object) (parametres.containsKey("nomObjet") ? parametres.get("nomObjet") : parametres.get("numeroObjet")), //numéro ou nom
			parametres.containsKey("quantite") ? (int) parametres.get("quantite") : 1 //retirer 1 par défaut
		);
	}
	
	@Override
	public final void executer() {
		//on calcule le numéro de l'Objet
		try {
			//l'identifiant de l'Objet est son numéro
			this.numeroObjet = (Integer) identifiantObjet;
		} catch (Exception e) {
			//l'identifiant de l'Objet est son nom
			this.numeroObjet = Objet.objetsDuJeuHash.get((String) identifiantObjet).numero;
		}
		
		//on procède à la suppression
		final int[] objetsPossedes = Fenetre.getPartieActuelle().objetsPossedes;
		objetsPossedes[this.numeroObjet] -= quantite;
		
		//on ne doit pas aller dans les négatifs
		if (objetsPossedes[this.numeroObjet] < 0) {
			objetsPossedes[this.numeroObjet] = 0;
		}
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		this.executer();
		return curseurActuel+1;
	}

}
