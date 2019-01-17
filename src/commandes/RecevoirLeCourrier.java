package commandes;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jeu.courrier.Poste;
import main.Commande;

/**
 * Recevoir le courrier.
 */
public class RecevoirLeCourrier extends Commande implements CommandeEvent {
	private static final Logger LOG = LogManager.getLogger(RecevoirLeCourrier.class);

	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		final String reponseHttp = Poste.recevoirLeCourrier();
		//TODO json
		
		return curseurActuel+1;
	}

}
