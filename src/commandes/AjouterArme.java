package commandes;

import java.util.HashMap;
import java.util.List;

import jeu.Partie;
import main.Commande;

/**
 * Ajouter une nouvelle Arme au Heros
 */
public class AjouterArme extends Commande implements CommandeEvent {
	int idArme;

	/**
	 * Constructeur explicite
	 * 
	 * @param arme identifiant de l'Arme a ajouter : son Numero ou son nom
	 */
	public AjouterArme(final int arme) {
		// l'identifiant de l'Arme est son Numero
		this.idArme = arme;

	}

	/**
	 * Constructeur generique
	 * 
	 * @param parametres liste de parametres issus de JSON
	 */
	public AjouterArme(final HashMap<String, Object> parametres) {
		this((int) parametres.get("idArme"));
	}

	@Override
	public final int executer(final int curseurActuel, final List<Commande> commandes) {
		final Partie partieActuelle = getPartieActuelle();
		if (!partieActuelle.armesPossedees[idArme]) {
			partieActuelle.armesPossedees[idArme] = true;
			partieActuelle.nombreDArmesPossedees++;
		}
		// Si actuellement rien d'�quip�, on l'�quipe
		if (partieActuelle.idArmeEquipee < 0) {
			partieActuelle.idArmeEquipee = idArme;
		}
		return curseurActuel + 1;
	}

}
