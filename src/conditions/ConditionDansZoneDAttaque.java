package conditions;

import main.Partie;
import map.Event;
import map.Heros;
import map.Hitbox;

public class ConditionDansZoneDAttaque extends Condition{
	
	public ConditionDansZoneDAttaque(){}
	
	@Override
	public Boolean estVerifiee() {
		Boolean estCeQueLeHerosAUneArme = (Partie.idArmesPossedees.size() > 0);
		if(estCeQueLeHerosAUneArme){
			Heros heros = this.page.event.map.heros;
			Event event = this.page.event;
			Boolean reponse = Hitbox.estDansZoneDAttaque(event,heros);
			return reponse;
		}else{
			return false;
		}
	}

}