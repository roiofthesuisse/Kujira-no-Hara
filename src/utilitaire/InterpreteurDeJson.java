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

import commandes.CommandeEvent;
import commandes.CommandeMenu;
import conditions.Condition;
import main.Commande;
import map.Event;
import map.PageEvent;
import mouvements.Mouvement;

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
	 * @throws JSONException Erreur de syntaxe dans le fichier JSON
	 */
	private static JSONObject ouvrirJson(final String nomFichier, final String adresse) throws FileNotFoundException, JSONException {
		final String nomFichierJson = adresse+nomFichier+".json";
		final Scanner scanner = new Scanner(new File(nomFichierJson));
		final String contenuFichierJson = scanner.useDelimiter("\\Z").next();
		scanner.close();
		try {
			final JSONObject jsonObject = new JSONObject(contenuFichierJson);
			return jsonObject;
		} catch (JSONException e) {
			System.err.println("Erreur de syntaxe dans le fichier JSON "+nomFichier);
			e.printStackTrace();
			throw e;
		}
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
	 * @param page qui contient ces Commandes Event
	 */
	public static void recupererLesCommandesEvent(final ArrayList<Commande> commandes, final JSONArray commandesJSON, final PageEvent page) {
		for (Object commandeJSON : commandesJSON) {
			try {
				Class<?> classeCommande;
				final String nomClasseCommande = ((JSONObject) commandeJSON).getString("nom");
				try {
					//la Commande est juste une Commande du package "commandes"
					classeCommande = Class.forName("commandes." + nomClasseCommande);
				} catch (ClassNotFoundException e0) {
					//la Commande est une Condition du package "conditions"
					classeCommande = Class.forName("conditions." + nomClasseCommande);
				}
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
				final Commande commande = (Commande) constructeurCommande.newInstance(parametres);
				commande.page = page;
				//on vérifie que c'est bien une CommandeEvent
				if (commande instanceof CommandeEvent) {
					commandes.add(commande);
				} else {
					System.err.println("La commande "+nomClasseCommande+"n'est pas une CommandeEvent !");
				}
			} catch (Exception e1) {
				System.err.println("Impossible de traduire l'objet JSON en CommandeEvent.");
				e1.printStackTrace();
			}
		}
	}
	
	/**
	 * Traduit les CommandesMenu depuis le format JSON et les range dans la liste des CommandesMenu de l'Element.
	 * @param commandes liste des Commandes de l'Element.
	 * @param commandesJSON tableau JSON contenant les CommandesMenu au format JSON
	 */
	public static void recupererLesCommandesMenu(final ArrayList<CommandeMenu> commandes, final JSONArray commandesJSON) {
		for (Object commandeJSON : commandesJSON) {
			try {
				final String nomClasseCommande = ((JSONObject) commandeJSON).getString("nom");
				final Class<?> classeCommande = Class.forName("commandes." + nomClasseCommande);
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
				final CommandeMenu commande = (CommandeMenu) constructeurCommande.newInstance(parametres);
				//on vérifie que c'est bien une CommandeMenu
				if (commande instanceof CommandeMenu) {
					commandes.add(commande);
				} else {
					System.err.println("La commande "+nomClasseCommande+" n'est pas une CommandeMenu !");
				}
			} catch (Exception e1) {
				System.err.println("Impossible de traduire l'objet JSON en CommandeMenu.");
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
			final Integer id = jsonEvent.getInt("id");
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

				final JSONArray jsonPages = jsonEventGenerique.getJSONArray("pages");
				event = new Event(xEvent, yEvent, nomEvent, id, jsonPages, largeurHitbox, hauteurHitbox);
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

				final JSONArray jsonPages = jsonEvent.getJSONArray("pages");
				event = new Event(xEvent, yEvent, nomEvent, id, jsonPages, largeurHitbox, hauteurHitbox);
			}
			events.add(event);
		}
	}
	
	/**
	 * Traduit un objet JSON représentant un Mouvement en un vrai objet Mouvement.
	 * @param mouvementJSON objet JSON représentant un Mouvement
	 * @return un objet Mouvement
	 */
	public static Mouvement recupererUnMouvement(final JSONObject mouvementJSON) {
		Class<?> classeMouvement;
		final String nomClasseMouvement = ((JSONObject) mouvementJSON).getString("nom");
		Mouvement mouvement = null;
		try {
			classeMouvement = Class.forName("mouvements." + nomClasseMouvement);
			final Iterator<String> parametresNoms = ((JSONObject) mouvementJSON).keys();
			String parametreNom; //nom du paramètre pour instancier le mouvement
			Object parametreValeur; //valeur du paramètre pour instancier le mouvement
			final HashMap<String, Object> parametres = new HashMap<String, Object>();
			while (parametresNoms.hasNext()) {
				parametreNom = parametresNoms.next();
				if (!parametreNom.equals("nom")) { //le nom servait à trouver la classe, ici on ne s'intéresse qu'aux paramètres
					parametreValeur = ((JSONObject) mouvementJSON).get(parametreNom);
					parametres.put( parametreNom, parametreValeur );
				}
			}
			final Constructor<?> constructeurMouvement = classeMouvement.getConstructor(parametres.getClass());
			mouvement = (Mouvement) constructeurMouvement.newInstance(parametres);
		} catch (Exception e) {
			System.err.println("Impossible de traduire l'objet JSON en Mouvement "+nomClasseMouvement);
			e.printStackTrace();
		}
		return mouvement;
	}
	
	/**
	 * Traduit un JSONArray représentant les Mouvements en une liste de Mouvements.
	 * @param mouvementsJSON JSONArray représentant les Mouvements
	 * @return liste des Mouvements
	 */
	public static ArrayList<Mouvement> recupererLesMouvements(final JSONArray mouvementsJSON) {
		final ArrayList<Mouvement> mouvements = new ArrayList<Mouvement>();
		for (Object object : mouvementsJSON) {
			final JSONObject mouvementJson = (JSONObject) object;
			final Mouvement mouvement = recupererUnMouvement(mouvementJson);
			mouvements.add(mouvement);
		}
		return mouvements;
	}
	
}
