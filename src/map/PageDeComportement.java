package map;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import javax.imageio.ImageIO;

import org.json.JSONException;
import org.json.JSONObject;

import comportementEvent.CommandeEvent;
import conditions.Condition;

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
		// TODO est-ce que tout event ne devrait pas avoir une page vierge au début ?

		
		//conditions
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
		
		ArrayList<CommandeEvent> commandes = new ArrayList<CommandeEvent>();
		for(Object commandeJSON : pageJSON.getJSONArray("commandes")){
			try{
				Class<?> classeCommande = Class.forName("comportementEvent."+((JSONObject) commandeJSON).get("nom"));
				String parametre1 = (String) ((JSONObject) commandeJSON).get("texte"); //TODO faire bien
				
				Constructor<?> constructeurCommande = classeCommande.getConstructor(String.class);
				CommandeEvent commande = (CommandeEvent) constructeurCommande.newInstance(parametre1);
				commandes.add(commande);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		this.commandes = commandes;
		
		
		
		this.nomImage = (String) pageJSON.get("image");
		
		this.animeALArret = (Boolean) pageJSON.get("animeALArret");
		this.animeEnMouvement = (Boolean) pageJSON.get("animeEnMouvement");
		this.traversable = (Boolean) pageJSON.get("traversable");
		System.out.println("traversable ? "+this.traversable);
		this.auDessusDeTout = (Boolean) pageJSON.get("auDessusDeTout");
		this.vitesse = (Integer) pageJSON.get("vitesse");
		this.frequence = (Integer) pageJSON.get("frequence");
		
		this.deplacement = null; //TODO faire ça bien
		this.repeterLeDeplacement = (Boolean) pageJSON.get("repeterLeDeplacement");
		this.ignorerLesMouvementsImpossibles = (Boolean) pageJSON.get("ignorerLesMouvementsImpossibles");
		
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
				this.event.activerUnePage();
			}
		}
	}
	
}
