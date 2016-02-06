package commandesEvent;

import java.util.ArrayList;

import map.PageEvent;

/**
 * Chaque Page de comportement d'un Event contient des Commandes.
 * Celles-ci sont executées dans l'ordre lorsque la page est déclenchée.
 * Lorsque la Commande est considérée comme terminée, le curseur se déplace à la Commande suivante.
 */
public abstract class CommandeEvent {
	public PageEvent page; //une Commande Event connaît la Page à laquelle elle appartient
	
	/**
	 * Execute la Commande totalement ou partiellement.
	 * Le curseur peut être inchangé (attendre n frames...) ;
	 * le curseur peut être incrémenté (assignement de variable...) ;
	 * le curseur peut faire un grand saut (boucles, conditions...).
	 * @param curseurActuel position du curseur avant que la Commande soit executée
	 * @param commandes liste des Commandes de la Page de comportement en train d'être lue
	 * @return nouvelle position du curseur après l'execution totale ou partielle de la Commande
	 */
	public abstract int executer(int curseurActuel, ArrayList<CommandeEvent> commandes);

}
