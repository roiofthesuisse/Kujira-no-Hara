package conditions;

import java.util.HashMap;

import commandes.CommandeEvent;
import commandes.CommandeMenu;
import utilitaire.Maths.Inegalite;

/**
 * Le joueur poss�de-t-il assez d'argent ?
 */
public class ConditionArgent extends Condition implements CommandeEvent, CommandeMenu {

	private int quantite;
	private Inegalite inegalite;
	
	/**
	 * Constructeur explicite
	 * @param numero de la Condition
	 * @param quantite d'argent a poss�der
	 * @param symbole de l'In�galit� a utiliser pour comparer l'argent
	 */
	public ConditionArgent(final int numero, final Integer quantite, final String symbole) {
		this.numero = numero;
		this.quantite = quantite;
		this.inegalite = Inegalite.getInegalite(symbole);
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public ConditionArgent(final HashMap<String, Object> parametres) {
		this( parametres.containsKey("numero") ? (int) parametres.get("numero") : -1,
			(int) parametres.get("quantite"),
			(String) parametres.get("inegalite")
		);
	}
	
	@Override
	public final boolean estVerifiee() {
		return inegalite.comparer(getPartieActuelle().argent, this.quantite);
	}

	@Override
	public final boolean estLieeAuHeros() {
		return false;
	}

}
