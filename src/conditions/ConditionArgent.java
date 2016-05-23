package conditions;

import java.util.HashMap;

import commandes.CommandeEvent;
import commandes.CommandeMenu;
import main.Fenetre;

/**
 * Le joueur possède-t-il assez d'argent ?
 */
public class ConditionArgent extends Condition implements CommandeEvent, CommandeMenu {
	private static final String PLUS_OU_AUTANT = ">=";
	private static final String PLUS_STRICTEMENT = ">";
	private static final String MOINS_OU_AUTANT = "<=";
	private static final String MOINS_STRICTEMENT = "<";
	/** Inégalités possibles pour comparer l'argent */
	private enum Inegalite {
		PLUS_OU_AUTANT, PLUS_STRICTEMENT, MOINS_OU_AUTANT, MOINS_STRICTEMENT
	}
	
	private int quantite;
	private Inegalite inegalite;
	
	/**
	 * Constructeur explicite
	 * @param quantite d'argent à posséder
	 * @param inegalite à utiliser pour comparer l'argent
	 */
	public ConditionArgent(final Integer quantite, final String inegalite) {
		this.quantite = quantite;
		switch(inegalite) {
		case PLUS_OU_AUTANT: 
			this.inegalite = Inegalite.PLUS_OU_AUTANT;
			break;
		case PLUS_STRICTEMENT: 
			this.inegalite = Inegalite.PLUS_STRICTEMENT;
			break;
		case MOINS_OU_AUTANT: 
			this.inegalite = Inegalite.MOINS_OU_AUTANT;
			break;
		case MOINS_STRICTEMENT: 
			this.inegalite = Inegalite.MOINS_STRICTEMENT;
			break;
		default: 
			this.inegalite = Inegalite.PLUS_OU_AUTANT;
			break;
		}
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public ConditionArgent(final HashMap<String, Object> parametres) {
		this( (int) parametres.get("quantite"),
				(String) parametres.get("inegalite")
			);
	}
	
	@Override
	public final boolean estVerifiee() {
		switch(this.inegalite) {
		case PLUS_OU_AUTANT:
			return Fenetre.getPartieActuelle().argent >= this.quantite;
		case PLUS_STRICTEMENT:
			return Fenetre.getPartieActuelle().argent > this.quantite;
		case MOINS_STRICTEMENT:
			return Fenetre.getPartieActuelle().argent < this.quantite;
		case MOINS_OU_AUTANT:
			return Fenetre.getPartieActuelle().argent <= this.quantite;
		default:
			return Fenetre.getPartieActuelle().argent >= this.quantite;
		}
		
	}

	@Override
	public final boolean estLieeAuHeros() {
		return false;
	}

}
