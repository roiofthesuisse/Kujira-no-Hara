package mouvements;

import java.util.HashMap;

import main.Main;
import map.Event;
import map.Event.Direction;
import utilitaire.Maths;

/**
 * Deplacer un Event d'un pas dans la direction oppos�e a la direction de l'Event.
 */
public class PasEnArriere extends Avancer {
	
	/**
	 * Constructeur explicite
	 * @param nombreDePixels ditance parcourue a reculons (en pixels)
	 */
	public PasEnArriere(final int nombreDePixels) {
		//le -1 est bidon, il sera remplac� par la direction de l'Event lors de la v�rification
		super(-1, nombreDePixels);
	}
	
	/**
	 * Constructeur generique
	 * @param parametres liste de parametres issus de JSON
	 */
	public PasEnArriere(final HashMap<String, Object> parametres) {
		this( (parametres.containsKey("nombreDeCarreaux") ? (int) parametres.get("parametres") : 1) * Main.TAILLE_D_UN_CARREAU);
	}
	
	/**
	 * Le mouvement dans cette Direction est-il possible ?
	 * @return si le mouvement est possible oui ou non
	 */
	@Override
	public final boolean mouvementPossible() {
		//on peut avancer a reculons si on peut avancer en arri�re
		final Event event = this.deplacement.getEventADeplacer();
		this.direction = event.direction; //la direction affichee durant le Mouvement est celle de l'Event avant
		final Avancer mouvementFictif = new Avancer(Event.Direction.directionOpposee(event.direction), Main.TAILLE_D_UN_CARREAU);
		mouvementFictif.deplacement = this.deplacement; //Deplacement pour �viter la NullPointerException dans Avancer
		
		//puis on lance la v�rification traditionnelle
		return mouvementFictif.mouvementPossible();
	}
	
	@Override
	public final void calculDuMouvement(final Event event) {
		event.avance = true;
		
		//il ne faut pas que l'Event aille plus loin que son objectif !
		final int enjambee = Maths.min(event.vitesseActuelle.valeur, this.etapes - this.ceQuiAEteFait);
		//deplacement :
		switch (this.direction) {
			case Direction.BAS : 
				event.y -= enjambee; 
				break;
			case Direction.GAUCHE : 
				event.x += enjambee; 
				break;
			case Direction.DROITE : 
				event.x -= enjambee; 
				break;
			case Direction.HAUT : 
				event.y += enjambee; 
				break;
		}
		//on actualise la completion du Mouvement
		this.ceQuiAEteFait += enjambee;
	}

}
