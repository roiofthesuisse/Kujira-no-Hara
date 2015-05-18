package conditions;

import map.Event;

public class ConditionDirection extends Condition{
	Event eventConcerne;
	int directionVoulue;
	
	public ConditionDirection(Event event, int direction){
		this.eventConcerne = event;
		this.directionVoulue = direction;
	}
	
	@Override
	public Boolean estVerifiee() {
		return eventConcerne.direction==directionVoulue;
	}

}