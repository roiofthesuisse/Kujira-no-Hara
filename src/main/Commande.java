package main;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.crypto.spec.SecretKeySpec;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import commandes.CommandeEvent;
import commandes.CommandeMenu;
import jeu.Partie;
import map.Event;
import map.PageEvent;
import menu.ElementDeMenu;

/**
 * Une Commande modifie l'�tat du jeu. Elle peut �tre lanc�e par une Page
 * d'Event, ou par un El�ment de Menu.
 */
public abstract class Commande {
	// cl� de cryptage
	private static final Logger LOG = LogManager.getLogger(Commande.class);
	private static final String CLE_CRYPTAGE_SAUVEGARDE = "t0p_k3k";
	private static final int NOMBRE_OCTETS_HASH = 16;

	/** [CommandeEvent] Eventuelle Page d'Event qui a appel� cette Commande */
	public PageEvent page;
	/** [CommandeMenu] Element de Menu qui a appel� cette Commande de Menu */
	public ElementDeMenu element;

	/**
	 * Execute la Commande totalement ou partiellement. Le curseur peut �tre
	 * inchang� (attendre n frames...) ; le curseur peut �tre incremente
	 * (assignement de variable...) ; le curseur peut faire un grand saut (boucles,
	 * conditions...).
	 * 
	 * @param curseurActuel position du curseur avant que la Commande soit execut�e
	 * @param commandes     liste des Commandes de la Page de comportement en train
	 *                      d'�tre lue
	 * @return nouvelle position du curseur apres l'execution totale ou partielle de
	 *         la Commande
	 */
	public abstract int executer(int curseurActuel, List<Commande> commandes);

	protected static Partie getPartieActuelle() {
		return Main.getPartieActuelle();
	}

	/**
	 * Construire la cl� de cryptage.
	 * 
	 * @return cl� de cryptage
	 */
	protected static final SecretKeySpec construireCleDeCryptage() {
		try {
			// Hashage de la cl�
			byte[] cle = CLE_CRYPTAGE_SAUVEGARDE.getBytes("UTF-8");
			final MessageDigest sha = MessageDigest.getInstance("SHA-1");
			cle = sha.digest(cle);

			cle = Arrays.copyOf(cle, NOMBRE_OCTETS_HASH); // seulement les 128 premiers bits
			return new SecretKeySpec(cle, "AES");

		} catch (UnsupportedEncodingException e) {
			LOG.error(e);
		} catch (NoSuchAlgorithmException e) {
			LOG.error(e);
		}
		return null;
	}

	/**
	 * <p>
	 * recuperer la liste des Events candidats potentiellement d�sign�s par la
	 * Condition.
	 * </p>
	 * <p>
	 * L'id peut �tre :
	 * <ul>
	 * <li>null : cet Event</li>
	 * <li>Integer : unique et identifi� par son num�ro</li>
	 * <li>String : non-unique et identifi� par son nom</li>
	 * </ul>
	 * </p>
	 * 
	 * @param id identifiant de l'Event
	 * @return liste des Events candidats a la Condition
	 */
	protected ArrayList<Event> recupererLesEventsCandidats(final Object id) {
		final ArrayList<Event> events = new ArrayList<Event>();
		if (id == null) {
			// null signifie cet Event
			events.add(this.page.event);
		} else if (id instanceof Integer) {
			// l'Event est identifi� par son numero
			final Event event;
			if ((Integer) id == 0) {
				event = this.page.event.map.heros;
			} else {
				event = this.page.event.map.eventsHash.get(id);
			}
			events.add(event);
		} else {
			// l'Event est identifi� par son nom ; potentiellement plusieurs candidats
			final String nomEvent = (String) id;
			for (Event event : this.page.event.map.events) {
				if (nomEvent.equals(event.nom)) {
					events.add(event);
				}
			}
		}
		return events;
	}

	/**
	 * Traduit les Commandes depuis le format JSON et les range dans la liste des
	 * Commandes de la Page.
	 * 
	 * @param commandes     liste des Commandes de la Page.
	 * @param commandesJSON tableau JSON contenant les Commandes au format JSON
	 */
	public static void recupererLesCommandesEvent(final ArrayList<Commande> commandes, final JSONArray commandesJSON) {
		// Ajouter les Commandes de la Page
		for (Object commandeJSON : commandesJSON) {
			final Commande commande = recupererUneCommande((JSONObject) commandeJSON);
			if (commande != null) {
				// on v�rifie que c'est bien une CommandeEvent
				if (commande instanceof CommandeEvent) {
					commandes.add(commande);
				} else {
					LOG.error("La commande " + commande.getClass().getName() + " n'est pas une CommandeEvent !");
				}
			}
		}
	}

	/**
	 * Traduit les Commandes depuis le format JSON et les range dans la liste des
	 * Commandes de l'Element de Menu.
	 * 
	 * @param commandes     liste des Commandes de la Page.
	 * @param commandesJSON tableau JSON contenant les Commandes au format JSON
	 */
	public static void recupererLesCommandesMenu(final ArrayList<CommandeMenu> commandes,
			final JSONArray commandesJSON) {
		for (Object commandeJSON : commandesJSON) {
			final Commande commande = recupererUneCommande((JSONObject) commandeJSON);
			if (commande != null) {
				// on v�rifie que c'est bien une CommandeMenu
				if (commande instanceof CommandeMenu) {
					commandes.add((CommandeMenu) commande);
				} else {
					LOG.error("La commande " + commande.getClass().getName() + " n'est pas une CommandeMenu !");
				}
			}
		}
	}

	/**
	 * Traduit les CommandesMenu depuis le format JSON et les range dans la liste
	 * des CommandesMenu de l'Element.
	 * 
	 * @param commandes     liste des Commandes de l'Element.
	 * @param commandesJSON tableau JSON contenant les CommandesMenu au format JSON
	 */
	public static void recupererLesCommandes(final ArrayList<Commande> commandes, final JSONArray commandesJSON) {
		for (Object commandeJSON : commandesJSON) {
			try {
				final String nomClasseCommande = ((JSONObject) commandeJSON).getString("nom");
				final Class<?> classeCommande = Class.forName("commandes." + nomClasseCommande);
				final Iterator<String> parametresNoms = ((JSONObject) commandeJSON).keys();
				String parametreNom; // nom du parametre pour instancier la Commande Event
				Object parametreValeur; // valeur du parametre pour instancier la Commande Event
				final HashMap<String, Object> parametres = new HashMap<String, Object>();
				while (parametresNoms.hasNext()) {
					parametreNom = parametresNoms.next();
					if (!parametreNom.equals("nom")) { // le nom servait a trouver la classe, ici on ne s'int�resse
														// qu'aux parametres
						parametreValeur = ((JSONObject) commandeJSON).get(parametreNom);
						parametres.put(parametreNom, parametreValeur);
					}
				}
				final Constructor<?> constructeurCommande = classeCommande.getConstructor(parametres.getClass());
				final Commande commande = (Commande) constructeurCommande.newInstance(parametres);
				commandes.add(commande);
			} catch (Exception e1) {
				LOG.error("Impossible de traduire l'objet JSON en CommandeMenu.", e1);
			}
		}
	}

	/**
	 * Traduit un objet JSON repr�sentant une Commande en vrai objet Commande.
	 * 
	 * @param commandeJson objet JSON repr�sentant une Commande
	 * @return objet Commande
	 */
	public static Commande recupererUneCommande(final JSONObject commandeJson) {
		try {
			Class<?> classeCommande;
			final String nomClasseCommande = commandeJson.getString("nom");
			try {
				// la Commande est juste une Commande du package "commandes"
				classeCommande = Class.forName("commandes." + nomClasseCommande);
			} catch (ClassNotFoundException e0) {
				// la Commande est une Condition du package "conditions"
				classeCommande = Class.forName("conditions." + nomClasseCommande);
			}
			final Iterator<String> parametresNoms = commandeJson.keys();
			String parametreNom; // nom du parametre pour instancier la Commande Event
			Object parametreValeur; // valeur du parametre pour instancier la Commande Event
			final HashMap<String, Object> parametres = new HashMap<String, Object>();
			while (parametresNoms.hasNext()) {
				parametreNom = parametresNoms.next();
				if (!parametreNom.equals("nom")) { // le nom servait a trouver la classe, ici on ne s'int�resse qu'aux
													// parametres
					parametreValeur = commandeJson.get(parametreNom);
					parametres.put(parametreNom, parametreValeur);
				}
			}
			final Constructor<?> constructeurCommande = classeCommande.getConstructor(parametres.getClass());
			final Commande commande = (Commande) constructeurCommande.newInstance(parametres);
			return commande;
		} catch (Exception e1) {
			LOG.error("Impossible de traduire l'objet JSON en CommandeEvent : " + commandeJson.toString(), e1);
			return null;
		}
	}

}
