package commandes;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

import main.Commande;
import main.Fenetre;
import map.LecteurMap;
import map.Map;
import map.PageEvent;

/**
 * Le Heros est téléporté sur une autre Map.
 */
public class ChangerDeMap implements CommandeEvent {
	private PageEvent page;
	
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
		this( (int) parametres.get("numeroNouvelleMap"), 
			(int) parametres.get("xDebutHeros"),
			(int) parametres.get("yDebutHeros")
		);
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<? extends Commande> commandes) {
		final LecteurMap nouveauLecteur = new LecteurMap(Fenetre.getFenetre());
		final int directionHeros = this.page.event.map.heros.direction;
		try {
			nouveauLecteur.changerMap(new Map(numeroNouvelleMap, nouveauLecteur, xDebutHeros, yDebutHeros, directionHeros));
		} catch (FileNotFoundException e) {
			System.err.println("Impossible de charger la map numero "+numeroNouvelleMap);
			e.printStackTrace();
		}
		return curseurActuel+1;
	}
	
	@Override
	public final PageEvent getPage() {
		return this.page;
	}

	@Override
	public final void setPage(final PageEvent page) {
		this.page = page;
	}

}
