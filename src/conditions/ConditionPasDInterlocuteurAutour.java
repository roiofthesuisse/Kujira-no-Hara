package conditions;

import java.util.ArrayList;

import map.Event;
import map.PageDeComportement;

public class ConditionPasDInterlocuteurAutour extends Condition {
	
	@Override
	public Boolean estVerifiee() {
		ArrayList<Event> events = this.page.event.map.events;
		for(Event event : events){
			if(event.pages!=null){
				for(PageDeComportement page : event.pages){
					if(page.conditions!=null){
						for(Condition condition : page.conditions){
							if(condition.getClass().getName().equals(ConditionParler.class.getName())){
								if(condition.estVerifiee()){
									return false;
								}
							}
						}
					}
				}
			}
		}
		return true;
	}

}
