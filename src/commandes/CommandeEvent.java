package commandes;

import main.Commande;
import map.PageEvent;

/**
 * Chaque Page de comportement d'un Event contient des Commandes.
 * Celles-ci sont executées dans l'ordre lorsque la page est déclenchée.
 * Lorsque la Commande est considérée comme terminée, le curseur se déplace à la Commande suivante.
 */
public interface CommandeEvent extends Commande {
	/**
	 * Une Commande Event connaît la Page à laquelle elle appartient.
	 * @return Page de cette Commande Event
	 */
	PageEvent getPage();
	
	/**
	 * Apprendre à la commande qui est sa Page.
	 * @param page à mémoriser
	 */
	void setPage(PageEvent page);

}
