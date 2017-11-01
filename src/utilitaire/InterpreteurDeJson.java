package utilitaire;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Classe utilitaire pour transformer les fichiers JSON en objets JSON.
 */
public abstract class InterpreteurDeJson {
	private static final Logger LOG = LogManager.getLogger(InterpreteurDeJson.class);
	
	/**
	 * Charger un objet JSON quelconque
	 * @param nomFichier nom du fichier JSON à charger
	 * @param adresse du fichier JSON à charger
	 * @param parPrefixe trouver le fichier uniquement à partir du début de son nom
	 * @return objet JSON
	 * @throws FileNotFoundException fichier JSON introuvable
	 * @throws JSONException Erreur de syntaxe dans le fichier JSON
	 */
	public static JSONObject ouvrirJson(final String nomFichier, final String adresse, final boolean parPrefixe) throws FileNotFoundException, JSONException {
		final File fichierJson;
		if (parPrefixe) {
			// fichiers commençant par
			final File dossier = new File(adresse);
			final File[] fichiersTrouves = dossier.listFiles(new FilenameFilter() {
			    public boolean accept(final File dir, final String name) {
			        return name.startsWith(nomFichier);
			    }
			});
			fichierJson = fichiersTrouves[0];
		} else {
			// nom exact
			fichierJson = new File(adresse+nomFichier+".json");
		}
		
		final Scanner scanner = new Scanner(fichierJson);
		final String contenuFichierJson = scanner.useDelimiter("\\Z").next();
		scanner.close();
		try {
			final JSONObject jsonObject = new JSONObject(contenuFichierJson);
			return jsonObject;
		} catch (JSONException e) {
			LOG.error("Erreur de syntaxe dans le fichier JSON "+nomFichier, e);
			throw e;
		}
	}
	
	/**
	 * Charger un objet JSON quelconque (par son nom exact)
	 * @param nomFichier nom du fichier JSON à charger
	 * @param adresse du fichier JSON à charger
	 * @return objet JSON
	 * @throws FileNotFoundException fichier JSON introuvable
	 * @throws JSONException Erreur de syntaxe dans le fichier JSON
	 */
	public static JSONObject ouvrirJson(final String nomFichier, final String adresse) throws FileNotFoundException, JSONException {
		return ouvrirJson(nomFichier, adresse, false);
	}
	
	/**
	 * Charger le paramétrage d'une nouvelle Partie au format JSON.
	 * @return objet JSON contenant le paramétrage d'une nouvelle Partie
	 * @throws FileNotFoundException fichier JSON introuvable
	 */
	public static JSONObject ouvrirJsonNouvellePartie() throws FileNotFoundException {
		return ouvrirJson("nouvellePartie", ".\\ressources\\Data\\");
	}
	
	/**
	 * Charger la liste des Quêtes du jeu au format JSON.
	 * @return objet JSON contenant la liste des Quêtes du jeu
	 * @throws FileNotFoundException fichier JSON introuvable
	 */
	public static JSONArray ouvrirJsonQuetes() throws FileNotFoundException {
		final JSONObject jsonQuetes = ouvrirJson("quetes", ".\\ressources\\Data\\");
		return jsonQuetes.getJSONArray("quetes");
	}
	
	/**
	 * Charger la liste des Objets du jeu au format JSON.
	 * @return objet JSON contenant la liste des Objets du jeu
	 * @throws FileNotFoundException fichier JSON introuvable
	 */
	public static JSONArray ouvrirJsonObjets() throws FileNotFoundException {
		final JSONObject jsonObjets = ouvrirJson("objets", ".\\ressources\\Data\\");
		return jsonObjets.getJSONArray("objets");
	}
	
	/**
	 * Charger la liste des Armes du jeu au format JSON.
	 * @return objet JSON contenant la liste des Armes du jeu
	 * @throws FileNotFoundException fichier JSON introuvable
	 */
	public static JSONArray ouvrirJsonArmes() throws FileNotFoundException {
		final JSONObject jsonArmes = ouvrirJson("armes", ".\\ressources\\Data\\");
		return jsonArmes.getJSONArray("armes");
	}
	
	/**
	 * Charger la liste des Gadgets du jeu au format JSON.
	 * @return objet JSON contenant la liste des Gadgets du jeu
	 * @throws FileNotFoundException fichier JSON introuvable
	 */
	public static JSONArray ouvrirJsonGadgets() throws FileNotFoundException {
		final JSONObject jsonGadgets = ouvrirJson("gadgets", ".\\ressources\\Data\\");
		return jsonGadgets.getJSONArray("gadgets");
	}
	
	/**
	 * Charger la liste des Animations modèles du jeu au format JSON.
	 * @return objet JSON contenant la liste des Animations du jeu
	 * @throws FileNotFoundException fichier JSON introuvable
	 */
	public static JSONArray ouvrirJsonAnimations() throws FileNotFoundException {
		final JSONObject jsonArmes = ouvrirJson("animations", ".\\ressources\\Data\\");
		return jsonArmes.getJSONArray("animations");
	}
	
	/**
	 * Charger une Map au format JSON.
	 * @param numero de la Map à charger
	 * @return objet JSON contenant la description de la Map
	 * @throws FileNotFoundException fichier JSON introuvable
	 */
	public static JSONObject ouvrirJsonMap(final int numero) throws FileNotFoundException {
		final String numeroA3Chiffres = String.format("%03d", numero) + "_";
		return ouvrirJson(numeroA3Chiffres, ".\\ressources\\Data\\Maps\\", true);
	}
	
	/**
	 * Charger un Tileset au format JSON.
	 * @param nom du Tileset à charger
	 * @return objet JSON contenant la description du Tileset
	 * @throws FileNotFoundException fichier JSON introuvable
	 */
	public static JSONObject ouvrirJsonTileset(final String nom) throws FileNotFoundException {
		return ouvrirJson(""+nom, ".\\ressources\\Data\\Tilesets\\");
	}

	/**
	 * Charger un Event générique au format JSON.
	 * Les Events génériques sont situés dans le dossier "ressources/Data/GenericEvents"
	 * @param nomEvent nom de l'Event générique
	 * @return objet JSON contenant la description de l'Event
	 * @throws FileNotFoundException fichier JSON introuvable
	 */
	public static JSONObject ouvrirJsonEventGenerique(final String nomEvent) throws FileNotFoundException {
		return ouvrirJson(nomEvent, ".\\ressources\\Data\\GenericEvents\\");
	}
	
}
