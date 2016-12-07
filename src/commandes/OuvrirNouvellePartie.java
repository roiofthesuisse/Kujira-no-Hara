package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.Commande;
import main.Fenetre;

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
	 * @param parametres liste de paramètres issus de JSON
	 */
	public OuvrirNouvellePartie(final HashMap<String, Object> parametres) {
		this();
	}
	
	@Override
	public final void executer() {
		final Fenetre fenetre = this.element.menu.lecteur.fenetre;
		LOG.info("nouvelle partie");
		fenetre.setPartieActuelle(null);
		fenetre.ouvrirLaPartie();
	}

	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		this.executer();
		return curseurActuel+1;
	}
	
}
