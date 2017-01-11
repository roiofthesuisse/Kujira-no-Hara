package menu;

import map.Event.Direction;

/**
 * Une Liste est un tableau d'ElementsDeMenu à plusieurs lignes et colonnes.
 */
public class Liste<T extends Listable> {
	/** Position x de la Liste */
	private final int x;
	/** Position y de la Liste */
	private final int y;
	/** Nombre de lignes visibles à l'écran à la fois */
	private final int lignesAffichees;
	/** Nombre de colonnes visibles à l'écran à la fois */
	private final int colonnesAffichees;
	
	/** coordonnée horizontale dans la Liste de l'ElementDeMenu sélectionné */
	private int iElementSelectionne;
	/** coordonnée verticale dans la Liste de l'ElementDeMenu sélectionné */
	private int jElementSelectionne;
	/** ElementsDeMenu de la Liste */
	private ElementDeMenu[][] elements; //TODO remplir ce tableau
	
	public Liste(final int x, final int y, final int lignesAffichees, final int colonnesAffichees) {
		this.x = x;
		this.y = y;
		this.lignesAffichees = lignesAffichees;
		this.colonnesAffichees = colonnesAffichees;
	}
	
	/**
	 * Chercher un autre ElementDeMenu à sélectionner dans la Liste.
	 * @param direction dans laquelle chercher
	 * @return ElementDeMenu à sélectionner, ou null si bord de Liste
	 */
	public ElementDeMenu selectionnerUnAutreElementDansLaListe(final int direction) {
		switch (direction) {
			case Direction.GAUCHE :
				if (this.iElementSelectionne==0) {
					// on sort de la Liste
					return null;
				}
				this.iElementSelectionne--;
				break;
			case Direction.HAUT :
				if (this.jElementSelectionne==0) {
					// on sort de la Liste
					return null;
				}
				this.jElementSelectionne--;
				break;
			case Direction.DROITE :
				if (this.iElementSelectionne==this.elements.length-1) {
					// on sort de la Liste
					return null;
				}
				this.iElementSelectionne++;
				break;
			case Direction.BAS :
				if (this.jElementSelectionne==this.elements[this.iElementSelectionne].length-1) {
					// on sort de la Liste
					return null;
				}
				this.jElementSelectionne++;
				break;
		}
		
		//TODO éventuellement masquer/afficher certains ElementsDeMenu en fonction du nombre de lignes/colonnes à afficher
		
		return this.elements[this.iElementSelectionne][this.jElementSelectionne];
	}

}
