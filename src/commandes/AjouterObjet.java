package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import jeu.Objet;
import main.Commande;
import main.Fenetre;

/**
 * Ajouter un certain nombre d'Objets au joueur.
 */
public class AjouterObjet extends Commande implements CommandeEvent, CommandeMenu {
	private Object identifiantObjet;
	private int numeroObjet;
	private final int quantite;
	
	/**
	 * Constructeur explicite
	 * @param identifiantObjet identifiant de l'Objet à ajouter : soit son nom, soit son numéro
	 * @param quantite à ajouter pour cet Objet
	 */
	public AjouterObjet(final Object identifiantObjet, final int quantite) {
		this.identifiantObjet = identifiantObjet;
		this.quantite = quantite;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public AjouterObjet(final HashMap<String, Object> parametres) {
		this( (Object) (parametres.containsKey("nomObjet") ? parametres.get("nomObjet") : parametres.get("numeroObjet")), //numéro ou nom
			parametres.containsKey("quantite") ? (int) parametres.get("quantite") : 1 //ajouter 1 par défaut
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
		
		//on procède à l'ajout
		final int[] objetsPossedes = Fenetre.getPartieActuelle().objetsPossedes;
		objetsPossedes[this.numeroObjet] += quantite;
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		this.executer();
		return curseurActuel+1;
	}

}
