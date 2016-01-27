package commandesEvent;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Attendre un certain nombre de frames avant d'executer la Commande suivante
 */
public class Attendre extends CommandeEvent {
	private int nombreDeFrames; //nombre de frames qu'il faut attendre
	private int ceQuiAEteFait; //nombre de frames qu'on a déjà attendu
	
	/**
	 * Constructeur explicite
	 * @param nombreDeFrames qu'il faut attendre
	 */
	public Attendre(final int nombreDeFrames) {
		this.ceQuiAEteFait = 0;
		this.nombreDeFrames = nombreDeFrames;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public Attendre(final HashMap<String, Object> parametres) {
		this( (Integer) parametres.get("nombreDeFrames") );
	}
	
	/**
	 * Si la Page de comportement doit être rejouée, il faut réinitialiser cette Commande.
	 */
	public final void reinitialiser() {
		this.ceQuiAEteFait = 0;
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<CommandeEvent> commandes) {
		if (this.ceQuiAEteFait>=this.nombreDeFrames) {
			reinitialiser();
			return curseurActuel+1;
		} else {
			this.ceQuiAEteFait++;
			return curseurActuel;
		}
	}

}
