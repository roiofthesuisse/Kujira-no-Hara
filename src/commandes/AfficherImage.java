package commandes;

import java.util.ArrayList;

import main.Commande;
import map.Picture;

/**
 * Afficher une image par dessus l'écran.
 */
public class AfficherImage extends Commande implements CommandeEvent {
	private Picture picture;
	
	//TODO constructeurs
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		this.page.event.map.lecteur.images.put(picture.numero, picture);
		return curseurActuel+1;
	}

}
