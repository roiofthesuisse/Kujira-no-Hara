package comportementEvent;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

import map.LecteurMap;
import map.Map;

/**
 * Le Heros est téléporté sur une autre Map.
 */
public class ChangerDeMap extends CommandeEvent {
	private final int numeroNouvelleMap;
	private final int xDebutHeros;
	private final int yDebutHeros;
	
	/**
	 * Constructeur explicite
	 * @param numeroNouvelleMap numéro de la nouvelle Map
	 * @param xDebutHeros coordonnée x du Héros à son arrivée sur la Map
	 * @param yDebutHeros coordonnée y du Héros à son arrivée sur la Map
	 */
	public ChangerDeMap(final int numeroNouvelleMap, final int xDebutHeros, final int yDebutHeros) {
		this.numeroNouvelleMap = numeroNouvelleMap;
		this.xDebutHeros = xDebutHeros;
		this.yDebutHeros = yDebutHeros;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public ChangerDeMap(final HashMap<String, Object> parametres) {
		this( (Integer) parametres.get("numeroNouvelleMap"), 
			(Integer) parametres.get("xDebutHeros"),
			(Integer) parametres.get("yDebutHeros")
		);
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<CommandeEvent> commandes) {
		final LecteurMap lecteur = commandes.get(0).page.event.map.lecteur;
		final int directionHeros = this.page.event.map.heros.direction;
		try {
			lecteur.changerMap(new Map(numeroNouvelleMap, lecteur, xDebutHeros, yDebutHeros, directionHeros));
		} catch (FileNotFoundException e) {
			System.err.println("Impossible de charger la map numero "+numeroNouvelleMap);
			e.printStackTrace();
		}
		return curseurActuel+1;
	}

}
