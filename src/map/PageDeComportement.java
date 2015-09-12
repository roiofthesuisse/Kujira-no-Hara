package map;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import comportementEvent.CommandeEvent;
import conditions.Condition;
import conditions.ConditionParler;

public class PageDeComportement {
	public Event event;
	public int numero;
	public Boolean sOuvreParParole = false;
	
	//conditions de déclenchement
	public ArrayList<Condition> conditions;
	
	//liste de commandes
	public ArrayList<CommandeEvent> commandes;
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
	public int vitesse = 4;
	public int frequence = 4;
	
	//mouvement
	public ArrayList<CommandeEvent> deplacement;
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
					sOuvreParParole = true;
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
						ConditionParler.frameDeLaDerniereFermetureDUnePageQuiACetteCondition = this.event.map.lecteur.frameActuelle;
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
					ConditionParler.frameDeLaDerniereFermetureDUnePageQuiACetteCondition = this.event.map.lecteur.frameActuelle;
					this.event.map.lecteur.stopEvent = false; //on désactive le stopEvent si fin de la page
				}
				this.event.activerUnePage();
			}
		}
	}
	
}
