package mouvements;

import java.util.HashMap;

import main.Fenetre;
import map.Event;
import map.Event.Direction;

/**
 * Déplacer un Event d'un pas dans la direction opposée à la direction de l'Event.
 */
public class PasEnArriere extends Avancer {
	
	/**
	 * Constructeur explicite
	 * @param nombreDePixels ditance parcourue à reculons (en pixels)
	 */
	public PasEnArriere(final int nombreDePixels) {
		//le -1 est bidon, il sera remplacé par la direction de l'Event lors de la vérification
		super(-1, nombreDePixels);
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public PasEnArriere(final HashMap<String, Object> parametres) {
		this( (parametres.containsKey("nombreDeCarreaux") ? (int) parametres.get("parametres") : 1) * Fenetre.TAILLE_D_UN_CARREAU);
	}
	
	/**
	 * Le mouvement dans cette Direction est-il possible ?
	 * @return si le mouvement est possible oui ou non
	 */
	@Override
	public final boolean mouvementPossible() {
		//on peut avancer à reculons si on peut avancer en arrière
		final Event event = this.deplacement.getEventADeplacer();
		this.direction = event.direction; //la direction affichée durant le Mouvement est celle de l'Event avant
		final Avancer mouvementFictif = new Avancer(calculerDirectionOpposee(event.direction), Fenetre.TAILLE_D_UN_CARREAU);
		mouvementFictif.deplacement = this.deplacement; //Deplacement pour éviter la NullPointerException dans Avancer
		
		//puis on lance la vérification traditionnelle
		return mouvementFictif.mouvementPossible();
	}
	
	@Override
	public final void calculDuMouvement(final Event event) {
		event.avance = true;
		
		//déplacement :
		switch (this.direction) {
			case Direction.BAS : 
				event.y -= event.vitesseActuelle; 
				break;
			case Direction.GAUCHE : 
				event.x += event.vitesseActuelle; 
				break;
			case Direction.DROITE : 
				event.x -= event.vitesseActuelle; 
				break;
			case Direction.HAUT : 
				event.y += event.vitesseActuelle; 
				break;
		}
		this.ceQuiAEteFait += event.vitesseActuelle;
	}

}
