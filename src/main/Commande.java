package main;

import java.util.ArrayList;

import map.PageEvent;
import menu.ElementDeMenu;

/**
 * Une Commande modifie l'état du jeu.
 * Elle peut être lancée par une Page d'Event, ou par un Elément de Menu.
 */
public abstract class Commande {
	/** [CommandeEvent] Eventuelle Page d'Event qui a appelé cette Commande */
	public PageEvent page;
	/** [CommandeMenu] Element de Menu qui a appelé cette Commande de Menu */
	public ElementDeMenu element;
	
	/**
	 * Execute la Commande totalement ou partiellement.
	 * Le curseur peut être inchangé (attendre n frames...) ;
	 * le curseur peut être incrémenté (assignement de variable...) ;
	 * le curseur peut faire un grand saut (boucles, conditions...).
	 * @param curseurActuel position du curseur avant que la Commande soit executée
	 * @param commandes liste des Commandes de la Page de comportement en train d'être lue
	 * @return nouvelle position du curseur après l'execution totale ou partielle de la Commande
	 */
	public abstract int executer(int curseurActuel, ArrayList<Commande> commandes);
}
