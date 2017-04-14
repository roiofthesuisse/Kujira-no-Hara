package commandes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.Commande;
import utilitaire.graphismes.Graphismes;

public class ModifierPanorama extends Commande implements CommandeEvent {
	protected static final Logger LOG = LogManager.getLogger(ModifierPanorama.class);
	
	private final String nomImage;
	
	/**
	 * Constructeur explicite
	 * @param nomImage nom de l'image de panorama
	 */
	public ModifierPanorama(final String nomImage) {
		//l'identifiant de l'Arme est son numéro
		this.nomImage = nomImage;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public ModifierPanorama(final HashMap<String, Object> parametres) {
		this( (String) parametres.get("nomImage") );
	}
	
	@Override
	public int executer(int curseurActuel, ArrayList<Commande> commandes) {
		try {
			this.page.event.map.panoramaActuel = Graphismes.ouvrirImage("Panorama", this.nomImage);
		} catch (IOException e) {
			LOG.error("Impossible d'ouvrir l'image de panorama : "+this.nomImage);
		}
		
		return 0;
	}
}
