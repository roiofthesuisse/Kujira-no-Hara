package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import main.Commande;

/**
 * Effacer une image.
 */
public class EffacerImage extends Commande implements CommandeEvent {
	private int numero;
	
	/**
	 * Constructeur explicite
	 * @param numero de l'image à effacer
	 */
	public EffacerImage(final int numero) {
		this.numero = numero;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public EffacerImage(final HashMap<String, Object> parametres) {
		this( (int) parametres.get("numero") );
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		getPartieActuelle().images.remove(new Integer(this.numero));
		return curseurActuel+1;
	}

}
