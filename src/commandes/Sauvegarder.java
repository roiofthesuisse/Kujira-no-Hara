package commandes;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

/**
 * Sauvegarder la Partie ainsi que l'état de la Map actuelle dans un fichier externe crypté.
 */
public class Sauvegarder implements CommandeMenu {
	private static final Logger LOG = LogManager.getLogger(Sauvegarder.class);
	
	private int numeroSauvegarde;
	
	/**
	 * Constructeur explicite
	 * @param numeroSauvegarde numéro du fichier de Sauvegarde
	 */
	private Sauvegarder(final int numeroSauvegarde) {
		this.numeroSauvegarde = numeroSauvegarde;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public Sauvegarder(final HashMap<String, Object> parametres) {
		this( (int) parametres.get("numeroSauvegarde") );
	}

	@Override
	public final void executer() {
		String filename = "./save"+this.numeroSauvegarde+".txt";
		try {
			FileWriter file = new FileWriter(filename);
			try {
				// Générer le fichier de sauvegarde
				JSONObject jsonSauvegarde = genererSauvegarde();
				String jsonStringSauvegarde = jsonSauvegarde.toString();
				
				// Crypter
				String jsonStringSauvegardeCryptee = crypter(jsonStringSauvegarde);
				
				// Enregistrer le fichier
				file.write(jsonStringSauvegardeCryptee);
				LOG.info("Partie sauvegardée dans le fichier "+filename);
				LOG.debug(jsonStringSauvegarde);
			} catch (IOException e) {
				throw e;
			} finally {
				file.flush();
				file.close();
			}
		} catch (IOException e) {
			LOG.error("Impossible de sauvegarder la partie dans le fichier "+filename, e);
		}
	}

	/**
	 * Produire un fichier JSON représentant la Partie et l'état actuel de la Map.
	 * @return sauvegarde au format JSON
	 */
	private JSONObject genererSauvegarde() {
		JSONObject jsonSauvegarde = new JSONObject();
		
		// Partie
		JSONObject jsonPartie = new JSONObject();
		//TODO
		jsonSauvegarde.put("partie", jsonPartie);
		
		// Etat de la Map
		JSONObject jsonEtatMap = new JSONObject();
		//TODO
		jsonSauvegarde.put("etatMap", jsonEtatMap);
		
		return jsonSauvegarde;
	}

	/**
	 * Crypter la sauvegarde.
	 * @param jsonStringSauvegarde texte de la Sauvegarde non crypté
	 * @return texte de la Sauvegarde crypté
	 */
	private String crypter(final String jsonStringSauvegarde) {
		// TODO
		return jsonStringSauvegarde;
	}
	
}
