package conditions;

import main.Fenetre;
import main.Partie;

/**
 * Vérifier si le Héros a équipé cette Arme
 */
public class ConditionArmeEquipee extends Condition {
	public int idArme;
	
	/**
	 * Constructeur explicite
	 * @param idArme identifiant de l'Arme à vérifier
	 */
	public ConditionArmeEquipee(final int idArme) {
		this.idArme = idArme;
	}
	
	@Override
	public final boolean estVerifiee() {
		final Partie partieActuelle = Fenetre.getPartieActuelle();
		if (partieActuelle.idArmesPossedees.size()>0) {
			return partieActuelle.getArmeEquipee().id == this.idArme;
		}
		return false; //aucune arme possédée
	}
	
	public final boolean estLieeAuHeros() {
		return false;
	}

}