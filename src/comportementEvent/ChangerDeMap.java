package comportementEvent;

import java.util.ArrayList;

import map.LecteurMap;
import map.Map;

public class ChangerDeMap extends CommandeEvent {
	int numeroNouvelleMap;
	int xDebutHeros;
	int yDebutHeros;
	
	public ChangerDeMap(int numeroNouvelleMap, int xDebutHeros, int yDebutHeros){
		this.numeroNouvelleMap = numeroNouvelleMap;
		this.xDebutHeros = xDebutHeros;
		this.yDebutHeros = yDebutHeros;
	}
	
	@Override
	public int executer(int curseurActuel, ArrayList<CommandeEvent> commandes) {
		LecteurMap lecteur = commandes.get(0).page.event.map.lecteur;
		lecteur.changerMap(new Map(numeroNouvelleMap,lecteur,xDebutHeros,yDebutHeros));
		return curseurActuel+1;
	}

}
