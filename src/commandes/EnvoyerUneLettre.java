package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jeu.Partie;
import jeu.courrier.EtatCourrier;
import jeu.courrier.LettreAEnvoyer;
import jeu.courrier.Poste;
import main.Commande;
import main.Main;

/**
 * Envoyer une lettre a la poste.
 */
public class EnvoyerUneLettre extends Commande implements CommandeEvent {
	private static final Logger LOG = LogManager.getLogger(EnvoyerUneLettre.class);

	private final int idLettre;
	
	/**
	 * Constructeur explicite
	 * @param idLettre identifiant de la lettre a envoyer
	 */
	public EnvoyerUneLettre(final int idLettre) {
		this.idLettre = idLettre;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public EnvoyerUneLettre(final HashMap<String, Object> parametres) {
		this((int) parametres.get("idLettre"));
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		final Partie partie = Main.getPartieActuelle();
		final LettreAEnvoyer lettre = partie.lettresAEnvoyer.get(this.idLettre);
		if (EtatCourrier.PAS_ENVOYEE.equals(lettre.etat)) {
			if (StringUtils.isNotEmpty(lettre.texte)) {
				final String responseHttp = Poste.envoyerDuCourrier(lettre);
				if (responseHttp.contains("ok")) { //TODO comment reconnaitre le succes ?
					// Succes de l'envoi
					LOG.info("La lettre "+idLettre+" a pu être envoyée.");
					lettre.etat = EtatCourrier.ENVOYEE_PAS_REPONDUE;
					
				} else {
					// Echec de l'envoi
					LOG.error("La lettre "+idLettre+" n'a pas pu être envoyée !");
				}
				
			} else {
				LOG.error("La lettre "+idLettre+" est vierge !");
			}
		} else {
			LOG.error("La lettre "+idLettre+" a déjà été envoyée !");
		}
		return curseurActuel+1;
	}

}
