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
	 * @param idArme identifiant de l'Arme à vérifier
	 */
	public ConditionArmePossedee(final int idArme) {
		this.idArme = idArme;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public ConditionArmePossedee(final HashMap<String, Object> parametres) {
		this( (int) parametres.get("idArme") );
	}
	
	@Override
	public final boolean estVerifiee() {
		final Partie partieActuelle = Fenetre.getPartieActuelle();
		if (partieActuelle.nombreDArmesPossedees > 0) {
			return partieActuelle.armesPossedees[this.idArme];
		}
		return false; //aucune arme possédée
	}
	
	/**
	 * Ce n'est pas une Condition qui implique une proximité avec le Héros.
	 * @return false 
	 */
	public final boolean estLieeAuHeros() {
		return false;
	}

}