package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import jeu.Partie;
import main.Commande;
import main.Fenetre;

/**
 * Ajouter un nouveau Gadget au Heros
 */
public class AjouterGadget extends Commande implements CommandeEvent {
	int idGadget;
	
	/**
	 * Constructeur explicite
	 * @param gadget identifiant du Gadget à ajouter : son numéro ou son nom
	 */
	public AjouterGadget(final int idGadget) {
		//l'identifiant du Gadget est son numéro
		this.idGadget = idGadget;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public AjouterGadget(final HashMap<String, Object> parametres) {
		this( (int) parametres.get("idGadget") );
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		final Partie partieActuelle = Fenetre.getPartieActuelle();
		if (!partieActuelle.gadgetsPossedes[idGadget]) {
			partieActuelle.gadgetsPossedes[idGadget] = true;
			partieActuelle.nombreDeGadgetsPossedes++;
		}
		// Si actuellement rien d'équipé, on l'équipe
		if (partieActuelle.idGadgetEquipe < 0) {
			partieActuelle.idGadgetEquipe = idGadget;
		}
		return curseurActuel+1;
	}

}
