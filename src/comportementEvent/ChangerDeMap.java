package comportementEvent;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import map.LecteurMap;
import map.Map;
import menu.Parametre;

public class ChangerDeMap extends CommandeEvent {
	private final int numeroNouvelleMap;
	private final int xDebutHeros;
	private final int yDebutHeros;
	
	/**
	 * Constructeur spécifique
	 */
	public ChangerDeMap(int numeroNouvelleMap, int xDebutHeros, int yDebutHeros){
		this.numeroNouvelleMap = numeroNouvelleMap;
		this.xDebutHeros = xDebutHeros;
		this.yDebutHeros = yDebutHeros;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public ChangerDeMap(ArrayList<Parametre> parametres){
		this( (Integer)trouverParametre("numeroNouvelleMap", parametres), 
			(Integer)trouverParametre("xDebutHeros", parametres),
			(Integer)trouverParametre("yDebutHeros", parametres)
		);
	}
	
	@Override
	public int executer(int curseurActuel, ArrayList<CommandeEvent> commandes){
		LecteurMap lecteur = commandes.get(0).page.event.map.lecteur;
		int directionHeros = this.page.event.map.heros.direction;
		try {
			lecteur.changerMap(new Map(numeroNouvelleMap,lecteur,xDebutHeros,yDebutHeros,directionHeros));
		} catch (FileNotFoundException e) {
			System.err.println("Impossible de charger la map numero "+numeroNouvelleMap);
			e.printStackTrace();
		}
		return curseurActuel+1;
	}

}
