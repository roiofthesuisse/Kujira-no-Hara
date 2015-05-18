package comportementEvent;

import java.util.ArrayList;

import main.Arme;
import main.Partie;
import map.Heros;

public class DemarrerAnimationAttaque extends CommandeEvent {
	
	public DemarrerAnimationAttaque(){
		
	}
	
	@Override
	public int executer(int curseurActuel, ArrayList<CommandeEvent> commandes) {
		if(this.page == null){
			System.out.println("page nulle");
		}
		Heros heros = this.page.event.map.heros;
		int idArme = Partie.idArmeEquipee;
		heros.animationAttaque = Arme.getArme(idArme).framesDAnimation.size();
		return curseurActuel+1;
	}

}
