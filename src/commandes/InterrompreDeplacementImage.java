package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import main.Commande;
import main.Fenetre;
import map.Picture;

/**
 * Figer une Image en supprimant son Déplacement.
 */
public class InterrompreDeplacementImage extends Commande implements CommandeEvent {
	
	/** numéro de l'image à déplacer */
	private Integer numero; //Integer car utilisé comme clé d'une HashMap
	
	/**
	 * Constructeur explicite
	 * @param numero de l'image à stopper
	 */
	private InterrompreDeplacementImage(final int numero) {
		this.numero = numero;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public InterrompreDeplacementImage(final HashMap<String, Object> parametres) {
		this( (int) parametres.get("numero") );
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		final Picture picture = Fenetre.getPartieActuelle().images.get(this.numero);
		picture.deplacementActuel = null;
		
		return curseurActuel+1;
	}

}
