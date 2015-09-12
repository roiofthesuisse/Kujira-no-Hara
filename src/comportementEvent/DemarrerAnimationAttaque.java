package comportementEvent;

import java.util.ArrayList;

import main.Arme;
import main.Audio;
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
		Arme armeActuelle = Arme.getArme(Partie.idArmeEquipee);
		heros.animationAttaque = armeActuelle.framesDAnimation.size();
		Audio.playSE(armeActuelle.nomEffetSonoreAttaque);
		return curseurActuel+1;
	}

}
