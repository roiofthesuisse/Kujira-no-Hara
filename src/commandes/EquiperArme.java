package commandes;

import java.util.HashMap;
import java.util.List;

import main.Commande;

/**
 * Equiper le Heros avec une Arme qu'il poss�de
 */
public class EquiperArme extends Commande implements CommandeEvent {
	int idArme;

	/**
	 * Constructeur explicite
	 * 
	 * @param idArme identifiant de l'Arme a �quiper
	 */
	public EquiperArme(final int idArme) {
		this.idArme = idArme;
	}

	/**
	 * Constructeur generique
	 * 
	 * @param parametres liste de parametres issus de JSON
	 */
	public EquiperArme(final HashMap<String, Object> parametres) {
		this((int) parametres.get("idArme"));
	}

	@Override
	public final int executer(final int curseurActuel, final List<Commande> commandes) {
		getPartieActuelle().equiperArme(this.idArme);
		return curseurActuel + 1;
	}

}
