package conditions;

import java.util.HashMap;

import commandes.CommandeEvent;
import commandes.CommandeMenu;
import jeu.Objet;
import main.Fenetre;

/**
 * Le joueur possède-t-il l'Objet ?
 */
public class ConditionObjetPossede extends Condition implements CommandeEvent, CommandeMenu {
	private Object objet;
	
	/**
	 * Constructeur partiel
	 * Réservé aux Conditions de Pages
	 * @param objet identifiant de l'Objet : numéro ou nom
	 */
	public ConditionObjetPossede(final Object objet) {
		this(-1, objet);
	}
	
	/**
	 * Constructeur explicite
	 * @param numero de la Condition
	 * @param objet identifiant de l'Objet : numéro ou nom
	 */
	public ConditionObjetPossede(final int numero, final Object objet) {
		this.numero = numero;
		this.objet = objet;
	}
		
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public ConditionObjetPossede(final HashMap<String, Object> parametres) {
		this( parametres.get("numero") != null ? (int) parametres.get("numero") : -1,
			parametres.get("objet")
		);
	}
	
	@Override
	public final boolean estVerifiee() {
		int numeroObjet;
		try {
			numeroObjet = (Integer) objet;
		} catch (Exception e) {
			numeroObjet = Objet.objetsDuJeuHash.get((String) objet).numero;
		}
		return Fenetre.getPartieActuelle().objetsPossedes[numeroObjet] >= 1;
	}

	@Override
	public final boolean estLieeAuHeros() {
		return false;
	}

}
