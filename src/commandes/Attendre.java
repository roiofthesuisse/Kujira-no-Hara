package commandes;

import java.util.HashMap;
import java.util.List;

import main.Commande;

/**
 * Attendre un certain nombre de frames avant d'executer la Commande suivante.
 */
public class Attendre extends Commande implements CommandeEvent {
	private final int nombreDeFrames;
	private int ceQuiAEteFait;

	/**
	 * Constructeur explicite
	 * 
	 * @param nombreDeFrames qu'il faut attendre
	 */
	public Attendre(final int nombreDeFrames) {
		this.nombreDeFrames = nombreDeFrames;
		this.ceQuiAEteFait = 0;
	}

	/**
	 * Constructeur generique
	 * 
	 * @param parametres liste de parametres issus de JSON
	 */
	public Attendre(final HashMap<String, Object> parametres) {
		this((Integer) parametres.get("nombreDeFrames"));
	}

	@Override
	public int executer(final int curseurActuel, final List<Commande> commandes) {
		if (this.ceQuiAEteFait < this.nombreDeFrames) {
			// On n'a pas fini d'attendre
			this.ceQuiAEteFait++;
			return curseurActuel;
		} else {
			// On a fini d'attendre
			this.ceQuiAEteFait = 0; // reinitialisation
			return curseurActuel + 1;
		}
	}

}
