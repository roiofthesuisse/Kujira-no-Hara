package commandes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.Commande;

public class Commentaire extends Commande implements CommandeEvent {

	private static final Logger LOG = LogManager.getLogger(Commentaire.class);
	private static final String ANGLAIS = "(en)";
	private static final String SEPARATEUR = "|";

	private final String texte;

	/**
	 * Constructeur explicite
	 * 
	 * @param texte affiche dans les logs
	 */
	public Commentaire(final String texte) {
		this.texte = texte;
	}

	/**
	 * Constructeur generique
	 * 
	 * @param parametres liste de parametres issus de JSON
	 */
	public Commentaire(final HashMap<String, Object> parametres) {
		this((String) parametres.get("texte"));
	}

	@Override
	public int executer(int curseurActuel, List<Commande> commandes) {
		LOG.info("Commentaire : " + this.texte);
		// le commentaire est-il une traduction de Message ?
		if (this.texte.startsWith(ANGLAIS)) {
			Message prochainMessage = null;
			int i = 0;
			onChercheLeProchainMessage: while (curseurActuel + i < commandes.size()) {
				if (commandes.get(curseurActuel + i) instanceof Message) {
					prochainMessage = (Message) commandes.get(curseurActuel + i);
					break onChercheLeProchainMessage;
				}
				i++;
			}
			if (prochainMessage != null) {
				if (prochainMessage instanceof Choix && this.texte.contains(SEPARATEUR)) {
					// on traduit toutes les alternatives du Choix
					String[] traductions = this.texte.replace(ANGLAIS, "").split(SEPARATEUR);
					ArrayList<ArrayList<String>> alternatives = ((Choix) prochainMessage).alternatives;
					for (int j = 0; j < traductions.length && j < alternatives.size(); j++) {
						ArrayList<String> traductionsDeLAlternative = alternatives.get(j);
						String traduction = traductions[j];
						traductionsDeLAlternative.add(traduction.trim());
					}
				} else {
					// on traduit le Message
					String traduction = this.texte.replace(ANGLAIS, "");
					prochainMessage.texte.add(traduction.trim());
				}
			}
		}
		return curseurActuel + 1;
	}

}
