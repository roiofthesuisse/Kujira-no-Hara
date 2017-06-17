package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import jeu.Chronometre;
import main.Commande;
import main.Fenetre;

/**
 * Démarrer le Chronometre à partir d'une certaine valeur.
 */
public class DemarrerChronometre extends Commande implements CommandeEvent {
	public final boolean croissant;
	public final int depart;
	
	/**
	 * Constructeur explicite
	 * @param croissant le temps augmente-t-il ?
	 * @param depart valeur dont part le temps
	 */
	public DemarrerChronometre(final boolean croissant, final int depart) {
		this.depart = depart;
		this.croissant = croissant;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public DemarrerChronometre(final HashMap<String, Object> parametres) {
		this( 
				parametres.containsKey("croissant") ? (boolean) parametres.get("croissant") : false,
				(boolean) parametres.get("croissant") ? 0 : (int) parametres.get("depart")
		);
	}
	
	@Override
	public int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		Fenetre.getPartieActuelle().chronometre = new Chronometre(croissant, depart);
		return curseurActuel+1;
	}

}
