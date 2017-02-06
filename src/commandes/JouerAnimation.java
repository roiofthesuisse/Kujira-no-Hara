package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import main.Commande;
import main.Fenetre;
import map.Event;
import map.LecteurMap;

/**
 * Jouer une Animation sur la Map.
 * Une Animation est une succession de vignettes.
 * Une Animation peut cibler un Event, ou bien être positionnée en des coordonnées spécifiques.
 */
public class JouerAnimation extends Commande implements CommandeEvent {
	public final int numeroAnimation;
	public final int xEcran, yEcran;
	
	public int frameActuelle;
	
	private JouerAnimation(final int numeroAnimation, final Integer idEvent, final int xEcran, final int yEcran) {
		this.numeroAnimation = numeroAnimation;
		if (idEvent != null && idEvent>=0) {
			//l'Animation est affichée sur un Event
			Event event = ((LecteurMap) Fenetre.getFenetre().lecteur).map.eventsHash.get(idEvent);
			this.xEcran = event.x;
			this.yEcran = event.y;
		} else {
			//l'Animation est affichée en des coordonnées spécifiées
			this.xEcran = xEcran;
			this.yEcran = yEcran;
		}
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public JouerAnimation(final HashMap<String, Object> parametres) {
		this( 	(int) parametres.get("numeroAnimation"),
				parametres.containsKey("idEvent") ? (int) parametres.get("idEvent") : null, //soit on utilise l'idEvent
				parametres.containsKey("idEvent") ? -1 : (int) parametres.get("xEcran"), //soit on utilise des coordonnées
				parametres.containsKey("idEvent") ? -1 : (int) parametres.get("yEcran")
		);
	}
	
	@Override
	public int executer(int curseurActuel, ArrayList<Commande> commandes) {
		this.frameActuelle = 0;
		Fenetre.getPartieActuelle().animations.add(this);
		return curseurActuel+1;
	}

}
