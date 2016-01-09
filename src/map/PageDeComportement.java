package map;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.imageio.ImageIO;

import org.json.JSONObject;

import comportementEvent.CommandeEvent;
import conditions.Condition;

/**
 * Un Event peut avoir plusieurs comportements. Chaque comportement est décrit par une Page de comportements.
 * La Page est déclenchée si certaines Conditions sont vérifiées, ses Commandes sont alors executées.
 */
public class PageDeComportement {
	/** event auquel appartient la page */
	public Event event;
	/** numero de la page */
	public int numero;
	/** ce flag est automatiquement mis à true si contient une page avec une condition Parler */
	private boolean sOuvreParParole = false;
	
	/** conditions de déclenchement */
	public final ArrayList<Condition> conditions;
	
	/** liste de commandes à executer dans l'ordre si les conditions sont vérifiées */
	public final ArrayList<CommandeEvent> commandes;
	 /**
	  * Le curseur indique quelle commande executer.
	  * Il se déplace incrémentalement, mais on peut lui faire faire des sauts.
	  */
	public int curseurCommandes = 0;
	
	//apparence
	public String nomImage;
	public BufferedImage image = null;
	
	//paramètres
	public boolean animeALArret = false;
	public boolean animeEnMouvement = true;
	public boolean traversable = false;
	public boolean auDessusDeTout = false;
	public int vitesse = Event.VITESSE_PAR_DEFAUT;
	public int frequence = Event.FREQUENCE_PAR_DEFAUT;
	
	//mouvement
	public final ArrayList<CommandeEvent> deplacement;
	public boolean repeterLeDeplacement = true;
	public boolean ignorerLesMouvementsImpossibles = false;
	
	/**
	 * Constructeur explicite de la Page de comportement.
	 * @param conditions de déclenchement de la page
	 * @param commandes à executer si la page est déclenchée
	 * @param nomImage nom du fichier image pour l'apparence
	 * @param deplacement naturel de l'Event si la page est activée
	 */
	public PageDeComportement(final ArrayList<Condition> conditions, final ArrayList<CommandeEvent> commandes, 
			final String nomImage, final ArrayList<CommandeEvent> deplacement) {
		this.conditions = conditions;
		this.commandes = commandes;
		this.nomImage = nomImage;
		this.deplacement = deplacement;
		//ouverture de l'image d'apparence
		try {
			this.image = ImageIO.read(new File(".\\ressources\\Graphics\\Characters\\"+nomImage));
		} catch (IOException e) {
			//l'image d'apparence n'existe pas
			e.printStackTrace();
		}
		//on précise si c'est une page qui s'ouvre en parlant à l'évent
		if (conditions!=null) {
			for (Condition cond : conditions) {
				//TODO dans le futur il y aura aussi la condition "arrivée sur la case" en plus de "parler" :
				if (cond.getClass().getName().equals("conditions.ConditionParler")) {
					this.sOuvreParParole = true;
				}
			}
		}
	}
	
	/**
	 * La page de comportement est créée à partir du fichier JSON.
	 * @param pageJSON objet JSON décrivant la page de comportements
	 */
	public PageDeComportement(final JSONObject pageJSON) {
		//conditions de déclenchement de la page
		final ArrayList<Condition> conditions = new ArrayList<Condition>();
		for (Object conditionJSON : pageJSON.getJSONArray("conditions")) {
			try {
				final Class<?> classeCondition = Class.forName("conditions.Condition"+((JSONObject) conditionJSON).get("nom"));
				final Constructor<?> constructeurCondition = classeCondition.getConstructor();
				final Condition condition = (Condition) constructeurCondition.newInstance();
				conditions.add(condition);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		this.conditions = conditions;
		
		//commandes de la page
		final ArrayList<CommandeEvent> commandes = new ArrayList<CommandeEvent>();
		for (Object commandeJSON : pageJSON.getJSONArray("commandes")) {
			try {
				final Class<?> classeCommande = Class.forName("comportementEvent."+((JSONObject) commandeJSON).get("nom"));
				final Iterator<String> parametresNoms = ((JSONObject) commandeJSON).keys();
				String parametreNom;
				Object parametreValeur;
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
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		this.commandes = commandes;
		
		
		//apparence de l'event lors de cette page
		this.nomImage = (String) pageJSON.get("image");
		
		//propriétés de l'event lors de cette page
		this.animeALArret = (boolean) pageJSON.get("animeALArret");
		this.animeEnMouvement = (boolean) pageJSON.get("animeEnMouvement");
		this.traversable = (boolean) pageJSON.get("traversable");
		this.auDessusDeTout = (boolean) pageJSON.get("auDessusDeTout");
		this.vitesse = (int) pageJSON.get("vitesse");
		this.frequence = (int) pageJSON.get("frequence");
		
		//mouvement de l'event lors de cette page
		this.deplacement = new ArrayList<CommandeEvent>();
		for (Object actionDeplacementJSON : pageJSON.getJSONArray("deplacement")) {
			try {
				final Class<?> classeActionDeplacement = Class.forName("comportementEvent."+((JSONObject) actionDeplacementJSON).get("nom"));
				final Iterator<String> parametresNoms = ((JSONObject) actionDeplacementJSON).keys();
				String parametreNom;
				Object parametreValeur;
				final HashMap<String, Object> parametres = new HashMap<String, Object>();
				while (parametresNoms.hasNext()) {
					parametreNom = parametresNoms.next();
					if (!parametreNom.equals("nom")) { //le nom servait à trouver la classe, ici on ne s'intéresse qu'aux paramètres
						parametreValeur = ((JSONObject) actionDeplacementJSON).get(parametreNom);
						parametres.put(parametreNom, parametreValeur);
					}
				}
				final Constructor<?> constructeurActionDeplacement = classeActionDeplacement.getConstructor(HashMap.class);
				final CommandeEvent actionDeplacement = (CommandeEvent) constructeurActionDeplacement.newInstance(parametres);
				deplacement.add(actionDeplacement);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		this.repeterLeDeplacement = (boolean) pageJSON.get("repeterLeDeplacement");
		this.ignorerLesMouvementsImpossibles = (boolean) pageJSON.get("ignorerLesMouvementsImpossibles");
		
		//ouverture de l'image d'apparence
		try {
			this.image = ImageIO.read(new File(".\\ressources\\Graphics\\Characters\\"+nomImage));
		} catch (IOException e) {
			//l'image d'apparence n'existe pas
			//e.printStackTrace();
		}
		//on précise si c'est une page qui s'ouvre en parlant à l'event
		if (conditions!=null) {
			for (Condition cond : conditions) {
				//TODO dans le futur il y aura aussi la condition "arrivée sur la case" en plus de "parler" :
				if (cond.getClass().getName().equals("conditions.ConditionParler")) {
					this.sOuvreParParole = true;
				}
			}
		}
	}

	/**
	 * Executer la Page de comportement.
	 * C'est-à-dire que les conditions de déclenchement ont été réunies.
	 * On va donc lire les commandes une par une avec un curseur.
	 */
	public final void executer() {
		//si la page est une page "Parler", elle active le stopEvent qui fige tous les events
		if (sOuvreParParole) {
			this.event.map.lecteur.stopEvent = true;
		}
		//lecture des commandes event
		if (commandes!=null) {
			try {
				if (curseurCommandes >= commandes.size()) {
					curseurCommandes = 0;
					if (sOuvreParParole) {
						this.event.map.lecteur.stopEvent = false; //on désactive le stopEvent si fin de la page
					}
				}
				boolean onAvanceDansLesCommandes = true;
				while (onAvanceDansLesCommandes) {
					final int ancienCurseur = curseurCommandes;
					curseurCommandes = this.commandes.get(curseurCommandes).executer(curseurCommandes, commandes);
					if (curseurCommandes==ancienCurseur) { 
						//le curseur n'a pas changé, c'est donc une commande qui prend du temps
						onAvanceDansLesCommandes = false;
					}
				}
			} catch (IndexOutOfBoundsException e) {
				//on a fini la page
				curseurCommandes = 0;
				if (sOuvreParParole) {
					this.event.map.lecteur.stopEvent = false; //on désactive le stopEvent si fin de la page
				}
				this.event.pageActive = null; 
			}
		}
	}
	
}
