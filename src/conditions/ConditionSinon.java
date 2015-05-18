package conditions;

public class ConditionSinon extends Condition{

	public ConditionSinon(int numero){
		this.numero = numero;
	}
	
	@Override
	public Boolean estVerifiee() {
		return false;
	}

}
