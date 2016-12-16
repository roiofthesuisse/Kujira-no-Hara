package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import jeu.Partie;
import main.Commande;
import main.Fenetre;

/**
 * Retirer un Gadget au joueur.
 */
public class RetirerGadget extends Commande implements CommandeEvent, CommandeMenu {
	private int idGadget;
	
	/**
	 * Constructeur explicite
	 * @param idGadget identifiant du Gadget à retirer (numéro)
	 */
	public RetirerGadget(final int idGadget) {
		this.idGadget = idGadget;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public RetirerGadget(final HashMap<String, Object> parametres) {
		this( (int) parametres.get("idGadget") );
	}
	
	@Override
	public final void executer() {
		//on procède à la suppression
		final Partie partieActuelle = Fenetre.getPartieActuelle();
		final boolean[] gadgetsPossedes = partieActuelle.gadgetsPossedes;
		if (gadgetsPossedes[idGadget]) {
			gadgetsPossedes[this.idGadget] = false;
			partieActuelle.nombreDeGadgetsPossedes--;
			partieActuelle.idGadgetEquipe = -1; // -1 pour signifier qu'aucun Gadget n'est équipé
		}
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		this.executer();
		return curseurActuel+1;
	}

}
