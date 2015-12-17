package comportementEvent;

import java.util.ArrayList;

import son.LecteurAudio;
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
		Arme armeActuelle = Partie.getArmeEquipee();
		heros.animationAttaque = armeActuelle.framesDAnimation.size();
		LecteurAudio.playSe(armeActuelle.nomEffetSonoreAttaque);
		return curseurActuel+1;
	}

}
