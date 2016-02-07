package main;

import java.util.ArrayList;

/**
 * Une Commande modifie l'état du jeu.
 * Elle peut être lancée par une Page d'Event, ou par un Elément de Menu.
 */
public interface Commande {
	/**
	 * Execute la Commande totalement ou partiellement.
	 * Le curseur peut être inchangé (attendre n frames...) ;
	 * le curseur peut être incrémenté (assignement de variable...) ;
	 * le curseur peut faire un grand saut (boucles, conditions...).
	 * @param curseurActuel position du curseur avant que la Commande soit executée
	 * @param commandes liste des Commandes de la Page de comportement en train d'être lue
	 * @return nouvelle position du curseur après l'execution totale ou partielle de la Commande
	 */
	int executer(int curseurActuel, ArrayList<? extends Commande> commandes);
}
