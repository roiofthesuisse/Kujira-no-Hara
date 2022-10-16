package commandes;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.Commande;
import map.Tileset;
import utilitaire.graphismes.Graphismes;

/**
 * Modifier l'image de Panorama pour en avoir une autre que celle associ�e par
 * d�faut au Tileset. On peut �galement changer le degr� de parallaxe.
 */
public class ModifierPanorama extends Commande implements CommandeEvent {
	protected static final Logger LOG = LogManager.getLogger(ModifierPanorama.class);

	private final String nomImage;
	private final int parallaxe;

	/**
	 * Constructeur explicite
	 * 
	 * @param nomImage  nom de l'image de panorama
	 * @param parallaxe (en pourcents) du Panorama par rapport au decor de la Map
	 */
	public ModifierPanorama(final String nomImage, final int parallaxe) {
		this.nomImage = nomImage;
		this.parallaxe = parallaxe;
	}

	/**
	 * Constructeur generique
	 * 
	 * @param parametres liste de parametres issus de JSON
	 */
	public ModifierPanorama(final HashMap<String, Object> parametres) {
		this((String) parametres.get("nomImage"),
				parametres.containsKey("parallaxe") ? (int) parametres.get("parallaxe") : Tileset.PARALLAXE_PAR_DEFAUT);
	}

	@Override
	public final int executer(final int curseurActuel, final List<Commande> commandes) {
		try {
			this.page.event.map.panoramaActuel = Graphismes.ouvrirImage("Panorama", this.nomImage);
			this.page.event.map.parallaxeActuelle = this.parallaxe;
		} catch (IOException e) {
			LOG.error("Impossible d'ouvrir l'image de panorama : " + this.nomImage);
		}

		return curseurActuel + 1;
	}
}
