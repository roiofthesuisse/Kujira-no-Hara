package conditions;

import java.util.HashMap;

import commandes.CommandeEvent;
import commandes.CommandeMenu;
import jeu.Partie;
import jeu.Quete.AvancementQuete;

/**
 * verifie si le Heros conna�t la Qu�te.
 */
public class ConditionEtatQuete extends Condition implements CommandeEvent, CommandeMenu {
	public int idQuete;
	private AvancementQuete avancementVoulu;

	/**
	 * Constructeur explicite
	 * @param numero de la Condition
	 * @param idQuete identifiant de la Qu�te a verifier
	 * @param avancementVoulu Etat de completion attendu pour cette Qu�te 
	 */
	public ConditionEtatQuete(final int numero, final int idQuete, final AvancementQuete avancementVoulu) {
		this.numero = numero;
		this.idQuete = idQuete;
		this.avancementVoulu = avancementVoulu;
	}
	
	/**
	 * Constructeur generique
	 * @param parametres liste de parametres issus de JSON
	 */
	public ConditionEtatQuete(final HashMap<String, Object> parametres) {
		this( parametres.get("numero") != null ? (int) parametres.get("numero") : -1,
			(int) parametres.get("idQuete"),
			AvancementQuete.getEtat( (String) parametres.get("etat") )
		);
	}
	
	@Override
	public final boolean estVerifiee() {
		final Partie partieActuelle = getPartieActuelle();
		final AvancementQuete avancementReel = partieActuelle.avancementDesQuetes[idQuete];
		return this.avancementVoulu.equals(avancementReel) 
		|| (avancementVoulu.equals(AvancementQuete.CONNUE) && avancementReel.equals(AvancementQuete.FAITE)); //une qu�te faite est connue
	}
	
	/**
	 * Ce n'est pas une Condition qui implique une proximite avec le Heros.
	 * @return false 
	 */
	public final boolean estLieeAuHeros() {
		return false;
	}

}