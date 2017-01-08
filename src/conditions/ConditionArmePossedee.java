package conditions;

import java.util.HashMap;

import commandes.CommandeEvent;
import commandes.CommandeMenu;
import jeu.Partie;
import main.Fenetre;

/**
 * Vérifie si le Héros possède cette Arme.
 */
public class ConditionArmePossedee extends Condition implements CommandeEvent, CommandeMenu {
	public int idArme;

	/**
	 * Constructeur explicite
	 * @param numero de la Condition
	 * @param idArme identifiant de l'Arme à vérifier
	 */
	public ConditionArmePossedee(final int numero, final int idArme) {
		this.numero = numero;
		this.idArme = idArme;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public ConditionArmePossedee(final HashMap<String, Object> parametres) {
		this( parametres.containsKey("numero") ? (int) parametres.get("numero") : -1,
			(int) parametres.get("idArme") 
		);
	}
	
	@Override
	public final boolean estVerifiee() {
		final Partie partieActuelle = Fenetre.getPartieActuelle();
		return partieActuelle.armesPossedees[this.idArme];
	}
	
	/**
	 * Ce n'est pas une Condition qui implique une proximité avec le Héros.
	 * @return false 
	 */
	public final boolean estLieeAuHeros() {
		return false;
	}

}