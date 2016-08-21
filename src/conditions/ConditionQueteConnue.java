package conditions;

import java.util.HashMap;

import commandes.CommandeEvent;
import commandes.CommandeMenu;
import jeu.Partie;
import jeu.Quete;
import main.Fenetre;

/**
 * Vérifie si le Héros connaît la Quête.
 */
public class ConditionQueteConnue extends Condition implements CommandeEvent, CommandeMenu {
	public int idQuete;

	/**
	 * Constructeur explicite
	 * @param numero de la Condition
	 * @param idQuete identifiant de la Quête à vérifier
	 */
	public ConditionQueteConnue(final int numero, final int idQuete) {
		this.numero = numero;
		this.idQuete = idQuete;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public ConditionQueteConnue(final HashMap<String, Object> parametres) {
		this( parametres.get("numero") != null ? (int) parametres.get("numero") : -1,
			(int) parametres.get("idQuete") 
		);
	}
	
	@Override
	public final boolean estVerifiee() {
		final Partie partieActuelle = Fenetre.getPartieActuelle();
		Quete.EtatQuete etatQuete = partieActuelle.quetesEtat[idQuete];
		return etatQuete.equals(Quete.EtatQuete.CONNUE) || etatQuete.equals(Quete.EtatQuete.FAITE);
	}
	
	/**
	 * Ce n'est pas une Condition qui implique une proximité avec le Héros.
	 * @return false 
	 */
	public final boolean estLieeAuHeros() {
		return false;
	}

}