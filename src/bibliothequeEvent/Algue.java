package bibliothequeEvent;

import java.util.ArrayList;

import comportementEvent.CommandeEvent;
import comportementEvent.ModifierApparence;
import conditions.Condition;
import conditions.ConditionDansZoneDAttaque;
import conditions.ConditionPendantDureeDAttaque;
import map.Event;
import map.Map;
import map.PageDeComportement;

public class Algue extends Event{
	ArrayList<CommandeEvent> deplacementParDefaut;
	
	public Algue(Map map, int x, int y){
		super(map, x,y, "algue", getPages(), 24);
	}
	
	public static ArrayList<PageDeComportement> getPages(){
		ArrayList<PageDeComportement> pages = new  ArrayList<PageDeComportement>();
		//page 0
		PageDeComportement page0 = new PageDeComportement(null, null, "algues character.png", null);
		page0.animeALArret = true;
		pages.add(page0);
		//page 1
			//conditions de déclenchement
			ArrayList<Condition> conditions = new ArrayList<Condition>();
			Condition cond1 = new ConditionDansZoneDAttaque(); conditions.add(cond1);
			Condition cond2 = new ConditionPendantDureeDAttaque(); conditions.add(cond2);
			//commandes event à executer
			ArrayList<CommandeEvent> commandes = new ArrayList<CommandeEvent>();
			CommandeEvent comm0 = new ModifierApparence("daruma character.png"); commandes.add(comm0);
			//CommandeEvent comm6 = new SupprimerEvent(); commandes.add(comm6);
		PageDeComportement page1 = new PageDeComportement(conditions, commandes, "algues character.png", null);
		page1.animeALArret = true;
		pages.add(page1);
		//fin page 1
		return pages;
	}
}
