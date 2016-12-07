package commandes;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jeu.Partie;
import main.Commande;
import main.Fenetre;

/**
 * Charger une Partie dans le menu dédié
 */
public class ChargerPartie extends Commande implements CommandeMenu {
	private static final Logger LOG = LogManager.getLogger(ChargerPartie.class);
	
	private final int numeroDeSauvegarde;
	
	/**
	 * Constructeur explicite
	 * @param numeroDeSauvegarde numéro du fichier de sauvegarde à charger
	 */
	public ChargerPartie(final int numeroDeSauvegarde) {
		this.numeroDeSauvegarde = numeroDeSauvegarde;
	}
	
	@Override
	public final void executer() {
		final Fenetre fenetre = this.element.menu.lecteur.fenetre;
		LOG.info("chargement de la partie numero "+numeroDeSauvegarde);
		fenetre.setPartieActuelle( Partie.chargerPartie(this.numeroDeSauvegarde) );
		fenetre.ouvrirLaPartie();
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		this.executer();
		return curseurActuel+1;
	}

}
