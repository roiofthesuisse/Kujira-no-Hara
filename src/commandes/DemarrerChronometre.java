package commandes;

import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jeu.Chronometre;
import main.Commande;

/**
 * Demarrer le Chronometre a partir d'une certaine valeur.
 */
public class DemarrerChronometre extends Commande implements CommandeEvent {
	private static final Logger LOG = LogManager.getLogger(DemarrerChronometre.class);

	public final boolean croissant;
	public final int nombreDeSecondesInitial;

	/**
	 * Constructeur explicite
	 * 
	 * @param croissant le temps augmente-t-il ?
	 * @param depart    valeur dont part le temps
	 */
	public DemarrerChronometre(final boolean croissant, final int depart) {
		this.nombreDeSecondesInitial = depart;
		this.croissant = croissant;
	}

	/**
	 * Constructeur generique
	 * 
	 * @param parametres liste de parametres issus de JSON
	 */
	public DemarrerChronometre(final HashMap<String, Object> parametres) {
		this(parametres.containsKey("croissant") ? (boolean) parametres.get("croissant") : false,
				parametres.containsKey("depart") ? (int) parametres.get("depart") : 0);
	}

	@Override
	public int executer(final int curseurActuel, final List<Commande> commandes) {
		getPartieActuelle().chronometre = new Chronometre(this.croissant, this.nombreDeSecondesInitial);
		LOG.debug("Chronometre " + (this.croissant ? "croissant" : "d�croissant") + " d�marr� a "
				+ this.nombreDeSecondesInitial + " secondes.");
		return curseurActuel + 1;
	}

}
