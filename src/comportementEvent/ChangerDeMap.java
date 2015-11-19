package comportementEvent;

import java.io.FileNotFoundException;
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
	public int executer(int curseurActuel, ArrayList<CommandeEvent> commandes){
		LecteurMap lecteur = commandes.get(0).page.event.map.lecteur;
		try {
			lecteur.changerMap(new Map(numeroNouvelleMap,lecteur,xDebutHeros,yDebutHeros));
		} catch (FileNotFoundException e) {
			System.err.println("Impossible de charger la map numero "+numeroNouvelleMap);
			e.printStackTrace();
		}
		return curseurActuel+1;
	}

}
