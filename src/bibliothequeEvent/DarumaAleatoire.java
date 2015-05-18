package bibliothequeEvent;

import java.util.ArrayList;

import comportementEvent.AjouterArme;
import comportementEvent.Attendre;
import comportementEvent.AvancerAleatoirement;
import comportementEvent.ChangerDeMap;
import comportementEvent.CommandeEvent;
import comportementEvent.Message;
import comportementEvent.TeleporterEvent;
import conditions.Condition;
import conditions.ConditionParler;
import conditions.ConditionToucheAction;
import map.Event;
import map.LecteurMap;
import map.Map;
import map.PageDeComportement;

public class DarumaAleatoire extends Event{
	ArrayList<CommandeEvent> deplacementParDefaut;
	
	public DarumaAleatoire(Map map, int x, int y){
		super(map, x,y, "daruma", getPages(), 32);
		setIgnorerLesMouvementsImpossibles(true);
	}
	
	public static ArrayList<PageDeComportement> getPages(){
		ArrayList<PageDeComportement> pages = new  ArrayList<PageDeComportement>();
		//page 0
			//deplacement
			ArrayList<CommandeEvent> deplacement = new ArrayList<CommandeEvent>();
			deplacement.add(new AvancerAleatoirement());
		PageDeComportement page0 = new PageDeComportement(null, null, "daruma character.png", deplacement);
		page0.ignorerLesMouvementsImpossibles=true;
		page0.vitesse=2;
		pages.add(page0);
		//page 1
			//conditions de déclenchement
			ArrayList<Condition> conditions = new ArrayList<Condition>();
			Condition cond1 = new ConditionParler(); conditions.add(cond1);
			Condition cond2 = new ConditionToucheAction(); conditions.add(cond2);
			//commandes event à executer
			ArrayList<CommandeEvent> commandes = new ArrayList<CommandeEvent>();
			CommandeEvent comm1 = new Message("lala lala\\n \\c[02]lele\\c[01] lele\\n popo\\n pr\\c[02]out\\c[01]"); commandes.add(comm1);
			CommandeEvent comm2 = new Message("\\c[02]lili"); commandes.add(comm2);
			CommandeEvent comm3 = new Message("lala"); commandes.add(comm3);
			CommandeEvent comm4 = new AjouterArme(0); commandes.add(comm4);
			CommandeEvent comm5 = new Attendre(5); commandes.add(comm5);
			//CommandeEvent comm5 = new Message("lélé"); commandes.add(comm5);
			//CommandeEvent comm6 = new ChangerDeMap(1,3,3); commandes.add(comm6);
		PageDeComportement page1 = new PageDeComportement(conditions, commandes, "daruma character.png", null);
		pages.add(page1);
		//fin page 1
		return pages;
	}
}
