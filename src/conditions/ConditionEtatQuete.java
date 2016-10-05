package conditions;

import java.util.HashMap;

import commandes.CommandeEvent;
import commandes.CommandeMenu;
import jeu.Partie;
import jeu.Quete.EtatQuete;
import main.Fenetre;

/**
 * Vérifie si le Héros connaît la Quête.
 */
public class ConditionEtatQuete extends Condition implements CommandeEvent, CommandeMenu {
	public int idQuete;
	private EtatQuete etatVoulu;

	/**
	 * Constructeur explicite
	 * @param numero de la Condition
	 * @param idQuete identifiant de la Quête à vérifier
	 * @param etatVoulu Etat de complétion attendu pour cette Quête 
	 */
	public ConditionEtatQuete(final int numero, final int idQuete, final EtatQuete etatVoulu) {
		this.numero = numero;
		this.idQuete = idQuete;
		this.etatVoulu = etatVoulu;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public ConditionEtatQuete(final HashMap<String, Object> parametres) {
		this( parametres.get("numero") != null ? (int) parametres.get("numero") : -1,
			(int) parametres.get("idQuete"),
			EtatQuete.getEtat( (String) parametres.get("etat") )
		);
	}
	
	@Override
	public final boolean estVerifiee() {
		final Partie partieActuelle = Fenetre.getPartieActuelle();
		final EtatQuete etatReel = partieActuelle.quetesEtat[idQuete];
		return etatReel.equals(this.etatVoulu) 
		|| (etatVoulu.equals(EtatQuete.CONNUE) && etatReel.equals(EtatQuete.FAITE)); //une quête faite est connue
	}
	
	/**
	 * Ce n'est pas une Condition qui implique une proximité avec le Héros.
	 * @return false 
	 */
	public final boolean estLieeAuHeros() {
		return false;
	}

}