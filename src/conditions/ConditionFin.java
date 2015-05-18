package conditions;

public class ConditionFin extends Condition{

	public ConditionFin(int numero){
		this.numero = numero;
	}
	
	@Override
	public Boolean estVerifiee() {
		return true;
	}

}
