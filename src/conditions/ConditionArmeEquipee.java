package conditions;

import main.Partie;

public class ConditionArmeEquipee extends Condition{
	public int idArme;
	
	public ConditionArmeEquipee(int idArme){
		this.idArme = idArme;
	}
	
	@Override
	public Boolean estVerifiee() {
		if(Partie.idArmesPossedees.size()>0){
			return Partie.getArmeEquipee().id == this.idArme;
		}
		return false; //aucune arme possédée
	}

}