package commandes;

import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jeu.Partie;
import jeu.courrier.LettreRecue;
import jeu.courrier.Poste;
import main.Commande;
import main.Main;

/**
 * Recevoir le courrier.
 */
public class RecevoirLeCourrier extends Commande implements CommandeEvent {
	private static final Logger LOG = LogManager.getLogger(RecevoirLeCourrier.class);

	/**
	 * Constructeur explicite
	 */
	public RecevoirLeCourrier() {
		// rien
	}

	/**
	 * Constructeur generique
	 * 
	 * @param parametres liste de parametres issus de JSON
	 */
	public RecevoirLeCourrier(final HashMap<String, Object> parametres) {
		this();
	}

	@Override
	public final int executer(final int curseurActuel, final List<Commande> commandes) {
		final String courrier = Poste.recevoirLeCourrier();
		final String[] lettres = courrier.split("SEPARATEUR");
		final Partie partie = Main.getPartieActuelle();
		for (String lettre : lettres) {
			final String[] champs = lettre.split("|");
			final String destinataire = champs[0];
			final int lettreId = Integer.parseInt(champs[1]);
			final String reponseTexte = champs[2];
			final LettreRecue lettreRecue = new LettreRecue(lettreId, destinataire, reponseTexte);
			partie.lettresRecues.add(lettreRecue);
		}

		return curseurActuel + 1;
	}

	/*
	 * public static void main(final String[] args) { final String courrier =
	 * "mathusalem|1|Salut Robert,\nMerci de prendre de mes nouvelles.Malheureusement, je ne veux plus jamais te parler.\nBien cordialement,\nPhilippeSEPARATEUR"
	 * ; final String[] lettres = courrier.split("SEPARATEUR"); for (String lettre :
	 * lettres) { final String[] champs = lettre.split("\\|"); final String
	 * destinataire = champs[0]; final int lettreId = Integer.parseInt(champs[1]);
	 * final String reponseTexte = champs[2]; final LettreRecue lettreRecue = new
	 * LettreRecue(lettreId, destinataire, reponseTexte);
	 * LOG.debug(lettreRecue.personnage.nomPersonnage+" : "+lettreRecue.texte); } }
	 */

}
