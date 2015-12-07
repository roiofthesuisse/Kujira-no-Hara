package map;

import java.util.ArrayList;

import comportementEvent.CommandeEvent;

public class AnimeALArret extends CommandeEvent {
	private boolean valeur;
	
	public AnimeALArret(boolean valeur){
		this.valeur = valeur;
	}
	@Override
	public int executer(int curseurActuel, ArrayList<CommandeEvent> commandes) {
		this.page.animeALArret = this.valeur;
		return curseurActuel+1;
	}

}
