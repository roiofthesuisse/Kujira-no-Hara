package commandes;

import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.Commande;
import main.Main;

/**
 * Créer une nouvelle Partie vierge et y jouer
 */
public class OuvrirNouvellePartie extends Commande implements CommandeMenu {
	private static final Logger LOG = LogManager.getLogger(OuvrirNouvellePartie.class);

	/**
	 * Constructeur vide
	 */
	private OuvrirNouvellePartie() {

	}

	/**
	 * Constructeur générique
	 * 
	 * @param parametres liste de paramètres issus de JSON
	 */
	public OuvrirNouvellePartie(final HashMap<String, Object> parametres) {
		this();
	}

	@Override
	public final int executer(final int curseurActuel, final List<Commande> commandes) {
		LOG.info("nouvelle partie");
		Main.setPartieActuelle(null);
		Main.ouvrirLaPartie();

		return curseurActuel + 1;
	}

}
