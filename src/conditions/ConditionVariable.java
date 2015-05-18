package conditions;

public class ConditionVariable extends Condition{
	int numeroVariable;
	int typeInegalite;
	int valeurQuIlEstCenseAvoir;
	
	/**
	 * @param numeroVariable numéro de la variable
	 * @param inegalite 0 egal ; 1 superieur large ; 2 inferieur large ; 3 superieur strict ; 4 inferieur strict ; 5 différent
	 * @param valeur comparative
	 * @param numero de la condition
	 */
	public ConditionVariable(int numeroVariable, int inegalite, int valeur, int numero){
		this.numeroVariable = numeroVariable;
		this.typeInegalite = inegalite;
		this.valeurQuIlEstCenseAvoir = valeur;
		this.numero = numero;
	}
	
	@Override
	public Boolean estVerifiee() {
		int[] variables = this.page.event.map.lecteur.fenetre.partie.variables;
		switch(typeInegalite){
			case 0 : return variables[numeroVariable]==valeurQuIlEstCenseAvoir;
			case 1 : return variables[numeroVariable]>=valeurQuIlEstCenseAvoir;
			case 2 : return variables[numeroVariable]<=valeurQuIlEstCenseAvoir;
			case 3 : return variables[numeroVariable]>valeurQuIlEstCenseAvoir;
			case 4 : return variables[numeroVariable]<valeurQuIlEstCenseAvoir;
			default : return variables[numeroVariable]!=valeurQuIlEstCenseAvoir;
		}
	}

}