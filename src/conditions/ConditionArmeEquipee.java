package conditions;

import java.util.HashMap;

import commandes.CommandeEvent;
import commandes.CommandeMenu;
import jeu.Partie;

/**
 * verifie si le Heros a �quip� cette Arme.
 */
public class ConditionArmeEquipee extends Condition implements CommandeEvent, CommandeMenu {
	public int idArme;
	
	/**
	 * Constructeur explicite
	 * @param numero de la Condition
	 * @param idArme identifiant de l'Arme a verifier
	 */
	public ConditionArmeEquipee(final int numero, final int idArme) {
		this.numero = numero;
		this.idArme = idArme;
	}
	
	/**
	 * Constructeur generique
	 * @param parametres liste de parametres issus de JSON
	 */
	public ConditionArmeEquipee(final HashMap<String, Object> parametres) {
		this( 
				parametres.containsKey("numero") ? (int) parametres.get("numero") : -1,
				(int) parametres.get("idArme") 
		);
	}
	
	@Override
	public final boolean estVerifiee() {
		final Partie partieActuelle = getPartieActuelle();
		if (partieActuelle.nombreDArmesPossedees > 0) {
			return partieActuelle.idArmeEquipee == this.idArme;
		}
		return false; //aucune arme poss�d�e
	}
	
	/**
	 * Ce n'est pas une Condition qui implique une proximite avec le Heros.
	 * @return false 
	 */
	public final boolean estLieeAuHeros() {
		return false;
	}

}