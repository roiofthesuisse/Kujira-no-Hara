package menu;

/**
 * Un Elément de Menu peut avoir diverses conséquences une fois sélectionné : ouverture d'un autre menu etc.
 */
public abstract class ComportementElementDeMenu {
	public Selectionnable element;
	
	/**
	 * Executer le Comportement de l'Element de Menu
	 */
	public abstract void executer();
}
