package commandes;

import main.Commande;
import menu.ElementDeMenu;

/**
 * Un Elément de Menu peut avoir diverses conséquences une fois sélectionné : ouverture d'un autre menu etc.
 */
public interface CommandeMenu extends Commande {
	
	/**
	 * Une Commande de Menu connaît son Elément de Menu.
	 * @return l'Elément de Menu de cette Commande
	 */
	ElementDeMenu getElement();
	
	/**
	 * Apprendre à la commande qui est son Elément de Menu.
	 * @param element de Menu à mémoriser
	 */
	void setElement(final ElementDeMenu element);
	
	/**
	 * Executer la Commande de Menu.
	 */
	void executer();

}
