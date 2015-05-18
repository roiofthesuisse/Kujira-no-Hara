package comportementEvent;

import java.util.ArrayList;

import map.Event;

public class Avancer extends CommandeEvent{
	protected int direction;
	public int nombreDeCarreaux;
	public int ceQuiAEteFait = 0; //avancée en pixel, doit atteindre nombreDeCarreaux*32
	
	public Avancer(int direction, int nombreDeCarreaux){
		this.direction = direction;
		this.nombreDeCarreaux = nombreDeCarreaux;
	}
	
	public int getDirection(){
		return direction;
	}
	
	public void reinitialiser(){
		ceQuiAEteFait = 0;
	}

	@Override
	public int executer(int curseurActuel, ArrayList<CommandeEvent> commandes) {
		Event event = this.page.event;
		if(ceQuiAEteFait >= nombreDeCarreaux*32){
			event.avance = false; //le mouvement est terminé
			return curseurActuel+1;
		}
		event.avance = true; //le mouvement est en cours
		event.deplacer();
		return curseurActuel;
	}
}
