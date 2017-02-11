package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import jeu.Partie;
import main.Commande;
import main.Fenetre;

/**
 * Ajouter une nouvelle Arme au Heros
 */
public class AjouterArme extends Commande implements CommandeEvent {
	int idArme;
	
	/**
	 * Constructeur explicite
	 * @param arme identifiant de l'Arme à ajouter : son numéro ou son nom
	 */
	public AjouterArme(final int arme) {
		//l'identifiant de l'Arme est son numéro
		this.idArme = arme;

	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public AjouterArme(final HashMap<String, Object> parametres) {
		this( (int) parametres.get("idArme") );
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		final Partie partieActuelle = Fenetre.getPartieActuelle();
		if (!partieActuelle.armesPossedees[idArme]) {
			partieActuelle.armesPossedees[idArme] = true;
			partieActuelle.nombreDArmesPossedees++;
		}
		// Si actuellement rien d'équipé, on l'équipe
		if (partieActuelle.idArmeEquipee <= 0) {
			partieActuelle.idArmeEquipee = idArme;
		}
		return curseurActuel+1;
	}

}
