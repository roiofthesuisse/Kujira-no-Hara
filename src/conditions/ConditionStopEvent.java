package conditions;

public class ConditionStopEvent extends Condition{
	boolean valeurQuIlEstCenseAvoir;
	
	public ConditionStopEvent(boolean valeur){
		this.valeurQuIlEstCenseAvoir = valeur;
	}
	
	@Override
	public Boolean estVerifiee() {
		return page.event.map.lecteur.stopEvent == valeurQuIlEstCenseAvoir;
	}

}