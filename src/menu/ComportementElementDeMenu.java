package menu;

/**
 * Un élément de menu peut avoir diverses conséquences une fois sélectionné : ouverture d'un autre menu etc.
 */
public abstract class ComportementElementDeMenu {
	public Selectionnable element;
	public abstract void executer();
}
