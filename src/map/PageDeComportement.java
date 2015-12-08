package map;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.ImageIO;

import org.json.JSONException;
import org.json.JSONObject;

import comportementEvent.CommandeEvent;
import conditions.Condition;
import menu.Parametre;

public class PageDeComportement {
	public Event event;
	public int numero;
	private Boolean sOuvreParParole = false; //équivalent à posséder la condition de déclenchement "parler"
	
	//conditions de déclenchement
	public final ArrayList<Condition> conditions;
	
	//liste de commandes
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
	public Boolean animeALArret = false;
	public Boolean animeEnMouvement = true;
	public Boolean traversable = false;
	public Boolean auDessusDeTout = false;
	public int vitesse = 4;
	public int frequence = 4;
	
	//mouvement
	public final ArrayList<CommandeEvent> deplacement;
	public Boolean repeterLeDeplacement = true;
	public Boolean ignorerLesMouvementsImpossibles = false;
	
	public PageDeComportement(ArrayList<Condition> conditions, ArrayList<CommandeEvent> commandes, String nomImage,
			ArrayList<CommandeEvent> deplacement){
		this.conditions = conditions;
		this.commandes = commandes;
		this.nomImage = nomImage;
		this.deplacement = deplacement;
		//ouverture de l'image d'apparence
		try {
			this.image = ImageIO.read(new File(".\\ressources\\Graphics\\Characters\\"+nomImage));
		} catch (IOException e) {
			System.out.println("Erreur lors de l'ouverture de l'apparence de l'event :");
			e.printStackTrace();
		}
		//on précise si c'est une page qui s'ouvre en parlant à l'évent
		if(conditions!=null){
			for(Condition cond : conditions){
				//TODO dans le futur il y aura aussi la condition "arrivée sur la case" en plus de "parler" :
				if(cond.getClass().getName().equals("conditions.ConditionParler")){
					this.sOuvreParParole = true;
				}
			}
		}
	}
	
	/**
	 * La page de comportement est créée à partir du fichier JSON.
	 * @param pageJSON objet JSON décrivant la page de comportements
	 * @throws JSONException 
	 * @throws ClassNotFoundException 
	 */
	public PageDeComportement(JSONObject pageJSON) {
		//conditions de déclenchement de la page
		ArrayList<Condition> conditions = new ArrayList<Condition>();
		for(Object conditionJSON : pageJSON.getJSONArray("conditions")){
			try{
				Class<?> classeCondition = Class.forName("conditions.Condition"+((JSONObject) conditionJSON).get("nom"));
				Constructor<?> constructeurCondition = classeCondition.getConstructor();
				Condition condition = (Condition) constructeurCondition.newInstance();
				conditions.add(condition);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		this.conditions = conditions;
		
		//commandes de la page
		ArrayList<CommandeEvent> commandes = new ArrayList<CommandeEvent>();
		for(Object commandeJSON : pageJSON.getJSONArray("commandes")){
			try{
				Class<?> classeCommande = Class.forName("comportementEvent."+((JSONObject) commandeJSON).get("nom"));
				Iterator<String> parametresNoms = ((JSONObject) commandeJSON).keys();
				String parametreNom;
				Object parametreValeur;
				ArrayList<Parametre> parametres = new ArrayList<Parametre>();
				while(parametresNoms.hasNext()){
					parametreNom = parametresNoms.next();
					if(!parametreNom.equals("nom")){ //le nom servait à trouver la classe, ici on ne s'intéresse qu'aux paramètres
						parametreValeur = ((JSONObject) commandeJSON).get(parametreNom);
						parametres.add( new Parametre(parametreNom, parametreValeur) );
					}
				}
				Constructor<?> constructeurCommande = classeCommande.getConstructor(parametres.getClass());
				CommandeEvent commande = (CommandeEvent) constructeurCommande.newInstance(parametres);
				commandes.add(commande);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		this.commandes = commandes;
		
		
		//apparence de l'event lors de cette page
		this.nomImage = (String) pageJSON.get("image");
		
		//propriétés de l'event lors de cette page
		this.animeALArret = (Boolean) pageJSON.get("animeALArret");
		this.animeEnMouvement = (Boolean) pageJSON.get("animeEnMouvement");
		this.traversable = (Boolean) pageJSON.get("traversable");
		this.auDessusDeTout = (Boolean) pageJSON.get("auDessusDeTout");
		this.vitesse = (Integer) pageJSON.get("vitesse");
		this.frequence = (Integer) pageJSON.get("frequence");
		
		//mouvement de l'event lors de cette page
		this.deplacement = new ArrayList<CommandeEvent>();
		for(Object actionDeplacementJSON : pageJSON.getJSONArray("deplacement")){
			try{
				Class<?> classeActionDeplacement = Class.forName("comportementEvent."+((JSONObject) actionDeplacementJSON).get("nom"));
				Iterator<String> parametresNoms = ((JSONObject) actionDeplacementJSON).keys();
				String parametreNom;
				Object parametreValeur;
				ArrayList<Parametre> parametres = new ArrayList<Parametre>();
				while(parametresNoms.hasNext()){
					parametreNom = parametresNoms.next();
					if(!parametreNom.equals("nom")){ //le nom servait à trouver la classe, ici on ne s'intéresse qu'aux paramètres
						parametreValeur = ((JSONObject) actionDeplacementJSON).get(parametreNom);
						parametres.add( new Parametre(parametreNom, parametreValeur) );
					}
				}
				Constructor<?> constructeurActionDeplacement = classeActionDeplacement.getConstructor(parametres.getClass());
				CommandeEvent actionDeplacement = (CommandeEvent) constructeurActionDeplacement.newInstance(parametres);
				deplacement.add(actionDeplacement);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		this.repeterLeDeplacement = (Boolean) pageJSON.get("repeterLeDeplacement");
		this.ignorerLesMouvementsImpossibles = (Boolean) pageJSON.get("ignorerLesMouvementsImpossibles");
		
		//ouverture de l'image d'apparence
		try {
			this.image = ImageIO.read(new File(".\\ressources\\Graphics\\Characters\\"+nomImage));
		} catch (IOException e) {
			System.out.println("Erreur lors de l'ouverture de l'apparence de l'event :");
			e.printStackTrace();
		}
		//on précise si c'est une page qui s'ouvre en parlant à l'event
		if(conditions!=null){
			for(Condition cond : conditions){
				//TODO dans le futur il y aura aussi la condition "arrivée sur la case" en plus de "parler" :
				if(cond.getClass().getName().equals("conditions.ConditionParler")){
					this.sOuvreParParole = true;
				}
			}
		}
	}

	public void executer() {
		//si la page est une page "Parler", elle active le stopEvent qui fige tous les events
		if(sOuvreParParole){
			this.event.map.lecteur.stopEvent = true;
		}
		//lecture des commandes event
		if(commandes!=null){
			try{
				if(curseurCommandes >= commandes.size()){
					curseurCommandes = 0;
					if(sOuvreParParole){
						this.event.map.lecteur.stopEvent = false; //on désactive le stopEvent si fin de la page
					}
				}
				Boolean onAvanceDansLesCommandes = true;
				while(onAvanceDansLesCommandes){
					int ancienCurseur = curseurCommandes;
					curseurCommandes = this.commandes.get(curseurCommandes).executer(curseurCommandes,commandes);
					if(curseurCommandes==ancienCurseur){ 
						//le curseur n'a pas changé, c'est donc une commande qui prend du temps
						onAvanceDansLesCommandes = false;
					}
				}
			}catch(IndexOutOfBoundsException e){
				//on a fini la page
				curseurCommandes = 0;
				if(sOuvreParParole){
					this.event.map.lecteur.stopEvent = false; //on désactive le stopEvent si fin de la page
				}
				this.event.pageActive = null; 
			}
		}
	}
	
}
