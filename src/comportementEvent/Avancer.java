package comportementEvent;

import java.util.ArrayList;

import map.Event;
import menu.Parametre;

public class Avancer extends CommandeEvent{
	protected int direction;
	public int nombreDeCarreaux;
	public int ceQuiAEteFait = 0; //avancée en pixel, doit atteindre nombreDeCarreaux*32
	
	public Avancer(Integer direction, Integer nombreDeCarreaux){
		this.direction = direction;
		this.nombreDeCarreaux = nombreDeCarreaux;
	}
	
	/**
	 * Constructeur générique.
	 * @param parametres liste de paramètres issus de JSON
	 */
	public Avancer(ArrayList<Parametre> parametres){
		this( (Integer)trouverParametre("direction", parametres), (Integer)trouverParametre("nombreDeCarreaux", parametres) );
	}
	
	public int getDirection(){
		return direction;
	}
	
	/**
	 * Réinitialiser un mouvement le déclare non fait, et change la direction en cas de mouvement aléatoire.
	 */
	public void reinitialiser(){
		ceQuiAEteFait = 0;
	}

	@Override
	public int executer(int curseurActuel, ArrayList<CommandeEvent> commandes) {
		Event event = this.page.event;
		if(ceQuiAEteFait >= nombreDeCarreaux*32){
			event.avance = false; //le mouvement est terminé
			return curseurActuel+1;
		}
		event.avance = true; //le mouvement est en cours
		event.deplacer();
		return curseurActuel;
	}
}
