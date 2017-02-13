package conditions;

import java.util.HashMap;

import commandes.CommandeEvent;
import commandes.CommandeMenu;
import main.Fenetre;
import utilitaire.Maths.Inegalite;

/**
 * Le joueur possède-t-il assez d'argent ?
 */
public class ConditionArgent extends Condition implements CommandeEvent, CommandeMenu {

	private int quantite;
	private Inegalite inegalite;
	
	/**
	 * Constructeur explicite
	 * @param numero de la Condition
	 * @param quantite d'argent à posséder
	 * @param symbole de l'Inégalité à utiliser pour comparer l'argent
	 */
	public ConditionArgent(final int numero, final Integer quantite, final String symbole) {
		this.numero = numero;
		this.quantite = quantite;
		this.inegalite = Inegalite.getInegalite(symbole);
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public ConditionArgent(final HashMap<String, Object> parametres) {
		this( parametres.containsKey("numero") ? (int) parametres.get("numero") : -1,
			(int) parametres.get("quantite"),
			(String) parametres.get("inegalite")
		);
	}
	
	@Override
	public final boolean estVerifiee() {
		return inegalite.comparer(Fenetre.getPartieActuelle().argent, this.quantite);
	}

	@Override
	public final boolean estLieeAuHeros() {
		return false;
	}

}
