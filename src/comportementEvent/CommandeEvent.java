package comportementEvent;

import java.util.ArrayList;

import map.GenerateurAleatoire;
import map.PageDeComportement;

public abstract class CommandeEvent {
	String nom;
	public PageDeComportement page; //une commande event connait sa page 
	public static GenerateurAleatoire rand = new GenerateurAleatoire();
	
	/**
	 * le int renvoyé est la nouvelle position du curseur.
	 * le curseur peut être inchangé (attendre n frames...)
	 * le curseur peut être incrémenté (assignement de variable...)
	 * le curseur peut faire un grand saut (boucles, conditions...)
	 */
	public abstract int executer(int curseurActuel, ArrayList<CommandeEvent> commandes);

}
