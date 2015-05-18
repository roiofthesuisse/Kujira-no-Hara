package bibliothequeEvent;

import java.util.ArrayList;

import comportementEvent.CommandeEvent;
import comportementEvent.Message;
import comportementEvent.ModifierVariable;
import conditions.Condition;
import conditions.ConditionFin;
import conditions.ConditionParler;
import conditions.ConditionSinon;
import conditions.ConditionToucheAction;
import conditions.ConditionVariable;
import map.Event;
import map.Map;
import map.PageDeComportement;

public class Panneau extends Event{
	ArrayList<CommandeEvent> deplacementParDefaut;
	
	public Panneau(Map map, int x, int y){
		super(map, x,y, "panneau", getPages(), 32);
		setIgnorerLesMouvementsImpossibles(true);
	}
	
	public static ArrayList<PageDeComportement> getPages(){
		ArrayList<PageDeComportement> pages = new  ArrayList<PageDeComportement>();
		//page 0
		PageDeComportement page0 = new PageDeComportement(null, null, "mouton character.png", null);
		pages.add(page0);
		//page 1
			//conditions de déclenchement
			ArrayList<Condition> conditions = new ArrayList<Condition>();
			Condition cond1 = new ConditionParler(); conditions.add(cond1);
			Condition cond2 = new ConditionToucheAction(); conditions.add(cond2);
			//commandes event à executer
			ArrayList<CommandeEvent> commandes = new ArrayList<CommandeEvent>();
			CommandeEvent comm1 = new Message("bonjour"); commandes.add(comm1);
			CommandeEvent comm2 = new ConditionVariable(5,1,10,17); commandes.add(comm2);
			CommandeEvent comm3 = new Message("plus grand que 10"); commandes.add(comm3);
			CommandeEvent comm4 = new ConditionSinon(17); commandes.add(comm4);
			CommandeEvent comm5 = new Message("plus petit que 10"); commandes.add(comm5);
			CommandeEvent comm6 = new ConditionFin(17); commandes.add(comm6);
			CommandeEvent comm7 = new ModifierVariable(5,1,0,1,0); commandes.add(comm7);
		PageDeComportement page1 = new PageDeComportement(conditions, commandes, "mouton character.png", null);
		pages.add(page1);
		//fin page 1
		return pages;
	}
}
