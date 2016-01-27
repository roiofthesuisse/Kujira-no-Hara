package utilitaire;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import commandesEvent.CommandeEvent;
import conditions.Condition;
import map.Event;

/**
 * Classe utilitaire pour transformer les fichiers JSON en objets JSON.
 */
public abstract class InterpreteurDeJson {
	/**
	 * Charger un objet JSON quelconque
	 * @param nomFichier nom du fichier JSON à charger
	 * @param adresse du fichier JSON à charger
	 * @return objet JSON
	 * @throws FileNotFoundException fichier JSON introuvable
	 */
	private static JSONObject ouvrirJson(final String nomFichier, final String adresse) throws FileNotFoundException {
		final String nomFichierJson = adresse+nomFichier+".json";
		final Scanner scanner = new Scanner(new File(nomFichierJson));
		final String contenuFichierJson = scanner.useDelimiter("\\Z").next();
		scanner.close();
		return new JSONObject(contenuFichierJson);
	}
	
	/**
	 * Charger une Map au format JSON.
	 * @param numero de la Map à charger
	 * @return objet JSON contenant la description de la Map
	 * @throws FileNotFoundException fichier JSON introuvable
	 */
	public static JSONObject ouvrirJsonMap(final int numero) throws FileNotFoundException {
		return ouvrirJson(""+numero, ".\\ressources\\Data\\Maps\\");
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
	
	/**
	 * Traduit les Conditions depuis le format JSON et les range dans la liste des Conditions de la Page.
	 * @param conditions liste des Conditions de la Page
	 * @param conditionsJSON tableau JSON contenant les Conditions au format JSON
	 */
	public static void recupererLesConditions(final ArrayList<Condition> conditions, final JSONArray conditionsJSON) {
		for (Object conditionJSON : conditionsJSON) {
			try {
				final Class<?> classeCondition = Class.forName("conditions.Condition"+((JSONObject) conditionJSON).get("nom"));
				try {
					//cas d'une Condition sans paramètres
					
					final Constructor<?> constructeurCondition = classeCondition.getConstructor();
					final Condition condition = (Condition) constructeurCondition.newInstance();
					conditions.add(condition);
					
				} catch (NoSuchMethodException e0) {
					//cas d'une Condition utilisant des paramètres
					
					final Iterator<String> parametresNoms = ((JSONObject) conditionJSON).keys();
					String parametreNom; //nom du paramètre pour instancier la Condition
					Object parametreValeur; //valeur du paramètre pour instancier la Condition
					final HashMap<String, Object> parametres = new HashMap<String, Object>();
					while (parametresNoms.hasNext()) {
						parametreNom = parametresNoms.next();
						if (!parametreNom.equals("nom")) { //le nom servait à trouver la classe, ici on ne s'intéresse qu'aux paramètres
							parametreValeur = ((JSONObject) conditionJSON).get(parametreNom);
							parametres.put( parametreNom, parametreValeur );
						}
					}
					final Constructor<?> constructeurCondition = classeCondition.getConstructor(parametres.getClass());
					final Condition condition = (Condition) constructeurCondition.newInstance(parametres);
					conditions.add(condition);
				}
				
			} catch (Exception e1) {
				System.err.println("Erreur lors de l'instanciation de la Condition :");
				e1.printStackTrace();
			}
		}
	}
	
	/**
	 * Traduit les Commandes depuis le format JSON et les range dans la liste des Commandes de la Page.
	 * @param commandes liste des Commandes de la Page.
	 * @param commandesJSON tableau JSON contenant les Commandes au format JSON
	 */
	public static void recupererLesCommandes(final ArrayList<CommandeEvent> commandes, final JSONArray commandesJSON) {
		for (Object commandeJSON : commandesJSON) {
			try {
				final Class<?> classeCommande = Class.forName("commandesEvent."+((JSONObject) commandeJSON).get("nom"));
				final Iterator<String> parametresNoms = ((JSONObject) commandeJSON).keys();
				String parametreNom; //nom du paramètre pour instancier la Commande Event
				Object parametreValeur; //valeur du paramètre pour instancier la Commande Event
				final HashMap<String, Object> parametres = new HashMap<String, Object>();
				while (parametresNoms.hasNext()) {
					parametreNom = parametresNoms.next();
					if (!parametreNom.equals("nom")) { //le nom servait à trouver la classe, ici on ne s'intéresse qu'aux paramètres
						parametreValeur = ((JSONObject) commandeJSON).get(parametreNom);
						parametres.put( parametreNom, parametreValeur );
					}
				}
				final Constructor<?> constructeurCommande = classeCommande.getConstructor(parametres.getClass());
				final CommandeEvent commande = (CommandeEvent) constructeurCommande.newInstance(parametres);
				commandes.add(commande);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}
	
	/**
	 * Traduit les Events depuis le format JSON et les range dans la liste des Events de la Map.
	 * @param events liste des Events de la Map
	 * @param eventsJSON tableau JSON contenant les Events au format JSON
	 */
	public static void recupererLesEvents(final ArrayList<Event> events, final JSONArray eventsJSON) {
		for (Object ev : eventsJSON) {
			final JSONObject jsonEvent = (JSONObject) ev;
			//récupération des données dans le JSON
			final String nomEvent = jsonEvent.getString("nom");
			final int xEvent = jsonEvent.getInt("x");
			final int yEvent = jsonEvent.getInt("y");
			//instanciation de l'event
			Event event;
			try {
				//on essaye de le créer à partir de la bibliothèque JSON GenericEvents
				final JSONObject jsonEventGenerique = InterpreteurDeJson.ouvrirJsonEventGenerique(nomEvent);
				int largeurHitbox;
				try {
					largeurHitbox = jsonEventGenerique.getInt("largeur");
				} catch (JSONException e2) {
					largeurHitbox = Event.LARGEUR_HITBOX_PAR_DEFAUT;
				}
				int hauteurHitbox;
				try {
				hauteurHitbox = jsonEventGenerique.getInt("hauteur");
				} catch (JSONException e2) {
					hauteurHitbox = Event.HAUTEUR_HITBOX_PAR_DEFAUT;
				}
				
				int direction;
				try {
					direction = jsonEvent.getInt("direction");
				} catch (Exception e1) {
					direction = Event.Direction.BAS; //direction par défaut
				}

				final JSONArray jsonPages = jsonEventGenerique.getJSONArray("pages");
				event = new Event(xEvent, yEvent, direction, nomEvent, jsonPages, largeurHitbox, hauteurHitbox);
			} catch (Exception e3) {
				//l'event n'est pas générique, on le construit à partir de sa description dans la page JSON
				int largeurHitbox;
				try {
					largeurHitbox = jsonEvent.getInt("largeur");
				} catch (JSONException e2) {
					largeurHitbox = Event.LARGEUR_HITBOX_PAR_DEFAUT;
				}
				int hauteurHitbox;
				try {
					hauteurHitbox = jsonEvent.getInt("hauteur");
				} catch (JSONException e2) {
					hauteurHitbox = Event.HAUTEUR_HITBOX_PAR_DEFAUT;
				}
				int direction;
				try {
					direction = jsonEvent.getInt("direction");
				} catch (Exception e1) {
					direction = Event.Direction.BAS; //direction par défaut
				}

				final JSONArray jsonPages = jsonEvent.getJSONArray("pages");
				event = new Event(xEvent, yEvent, direction, nomEvent, jsonPages, largeurHitbox, hauteurHitbox);
			}
			events.add(event);
		}
	}
	
}
