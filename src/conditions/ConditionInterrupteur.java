package conditions;

public class ConditionInterrupteur extends Condition{
	int numeroInterrupteur;
	boolean valeurQuIlEstCenseAvoir;
	
	public ConditionInterrupteur(int numeroInterrupteur, boolean valeur, int numeroCondition){
		this.numeroInterrupteur = numeroInterrupteur;
		this.valeurQuIlEstCenseAvoir = valeur;
		this.numero = numeroCondition;
	}
	
	@Override
	public Boolean estVerifiee() {
		return page.event.map.lecteur.fenetre.partie.interrupteurs[numeroInterrupteur] == valeurQuIlEstCenseAvoir;
	}

}