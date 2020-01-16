package commandes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import main.Commande;
import main.Main;
import map.Event;
import map.Map;
import map.PageEvent;

public class InvoquerUnEvent extends Commande implements CommandeEvent {

	private final int idMap, idEvent, x, y;
	
	/**
	 * Constructeur explicite
	 * 
	 * @param x coordonnee (case) ou placer la copie
	 * @param y coordonnee (case) ou placer la copie
	 * @param idMap id de la Map ou se trouve l'Event a imiter
	 * @param idEvent id de l'Event a imiter
	 */
	public InvoquerUnEvent(final int  x,final int  y, final int idMap, final int idEvent) {
		this.idMap = idMap;
		this.idEvent = idEvent;
		this.x = x;
		this.y = y;
	}

	/**
	 * Constructeur générique
	 * 
	 * @param parametres liste de paramètres issus de JSON
	 */
	public InvoquerUnEvent(final HashMap<String, Object> parametres) {
		this(
			(int) parametres.get("x"),
			(int) parametres.get("y"),
			(int) parametres.get("idMap"),
			(int) parametres.get("idEvent")
		);
	}
	
	@Override
	public int executer(int curseurActuel, List<Commande> commandes) {
		// TODO fonction qui crée juste un Event à partir du JSON de la Map
		// (pas la peine de créer toute la Map)
		return 0;
	}

}
